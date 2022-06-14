package cn.korostudio.koroworld.connect.ws;

import cn.hutool.core.codec.Base62;
import cn.hutool.core.thread.ThreadUtil;
import cn.korostudio.koroworld.connect.KoroWorldConnect;
import cn.korostudio.koroworld.core.KoroWorldCore;
import lombok.extern.slf4j.Slf4j;

import java.net.URI;

/**
 * WS连接控制器
 */
@Slf4j
public class WSConnector {
    /**
     * WS连接实例对象
     */
    protected static WSClient wsClient =null;

    /**
     * 第一次触发连接
     */
    public static void start(){
        ThreadUtil.execute(WSConnector::CreatConnect);
    }

    /**
     * 发送ws数据，这个方法是线程安全的。
     * @param message 发送的信息
     */
    public synchronized static void sendMessage(String message){
        if (wsClient==null||wsClient.isClosed()){
            log.error("Send Message Fail ! WS Connect is Close or BreakDown!");
            return;
        }
        wsClient.send(message);
    }

    /**
     * WS连接关闭处理方法
     */
    public static void wsClose(){
        log.error("WS Connect Close!Retry after 1 minute.");
        ThreadUtil.execute(()->{
            ThreadUtil.sleep(60000);
            CreatConnect();
        });
    }

    /**
     * WS连接异常处理方法
     * @param e 异常对象
     */
    public static void wsError(Exception e){
        log.error("WS Connect Error!Error is"+e.getMessage()+" Retry after 1 minute.");
        ThreadUtil.execute(()->{
            ThreadUtil.sleep(60000);
            CreatConnect();
        });
    }

    /**
     * 创建并连接WS服务器
     */
    protected static void CreatConnect(){
        try {
            String wsURL = KoroWorldConnect.isSsl()?"wss://":"ws://" + KoroWorldConnect.getConnect()+"/ws"+"?key="+ Base62.encode(KoroWorldCore.getServerName());
            log.info("Trying to connect to:"+wsURL);
            URI uri = new URI(wsURL);
            wsClient = new WSClient(uri);
            wsClient.connect();
        } catch (Exception exception) {
            log.error("WS Connect FAIL!");
            exception.printStackTrace();
        }
    }
}
