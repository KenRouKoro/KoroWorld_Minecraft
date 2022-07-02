package cn.korostudio.koroworld.core;

import cn.hutool.cron.CronUtil;
import cn.korostudio.koroworld.core.data.Data;
import cn.korostudio.koroworld.core.event.DeathEvent;
import cn.korostudio.koroworld.core.event.TeleportEvent;
import cn.korostudio.koroworld.core.event.interfaces.PlayerDeathEvent;
import cn.korostudio.koroworld.core.event.interfaces.PlayerTeleportEvent;
import cn.korostudio.koroworld.core.util.MessageTool;
import lombok.Getter;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KoroWorldCore implements ModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("KoroWorld-Core");
    //为true时拦截原版聊天系统
    public static boolean cancelChat = false;
    @Getter
    protected static String serverName;
    @Getter
    protected static String systemName;

    protected static void register() {
        PlayerDeathEvent.EVENT.register(DeathEvent::onDeath);
        PlayerTeleportEvent.EVENT.register(TeleportEvent::teleportEvent);
    }

    protected static void loadSetting() {
        systemName = Data.KoroWorldConfig.getStr("systemname", "core", "小祥凤");
        serverName = Data.KoroWorldConfig.getStr("servername", "core", "KoroWorld");
        MessageTool.setTimeTemplate(Data.KoroWorldConfig.getStr("TimeTemplate","core","HH:mm:ss"));
        MessageTool.setDateTemplate(Data.KoroWorldConfig.getStr("DateTemplate","core","yyyy年MM月dd日"));
        MessageTool.setChatTemplate(Data.KoroWorldConfig.getStr("ChatTemplate","core","§6<{ServerName}>§b<§b{player}§b>§f{text}"));
        MessageTool.setSystemTemplate(Data.KoroWorldConfig.getStr("SystemTemplate","core","§6<{SystemName}>{text}"));
    }

    @Override
    public void onInitialize() {
        LOGGER.info("KoroWorld-Core Is Loaded!");
        ServerLifecycleEvents.SERVER_STARTED.register(minecraftServer -> {
            Data.server = minecraftServer;
        });
        Data.KoroWorldConfig.autoLoad(true);
        loadSetting();
        register();
        MessageTool.Init();
        CronUtil.setMatchSecond(true);
        CronUtil.start();
    }
}
