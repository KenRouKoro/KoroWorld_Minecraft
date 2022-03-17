package cn.korostudio.koroworldmessage.event;

import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.json.JSONUtil;
import cn.korostudio.koroworldmessage.data.MessageSystemData;
import cn.korostudio.koroworldmessage.data.PlayerChatData;
import cn.korostudio.koroworldcore.KoroworldCore;
import cn.korostudio.koroworldcore.util.MessageTool;
import cn.korostudio.koroworldmessage.KoroworldMessage;
import cn.korostudio.koroworldmessage.KoroworldMessageServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;

public class PlayerChatEvent {
    public static ActionResult onChat(ServerPlayerEntity player, String text){
        PlayerChatData chatData = new PlayerChatData();
        chatData.setSystem(false);
        chatData.setHighestAuthority(false);
        chatData.setMessage(text);
        chatData.setName(player.getName().asString());
        chatData.setServer(KoroworldCore.getServerName());
        chatData.setGroup(MessageSystemData.MessageGroup);
        ThreadUtil.execute(()->{
            KoroworldMessage.getLogger().info("Send Player:"+player.getName().asString()+" Message To Server.");
            KoroworldMessageServer.getServerWSClient().send(JSONUtil.toJsonStr(chatData));
        });
        return ActionResult.PASS;
    }
}
