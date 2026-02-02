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

    public static final Block CALIBRATED_SPORE_BLOSSOM = new CalibratedSporeBlossomBlock(
        BlockBehaviour.Properties.of()
            .setId(BLOCK_KEY)
            .strength(1.5f)
            .sound(SoundType.AMETHYST)
            .requiresCorrectToolForDrops()
            .noOcclusion()
    );

    public static void register() {
        Registry.register(BuiltInRegistries.BLOCK, ID, CALIBRATED_SPORE_BLOSSOM);
        Registry.register(BuiltInRegistries.ITEM, ID, new BlockItem(CALIBRATED_SPORE_BLOSSOM, new Item.Properties().setId(ITEM_KEY)));

        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.FUNCTIONAL_BLOCKS).register(entries -> {
            entries.accept(CALIBRATED_SPORE_BLOSSOM);
        });
    }

    private ModBlocks() {}
}
