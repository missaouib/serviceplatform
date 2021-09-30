package co.yixiang.modules.hospitaldemand.web.controller;

import co.yixiang.modules.hospitaldemand.entity.InternetHospitalDemandDetail;
import co.yixiang.modules.hospitaldemand.service.InternetHospitalDemandDetailService;
import co.yixiang.modules.hospitaldemand.web.param.InternetHospitalDemandDetailQueryParam;
import co.yixiang.modules.hospitaldemand.web.vo.InternetHospitalDemandDetailQueryVo;
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
 * 互联网医院导入的需求单药品明细 前端控制器
 * </p>
 *
 * @author visa
 * @since 2020-12-04
 */
@Slf4j
@RestController
@RequestMapping("/internetHospitalDemandDetail")
@Api("互联网医院导入的需求单药品明细 API")
public class InternetHospitalDemandDetailController extends BaseController {

    @Autowired
    private InternetHospitalDemandDetailService internetHospitalDemandDetailService;

    /**
    * 添加互联网医院导入的需求单药品明细
    */
    @PostMapping("/add")
    @ApiOperation(value = "添加InternetHospitalDemandDetail对象",notes = "添加互联网医院导入的需求单药品明细",response = ApiResult.class)
    public ApiResult<Boolean> addInternetHospitalDemandDetail(@Valid @RequestBody InternetHospitalDemandDetail internetHospitalDemandDetail) throws Exception{
        boolean flag = internetHospitalDemandDetailService.save(internetHospitalDemandDetail);
        return ApiResult.result(flag);
    }

    /**
    * 修改互联网医院导入的需求单药品明细
    */
    @PostMapping("/update")
    @ApiOperation(value = "修改InternetHospitalDemandDetail对象",notes = "修改互联网医院导入的需求单药品明细",response = ApiResult.class)
    public ApiResult<Boolean> updateInternetHospitalDemandDetail(@Valid @RequestBody InternetHospitalDemandDetail internetHospitalDemandDetail) throws Exception{
        boolean flag = internetHospitalDemandDetailService.updateById(internetHospitalDemandDetail);
        return ApiResult.result(flag);
    }

    /**
    * 删除互联网医院导入的需求单药品明细
    */
    @PostMapping("/delete")
    @ApiOperation(value = "删除InternetHospitalDemandDetail对象",notes = "删除互联网医院导入的需求单药品明细",response = ApiResult.class)
    public ApiResult<Boolean> deleteInternetHospitalDemandDetail(@Valid @RequestBody IdParam idParam) throws Exception{
        boolean flag = internetHospitalDemandDetailService.removeById(idParam.getId());
        return ApiResult.result(flag);
    }

    /**
    * 获取互联网医院导入的需求单药品明细
    */
    @PostMapping("/info")
    @ApiOperation(value = "获取InternetHospitalDemandDetail对象详情",notes = "查看互联网医院导入的需求单药品明细",response = InternetHospitalDemandDetailQueryVo.class)
    public ApiResult<InternetHospitalDemandDetailQueryVo> getInternetHospitalDemandDetail(@Valid @RequestBody IdParam idParam) throws Exception{
        InternetHospitalDemandDetailQueryVo internetHospitalDemandDetailQueryVo = internetHospitalDemandDetailService.getInternetHospitalDemandDetailById(idParam.getId());
        return ApiResult.ok(internetHospitalDemandDetailQueryVo);
    }

    /**
     * 互联网医院导入的需求单药品明细分页列表
     */
    @PostMapping("/getPageList")
    @ApiOperation(value = "获取InternetHospitalDemandDetail分页列表",notes = "互联网医院导入的需求单药品明细分页列表",response = InternetHospitalDemandDetailQueryVo.class)
    public ApiResult<Paging<InternetHospitalDemandDetailQueryVo>> getInternetHospitalDemandDetailPageList(@Valid @RequestBody(required = false) InternetHospitalDemandDetailQueryParam internetHospitalDemandDetailQueryParam) throws Exception{
        Paging<InternetHospitalDemandDetailQueryVo> paging = internetHospitalDemandDetailService.getInternetHospitalDemandDetailPageList(internetHospitalDemandDetailQueryParam);
        return ApiResult.ok(paging);
    }

}

