package cn.korostudio.koroworlditem.uitl;

import cn.hutool.http.HttpException;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.korostudio.koroworldcore.KoroworldCore;
import cn.korostudio.koroworldcore.util.MessageTool;
import cn.korostudio.koroworlditem.data.ItemSystemData;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.collection.DefaultedList;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ItemServiceTool {
    public static void upToServer(PlayerEntity player, Logger logger) {
        String lock;
        try{
            HashMap<String,Object> keyPostMap = new HashMap<>();
            keyPostMap.put("key","set");
            keyPostMap.put("UUID",player.getUuidAsString());
            keyPostMap.put("value","true");
            keyPostMap.put("group",ItemSystemData.itemGroup);
            lock = HttpUtil.post(ItemSystemData.httpServer + "/item/lock", keyPostMap);
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
        paramMap.put("group",ItemSystemData.itemGroup);
        String result = null;
        try {
            result = HttpUtil.post(ItemSystemData.httpServer + "/item/upload", paramMap);

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
                keyPostMap.put("group",ItemSystemData.itemGroup);
                lock = HttpUtil.post(ItemSystemData.httpServer + "/item/lock", keyPostMap);
            }catch (HttpException ignored){
            }finally {
                logger.info("Unlock Data Finish.");
            }
        }
    }
    public static void downloadForServer(ServerPlayerEntity player, Logger logger){
        MessageTool.sendSystemMessage(player, "正在同步您的物品......");
        PlayerInventory inventory = player.getInventory();
        String lock=null;
        boolean unlock = false;
        int index=0;
        while (!unlock) {
            index++;
            if(index>30){
                logger.info("30 attempts have been made. It is judged that the data is corrupt. Skip the lock check.");
                return;
            }
            try {
                HashMap<String, Object> keyPostMap = new HashMap<>();
                keyPostMap.put("key", "get");
                keyPostMap.put("UUID", player.getUuidAsString());
                keyPostMap.put("group",ItemSystemData.itemGroup);
                lock = HttpUtil.post(ItemSystemData.httpServer + "/item/lock", keyPostMap);
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
        paramMap.put("group",ItemSystemData.itemGroup);
        String result = null;
        try {
            result = HttpUtil.post(ItemSystemData.httpServer + "/item/download", paramMap);
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
        MessageTool.sendSystemMessage(player, "同步完毕~");
    }
}
