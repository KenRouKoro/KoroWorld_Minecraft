package cn.korostudio.koroworld.multiserver.mixin;

import cn.hutool.json.JSONObject;
import cn.korostudio.koroworld.multiserver.connectTool.ClientConnectTool;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerServerListWidget;
import net.minecraft.client.network.ServerInfo;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
@Mixin(MultiplayerServerListWidget.ServerEntry.class)
public class ServerEntryMixin {
    @Redirect(method = "<init>", at = @At(value = "FIELD", target = "Lnet/minecraft/client/network/ServerInfo;address:Ljava/lang/String;", opcode = Opcodes.GETFIELD))
    private String remapAddress(ServerInfo instance) {
        JSONObject jsonObject = ClientConnectTool.getDefAddress(instance.address);
        if(!jsonObject.getBool("status",false)){
            return instance.address;
        }
        return jsonObject.getStr("address");
    }
}
