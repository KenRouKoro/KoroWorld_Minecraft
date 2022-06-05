package cn.korostudio.koroworld.command.command;

import cn.korostudio.koroworld.core.data.Data;
import cn.korostudio.koroworld.core.data.PlayerPOSData;
import cn.korostudio.koroworld.core.util.MessageTool;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.BlockPos;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static net.minecraft.server.command.CommandManager.literal;

public class Command {

    protected static Logger logger = LoggerFactory.getLogger(Command.class);

    public static void register(CommandDispatcher<ServerCommandSource> server, boolean b){
        if(Data.commandEn.getBool("backEN",true)) server.register(literal("back").executes(Command::back).then(CommandManager.argument("player", StringArgumentType.string())));
        if(Data.commandEn.getBool("spawnEN",true)) server.register(literal("spawn").executes(Command::spawn).then(CommandManager.argument("player", StringArgumentType.string())));
    }

    //spawn
    public static int spawn(CommandContext<ServerCommandSource> server){
        try {
            ServerPlayerEntity player = server.getSource().getPlayer();
            float yaw,pitch;
            ServerWorld overworld = Data.server.getOverworld();
            BlockPos pos  = overworld.getSpawnPos();
            yaw=player.getYaw();
            pitch = player.getPitch();
            player.teleport(overworld,pos.getX(),pos.getY(),pos.getZ(),yaw,pitch);
            boolean backEN = Data.commandEn.getBool("backEN",true);
            MutableText spawnText =new TranslatableText("koroworld.spawn.main");
            if(backEN){
                MutableText backText =new TranslatableText("koroworld.spawn.back");
                backText.setStyle(Style.EMPTY.withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,"back")));
                spawnText.append(backText);
            }
            MessageTool.Say(player,spawnText);
        } catch (CommandSyntaxException ignored) {
            logger.info("别在控制台执行这个指令！");
        }
        return 1;
    }
    //back
    public static int back(CommandContext<ServerCommandSource> server){
        try {
            ServerPlayerEntity player = server.getSource().getPlayer();
            PlayerPOSData posData = PlayerPOSData.teleportMap.get(player.getUuidAsString());
            if(posData==null){
                MessageTool.Say(player,new TranslatableText("koroworld.back.null"));
                return 1;
            }
            MessageTool.Say(player,new TranslatableText("koroworld.back.backing"));
            player.teleport(posData.getServerWorld(),posData.getX(),posData.getY(),posData.getZ(),posData.getYaw(),posData.getPitch());
            MessageTool.Say(player,new TranslatableText("koroworld.back.backed"));
        } catch (CommandSyntaxException ignored) {
            logger.info("别在控制台执行这个指令！");
        }

        return 1;
    }




}
