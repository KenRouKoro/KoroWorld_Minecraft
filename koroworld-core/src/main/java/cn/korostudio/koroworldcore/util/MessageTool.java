package cn.korostudio.koroworldcore.util;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.file.FileReader;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.korostudio.koroworldcore.KoroworldCore;
import cn.korostudio.koroworldcore.data.Data;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.network.MessageType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class MessageTool {

    protected static Logger logger = LoggerFactory.getLogger(MessageTool.class);

    @Getter
    protected static Map<String,String> baseValues;
    @Setter
    @Getter
    protected static String chatTemplate;
    @Setter
    @Getter
    protected static String opChatTemplate;
    @Setter
    @Getter
    protected static String timeTemplate;
    @Setter
    @Getter
    protected static String dateTemplate;
    @Setter
    @Getter
    protected static String systemTemplate;

    @Getter
    protected static Vector<MessageTemplateValue>listeners;

    static {
        listeners = new Vector<>();
    }

    public static void initTemplateValueMap(){
        baseValues = new ConcurrentHashMap<>();
        baseValues.put("br","\n");
        baseValues.put("system",KoroworldCore.getSystemName());
        baseValues.put("server",KoroworldCore.getServerName());
        FileReader fileReader =new FileReader(FileUtil.touch(System.getProperty("user.dir")+"/koroworld/config/custom.json"));
        try {
            JSONObject jsonObject = JSONUtil.parseObj(fileReader.readString());
            Set<Map.Entry<String, Object>> entrySet = jsonObject.entrySet();
            for(Map.Entry<String, Object> entry: entrySet){
                baseValues.put(entry.getKey(),String.valueOf(entry.getValue()));
            }
        }catch (Exception e){
            logger.info("自定义变量Json文件是空的或者语法有错误，不用担心，这条只是提示而已。");
        }
    }

    public static int getPermissionLevel(ServerPlayerEntity player) {
        return Data.server.getPermissionLevel(player.getGameProfile());
    }
    public static int getPermissionLevel(String player) {
        try {
            return Data.server.getPermissionLevel(Data.server.getPlayerManager().getPlayer(player).getGameProfile());
        }catch (Exception e){
            return 0;
        }
    }

    public static Map<String,String> getTemplateValue(MinecraftServer server){
        ConcurrentHashMap<String, String> buildingMap = new ConcurrentHashMap<>();
        buildingMap.putAll(baseValues);
        buildingMap.put("number-of-player", String.valueOf(server.getPlayerManager().getPlayerList().size()));
        buildingMap.put("time", DateTime.now().toString(timeTemplate));
        buildingMap.put("date", DateTime.now().toString(dateTemplate));
        return buildingMap;
    }

    public static void sendMessage(ServerPlayerEntity player,String text,String template,Map<String,String>values){
        text = StrUtil.format(text,values);
        values.put("player",player.getName().asString());
        values.put("now-player",player.getName().asString());
        values.put("text",text);
        for(MessageTemplateValue messageTemplateValue:listeners){
            messageTemplateValue.changeValue(values,player);
        }
        String sendStr = StrUtil.format(template,values);
        player.sendSystemMessage(new LiteralText(sendStr),player.getUuid());
    }

    public static void sendMessageAll(MinecraftServer server, String text,String template,Map<String,String>values){
        text = StrUtil.format(text,values);
        values.put("text",text);
        for(ServerPlayerEntity targetPlayer : server.getPlayerManager().getPlayerList()){
            HashMap<String, String> sendMap = new HashMap<>(values);
            sendMap.put("now-player",targetPlayer.getName().asString());
            for(MessageTemplateValue messageTemplateValue:listeners){
                messageTemplateValue.changeValueInAll(values,targetPlayer);
            }
            targetPlayer.sendSystemMessage(new LiteralText(StrUtil.format(template,sendMap)),targetPlayer.getUuid());
        }
    }

    public static void sendCharMessage(ServerPlayerEntity player,String text,Map<String,String>values) {
        if(getPermissionLevel(player)>=2) {
            sendMessage(player, text, opChatTemplate, values);
        }else {
            sendMessage(player, text, chatTemplate, values);
        }
    }
    public static void sendCharMessage(ServerPlayerEntity player,String text){
        Map<String,String> values = getTemplateValue(Data.server);
        sendCharMessage(player,text,values);
    }
    public static void sendCharMessage(String player,String text){
        sendCharMessage(Data.server.getPlayerManager().getPlayer(player),text);
    }

    public static void sendCharMessageAll(MinecraftServer server, String text,Map<String,String>values){
        logger.info("Player:"+values.get("player")+" say:"+text);
        if(getPermissionLevel(values.get("player"))>=2) {
            sendMessageAll(server, text, opChatTemplate, values);
        }else{
            sendMessageAll(server, text, chatTemplate, values);
        }
    }
    public static void sendCharMessageAll(MinecraftServer server, String text){
        Map<String,String> values = getTemplateValue(Data.server);
        sendCharMessageAll(server,text,values);
    }
    public static void sendCharMessageAll(String text){
        sendCharMessageAll(Data.server,text);
    }
    public static void sendCharMessageAll(String text,String playername){
        Map<String,String> values = getTemplateValue(Data.server);
        values.put("player",playername);
        sendCharMessageAll(Data.server,text,values);
    }
    public static void sendCharMessageAll(String text,String playername,String servername){
        Map<String,String> values = getTemplateValue(Data.server);
        values.put("player",playername);
        values.put("servername",servername);
        sendCharMessageAll(Data.server,text,values);
    }

    public static void sendSystemMessage(ServerPlayerEntity player,String text,Map<String,String>values) {
        sendMessage(player,text,systemTemplate,values);
    }
    public static void sendSystemMessage(ServerPlayerEntity player,String text){
        Map<String,String> values = getTemplateValue(Data.server);
        sendSystemMessage(player,text,values);
    }
    public static void sendSystemMessage(String player,String text){
        sendSystemMessage(Data.server.getPlayerManager().getPlayer(player),text);
    }

    public static void sendSystemMessageAll(MinecraftServer server, String text,Map<String,String>values){
        sendMessageAll(server,text,systemTemplate,values);
    }
    public static void sendSystemMessageAll(MinecraftServer server, String text){
        Map<String,String> values = getTemplateValue(Data.server);
        sendSystemMessageAll(server,text,values);
    }
    public static void sendSystemMessageAll(String text){
        sendSystemMessageAll(Data.server,text);
    }
    public static void sendSystemMessageAll(String text,String playername){
        Map<String,String> values = getTemplateValue(Data.server);
        values.put("player",playername);
        sendSystemMessageAll(Data.server,text,values);
    }
}
