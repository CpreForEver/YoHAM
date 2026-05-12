package yoham.mc.item;

import java.util.function.Function;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.Registry;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.minecraft.world.item.CreativeModeTabs;
import yoham.mc.Yoham;

public class ModItems {
	public static <T extends Item> T register(String name, Function<Item.Properties, T> itemFactory, Item.Properties settings) {
		ResourceKey<Item> itemKey = ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(Yoham.MOD_ID, name));
		T item = itemFactory.apply(settings.setId(itemKey));
		Registry.register(BuiltInRegistries.ITEM, itemKey, item);
		return item;
	}

	public static final RadioItem RADIO = register("radio", RadioItem::new, new Item.Properties());
	public static final UhfRadioItem UHF_RADIO = register("uhf_radio", UhfRadioItem::new, new Item.Properties());

	public static void initialize() {
		CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.TOOLS_AND_UTILITIES)
				.register((creativeTab) -> {
					creativeTab.accept(ModItems.RADIO);
					creativeTab.accept(ModItems.UHF_RADIO);
				});
	}
}
