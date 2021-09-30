package co.yixiang.modules.yaoshitong.service.impl;

import co.yixiang.common.utils.QueryHelpPlus;
import co.yixiang.modules.yaoshitong.entity.ChatGroupMember;
import co.yixiang.modules.yaoshitong.mapper.ChatGroupMemberMapper;
import co.yixiang.modules.yaoshitong.service.ChatGroupMemberService;
import co.yixiang.modules.yaoshitong.web.param.ChatGroupMemberQueryParam;
import co.yixiang.modules.yaoshitong.web.vo.ChatGroupMemberQueryVo;
import co.yixiang.common.service.impl.BaseServiceImpl;
import co.yixiang.common.web.vo.Paging;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import java.io.Serializable;


/**
 * <p>
 * 聊天群组成员 服务实现类
 * </p>
 *
 * @author visa
 * @since 2020-09-02
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class ChatGroupMemberServiceImpl extends BaseServiceImpl<ChatGroupMemberMapper, ChatGroupMember> implements ChatGroupMemberService {

    @Autowired
    private ChatGroupMemberMapper chatGroupMemberMapper;

    @Override
    public ChatGroupMemberQueryVo getChatGroupMemberById(Serializable id) throws Exception{
        return chatGroupMemberMapper.getChatGroupMemberById(id);
    }

    @Override
    public Paging<ChatGroupMemberQueryVo> getChatGroupMemberPageList(ChatGroupMemberQueryParam chatGroupMemberQueryParam) throws Exception{
        Page page = setPageParam(chatGroupMemberQueryParam,OrderItem.desc("create_time"));
        QueryWrapper queryWrapper = QueryHelpPlus.getPredicate(ChatGroupMemberQueryParam.class, chatGroupMemberQueryParam);
        IPage<ChatGroupMember> iPage = chatGroupMemberMapper.selectPage(page,queryWrapper);
        return new Paging(iPage);
    }

}
