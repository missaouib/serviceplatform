package co.yixiang.modules.websocket;

import lombok.extern.slf4j.Slf4j;

import net.sf.json.JSONObject;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author scott
 * @Date 2019/11/29 9:41
 * @Description: 此注解相当于设置访问URL
 */
@Component
@Slf4j
@ServerEndpoint("/websocket/{userId}") //此注解相当于设置访问URL
public class WebSocket {

    private static int onlineCount = 0;
    private static Map<String, WebSocket> clients = new ConcurrentHashMap<String, WebSocket>();
    private Session session;
    private String userId;

    @OnOpen
    public void onOpen(Session session,@PathParam(value = "userId")  String userId) throws IOException {

        this.userId = userId;
        this.session = session;

        addOnlineCount();
        clients.put(userId, this);
        log.info("【websocket消息】有新的连接，总数为:" + getOnlineCount());
    }

    @OnClose
    public void onClose() throws IOException {
        clients.remove(userId);
        subOnlineCount();
        log.info("【websocket消息】连接断开，总数为:" + getOnlineCount());
    }

    @OnMessage
    public void onMessage(String message)  {
        log.debug("【websocket消息】收到客户端消息:" + message);

        JSONObject jsonTo = JSONObject.fromObject(message);

        if (!jsonTo.get("To").equals("All")){
            pushMessage(jsonTo.get("To").toString(),jsonTo.get("message").toString() );
        }else{
            pushMessage(jsonTo.get("message").toString());
        }
    }

    @OnError
    public void onError(Session session, Throwable error) {
        error.printStackTrace();
    }

    public void pushMessage(String To,String message) {
        for (WebSocket item : getClients().values()) {
            if (item.userId.equals(To) ) {
                log.info("【websocket消息】 单点消息:" + message);
                item.session.getAsyncRemote().sendText(message);
            }
        }
    }

    public void pushMessage(String message) {
        for (WebSocket item : getClients().values()) {
            item.session.getAsyncRemote().sendText(message);
        }
    }



    public static synchronized int getOnlineCount() {
        return onlineCount;
    }

    public static synchronized void addOnlineCount() {
        WebSocket.onlineCount++;
    }

    public static synchronized void subOnlineCount() {
        WebSocket.onlineCount--;
    }

    public static synchronized Map<String, WebSocket> getClients() {
        return clients;
    }

}