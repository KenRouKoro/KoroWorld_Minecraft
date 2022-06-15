package cn.korostudio.koroworld.multiserver;

import cn.hutool.http.HttpGlobalConfig;
import cn.hutool.json.JSONObject;
import cn.korostudio.koroworld.multiserver.connectTool.ClientConnectTool;
import lombok.extern.slf4j.Slf4j;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.Style;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

@Slf4j
public class KoroWorldMultiServerClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        register();
    }

    protected static void register(){
        ClientPlayNetworking.registerGlobalReceiver(new Identifier("koroworld:reconnect"), (client, handler, buf, responseSender) -> {

            ClientPlayerEntity player = client.player;

            if (player==null){
                log.error("为什么没连接服务器会调用这个监听器？？？？？这不应该。");
                return;
            }

            if(ClientConnectTool.nowAddress==null){

                TranslatableText errText = new TranslatableText("koroworld.ms.noserverconnection");
                errText.setStyle(Style.EMPTY.withColor(Formatting.RED));
                player.sendSystemMessage( errText , Util.NIL_UUID);

                return;
            }

            JSONObject serverInfo = ClientConnectTool.getNextServerAddress(ClientConnectTool.nowAddress);

            if(!serverInfo.getBool("status",false)){

                TranslatableText errText = new TranslatableText("koroworld.ms.msservererror");
                errText.setStyle(Style.EMPTY.withColor(Formatting.RED));
                player.sendSystemMessage( errText , Util.NIL_UUID);

                return;
            }

            String servername = serverInfo.getStr("name");
            String serverAddress = serverInfo.getStr("address");
            client.execute(()->{
                ClientConnectTool.redirect(servername,serverAddress);
            });
        });
    }
}
