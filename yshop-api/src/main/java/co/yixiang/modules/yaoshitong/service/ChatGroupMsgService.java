package co.yixiang.modules.yaoshitong.service;

import co.yixiang.modules.yaoshitong.entity.ChatGroup;
import co.yixiang.modules.yaoshitong.entity.ChatGroupMsg;
import co.yixiang.common.service.BaseService;
import co.yixiang.modules.yaoshitong.web.param.ChatGroupMsgQueryParam;
import co.yixiang.modules.yaoshitong.web.vo.ChatGroupMsgQueryVo;
import co.yixiang.common.web.vo.Paging;
import co.yixiang.modules.yaoshitong.web.vo.ChatMsg;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.scheduling.annotation.Async;

import java.io.Serializable;

/**
 * <p>
 * 聊天组群聊天记录 服务类
 * </p>
 *
 * @author visa
 * @since 2020-09-02
 */
public interface ChatGroupMsgService extends BaseService<ChatGroupMsg> {

    /**
     * 根据ID获取查询对象
     * @param id
     * @return
     */
    ChatGroupMsgQueryVo getChatGroupMsgById(Serializable id) throws Exception;

    /**
     * 获取分页对象
     * @param chatGroupMsgQueryParam
     * @return
     */
    Paging<ChatGroupMsgQueryVo> getChatGroupMsgPageList(ChatGroupMsgQueryParam chatGroupMsgQueryParam) throws Exception;


    void InsertChatGroupMsg(ChatGroupMsg chatGroupMsg);

    IPage<ChatGroupMsg> LookGroupMsg(ChatGroupMsg chatGroupMsg);

    void InsertUnreadGroup(Integer reviceuserid,Integer groupId);
}
