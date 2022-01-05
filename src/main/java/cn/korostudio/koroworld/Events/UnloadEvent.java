package cn.korostudio.koroworld.Events;

import cn.hutool.http.HttpException;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.korostudio.koroworld.Koroworld;
import cn.korostudio.koroworld.command.KoroCommand;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.collection.DefaultedList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;

public class UnloadEvent {
    static protected Logger logger = LoggerFactory.getLogger("KoroWorld-ITSY-UnloadEvent");

    static public void onSpawn(ServerPlayNetworkHandler handler, MinecraftServer server) {
        new Thread(() -> {
            PlayerEntity player = handler.player;
            KoroCommand.upToServer(player, logger);
        }).start();


    }
}
