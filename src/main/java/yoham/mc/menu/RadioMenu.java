package yoham.mc.menu;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class RadioMenu extends AbstractContainerMenu {
	public RadioMenu(int containerId, Inventory playerInventory) {
		super(RadioMenuType.RADIO, containerId);
		addStandardInventorySlots(playerInventory, 8, 84);
	}

	@Override
	public ItemStack quickMoveStack(Player player, int slotIndex) {
		return ItemStack.EMPTY;
	}

	@Override
	public boolean stillValid(Player player) {
		return true;
	}
}
