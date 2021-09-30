package co.yixiang.modules.shop.web.controller;

import co.yixiang.modules.shop.entity.YxStoreProductGroup;
import co.yixiang.modules.shop.service.YxStoreProductGroupService;
import co.yixiang.modules.shop.web.param.YxStoreProductGroupQueryParam;
import co.yixiang.modules.shop.web.vo.YxStoreProductGroupQueryVo;
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
 * 商品组合 前端控制器
 * </p>
 *
 * @author visa
 * @since 2021-08-19
 */
@Slf4j
@RestController
@RequestMapping("/yxStoreProductGroup")
@Api("商品组合 API")
public class YxStoreProductGroupController extends BaseController {

    @Autowired
    private YxStoreProductGroupService yxStoreProductGroupService;

    /**
    * 添加商品组合
    */
    @PostMapping("/add")
    @ApiOperation(value = "添加YxStoreProductGroup对象",notes = "添加商品组合",response = ApiResult.class)
    public ApiResult<Boolean> addYxStoreProductGroup(@Valid @RequestBody YxStoreProductGroup yxStoreProductGroup) throws Exception{
        boolean flag = yxStoreProductGroupService.save(yxStoreProductGroup);
        return ApiResult.result(flag);
    }

    /**
    * 修改商品组合
    */
    @PostMapping("/update")
    @ApiOperation(value = "修改YxStoreProductGroup对象",notes = "修改商品组合",response = ApiResult.class)
    public ApiResult<Boolean> updateYxStoreProductGroup(@Valid @RequestBody YxStoreProductGroup yxStoreProductGroup) throws Exception{
        boolean flag = yxStoreProductGroupService.updateById(yxStoreProductGroup);
        return ApiResult.result(flag);
    }

    /**
    * 删除商品组合
    */
    @PostMapping("/delete")
    @ApiOperation(value = "删除YxStoreProductGroup对象",notes = "删除商品组合",response = ApiResult.class)
    public ApiResult<Boolean> deleteYxStoreProductGroup(@Valid @RequestBody IdParam idParam) throws Exception{
        boolean flag = yxStoreProductGroupService.removeById(idParam.getId());
        return ApiResult.result(flag);
    }

    /**
    * 获取商品组合
    */
    @PostMapping("/info")
    @ApiOperation(value = "获取YxStoreProductGroup对象详情",notes = "查看商品组合",response = YxStoreProductGroupQueryVo.class)
    public ApiResult<YxStoreProductGroupQueryVo> getYxStoreProductGroup(@Valid @RequestBody IdParam idParam) throws Exception{
        YxStoreProductGroupQueryVo yxStoreProductGroupQueryVo = yxStoreProductGroupService.getYxStoreProductGroupById(idParam.getId());
        return ApiResult.ok(yxStoreProductGroupQueryVo);
    }

    /**
     * 商品组合分页列表
     */
    @PostMapping("/getPageList")
    @ApiOperation(value = "获取YxStoreProductGroup分页列表",notes = "商品组合分页列表",response = YxStoreProductGroupQueryVo.class)
    public ApiResult<Paging<YxStoreProductGroupQueryVo>> getYxStoreProductGroupPageList(@Valid @RequestBody(required = false) YxStoreProductGroupQueryParam yxStoreProductGroupQueryParam) throws Exception{
        Paging<YxStoreProductGroupQueryVo> paging = yxStoreProductGroupService.getYxStoreProductGroupPageList(yxStoreProductGroupQueryParam);
        return ApiResult.ok(paging);
    }

}

