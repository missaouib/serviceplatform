package co.yixiang.modules.shop.web.controller;

import co.yixiang.annotation.AnonymousAccess;
import co.yixiang.modules.shop.entity.Hospital;
import co.yixiang.modules.shop.service.HospitalService;
import co.yixiang.modules.shop.web.param.HospitalQueryParam;
import co.yixiang.modules.shop.web.vo.HospitalQueryVo;
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
 * 医院 前端控制器
 * </p>
 *
 * @author visa
 * @since 2021-06-11
 */
@Slf4j
@RestController
@RequestMapping("/hospital")
@Api("医院 API")
public class HospitalController extends BaseController {

    @Autowired
    private HospitalService hospitalService;

    /**
    * 添加医院
    */
    @PostMapping("/add")
    @ApiOperation(value = "添加Hospital对象",notes = "添加医院",response = ApiResult.class)
    public ApiResult<Boolean> addHospital(@Valid @RequestBody Hospital hospital) throws Exception{
        boolean flag = hospitalService.save(hospital);
        return ApiResult.result(flag);
    }

    /**
    * 修改医院
    */
    @PostMapping("/update")
    @ApiOperation(value = "修改Hospital对象",notes = "修改医院",response = ApiResult.class)
    public ApiResult<Boolean> updateHospital(@Valid @RequestBody Hospital hospital) throws Exception{
        boolean flag = hospitalService.updateById(hospital);
        return ApiResult.result(flag);
    }

    /**
    * 删除医院
    */
    @PostMapping("/delete")
    @ApiOperation(value = "删除Hospital对象",notes = "删除医院",response = ApiResult.class)
    public ApiResult<Boolean> deleteHospital(@Valid @RequestBody IdParam idParam) throws Exception{
        boolean flag = hospitalService.removeById(idParam.getId());
        return ApiResult.result(flag);
    }

    /**
    * 获取医院
    */
    @PostMapping("/info")
    @ApiOperation(value = "获取Hospital对象详情",notes = "查看医院",response = HospitalQueryVo.class)
    public ApiResult<HospitalQueryVo> getHospital(@Valid @RequestBody IdParam idParam) throws Exception{
        HospitalQueryVo hospitalQueryVo = hospitalService.getHospitalById(idParam.getId());
        return ApiResult.ok(hospitalQueryVo);
    }

    /**
     * 医院分页列表
     */
    @PostMapping("/getPageList")
    @AnonymousAccess
    @ApiOperation(value = "获取Hospital分页列表",notes = "医院分页列表",response = HospitalQueryVo.class)
    public ApiResult<Paging<HospitalQueryVo>> getHospitalPageList(@Valid @RequestBody(required = false) HospitalQueryParam hospitalQueryParam) throws Exception{
        Paging<HospitalQueryVo> paging = hospitalService.getHospitalPageList(hospitalQueryParam);
        return ApiResult.ok(paging);
    }

}

