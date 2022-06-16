package cn.korostudio.koroworld.itemsyn;

import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.korostudio.koroworld.connect.api.DataAPI;
import cn.korostudio.koroworld.core.data.Data;
import cn.korostudio.koroworld.core.util.MessageTool;
import cn.korostudio.koroworld.itemsyn.command.Command;
import cn.korostudio.koroworld.itemsyn.tool.ItemTool;
import lombok.extern.slf4j.Slf4j;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.TranslatableText;
import org.checkerframework.checker.units.qual.K;

@Slf4j
public class ItemSyn implements DedicatedServerModInitializer {
    @Override
    public void onInitializeServer() {
        register();
        log.info("KoroWorld ItemSyn Loaded!");
    }
    protected static void register(){
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            ServerPlayerEntity player = handler.getPlayer();
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

        });
        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
            ServerPlayerEntity player = handler.getPlayer();
            String UUID = player.getUuidAsString();
            ThreadUtil.execute(()->{
                String Key = "PlayerItemData-"+ Data.KoroWorldConfig.getStr("GroupName","itemsyn","koroworld")+"-"+UUID;

                String data = ItemTool.getPlayerItem(player);
                boolean status = JSONUtil.parseObj(DataAPI.saveData(Key,data,true)).getBool("status",false);
                if(!status){
                    MessageTool.Say(player,new TranslatableText("koroworld.itemsyn.downloadfail"));
                    return;
                }
                MessageTool.Say(player,new TranslatableText("koroworld.itemsyn.downloadsuccess"));
            });
        });
        CommandRegistrationCallback.EVENT.register(Command::register);
    }
}
