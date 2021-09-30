package co.yixiang.modules.shop.service;

import co.yixiang.modules.shop.entity.Product4project;
import co.yixiang.common.service.BaseService;
import co.yixiang.modules.shop.web.dto.Data4ProjectDTO;
import co.yixiang.modules.shop.web.dto.SpecialProjectDTO;
import co.yixiang.modules.shop.web.param.Product4projectQueryParam;
import co.yixiang.modules.shop.web.vo.Product4projectQueryVo;
import co.yixiang.common.web.vo.Paging;

import java.io.Serializable;
import java.util.List;

/**
 * <p>
 * 项目对应的药品 服务类
 * </p>
 *
 * @author visazhou
 * @since 2020-06-11
 */
public interface Product4projectService extends BaseService<Product4project> {

    /**
     * 根据ID获取查询对象
     * @param id
     * @return
     */
    Product4projectQueryVo getProduct4projectById(Serializable id) throws Exception;

    /**
     * 获取分页对象
     * @param product4projectQueryParam
     * @return
     */
    Paging<Product4projectQueryVo> getProduct4projectPageList(Product4projectQueryParam product4projectQueryParam) throws Exception;

    List<SpecialProjectDTO> querySpecialProject() ;

    Data4ProjectDTO queryData(Product4projectQueryParam product4projectQueryParam);

}
