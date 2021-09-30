package co.yixiang.modules.shop.web.controller;

import co.yixiang.modules.shop.entity.Project;
import co.yixiang.modules.shop.service.ProjectService;
import co.yixiang.modules.shop.web.param.ProjectQueryParam;
import co.yixiang.modules.shop.web.vo.ProjectQueryVo;
import co.yixiang.common.web.controller.BaseController;
import co.yixiang.common.api.ApiResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import co.yixiang.common.web.vo.Paging;
import co.yixiang.common.web.param.IdParam;

/**
 * <p>
 * 项目 前端控制器
 * </p>
 *
 * @author visa
 * @since 2021-03-05
 */
@Slf4j
@RestController
@RequestMapping("/project")
@Api("项目 API")
public class ProjectController extends BaseController {

    @Autowired
    private ProjectService projectService;

    /**
    * 添加项目
    */
    @PostMapping("/add")
    @ApiOperation(value = "添加Project对象",notes = "添加项目",response = ApiResult.class)
    public ApiResult<Boolean> addProject(@Valid @RequestBody Project project) throws Exception{
        boolean flag = projectService.save(project);
        return ApiResult.result(flag);
    }

    /**
    * 修改项目
    */
    @PostMapping("/update")
    @ApiOperation(value = "修改Project对象",notes = "修改项目",response = ApiResult.class)
    public ApiResult<Boolean> updateProject(@Valid @RequestBody Project project) throws Exception{
        boolean flag = projectService.updateById(project);
        return ApiResult.result(flag);
    }

    /**
    * 删除项目
    */
    @PostMapping("/delete")
    @ApiOperation(value = "删除Project对象",notes = "删除项目",response = ApiResult.class)
    public ApiResult<Boolean> deleteProject(@Valid @RequestBody IdParam idParam) throws Exception{
        boolean flag = projectService.removeById(idParam.getId());
        return ApiResult.result(flag);
    }

    /**
    * 获取项目
    */
    @PostMapping("/info")
    @ApiOperation(value = "获取Project对象详情",notes = "查看项目",response = ProjectQueryVo.class)
    public ApiResult<ProjectQueryVo> getProject(@Valid @RequestBody IdParam idParam) throws Exception{
        ProjectQueryVo projectQueryVo = projectService.getProjectById(idParam.getId());
        return ApiResult.ok(projectQueryVo);
    }

    /**
     * 项目分页列表
     */
    @PostMapping("/getPageList")
    @ApiOperation(value = "获取Project分页列表",notes = "项目分页列表",response = ProjectQueryVo.class)
    public ApiResult<Paging<ProjectQueryVo>> getProjectPageList(@Valid @RequestBody(required = false) ProjectQueryParam projectQueryParam) throws Exception{
        Paging<ProjectQueryVo> paging = projectService.getProjectPageList(projectQueryParam);
        return ApiResult.ok(paging);
    }

}

