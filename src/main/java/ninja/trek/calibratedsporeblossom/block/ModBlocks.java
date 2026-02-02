package ninja.trek.calibratedsporeblossom.block;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import ninja.trek.calibratedsporeblossom.CalibratedSporeBlossom;

public final class ModBlocks {

    private static final Identifier ID = Identifier.fromNamespaceAndPath(CalibratedSporeBlossom.MOD_ID, "calibrated_spore_blossom");
    private static final ResourceKey<Block> BLOCK_KEY = ResourceKey.create(Registries.BLOCK, ID);
    private static final ResourceKey<Item> ITEM_KEY = ResourceKey.create(Registries.ITEM, ID);

    private static final Identifier ADV_ID = Identifier.fromNamespaceAndPath(CalibratedSporeBlossom.MOD_ID, "advanced_calibrated_spore_blossom");
    private static final ResourceKey<Block> ADV_BLOCK_KEY = ResourceKey.create(Registries.BLOCK, ADV_ID);
    private static final ResourceKey<Item> ADV_ITEM_KEY = ResourceKey.create(Registries.ITEM, ADV_ID);

    public static final Block CALIBRATED_SPORE_BLOSSOM = new CalibratedSporeBlossomBlock(
        BlockBehaviour.Properties.of()
            .setId(BLOCK_KEY)
            .strength(1.5f)
            .sound(SoundType.AMETHYST)
            .requiresCorrectToolForDrops()
            .noOcclusion()
    );

    public static final Block ADVANCED_CALIBRATED_SPORE_BLOSSOM = new AdvancedCalibratedSporeBlossomBlock(
        BlockBehaviour.Properties.of()
            .setId(ADV_BLOCK_KEY)
            .strength(1.5f)
            .sound(SoundType.SCULK)
            .requiresCorrectToolForDrops()
            .noOcclusion()
    );

    public static void register() {
        Registry.register(BuiltInRegistries.BLOCK, ID, CALIBRATED_SPORE_BLOSSOM);
        Registry.register(BuiltInRegistries.ITEM, ID, new BlockItem(CALIBRATED_SPORE_BLOSSOM, new Item.Properties().setId(ITEM_KEY)));

        Registry.register(BuiltInRegistries.BLOCK, ADV_ID, ADVANCED_CALIBRATED_SPORE_BLOSSOM);
        Registry.register(BuiltInRegistries.ITEM, ADV_ID, new BlockItem(ADVANCED_CALIBRATED_SPORE_BLOSSOM, new Item.Properties().setId(ADV_ITEM_KEY)));

        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.FUNCTIONAL_BLOCKS).register(entries -> {
            entries.accept(CALIBRATED_SPORE_BLOSSOM);
            entries.accept(ADVANCED_CALIBRATED_SPORE_BLOSSOM);
        });
    }

    private ModBlocks() {}
}
