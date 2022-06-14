package cn.korostudio.koroworld.command.data;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.file.FileReader;
import cn.hutool.core.io.file.FileWriter;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Data
@Slf4j
public class HomeData {
    static final public Map<String, HomeData> HOMEDATA = new ConcurrentHashMap<>();
    static protected int MaxHomes = 5;
    protected String name;
    protected List<HomePOSData> homes;

    public HomeData(String uuid) {
        name = uuid;
        homes = new CopyOnWriteArrayList<>();
    }

    public static int getMaxHomes() {
        return MaxHomes;
    }

    public static void setMaxHomes(int maxHomes) {
        MaxHomes = maxHomes;
    }

    static public void loadHomes() {
        try {
            FileReader fileReader = new FileReader(FileUtil.touch(System.getProperty("user.dir") + "/koroworld/data/homes.json"));
            JSONObject jsonObject = JSONUtil.parseObj(fileReader.readString());
            JSONArray jsonArray = jsonObject.getJSONArray("homes");
            List<HomeData> homes = jsonArray.toList(HomeData.class);
            for (HomeData data : homes) {
                HOMEDATA.put(data.name, data);
            }
        } catch (Exception e) {
            log.info("大概是没有任何home信息");
        }
    }

    static public void saveHomes() {
        FileWriter fileWriter = new FileWriter(FileUtil.touch(System.getProperty("user.dir") + "/koroworld/data/homes.json"));

        JSONArray jsonArray = JSONUtil.parseArray(HOMEDATA.values().toArray());
        JSONObject jsonObject = new JSONObject();
        jsonObject.putOnce("homes", jsonArray);
        fileWriter.write(jsonObject.toStringPretty());
    }

    @Data
    public static class HomePOSData {

        protected String name;
        protected String world;
        protected double x;
        protected double y;
        protected double z;
        protected float yaw;
        protected float pitch;
        public HomePOSData(String name, String world, double x, double y, double z, float yaw, float pitch) {
            this.name = name;
            this.world = world;
            this.x = x;
            this.y = y;
            this.z = z;
            this.yaw = yaw;
            this.pitch = pitch;
        }
    }
}
