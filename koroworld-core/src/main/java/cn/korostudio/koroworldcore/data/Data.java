package cn.korostudio.koroworldcore.data;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.setting.Setting;
import net.minecraft.server.MinecraftServer;

import java.util.concurrent.ConcurrentHashMap;

public class Data {
    public static MinecraftServer server;
    public static String serverConnectBanner;
    public static String serverConnectBannerForPlayer;
    public static String serverDisConnectBanner;
    public static boolean saveTeleport = true;
    public static Setting commandEn = new Setting(FileUtil.touch(System.getProperty("user.dir")+"/koroworld/config/commandEn.setting"), CharsetUtil.CHARSET_UTF_8,true);;
}
