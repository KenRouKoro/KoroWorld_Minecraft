package cn.korostudio.koroworlditem.event;

import cn.hutool.core.thread.ThreadUtil;
import cn.korostudio.koroworldcore.KoroworldCore;
import cn.korostudio.koroworlditem.data.ItemSystemData;
import cn.korostudio.koroworlditem.uitl.ItemServiceTool;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UnloadEvent {
    static protected Logger logger = LoggerFactory.getLogger("KoroWorld-ITSY-UnloadEvent");

    static public void onSpawn(ServerPlayNetworkHandler handler, MinecraftServer server) {
        if(ItemSystemData.serverCaseEnable)
        ThreadUtil.execute(() -> {
            PlayerEntity player = handler.player;
            ItemServiceTool.upToServer(player, logger);
        });

    }
}
