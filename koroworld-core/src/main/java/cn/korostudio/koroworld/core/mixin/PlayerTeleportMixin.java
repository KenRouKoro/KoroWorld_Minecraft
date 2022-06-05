package cn.korostudio.koroworld.core.mixin;

import cn.korostudio.koroworld.core.event.interfaces.PlayerTeleportEvent;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public abstract class PlayerTeleportMixin {
    @Inject(method = "teleport",at = @At(value = "INVOKE",target = "Lnet/minecraft/server/network/ServerPlayerEntity;setCameraEntity(Lnet/minecraft/entity/Entity;)V"))
    public void onTeleport(ServerWorld targetWorld, double x, double y, double z, float yaw, float pitch, CallbackInfo ci){
        PlayerTeleportEvent.EVENT.invoker().interact(targetWorld, x, y, z, yaw, pitch);
        /*
        if(Data.saveTeleport){
            Vec3d vec3d =((ServerPlayerEntity)(Object)this).getPos();
            System.out.println(vec3d.getX()+" "+ vec3d.getY()+" "+ vec3d.getZ());
            PlayerPOSData.teleportMap.put(((ServerPlayerEntity)(Object)this).getUuidAsString(), new PlayerPOSData(vec3d.getX(), vec3d.getY(), vec3d.getZ(), ((ServerPlayerEntity)(Object)this).getUuidAsString(),  ((ServerPlayerEntity)(Object)this).getWorld()));
            //MessageTool.sendSystemMessage(((ServerPlayerEntity)(Object)this), "建议使用 /back 指令返回死亡地点。（如果死岩浆、方块、虚空里就算了XD）");
        }
        */
    }
}
