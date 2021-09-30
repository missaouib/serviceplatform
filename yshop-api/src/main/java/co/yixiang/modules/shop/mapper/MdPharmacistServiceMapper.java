package co.yixiang.modules.shop.mapper;

import co.yixiang.modules.yaoshitong.entity.YaoshitongPatient;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import co.yixiang.modules.shop.entity.MdPharmacistService;
import co.yixiang.modules.shop.web.param.MdPharmacistServiceQueryParam;
import co.yixiang.modules.shop.web.vo.MdPharmacistServiceQueryVo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.io.Serializable;

/**
 * <p>
 * 药师在线配置表 Mapper 接口
 * </p>
 *
 * @author visazhou
 * @since 2020-06-09
 */
@Repository
public interface MdPharmacistServiceMapper extends BaseMapper<MdPharmacistService> {

    /**
     * 根据ID获取查询对象
     * @param id
     * @return
     */
    MdPharmacistServiceQueryVo getMdPharmacistServiceById(Serializable id);

    /**
     * 获取分页对象
     * @param page
     * @param mdPharmacistServiceQueryParam
     * @return
     */
    IPage<MdPharmacistServiceQueryVo> getMdPharmacistServicePageList(@Param("page") Page page, @Param("param") MdPharmacistServiceQueryParam mdPharmacistServiceQueryParam);

    @Select("select a.* FROM md_pharmacist_service  a , yaoshitong_patient_relation b ${ew.customSqlSegment}   ")
    IPage<MdPharmacistService> getPharmacistPageList(@Param("page") Page page, @Param(Constants.WRAPPER) Wrapper<MdPharmacistService> wrapper );
}
