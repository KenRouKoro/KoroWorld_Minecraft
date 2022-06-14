package cn.korostudio.koroworld.connect.api;

import cn.hutool.json.JSONObject;

import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 数据包监听器
 */
public abstract class DataPackListener {
    /**
     * 当前线程handle
     */
    Thread thisThread;

    /**
     * 获取当前线程handle
     */
    {
        thisThread = Thread.currentThread();
    }

    /**
     * 监听器处理
     * @param datapack JSON对象数据包
     */
    public abstract void run(JSONObject datapack);

    /**
     * 获取处理标签
     * @return Type标签
     */
    public String getType() {
        return null;
    }

    /**
     * 获取处理UUID
     * @return UUID
     */
    public String getUUID() {
        return null;
    }

    /**
     * 获取资源中继List，因为汝是不能在内部匿名类里操作局部变量的，所以得有个中继。
     * @return 资源中继List
     */
    public CopyOnWriteArrayList<Object> getSynchronizer(){
        return null;
    };
}
