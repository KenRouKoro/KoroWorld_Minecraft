package cn.korostudio.koroworld.core.mixin.authlib;

import cn.korostudio.koroworld.core.data.Data;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.exceptions.AuthenticationUnavailableException;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.authlib.yggdrasil.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.A;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;
import java.net.URL;

@Mixin(value = YggdrasilAuthenticationService.class,remap = false)
@Slf4j
public abstract class YggdrasilAuthenticationServiceMixin{

    @Shadow
    protected abstract  <T extends Response> T makeRequest( URL url,  Object input,  Class<T> classOfT,   String authentication) throws AuthenticationException;

    @Inject(method = "makeRequest(Ljava/net/URL;Ljava/lang/Object;Ljava/lang/Class;)Lcom/mojang/authlib/yggdrasil/response/Response;",at = @At(value = "INVOKE",target = "Lcom/mojang/authlib/yggdrasil/YggdrasilAuthenticationService;makeRequest(Ljava/net/URL;Ljava/lang/Object;Ljava/lang/Class;Ljava/lang/String;)Lcom/mojang/authlib/yggdrasil/response/Response;"),cancellable = true)
    private  <T extends Response> void makeRequestEMixin(URL url, Object input, Class<T> classOfT, CallbackInfoReturnable<T> cir) throws AuthenticationException {
        try{
             cir.setReturnValue(makeRequest(url, input, classOfT, null));
             cir.cancel();
        } catch (AuthenticationException e) {
            log.warn("AuthenticationException trigger! Retrying!");
            if(Data.KoroWorldConfig.getBool("printLoginFailThrowableDebug","core",false)){
                log.error("Err Url Is :"+url.toString());
                e.printStackTrace();
            }
            try {
                cir.setReturnValue(makeRequest(url, input, classOfT, null));
                cir.cancel();
            } catch (AuthenticationException ex) {
                log.warn("Retrying FAIL QAQ ,the odds are that the MJ server is haunted.");
                throw ex;
            }
        }


    }
}
