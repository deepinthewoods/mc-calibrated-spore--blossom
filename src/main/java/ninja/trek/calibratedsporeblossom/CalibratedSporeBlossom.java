package ninja.trek.calibratedsporeblossom;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.world.level.block.entity.BlockEntity;
import ninja.trek.calibratedsporeblossom.block.AdvancedSporeBlossomBlockEntity;
import ninja.trek.calibratedsporeblossom.block.ModBlocks;
import ninja.trek.calibratedsporeblossom.network.AdvancedSporeBlossomSyncPayload;
import ninja.trek.calibratedsporeblossom.registry.ModBlockEntities;
import ninja.trek.calibratedsporeblossom.registry.ModMenuTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CalibratedSporeBlossom implements ModInitializer {
	public static final String MOD_ID = "calibrated-spore-blossom";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		ModBlocks.register();
		ModBlockEntities.register();
		ModMenuTypes.register();

		// Register C2S payload for slider sync
		PayloadTypeRegistry.playC2S().register(
				AdvancedSporeBlossomSyncPayload.TYPE,
				AdvancedSporeBlossomSyncPayload.STREAM_CODEC
		);

		// Handle slider updates from client
		ServerPlayNetworking.registerGlobalReceiver(AdvancedSporeBlossomSyncPayload.TYPE, (payload, context) -> {
			context.server().execute(() -> {
				var player = context.player();
				var level = player.level();
				var pos = payload.pos();

				// Security: check distance
				if (player.distanceToSqr(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5) > 64.0) return;

				BlockEntity be = level.getBlockEntity(pos);
				if (be instanceof AdvancedSporeBlossomBlockEntity advBe) {
					advBe.setSettingFromInt(payload.settingIndex(), payload.value());
					advBe.syncToClients();
				}
			});
		});

		LOGGER.info("Calibrated Spore Blossom initialized");
	}
}
