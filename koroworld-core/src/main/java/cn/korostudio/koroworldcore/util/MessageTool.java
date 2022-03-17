package cn.korostudio.koroworldcore.util;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.StrUtil;
import cn.korostudio.koroworldcore.KoroworldCore;
import cn.korostudio.koroworldcore.data.Data;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;

import java.util.HashMap;
import java.util.List;

public class MessageTool {
    @Setter
    @Getter
    protected static String chatTemplate;
    @Setter
    @Getter
    protected static String timeTemplate;
    @Setter
    @Getter
    protected static String systemTemplate;
    public static void sendMessage(ServerPlayerEntity player, String text){
        player.sendSystemMessage(new LiteralText(text),player.getUuid());
    }
    public static void sendMessageAll(MinecraftServer server, String text){
        ThreadUtil.execute(()->{
            List<ServerPlayerEntity> players = server.getPlayerManager().getPlayerList();
            for(ServerPlayerEntity player:players){
                HashMap <String,String> value = new HashMap<>();
                value.put("now-player",player.getName().asString());
                sendMessage(player,StrUtil.format(text,value));
            }
        });
    }

    public static void sendCharMessage(ServerPlayerEntity player, String text,String servername,String playername){
        HashMap <String,String> value = new HashMap<>();
        value.put("player",playername);
        value.put("server",servername);
        value.put("time", DateTime.now().toString(timeTemplate));
        value.put("br","\n");
        text = StrUtil.format(text,value);
        value.put("text",text);
        String sendStr = StrUtil.format(chatTemplate,value);
        sendMessage(player,sendStr);
    }

    public static void sendCharMessageAll(String text,String servername,String playername){
        HashMap <String,String> value = new HashMap<>();
        value.put("player",playername);
        value.put("server",servername);
        value.put("time", DateTime.now().toString(timeTemplate));
        value.put("br","\n");
        text = StrUtil.format(text,value);
        value.put("text",text);
        String sendStr = StrUtil.format(chatTemplate,value);
        sendMessageAll(Data.server,sendStr);
    }

    public static void sendSystemMessage(ServerPlayerEntity player, String text,String servername){
        HashMap <String,String> value = new HashMap<>();
        value.put("server",servername);
        value.put("time", DateTime.now().toString(timeTemplate));
        value.put("player",player.getName().asString());
        value.put("system", KoroworldCore.getSystemName());
        value.put("br","\n");
        text = StrUtil.format(text,value);
        value.put("text",text);
        String sendStr = StrUtil.format(systemTemplate,value);
        sendMessage(player,sendStr);
    }

    public static void sendSystemMessage(ServerPlayerEntity sendPlayer, String  player, String text,String servername){
        HashMap <String,String> value = new HashMap<>();
        value.put("server",servername);
        value.put("time", DateTime.now().toString(timeTemplate));
        value.put("player",player);
        value.put("system", KoroworldCore.getSystemName());
        value.put("br","\n");
        text = StrUtil.format(text,value);
        value.put("text",text);
        String sendStr = StrUtil.format(systemTemplate,value);
        sendMessage(sendPlayer,sendStr);
    }
    public static void sendSystemMessageAll(String text,String servername){
        ThreadUtil.execute(()->{
            List<ServerPlayerEntity> players = Data.server.getPlayerManager().getPlayerList();
            for(ServerPlayerEntity player:players){
                sendSystemMessage(player,text,servername);
            }
        });
    }
    public static void sendSystemMessageAll(ServerPlayerEntity player,String text,String servername){
        ThreadUtil.execute(()->{
            List<ServerPlayerEntity> players = Data.server.getPlayerManager().getPlayerList();
            for(ServerPlayerEntity player1:players){
                HashMap <String,String> value = new HashMap<>();
                value.put("now-player",player1.getName().asString());
                sendSystemMessage(player1,player.getName().asString(),StrUtil.format(text,value),servername);
            }
        });
    }

    public static void sendSystemMessageAll(String player,String text,String servername){
        ThreadUtil.execute(()->{
            List<ServerPlayerEntity> players = Data.server.getPlayerManager().getPlayerList();
            for(ServerPlayerEntity player1:players){
                HashMap <String,String> value = new HashMap<>();
                value.put("now-player",player1.getName().asString());
                sendSystemMessage(player1,player,StrUtil.format(text,value),servername);
            }
        });
    }

}
