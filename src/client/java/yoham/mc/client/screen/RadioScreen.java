package yoham.mc.client.screen;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import yoham.mc.Yoham;
import yoham.mc.menu.RadioMenu;

public class RadioScreen extends AbstractContainerScreen<RadioMenu> {
	private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(Yoham.MOD_ID, "textures/gui/radio_gui.png");

	private Button powerButton;
	private Button channelUpButton;
	private Button channelDownButton;
	private Button transmitButton;
	private boolean powered = false;

	public RadioScreen(RadioMenu menu, Inventory playerInventory, Component title) {
		super(menu, playerInventory, title, 176, 166);
	}

	@Override
	protected void init() {
		super.init();
		this.titleLabelX = (this.imageWidth - this.font.width(this.title)) / 2;

		int centerX = this.leftPos + this.imageWidth / 2;
		int topY = this.topPos;

		// Power button
		this.powerButton = Button.builder(
			Component.literal("PWR"),
			button -> {
				this.powered = !this.powered;
				this.menu.setPowered(this.powered);
				button.setMessage(Component.literal(this.powered ? "PWR" : "OFF"));
			}
		).bounds(centerX - 70, topY + 50, 30, 20).build();
		this.addRenderableWidget(this.powerButton);

		// Channel up button
		this.channelUpButton = Button.builder(
			Component.literal("▲"),
			button -> {
				this.menu.setFrequency(this.menu.frequency + 5000);
			}
		).bounds(centerX - 70, topY + 75, 30, 20).build();
		this.addRenderableWidget(this.channelUpButton);

		// Channel down button
		this.channelDownButton = Button.builder(
			Component.literal("▼"),
			button -> {
				this.menu.setFrequency(this.menu.frequency - 5000);
			}
		).bounds(centerX - 70, topY + 100, 30, 20).build();
		this.addRenderableWidget(this.channelDownButton);

		// Transmit button
		this.transmitButton = Button.builder(
			Component.literal("TX"),
			button -> {
				this.menu.setTransmitting(!this.menu.transmitting);
				button.setMessage(Component.literal(this.menu.transmitting ? "TX" : "RX"));
			}
		).bounds(centerX + 40, topY + 50, 30, 20).build();
		this.addRenderableWidget(this.transmitButton);
	}

  @Override
  public void extractRenderState(GuiGraphicsExtractor g, int mouseX, int mouseY, float delta) {
      // 1. Mandatory: Extract widgets (buttons)
      super.extractRenderState(g, mouseX, mouseY, delta);
  
      // 2. Main Background - Ensure we specify the full texture size (usually 256x256)
      g.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, this.leftPos, this.topPos, 0.0F, 0.0F, this.imageWidth, this.imageHeight, 256, 256);
  
      // 3. Titles - Must add leftPos/topPos and wrap in Component.literal
      g.text(this.font, this.title, this.leftPos + this.titleLabelX, this.topPos + this.titleLabelY, 0x404040);
      g.text(this.font, Component.literal("Inventory"), this.leftPos + this.inventoryLabelX, this.topPos + this.inventoryLabelY, 0x404040);
  
      int centerX = this.leftPos + this.imageWidth / 2;
      int topY = this.topPos;
  
      // 4. Frequency Display
      Component freqComp;
      int color;
      if (this.powered) {
          freqComp = Component.literal(formatFrequency(this.menu.frequency));
          color = this.menu.transmitting ? 0x00FF00 : 0xFFFFFF;
      } else {
          freqComp = Component.literal("---.---");
          color = 0x888888;
      }
      g.text(this.font, freqComp, centerX - this.font.width(freqComp) / 2, topY + 24, color);
  
      // 5. Progress Bars (Signal & Volume)
      // Note: Use specific UVs from your texture for the 'filled' portion
      if (this.powered) {
          int sigWidth = (int)(60.0F * this.menu.signalStrength / 100.0F);
          if (sigWidth > 0) {
              // Adjust the U/V (0.0F, 166.0F etc) to point to your bar texture's location
              g.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, centerX - 30, topY + 130, 0.0F, 166.0F, sigWidth, 6, 256, 256);
          }
      }
  
      // 6. Labels
      g.text(this.font, Component.literal("SIG"), centerX - 55, topY + 131, 0xAAAAAA);
      g.text(this.font, Component.literal("VOL"), centerX - 55, topY + 143, 0xAAAAAA);
  }
  
	@Override
	public void extractContents(GuiGraphicsExtractor g, int mouseX, int mouseY, float delta) {
		super.extractContents(g, mouseX, mouseY, delta);
	}

	private String formatFrequency(int freq) {
		int mhz = freq / 1000000;
		int khz = (freq % 1000000) / 1000;
		return String.format("%d.%03d", mhz, khz);
	}
}
