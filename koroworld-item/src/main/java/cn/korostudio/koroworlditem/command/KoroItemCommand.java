package cn.korostudio.koroworlditem.command;

import cn.hutool.core.thread.ThreadUtil;
import cn.korostudio.koroworldcore.KoroworldCore;
import cn.korostudio.koroworldcore.data.Data;
import cn.korostudio.koroworldcore.util.MessageTool;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static cn.korostudio.koroworlditem.uitl.ItemServiceTool.upToServer;
import static net.minecraft.server.command.CommandManager.literal;

public class KoroItemCommand {
    protected static Logger logger = LoggerFactory.getLogger(KoroItemCommand.class);

    public static void registerCommand(CommandDispatcher<ServerCommandSource> server, boolean b) {
        //option
        server.register(literal("update").executes(KoroItemCommand::update).then(CommandManager.argument("player", StringArgumentType.string())));
        server.register(literal("updateAll").requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2)).executes(KoroItemCommand::updateAll).then(CommandManager.argument("option", StringArgumentType.string())));
    }
    public static int update(CommandContext<ServerCommandSource> server){
        if(!Data.commandEn.getBool("updateEn",true)){
            return 1;
        }
        ServerPlayerEntity serverPlayer = null;
        try {
            serverPlayer = server.getSource().getPlayer();
        } catch (CommandSyntaxException e) {
            return 1;
        }
        ServerPlayerEntity finalServerPlayer = serverPlayer;
        ThreadUtil.execute(() -> {
            MessageTool.sendSystemMessage(finalServerPlayer,"正在上传您的数据......");
            PlayerEntity player = finalServerPlayer.getInventory().player;
            upToServer(player, logger);
            MessageTool.sendSystemMessage(finalServerPlayer,"数据上传完成！");
        });

        return 1;
    }

    public static int updateAll(CommandContext<ServerCommandSource> server){
        List<ServerPlayerEntity> players =  server.getSource().getServer().getPlayerManager().getPlayerList();
        ThreadUtil.execute(() -> {
            for(ServerPlayerEntity finalServerPlayer:players) {
                MessageTool.sendSystemMessage(finalServerPlayer, "正在上传您的数据......");
                PlayerEntity player = finalServerPlayer.getInventory().player;
                upToServer(player, logger);
                MessageTool.sendSystemMessage(finalServerPlayer, "数据上传完成！");
            }
        });
        return 1;
    }


}
