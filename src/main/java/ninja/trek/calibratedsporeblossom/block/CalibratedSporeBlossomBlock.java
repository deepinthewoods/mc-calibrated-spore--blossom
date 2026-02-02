package ninja.trek.calibratedsporeblossom.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;
import ninja.trek.calibratedsporeblossom.particle.ParticleTypeRegistry;

public class CalibratedSporeBlossomBlock extends DirectionalBlock {

    public static final MapCodec<CalibratedSporeBlossomBlock> CODEC = simpleCodec(CalibratedSporeBlossomBlock::new);

    public static final IntegerProperty PARTICLE_TYPE = IntegerProperty.create("particle_type", 0, ParticleTypeRegistry.COUNT - 1);
    public static final IntegerProperty POWER = IntegerProperty.create("power", 0, 15);

    public CalibratedSporeBlossomBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(
            this.stateDefinition.any()
                .setValue(FACING, Direction.UP)
                .setValue(PARTICLE_TYPE, 0)
                .setValue(POWER, 0)
        );
    }

    @Override
    protected MapCodec<? extends DirectionalBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, PARTICLE_TYPE, POWER);
    }

    // --- Placement ---

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return this.defaultBlockState()
            .setValue(FACING, ctx.getClickedFace())
            .setValue(POWER, ctx.getLevel().getBestNeighborSignal(ctx.getClickedPos()));
    }

    @Override
    protected BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    protected BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    // --- Interaction ---

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (!level.isClientSide()) {
            int current = state.getValue(PARTICLE_TYPE);
            int next;
            if (player.isShiftKeyDown()) {
                next = (current - 1 + ParticleTypeRegistry.COUNT) % ParticleTypeRegistry.COUNT;
            } else {
                next = (current + 1) % ParticleTypeRegistry.COUNT;
            }
            level.setBlock(pos, state.setValue(PARTICLE_TYPE, next), Block.UPDATE_ALL);
            player.displayClientMessage(
                Component.literal("Particle: " + ParticleTypeRegistry.getDisplayName(next)),
                true
            );
        }
        return InteractionResult.SUCCESS;
    }

    // --- Redstone ---

    @Override
    protected boolean hasAnalogOutputSignal(BlockState state) {
        return false;
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, @Nullable Orientation orientation, boolean movedByPiston) {
        if (!level.isClientSide()) {
            int newPower = level.getBestNeighborSignal(pos);
            if (state.getValue(POWER) != newPower) {
                level.setBlock(pos, state.setValue(POWER, newPower), Block.UPDATE_ALL);
            }
        }
    }

    // --- Particles ---

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        int power = state.getValue(POWER);

        int particleIdx = state.getValue(PARTICLE_TYPE);
        ParticleOptions[] particles = ParticleTypeRegistry.getParticles(particleIdx);
        Direction facing = state.getValue(FACING);

        // Half-cube: lateral axes grow by +2 per side per power level,
        // facing axis grows by +2 per power level (only outward from block face).
        // Power 0 = 1x1x1, power 1 = 5x5x3, ..., power 15 = 61x61x31
        double lateralSize = 1 + 4 * power;
        double facingSize = 1 + 2 * power;
        int count = Math.max(1, power);

        double cx = pos.getX() + 0.5;
        double cy = pos.getY() + 0.5;
        double cz = pos.getZ() + 0.5;

        int sx = facing.getStepX();
        int sy = facing.getStepY();
        int sz = facing.getStepZ();

        for (int i = 0; i < count; i++) {
            // Facing axis: starts at block face (0.5 from center), extends outward
            double facingOffset = 0.5 + random.nextDouble() * facingSize;

            // Lateral offsets: centered on block center
            double latX = (random.nextDouble() - 0.5) * lateralSize;
            double latY = (random.nextDouble() - 0.5) * lateralSize;
            double latZ = (random.nextDouble() - 0.5) * lateralSize;

            // For the facing axis use facingOffset, for lateral axes use lateral offset
            double fx = cx + (sx != 0 ? sx * facingOffset : latX);
            double fy = cy + (sy != 0 ? sy * facingOffset : latY);
            double fz = cz + (sz != 0 ? sz * facingOffset : latZ);

            // Check if the target position is not inside a full block
            BlockPos targetPos = BlockPos.containing(fx, fy, fz);
            if (level.getBlockState(targetPos).isCollisionShapeFullBlock(level, targetPos)) {
                continue;
            }

            for (ParticleOptions particle : particles) {
                level.addParticle(particle, fx, fy, fz, 0.0, 0.0, 0.0);
            }
        }
    }
}
