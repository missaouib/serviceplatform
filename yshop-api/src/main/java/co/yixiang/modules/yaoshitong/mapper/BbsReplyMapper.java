package co.yixiang.modules.yaoshitong.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import co.yixiang.modules.yaoshitong.entity.BbsReply;
import co.yixiang.modules.yaoshitong.web.param.BbsReplyQueryParam;
import co.yixiang.modules.yaoshitong.web.vo.BbsReplyQueryVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.io.Serializable;

/**
 * <p>
 * 帖子回复表 Mapper 接口
 * </p>
 *
 * @author visa
 * @since 2020-07-27
 */
@Repository
public interface BbsReplyMapper extends BaseMapper<BbsReply> {

    /**
     * 根据ID获取查询对象
     * @param id
     * @return
     */
    BbsReplyQueryVo getBbsReplyById(Serializable id);

    /**
     * 获取分页对象
     * @param page
     * @param bbsReplyQueryParam
     * @return
     */
    IPage<BbsReplyQueryVo> getBbsReplyPageList(@Param("page") Page page, @Param("param") BbsReplyQueryParam bbsReplyQueryParam);

}
