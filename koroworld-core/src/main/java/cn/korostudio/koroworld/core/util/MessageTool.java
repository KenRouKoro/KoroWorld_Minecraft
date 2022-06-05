package cn.korostudio.koroworld.core.util;


import cn.korostudio.koroworld.core.data.Data;
import lombok.Getter;
import net.minecraft.network.MessageType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class MessageTool {
    protected static final Logger logger = LoggerFactory.getLogger(MessageTool.class);
    @Getter
    protected static final CopyOnWriteArrayList<MessageTemplateValue>listeners = new CopyOnWriteArrayList<>();

    public static void Broadcast(Text sendText){
        Data.server.getPlayerManager().broadcast(sendText,MessageType.SYSTEM, Util.NIL_UUID);
    }
    public static void Say(ServerPlayerEntity serverPlayerEntity,Text sendText){
        serverPlayerEntity.sendMessage(sendText,MessageType.SYSTEM, Util.NIL_UUID);
    }


}
