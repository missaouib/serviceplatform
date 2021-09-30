package co.yixiang.modules.shop.web.controller;

import co.yixiang.annotation.AnonymousAccess;
import co.yixiang.modules.manage.web.param.YxStoreCartProjectQueryParam;
import co.yixiang.modules.shop.entity.Product4project;
import co.yixiang.modules.shop.service.Product4projectService;
import co.yixiang.modules.shop.web.dto.Data4ProjectDTO;
import co.yixiang.modules.shop.web.dto.SpecialProjectDTO;
import co.yixiang.modules.shop.web.param.Product4projectQueryParam;
import co.yixiang.modules.shop.web.vo.Product4projectQueryVo;
import co.yixiang.common.web.controller.BaseController;
import co.yixiang.common.api.ApiResult;
import co.yixiang.utils.SecurityUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import co.yixiang.common.web.vo.Paging;
import co.yixiang.common.web.param.IdParam;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 项目对应的药品 前端控制器
 * </p>
 *
 * @author visazhou
 * @since 2020-06-11
 */
@Slf4j
@RestController
@RequestMapping("/product4project")
@Api("项目对应的药品 API")
public class Product4projectController extends BaseController {

    @Autowired
    private Product4projectService product4projectService;

    /**
    * 添加项目对应的药品
    */
    @PostMapping("/add")
    @ApiOperation(value = "添加Product4project对象",notes = "添加项目对应的药品",response = ApiResult.class)
    public ApiResult<Boolean> addProduct4project(@Valid @RequestBody Product4project product4project) throws Exception{
        boolean flag = product4projectService.save(product4project);
        return ApiResult.result(flag);
    }

    /**
    * 修改项目对应的药品
    */
    @PostMapping("/update")
    @ApiOperation(value = "修改Product4project对象",notes = "修改项目对应的药品",response = ApiResult.class)
    public ApiResult<Boolean> updateProduct4project(@Valid @RequestBody Product4project product4project) throws Exception{
        boolean flag = product4projectService.updateById(product4project);
        return ApiResult.result(flag);
    }

    /**
    * 删除项目对应的药品
    */
    @PostMapping("/delete")
    @ApiOperation(value = "删除Product4project对象",notes = "删除项目对应的药品",response = ApiResult.class)
    public ApiResult<Boolean> deleteProduct4project(@Valid @RequestBody IdParam idParam) throws Exception{
        boolean flag = product4projectService.removeById(idParam.getId());
        return ApiResult.result(flag);
    }

    /**
    * 获取项目对应的药品
    */
    @PostMapping("/info")
    @ApiOperation(value = "获取Product4project对象详情",notes = "查看项目对应的药品",response = Product4projectQueryVo.class)
    public ApiResult<Product4projectQueryVo> getProduct4project(@Valid @RequestBody IdParam idParam) throws Exception{
        Product4projectQueryVo product4projectQueryVo = product4projectService.getProduct4projectById(idParam.getId());
        return ApiResult.ok(product4projectQueryVo);
    }

    /**
     * 项目对应的药品分页列表
     */
    @PostMapping("/getPageList")
    @ApiOperation(value = "获取Product4project分页列表",notes = "项目对应的药品分页列表",response = Product4projectQueryVo.class)
    public ApiResult<Paging<Product4projectQueryVo>> getProduct4projectPageList(@Valid @RequestBody(required = false) Product4projectQueryParam product4projectQueryParam) throws Exception{
        Paging<Product4projectQueryVo> paging = product4projectService.getProduct4projectPageList(product4projectQueryParam);
        return ApiResult.ok(paging);
    }


    /**
     * 获取罕见病专区的数据
     */
    @AnonymousAccess
    @GetMapping("/specialProject")
    @ApiOperation(value = "获取罕见病专区的数据",notes = "获取罕见病专区的数据")
    public ApiResult<Map<String,Object>> specialProject(YxStoreCartProjectQueryParam queryParam){
        Map<String,Object> map = new LinkedHashMap<>();
        List<SpecialProjectDTO> result = product4projectService.querySpecialProject();
        return ApiResult.ok(result);
    }


    /**
     * 获取项目专属的数据
     */
    @AnonymousAccess
    @GetMapping
    @ApiOperation(value = "获取项目的数据",notes = "获取项目的数据")
    public ApiResult<Data4ProjectDTO> queryData(Product4projectQueryParam product4projectQueryParam){
        Data4ProjectDTO result = product4projectService.queryData(product4projectQueryParam);
        return ApiResult.ok(result);
    }

}

