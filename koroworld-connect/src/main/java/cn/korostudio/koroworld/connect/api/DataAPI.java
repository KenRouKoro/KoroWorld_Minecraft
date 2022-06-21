package cn.korostudio.koroworld.connect.api;

import cn.hutool.cache.impl.TimedCache;
import cn.hutool.core.collection.ConcurrentHashSet;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.korostudio.koroworld.connect.KoroWorldConnect;
import cn.korostudio.koroworld.connect.data.DataPack;
import cn.korostudio.koroworld.connect.data.SaveDataPack;
import cn.korostudio.koroworld.connect.ws.WSConnector;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 数据收发API
 */
@Slf4j
public class DataAPI {
    /**
     * 临时数据包监听器缓存
     */
    @Getter
    protected static TimedCache<String,DataPackListener> TimeoutDataPackListeners = new TimedCache<>(KoroWorldConnect.timeout);
    /**
     * 数据包监听器
     */
    @Getter
    protected static ConcurrentHashMap<String, CopyOnWriteArrayList<DataPackListener>>DataPackListeners = new ConcurrentHashMap<>();
    /**
     * 数据包缓存，如果一个数据包没有被任何监听器处理，则会被放至此
     */
    @Getter
    protected static TimedCache<String,JSONObject> DatapackCache = new TimedCache<>(KoroWorldConnect.cacheTimeout);

    static {
        TimeoutDataPackListeners.schedulePrune(2000);
        DatapackCache.schedulePrune(10000);
    }

    /**
     * 设置数据包监听器，监听器type有非null值即当作正常监听器处理，否则为临时监听器，默认10秒超时。
     * @param dataPackListener 监听器
     * @return 数据同步List
     */
    public static CopyOnWriteArrayList<Object> listen(DataPackListener dataPackListener){
        if(dataPackListener.getType()!=null) {
            if (DataPackListeners.containsKey(dataPackListener.getType())){
                DataPackListeners.get(dataPackListener.getType()).add(dataPackListener);
            }else{
                CopyOnWriteArrayList<DataPackListener> listeners = new CopyOnWriteArrayList<>();
                listeners.add(dataPackListener);
                DataPackListeners.put(dataPackListener.getType(),listeners);
            }
            return dataPackListener.getSynchronizer();
        } else if(dataPackListener.getUUID()!=null){

            return listen(dataPackListener,KoroWorldConnect.timeout);
        }
        return null;
    }

    /**
     * 设置临时数据包监听器
     * @param dataPackListener 监听器
     * @param timeout 超时时间
     * @return 数据同步List
     */
    public static CopyOnWriteArrayList<Object> listen(DataPackListener dataPackListener,long timeout){
        TimeoutDataPackListeners.put(dataPackListener.getUUID(),dataPackListener,timeout);
        ThreadUtil.sleep(KoroWorldConnect.timeout);
        return dataPackListener.getSynchronizer();
    }

    /**
     * 移除数据包监听器
     * @param dataPackListener 需要移除的数据包监听器对象
     * @return 移除的数据包监听器
     */
    public static DataPackListener removeListener(DataPackListener dataPackListener){
        if (dataPackListener.getType()==null) return null;
        List<DataPackListener> list = DataPackListeners.get(dataPackListener.getType());
        if(list==null)return null;
        list.remove(dataPackListener);
        if (list.size()==0){
            DataPackListeners.remove(dataPackListener.getType());
        }
        return dataPackListener;
    }

    /**
     * 处理数据包
     * @param datapack JSON对象数据包
     */
    public static void process(JSONObject datapack){
        String uuid = datapack.getStr("UUID");
        if (uuid==null)return;
        DataPackListener dataPackListener = null;
        dataPackListener = TimeoutDataPackListeners.get(uuid,false);
        if(dataPackListener==null){
            String type = datapack.getStr("type");
            if (type==null|| !DataPackListeners.containsKey(type)){
                DatapackCache.put(uuid,datapack);
                return;
            }
            List<DataPackListener> listeners= DataPackListeners.get(type);
            for (DataPackListener listener:listeners){
                listener.run(datapack);
            }
        }else{
            DataPackListener finalDataPackListener = dataPackListener;
            ThreadUtil.execute(()->{
                finalDataPackListener.run(datapack);});
            TimeoutDataPackListeners.remove(dataPackListener.getUUID());
        }
    }

    /**
     * 发送WS数据包
     * @param dataPack Bean类数据包
     */
    public static void sendToWS(DataPack dataPack){
        sendToWS(JSONUtil.parseObj(dataPack));
    }

    /**
     * 发送WS数据包
     * @param dataPack JSON对象格式的数据包
     */
    public static void sendToWS(JSONObject dataPack){
        WSConnector.sendMessage(dataPack.toString());
    }

    /**
     * 发送Http数据包
     * @param dataPack Bean类数据包
     * @return String Http返回值
     */
    public static String sendToHttp(DataPack dataPack){
        return sendToHttp(JSONUtil.parseObj(dataPack));
    }

    /**
     *发送Http数据包
     * @param dataPack JSON对象格式的数据包
     * @return String Http返回值
     */
    public static String sendToHttp(JSONObject dataPack){
        Map<String ,Object> data  = new HashMap<>();
        data.put("data",dataPack.toString());
        String back = null;
        try {
            back = HttpUtil.post( "http://" + KoroWorldConnect.getConnect() + "/data/process", data, 10000);
        }catch (Exception e){
            return null;
        }
        return back;
    }

    /**
     * 储存String数据
     * @param tags String 标签
     * @param data String  储存的数据
     * @param http boolean 传输模式
     * @return String  UUID , Http模式为服务器返回
     */
    public static String saveData(String tags,String data,boolean http){
        SaveDataPack saveDataPack = new SaveDataPack();
        saveDataPack.setCommand("datasave update");
        saveDataPack.setData(data);
        saveDataPack.setUUID(StrUtil.uuid());
        saveDataPack.setFrom(KoroWorldConnect.SUID);
        saveDataPack.setTarget("SERVERONLY");
        saveDataPack.setTags(tags);

        listen(new DataPackListener() {
            @Override
            public void run(JSONObject datapack) {
                log.debug("DataPack UUID"+datapack.getStr("UUID")+" is "+datapack.getBool("status"));
            }
            @Override
            public String getType() {
                return saveDataPack.getUUID();
            }
        });

        if(http){
            return sendToHttp(JSONUtil.parseObj(saveDataPack));
        }else{
            sendToWS(JSONUtil.parseObj(saveDataPack));
        }

        return saveDataPack.getUUID();
    }

    /**
     *  获取储存值，默认10秒超时
     * @param tags String 标签
     * @param http boolean 传输模式
     * @return String 储存值，无值返回null
     */
    public static String getData(String tags,boolean http){
        String backData = null;

        SaveDataPack saveDataPack = new SaveDataPack();
        saveDataPack.setCommand("datasave download");
        saveDataPack.setData(null);
        saveDataPack.setUUID(StrUtil.uuid());
        saveDataPack.setFrom(KoroWorldConnect.SUID);
        saveDataPack.setTarget("SERVERONLY");
        saveDataPack.setTags(tags);

        if (http) {
            JSONObject backDataObj =  JSONUtil.parseObj(sendToHttp(JSONUtil.parseObj(saveDataPack)));
            return backDataObj.getStr("data");
        }else {
            sendToWS(JSONUtil.parseObj(saveDataPack));
        }

        CopyOnWriteArrayList<Object> data = listen(new DataPackListener() {
            final CopyOnWriteArrayList<Object> data = new CopyOnWriteArrayList<>();
            @Override
            public void run(JSONObject datapack) {
                if(datapack.getBool("status")){
                    data.add(datapack.getStr("data"));
                }
                thisThread.interrupt();
            }

            @Override
            public String getUUID() {
                return saveDataPack.getUUID();
            }
            @Override
            public CopyOnWriteArrayList<Object> getSynchronizer() {
                return data;
            }
        });

        if (data!=null){
            if(!data.isEmpty()) {
                backData = (String) data.get(0);
            }
        }

        return backData;
    }
}
