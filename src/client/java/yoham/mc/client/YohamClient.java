package yoham.mc.client;

import net.fabricmc.api.ClientModInitializer;
import yoham.mc.client.screen.YohamScreenRegistry;

public class YohamClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		YohamScreenRegistry.registerScreens();
	}
}
