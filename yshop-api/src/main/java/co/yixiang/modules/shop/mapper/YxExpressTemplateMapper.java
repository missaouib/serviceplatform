package co.yixiang.modules.shop.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import co.yixiang.modules.shop.entity.YxExpressTemplate;
import co.yixiang.modules.shop.web.param.YxExpressTemplateQueryParam;
import co.yixiang.modules.shop.web.vo.YxExpressTemplateQueryVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.io.Serializable;

/**
 * <p>
 * 物流运费模板 Mapper 接口
 * </p>
 *
 * @author visa
 * @since 2020-11-28
 */
@Repository
public interface YxExpressTemplateMapper extends BaseMapper<YxExpressTemplate> {

    /**
     * 根据ID获取查询对象
     * @param id
     * @return
     */
    YxExpressTemplateQueryVo getYxExpressTemplateById(Serializable id);

    /**
     * 获取分页对象
     * @param page
     * @param yxExpressTemplateQueryParam
     * @return
     */
    IPage<YxExpressTemplateQueryVo> getYxExpressTemplatePageList(@Param("page") Page page, @Param("param") YxExpressTemplateQueryParam yxExpressTemplateQueryParam);

}
