package cn.korostudio.koroworld.itemsyn.command;

import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.json.JSONUtil;
import cn.korostudio.koroworld.connect.api.DataAPI;
import cn.korostudio.koroworld.core.data.Data;
import cn.korostudio.koroworld.core.util.MessageTool;
import cn.korostudio.koroworld.itemsyn.tool.ItemTool;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.TranslatableText;

import java.util.List;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;
@Slf4j
public class Command {
    public static void register(CommandDispatcher<ServerCommandSource> server, boolean b) {
        if(Data.commandEn.getBool("itemsyn",true)){
            server.register(
                    literal("itemsyn").then(
                            literal("updateAll").requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))
                                    .executes(Command::updateAllPlayer)
                    ).then(
                            literal("update").requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(
                                        Data.KoroWorldConfig.getInt("PlayerUpdateLevel","itemsyn",0)
                                    ))
                                    .executes(Command::update)
                                    .then(
                                            argument("player", EntityArgumentType.player())
                                                    .requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))
                                                    .executes(Command::updatePlayer)
                                    )
                    ).then(
                            literal("downloadAll").requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))
                                    .executes(Command::downloadAll)
                    ).then(
                            literal("download").requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))
                                    .then(argument("player",EntityArgumentType.player()))
                                    .executes(Command::download)
                    )
            );
        }
    }

    public static int updateAllPlayer(CommandContext<ServerCommandSource> server){
        List<ServerPlayerEntity> players = Data.server.getPlayerManager().getPlayerList();
        for(ServerPlayerEntity player:players){
            updatePlayer(player);
        }
        return 1;
    }
    public static int update(CommandContext<ServerCommandSource> server){
        ServerPlayerEntity player = null ;
        try{
            player = server.getSource().getPlayer();
        } catch (CommandSyntaxException ignored) {
            log.error("This is a player instruction!");
            return 1;
        }
        updatePlayer(player);
        return 1;
    }

    public static int downloadAll(CommandContext<ServerCommandSource> server){
        List<ServerPlayerEntity> players = Data.server.getPlayerManager().getPlayerList();
        for(ServerPlayerEntity player:players){
            downloadPlayer(player);
        }
        return 1;
    }
    public static int download(CommandContext<ServerCommandSource> server) throws CommandSyntaxException {
        ServerPlayerEntity player = EntityArgumentType.getPlayer(server,"player");
        downloadPlayer(player);
        return 1;
    }

    public static int updatePlayer(CommandContext<ServerCommandSource> server) throws CommandSyntaxException {
        ServerPlayerEntity player = EntityArgumentType.getPlayer(server,"player");
        updatePlayer(player);
        return 1;
    }

    protected static void downloadPlayer(ServerPlayerEntity player){
        String UUID = player.getUuidAsString();
        ThreadUtil.execute(()->{
            ThreadUtil.sleep(Data.KoroWorldConfig.getLong("SynDelay","itemsyn",1000L));
            String Key = "PlayerItemData-"+ Data.KoroWorldConfig.getStr("GroupName","itemsyn","koroworld")+"-"+UUID;
            String data = DataAPI.getData(Key,true);
            if(data==null){
                MessageTool.Say(player,new TranslatableText("koroworld.itemsyn.downloadfail"));
                return;
            }
            ItemTool.setPlayerItem(player,data);
            MessageTool.Say(player,new TranslatableText("koroworld.itemsyn.downloadsuccess"));
        });

    }

    protected static void updatePlayer(ServerPlayerEntity player){
        String UUID = player.getUuidAsString();
        ThreadUtil.execute(()->{
            String Key = "PlayerItemData-"+ Data.KoroWorldConfig.getStr("GroupName","itemsyn","koroworld")+"-"+UUID;

            String data = ItemTool.getPlayerItem(player);
            boolean status = JSONUtil.parseObj(DataAPI.saveData(Key,data,true)).getBool("status",false);
            if(status){
                MessageTool.Say(player,new TranslatableText("koroworld.itemsyn.updatesuccess"));

            }else {
                MessageTool.Say(player,new TranslatableText("koroworld.itemsyn.updatefail"));
            }

        });
    }
}
