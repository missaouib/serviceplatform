package co.yixiang.modules.shop.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import co.yixiang.modules.shop.entity.RocheHospital;
import co.yixiang.modules.shop.web.param.RocheHospitalQueryParam;
import co.yixiang.modules.shop.web.vo.RocheHospitalQueryVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.io.Serializable;

/**
 * <p>
 * 罗氏罕见病sma医院列表 Mapper 接口
 * </p>
 *
 * @author visa
 * @since 2021-02-05
 */
@Repository
public interface RocheHospitalMapper extends BaseMapper<RocheHospital> {

    /**
     * 根据ID获取查询对象
     * @param id
     * @return
     */
    RocheHospitalQueryVo getRocheHospitalById(Serializable id);

    /**
     * 获取分页对象
     * @param page
     * @param rocheHospitalQueryParam
     * @return
     */
    IPage<RocheHospitalQueryVo> getRocheHospitalPageList(@Param("page") Page page, @Param("param") RocheHospitalQueryParam rocheHospitalQueryParam);

}
