package cn.korostudio.koroworld.core.util;


import cn.korostudio.koroworld.core.data.Data;
import lombok.Getter;
import net.minecraft.network.MessageType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

public class MessageTool {
    protected static final Logger logger = LoggerFactory.getLogger(MessageTool.class);
    @Getter
    protected static final CopyOnWriteArrayList<MessageTemplateValue> listeners = new CopyOnWriteArrayList<>();

    public static void Broadcast(Text sendText) {
        Broadcast(sendText,Util.NIL_UUID);
    }
    public static void Broadcast(Text sendText , UUID uuid) {
        Data.server.getPlayerManager().broadcast(sendText, MessageType.SYSTEM, uuid );
    }

    public static void Say(ServerPlayerEntity serverPlayerEntity, Text sendText) {
        Say(serverPlayerEntity,sendText,Util.NIL_UUID);
    }
    public static void Say(ServerPlayerEntity serverPlayerEntity, Text sendText,UUID uuid) {
        serverPlayerEntity.sendMessage(sendText, MessageType.SYSTEM, uuid);
    }



    public interface SayAfterProcessor{
        Text process(ServerPlayerEntity serverPlayerEntity,Text sendText,UUID uuid);
    }
    public interface SayBeforeProcessor{
        Text process(ServerPlayerEntity serverPlayerEntity,Text sendText,UUID uuid);
    }
    public interface BroadcastAfterProcessor{
        Text process(Text sendText,UUID uuid);
    }
    public interface BroadcastBeforeProcessor{
        Text process(Text sendText,UUID uuid);
    }
}
