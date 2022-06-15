package cn.korostudio.koroworld.multiserver.mixin;

import cn.hutool.json.JSONObject;
import cn.korostudio.koroworld.multiserver.connectTool.ClientConnectTool;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.network.ServerInfo;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(MultiplayerScreen.class)
public class MultiplayerScreenMixin {
    @Redirect(method = "connect(Lnet/minecraft/client/network/ServerInfo;)V", at = @At(value = "FIELD", target = "Lnet/minecraft/client/network/ServerInfo;address:Ljava/lang/String;", opcode = Opcodes.GETFIELD))
    private String remapAddress(ServerInfo instance) {
        JSONObject jsonObject = ClientConnectTool.getTrueServerAddress(instance.address);
        if(!jsonObject.getBool("status",false)){
            return instance.address;
        }
        return jsonObject.getStr("address");
    }
}
