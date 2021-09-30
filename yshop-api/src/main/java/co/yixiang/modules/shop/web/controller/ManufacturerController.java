package co.yixiang.modules.shop.web.controller;

import co.yixiang.annotation.AnonymousAccess;
import co.yixiang.modules.shop.entity.Manufacturer;
import co.yixiang.modules.shop.service.ManufacturerService;
import co.yixiang.modules.shop.web.param.ManufacturerQueryParam;
import co.yixiang.modules.shop.web.vo.ManufacturerQueryVo;
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
 * 生产厂家主数据表 前端控制器
 * </p>
 *
 * @author visa
 * @since 2020-12-07
 */
@Slf4j
@RestController
@RequestMapping("/manufacturer")
@Api("生产厂家主数据表 API")
public class ManufacturerController extends BaseController {

    @Autowired
    private ManufacturerService manufacturerService;

    /**
    * 添加生产厂家主数据表
    */
    @PostMapping("/add")
    @ApiOperation(value = "添加Manufacturer对象",notes = "添加生产厂家主数据表",response = ApiResult.class)
    public ApiResult<Boolean> addManufacturer(@Valid @RequestBody Manufacturer manufacturer) throws Exception{
        boolean flag = manufacturerService.save(manufacturer);
        return ApiResult.result(flag);
    }

    /**
    * 修改生产厂家主数据表
    */
    @PostMapping("/update")
    @ApiOperation(value = "修改Manufacturer对象",notes = "修改生产厂家主数据表",response = ApiResult.class)
    public ApiResult<Boolean> updateManufacturer(@Valid @RequestBody Manufacturer manufacturer) throws Exception{
        boolean flag = manufacturerService.updateById(manufacturer);
        return ApiResult.result(flag);
    }

    /**
    * 删除生产厂家主数据表
    */
    @PostMapping("/delete")
    @ApiOperation(value = "删除Manufacturer对象",notes = "删除生产厂家主数据表",response = ApiResult.class)
    public ApiResult<Boolean> deleteManufacturer(@Valid @RequestBody IdParam idParam) throws Exception{
        boolean flag = manufacturerService.removeById(idParam.getId());
        return ApiResult.result(flag);
    }

    /**
    * 获取生产厂家主数据表
    */
    @PostMapping("/info")
    @ApiOperation(value = "获取Manufacturer对象详情",notes = "查看生产厂家主数据表",response = ManufacturerQueryVo.class)
    public ApiResult<ManufacturerQueryVo> getManufacturer(@Valid @RequestBody IdParam idParam) throws Exception{
        ManufacturerQueryVo manufacturerQueryVo = manufacturerService.getManufacturerById(idParam.getId());
        return ApiResult.ok(manufacturerQueryVo);
    }

    /**
     * 生产厂家主数据表分页列表
     */
    @AnonymousAccess
    @PostMapping("/getPageList")
    @ApiOperation(value = "获取Manufacturer分页列表",notes = "生产厂家主数据表分页列表",response = ManufacturerQueryVo.class)
    public ApiResult<Paging<ManufacturerQueryVo>> getManufacturerPageList(@Valid @RequestBody(required = false) ManufacturerQueryParam manufacturerQueryParam) throws Exception{
        Paging<ManufacturerQueryVo> paging = manufacturerService.getManufacturerPageList(manufacturerQueryParam);
        return ApiResult.ok(paging);
    }

}

