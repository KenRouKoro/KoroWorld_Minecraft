package cn.korostudio.koroworld.command;

import cn.korostudio.koroworld.command.command.Command;
import cn.korostudio.koroworld.command.data.HomeData;
import cn.korostudio.koroworld.command.data.TPData;
import cn.korostudio.koroworld.core.data.Data;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KoroWorldCommand implements ModInitializer {


    public static final Logger LOGGER = LoggerFactory.getLogger("KoroWorld-Command");

    protected static void register() {
        CommandRegistrationCallback.EVENT.register(Command::register);
    }

    protected static void loadSetting() {
        TPData.timeout = Data.KoroWorldConfig.getInt("tptimeout", "command", 120);
        HomeData.setMaxHomes(Data.KoroWorldConfig.getInt("maxhomes", "command", 5));
    }

    @Override
    public void onInitialize() {
        LOGGER.info("KoroWorld-Command Is Loaded!");
        loadSetting();
        register();
        HomeData.loadHomes();
        TPData.Init();
    }
}
