package ninja.trek.calibratedsporeblossom;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.BlockRenderLayerMap;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import ninja.trek.calibratedsporeblossom.block.ModBlocks;
import ninja.trek.calibratedsporeblossom.registry.ModMenuTypes;
import ninja.trek.calibratedsporeblossom.screen.AdvancedSporeBlossomScreen;

public class CalibratedSporeBlossomClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		BlockRenderLayerMap.putBlock(ModBlocks.CALIBRATED_SPORE_BLOSSOM, ChunkSectionLayer.CUTOUT);
		BlockRenderLayerMap.putBlock(ModBlocks.ADVANCED_CALIBRATED_SPORE_BLOSSOM, ChunkSectionLayer.CUTOUT);

		MenuScreens.register(ModMenuTypes.ADVANCED_SPORE_BLOSSOM_MENU, AdvancedSporeBlossomScreen::new);
	}
}
