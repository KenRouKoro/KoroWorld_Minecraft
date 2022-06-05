package cn.korostudio.koroworld.command;

import cn.korostudio.koroworld.command.command.Command;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KoroWorldCommand implements ModInitializer {


    public static final Logger LOGGER = LoggerFactory.getLogger("KoroWorld-Command");

    @Override
    public void onInitialize() {
        LOGGER.info("KoroWorld-Command Is Loaded!");
        register();
    }

    protected static void register(){
        CommandRegistrationCallback.EVENT.register(Command::register);
    }
}
