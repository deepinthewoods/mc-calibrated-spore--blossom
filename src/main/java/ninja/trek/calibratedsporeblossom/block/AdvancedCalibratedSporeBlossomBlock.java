package ninja.trek.calibratedsporeblossom.block;

import com.mojang.serialization.MapCodec;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.world.phys.BlockHitResult;
import ninja.trek.calibratedsporeblossom.particle.ParticleTypeRegistry;
import ninja.trek.calibratedsporeblossom.registry.ModBlockEntities;
import ninja.trek.calibratedsporeblossom.screen.AdvancedSporeBlossomData;
import ninja.trek.calibratedsporeblossom.screen.AdvancedSporeBlossomMenu;
import org.jetbrains.annotations.Nullable;

public class AdvancedCalibratedSporeBlossomBlock extends BaseEntityBlock {

    public static final MapCodec<AdvancedCalibratedSporeBlossomBlock> CODEC =
            simpleCodec(AdvancedCalibratedSporeBlossomBlock::new);

    public static final EnumProperty<Direction> FACING = BlockStateProperties.FACING;
    public static final IntegerProperty POWER = IntegerProperty.create("power", 0, 15);

    // animateTick fires ~once per 50 ticks for a given block; the client ticker fires every tick.
    // Divide particle count by this to keep the same effective spawn rate.
    private static final double TICK_COMPENSATION = 50.0;

    public AdvancedCalibratedSporeBlossomBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(
                this.stateDefinition.any()
                        .setValue(FACING, Direction.UP)
                        .setValue(POWER, 0)
        );
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, POWER);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    // --- Block Entity ---

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new AdvancedSporeBlossomBlockEntity(pos, state);
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide()) {
            return createTickerHelper(type, ModBlockEntities.ADVANCED_SPORE_BLOSSOM_BE,
                    AdvancedCalibratedSporeBlossomBlock::clientTick);
        }
        return null;
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

    // --- Interaction: Open GUI ---

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (!level.isClientSide() && player instanceof ServerPlayer serverPlayer) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof AdvancedSporeBlossomBlockEntity advBe) {
                serverPlayer.openMenu(new ExtendedScreenHandlerFactory<AdvancedSporeBlossomData>() {
                    @Override
                    public Component getDisplayName() {
                        return advBe.getDisplayName();
                    }

                    @Override
                    public @Nullable AbstractContainerMenu createMenu(int syncId, Inventory playerInv, Player p) {
                        return new AdvancedSporeBlossomMenu(syncId, playerInv, advBe);
                    }

                    @Override
                    public AdvancedSporeBlossomData getScreenOpeningData(ServerPlayer player) {
                        return new AdvancedSporeBlossomData(pos);
                    }
                });
            }
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

    // --- Particles (client-side block entity ticker) ---

    private static void clientTick(Level level, BlockPos pos, BlockState state, AdvancedSporeBlossomBlockEntity advBe) {
        RandomSource random = level.random;

        // Pulse check
        if (advBe.getPulseMode() == 1) {
            long tick = level.getGameTime() % advBe.getPulseInterval();
            if (tick >= advBe.getPulseDuration()) return;
        }

        int power = state.getValue(POWER);
        int redstoneMode = advBe.getRedstoneMode();

        float effectiveDensity = advBe.getDensity();
        int effectiveXRadius = advBe.getXRadius();
        int effectiveYRadius = advBe.getYRadius();
        int effectiveZRadius = advBe.getZRadius();

        if (redstoneMode == 1) {
            effectiveDensity = (power / 15.0f) * advBe.getDensity();
            if (power == 0) return;
        } else if (redstoneMode == 2) {
            effectiveXRadius = Math.max(1, Math.round((power / 15.0f) * advBe.getXRadius()));
            effectiveYRadius = Math.max(1, Math.round((power / 15.0f) * advBe.getYRadius()));
            effectiveZRadius = Math.max(1, Math.round((power / 15.0f) * advBe.getZRadius()));
            if (power == 0) return;
        }

        int particleIdx = advBe.getParticleType();
        Direction facing = state.getValue(FACING);

        ParticleOptions[] particles;
        if (particleIdx == ParticleTypeRegistry.BLOCK_PARTICLE_INDEX) {
            BlockPos attachedPos = pos.relative(facing.getOpposite());
            BlockState attachedState = level.getBlockState(attachedPos);
            if (attachedState.isAir()) {
                attachedState = Blocks.STONE.defaultBlockState();
            }
            particles = new ParticleOptions[]{ new BlockParticleOption(ParticleTypes.BLOCK, attachedState) };
        } else {
            particles = ParticleTypeRegistry.getParticles(particleIdx);
        }

        double volume = (double) effectiveXRadius * 2 * effectiveYRadius * 2 * effectiveZRadius * 2;
        // Compensate: ticker fires every tick vs animateTick's sporadic calls
        double rawCount = (effectiveDensity * volume) / TICK_COMPENSATION;
        int count = (int) rawCount;
        if (random.nextDouble() < (rawCount - count)) {
            count++;
        }
        if (count <= 0) return;

        double cx = pos.getX() + 0.5;
        double cy = pos.getY() + 0.5;
        double cz = pos.getZ() + 0.5;

        float speed = advBe.getAnimationSpeed();
        float vx = advBe.getVelocityX() * speed;
        float vy = (advBe.getVelocityY() + advBe.getGravity()) * speed;
        float vz = advBe.getVelocityZ() * speed;

        int colorEnabled = advBe.getColorEnabled();
        int colorR = advBe.getColorR();
        int colorG = advBe.getColorG();
        int colorB = advBe.getColorB();
        float particleScale = advBe.getParticleScale();

        int emissionMode = advBe.getEmissionMode();
        float densityFalloff = advBe.getDensityFalloff();
        float randomness = advBe.getRandomness();

        for (int i = 0; i < count; i++) {
            double ox, oy, oz;

            if (randomness >= 1.0f) {
                ox = (random.nextDouble() * 2 - 1) * effectiveXRadius;
                oy = (random.nextDouble() * 2 - 1) * effectiveYRadius;
                oz = (random.nextDouble() * 2 - 1) * effectiveZRadius;
            } else if (randomness <= 0.0f) {
                int gridSize = Math.max(1, (int) Math.cbrt(count));
                int gi = i % gridSize;
                int gj = (i / gridSize) % gridSize;
                int gk = (i / (gridSize * gridSize)) % gridSize;
                ox = ((gi + 0.5) / gridSize * 2 - 1) * effectiveXRadius;
                oy = ((gj + 0.5) / gridSize * 2 - 1) * effectiveYRadius;
                oz = ((gk + 0.5) / gridSize * 2 - 1) * effectiveZRadius;
            } else {
                int gridSize = Math.max(1, (int) Math.cbrt(count));
                int gi = i % gridSize;
                int gj = (i / gridSize) % gridSize;
                int gk = (i / (gridSize * gridSize)) % gridSize;
                double gx = ((gi + 0.5) / gridSize * 2 - 1) * effectiveXRadius;
                double gy = ((gj + 0.5) / gridSize * 2 - 1) * effectiveYRadius;
                double gz = ((gk + 0.5) / gridSize * 2 - 1) * effectiveZRadius;
                double rx = (random.nextDouble() * 2 - 1) * effectiveXRadius;
                double ry = (random.nextDouble() * 2 - 1) * effectiveYRadius;
                double rz = (random.nextDouble() * 2 - 1) * effectiveZRadius;
                ox = gx + (rx - gx) * randomness;
                oy = gy + (ry - gy) * randomness;
                oz = gz + (rz - gz) * randomness;
            }

            if (densityFalloff > 0.0f) {
                double dist = Math.sqrt(
                        (ox * ox) / (effectiveXRadius * effectiveXRadius) +
                        (oy * oy) / (effectiveYRadius * effectiveYRadius) +
                        (oz * oz) / (effectiveZRadius * effectiveZRadius)
                );
                double keepProbability = Math.pow(Math.max(0, 1 - dist), densityFalloff);
                if (random.nextDouble() > keepProbability) continue;
            }

            double fx = cx + ox;
            double fy = cy + oy;
            double fz = cz + oz;

            BlockPos targetPos = BlockPos.containing(fx, fy, fz);
            BlockState targetState = level.getBlockState(targetPos);

            if (emissionMode == 0) {
                if (targetState.isCollisionShapeFullBlock(level, targetPos)) continue;
            } else if (emissionMode == 1) {
                if (!targetState.isCollisionShapeFullBlock(level, targetPos)) continue;
                if (!hasNonSolidNeighbor(level, targetPos)) continue;
                Direction surfaceFace = findNonSolidNeighborFace(level, targetPos, random);
                if (surfaceFace == null) continue;
                fx = targetPos.getX() + 0.5 + surfaceFace.getStepX() * 0.51;
                fy = targetPos.getY() + 0.5 + surfaceFace.getStepY() * 0.51;
                fz = targetPos.getZ() + 0.5 + surfaceFace.getStepZ() * 0.51;
            } else if (emissionMode == 2) {
                if (targetState.isCollisionShapeFullBlock(level, targetPos)) continue;
                if (!hasSolidNeighbor(level, targetPos)) continue;
            }

            if (colorEnabled == 1) {
                int color = (255 << 24) | (colorR << 16) | (colorG << 8) | colorB;
                DustParticleOptions dust = new DustParticleOptions(color, particleScale);
                level.addParticle(dust, fx, fy, fz, vx, vy, vz);
            } else {
                for (ParticleOptions particle : particles) {
                    level.addParticle(particle, fx, fy, fz, vx, vy, vz);
                }
            }
        }
    }

    private static boolean hasNonSolidNeighbor(Level level, BlockPos pos) {
        for (Direction dir : Direction.values()) {
            BlockPos neighbor = pos.relative(dir);
            if (!level.getBlockState(neighbor).isCollisionShapeFullBlock(level, neighbor)) {
                return true;
            }
        }
        return false;
    }

    private static boolean hasSolidNeighbor(Level level, BlockPos pos) {
        for (Direction dir : Direction.values()) {
            BlockPos neighbor = pos.relative(dir);
            if (level.getBlockState(neighbor).isCollisionShapeFullBlock(level, neighbor)) {
                return true;
            }
        }
        return false;
    }

    private static @Nullable Direction findNonSolidNeighborFace(Level level, BlockPos pos, RandomSource random) {
        Direction[] dirs = Direction.values();
        // Shuffle starting point for variety
        int start = random.nextInt(6);
        for (int i = 0; i < 6; i++) {
            Direction dir = dirs[(start + i) % 6];
            BlockPos neighbor = pos.relative(dir);
            if (!level.getBlockState(neighbor).isCollisionShapeFullBlock(level, neighbor)) {
                return dir;
            }
        }
        return null;
    }
}
