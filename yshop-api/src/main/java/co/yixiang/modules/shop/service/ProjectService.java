package co.yixiang.modules.shop.service;

import co.yixiang.modules.shop.entity.Project;
import co.yixiang.common.service.BaseService;
import co.yixiang.modules.shop.web.param.ProjectQueryParam;
import co.yixiang.modules.shop.web.vo.ProjectQueryVo;
import co.yixiang.common.web.vo.Paging;

import java.io.Serializable;

/**
 * <p>
 * 项目 服务类
 * </p>
 *
 * @author visa
 * @since 2021-03-05
 */
public interface ProjectService extends BaseService<Project> {

    /**
     * 根据ID获取查询对象
     * @param id
     * @return
     */
    ProjectQueryVo getProjectById(Serializable id) throws Exception;

    /**
     * 获取分页对象
     * @param projectQueryParam
     * @return
     */
    Paging<ProjectQueryVo> getProjectPageList(ProjectQueryParam projectQueryParam) throws Exception;

}
