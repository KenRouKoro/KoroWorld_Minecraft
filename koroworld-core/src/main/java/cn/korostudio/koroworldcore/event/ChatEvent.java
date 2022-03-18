package cn.korostudio.koroworldcore.event;

import cn.korostudio.koroworldcore.KoroworldCore;
import cn.korostudio.koroworldcore.util.MessageTool;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;

public class ChatEvent {
    public static ActionResult onChat(ServerPlayerEntity player,String text){
        MessageTool.sendCharMessageAll(text, KoroworldCore.getServerName(),player.getName().asString());
        return ActionResult.PASS;
    }
}
