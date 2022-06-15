package cn.korostudio.koroworld.multiserver.connectTool;

import cn.hutool.http.HttpException;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.korostudio.koroworld.multiserver.event.ClientRedirectEvent;
import lombok.extern.slf4j.Slf4j;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ConnectScreen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.network.ServerAddress;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.util.ActionResult;

import java.util.HashMap;

@Slf4j
@Environment(EnvType.CLIENT)
public class ClientConnectTool {
    /**
     * 用于储存当前已连接服务器地址
     */
    static public String nowAddress = null;

    /**
     * 重定向客户端到指定服务器
     * 这模拟了点击断开连接按钮并直接连接到指定的服务器地址
     * @param serverAddress 新服务器地址
     */
    @Environment(EnvType.CLIENT)
    public static void redirect(String servername,String serverAddress) {
        if (ClientRedirectEvent.EVENT.invoker().redirect(serverAddress) != ActionResult.SUCCESS) {
            return;
        }

        log.info("Connecting to " + serverAddress);

        final MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.world != null) {
            mc.world.disconnect();
        }

        mc.disconnect();

        mc.setScreen(new MultiplayerScreen(new TitleScreen()));
        ConnectScreen.connect(mc.currentScreen, mc, ServerAddress.parse(serverAddress), new ServerInfo(servername, serverAddress, false));
    }

    /**
     * 获取服务器的真实地址
     * @param address MS的服务器地址
     * @return MC服务器信息
     */
    @Environment(EnvType.CLIENT)
    public static JSONObject getTrueServerAddress(String address){
        final MinecraftClient mc = MinecraftClient.getInstance();
        String UUID = mc.getSession().getUuid();
        HashMap<String,Object> map = new HashMap<>();

        map.put("UUID",UUID);
        map.put("first","true");

        String backStr ;

        nowAddress = address;

        try {
            backStr = HttpUtil.post("http://"+address+"/client/process",map,1000);
        }catch (HttpException e){
            return JSONUtil.parseObj("{\"status\":false}");
        }

        return JSONUtil.parseObj(backStr);
    }

    /**
     * 解析默认服务器地址
     * @param address MS地址
     * @return 真实MC地址
     */
    @Environment(EnvType.CLIENT)
    public static JSONObject getDefAddress(String address){

        String backStr;
        try {
            backStr = HttpUtil.get("http://" + address + "/client/default", 1000);
        }catch (HttpException e){
            return JSONUtil.parseObj("{\"status\":false}");
        }
        return JSONUtil.parseObj(backStr);
    }

    /**
     * 获取下一步服务器连接地址
     * @param address MS的服务器地址
     * @return MC服务器信息
     */
    @Environment(EnvType.CLIENT)
    public static JSONObject getNextServerAddress(String address){
        final MinecraftClient mc = MinecraftClient.getInstance();
        String UUID = mc.getSession().getUuid();
        HashMap<String,Object> map = new HashMap<>();

        map.put("UUID",UUID);
        map.put("first","false");

        String backStr ;
        try {
            backStr = HttpUtil.post("http://"+address+"/client/process",map,1000);
        }catch (HttpException e){
            return JSONUtil.parseObj("{\"status\":false}");
        }

        return JSONUtil.parseObj(backStr);
    }
}
