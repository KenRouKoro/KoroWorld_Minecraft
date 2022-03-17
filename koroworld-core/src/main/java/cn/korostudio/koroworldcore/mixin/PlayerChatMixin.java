package cn.korostudio.koroworldcore.mixin;

import cn.korostudio.koroworldcore.event.interfaces.PlayerChatEvent;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import org.apache.commons.lang3.StringUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayNetworkHandler.class)
public abstract class PlayerChatMixin {

    @Shadow
    public ServerPlayerEntity player;

    @Shadow public abstract void syncWithPlayerPosition();

    @Inject(method = "onChatMessage",at = @At(value = "INVOKE",target = "Lnet/minecraft/server/network/ServerPlayNetworkHandler;filterText(Ljava/lang/String;Ljava/util/function/Consumer;)V"), cancellable = true)
    public void message(ChatMessageC2SPacket packet, CallbackInfo ci){
        String string = StringUtils.normalizeSpace(packet.getChatMessage());
        PlayerChatEvent.EVENT.invoker().interact(player,string);
        ci.cancel();
    }


}
