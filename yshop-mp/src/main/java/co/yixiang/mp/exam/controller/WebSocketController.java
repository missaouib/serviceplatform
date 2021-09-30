package co.yixiang.mp.exam.controller;

import co.yixiang.mp.exam.websocket.WebSocketServer;
import lombok.Data;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.util.concurrent.CopyOnWriteArraySet;
/**
 * @ClassName WebSocketController
 * @Author zhouhang
 * @Date 2019/11/6
 **/
@Controller
@Data
public class WebSocketController {

    @RequestMapping(value = "/pushMsgToHtml",method = RequestMethod.GET)
    @ResponseBody
    public String pushMsgToHtml() throws IOException{
        CopyOnWriteArraySet<WebSocketServer> webSocketSet = WebSocketServer.webSocketSet;
        for(WebSocketServer webSocketServer : webSocketSet){
            webSocketServer.sendMessage("你好客户端.....我是服务端");
        }

        return "200";
    }

}
