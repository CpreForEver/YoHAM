package yoham.mc.menu;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class RadioMenu extends AbstractContainerMenu {
	public int frequency = 460000;
	public int volume = 75;
	public int signalStrength = 0;
	public boolean powered = false;
	public boolean transmitting = false;

	public RadioMenu(int containerId, Inventory playerInventory) {
		super(ModMenus.RADIO, containerId);
		addStandardInventorySlots(playerInventory, 8, 84);
	}

	public void setFrequency(int frequency) {
		this.frequency = frequency;
	}

	public void setVolume(int volume) {
		this.volume = Math.max(0, Math.min(100, volume));
	}

	public void setSignalStrength(int strength) {
		this.signalStrength = Math.max(0, Math.min(100, strength));
	}

	public void setPowered(boolean powered) {
		this.powered = powered;
	}

	public void setTransmitting(boolean transmitting) {
		this.transmitting = transmitting;
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
