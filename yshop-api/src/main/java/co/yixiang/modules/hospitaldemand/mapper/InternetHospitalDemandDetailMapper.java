package co.yixiang.modules.hospitaldemand.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import co.yixiang.modules.hospitaldemand.entity.InternetHospitalDemandDetail;
import co.yixiang.modules.hospitaldemand.web.param.InternetHospitalDemandDetailQueryParam;
import co.yixiang.modules.hospitaldemand.web.vo.InternetHospitalDemandDetailQueryVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.io.Serializable;

/**
 * <p>
 * 互联网医院导入的需求单药品明细 Mapper 接口
 * </p>
 *
 * @author visa
 * @since 2020-12-04
 */
@Repository
public interface InternetHospitalDemandDetailMapper extends BaseMapper<InternetHospitalDemandDetail> {

    /**
     * 根据ID获取查询对象
     * @param id
     * @return
     */
    InternetHospitalDemandDetailQueryVo getInternetHospitalDemandDetailById(Serializable id);

    /**
     * 获取分页对象
     * @param page
     * @param internetHospitalDemandDetailQueryParam
     * @return
     */
    IPage<InternetHospitalDemandDetailQueryVo> getInternetHospitalDemandDetailPageList(@Param("page") Page page, @Param("param") InternetHospitalDemandDetailQueryParam internetHospitalDemandDetailQueryParam);

}
