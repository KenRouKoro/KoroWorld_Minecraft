package cn.korostudio.koroworld.core.mixin;

import cn.korostudio.koroworld.core.event.interfaces.PlayerDeathEvent;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public class PlayerDeathMixin {

    @Inject(at = @At("HEAD"), method = "onDeath")
    private void onDeath(final DamageSource source, CallbackInfo callbackInfo) {
        PlayerDeathEvent.EVENT.invoker().interact((ServerPlayerEntity) (Object) this);
    }
}