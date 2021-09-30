package co.yixiang.modules.yaoshitong.service;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import co.yixiang.modules.user.service.YxUserService;
import co.yixiang.modules.user.web.vo.YxUserQueryVo;
import co.yixiang.modules.yaoshitong.entity.ChatGroupMsg;
import co.yixiang.modules.yaoshitong.entity.YaoshitongPatient;
import co.yixiang.modules.yaoshitong.mapper.ChatGroupMsgMapper;
import co.yixiang.modules.yaoshitong.web.vo.ChatMsg;
import co.yixiang.modules.yaoshitong.mapper.ChatMsgMapper;
import co.yixiang.utils.RedisUtils;
import co.yixiang.utils.SecurityUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.xiaoymin.knife4j.spring.model.SwaggerBootstrapUiPath;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
public class ChatMsgService {
    @Autowired
    ChatMsgMapper chatMsgMapper;


    @Autowired
    YxUserService yxUserService;

    @Autowired
    RedisUtils redisUtils;

    @Async
    public void InsertChatMsg(ChatMsg chatMsg){
        chatMsgMapper.InsertChatMsg(chatMsg);
    }
    public IPage<ChatMsg> LookTwoUserMsg(ChatMsg chatMsg){
       // YxUserQueryVo user1 = yxUserService.getYxUserById(chatMsg.getReciveuserid());
       // YxUserQueryVo user2 = yxUserService.getYxUserById(chatMsg.getSenduserid());

        Integer senduserid = Integer.valueOf(chatMsg.getSenduserid());
        Integer reviceuserid = Integer.valueOf(chatMsg.getReciveuserid());

        String key = "msgUnread-"+senduserid+"-"+reviceuserid;

        redisUtils.del(key);
        Page<ChatMsg> pageModel = new Page<>(chatMsg.getPage(),
                chatMsg.getLimit());

        QueryWrapper<ChatMsg> queryWrapper = new QueryWrapper();
        queryWrapper.apply(" (senduserid= {0} and reciveuserid={1} ) or\n" +
                "    (senduserid=  {2} and reciveuserid= {3})",
                chatMsg.getSenduserid(),chatMsg.getReciveuserid(),chatMsg.getReciveuserid(),chatMsg.getSenduserid()
        );
        queryWrapper.orderByDesc("sendtime");
        IPage<ChatMsg> pageList = chatMsgMapper.selectPage(pageModel,queryWrapper);


        pageList.getRecords().sort(new Comparator<ChatMsg>() {
            @Override
            public int compare(ChatMsg o1, ChatMsg o2) {
                return o1.getSendtime().compareTo(o2.getSendtime());
            }
        });

      //  List<ChatMsg> chatMsgLlist = chatMsgMapper.LookTwoUserMsg(chatMsg);

        return pageList;
    }

    public void InsertUnread(Integer senduserid,Integer reviceuserid){
        String key = "msgUnread-"+senduserid+"-"+reviceuserid;
        redisUtils.incr(key,1);


    }


    public Integer queryUnreadCountByBothSides(Integer senduserid,Integer reviceuserid) {
        String key = "msgUnread-"+senduserid+"-"+reviceuserid;

        Object o = redisUtils.get(key);
        if(ObjectUtil.isEmpty(o)) {
            return 0;
        } else {
            return Integer.valueOf(String.valueOf(o));
        }
    }


    public Integer queryUnreadCountByReviceuser(Integer reviceuserid) {
        String key = "msgUnread-*-"+reviceuserid;

        List<String> list = redisUtils.scan(key);
        if(ObjectUtil.isEmpty(list)) {
            return 0;
        } else {
            int a = 0;
            String ignoreKey = "msgUnread-0-"+reviceuserid;
            for(String o:list) {

                if(ignoreKey.equals(o)) {
                    continue;
                }

                Object unreadCount = redisUtils.get(o);
                a = a+ Integer.valueOf(String.valueOf(unreadCount));
            }
            return a;
        }
    }
}
