package cn.korostudio.koroworld.multiserver;

import cn.hutool.http.HttpGlobalConfig;
import lombok.extern.slf4j.Slf4j;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;
@Slf4j
public class KoroWorldMultiServer implements ModInitializer {
    @Override
    public void onInitialize() {
        HttpGlobalConfig.setMaxRedirectCount(4);
        log.info("KoroWorld-MultiServer Is Loaded!");
    }
}
