package co.yixiang.modules.yaoshitong.service;

import co.yixiang.modules.yaoshitong.entity.ChatGroup;
import co.yixiang.common.service.BaseService;
import co.yixiang.modules.yaoshitong.entity.ChatGroupMsg;
import co.yixiang.modules.yaoshitong.web.param.ChatGroupQueryParam;
import co.yixiang.modules.yaoshitong.web.vo.ChatGroupQueryVo;
import co.yixiang.common.web.vo.Paging;
import com.baomidou.mybatisplus.core.metadata.IPage;

import java.io.Serializable;

/**
 * <p>
 * 聊天群组 服务类
 * </p>
 *
 * @author visa
 * @since 2020-09-02
 */
public interface ChatGroupService extends BaseService<ChatGroup> {

    /**
     * 根据ID获取查询对象
     * @param id
     * @return
     */
    ChatGroup getChatGroupById(Serializable id) throws Exception;

    /**
     * 获取分页对象
     * @param chatGroupQueryParam
     * @return
     */
    Paging<ChatGroup> getChatGroupPageList(ChatGroupQueryParam chatGroupQueryParam) throws Exception;

    boolean saveChatGroup(ChatGroup chatGroup);

}
