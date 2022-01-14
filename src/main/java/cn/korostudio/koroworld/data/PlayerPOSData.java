package cn.korostudio.koroworld.data;

import lombok.Data;
import lombok.Getter;
import net.minecraft.server.world.ServerWorld;

import java.util.HashMap;

@Getter
public class PlayerPOSData {
    static public HashMap<String,PlayerPOSData> deathMap = new HashMap<>();
    protected double x,y,z;
    protected String UUID;
    protected ServerWorld world;
    public PlayerPOSData(double x,double y,double z,String UUID,ServerWorld world){
        this.x=x;
        this.y=y;
        this.z=z;
        this.UUID=UUID;
        this.world= world;
    }
}
