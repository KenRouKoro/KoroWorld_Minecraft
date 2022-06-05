package cn.korostudio.koroworld.core;

import cn.korostudio.koroworld.core.data.Data;
import cn.korostudio.koroworld.core.event.DeathEvent;
import cn.korostudio.koroworld.core.event.interfaces.PlayerDeathEvent;
import lombok.Getter;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KoroWorldCore implements ModInitializer {
	//为true时拦截原版聊天系统
	public static boolean cancelChat = false;

	@Getter
	protected static String serverName;

	@Getter
	protected static String connect;

	@Getter
	protected static String systemName;


	public static final Logger LOGGER = LoggerFactory.getLogger("KoroWorld-Core");

	@Override
	public void onInitialize() {
		LOGGER.info("KoroWorld-Core Is Loaded!");
		ServerLifecycleEvents.SERVER_STARTED.register(minecraftServer -> {
			Data.server = minecraftServer;
		});
		register();
	}

	protected static void register(){
		PlayerDeathEvent.EVENT.register(DeathEvent::onDeath);
	}
}
