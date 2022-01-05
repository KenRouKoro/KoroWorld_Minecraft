package cn.korostudio.koroworld.Events;

import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.json.JSONUtil;
import cn.korostudio.koroworld.Koroworld;
import cn.korostudio.koroworld.data.PlayerChatData;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChatEven {
    static protected Logger logger = LoggerFactory.getLogger("Player Chat");
    public static void onPlayerChat(String message, ServerPlayerEntity sender){
        MinecraftServer server = Koroworld.server;
        if(sender == null){
            Koroworld.sendMessageAllServer(Koroworld.serverNameStr,server, message);
            return;
        }
        String messageStr = "§b["+sender.getName().asString()+"] §r"+message ;
        logger.info(messageStr);
        Koroworld.sendMessageAllServer(Koroworld.serverNameStr,Koroworld.server,messageStr);

        PlayerChatData chatData = new PlayerChatData();
        chatData.setBroadcast(false);
        chatData.setMessage(message);
        chatData.setName(sender.getName().asString());
        chatData.setServer(Koroworld.serverNameStr);
        ThreadUtil.execute(()->{
            logger.info("Send Player:"+sender.getName().asString()+" Message To Server.");
            Koroworld.serverWSClient.send(JSONUtil.toJsonStr(chatData));
        });
    }
}
