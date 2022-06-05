package cn.korostudio.koroworld.core.data;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.setting.Setting;
import net.minecraft.server.MinecraftServer;

public class Data {
    public static MinecraftServer server;
    public final static Setting commandEn = new Setting(FileUtil.touch(System.getProperty("user.dir")+"/koroworld/config/commandEn.setting"), CharsetUtil.CHARSET_UTF_8,true);
    public final static Setting KoroWorldConfig = new Setting(FileUtil.touch(System.getProperty("user.dir")+"/koroworld/config/config.setting"), CharsetUtil.CHARSET_UTF_8,true);
}
