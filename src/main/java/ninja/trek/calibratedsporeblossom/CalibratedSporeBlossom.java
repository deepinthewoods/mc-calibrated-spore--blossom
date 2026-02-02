package ninja.trek.calibratedsporeblossom;

import net.fabricmc.api.ModInitializer;
import ninja.trek.calibratedsporeblossom.block.ModBlocks;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CalibratedSporeBlossom implements ModInitializer {
	public static final String MOD_ID = "calibrated-spore-blossom";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		ModBlocks.register();
		LOGGER.info("Calibrated Spore Blossom initialized");
	}
}
