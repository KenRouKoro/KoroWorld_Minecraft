package cn.korostudio.koroworld.koroworldconnectkubejs;

import lombok.extern.slf4j.Slf4j;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.api.ModInitializer;

@Slf4j
public class KoroworldConnectKubejs implements DedicatedServerModInitializer {

    @Override
    public void onInitializeServer() {
        log.info("KoroWorld-Connect-Kubejs is Loaded!");
    }

}
