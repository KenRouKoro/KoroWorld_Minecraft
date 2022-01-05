package cn.korostudio.koroworld.command;

import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.http.HttpException;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.korostudio.koroworld.Koroworld;
import cn.korostudio.koroworld.data.PlayerChatData;
import cn.korostudio.koroworld.data.PlayerPOSData;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.enums.ReadyState;
import org.java_websocket.handshake.ServerHandshake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static net.minecraft.server.command.CommandManager.literal;

public class KoroCommand {
    protected static Logger logger = LoggerFactory.getLogger(KoroCommand.class);

    public static void registerCommand(CommandDispatcher<ServerCommandSource> server, boolean b) {
        //option
        server.register(literal("backdeath").executes(KoroCommand::backdeath).then(CommandManager.argument("player", StringArgumentType.string())));
        server.register(literal("spawn").executes(KoroCommand::spawn).then(CommandManager.argument("player", StringArgumentType.string())));
        server.register(literal("update").executes(KoroCommand::update).then(CommandManager.argument("player", StringArgumentType.string())));
        server.register(literal("updateAll").executes(KoroCommand::updateAll).then(CommandManager.argument("option", StringArgumentType.string())));
        server.register(literal("connectWS").executes(KoroCommand::connectWS).then(CommandManager.argument("option", StringArgumentType.string())));
    }

    static public int backdeath (CommandContext<ServerCommandSource> server){
        ServerPlayerEntity player = null;
        try {
             player = server.getSource().getPlayer();
        } catch (CommandSyntaxException e) {
            return 1;
        }
        PlayerPOSData posData = PlayerPOSData.deathMap.get(player.getUuidAsString());
        if(posData==null){
            Koroworld.sendMessage(player,"您在本次轮回还没死过哦~");
            return 1;
        }
        Koroworld.sendMessage(player,"正在返回死亡地点......");
        player.teleport(posData.getWorld(),posData.getX(),posData.getY(),posData.getZ(),0,0);
        Koroworld.sendMessage(player,"返回死亡地点完成~");
        return 1;
    }
    public static int spawn(CommandContext<ServerCommandSource> objectCommandContext){
        try{
            ServerPlayerEntity player = objectCommandContext.getSource().getPlayer();
            ServerWorld overworld = Koroworld.server.getOverworld();
            BlockPos spawnpoint = overworld.getSpawnPos();
            player.teleport(overworld,spawnpoint.getX(),spawnpoint.getY(),spawnpoint.getZ(),0,overworld.getSpawnAngle());
            Koroworld.sendMessage(player,"已经传送到出生点~");
        }catch (Exception ex){
            ex.printStackTrace(System.out);
        }
        return 1;
    }
    public static int update(CommandContext<ServerCommandSource> server){
        ServerPlayerEntity serverPlayer = null;
        try {
            serverPlayer = server.getSource().getPlayer();
        } catch (CommandSyntaxException e) {
            return 1;
        }
        ServerPlayerEntity finalServerPlayer = serverPlayer;
        new Thread(() -> {
            Koroworld.sendMessage(finalServerPlayer,"正在上传您的数据......");
            PlayerEntity player = finalServerPlayer.getInventory().player;
            upToServer(player, logger);
            Koroworld.sendMessage(finalServerPlayer,"数据上传完成！");
        }).start();

        return 1;
    }

    public static int updateAll(CommandContext<ServerCommandSource> server){
        List<ServerPlayerEntity>players =  server.getSource().getServer().getPlayerManager().getPlayerList();

        return 1;
    }

    public static int connectWS(CommandContext<ServerCommandSource> server){
        try {
            if (Koroworld.serverWSClient.isOpen())return 1;
            String wsURL = Koroworld.setting.getStr("server", "ws://127.0.0.1:18620")+"/message/ws"+"?token="+Koroworld.serverNameStr;
            URI uri = new URI(wsURL);
            Koroworld.serverWSClient = new WebSocketClient(uri){

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
                    logger.info("WS Connect Close,Try Connecting.");
                    ThreadUtil.execute(()->{
                        boolean connect = false;
                        while (!connect) {
                            logger.info("WS Connect Close,Try Connecting in 5s.");
                            try {
                                Thread.sleep(5000);
                            } catch (InterruptedException ignored) {
                            }
                            Koroworld.serverWSClient.connect();
                            int index = 0;
                            while ((!(Koroworld.serverWSClient.getReadyState().equals(ReadyState.OPEN)) )&& index <= 10) {
                                try {
                                    Thread.sleep(100);
                                } catch (InterruptedException ignored) {
                                }
                                logger.info("Conning......");
                                index++;
                            }
                            connect = index > 10||!(Koroworld.serverWSClient.getReadyState().equals(ReadyState.OPEN));
                        }
                    });
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
        Koroworld.serverWSClient.connect();

        return 1;
    }

    public static void upToServer(PlayerEntity player, Logger logger) {
        String lock;
        try{
            HashMap<String,Object> keyPostMap = new HashMap<>();
            keyPostMap.put("key","set");
            keyPostMap.put("UUID",player.getUuidAsString());
            keyPostMap.put("value","true");
            lock = HttpUtil.post(Koroworld.setting.getStr("server","http://127.0.0.1:18620") + "/item/lock", keyPostMap);
        }catch (HttpException ignored){
        }finally {
            logger.info("Lock Data Finish.");
        }

        DefaultedList<ItemStack> main = player.getInventory().main;
        DefaultedList<ItemStack> armor = player.getInventory().armor;
        DefaultedList<ItemStack> offHand = player.getInventory().offHand;

        ArrayList<String> mainArraylist = new ArrayList<>();
        ArrayList<String> armorArraylist = new ArrayList<>();
        ArrayList<String> offHandArraylist = new ArrayList<>();

        for (ItemStack itemStack : main) {
            mainArraylist.add(itemStack.writeNbt(new NbtCompound()).asString());
        }
        for (ItemStack itemStack : armor) {
            armorArraylist.add(itemStack.writeNbt(new NbtCompound()).asString());
        }
        for (ItemStack itemStack : offHand) {
            offHandArraylist.add(itemStack.writeNbt(new NbtCompound()).asString());
        }


        logger.info("Player:" + player.getName().getString() + " UUID is :" + player.getUuidAsString() + " Data is uploading to ServerCore.");
        String PlayerItemDataStr;

        JSONArray mainJSON = JSONUtil.parseArray(mainArraylist);
        JSONArray armorJSON = JSONUtil.parseArray(armorArraylist);
        JSONArray offHandJSON = JSONUtil.parseArray(offHandArraylist);

        JSONObject PlayerItemDataJSON = JSONUtil.createObj();

        HashMap<String, JSONArray> PlayerItemDataMap = new HashMap<>();

        PlayerItemDataMap.put("mainJSON", mainJSON);
        PlayerItemDataMap.put("armorJSON", armorJSON);
        PlayerItemDataMap.put("offHandJSON", offHandJSON);

        PlayerItemDataJSON.putAll(PlayerItemDataMap);

        PlayerItemDataStr = PlayerItemDataJSON.toString();

        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("UUID", player.getUuidAsString());
        paramMap.put("SNBT", PlayerItemDataStr);
        String result = null;
        try {
            result = HttpUtil.post(Koroworld.setting.getStr("server","http://127.0.0.1:18620") + "/item/upload", paramMap);
        } catch (HttpException e) {
            e.printStackTrace();
        } finally {
            if (result == null || !result.equals("get"))
                logger.info("Server Connect Fail.");
            else
                logger.info("Player:" + player.getName().getString() + " UUID is :" + player.getUuidAsString() + " Data is uploading to ServerCore Finish.");
            try{
                HashMap<String,Object> keyPostMap = new HashMap<>();
                keyPostMap.put("key","set");
                keyPostMap.put("UUID",player.getUuidAsString());
                keyPostMap.put("value","false");
                lock = HttpUtil.post(Koroworld.setting.getStr("server","http://127.0.0.1:18620") + "/item/lock", keyPostMap);
            }catch (HttpException ignored){
            }finally {
                logger.info("Unlock Data Finish.");
            }
        }
    }


}
