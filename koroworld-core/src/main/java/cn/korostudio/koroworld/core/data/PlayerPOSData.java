package cn.korostudio.koroworld.core.data;

import lombok.Data;
import net.minecraft.server.world.ServerWorld;

import java.util.concurrent.ConcurrentHashMap;

@Data
public class PlayerPOSData {
    static public ConcurrentHashMap<String, PlayerPOSData> teleportMap = new ConcurrentHashMap<>();
    protected double x;
    protected double y;
    protected double z;
    protected float yaw;
    protected float pitch;
    protected String playerUUID;
    protected ServerWorld serverWorld;

    public PlayerPOSData(String playerUUID, ServerWorld targetWorld, double x, double y, double z, float yaw, float pitch) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.playerUUID = playerUUID;
        this.serverWorld = targetWorld;
        this.pitch = pitch;
        this.yaw = yaw;
    }
}
