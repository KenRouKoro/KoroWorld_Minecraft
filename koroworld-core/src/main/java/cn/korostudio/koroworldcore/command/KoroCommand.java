package cn.korostudio.koroworldcore.command;
import cn.korostudio.koroworldcore.KoroworldCore;
import cn.korostudio.koroworldcore.data.Data;
import cn.korostudio.koroworldcore.data.PlayerPOSData;
import cn.korostudio.koroworldcore.util.MessageTool;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.argument.MessageArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import static net.minecraft.server.command.CommandManager.literal;

public class KoroCommand {
    protected static Logger logger = LoggerFactory.getLogger(KoroCommand.class);

    public static void registerCommand(CommandDispatcher<ServerCommandSource> server, boolean b) {
        //option
        server.register(literal("backdeath").executes(KoroCommand::backDeath).then(CommandManager.argument("player", StringArgumentType.string())));
        server.register(literal("spawn").executes(KoroCommand::spawn).then(CommandManager.argument("player", StringArgumentType.string())));
    }

    static public int backDeath (CommandContext<ServerCommandSource> server){
        ServerPlayerEntity player = null;
        try {
             player = server.getSource().getPlayer();
        } catch (CommandSyntaxException e) {
            return 1;
        }
        PlayerPOSData posData = PlayerPOSData.deathMap.get(player.getUuidAsString());
        if(posData==null){
            MessageTool.sendSystemMessage(player,"您在本次轮回还没死过哦~", KoroworldCore.getServerName());
            return 1;
        }
        MessageTool.sendSystemMessage(player,"正在返回死亡地点......",KoroworldCore.getServerName());
        player.teleport(posData.getWorld(),posData.getX(),posData.getY(),posData.getZ(),0,0);
        MessageTool.sendSystemMessage(player,"返回死亡地点完成~",KoroworldCore.getServerName());
        return 1;
    }
    public static int spawn(CommandContext<ServerCommandSource> objectCommandContext){
        try{
            ServerPlayerEntity player = objectCommandContext.getSource().getPlayer();
            ServerWorld overworld = Data.server.getOverworld();
            BlockPos spawnpoint = overworld.getSpawnPos();
            player.teleport(overworld,spawnpoint.getX(),spawnpoint.getY(),spawnpoint.getZ(),0,overworld.getSpawnAngle());
            MessageTool.sendSystemMessage(player,"已经传送到出生点~",KoroworldCore.getServerName());
        }catch (Exception ex){
            ex.printStackTrace(System.out);
        }
        return 1;
    }

}
