package cn.korostudio.koroworldcore.command;
import cn.korostudio.koroworldcore.KoroworldCore;
import cn.korostudio.koroworldcore.data.Data;
import cn.korostudio.koroworldcore.data.PlayerPOSData;
import cn.korostudio.koroworldcore.util.MessageTool;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import lombok.Getter;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.util.Vector;

import static net.minecraft.server.command.CommandManager.literal;

public class KoroCommand {
    protected static Logger logger = LoggerFactory.getLogger(KoroCommand.class);
    @Getter
    protected static Vector<CommandCallBack> backCallBacks = new Vector<>();
    @Getter
    protected static Vector<CommandCallBack> spawnCallBacks = new Vector<>();

    public static void registerCommand(CommandDispatcher<ServerCommandSource> server, boolean b) {
        server.register(literal("back").executes(KoroCommand::back).then(CommandManager.argument("player", StringArgumentType.string())));
        server.register(literal("spawn").executes(KoroCommand::spawn).then(CommandManager.argument("player", StringArgumentType.string())));
        server.register(literal("corereload").requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2)).executes(KoroCommand::corereload).then(CommandManager.argument("player", StringArgumentType.string())));
    }
    static public int corereload (CommandContext<ServerCommandSource> server){
        KoroworldCore.loadSetting();
        MessageTool.initTemplateValueMap();
        ServerPlayerEntity player = null;
        try {
            player = server.getSource().getPlayer();
        } catch (CommandSyntaxException e) {
            return 1;
        }finally {
            logger.info("已重载配置");
        }

        return 1;
    }


    static public int back (CommandContext<ServerCommandSource> server){
        if(!Data.commandEn.getBool("backEn",true)){
            return 1;
        }

        for(CommandCallBack callBack:backCallBacks){
            if(!callBack.run(server)){
                return 1;
            }
        }

        ServerPlayerEntity player = null;
        try {
             player = server.getSource().getPlayer();
        } catch (CommandSyntaxException e) {
            return 1;
        }
        PlayerPOSData posData = PlayerPOSData.teleportMap.get(player.getUuidAsString());
        if(posData==null){
            MessageTool.sendSystemMessage(player,"您在本次轮回还没有地点记录哦~");
            return 1;
        }
        MessageTool.sendSystemMessage(player,"正在返回上一个关键地点......");
        player.teleport(posData.getWorld(),posData.getX(),posData.getY(),posData.getZ(),0,0);
        MessageTool.sendSystemMessage(player,"返回关键地点完成~");
        return 1;
    }
    public static int spawn(CommandContext<ServerCommandSource> objectCommandContext){
        if(!Data.commandEn.getBool("spawnEn",true)){
            return 1;
        }

        for(CommandCallBack callBack:spawnCallBacks) {
            if (!callBack.run(objectCommandContext)) {
                return 1;
            }
        }

        try{
            ServerPlayerEntity player = objectCommandContext.getSource().getPlayer();
            ServerWorld overworld = Data.server.getOverworld();
            BlockPos spawnpoint = overworld.getSpawnPos();
            player.teleport(overworld,spawnpoint.getX(),spawnpoint.getY(),spawnpoint.getZ(),0,overworld.getSpawnAngle());
            MessageTool.sendSystemMessage(player,"已经传送到出生点~");
        }catch (Exception ex){
            ex.printStackTrace(System.out);
        }
        return 1;
    }

    public static interface CommandCallBack{
        public boolean run(CommandContext<ServerCommandSource> server);
    }

}
