package co.yixiang.modules.manage.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import co.yixiang.modules.manage.entity.Charities;
import co.yixiang.modules.manage.web.param.CharitiesQueryParam;
import co.yixiang.modules.manage.web.vo.CharitiesQueryVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.io.Serializable;

/**
 * <p>
 * 慈善活动表 Mapper 接口
 * </p>
 *
 * @author visa
 * @since 2020-08-20
 */
@Repository
public interface CharitiesMapper extends BaseMapper<Charities> {

    /**
     * 根据ID获取查询对象
     * @param id
     * @return
     */
    CharitiesQueryVo getCharitiesById(Serializable id);

    /**
     * 获取分页对象
     * @param page
     * @param charitiesQueryParam
     * @return
     */
    IPage<CharitiesQueryVo> getCharitiesPageList(@Param("page") Page page, @Param("param") CharitiesQueryParam charitiesQueryParam);

}
