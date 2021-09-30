package co.yixiang.modules.shop.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import co.yixiang.modules.shop.entity.MedCalculatorDetail;
import co.yixiang.modules.shop.web.param.MedCalculatorDetailQueryParam;
import co.yixiang.modules.shop.web.vo.MedCalculatorDetailQueryVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.io.Serializable;

/**
 * <p>
 * 用药计算器用药量变更表 Mapper 接口
 * </p>
 *
 * @author visa
 * @since 2021-01-12
 */
@Repository
public interface MedCalculatorDetailMapper extends BaseMapper<MedCalculatorDetail> {

    /**
     * 根据ID获取查询对象
     * @param id
     * @return
     */
    MedCalculatorDetailQueryVo getMedCalculatorDetailById(Serializable id);

    /**
     * 获取分页对象
     * @param page
     * @param medCalculatorDetailQueryParam
     * @return
     */
    IPage<MedCalculatorDetailQueryVo> getMedCalculatorDetailPageList(@Param("page") Page page, @Param("param") MedCalculatorDetailQueryParam medCalculatorDetailQueryParam);

}
