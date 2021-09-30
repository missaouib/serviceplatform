package co.yixiang.modules.yaoshitong.websocket;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import co.yixiang.common.api.ApiResult;
import co.yixiang.modules.user.service.YxUserService;
import co.yixiang.modules.user.service.YxWechatUserService;
import co.yixiang.modules.user.web.vo.YxUserQueryVo;
import co.yixiang.modules.user.web.vo.YxWechatUserQueryVo;
import co.yixiang.modules.yaoshitong.entity.YaoshitongPatient;
import co.yixiang.modules.yaoshitong.service.YaoshitongPatientService;
import co.yixiang.modules.yaoshitong.web.vo.ChatMsg;
import co.yixiang.modules.yaoshitong.service.ChatMsgService;
import co.yixiang.modules.yaoshitong.util.EmojiFilter;
import co.yixiang.modules.yaoshitong.web.param.YaoshitongPatientQueryCriteria;
import co.yixiang.mp.domain.YxWechatUserInfo;
import co.yixiang.mp.service.YxTemplateService;
import co.yixiang.mp.service.YxWechatUserInfoService;
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
import java.util.concurrent.ConcurrentHashMap;

/**
 * @ServerEndpoint 注解是一个类层次的注解，它的功能主要是将目前的类定义成一个websocket服务器端,
 * 注解的值将被用于监听用户连接的终端访问URL地址,客户端可以通过这个URL来连接到WebSocket服务器端
 * @ServerEndpoint 可以把当前类变成websocket服务类
 */
@CrossOrigin
@Slf4j
@RestController
@ServerEndpoint(value = "/websocket/{userno}")
public class ChatWebSocket {
    // 这里使用静态，让 service 属于类
    private static ChatMsgService chatMsgService;

    private static YaoshitongPatientService patientService;

    private static YxTemplateService yxTemplateService;

    private static YxWechatUserService wechatUserService;

    private static YxUserService yxUserService;

    private static YxWechatUserInfoService yxWechatUserInfoService;
    // 注入的时候，给类的 service 注入
    @Autowired
    public void setChatService(ChatMsgService chatService) {

       // log.info("===========================websocket chatService init");
        ChatWebSocket.chatMsgService = chatService;

    }

    @Autowired
    public void setYaoshitongPatientService(YaoshitongPatientService patientService) {

       // log.info("===========================websocket patientService init");
        ChatWebSocket.patientService = patientService;
    }

    @Autowired
    public void setYxTemplateService(YxTemplateService yxTemplateService,YxWechatUserService wechatUserService,YxUserService yxUserService,YxWechatUserInfoService yxWechatUserInfoService) {


        ChatWebSocket.yxTemplateService = yxTemplateService;
        ChatWebSocket.wechatUserService = wechatUserService;
        ChatWebSocket.yxUserService = yxUserService;
        ChatWebSocket.yxWechatUserInfoService = yxWechatUserInfoService;
       // log.info("===========================websocket yxTemplateService init");
    }
    //静态变量，用来记录当前在线连接数。应该把它设计成线程安全的。
    private static int onlineCount = 0;
    //concurrent包的线程安全Set，用来存放每个客户端对应的MyWebSocket对象。若要实现服务端与单一客户端通信的话，可以使用Map来存放，其中Key可以为用户标识
    private static ConcurrentHashMap<String, ChatWebSocket> webSocketSet = new ConcurrentHashMap<String, ChatWebSocket>();
    //与某个客户端的连接会话，需要通过它来给客户端发送数据
    private Session WebSocketsession;
    //当前发消息的人员编号
    private String userno = "";


    /**
     * 连接建立成功调用的方法
     *
     * session 可选的参数。session为与某个客户端的连接会话，需要通过它来给客户端发送数据
     */
    @OnOpen
    public void onOpen(@PathParam(value = "userno") String param, Session WebSocketsession) {
        log.info("onOpen WebSocket ...[{}]",param);
        userno = param;//接收到发送消息的人员编号
        this.WebSocketsession = WebSocketsession;
        webSocketSet.put(param, this);//加入map中
        addOnlineCount();     //在线数加1
        //System.out.println("有新连接加入！当前在线人数为" + getOnlineCount());
        log.info( "有新连接加入[{}]！当前在线人数为{}" ,param, getOnlineCount());
      //  YxUserQueryVo user = yxUserService.getYxUserById(Integer.valueOf(param));
      //  log.info("用户[{}]加入",user.getNickname());
    }


    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose() {
        if (!userno.equals("")) {
            webSocketSet.remove(userno); //从set中删除
            subOnlineCount();     //在线数减1
            //System.out.println("有一连接关闭！当前在线人数为" + getOnlineCount());
            log.info("用户[{}]连接关闭！当前在线人数为{}",userno,getOnlineCount());
        }
    }


    /**
     * 收到客户端消息后调用的方法
     *
     * @param chatmsg 客户端发送过来的消息
     * @param session 可选的参数
     */
    @SuppressWarnings("unused")
	@OnMessage
    public void onMessage(String chatmsg, Session session) {
        JSONObject jsonObject = JSONObject.parseObject(chatmsg);
        //给指定的人发消息
        sendToUser(jsonObject.toJavaObject(ChatMsg.class));
        //sendAll(message);
    }


    /**
     * 给指定的人发送消息
     *
     * @param chatMsg 消息对象
     */
    public void sendToUser(ChatMsg chatMsg) {
        String reviceUserid = chatMsg.getReciveuserid();
        String sendMessage = chatMsg.getSendtext();
      //  sendMessage= EmojiFilter.filterEmoji(sendMessage);//过滤输入法输入的表情
        chatMsg.setSenduserid(userno);
        chatMsg.setSendtext(sendMessage);
        chatMsgService.InsertChatMsg(chatMsg);

        // 更新最后聊天时间



        try {
            if (webSocketSet.get(reviceUserid) != null) {
               /* ChatMsg chatMsg1 = new ChatMsg();
                chatMsg1.setMsgtype("0");
                chatMsg1.setReciveuserid(reviceUserid);
                chatMsg1.setSenduserid(userno);
                chatMsg1.setSendtext(sendMessage);*/

                webSocketSet.get(reviceUserid).sendMessage(JSONUtil.parseObj(chatMsg).toString());
            }else{

              /*  ChatMsg chatMsg1 = new ChatMsg();
                chatMsg1.setMsgtype("-1");
                chatMsg1.setReciveuserid(userno);
                chatMsg1.setSenduserid("0");
                chatMsg1.setSendtext("当前用户不在线");*/

                //webSocketSet.get(userno).sendMessage(JSONUtil.parseObj(chatMsg1).toString());
                log.info(" websocket 未读消息保存：senduserid={},reviceuserid={}",Integer.valueOf(userno),Integer.valueOf(reviceUserid));
                chatMsgService.InsertUnread(Integer.valueOf(userno),Integer.valueOf(reviceUserid));
                YxWechatUserQueryVo wechatUser =  wechatUserService.getYxWechatUserById(Integer.valueOf(reviceUserid));
                // 判断接收人是药师，还是患者
                YxUserQueryVo yxUserQueryVo = yxUserService.getYxUserById(Integer.valueOf(reviceUserid));
                if(StrUtil.isNotBlank(yxUserQueryVo.getYaoshiPhone())) {   // 接收人是药师，发微信通知
                   // YaoshitongPatient patient = patientService.findPatientByUid(Integer.valueOf(userno));
                    YxUserQueryVo patient = yxUserService.getYxUserById(Integer.valueOf(userno));
                    if(ObjectUtil.isNotNull(wechatUser)  && ObjectUtil.isNotEmpty(patient)  && StrUtil.isNotBlank(wechatUser.getOpenid())){
                        String userName= ObjectUtil.defaultIfBlank(patient.getRealName(),patient.getNickname());
                        String userPhone= patient.getPhone();
                        String requestDate= DateUtil.now();
                        String openid= wechatUser.getOpenid();
                        yxTemplateService.requestNotice(userName,userPhone,requestDate,openid);
                    }
                } else  {  // 接收人是患者

                    YxUserQueryVo sendUser = yxUserService.getYxUserById(Integer.valueOf(userno));

                    String title = "药师留言提醒";
                    String content = "药师["+ sendUser.getRealName() +"]给你留言";
                    String requestDate = DateUtil.now();
                    String remark = "请点击查看";
                    String openid = wechatUser.getOpenid();

                    if(StrUtil.isBlank(openid)) {
                       YxWechatUserInfo yxWechatUserInfo = yxWechatUserInfoService.getOne(new QueryWrapper<YxWechatUserInfo>().eq("union_id",wechatUser.getUnionid()));
                       openid = yxWechatUserInfo.getOpenId();
                    }
                    if(StrUtil.isNotBlank(openid)) {
                        log.info("向患者发送留言信息 接收患者id={},发送药师id={}，接收人的openid={}",reviceUserid,sendUser.getUid(),openid);
                        yxTemplateService.requestNotice(title,content,requestDate,remark,openid);
                    } else {
                        log.info("向患者发送留言信息 接收患者id={},发送药师id={}，接收人的openid无法找到",reviceUserid,sendUser.getUid());
                    }


                }
            }
        } catch (IOException e) {
            e.printStackTrace();
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


    @PostMapping("/send")
    public ApiResult<Object> sendMessageTest(@RequestBody YaoshitongPatientQueryCriteria criteria) throws IOException {
        log.info("criteria={}",criteria);
        for (String key : webSocketSet.keySet()) {
            log.info("向{}发消息",key);
            if(webSocketSet.get(key) != null){
                webSocketSet.get(key).sendMessage(criteria.getName());

            }

        }


      //  this.WebSocketsession.getBasicRemote().sendText(criteria.getName());
        //this.session.getAsyncRemote().sendText(message);
        return ApiResult.ok();
    }

    public static synchronized int getOnlineCount() {
        return onlineCount;
    }


    public static synchronized void addOnlineCount() {
        ChatWebSocket.onlineCount++;
    }


    public static synchronized void subOnlineCount() {
        ChatWebSocket.onlineCount--;
    }

}

