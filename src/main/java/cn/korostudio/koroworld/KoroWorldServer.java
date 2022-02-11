package cn.korostudio.koroworld;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.setting.Setting;
import cn.korostudio.koroworld.Events.DeathEvent;
import cn.korostudio.koroworld.Events.LoadEvent;
import cn.korostudio.koroworld.Events.interfaces.PlayerDeathEvent;
import cn.korostudio.koroworld.Events.UnloadEvent;
import cn.korostudio.koroworld.command.KoroCommand;
import cn.korostudio.koroworld.ws.ServerWSClient;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import org.java_websocket.client.WebSocketClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import static cn.korostudio.koroworld.KoroWorldMain.setting;

public class KoroWorldServer implements DedicatedServerModInitializer {


    public static String serverNameStr;
    protected static Logger logger = LoggerFactory.getLogger(KoroWorldServer.class);
    public static MinecraftServer server;
    public static WebSocketClient serverWSClient;

    @Override
    public void onInitializeServer() {
        logger.info("""
                 
                 power by:
                 _   __                  _____ _             _ _
                | | / /                 /  ___| |           | (_)
                | |/ /  ___  _ __ ___   \\ `--.| |_ _   _  __| |_  ___
                |    \\ / _ \\| '__/ _ \\   `--. \\ __| | | |/ _` | |/ _ \\
                | |\\  \\ (_) | | | (_) | /\\__/ / |_| |_| | (_| | | (_) |
                \\_| \\_/\\___/|_|  \\___/  \\____/ \\__|\\__,_|\\__,_|_|\\___/
                """);


        logger.info("Load Config.");
        logger.info("Config File: "+FileUtil.touch(System.getProperty("user.dir")+"/koroworld/config/koroworld.setting").getAbsolutePath().toString());
        serverNameStr = setting.getStr("servername","KoroWorld");
        logger.info("Register Events.");
        registerEvents();
        logger.info("Mod Load Finish.");
        wsConnect();
        logger.info("WSConnect Finish.");
    }
    private void registerEvents(){
        ServerPlayConnectionEvents.JOIN.register(LoadEvent::onSpawn);
        ServerPlayConnectionEvents.DISCONNECT.register(UnloadEvent::onSpawn);
        CommandRegistrationCallback.EVENT.register(KoroCommand::registerCommand);
        PlayerDeathEvent.EVENT.register(DeathEvent::onDeath);

        ServerLifecycleEvents.SERVER_STARTED.register(minecraftServer -> {
            server = minecraftServer;
        });
    }
    public static void sendMessageServer(ServerPlayerEntity player, String text){
        player.sendSystemMessage(new LiteralText("§6["+setting.getStr("systemname","KoroServerCore")+"] §r"+text),player.getUuid());
    }
    public static void sendMessage(ServerPlayerEntity player, String text){
        player.sendSystemMessage(new LiteralText(text),player.getUuid());
    }
    public static void sendMessage(String serverName, ServerPlayerEntity player, String text){
        player.sendSystemMessage(new LiteralText("§6["+setting.getStr("systemname","KoroServerCore")+"] §r["+serverName+"] §r"+text),player.getUuid());
    }
    public static void sendMessageAll(String serverName,MinecraftServer server, String text){
        ThreadUtil.execute(()->{
            List<ServerPlayerEntity> players = server.getPlayerManager().getPlayerList();
            for(ServerPlayerEntity player:players){
                sendMessage(serverName,player,text);
            }
        });
    }
    public static void sendMessageAll(MinecraftServer server, String text){
        ThreadUtil.execute(()->{
            List<ServerPlayerEntity> players = server.getPlayerManager().getPlayerList();
            for(ServerPlayerEntity player:players){
                sendMessage(player,text);
            }
        });
    }
    public static void sendMessageAllServer(String serverName,MinecraftServer server, String text){
        ThreadUtil.execute(()->{
            List<ServerPlayerEntity> players = server.getPlayerManager().getPlayerList();
            for(ServerPlayerEntity player:players){
                sendMessage(player,"§r§a["+serverName+"§a]§r§r "+"§f"+text);
            }
        });
    }

    public static void wsConnect(){
        try {
        String wsURL = setting.getStr("wsserver", "ws://127.0.0.1:18620")+"/message/ws"+"?token="+serverNameStr;
        URI uri = new URI(wsURL);
            serverWSClient = new ServerWSClient(uri);
        } catch (URISyntaxException e) {
            logger.error("WS Connect FAIL!");
        }
        serverWSClient.connect();
    }

}
