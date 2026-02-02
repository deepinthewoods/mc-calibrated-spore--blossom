package ninja.trek.calibratedsporeblossom;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.BlockRenderLayerMap;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import ninja.trek.calibratedsporeblossom.block.ModBlocks;

public class CalibratedSporeBlossomClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		BlockRenderLayerMap.putBlock(ModBlocks.CALIBRATED_SPORE_BLOSSOM, ChunkSectionLayer.CUTOUT);
	}
}
