package co.yixiang.modules.yaoshitong.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import co.yixiang.modules.yaoshitong.entity.ChatGroup;
import co.yixiang.modules.yaoshitong.web.param.ChatGroupQueryParam;
import co.yixiang.modules.yaoshitong.web.vo.ChatGroupQueryVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.io.Serializable;

/**
 * <p>
 * 聊天群组 Mapper 接口
 * </p>
 *
 * @author visa
 * @since 2020-09-02
 */
@Repository
public interface ChatGroupMapper extends BaseMapper<ChatGroup> {

    /**
     * 根据ID获取查询对象
     * @param id
     * @return
     */
    ChatGroupQueryVo getChatGroupById(Serializable id);

    /**
     * 获取分页对象
     * @param page
     * @param chatGroupQueryParam
     * @return
     */
    IPage<ChatGroupQueryVo> getChatGroupPageList(@Param("page") Page page, @Param("param") ChatGroupQueryParam chatGroupQueryParam);

}
