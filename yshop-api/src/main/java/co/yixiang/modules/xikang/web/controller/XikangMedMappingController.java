package co.yixiang.modules.xikang.web.controller;

import co.yixiang.modules.xikang.entity.XikangMedMapping;
import co.yixiang.modules.xikang.service.XikangMedMappingService;
import co.yixiang.modules.xikang.web.param.XikangMedMappingQueryParam;
import co.yixiang.modules.xikang.web.vo.XikangMedMappingQueryVo;
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
 * 熙康医院与商城药品的映射 前端控制器
 * </p>
 *
 * @author visa
 * @since 2020-12-30
 */
@Slf4j
@RestController
@RequestMapping("/xikangMedMapping")
@Api("熙康医院与商城药品的映射 API")
public class XikangMedMappingController extends BaseController {

    @Autowired
    private XikangMedMappingService xikangMedMappingService;

    /**
    * 添加熙康医院与商城药品的映射
    */
    @PostMapping("/add")
    @ApiOperation(value = "添加XikangMedMapping对象",notes = "添加熙康医院与商城药品的映射",response = ApiResult.class)
    public ApiResult<Boolean> addXikangMedMapping(@Valid @RequestBody XikangMedMapping xikangMedMapping) throws Exception{
        boolean flag = xikangMedMappingService.save(xikangMedMapping);
        return ApiResult.result(flag);
    }

    /**
    * 修改熙康医院与商城药品的映射
    */
    @PostMapping("/update")
    @ApiOperation(value = "修改XikangMedMapping对象",notes = "修改熙康医院与商城药品的映射",response = ApiResult.class)
    public ApiResult<Boolean> updateXikangMedMapping(@Valid @RequestBody XikangMedMapping xikangMedMapping) throws Exception{
        boolean flag = xikangMedMappingService.updateById(xikangMedMapping);
        return ApiResult.result(flag);
    }

    /**
    * 删除熙康医院与商城药品的映射
    */
    @PostMapping("/delete")
    @ApiOperation(value = "删除XikangMedMapping对象",notes = "删除熙康医院与商城药品的映射",response = ApiResult.class)
    public ApiResult<Boolean> deleteXikangMedMapping(@Valid @RequestBody IdParam idParam) throws Exception{
        boolean flag = xikangMedMappingService.removeById(idParam.getId());
        return ApiResult.result(flag);
    }

    /**
    * 获取熙康医院与商城药品的映射
    */
    @PostMapping("/info")
    @ApiOperation(value = "获取XikangMedMapping对象详情",notes = "查看熙康医院与商城药品的映射",response = XikangMedMappingQueryVo.class)
    public ApiResult<XikangMedMappingQueryVo> getXikangMedMapping(@Valid @RequestBody IdParam idParam) throws Exception{
        XikangMedMappingQueryVo xikangMedMappingQueryVo = xikangMedMappingService.getXikangMedMappingById(idParam.getId());
        return ApiResult.ok(xikangMedMappingQueryVo);
    }

    /**
     * 熙康医院与商城药品的映射分页列表
     */
    @PostMapping("/getPageList")
    @ApiOperation(value = "获取XikangMedMapping分页列表",notes = "熙康医院与商城药品的映射分页列表",response = XikangMedMappingQueryVo.class)
    public ApiResult<Paging<XikangMedMappingQueryVo>> getXikangMedMappingPageList(@Valid @RequestBody(required = false) XikangMedMappingQueryParam xikangMedMappingQueryParam) throws Exception{
        Paging<XikangMedMappingQueryVo> paging = xikangMedMappingService.getXikangMedMappingPageList(xikangMedMappingQueryParam);
        return ApiResult.ok(paging);
    }

}

