package cn.korostudio.koroworld.core.event;

import cn.korostudio.koroworld.core.data.PlayerPOSData;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;

public class DeathEvent {

    public static boolean saveDeath = true;

    public static ActionResult onDeath(ServerPlayerEntity player){

        if (saveDeath) {
            PlayerPOSData.teleportMap.put(player.getUuidAsString(), new PlayerPOSData(player.getUuidAsString(), player.getWorld(), player.getX(), player.getY(), player.getZ(), player.getYaw(), player.getPitch()));
        }
        return ActionResult.PASS;
    }

}
