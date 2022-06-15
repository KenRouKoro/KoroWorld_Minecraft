package cn.korostudio.koroworld.multiserver.command;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.korostudio.koroworld.connect.KoroWorldConnect;
import cn.korostudio.koroworld.core.KoroWorldCore;
import cn.korostudio.koroworld.core.data.Data;
import cn.korostudio.koroworld.core.util.MessageTool;
import cn.korostudio.koroworld.multiserver.connectTool.ServerConnectTool;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import lombok.extern.slf4j.Slf4j;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.util.Objects;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;
@Slf4j
public class Command {
    public static void register(CommandDispatcher<ServerCommandSource> server, boolean b) {

        if(Data.commandEn.getBool("serverEN",true)) {
            server.register(literal("server").requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(Data.KoroWorldConfig.getInt("serverCommandLevel","multiserver",0))).executes(Command::server).then(argument("server", StringArgumentType.string()).requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(Data.KoroWorldConfig.getInt("playerTeleportCommandLevel","multiserver",0))).executes(Command::sendPlayer)));
            server.register(literal("connectServer").then(argument("player", EntityArgumentType.player()).then(argument("server", StringArgumentType.string()).requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(Data.KoroWorldConfig.getInt("serverTeleportCommandLevel","multiserver",2))).executes(Command::process))));
        }
    }

    public static int server(CommandContext<ServerCommandSource> server){
        ServerPlayerEntity player = null ;
        try{
            player = server.getSource().getPlayer();
        } catch (CommandSyntaxException ignored) {
            log.error("This is a player instruction!");
            return 1;
        }
        JSONObject servers = ServerConnectTool.getServerList();
        MessageTool.Say(player ,new TranslatableText("koroworld.ms.serverlist"));
        for(String names:servers.keySet()){
            String address = servers.getStr(names);

            if(names.length()>16){
                names=names.substring(0,15);
            }

            MutableText serverTEXT = new LiteralText(StrUtil.fillAfter(names,' ',20));
            TranslatableText connectText = new TranslatableText(Objects.equals(names, KoroWorldConnect.SUID) ? "koroworld.ms.nowserver":"koroworld.ms.connect" ,names);
            if(!Objects.equals(names, KoroWorldConnect.SUID)){
                serverTEXT.setStyle(Style.EMPTY.withColor(Formatting.AQUA).withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,"/server "+names)));
            }

            serverTEXT.setStyle(serverTEXT.getStyle().withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,connectText)));

            MessageTool.Say(player,serverTEXT);
        }


        return 1;
    }

    public static int sendPlayer(CommandContext<ServerCommandSource> server){
        ServerPlayerEntity player = null ;
        try{
            player = server.getSource().getPlayer();
        } catch (CommandSyntaxException ignored) {
            log.error("This is a player instruction!");
            return 1;
        }

        String servername = null;

        servername = StringArgumentType.getString(server,"server");

        if (servername==null){
            if (player!=null)MessageTool.Say(player ,new TranslatableText("koroworld.ms.servernameerror").setStyle(Style.EMPTY.withColor(Formatting.RED)));
            else log.error("Server Name Is NULL!");
            return 1;
        }

        ServerConnectTool.SetPlayerNextServer(player.getUuidAsString(),servername);

        ServerPlayNetworking.send(player,new Identifier("koroworld:reconnect"), PacketByteBufs.empty());

        return 1;
    }

    public static int process(CommandContext<ServerCommandSource> server){
        ServerPlayerEntity player = null ;
        try{
            player = server.getSource().getPlayer();
        } catch (CommandSyntaxException ignored) {
        }
        ServerPlayerEntity target;
        try {
            target = EntityArgumentType.getPlayer(server, "player");
        } catch (CommandSyntaxException ignored) {
            if (player!=null)MessageTool.Say(player ,new TranslatableText("koroworld.ms.playernameerror").setStyle(Style.EMPTY.withColor(Formatting.RED)));
            else log.error("Players do not exist！");
            return 1;
        }

        String servername = null;

        servername = StringArgumentType.getString(server,"server");

        if (servername==null){
            if (player!=null)MessageTool.Say(player ,new TranslatableText("koroworld.ms.servernameerror").setStyle(Style.EMPTY.withColor(Formatting.RED)));
            else log.error("Server Name Is NULL!");
            return 1;
        }

        ServerConnectTool.SetPlayerNextServer(target.getUuidAsString(),servername);

        ServerPlayNetworking.send(target,new Identifier("koroworld:reconnect"), PacketByteBufs.empty());

        return 1;
    }


}
