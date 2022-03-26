package cn.korostudio.koroworldeco;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.setting.Setting;
import lombok.Getter;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.api.ModInitializer;

public class KoroworldEco implements DedicatedServerModInitializer {
    @Getter
    protected static Setting setting = new Setting(FileUtil.touch(System.getProperty("user.dir")+"/koroworld/config/eco.setting"), CharsetUtil.CHARSET_UTF_8,true);;

    @Override    public void onInitializeServer() {

    }

    public static void loadSetting(){

    }
}
