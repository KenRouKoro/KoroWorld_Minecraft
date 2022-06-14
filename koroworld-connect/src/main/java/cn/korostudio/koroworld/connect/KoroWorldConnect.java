package cn.korostudio.koroworld.connect;


import cn.hutool.core.util.StrUtil;
import cn.korostudio.koroworld.command.Command;
import cn.korostudio.koroworld.connect.ws.WSConnector;
import cn.korostudio.koroworld.core.data.Data;
import com.mojang.brigadier.CommandDispatcher;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.server.command.ServerCommandSource;

/**
 * KoroWorld Connect模块
 */
@Slf4j(topic = "KoroWorld-Connect")
public class KoroWorldConnect implements ModInitializer {

    /**
     * 连接地址
     */
    @Getter
    protected static String connect;
    /**
     * 是否加密
     */
    @Getter
    protected static boolean ssl;
    /**
     * 当前服务器标识
     */
    public static String SUID;
    /**
     * 临时数据包处理器超时时间
     */
    public static long timeout = 10000;
    /**
     * 数据包缓存超时时间
     */
    public static long cacheTimeout = 60000;

    /**
     * 注册监听器
     */
    protected static void register() {
        CommandRegistrationCallback.EVENT.register(Command::register);
    }

    /**
     * 读取配置文件
     */
    protected static void loadSetting() {
        connect = Data.KoroWorldConfig.getStr("connect", "connect", "127.0.0.1:18620");
        ssl = Data.KoroWorldConfig.getBool("useSSL", "connect", false);
        SUID = Data.KoroWorldConfig.getStr("SUID","connect", StrUtil.uuid());
        timeout = Data.KoroWorldConfig.getLong("timeout","connect",10000L);
        cacheTimeout = Data.KoroWorldConfig.getLong("cacheTimeout","connect",60000L);
    }

    /**
     * Fabric默认启动方法
     */
    @Override
    public void onInitialize() {
        log.info("KoroWorld-Connect Is Loading!");
        loadSetting();
        register();
        WSConnector.start();
    }
}
