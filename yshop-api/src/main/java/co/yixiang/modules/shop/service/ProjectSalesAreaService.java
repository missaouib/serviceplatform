package co.yixiang.modules.shop.service;

import co.yixiang.modules.shop.entity.ProjectSalesArea;
import co.yixiang.common.service.BaseService;
import co.yixiang.modules.shop.web.param.ProjectSalesAreaQueryParam;
import co.yixiang.modules.shop.web.vo.ProjectSalesAreaQueryVo;
import co.yixiang.common.web.vo.Paging;

import java.io.Serializable;

/**
 * <p>
 * 项目配置销售省份 服务类
 * </p>
 *
 * @author visa
 * @since 2021-04-12
 */
public interface ProjectSalesAreaService extends BaseService<ProjectSalesArea> {

    /**
     * 根据ID获取查询对象
     * @param id
     * @return
     */
    ProjectSalesAreaQueryVo getProjectSalesAreaById(Serializable id) throws Exception;

    /**
     * 获取分页对象
     * @param projectSalesAreaQueryParam
     * @return
     */
    Paging<ProjectSalesAreaQueryVo> getProjectSalesAreaPageList(ProjectSalesAreaQueryParam projectSalesAreaQueryParam) throws Exception;

}
