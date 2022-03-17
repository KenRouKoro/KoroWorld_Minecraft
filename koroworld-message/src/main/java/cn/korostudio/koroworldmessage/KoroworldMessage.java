package cn.korostudio.koroworldmessage;

import lombok.Getter;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KoroworldMessage implements ModInitializer {

    @Getter
    protected static Logger logger = LoggerFactory.getLogger(KoroworldMessage.class);
    @Override
    public void onInitialize() {
    }
}
