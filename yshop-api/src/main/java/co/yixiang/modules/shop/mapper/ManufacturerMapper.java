package co.yixiang.modules.shop.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import co.yixiang.modules.shop.entity.Manufacturer;
import co.yixiang.modules.shop.web.param.ManufacturerQueryParam;
import co.yixiang.modules.shop.web.vo.ManufacturerQueryVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.io.Serializable;

/**
 * <p>
 * 生产厂家主数据表 Mapper 接口
 * </p>
 *
 * @author visa
 * @since 2020-12-07
 */
@Repository
public interface ManufacturerMapper extends BaseMapper<Manufacturer> {

    /**
     * 根据ID获取查询对象
     * @param id
     * @return
     */
    ManufacturerQueryVo getManufacturerById(Serializable id);

    /**
     * 获取分页对象
     * @param page
     * @param manufacturerQueryParam
     * @return
     */
    IPage<ManufacturerQueryVo> getManufacturerPageList(@Param("page") Page page, @Param("param") ManufacturerQueryParam manufacturerQueryParam);

}
