package cn.korostudio.koroworld.Events;

import cn.hutool.core.thread.ThreadUtil;
import cn.korostudio.koroworld.KoroWorldMain;
import cn.korostudio.koroworld.command.KoroCommand;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UnloadEvent {
    static protected Logger logger = LoggerFactory.getLogger("KoroWorld-ITSY-UnloadEvent");

    static public void onSpawn(ServerPlayNetworkHandler handler, MinecraftServer server) {
        if(KoroWorldMain.setting.getStr("ITSYEnable","true").equals("true"))
        ThreadUtil.execute(() -> {
            PlayerEntity player = handler.player;
            KoroCommand.upToServer(player, logger);
        });

    }
}
