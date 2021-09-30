package co.yixiang.modules.shop.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import co.yixiang.modules.shop.entity.YxExpressTemplateDetail;
import co.yixiang.modules.shop.web.param.YxExpressTemplateDetailQueryParam;
import co.yixiang.modules.shop.web.vo.YxExpressTemplateDetailQueryVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.io.Serializable;

/**
 * <p>
 * 物流运费模板明细 Mapper 接口
 * </p>
 *
 * @author visa
 * @since 2020-11-28
 */
@Repository
public interface YxExpressTemplateDetailMapper extends BaseMapper<YxExpressTemplateDetail> {

    /**
     * 根据ID获取查询对象
     * @param id
     * @return
     */
    YxExpressTemplateDetailQueryVo getYxExpressTemplateDetailById(Serializable id);

    /**
     * 获取分页对象
     * @param page
     * @param yxExpressTemplateDetailQueryParam
     * @return
     */
    IPage<YxExpressTemplateDetailQueryVo> getYxExpressTemplateDetailPageList(@Param("page") Page page, @Param("param") YxExpressTemplateDetailQueryParam yxExpressTemplateDetailQueryParam);

}
