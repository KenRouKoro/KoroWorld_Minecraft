package cn.korostudio.koroworld.koroworldconnectkubejs.util;

import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.korostudio.koroworld.connect.KoroWorldConnect;
import cn.korostudio.koroworld.connect.api.DataAPI;
import cn.korostudio.koroworld.connect.api.DataPackListener;
import cn.korostudio.koroworld.koroworldconnectkubejs.event.DataPackEventJS;

public class DataPackUtil {
    public static void sendDataPack(String tags,String data,String target,boolean http){
        KubeJSDataPack kubeJSDataPack = new KubeJSDataPack();
        kubeJSDataPack.setUUID(StrUtil.uuid());
        kubeJSDataPack.setTags(tags);
        kubeJSDataPack.setData(data);
        kubeJSDataPack.setFrom(KoroWorldConnect.SUID);
        kubeJSDataPack.setTarget(target);
        if (http){
            DataAPI.sendToHttp(kubeJSDataPack);
        }else{
            DataAPI.sendToWS(kubeJSDataPack);
        }
    }

}
