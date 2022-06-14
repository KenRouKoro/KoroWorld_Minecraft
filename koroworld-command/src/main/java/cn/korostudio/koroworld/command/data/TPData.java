package cn.korostudio.koroworld.command.data;

import cn.hutool.cron.CronUtil;
import cn.hutool.cron.task.Task;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;

@Slf4j(topic = "KoroWorld-Command")
public class TPData {
    public static int timeout = 120;
    //@Getter
    protected static ConcurrentHashMap<String, TPData> TPData = new ConcurrentHashMap<>();

    public static void Init() {
        CronUtil.schedule("* * * * */2 *", (Task) () -> {
            log.debug("正在自动清理传送请求。");
            TPData.forEach((s, tpData) -> {
                if (tpData.getTime() + 120 < System.currentTimeMillis() / 1000L) {
                    TPData.remove(s);
                }
            });
        });
    }

    @Getter
    @Setter
    protected String uuid;
    @Getter
    @Setter
    protected ServerPlayerEntity target;
    @Getter
    @Setter
    protected ServerPlayerEntity from;
    @Getter
    @Setter
    protected long time;
    @Getter
    @Setter
    protected boolean mode;

    public static ConcurrentHashMap<String, cn.korostudio.koroworld.command.data.TPData> getTPData() {
        return TPData;
    }
}
