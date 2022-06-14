package cn.korostudio.koroworld.core.event;

import cn.korostudio.koroworld.core.data.PlayerPOSData;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;

public class TeleportEvent {
    public static ActionResult teleportEvent(ServerPlayerEntity player, ServerWorld targetWorld, double x, double y, double z, float yaw, float pitch) {
        PlayerPOSData.teleportMap.put(player.getUuidAsString(), new PlayerPOSData(player.getUuidAsString(), player.getWorld(), player.getX(), player.getY(), player.getZ(), player.getYaw(), player.getPitch()));
        return ActionResult.PASS;
    }
}
