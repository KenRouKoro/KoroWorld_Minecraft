package cn.korostudio.koroworld;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.json.JSONUtil;
import cn.hutool.setting.Setting;
import cn.korostudio.koroworld.Events.DeathEvent;
import cn.korostudio.koroworld.Events.LoadEvent;
import cn.korostudio.koroworld.Events.interfaces.PlayerDeathEvent;
import cn.korostudio.koroworld.Events.UnloadEvent;
import cn.korostudio.koroworld.command.KoroCommand;
import cn.korostudio.koroworld.data.PlayerChatData;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.enums.ReadyState;
import org.java_websocket.handshake.ServerHandshake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.WebSocket;
import java.util.Iterator;
import java.util.List;

public class Koroworld implements DedicatedServerModInitializer {

    public static Setting setting ;
    public static String serverNameStr;
    protected static Logger logger = LoggerFactory.getLogger(Koroworld.class);
    public static MinecraftServer server;
    public static WebSocketClient serverWSClient;

    @Override
    public void onInitializeServer() {
        logger.info("""
                 
                 _   __                  _____ _             _ _
                | | / /                 /  ___| |           | (_)
                | |/ /  ___  _ __ ___   \\ `--.| |_ _   _  __| |_  ___
                |    \\ / _ \\| '__/ _ \\   `--. \\ __| | | |/ _` | |/ _ \\
                | |\\  \\ (_) | | | (_) | /\\__/ / |_| |_| | (_| | | (_) |
                \\_| \\_/\\___/|_|  \\___/  \\____/ \\__|\\__,_|\\__,_|_|\\___/
                """);


        logger.info("Load Config.");
        setting = new Setting(FileUtil.touch("koroworld/config/koroworld.setting"), CharsetUtil.CHARSET_UTF_8,true);
        logger.info("Config File: "+FileUtil.touch("koroworld/config/koroworld.setting").getAbsolutePath().toString());
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
        player.sendSystemMessage(new LiteralText("§6[KoroServerCore] §r"+text),player.getUuid());
    }
    public static void sendMessage(ServerPlayerEntity player, String text){
        player.sendSystemMessage(new LiteralText(text),player.getUuid());
    }
    public static void sendMessage(String serverName, ServerPlayerEntity player, String text){
        player.sendSystemMessage(new LiteralText("§6[KoroServerCore] §r["+serverName+"] §r"+text),player.getUuid());
    }
    public static void sendMessageAll(String serverName,MinecraftServer server, String text){
        ThreadUtil.execute(()->{
            List<ServerPlayerEntity> players = server.getPlayerManager().getPlayerList();
            for(ServerPlayerEntity player:players){
                sendMessage(serverName,player,text);
            }
        });
    }
    public static void sendMessageAllServer(String serverName,MinecraftServer server, String text){
        ThreadUtil.execute(()->{
            List<ServerPlayerEntity> players = server.getPlayerManager().getPlayerList();
            for(ServerPlayerEntity player:players){
                sendMessage(player,"§a["+serverName+"] §r"+text);
            }
        });
    }

    public static void wsConnect(){
        try {
        String wsURL = setting.getStr("wsserver", "ws://127.0.0.1:18620")+"/message/ws"+"?token="+serverNameStr;
        URI uri = new URI(wsURL);
            serverWSClient = new WebSocketClient(uri){

            /**
             * Called after an opening handshake has been performed and the given websocket is ready to be
             * written on.
             *
             * @param handshakedata The handshake of the websocket instance
             */
            @Override
            public void onOpen(ServerHandshake handshakedata) {
                logger.info("WS Connect!");
            }

            /**
             * Callback for string messages received from the remote host
             *
             * @param message The UTF-8 decoded message that was received.
             **/
            @Override
            public void onMessage(String message) {
                PlayerChatData chatData = JSONUtil.toBean(message, PlayerChatData.class);
                String messageStr = "§b["+chatData.getName()+"] §r"+chatData.getMessage() ;
                logger.info(messageStr);
                Koroworld.sendMessageAllServer(chatData.getServer(),Koroworld.server,messageStr);
            }

            /**
             * Called after the websocket connection has been closed.
             *
             * @param reason Additional information string
             * @param remote Returns whether or not the closing of the connection was initiated by the remote
             **/
            @Override
            public void onClose(int code, String reason, boolean remote) {
                logger.info("WS Connect Close.");
            }

            /**
             * Called when errors occurs. If an error causes the websocket connection to fail {@link
             * #onClose(int, String, boolean)} will be called additionally.<br> This method will be called
             * primarily because of IO or protocol errors.<br> If the given exception is an RuntimeException
             * that probably means that you encountered a bug.<br>
             *
             * @param ex The exception causing this error
             **/
            @Override
            public void onError(Exception ex) {
                ex.printStackTrace();
            }
        };
        } catch (URISyntaxException e) {
            logger.error("WS Connect FAIL!");
        }
        //serverWSClient.addHeader("token",serverNameStr);
        serverWSClient.connect();
    }

}
