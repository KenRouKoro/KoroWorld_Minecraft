package cn.korostudio.koroworld;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;
import net.minecraft.util.registry.Registry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KoroWorldClient implements ClientModInitializer {
    protected static Logger logger = LoggerFactory.getLogger("KoroWorld");

    /**
     * Runs the mod initializer on the client environment.
     */
    @Override
    public void onInitializeClient() {
        logger.info("""
                 
                 power by:
                 _   __                  _____ _             _ _
                | | / /                 /  ___| |           | (_)
                | |/ /  ___  _ __ ___   \\ `--.| |_ _   _  __| |_  ___
                |    \\ / _ \\| '__/ _ \\   `--. \\ __| | | |/ _` | |/ _ \\
                | |\\  \\ (_) | | | (_) | /\\__/ / |_| |_| | (_| | | (_) |
                \\_| \\_/\\___/|_|  \\___/  \\____/ \\__|\\__,_|\\__,_|_|\\___/
                """);

    }
}
