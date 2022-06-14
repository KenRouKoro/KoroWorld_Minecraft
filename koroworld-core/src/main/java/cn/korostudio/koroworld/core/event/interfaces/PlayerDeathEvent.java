package cn.korostudio.koroworld.core.event.interfaces;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;

public interface PlayerDeathEvent {
    Event<PlayerDeathEvent> EVENT = EventFactory.createArrayBacked(PlayerDeathEvent.class,
            (listeners) -> (player) -> {
                for (PlayerDeathEvent listener : listeners) {
                    ActionResult result = listener.interact(player);
                    if (result != ActionResult.PASS) {
                        return result;
                    }
                }
                return ActionResult.PASS;
            });

    ActionResult interact(ServerPlayerEntity player);
}