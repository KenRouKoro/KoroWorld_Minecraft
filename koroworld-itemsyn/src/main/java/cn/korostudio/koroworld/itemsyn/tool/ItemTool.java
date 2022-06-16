package cn.korostudio.koroworld.itemsyn.tool;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.collection.DefaultedList;

import java.util.ArrayList;
import java.util.List;

public class ItemTool {
    public static String getPlayerItem(ServerPlayerEntity player){
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

        JSONArray mainJSON = JSONUtil.parseArray(mainArraylist);
        JSONArray armorJSON = JSONUtil.parseArray(armorArraylist);
        JSONArray offHandJSON = JSONUtil.parseArray(offHandArraylist);

        JSONObject dataJSON = new JSONObject();

        dataJSON.putOnce("mainJSON",mainJSON);
        dataJSON.putOnce("armorJSON",armorJSON);
        dataJSON.putOnce("offHandJSON",offHandJSON);

        return dataJSON.toString();
    }
    public static boolean setPlayerItem(ServerPlayerEntity player,String itemSNBT){

        JSONObject PlayerItemDataJSON = JSONUtil.parseObj(itemSNBT);

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

        return true;
    }
}
