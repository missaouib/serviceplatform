package co.yixiang.modules.shop.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import co.yixiang.modules.shop.entity.Hospital;
import co.yixiang.modules.shop.web.param.HospitalQueryParam;
import co.yixiang.modules.shop.web.vo.HospitalQueryVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.io.Serializable;

/**
 * <p>
 * 医院 Mapper 接口
 * </p>
 *
 * @author visa
 * @since 2021-06-11
 */
@Repository
public interface HospitalMapper extends BaseMapper<Hospital> {

    /**
     * 根据ID获取查询对象
     * @param id
     * @return
     */
    HospitalQueryVo getHospitalById(Serializable id);

    /**
     * 获取分页对象
     * @param page
     * @param hospitalQueryParam
     * @return
     */
    IPage<HospitalQueryVo> getHospitalPageList(@Param("page") Page page, @Param("param") HospitalQueryParam hospitalQueryParam);

}
