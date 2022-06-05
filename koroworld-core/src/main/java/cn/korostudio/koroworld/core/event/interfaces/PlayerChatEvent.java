package cn.korostudio.koroworld.core.event.interfaces;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;

public interface PlayerChatEvent {
    Event<PlayerChatEvent> EVENT = EventFactory.createArrayBacked(PlayerChatEvent.class,
            (listeners) -> (player,text) -> {
                for (PlayerChatEvent listener : listeners) {
                    ActionResult result = listener.interact(player,text);
                    if(result != ActionResult.PASS) {
                        return result;
                    }
                }
                return ActionResult.PASS;
            });

    ActionResult interact(ServerPlayerEntity player,String text);
}
