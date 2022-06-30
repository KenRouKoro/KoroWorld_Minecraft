package cn.korostudio.koroworld.core.mixin.authlib;

import cn.korostudio.koroworld.core.data.Data;
import com.mojang.authlib.Environment;
import com.mojang.authlib.EnvironmentParser;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(EnvironmentParser.class)
public class EnvironmentParserMixin {
    @Inject(method = "fromHostNames",at = @At("HEAD"),cancellable = true,remap = false)
    private static void fromHostNamesRe(CallbackInfoReturnable<Optional<Environment>> cir){
        final String authHost = Data.KoroWorldConfig.getStr("authHost","core","https://authserver.mojang.com");
        final String accountsHost = Data.KoroWorldConfig.getStr("accountsHost","core","https://api.mojang.com");
        final String sessionHost = Data.KoroWorldConfig.getStr("sessionHost","core","https://sessionserver.mojang.com");
        final String servicesHost = Data.KoroWorldConfig.getStr("servicesHost","core","https://api.minecraftservices.com");
        cir.setReturnValue(Optional.of(Environment.create(authHost, accountsHost, sessionHost, servicesHost, "properties")));
        cir .cancel();
    }
}
