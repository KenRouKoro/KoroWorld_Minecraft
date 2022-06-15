package cn.korostudio.koroworld.multiserver.connectTool;

import cn.hutool.core.exceptions.StatefulException;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.korostudio.koroworld.connect.KoroWorldConnect;
import cn.korostudio.koroworld.core.KoroWorldCore;
import cn.korostudio.koroworld.core.data.Data;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ServerConnectTool {

    public static void SetPlayerServerStatus(ServerPlayerEntity player){
        SetPlayerServerStatus(player.getUuidAsString().replace("-",""));
    }
    public static void SetPlayerServerStatus(String playerUUID){
        JSONObject servers = getServerList();
        String server = KoroWorldConnect.SUID;
        if(servers.getStr(server)==null){
            throw new StatefulException("NullServer","Server does not exist!");
        }
        HashMap<String,Object> postData = new HashMap<>();
        postData.put("UUID",playerUUID);
        postData.put("server",server);

        boolean status = JSONUtil.parseObj(HttpUtil.post("http://"+KoroWorldConnect.getConnect()+"/server/setLast",postData)).getBool("status",false);
        if (!status){
            throw new StatefulException("ServerError","Server Return False!");
        }
    }
    public static JSONObject getServerList(){
        JSONObject object = JSONUtil.parseObj(HttpUtil.get("http://"+KoroWorldConnect.getConnect()+"/server/list"));
        ArrayList<String> banServers;
        try {
            banServers = new ArrayList<>(List.of(Data.KoroWorldConfig.getStrings("banServers", "multiserver", ",Settin")));
        }catch (NullPointerException e){
            return object;
        }
        for(String server: object.keySet()){
            if (banServers.contains(server)){
                object.remove(server);
            }
        }

        return object;
    }
    public static void SetPlayerNextServer(ServerPlayerEntity player,String server){
        SetPlayerNextServer(player.getUuidAsString().replace("-",""),server);
    }
    public static void SetPlayerNextServer(String playerUUID,String server){
        JSONObject servers = getServerList();
        if(servers.getStr(server)==null){
            throw new StatefulException("NullServer","Server does not exist!");
        }
        HashMap<String,Object> postData = new HashMap<>();
        postData.put("UUID",playerUUID);
        postData.put("server",server);

        boolean status = JSONUtil.parseObj(HttpUtil.post("http://"+KoroWorldConnect.getConnect()+"/server/setNext",postData)).getBool("status",false);
        if (!status){
            throw new StatefulException("ServerError","Server Return False!");
        }
    }


}
