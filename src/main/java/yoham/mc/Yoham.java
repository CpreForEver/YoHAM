package yoham.mc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.fabricmc.api.ModInitializer;
import yoham.mc.item.ModItems;
import yoham.mc.menu.ModMenus;

public class Yoham implements ModInitializer {
	public static final String MOD_ID = "yoham";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
public void onInitialize() {
		LOGGER.info("YoHAM Server initialized");
		ModMenus.initialize();
		ModItems.initialize();
	}
}