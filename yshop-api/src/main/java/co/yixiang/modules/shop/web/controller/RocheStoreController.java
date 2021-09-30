package co.yixiang.modules.shop.web.controller;

import co.yixiang.annotation.AnonymousAccess;
import co.yixiang.modules.shop.entity.RocheStore;
import co.yixiang.modules.shop.service.RocheStoreService;
import co.yixiang.modules.shop.web.param.RocheStoreQueryParam;
import co.yixiang.modules.shop.web.vo.RocheStoreQueryVo;
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
 *  前端控制器
 * </p>
 *
 * @author visa
 * @since 2020-12-28
 */
@Slf4j
@RestController
@RequestMapping("/rocheStore")
@Api(" API")
public class RocheStoreController extends BaseController {

    @Autowired
    private RocheStoreService rocheStoreService;

    /**
    * 添加
    */
    @PostMapping("/add")
    @ApiOperation(value = "添加RocheStore对象",notes = "添加",response = ApiResult.class)
    public ApiResult<Boolean> addRocheStore(@Valid @RequestBody RocheStore rocheStore) throws Exception{
        boolean flag = rocheStoreService.save(rocheStore);
        return ApiResult.result(flag);
    }

    /**
    * 修改
    */
    @PostMapping("/update")
    @ApiOperation(value = "修改RocheStore对象",notes = "修改",response = ApiResult.class)
    public ApiResult<Boolean> updateRocheStore(@Valid @RequestBody RocheStore rocheStore) throws Exception{
        boolean flag = rocheStoreService.updateById(rocheStore);
        return ApiResult.result(flag);
    }

    /**
    * 删除
    */
    @PostMapping("/delete")
    @ApiOperation(value = "删除RocheStore对象",notes = "删除",response = ApiResult.class)
    public ApiResult<Boolean> deleteRocheStore(@Valid @RequestBody IdParam idParam) throws Exception{
        boolean flag = rocheStoreService.removeById(idParam.getId());
        return ApiResult.result(flag);
    }

    /**
    * 获取
    */
    @PostMapping("/info")
    @ApiOperation(value = "获取RocheStore对象详情",notes = "查看",response = RocheStoreQueryVo.class)
    public ApiResult<RocheStoreQueryVo> getRocheStore(@Valid @RequestBody IdParam idParam) throws Exception{
        RocheStoreQueryVo rocheStoreQueryVo = rocheStoreService.getRocheStoreById(idParam.getId());
        return ApiResult.ok(rocheStoreQueryVo);
    }

    /**
     * 分页列表
     */
    @PostMapping("/getPageList")
    @AnonymousAccess
    @ApiOperation(value = "获取RocheStore分页列表",notes = "分页列表",response = RocheStoreQueryVo.class)
    public ApiResult<Paging<RocheStoreQueryVo>> getRocheStorePageList(@Valid @RequestBody(required = false) RocheStoreQueryParam rocheStoreQueryParam) throws Exception{
        Paging<RocheStoreQueryVo> paging = rocheStoreService.getRocheStorePageList(rocheStoreQueryParam);
        return ApiResult.ok(paging);
    }

}

