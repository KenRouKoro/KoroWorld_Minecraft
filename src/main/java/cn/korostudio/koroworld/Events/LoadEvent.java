package cn.korostudio.koroworld.Events;

import cn.hutool.http.HttpException;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.korostudio.koroworld.Koroworld;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.collection.DefaultedList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class LoadEvent {
    static protected Logger logger = LoggerFactory.getLogger("KoroWorld-ITSY-LoadEvent");

    static public void onSpawn(ServerPlayNetworkHandler handler, PacketSender sender, MinecraftServer server) {
        new Thread(() -> {

            PlayerEntity player = handler.player;
            Koroworld.sendMessageServer(handler.getPlayer(), "正在同步您的物品......");
            PlayerInventory inventory = player.getInventory();
            String lock=null;
            boolean unlock = false;
            int index=0;
            while (!unlock) {
                index++;
                if(index>30){
                    logger.info("30 attempts have been made. It is judged that the data is corrupt. Skip the lock check.");
                    break;
                }
                try {
                    HashMap<String, Object> keyPostMap = new HashMap<>();
                    keyPostMap.put("key", "get");
                    keyPostMap.put("UUID", player.getUuidAsString());
                    lock = HttpUtil.post(Koroworld.setting.getStr("server", "http://127.0.0.1:18620") + "/item/lock", keyPostMap);
                } catch (HttpException e) {
                    lock = "error";
                }
                if(lock.equals("null")){
                    logger.info("Player :"+player.getName().getString()+" not have Server Data,Skip the Data check.");
                    return;
                }
                else if(lock .equals("error")){
                    return;
                }else if(lock.equals("true")){
                    logger.info("Player :" + player.getName().getString() + " Data is Lock,Try again in 0.2 seconds.");
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException ignored) {}
                }else if(lock.equals("false")){
                    unlock = true;
                }else {
                    return;
                }

            }

            logger.info("Player:" + player.getName().getString() + " UUID is :" + player.getUuidAsString() + " Data is downloading from ServerCore.");

            HashMap<String, Object> paramMap = new HashMap<>();
            paramMap.put("UUID", player.getUuidAsString());
            String result = null;
            try {
                result = HttpUtil.post(Koroworld.setting.getStr("server","http://127.0.0.1:18620") + "/item/download", paramMap);
            } catch (HttpException e) {
                e.printStackTrace();
                return;
            }
            if (result.equals("non")) {
                return;
            }
            JSONObject PlayerItemDataJSON = JSONUtil.parseObj(result);

            JSONArray mainJSON = PlayerItemDataJSON.getJSONArray("mainJSON");
            JSONArray armorJSON = PlayerItemDataJSON.getJSONArray("armorJSON");
            JSONArray offHandJSON = PlayerItemDataJSON.getJSONArray("offHandJSON");

            List<String> mainArraylist = JSONUtil.toList(mainJSON, String.class);
            List<String> armorArraylist = JSONUtil.toList(armorJSON, String.class);
            List<String> offHandArraylist = JSONUtil.toList(offHandJSON, String.class);

            DefaultedList<ItemStack> main = player.getInventory().main;
            DefaultedList<ItemStack> armor = player.getInventory().armor;
            DefaultedList<ItemStack> offHand = player.getInventory().offHand;

            for (int i = 0; i < mainArraylist.size(); i++) {
                try {
                    main.set(i, ItemStack.fromNbt(StringNbtReader.parse(mainArraylist.get(i))));
                } catch (CommandSyntaxException e) {
                    e.printStackTrace();
                }
            }
            for (int i = 0; i < armorArraylist.size(); i++) {
                try {
                    armor.set(i, ItemStack.fromNbt(StringNbtReader.parse(armorArraylist.get(i))));
                } catch (CommandSyntaxException e) {
                    e.printStackTrace();
                }
            }
            for (int i = 0; i < offHandArraylist.size(); i++) {
                try {
                    offHand.set(i, ItemStack.fromNbt(StringNbtReader.parse(offHandArraylist.get(i))));
                } catch (CommandSyntaxException e) {
                    e.printStackTrace();
                }
            }
            logger.info("Player:" + player.getName().getString() + " UUID is :" + player.getUuidAsString() + " Data download from ServerCore Finish.");
            Koroworld.sendMessageServer(handler.getPlayer(), "同步完毕~");
        }).start();
    }
}
