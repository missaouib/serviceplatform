package co.yixiang.modules.yaoshitong.websocket;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import co.yixiang.common.api.ApiResult;
import co.yixiang.modules.user.entity.YxUser;
import co.yixiang.modules.user.service.YxUserService;
import co.yixiang.modules.user.service.YxWechatUserService;
import co.yixiang.modules.user.web.vo.YxWechatUserQueryVo;
import co.yixiang.modules.yaoshitong.entity.ChatGroupMember;
import co.yixiang.modules.yaoshitong.entity.ChatGroupMsg;
import co.yixiang.modules.yaoshitong.entity.YaoshitongPatient;
import co.yixiang.modules.yaoshitong.service.*;
import co.yixiang.modules.yaoshitong.util.EmojiFilter;
import co.yixiang.modules.yaoshitong.web.param.YaoshitongPatientQueryCriteria;
import co.yixiang.modules.yaoshitong.web.vo.ChatMsg;
import co.yixiang.mp.service.YxTemplateService;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @ServerEndpoint 注解是一个类层次的注解，它的功能主要是将目前的类定义成一个websocket服务器端,
 * 注解的值将被用于监听用户连接的终端访问URL地址,客户端可以通过这个URL来连接到WebSocket服务器端
 * @ServerEndpoint 可以把当前类变成websocket服务类
 */
@CrossOrigin
@Slf4j
@RestController
@ServerEndpoint(value = "/websocketGroup/{userno}")
public class ChatGroupWebSocket {
    // 这里使用静态，让 service 属于类
    private static ChatMsgService chatMsgService;

    private static YaoshitongPatientService patientService;

    private static YxTemplateService yxTemplateService;

    private  static YxWechatUserService wechatUserService;

    private static ChatGroupMsgService chatGroupMsgService;

    private static ChatGroupMemberService chatGroupMemberService;

    private static YxUserService yxUserService;
    // 注入的时候，给类的 service 注入
    @Autowired
    public void setChatService(ChatMsgService chatService) {

      //  log.info("===========================websocket chatService init");
        ChatGroupWebSocket.chatMsgService = chatService;
    }

    @Autowired
    public void setChatGroupService(ChatGroupMsgService chatGroupMsgService,ChatGroupMemberService chatGroupMemberService,YxUserService yxUserService) {

     //   log.info("===========================websocket setChatGroupService init");
        ChatGroupWebSocket.chatGroupMsgService = chatGroupMsgService;
        ChatGroupWebSocket.chatGroupMemberService = chatGroupMemberService;
        ChatGroupWebSocket.yxUserService = yxUserService;
    }

    @Autowired
    public void setYaoshitongPatientService(YaoshitongPatientService patientService) {

     //   log.info("===========================websocket patientService init");
        ChatGroupWebSocket.patientService = patientService;
    }

    @Autowired
    public void setYxTemplateService(YxTemplateService yxTemplateService,YxWechatUserService wechatUserService) {


        ChatGroupWebSocket.yxTemplateService = yxTemplateService;
        ChatGroupWebSocket.wechatUserService = wechatUserService;

      //  log.info("===========================websocket yxTemplateService init");
    }
    //静态变量，用来记录当前在线连接数。应该把它设计成线程安全的。
    private static int onlineCount = 0;
    //concurrent包的线程安全Set，用来存放每个客户端对应的MyWebSocket对象。若要实现服务端与单一客户端通信的话，可以使用Map来存放，其中Key可以为用户标识
    private static ConcurrentHashMap<String, ChatGroupWebSocket> webSocketSet = new ConcurrentHashMap<String, ChatGroupWebSocket>();
    //与某个客户端的连接会话，需要通过它来给客户端发送数据
    private Session WebSocketsession;
    //当前发消息的人员编号
    private String userno ;


    /**
     * 连接建立成功调用的方法
     *
     * session 可选的参数。session为与某个客户端的连接会话，需要通过它来给客户端发送数据
     */
    @OnOpen
    public void onOpen(@PathParam(value = "userno") String param, Session WebSocketsession) {
        log.info("onOpen groupWebSocket ...[{}]",param);
        userno = param;//接收到发送消息的人员编号
        this.WebSocketsession = WebSocketsession;
        webSocketSet.put(param, this);//加入map中
        addOnlineCount();     //在线数加1
        //System.out.println("有新连接加入！当前在线人数为" + getOnlineCount());
        log.info( "有新连接加入group websocket [{}]！当前在线人数为{}" ,param, getOnlineCount());
      //  YxUserQueryVo user = yxUserService.getYxUserById(Integer.valueOf(param));
      //  log.info("用户[{}]加入",user.getNickname());
    }


    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose() {
        if ( userno != null && !userno.equals("")) {
            webSocketSet.remove(userno); //从set中删除
            subOnlineCount();     //在线数减1
            //System.out.println("有一连接关闭！当前在线人数为" + getOnlineCount());
            log.info("用户[{}]group websocket连接关闭！当前在线人数为{}",userno,getOnlineCount());
        }
    }


    /**
     * 收到客户端消息后调用的方法
     *
     * @param chatGroupMsg 客户端发送过来的消息
     * @param session 可选的参数
     */
    @SuppressWarnings("unused")
	@OnMessage
    public void onMessage(String chatGroupMsg, Session session) {
        log.info("chatGroupMsg={}",chatGroupMsg);
        JSONObject jsonObject = JSONObject.parseObject(chatGroupMsg);
        //给指定的人发消息
      //  sendToUser(jsonObject.toJavaObject(ChatMsg.class));

        sendToGroup(jsonObject.toJavaObject(ChatGroupMsg.class));
        //sendAll(message);
    }


    /**
     * 给指定的人发送消息
     *
     * @param chatMsg 消息对象
     */
/*    public void sendToUser(ChatMsg chatMsg) {
        String reviceUserid = chatMsg.getReciveuserid();
        String sendMessage = chatMsg.getSendtext();
        sendMessage= EmojiFilter.filterEmoji(sendMessage);//过滤输入法输入的表情
        chatMsg.setSenduserid(userno);
        chatMsgService.InsertChatMsg(chatMsg);
        try {
            if (webSocketSet.get(reviceUserid) != null) {
                ChatMsg chatMsg1 = new ChatMsg();
                chatMsg1.setMsgtype("0");
                chatMsg1.setReciveuserid(reviceUserid);
                chatMsg1.setSenduserid(userno);
                chatMsg1.setSendtext(sendMessage);

                webSocketSet.get(reviceUserid).sendMessage(userno+"|"+sendMessage);
            }else{
                webSocketSet.get(userno).sendMessage("0"+"|"+"当前用户不在线");

                chatMsgService.InsertUnread(Integer.valueOf(userno),Integer.valueOf(reviceUserid));
                YxWechatUserQueryVo wechatUser =  wechatUserService.getYxWechatUserById(Integer.valueOf(reviceUserid));

                YaoshitongPatient patient = patientService.findPatientByUid(Integer.valueOf(userno));

                if(ObjectUtil.isNotNull(wechatUser)  && ObjectUtil.isNotEmpty(patient)  && StrUtil.isNotBlank(wechatUser.getOpenid())){
                    String userName= patient.getName();
                    String userPhone= patient.getPhone();
                    String requestDate= DateUtil.now();
                    String openid= wechatUser.getOpenid();
                    yxTemplateService.requestNotice(userName,userPhone,requestDate,openid);
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/


    /**
     * 给指定的人发送消息
     *
     * @param chatGroupMsg 消息对象
     */
    public void sendToGroup(ChatGroupMsg chatGroupMsg) {

        String sendMessage = chatGroupMsg.getSendText();
        sendMessage= EmojiFilter.filterEmoji(sendMessage);//过滤输入法输入的表情
        chatGroupMsg.setSendUid(Integer.valueOf(userno));
        chatGroupMsg.setSendText(sendMessage);
        chatGroupMsg.setSendTime(DateUtil.date());
        YxUser yxUser = yxUserService.getById(chatGroupMsg.getSendUid());

        chatGroupMsg.setSendName(yxUser.getNickname());
        chatGroupMsg.setSendAvatar(yxUser.getAvatar());

        chatGroupMsgService.InsertChatGroupMsg(chatGroupMsg);

        List<ChatGroupMember> memberList = chatGroupMemberService.list(new QueryWrapper<ChatGroupMember>().eq("group_id",chatGroupMsg.getGroupId()).ne("uid",userno));
        for(ChatGroupMember member : memberList) {
            try {
                if (webSocketSet.get(member.getUid()) != null) {

                    String msg = JSONUtil.parseObj(chatGroupMsg).toString();
                    webSocketSet.get(member.getUid()).sendMessage(msg);
                }else{
                    /*ChatGroupMsg chatGroupMsg1 = new ChatGroupMsg();
                    chatGroupMsg1.setMsgType("offline");
                    chatGroupMsg1.setSendText("当前用户不在线");
                    String msg = JSONUtil.parseObj(chatGroupMsg1).toString();
                    webSocketSet.get(userno).sendMessage(msg);*/

                    chatGroupMsgService.InsertUnreadGroup(member.getUid(),chatGroupMsg.getGroupId());
                /*    YxWechatUserQueryVo wechatUser =  wechatUserService.getYxWechatUserById(Integer.valueOf(reviceUserid));

                    YaoshitongPatient patient = patientService.findPatientByUid(Integer.valueOf(userno));

                    if(ObjectUtil.isNotNull(wechatUser)  && ObjectUtil.isNotEmpty(patient)  && StrUtil.isNotBlank(wechatUser.getOpenid())){
                        String userName= patient.getName();
                        String userPhone= patient.getPhone();
                        String requestDate= DateUtil.now();
                        String openid= wechatUser.getOpenid();
                        yxTemplateService.requestNotice(userName,userPhone,requestDate,openid);
                    }*/

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }


    /**
     * 给所有人发消息
     *
     * @param message
     */
    private void sendAll(String message) {
        String sendMessage = message.split("[|]")[1];
        //遍历HashMap
        for (String key : webSocketSet.keySet()) {
            try {
                //判断接收用户是否是当前发消息的用户
                if (!userno.equals(key)) {
                    webSocketSet.get(key).sendMessage(sendMessage);
                    System.out.println("key = " + key);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 发生错误时调用
     *
     * @param session
     * @param error
     */
    @OnError
    public void onError(Session session, Throwable error) {
        error.printStackTrace();
    }


    /**
     * 这个方法与上面几个方法不一样。没有用注解，是根据自己需要添加的方法。
     *
     * @param
     * @throws IOException
     */
    public void sendMessage(String message) throws IOException {
        log.info("criteria={}",message);
        this.WebSocketsession.getBasicRemote().sendText(message);
        //this.session.getAsyncRemote().sendText(message);
    }


    public static synchronized int getOnlineCount() {
        return onlineCount;
    }


    public static synchronized void addOnlineCount() {
        ChatGroupWebSocket.onlineCount++;
    }


    public static synchronized void subOnlineCount() {
        ChatGroupWebSocket.onlineCount--;
    }

}

