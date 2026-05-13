package yoham.mc.client;

import net.fabricmc.api.ClientModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import yoham.mc.client.screen.YohamScreenRegistry;

public class YohamClient implements ClientModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger("YohamClient");

	@Override
	public void onInitializeClient() {
		LOGGER.info("YoHAM Client initialized");
		YohamScreenRegistry.registerScreens();
		LOGGER.info("Screen registry initialized");
	}
}
