package yoham.mc.menu;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.Registry;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.resources.Identifier;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.MenuType.MenuSupplier;
import yoham.mc.Yoham;

public class ModMenus {
	public static MenuType<RadioMenu> RADIO;

	public static <T extends AbstractContainerMenu> MenuType<T> register(String name, MenuSupplier<T> constructor) {
		return Registry.register(BuiltInRegistries.MENU, Identifier.fromNamespaceAndPath(Yoham.MOD_ID, name), new MenuType<>(constructor, FeatureFlagSet.of()));
	}

	public static void initialize() {
		RADIO = register("radio", RadioMenu::new);
	}
}
