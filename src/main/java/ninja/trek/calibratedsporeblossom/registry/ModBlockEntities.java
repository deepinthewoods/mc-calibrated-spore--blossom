package ninja.trek.calibratedsporeblossom.registry;

import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.entity.BlockEntityType;
import ninja.trek.calibratedsporeblossom.CalibratedSporeBlossom;
import ninja.trek.calibratedsporeblossom.block.AdvancedSporeBlossomBlockEntity;
import ninja.trek.calibratedsporeblossom.block.ModBlocks;

public final class ModBlockEntities {

    public static final BlockEntityType<AdvancedSporeBlossomBlockEntity> ADVANCED_SPORE_BLOSSOM_BE =
            FabricBlockEntityTypeBuilder.create(
                    AdvancedSporeBlossomBlockEntity::new,
                    ModBlocks.ADVANCED_CALIBRATED_SPORE_BLOSSOM
            ).build();

    public static void register() {
        Registry.register(
                BuiltInRegistries.BLOCK_ENTITY_TYPE,
                Identifier.fromNamespaceAndPath(CalibratedSporeBlossom.MOD_ID, "advanced_spore_blossom_be"),
                ADVANCED_SPORE_BLOSSOM_BE
        );
    }

    private ModBlockEntities() {}
}
