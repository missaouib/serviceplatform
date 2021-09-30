package co.yixiang.modules.yaoshitong.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import co.yixiang.modules.yaoshitong.entity.ChatGroupMsg;
import co.yixiang.modules.yaoshitong.web.param.ChatGroupMsgQueryParam;
import co.yixiang.modules.yaoshitong.web.vo.ChatGroupMsgQueryVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.io.Serializable;

/**
 * <p>
 * 聊天组群聊天记录 Mapper 接口
 * </p>
 *
 * @author visa
 * @since 2020-09-02
 */
@Repository
public interface ChatGroupMsgMapper extends BaseMapper<ChatGroupMsg> {

    /**
     * 根据ID获取查询对象
     * @param id
     * @return
     */
    ChatGroupMsgQueryVo getChatGroupMsgById(Serializable id);

    /**
     * 获取分页对象
     * @param page
     * @param chatGroupMsgQueryParam
     * @return
     */
    IPage<ChatGroupMsgQueryVo> getChatGroupMsgPageList(@Param("page") Page page, @Param("param") ChatGroupMsgQueryParam chatGroupMsgQueryParam);

}
