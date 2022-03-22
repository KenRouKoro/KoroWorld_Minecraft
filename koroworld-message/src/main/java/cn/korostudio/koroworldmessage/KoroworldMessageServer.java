package cn.korostudio.koroworldmessage;

import cn.hutool.core.codec.Base62;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.setting.Setting;
import cn.korostudio.koroworldmessage.command.KoroMessageCommand;
import cn.korostudio.koroworldmessage.data.MessageSystemData;
import cn.korostudio.koroworldcore.KoroworldCore;
import cn.korostudio.koroworldcore.event.interfaces.PlayerChatEvent;
import cn.korostudio.koroworldmessage.ws.ServerWSClient;
import lombok.Getter;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;

import java.net.URI;

public class KoroworldMessageServer implements DedicatedServerModInitializer {
    static {
        setting = new Setting(FileUtil.touch(System.getProperty("user.dir")+"/koroworld/config/message.setting"), CharsetUtil.CHARSET_UTF_8,true);
        loadSetting();
    }
    @Getter
    protected static ServerWSClient serverWSClient =null;

    protected static Setting setting ;
    @Override
    public void onInitializeServer() {
        KoroworldMessage.getLogger().info("Koroworld Message System is Loading.");
        register();
        connectWSServer();
    }

    protected void register(){
        CommandRegistrationCallback.EVENT.register(KoroMessageCommand::registerCommand);
        PlayerChatEvent.EVENT.register(cn.korostudio.koroworldmessage.event.PlayerChatEvent::onChat);
    }

    public static void loadSetting(){
        MessageSystemData.MessageGroup = setting.getStr("MessageGroup","KoroWorld");
        MessageSystemData.WSServer = setting.getStr("WSServer","ws://127.0.0.1:18620");
    }

    public static void connectWSServer(){
        try {
            String wsURL = MessageSystemData.WSServer+"/message/ws"+"?token="+ Base62.encode(KoroworldCore.getServerName());
            URI uri = new URI(wsURL);
            serverWSClient = new ServerWSClient(uri);
            serverWSClient.connect();
        } catch (Exception exception) {
            KoroworldMessage.getLogger().error("WS Connect FAIL!");
        }
    }
}
