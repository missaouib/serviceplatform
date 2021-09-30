package co.yixiang.modules.shop.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import co.yixiang.modules.shop.entity.MedCalculator;
import co.yixiang.modules.shop.web.param.MedCalculatorQueryParam;
import co.yixiang.modules.shop.web.vo.MedCalculatorQueryVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.io.Serializable;

/**
 * <p>
 * 用药计算器 Mapper 接口
 * </p>
 *
 * @author visa
 * @since 2021-01-03
 */
@Repository
public interface MedCalculatorMapper extends BaseMapper<MedCalculator> {

    /**
     * 根据ID获取查询对象
     * @param id
     * @return
     */
    MedCalculatorQueryVo getMedCalculatorById(Serializable id);

    /**
     * 获取分页对象
     * @param page
     * @param medCalculatorQueryParam
     * @return
     */
    IPage<MedCalculatorQueryVo> getMedCalculatorPageList(@Param("page") Page page, @Param("param") MedCalculatorQueryParam medCalculatorQueryParam);

}
