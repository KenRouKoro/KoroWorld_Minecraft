package cn.korostudio.koroworldcore.event;

import cn.korostudio.koroworldcore.KoroworldCore;
import cn.korostudio.koroworldcore.data.Data;
import cn.korostudio.koroworldcore.util.MessageTool;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;

public class PlayerDisconnectEvent {
    static public void onDisconnect(ServerPlayNetworkHandler handler, MinecraftServer server){
        MessageTool.sendSystemMessageAll( Data.serverDisConnectBanner, handler.getPlayer().getName().asString());
    }
}
