package co.yixiang.modules.shop.web.controller;

import co.yixiang.modules.shop.entity.MedCalculatorDetail;
import co.yixiang.modules.shop.service.MedCalculatorDetailService;
import co.yixiang.modules.shop.web.param.MedCalculatorDetailQueryParam;
import co.yixiang.modules.shop.web.vo.MedCalculatorDetailQueryVo;
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
 * 用药计算器用药量变更表 前端控制器
 * </p>
 *
 * @author visa
 * @since 2021-01-12
 */
@Slf4j
@RestController
@RequestMapping("/medCalculatorDetail")
@Api("用药计算器用药量变更表 API")
public class MedCalculatorDetailController extends BaseController {

    @Autowired
    private MedCalculatorDetailService medCalculatorDetailService;

    /**
    * 添加用药计算器用药量变更表
    */
    @PostMapping("/add")
    @ApiOperation(value = "添加MedCalculatorDetail对象",notes = "添加用药计算器用药量变更表",response = ApiResult.class)
    public ApiResult<Boolean> addMedCalculatorDetail(@Valid @RequestBody MedCalculatorDetail medCalculatorDetail) throws Exception{
        boolean flag = medCalculatorDetailService.save(medCalculatorDetail);
        return ApiResult.result(flag);
    }

    /**
    * 修改用药计算器用药量变更表
    */
    @PostMapping("/update")
    @ApiOperation(value = "修改MedCalculatorDetail对象",notes = "修改用药计算器用药量变更表",response = ApiResult.class)
    public ApiResult<Boolean> updateMedCalculatorDetail(@Valid @RequestBody MedCalculatorDetail medCalculatorDetail) throws Exception{
        boolean flag = medCalculatorDetailService.updateById(medCalculatorDetail);
        return ApiResult.result(flag);
    }

    /**
    * 删除用药计算器用药量变更表
    */
    @PostMapping("/delete")
    @ApiOperation(value = "删除MedCalculatorDetail对象",notes = "删除用药计算器用药量变更表",response = ApiResult.class)
    public ApiResult<Boolean> deleteMedCalculatorDetail(@Valid @RequestBody IdParam idParam) throws Exception{
        boolean flag = medCalculatorDetailService.removeById(idParam.getId());
        return ApiResult.result(flag);
    }

    /**
    * 获取用药计算器用药量变更表
    */
    @PostMapping("/info")
    @ApiOperation(value = "获取MedCalculatorDetail对象详情",notes = "查看用药计算器用药量变更表",response = MedCalculatorDetailQueryVo.class)
    public ApiResult<MedCalculatorDetailQueryVo> getMedCalculatorDetail(@Valid @RequestBody IdParam idParam) throws Exception{
        MedCalculatorDetailQueryVo medCalculatorDetailQueryVo = medCalculatorDetailService.getMedCalculatorDetailById(idParam.getId());
        return ApiResult.ok(medCalculatorDetailQueryVo);
    }

    /**
     * 用药计算器用药量变更表分页列表
     */
    @PostMapping("/getPageList")
    @ApiOperation(value = "获取MedCalculatorDetail分页列表",notes = "用药计算器用药量变更表分页列表",response = MedCalculatorDetailQueryVo.class)
    public ApiResult<Paging<MedCalculatorDetailQueryVo>> getMedCalculatorDetailPageList(@Valid @RequestBody(required = false) MedCalculatorDetailQueryParam medCalculatorDetailQueryParam) throws Exception{
        Paging<MedCalculatorDetailQueryVo> paging = medCalculatorDetailService.getMedCalculatorDetailPageList(medCalculatorDetailQueryParam);
        return ApiResult.ok(paging);
    }

}

