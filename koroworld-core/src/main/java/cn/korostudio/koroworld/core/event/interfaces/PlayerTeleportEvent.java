package cn.korostudio.koroworld.core.event.interfaces;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;

public interface PlayerTeleportEvent {
    Event<PlayerTeleportEvent> EVENT = EventFactory.createArrayBacked(PlayerTeleportEvent.class,
            (listeners) -> (targetWorld,  x,  y,  z,  yaw,  pitch) -> {
                for (PlayerTeleportEvent listener : listeners) {
                    ActionResult result = listener.interact(targetWorld,  x,  y,  z,  yaw,  pitch);
                    if(result != ActionResult.PASS) {
                        return result;
                    }
                }
                return ActionResult.PASS;
            });

    ActionResult interact(ServerWorld targetWorld, double x, double y, double z, float yaw, float pitch);
}
