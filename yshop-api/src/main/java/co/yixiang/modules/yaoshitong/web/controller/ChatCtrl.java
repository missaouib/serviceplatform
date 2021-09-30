package co.yixiang.modules.yaoshitong.web.controller;

import co.yixiang.annotation.AnonymousAccess;
import co.yixiang.common.api.ApiResult;
import co.yixiang.modules.user.service.YxUserService;
import co.yixiang.modules.user.web.vo.YxUserQueryVo;
import co.yixiang.modules.yaoshitong.entity.ChatGroupMsg;
import co.yixiang.modules.yaoshitong.service.ChatGroupMsgService;
import co.yixiang.modules.yaoshitong.web.vo.ChatMsg;
import co.yixiang.modules.yaoshitong.service.ChatMsgService;
import co.yixiang.modules.yaoshitong.web.vo.ChatUserInfo;
import co.yixiang.utils.SecurityUtils;
import com.alibaba.fastjson.JSONObject;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

@RestController
@Slf4j
public class ChatCtrl {

    @Autowired
    ChatMsgService chatMsgService;

    @Autowired
    YxUserService yxUserService;

    @Autowired
    ChatGroupMsgService chatGroupMsgService;

    /**
     * 上传聊天图片
     * **/
    @PostMapping(value = "/chat/upimg")
    @ResponseBody
    public JSONObject upauz(@RequestParam(value = "file", required = false) MultipartFile file) throws IOException {
        JSONObject res = new JSONObject();
        JSONObject resUrl = new JSONObject();
        LocalDate today = LocalDate.now();
        Instant timestamp = Instant.now();
        String ext = FilenameUtils.getExtension(file.getOriginalFilename());
        String filenames = today + String.valueOf(timestamp.toEpochMilli()) + "."+ext;
        file.transferTo(new File("D:\\chat\\" + filenames));
        resUrl.put("src", "/pic/" + filenames);
        res.put("msg", "");
        res.put("code", 0);
        res.put("data", resUrl);
        return res;
    }



    /***
     * 查询两个用户之间的聊天记录
     * */
    @PostMapping("/chat/lkuschatmsg/{senduserid}")
    @ResponseBody
    @AnonymousAccess
    public ApiResult<IPage<ChatMsg>> lkfriends(HttpSession session, @PathVariable("senduserid")String senduserid, @RequestBody ChatMsg chatMsg){
        // String userid=(String)session.getAttribute("userid");
        String reciveuserid = SecurityUtils.getUserId().toString();
       // String userid = "31";
        chatMsg.setSenduserid(senduserid);
        chatMsg.setReciveuserid(reciveuserid);
        return ApiResult.ok(chatMsgService.LookTwoUserMsg(chatMsg));
    }


    /***
     * 查询两个用户之间的聊天记录-- 药师通查询
     * */
    @PostMapping("/chat/lkuschatmsgYaoshitong/{senduserid}")
    @ResponseBody
    @AnonymousAccess
    public ApiResult<IPage<ChatMsg>> lkfriendsYaoshitong(HttpSession session, @PathVariable("senduserid")String senduserid, @RequestBody ChatMsg chatMsg){
        // String userid=(String)session.getAttribute("userid");
        String reciveuserid = SecurityUtils.getUserId().toString();
        // String userid = "31";
        chatMsg.setSenduserid(senduserid);
        chatMsg.setReciveuserid(reciveuserid);

        IPage<ChatMsg> ipage =  chatMsgService.LookTwoUserMsg(chatMsg);
        for(ChatMsg msg:ipage.getRecords()) {
            if(msg.getMsgtype().equals("1")) {  // 是图片类型的，需要加上样式
               String text =  msg.getSendtext();
               String imgText = "<img src=\"${imgurl}\"  style=\"width: 3rem; height: 3.5rem;vertical-align: middle;\">";
                imgText = imgText.replace("${imgurl}",text);
               msg.setSendtext(imgText);
            }
        }

        return ApiResult.ok(ipage);
    }


    /***
     * Ajax上传web界面js录制的音频数据
     * */
    @PostMapping("/chat/audio")
    @ResponseBody
    public JSONObject upaudio(@RequestParam(value = "file") MultipartFile file) throws IOException {
        JSONObject res = new JSONObject();
        JSONObject resUrl = new JSONObject();
        LocalDate today = LocalDate.now();
        Instant timestamp = Instant.now();
        String filenames = today  + String.valueOf(timestamp.toEpochMilli()) + ".mp3";
        String pathname = "D:\\chat\\" + filenames;
        file.transferTo(new File(pathname));
        resUrl.put("src", "/pic/"+filenames);
        res.put("msg", "");
        res.put("data", resUrl);
        return res;
    }


    @GetMapping("/chat/userInfo/{uidOther}")
    @ApiOperation("查询本人和对方的名称和头像")
    public ApiResult<Object> getChatUserInfo(@PathVariable Integer uidOther){

        YxUserQueryVo userQueryVo_other = yxUserService.getYxUserById(uidOther);
        Integer uidSelf = SecurityUtils.getUserId().intValue();

        YxUserQueryVo userQueryVo_self = yxUserService.getYxUserById(uidSelf);

        ChatUserInfo chatUserInfo = new ChatUserInfo();
        chatUserInfo.setUidSelf(uidSelf);
        chatUserInfo.setNameSelf(userQueryVo_self.getNickname());
        chatUserInfo.setImageSelf(userQueryVo_self.getAvatar());

        chatUserInfo.setUidOther(uidOther);
        chatUserInfo.setNameOther(userQueryVo_other.getNickname());
        chatUserInfo.setImageOther(userQueryVo_other.getAvatar());

        return ApiResult.ok(chatUserInfo);
    }

    /**
     * 未读消息
     */
    @PostMapping("/chat/unread/{senduserid}")
    @ApiOperation(value = "未读消息对象",notes = "未读消息",response = ApiResult.class)
    public ApiResult<Boolean> unreadMsg(@PathVariable("senduserid")Integer senduserid) throws Exception{

        Integer reviceuserid = SecurityUtils.getUserId().intValue();
        log.info(" chatCtrl 未读消息保存：senduserid={},reviceuserid={}",senduserid,reviceuserid);
       // chatMsgService.InsertUnread(senduserid,reviceuserid);
        return ApiResult.ok();
    }


    /***
     * 查询群聊的聊天记录
     * */
    @PostMapping("/chat/group/lkuschatmsg")
    @ResponseBody
    @AnonymousAccess
    public ApiResult<IPage<ChatGroupMsg>> lkfriends(HttpSession session, @RequestBody ChatGroupMsg chatMsg){

        return ApiResult.ok(chatGroupMsgService.LookGroupMsg(chatMsg));
    }


    /**
     * 未读消息
     */
    @PostMapping("/chat/group/unread/{groupId}")
    @ApiOperation(value = "未读消息对象",notes = "未读消息",response = ApiResult.class)
    public ApiResult<Boolean> unreadMsgGroup(@PathVariable("groupId")Integer groupId) throws Exception{
        Integer uid = SecurityUtils.getUserId().intValue();
        chatGroupMsgService.InsertUnreadGroup(uid,groupId);
        return ApiResult.ok();
    }



    @GetMapping("/chat/unreadCount")
    @ApiOperation("查询与某人的未读消息数")
    public ApiResult<Object> queryUnreadCountByBothSides(@RequestParam(value = "",required = false) Integer sendUserid){
        Integer reviceuserid = SecurityUtils.getUserId().intValue();
        Integer unreadCount = chatMsgService.queryUnreadCountByBothSides(sendUserid,reviceuserid);

        return ApiResult.ok(unreadCount);
    }

    @GetMapping("/chat/unreadCountTTL")
    @ApiOperation("查询本人未读消息数")
    public ApiResult<Object> queryUnreadCountByReviceuser(){
        Integer reviceuserid = -1;
        try {
             reviceuserid = SecurityUtils.getUserId().intValue();
        } catch (Exception e) {

        }

        Integer unreadCount = chatMsgService.queryUnreadCountByReviceuser(reviceuserid);

        return ApiResult.ok(unreadCount);
    }
}
