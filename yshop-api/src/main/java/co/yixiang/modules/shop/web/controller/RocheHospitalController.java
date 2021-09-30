package co.yixiang.modules.shop.web.controller;

import co.yixiang.modules.shop.entity.RocheHospital;
import co.yixiang.modules.shop.service.RocheHospitalService;
import co.yixiang.modules.shop.web.param.RocheHospitalQueryParam;
import co.yixiang.modules.shop.web.vo.RocheHospitalQueryVo;
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
 * 罗氏罕见病sma医院列表 前端控制器
 * </p>
 *
 * @author visa
 * @since 2021-02-05
 */
@Slf4j
@RestController
@RequestMapping("/rocheHospital")
@Api("罗氏罕见病sma医院列表 API")
public class RocheHospitalController extends BaseController {

    @Autowired
    private RocheHospitalService rocheHospitalService;

    /**
    * 添加罗氏罕见病sma医院列表
    */
    @PostMapping("/add")
    @ApiOperation(value = "添加RocheHospital对象",notes = "添加罗氏罕见病sma医院列表",response = ApiResult.class)
    public ApiResult<Boolean> addRocheHospital(@Valid @RequestBody RocheHospital rocheHospital) throws Exception{
        boolean flag = rocheHospitalService.save(rocheHospital);
        return ApiResult.result(flag);
    }

    /**
    * 修改罗氏罕见病sma医院列表
    */
    @PostMapping("/update")
    @ApiOperation(value = "修改RocheHospital对象",notes = "修改罗氏罕见病sma医院列表",response = ApiResult.class)
    public ApiResult<Boolean> updateRocheHospital(@Valid @RequestBody RocheHospital rocheHospital) throws Exception{
        boolean flag = rocheHospitalService.updateById(rocheHospital);
        return ApiResult.result(flag);
    }

    /**
    * 删除罗氏罕见病sma医院列表
    */
    @PostMapping("/delete")
    @ApiOperation(value = "删除RocheHospital对象",notes = "删除罗氏罕见病sma医院列表",response = ApiResult.class)
    public ApiResult<Boolean> deleteRocheHospital(@Valid @RequestBody IdParam idParam) throws Exception{
        boolean flag = rocheHospitalService.removeById(idParam.getId());
        return ApiResult.result(flag);
    }

    /**
    * 获取罗氏罕见病sma医院列表
    */
    @PostMapping("/info")
    @ApiOperation(value = "获取RocheHospital对象详情",notes = "查看罗氏罕见病sma医院列表",response = RocheHospitalQueryVo.class)
    public ApiResult<RocheHospitalQueryVo> getRocheHospital(@Valid @RequestBody IdParam idParam) throws Exception{
        RocheHospitalQueryVo rocheHospitalQueryVo = rocheHospitalService.getRocheHospitalById(idParam.getId());
        return ApiResult.ok(rocheHospitalQueryVo);
    }

    /**
     * 罗氏罕见病sma医院列表分页列表
     */
    @PostMapping("/getPageList")
    @ApiOperation(value = "获取RocheHospital分页列表",notes = "罗氏罕见病sma医院列表分页列表",response = RocheHospitalQueryVo.class)
    public ApiResult<Paging<RocheHospitalQueryVo>> getRocheHospitalPageList(@Valid @RequestBody(required = false) RocheHospitalQueryParam rocheHospitalQueryParam) throws Exception{
        rocheHospitalQueryParam.setStatus("有效");
        Paging<RocheHospitalQueryVo> paging = rocheHospitalService.getRocheHospitalPageList(rocheHospitalQueryParam);
        return ApiResult.ok(paging);
    }

}

