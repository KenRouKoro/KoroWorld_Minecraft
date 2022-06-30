package cn.korostudio.koroworld.core.mixin.authlib;

import com.mojang.authlib.yggdrasil.YggdrasilMinecraftSessionService;
import javafx.fxml.FXML;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = YggdrasilMinecraftSessionService.class ,remap = false)
public class YggdrasilMinecraftSessionServiceMixin {
    @Final
    @Shadow
    @Mutable
    private static String[] ALLOWED_DOMAINS = {
        ".net",".cn",".com",".xyz",".org",".su",".moe",".jp",".eu",".uk","hk",".tw"
    };
    @Final
    @Shadow
    @Mutable
    private static final String[] BLOCKED_DOMAINS = {
    };


}
