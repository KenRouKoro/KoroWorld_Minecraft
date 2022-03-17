package cn.korostudio.koroworldmessage.command;

import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.json.JSONUtil;
import cn.korostudio.koroworldcore.KoroworldCore;
import cn.korostudio.koroworldcore.util.MessageTool;
import cn.korostudio.koroworldmessage.KoroworldMessage;
import cn.korostudio.koroworldmessage.KoroworldMessageServer;
import cn.korostudio.koroworldmessage.data.MessageSystemData;
import cn.korostudio.koroworldmessage.data.PlayerChatData;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.argument.MessageArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static net.minecraft.server.command.CommandManager.literal;

public class KoroMessageCommand {
    protected static Logger logger = LoggerFactory.getLogger(KoroMessageCommand.class);

    public static void registerCommand(CommandDispatcher<ServerCommandSource> server, boolean b) {
        //option
        server.register(literal("broadcast").requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2)).then(CommandManager.argument("message", MessageArgumentType.message()).executes(KoroMessageCommand::broadcast)));
        server.register(literal("broadcastSU").requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2)).then(CommandManager.argument("message", MessageArgumentType.message()).executes(KoroMessageCommand::broadcastSU)));
        server.register(literal("connectWS").requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2)).executes(KoroMessageCommand::connectWS));
    }

    public static int connectWS(CommandContext<ServerCommandSource> server){
        Entity entity = ((ServerCommandSource)server.getSource()).getEntity();
        if (KoroworldMessageServer.getServerWSClient()==null){
            return 1;
        }
        if (KoroworldMessageServer.getServerWSClient().isOpen()){
            if(entity==null){
                KoroworldMessage.getLogger().error("WS已在链接。");
            }else{
                try {
                    MessageTool.sendSystemMessage(server.getSource().getPlayer(),"{player},WS已在链接中，请勿多次链接。",KoroworldCore.getServerName());
                } catch (CommandSyntaxException ignored) {
                }
            }
        }else{
            try {
                MessageTool.sendSystemMessage(server.getSource().getPlayer(),"{player},系统正在尝试链接WS。",KoroworldCore.getServerName());
            } catch (CommandSyntaxException ignored) {
            }
            KoroworldMessageServer.connectWSServer();
        }
        return 1;
    }

    public static int broadcast(CommandContext<ServerCommandSource> server){
        Text text;
        Entity entity = ((ServerCommandSource)server.getSource()).getEntity();
        try {
            text = MessageArgumentType.getMessage(server, "message");
        } catch (CommandSyntaxException e) {
            return 1;
        }
        String message = text.asString();

        PlayerChatData chatData = new PlayerChatData();
        chatData.setSystem(true);
        chatData.setHighestAuthority(false);
        chatData.setMessage(message);
        chatData.setGroup(MessageSystemData.MessageGroup);
        if (entity!=null) {
            chatData.setName(entity.getName().asString());
        }else{
            chatData.setName("null");
        }
        chatData.setServer(KoroworldCore.getServerName());
        ThreadUtil.execute(()->{
            KoroworldMessage.getLogger().info("Send broadcast Message To Server.");
            KoroworldMessageServer.getServerWSClient().send(JSONUtil.toJsonStr(chatData));
        });
        MessageTool.sendSystemMessageAll(chatData.getMessage(),chatData.getServer());
        return 1;
    }
    public static int broadcastSU(CommandContext<ServerCommandSource> server){
        Text text;
        Entity entity = ((ServerCommandSource)server.getSource()).getEntity();
        try {
            text = MessageArgumentType.getMessage(server, "message");
        } catch (CommandSyntaxException e) {
            return 1;
        }
        String message = text.asString();
        PlayerChatData chatData = new PlayerChatData();
        chatData.setSystem(true);
        chatData.setHighestAuthority(true);
        chatData.setMessage(message);
        chatData.setGroup(MessageSystemData.MessageGroup);
        if (entity!=null) {
            chatData.setName(entity.getName().asString());
        }else{
            chatData.setName("null");
        }
        chatData.setServer(KoroworldCore.getServerName());
        ThreadUtil.execute(()->{
            KoroworldMessage.getLogger().info("Send broadcast Message To Server.");
            KoroworldMessageServer.getServerWSClient().send(JSONUtil.toJsonStr(chatData));
        });
        MessageTool.sendSystemMessageAll(chatData.getMessage(),chatData.getServer());

        return 1;
    }



}
