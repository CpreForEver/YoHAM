package yoham.mc.client.screen;

import net.minecraft.client.gui.screens.MenuScreens;
import yoham.mc.menu.ModMenus;
import yoham.mc.menu.RadioMenu;

public class YohamScreenRegistry {
	public static void registerScreens() {
		MenuScreens.register(ModMenus.RADIO, RadioScreen::new);
	}
}
