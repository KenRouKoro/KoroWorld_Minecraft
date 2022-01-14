package cn.korostudio.koroworld.Events;

import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.json.JSONUtil;
import cn.korostudio.koroworld.KoroWorldServer;
import cn.korostudio.koroworld.data.PlayerChatData;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChatEven {
    static protected Logger logger = LoggerFactory.getLogger("Player Chat");
    public static void onPlayerChat(String message, ServerPlayerEntity sender){
        MinecraftServer server = KoroWorldServer.server;
        if(sender == null){
            KoroWorldServer.sendMessageAllServer(KoroWorldServer.serverNameStr,server, message);
            return;
        }
        String messageStr = "§b["+sender.getName().asString()+"] §r"+message ;
        logger.info(messageStr);
        KoroWorldServer.sendMessageAllServer(KoroWorldServer.serverNameStr, KoroWorldServer.server,messageStr);

        PlayerChatData chatData = new PlayerChatData();
        chatData.setBroadcast(false);
        chatData.setMessage(message);
        chatData.setName(sender.getName().asString());
        chatData.setServer(KoroWorldServer.serverNameStr);
        ThreadUtil.execute(()->{
            logger.info("Send Player:"+sender.getName().asString()+" Message To Server.");
            KoroWorldServer.serverWSClient.send(JSONUtil.toJsonStr(chatData));
        });
    }
}
