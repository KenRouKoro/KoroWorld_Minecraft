package cn.korostudio.koroworldcore.mixin;

import cn.korostudio.koroworldcore.event.interfaces.PlayerDeathEvent;
import cn.korostudio.koroworldcore.util.MessageTool;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public class PlayerDeathMixin {

    @Inject(at=@At("HEAD"), method = "onDeath")
    private void onDeath(final DamageSource source, CallbackInfo callbackInfo){
        PlayerDeathEvent.EVENT.invoker().interact((ServerPlayerEntity)(Object)this);
        MessageTool.sendSystemMessage(((ServerPlayerEntity)(Object)this), "建议使用 /back 指令返回死亡地点。（如果死岩浆、方块、虚空里就算了XD）");
    }
}