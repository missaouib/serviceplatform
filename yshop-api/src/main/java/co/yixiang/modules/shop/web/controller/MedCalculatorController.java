package co.yixiang.modules.shop.web.controller;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import co.yixiang.modules.shop.entity.MedCalculator;
import co.yixiang.modules.shop.service.MedCalculatorService;
import co.yixiang.modules.shop.web.param.MedCalculatorQueryParam;
import co.yixiang.modules.shop.web.vo.MedCalculatorQueryVo;
import co.yixiang.common.web.controller.BaseController;
import co.yixiang.common.api.ApiResult;
import co.yixiang.utils.SecurityUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.security.SecurityUtil;
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
 * 用药计算器 前端控制器
 * </p>
 *
 * @author visa
 * @since 2021-01-03
 */
@Slf4j
@RestController
@RequestMapping("/medCalculator")
@Api("用药计算器 API")
public class MedCalculatorController extends BaseController {

    @Autowired
    private MedCalculatorService medCalculatorService;

    /**
    * 添加用药计算器
    */
    @PostMapping("/add")
    @ApiOperation(value = "添加MedCalculator对象",notes = "添加用药计算器",response = ApiResult.class)
    public ApiResult<Boolean> addMedCalculator(@Valid @RequestBody MedCalculator medCalculator) throws Exception{
        boolean flag = medCalculatorService.save(medCalculator);
        return ApiResult.result(flag);
    }

    /**
    * 修改用药计算器
    */
    @PostMapping("/update")
    @ApiOperation(value = "修改MedCalculator对象",notes = "修改用药计算器",response = ApiResult.class)
    public ApiResult<Boolean> updateMedCalculator(@Valid @RequestBody MedCalculator medCalculator) throws Exception{
        log.info("修改MedCalculator对象={}",medCalculator);

        medCalculatorService.updateMedCalculator(medCalculator);
        return ApiResult.result(true);
    }

    /**
    * 删除用药计算器
    */
    @PostMapping("/delete")
    @ApiOperation(value = "删除MedCalculator对象",notes = "删除用药计算器",response = ApiResult.class)
    public ApiResult<Boolean> deleteMedCalculator(@Valid @RequestBody IdParam idParam) throws Exception{
        boolean flag = medCalculatorService.removeById(idParam.getId());
        return ApiResult.result(flag);
    }

    /**
    * 获取用药计算器
    */
    @PostMapping("/info")
    @ApiOperation(value = "获取MedCalculator对象详情",notes = "查看用药计算器",response = MedCalculatorQueryVo.class)
    public ApiResult<MedCalculator> getMedCalculator() throws Exception{
        Integer uid = SecurityUtils.getUserId().intValue();
        MedCalculator medCalculatorQueryVo = medCalculatorService.getMedCalculatorByUid(uid, DateUtil.date());
        return ApiResult.ok(medCalculatorQueryVo);
    }

    /**
     * 用药计算器分页列表
     */
    @PostMapping("/getPageList")
    @ApiOperation(value = "获取MedCalculator分页列表",notes = "用药计算器分页列表",response = MedCalculatorQueryVo.class)
    public ApiResult<Paging<MedCalculatorQueryVo>> getMedCalculatorPageList(@Valid @RequestBody(required = false) MedCalculatorQueryParam medCalculatorQueryParam) throws Exception{
        Paging<MedCalculatorQueryVo> paging = medCalculatorService.getMedCalculatorPageList(medCalculatorQueryParam);
        return ApiResult.ok(paging);
    }

}

