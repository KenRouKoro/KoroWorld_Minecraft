package cn.korostudio.koroworld.core.util;


import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.StrUtil;
import cn.korostudio.koroworld.core.KoroWorldCore;
import cn.korostudio.koroworld.core.data.Data;
import cn.korostudio.koroworld.core.event.interfaces.PlayerChatEvent;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.network.MessageType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class MessageTool {
    protected static final Logger logger = LoggerFactory.getLogger(MessageTool.class);
    @Getter
    protected static final CopyOnWriteArrayList<MessageTemplateValueProcessor> MessageTemplateValueProcessors = new CopyOnWriteArrayList<>();
    @Getter
    protected static final CopyOnWriteArrayList<SayProcessor> SayProcessors = new CopyOnWriteArrayList<>();
    @Getter
    protected static final CopyOnWriteArrayList<BroadcastProcessor> BroadcastProcessors = new CopyOnWriteArrayList<>();
    @Getter
    protected static final ConcurrentHashMap<String,String>StaticTemplateValues = new ConcurrentHashMap<>();


    @Setter
    @Getter
    protected static String timeTemplate;
    @Setter
    @Getter
    protected static String dateTemplate;

    @Setter
    @Getter
    protected static String ChatTemplate;

    @Setter
    @Getter
    protected static String SystemTemplate;

    public static void Init(){

        StaticTemplateValues.put("SystemName",KoroWorldCore.getSystemName());
        StaticTemplateValues.put("ServerName",KoroWorldCore.getServerName());
        StaticTemplateValues.put("br","\n");


        if(Data.KoroWorldConfig.getBool("TemplateEN","core",false)){
            KoroWorldCore.cancelChat = true;
            PlayerChatEvent.EVENT.register((player, text) -> {
                Chat(player.getName().asString(),text,player.getUuid(), ProcessValues(StaticTemplateValues,player));
                return ActionResult.PASS ;
            });
            RegisterDefMessageTemplateValueProcessor();
        }
    }
    protected static void RegisterDefMessageTemplateValueProcessor(){
        MessageTemplateValueProcessors.add((map,player)->{
            map.put("time", DateTime.now().toString(timeTemplate));
            map.put("date", DateTime.now().toString(dateTemplate));
            if (player!=null){
                map.put("player", player.getName().asString());
            }
        });
        BroadcastProcessors.add((sendText, uuid) -> {
            return new LiteralText("§6<"+KoroWorldCore.getSystemName()+">").append(sendText);
        });
    }
    public static Map<String,String> ProcessValues(Map<String,String>baseMap,ServerPlayerEntity player){
        ConcurrentHashMap<String,String> cacheMap = new ConcurrentHashMap<>(baseMap);
        for(MessageTemplateValueProcessor messageTemplateValueProcessor:MessageTemplateValueProcessors){
            messageTemplateValueProcessor.processValue(cacheMap,player);
        }
        return cacheMap;
    }
    public static void Broadcast(Text sendText) {
        Broadcast(sendText,Util.NIL_UUID);
    }
    public static void Broadcast(Text sendText , UUID uuid) {
        Text sendTextCache = sendText ;
        for(BroadcastProcessor broadProcessor:BroadcastProcessors){
            sendTextCache = broadProcessor.process(sendText,uuid);
        }
        Data.server.getPlayerManager().broadcast(sendTextCache, MessageType.SYSTEM, uuid );
    }

    public static void Say(ServerPlayerEntity serverPlayerEntity, Text sendText) {
        Say(serverPlayerEntity,sendText,Util.NIL_UUID);
    }
    public static void Say(ServerPlayerEntity serverPlayerEntity, Text sendText,UUID uuid) {
        Text sendTextCache = sendText ;
        for(SayProcessor sayProcessor:SayProcessors){
            sendTextCache = sayProcessor.process(serverPlayerEntity,sendText,uuid);
        }
        serverPlayerEntity.sendMessage(sendTextCache, MessageType.SYSTEM, uuid);
    }

    public static void Chat(String playerName, String text, UUID uuid, Map<String,String> values){
        boolean useTemplate = Data.KoroWorldConfig.getBool("TemplateEN","core",false);
        if(useTemplate){
            text = StrUtil.format(text,values);
            values.put("text",text);
            String showChatText = StrUtil.format(ChatTemplate,values);
            Data.server.getPlayerManager().broadcast(new LiteralText(showChatText),MessageType.CHAT,uuid);
        }else{
            Data.server.getPlayerManager().broadcast(new TranslatableText("chat.type.text", playerName, text), MessageType.CHAT, uuid );
        }
    }

    public interface MessageTemplateValueProcessor {
        void processValue(Map<String, String> values,ServerPlayerEntity player);
    }

    public interface SayProcessor{
        Text process(ServerPlayerEntity serverPlayerEntity,Text sendText,UUID uuid);
    }

    public interface BroadcastProcessor{
        Text process(Text sendText,UUID uuid);
    }

}
