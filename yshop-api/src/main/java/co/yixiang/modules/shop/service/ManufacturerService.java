package co.yixiang.modules.shop.service;

import co.yixiang.modules.shop.entity.Manufacturer;
import co.yixiang.common.service.BaseService;
import co.yixiang.modules.shop.web.param.ManufacturerQueryParam;
import co.yixiang.modules.shop.web.vo.ManufacturerQueryVo;
import co.yixiang.common.web.vo.Paging;

import java.io.Serializable;

/**
 * <p>
 * 生产厂家主数据表 服务类
 * </p>
 *
 * @author visa
 * @since 2020-12-07
 */
public interface ManufacturerService extends BaseService<Manufacturer> {

    /**
     * 根据ID获取查询对象
     * @param id
     * @return
     */
    ManufacturerQueryVo getManufacturerById(Serializable id) throws Exception;

    /**
     * 获取分页对象
     * @param manufacturerQueryParam
     * @return
     */
    Paging<ManufacturerQueryVo> getManufacturerPageList(ManufacturerQueryParam manufacturerQueryParam) throws Exception;

}
