package cn.korostudio.koroworlditem.event;

import cn.hutool.core.thread.ThreadUtil;
import cn.korostudio.koroworlditem.data.ItemSystemData;
import cn.korostudio.koroworlditem.uitl.ItemServiceTool;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoadEvent {
    static protected Logger logger = LoggerFactory.getLogger("KoroWorld-ITSY-LoadEvent");



    static public void onSpawn(ServerPlayNetworkHandler handler, PacketSender sender, MinecraftServer server) {
        if(ItemSystemData.serverCaseEnable)
        ThreadUtil.execute(() -> {
            ItemServiceTool.downloadForServer(handler.getPlayer(), logger);
        });
    }
}
