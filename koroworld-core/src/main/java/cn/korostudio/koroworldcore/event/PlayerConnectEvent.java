package cn.korostudio.koroworldcore.event;

import cn.korostudio.koroworldcore.KoroworldCore;
import cn.korostudio.koroworldcore.data.Data;
import cn.korostudio.koroworldcore.util.MessageTool;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;

public class PlayerConnectEvent {
    static public void onConnect(ServerPlayNetworkHandler handler, PacketSender sender, MinecraftServer server){
        MessageTool.sendSystemMessageAll(Data.serverConnectBanner,handler.player.getName().asString());
        MessageTool.sendSystemMessage(handler.getPlayer(),Data.serverConnectBannerForPlayer);
    }
}
