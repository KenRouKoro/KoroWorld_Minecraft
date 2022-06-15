package cn.korostudio.koroworld.multiserver;

import cn.korostudio.koroworld.multiserver.command.Command;
import cn.korostudio.koroworld.multiserver.connectTool.ServerConnectTool;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;

public class KoroWorldMultiServerServer implements DedicatedServerModInitializer {
    @Override
    public void onInitializeServer() {
        CommandRegistrationCallback.EVENT.register(Command::register);
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            ServerConnectTool.SetPlayerServerStatus(handler.getPlayer());
        });
    }
}
