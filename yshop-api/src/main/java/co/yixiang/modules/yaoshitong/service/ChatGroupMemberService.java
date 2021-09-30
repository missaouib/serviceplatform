package co.yixiang.modules.yaoshitong.service;

import co.yixiang.modules.yaoshitong.entity.ChatGroupMember;
import co.yixiang.common.service.BaseService;
import co.yixiang.modules.yaoshitong.web.param.ChatGroupMemberQueryParam;
import co.yixiang.modules.yaoshitong.web.vo.ChatGroupMemberQueryVo;
import co.yixiang.common.web.vo.Paging;

import java.io.Serializable;

/**
 * <p>
 * 聊天群组成员 服务类
 * </p>
 *
 * @author visa
 * @since 2020-09-02
 */
public interface ChatGroupMemberService extends BaseService<ChatGroupMember> {

    /**
     * 根据ID获取查询对象
     * @param id
     * @return
     */
    ChatGroupMemberQueryVo getChatGroupMemberById(Serializable id) throws Exception;

    /**
     * 获取分页对象
     * @param chatGroupMemberQueryParam
     * @return
     */
    Paging<ChatGroupMemberQueryVo> getChatGroupMemberPageList(ChatGroupMemberQueryParam chatGroupMemberQueryParam) throws Exception;

}
