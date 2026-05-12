package yoham.mc.item;

import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import yoham.mc.menu.RadioMenu;

public class UhfRadioItem extends Item {
	public UhfRadioItem(Item.Properties properties) {
		super(properties);
	}

	@Override
	public InteractionResult use(Level level, Player user, InteractionHand hand) {
		if (!level.isClientSide()) {
			user.openMenu(new RadioMenuProvider());
		}
		return InteractionResult.SUCCESS;
	}

	private class RadioMenuProvider implements net.minecraft.world.MenuProvider {
		@Override
		public Component getDisplayName() {
			return Component.translatable("item.yoham.uhf_radio");
		}

		@Override
		public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
			return new RadioMenu(containerId, inventory);
		}
	}
}
