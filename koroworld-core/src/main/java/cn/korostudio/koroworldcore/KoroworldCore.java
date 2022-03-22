package cn.korostudio.koroworldcore;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.file.FileReader;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.hutool.setting.Setting;
import cn.korostudio.koroworldcore.command.KoroCommand;
import cn.korostudio.koroworldcore.data.Data;
import cn.korostudio.koroworldcore.event.ChatEvent;
import cn.korostudio.koroworldcore.event.DeathEvent;
import cn.korostudio.koroworldcore.event.PlayerConnectEvent;
import cn.korostudio.koroworldcore.event.PlayerDisconnectEvent;
import cn.korostudio.koroworldcore.event.interfaces.PlayerChatEvent;
import cn.korostudio.koroworldcore.event.interfaces.PlayerDeathEvent;
import cn.korostudio.koroworldcore.util.MessageTool;
import lombok.Getter;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class KoroworldCore implements ModInitializer {

    static{
        setting = new Setting(FileUtil.touch(System.getProperty("user.dir")+"/koroworld/config/core.setting"), CharsetUtil.CHARSET_UTF_8,true);
        loadSetting();
    }

    @Getter
    protected static Setting setting;

    protected static Logger logger = LoggerFactory.getLogger("KoroWorld");

    @Getter
    protected static String serverName;

    @Getter
    protected static String connect;

    @Getter
    protected static String systemName;


    @Override
    public void onInitialize() {
        ServerLifecycleEvents.SERVER_STARTED.register(minecraftServer -> {
            Data.server = minecraftServer;
        });
        showLogo();
        register();
        MessageTool.initTemplateValueMap();
    }

    public static void loadSetting(){
        setting = new Setting(FileUtil.touch(System.getProperty("user.dir")+"/koroworld/config/core.setting"), CharsetUtil.CHARSET_UTF_8,true);
        MessageTool.setChatTemplate(setting.getStr("ChatTemplate","[{time}][{server}][{player}] {text}"));
        MessageTool.setOpChatTemplate(setting.getStr("OPChatTemplate","[{time}][管理员][{server}][{player}] {text}"));
        MessageTool.setTimeTemplate(setting.getStr("TimeTemplate","yyyy-MM-dd HH:mm:ss"));
        MessageTool.setDateTemplate(setting.getStr("DateTemplate","yyyy年MM月dd日"));
        MessageTool.setSystemTemplate(setting.getStr("SystemTemplate","[{system}] {text}"));
        systemName = setting.getStr("SystemName","KoroWorld");
        serverName = setting.getStr("ServerName","KoroWorld");

        Data.serverConnectBanner = setting.getStr("ConnectBanner","{player}来了喵~");
        Data.serverDisConnectBanner = setting.getStr("DisconnectBanner","{player}跑掉喵~");
        Data.serverConnectBannerForPlayer = setting.getStr("ConnectBannerForPlayer","欢迎来到{server}服务器~");

        Data.saveTeleport = setting.getBool("SaveTeleportPOS",true);
        DeathEvent.saveDeath = setting.getBool("SaveDeathPOS",true);
    }

    protected void register(){
        CommandRegistrationCallback.EVENT.register(KoroCommand::registerCommand);
        PlayerDeathEvent.EVENT.register(DeathEvent::onDeath);
        PlayerChatEvent.EVENT.register(ChatEvent::onChat);
        ServerPlayConnectionEvents.JOIN.register(PlayerConnectEvent::onConnect);
        ServerPlayConnectionEvents.DISCONNECT.register(PlayerDisconnectEvent::onDisconnect);
    }

    public void showLogo(){
        logger.info("""
                 
                 power by:
                 _   __                  _____ _             _ _
                | | / /                 /  ___| |           | (_)
                | |/ /  ___  _ __ ___   \\ `--.| |_ _   _  __| |_  ___
                |    \\ / _ \\| '__/ _ \\   `--. \\ __| | | |/ _` | |/ _ \\
                | |\\  \\ (_) | | | (_) | /\\__/ / |_| |_| | (_| | | (_) |
                \\_| \\_/\\___/|_|  \\___/  \\____/ \\__|\\__,_|\\__,_|_|\\___/
                """);
    }
}
