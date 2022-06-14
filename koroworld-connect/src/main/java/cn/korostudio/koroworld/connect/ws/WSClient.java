package cn.korostudio.koroworld.connect.ws;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.korostudio.koroworld.connect.api.DataAPI;
import lombok.extern.slf4j.Slf4j;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;

import static cn.korostudio.koroworld.connect.ws.WSConnector.wsClose;
import static cn.korostudio.koroworld.connect.ws.WSConnector.wsError;

/**
 * WS连接客户端
 */
@Slf4j
public class WSClient extends WebSocketClient {
    /**
     * 默认构造方法
     * @param serverUri 连接URI地址
     */
    public WSClient(URI serverUri) {
        super(serverUri);
    }

    /**
     * WS连接打开处理程序
     * @param handshakedata The handshake of the websocket instance
     */
    @Override
    public void onOpen(ServerHandshake handshakedata) {
        log.info("WS Connected!");
    }

    /**
     * WS连接信息处理程序
     * @param message The UTF-8 decoded message that was received.
     */
    @Override
    public void onMessage(String message) {
        JSONObject jsonObject = JSONUtil.parseObj(message);
        DataAPI.process(jsonObject);
    }

    /**
     * WS连接关闭处理程序
     * @param code   The codes can be looked up here: CloseFrame
     * @param reason Additional information string
     * @param remote Returns whether or not the closing of the connection was initiated by the remote
     *               host.
     */
    @Override
    public void onClose(int code, String reason, boolean remote) {
        wsClose();
    }

    /**
     * WS连接异常处理程序
     * @param ex The exception causing this error
     */
    @Override
    public void onError(Exception ex) {
        wsError(ex);
    }


}
