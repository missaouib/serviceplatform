package co.yixiang.modules.shop.web.controller;

import co.yixiang.modules.shop.entity.ProjectSalesArea;
import co.yixiang.modules.shop.service.ProjectSalesAreaService;
import co.yixiang.modules.shop.web.param.ProjectSalesAreaQueryParam;
import co.yixiang.modules.shop.web.vo.ProjectSalesAreaQueryVo;
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
 * 项目配置销售省份 前端控制器
 * </p>
 *
 * @author visa
 * @since 2021-04-12
 */
@Slf4j
@RestController
@RequestMapping("/projectSalesArea")
@Api("项目配置销售省份 API")
public class ProjectSalesAreaController extends BaseController {

    @Autowired
    private ProjectSalesAreaService projectSalesAreaService;

    /**
    * 添加项目配置销售省份
    */
    @PostMapping("/add")
    @ApiOperation(value = "添加ProjectSalesArea对象",notes = "添加项目配置销售省份",response = ApiResult.class)
    public ApiResult<Boolean> addProjectSalesArea(@Valid @RequestBody ProjectSalesArea projectSalesArea) throws Exception{
        boolean flag = projectSalesAreaService.save(projectSalesArea);
        return ApiResult.result(flag);
    }

    /**
    * 修改项目配置销售省份
    */
    @PostMapping("/update")
    @ApiOperation(value = "修改ProjectSalesArea对象",notes = "修改项目配置销售省份",response = ApiResult.class)
    public ApiResult<Boolean> updateProjectSalesArea(@Valid @RequestBody ProjectSalesArea projectSalesArea) throws Exception{
        boolean flag = projectSalesAreaService.updateById(projectSalesArea);
        return ApiResult.result(flag);
    }

    /**
    * 删除项目配置销售省份
    */
    @PostMapping("/delete")
    @ApiOperation(value = "删除ProjectSalesArea对象",notes = "删除项目配置销售省份",response = ApiResult.class)
    public ApiResult<Boolean> deleteProjectSalesArea(@Valid @RequestBody IdParam idParam) throws Exception{
        boolean flag = projectSalesAreaService.removeById(idParam.getId());
        return ApiResult.result(flag);
    }

    /**
    * 获取项目配置销售省份
    */
    @PostMapping("/info")
    @ApiOperation(value = "获取ProjectSalesArea对象详情",notes = "查看项目配置销售省份",response = ProjectSalesAreaQueryVo.class)
    public ApiResult<ProjectSalesAreaQueryVo> getProjectSalesArea(@Valid @RequestBody IdParam idParam) throws Exception{
        ProjectSalesAreaQueryVo projectSalesAreaQueryVo = projectSalesAreaService.getProjectSalesAreaById(idParam.getId());
        return ApiResult.ok(projectSalesAreaQueryVo);
    }

    /**
     * 项目配置销售省份分页列表
     */
    @PostMapping("/getPageList")
    @ApiOperation(value = "获取ProjectSalesArea分页列表",notes = "项目配置销售省份分页列表",response = ProjectSalesAreaQueryVo.class)
    public ApiResult<Paging<ProjectSalesAreaQueryVo>> getProjectSalesAreaPageList(@Valid @RequestBody(required = false) ProjectSalesAreaQueryParam projectSalesAreaQueryParam) throws Exception{
        Paging<ProjectSalesAreaQueryVo> paging = projectSalesAreaService.getProjectSalesAreaPageList(projectSalesAreaQueryParam);
        return ApiResult.ok(paging);
    }

}

