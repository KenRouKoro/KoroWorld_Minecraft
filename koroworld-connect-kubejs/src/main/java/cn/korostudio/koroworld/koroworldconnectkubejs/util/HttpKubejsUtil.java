package cn.korostudio.koroworld.koroworldconnectkubejs.util;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;

public class HttpKubejsUtil {
    public static String Post(String url,String body){
        String returnData;
        try {

            returnData = HttpUtil.post(url,body);

        }catch (Exception e){
            return "false";
        }

        return returnData;
    }

    public static String Get(String url,String paramJSON){
        String returnData;
        try {

            returnData = HttpUtil.get(url,JSONUtil.parseObj(paramJSON));

        }catch (Exception e){
            return "false";
        }

        return returnData;
    }
}
