package co.yixiang.modules.yaoshitong.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import co.yixiang.modules.yaoshitong.entity.ChatGroupMember;
import co.yixiang.modules.yaoshitong.web.param.ChatGroupMemberQueryParam;
import co.yixiang.modules.yaoshitong.web.vo.ChatGroupMemberQueryVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.io.Serializable;

/**
 * <p>
 * 聊天群组成员 Mapper 接口
 * </p>
 *
 * @author visa
 * @since 2020-09-02
 */
@Repository
public interface ChatGroupMemberMapper extends BaseMapper<ChatGroupMember> {

    /**
     * 根据ID获取查询对象
     * @param id
     * @return
     */
    ChatGroupMemberQueryVo getChatGroupMemberById(Serializable id);

    /**
     * 获取分页对象
     * @param page
     * @param chatGroupMemberQueryParam
     * @return
     */
    IPage<ChatGroupMemberQueryVo> getChatGroupMemberPageList(@Param("page") Page page, @Param("param") ChatGroupMemberQueryParam chatGroupMemberQueryParam);

}
