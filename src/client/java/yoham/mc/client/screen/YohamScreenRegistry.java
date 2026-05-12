package yoham.mc.client.screen;

import net.minecraft.client.gui.screens.MenuScreens;
import yoham.mc.menu.RadioMenu;
import yoham.mc.menu.RadioMenuType;

public class YohamScreenRegistry {
	public static void registerScreens() {
		MenuScreens.register(RadioMenuType.RADIO, RadioScreen::new);
	}
}
