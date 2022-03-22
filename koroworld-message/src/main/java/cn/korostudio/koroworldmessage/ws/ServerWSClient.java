package cn.korostudio.koroworldmessage.ws;

import cn.hutool.json.JSONUtil;
import cn.korostudio.koroworldmessage.data.MessageSystemData;
import cn.korostudio.koroworldmessage.data.PlayerChatData;
import cn.korostudio.koroworldcore.util.MessageTool;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.handshake.ServerHandshake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.Map;

public class ServerWSClient extends WebSocketClient {

    protected static Logger logger = LoggerFactory.getLogger(ServerWSClient.class);

    public ServerWSClient(URI serverUri) {
        super(serverUri);
    }

    public ServerWSClient(URI serverUri, Draft protocolDraft) {
        super(serverUri, protocolDraft);
    }

    public ServerWSClient(URI serverUri, Map<String, String> httpHeaders) {
        super(serverUri, httpHeaders);
    }

    public ServerWSClient(URI serverUri, Draft protocolDraft, Map<String, String> httpHeaders) {
        super(serverUri, protocolDraft, httpHeaders);
    }

    public ServerWSClient(URI serverUri, Draft protocolDraft, Map<String, String> httpHeaders, int connectTimeout) {
        super(serverUri, protocolDraft, httpHeaders, connectTimeout);
    }

    /**
     * Called after an opening handshake has been performed and the given websocket is ready to be
     * written on.
     *
     * @param handshakedata The handshake of the websocket instance
     */
    @Override
    public void onOpen(ServerHandshake handshakedata) {
        logger.info("WS Connected!");
    }

    /**
     * Callback for string messages received from the remote host
     *
     * @param message The UTF-8 decoded message that was received.
     **/
    @Override
    public void onMessage(String message) {
        PlayerChatData chatData = JSONUtil.toBean(message, PlayerChatData.class);
        if(!chatData.getGroup().equals(MessageSystemData.MessageGroup)){
            if(!chatData.isHighestAuthority()){
                return;
            }
        }
        if(chatData.isSystem()){
            MessageTool.sendSystemMessageAll(chatData.getMessage(),chatData.getServer());
        } else{
            MessageTool.sendCharMessageAll(chatData.getMessage(),chatData.getName(),chatData.getServer());
        }
        logger.info(chatData.getMessage());
    }

    /**
     * Called after the websocket connection has been closed.
     *
     * @param reason Additional information string
     * @param remote Returns whether or not the closing of the connection was initiated by the remote
     **/
    @Override
    public void onClose(int code, String reason, boolean remote) {
        logger.info("WS Connect Close.");
    }

    /**
     * Called when errors occurs. If an error causes the websocket connection to fail {@link
     * #onClose(int, String, boolean)} will be called additionally.<br> This method will be called
     * primarily because of IO or protocol errors.<br> If the given exception is an RuntimeException
     * that probably means that you encountered a bug.<br>
     *
     * @param ex The exception causing this error
     **/
    @Override
    public void onError(Exception ex) {
        ex.printStackTrace();
    }
}
