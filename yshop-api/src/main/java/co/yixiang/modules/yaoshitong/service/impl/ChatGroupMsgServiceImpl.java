package co.yixiang.modules.yaoshitong.service.impl;

import co.yixiang.common.utils.QueryHelpPlus;
import co.yixiang.modules.user.entity.YxUser;
import co.yixiang.modules.user.service.YxUserService;
import co.yixiang.modules.yaoshitong.entity.ChatGroupMsg;
import co.yixiang.modules.yaoshitong.mapper.ChatGroupMsgMapper;
import co.yixiang.modules.yaoshitong.service.ChatGroupMsgService;
import co.yixiang.modules.yaoshitong.web.param.ChatGroupMsgQueryParam;
import co.yixiang.modules.yaoshitong.web.vo.ChatGroupMsgQueryVo;
import co.yixiang.common.service.impl.BaseServiceImpl;
import co.yixiang.common.web.vo.Paging;
import co.yixiang.utils.RedisUtils;
import co.yixiang.utils.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import java.io.Serializable;
import java.util.Comparator;


/**
 * <p>
 * 聊天组群聊天记录 服务实现类
 * </p>
 *
 * @author visa
 * @since 2020-09-02
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class ChatGroupMsgServiceImpl extends BaseServiceImpl<ChatGroupMsgMapper, ChatGroupMsg> implements ChatGroupMsgService {

    @Autowired
    private ChatGroupMsgMapper chatGroupMsgMapper;
    @Autowired
    RedisUtils redisUtils;

    @Autowired
    private YxUserService yxUserService;

    @Override
    public ChatGroupMsgQueryVo getChatGroupMsgById(Serializable id) throws Exception{
        return chatGroupMsgMapper.getChatGroupMsgById(id);
    }

    @Override
    public Paging<ChatGroupMsgQueryVo> getChatGroupMsgPageList(ChatGroupMsgQueryParam chatGroupMsgQueryParam) throws Exception{
        Page page = setPageParam(chatGroupMsgQueryParam,OrderItem.desc("create_time"));
        QueryWrapper queryWrapper = QueryHelpPlus.getPredicate(ChatGroupMsgQueryParam.class, chatGroupMsgQueryParam);
        IPage<ChatGroupMsg> iPage = chatGroupMsgMapper.selectPage(page,queryWrapper);
        return new Paging(iPage);
    }
    @Async
    @Override
    public void InsertChatGroupMsg(ChatGroupMsg chatMsg){
        chatGroupMsgMapper.insert(chatMsg);
    }

    @Override
    public IPage<ChatGroupMsg> LookGroupMsg(ChatGroupMsg chatMsg){

        Integer uid = SecurityUtils.getUserId().intValue();
        String key = "msgUnread-group-"+chatMsg.getGroupId() + "-" + uid;

        redisUtils.del(key);
        Page<ChatGroupMsg> pageModel = new Page<>(chatMsg.getPage(),
                chatMsg.getLimit());

        QueryWrapper<ChatGroupMsg> queryWrapper = new QueryWrapper();
        queryWrapper.eq("group_id",chatMsg.getGroupId());
        queryWrapper.orderByDesc("send_time");
        IPage<ChatGroupMsg> pageList = chatGroupMsgMapper.selectPage(pageModel,queryWrapper);

        for(ChatGroupMsg chatGroupMsg:pageList.getRecords()) {
            YxUser yxUser = yxUserService.getById(chatGroupMsg.getSendUid());

            chatGroupMsg.setSendName(yxUser.getNickname());
            chatGroupMsg.setSendAvatar(yxUser.getAvatar());
            if(uid.equals(chatGroupMsg.getSendUid())) {
                chatGroupMsg.setIsSelfRecord(true);
            }else {
                chatGroupMsg.setIsSelfRecord(false);
            }
        }

        pageList.getRecords().sort(new Comparator<ChatGroupMsg>() {
            @Override
            public int compare(ChatGroupMsg o1, ChatGroupMsg o2) {
                return o1.getSendTime().compareTo(o2.getSendTime());
            }
        });

        // List<ChatMsg> chatMsgLlist = chatMsgMapper.LookTwoUserMsg(chatMsg);

        return pageList;
    }

    @Override
    public void InsertUnreadGroup(Integer reviceuserid,Integer groupId){
        String key = "msgUnread-group-"+groupId+"-"+reviceuserid;
        redisUtils.incr(key,1);
    }


}
