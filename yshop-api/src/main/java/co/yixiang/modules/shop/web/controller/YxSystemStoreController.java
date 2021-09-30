package co.yixiang.modules.shop.web.controller;

import co.yixiang.annotation.AnonymousAccess;
import co.yixiang.modules.shop.entity.YxSystemStore;
import co.yixiang.modules.shop.service.YxSystemStoreService;
import co.yixiang.modules.shop.web.param.YxSystemStoreQueryParam;
import co.yixiang.modules.shop.web.vo.YxSystemStoreQueryVo;
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
 * 门店自提 前端控制器
 * </p>
 *
 * @author visazhou
 * @since 2020-06-10
 */
@Slf4j
@RestController
@RequestMapping("/yxSystemStore")
@Api("门店自提 API")
public class YxSystemStoreController extends BaseController {

    @Autowired
    private YxSystemStoreService yxSystemStoreService;

    /**
    * 添加门店自提
    */
    @PostMapping("/add")
    @ApiOperation(value = "添加YxSystemStore对象",notes = "添加门店自提",response = ApiResult.class)
    public ApiResult<Boolean> addYxSystemStore(@Valid @RequestBody YxSystemStore yxSystemStore) throws Exception{
        boolean flag = yxSystemStoreService.save(yxSystemStore);
        return ApiResult.result(flag);
    }

    /**
    * 修改门店自提
    */
    @PostMapping("/update")
    @ApiOperation(value = "修改YxSystemStore对象",notes = "修改门店自提",response = ApiResult.class)
    public ApiResult<Boolean> updateYxSystemStore(@Valid @RequestBody YxSystemStore yxSystemStore) throws Exception{
        boolean flag = yxSystemStoreService.updateById(yxSystemStore);
        return ApiResult.result(flag);
    }

    /**
    * 删除门店自提
    */
    @PostMapping("/delete")
    @ApiOperation(value = "删除YxSystemStore对象",notes = "删除门店自提",response = ApiResult.class)
    public ApiResult<Boolean> deleteYxSystemStore(@Valid @RequestBody IdParam idParam) throws Exception{
        boolean flag = yxSystemStoreService.removeById(idParam.getId());
        return ApiResult.result(flag);
    }

    /**
    * 获取门店自提
    */
    @PostMapping("/info")
    @AnonymousAccess
    @ApiOperation(value = "获取YxSystemStore对象详情",notes = "查看门店自提",response = YxSystemStoreQueryVo.class)
    public ApiResult<YxSystemStoreQueryVo> getYxSystemStore(@Valid @RequestBody IdParam idParam) throws Exception{
        YxSystemStoreQueryVo yxSystemStoreQueryVo = yxSystemStoreService.getYxSystemStoreById(idParam.getId());
        return ApiResult.ok(yxSystemStoreQueryVo);
    }

    /**
     * 门店自提分页列表
     */
    @PostMapping("/getPageList")
    @ApiOperation(value = "获取YxSystemStore分页列表",notes = "门店自提分页列表",response = YxSystemStoreQueryVo.class)
    public ApiResult<Paging<YxSystemStoreQueryVo>> getYxSystemStorePageList(@Valid @RequestBody(required = false) YxSystemStoreQueryParam yxSystemStoreQueryParam) throws Exception{
        Paging<YxSystemStoreQueryVo> paging = yxSystemStoreService.getYxSystemStorePageList(yxSystemStoreQueryParam);
        return ApiResult.ok(paging);
    }

}

