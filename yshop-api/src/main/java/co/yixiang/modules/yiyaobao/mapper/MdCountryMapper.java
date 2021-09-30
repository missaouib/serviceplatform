package co.yixiang.modules.yiyaobao.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import co.yixiang.modules.yiyaobao.entity.MdCountry;
import co.yixiang.modules.yiyaobao.web.param.MdCountryQueryParam;
import co.yixiang.modules.yiyaobao.web.vo.MdCountryQueryVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.io.Serializable;

/**
 * <p>
 * 国家地区信息表 Mapper 接口
 * </p>
 *
 * @author visazhou
 * @since 2020-05-16
 */
@Repository
public interface MdCountryMapper extends BaseMapper<MdCountry> {

    /**
     * 根据ID获取查询对象
     * @param id
     * @return
     */
    MdCountryQueryVo getMdCountryById(Serializable id);

    /**
     * 获取分页对象
     * @param page
     * @param mdCountryQueryParam
     * @return
     */
    IPage<MdCountryQueryVo> getMdCountryPageList(@Param("page") Page page, @Param("param") MdCountryQueryParam mdCountryQueryParam);

}
