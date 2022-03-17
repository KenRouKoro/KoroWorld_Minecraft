package cn.korostudio.koroworlditem;

import cn.korostudio.koroworlditem.command.KoroItemCommand;
import cn.korostudio.koroworlditem.event.LoadEvent;
import cn.korostudio.koroworlditem.event.UnloadEvent;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;

public class KoroworldItemServer implements DedicatedServerModInitializer {

    protected static void register(){
        ServerPlayConnectionEvents.JOIN.register(LoadEvent::onSpawn);
        ServerPlayConnectionEvents.DISCONNECT.register(UnloadEvent::onSpawn);
        CommandRegistrationCallback.EVENT.register(KoroItemCommand::registerCommand);
    }

    @Override
    public void onInitializeServer() {
        register();
    }
}
