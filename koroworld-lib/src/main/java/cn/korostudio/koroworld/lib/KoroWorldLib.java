package cn.korostudio.koroworld.lib;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KoroWorldLib implements ModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("KoroWorld-Lib");

    @Override
    public void onInitialize() {
        LOGGER.info("KoroWorld-Lib Is Loaded!");
    }
}
