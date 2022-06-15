package cn.korostudio.koroworld.multiserver.event;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.util.ActionResult;

@Environment(EnvType.CLIENT)
public interface ClientRedirectEvent {
    Event<ClientRedirectEvent> EVENT = EventFactory.createArrayBacked(ClientRedirectEvent.class, listeners -> address -> {
        for (ClientRedirectEvent listener : listeners) {
            ActionResult result = listener.redirect(address);

            if (result != ActionResult.PASS) {
                return result;
            }
        }

        return ActionResult.SUCCESS;
    });

    ActionResult redirect(String address);
}
