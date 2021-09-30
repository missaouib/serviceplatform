package co.yixiang.modules.shop.web.controller;

import co.yixiang.modules.shop.entity.RechargeLog;
import co.yixiang.modules.shop.service.RechargeLogService;
import co.yixiang.modules.shop.web.param.RechargeLogQueryParam;
import co.yixiang.modules.shop.web.vo.RechargeLogQueryVo;
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
 * 储值记录表 前端控制器
 * </p>
 *
 * @author visa
 * @since 2021-07-05
 */
@Slf4j
@RestController
@RequestMapping("/rechargeLog")
@Api("储值记录表 API")
public class RechargeLogController extends BaseController {

    @Autowired
    private RechargeLogService rechargeLogService;

    /**
    * 添加储值记录表
    */
    @PostMapping("/add")
    @ApiOperation(value = "添加RechargeLog对象",notes = "添加储值记录表",response = ApiResult.class)
    public ApiResult<Boolean> addRechargeLog(@Valid @RequestBody RechargeLog rechargeLog) throws Exception{
        boolean flag = rechargeLogService.save(rechargeLog);
        return ApiResult.result(flag);
    }

    /**
    * 修改储值记录表
    */
    @PostMapping("/update")
    @ApiOperation(value = "修改RechargeLog对象",notes = "修改储值记录表",response = ApiResult.class)
    public ApiResult<Boolean> updateRechargeLog(@Valid @RequestBody RechargeLog rechargeLog) throws Exception{
        boolean flag = rechargeLogService.updateById(rechargeLog);
        return ApiResult.result(flag);
    }

    /**
    * 删除储值记录表
    */
    @PostMapping("/delete")
    @ApiOperation(value = "删除RechargeLog对象",notes = "删除储值记录表",response = ApiResult.class)
    public ApiResult<Boolean> deleteRechargeLog(@Valid @RequestBody IdParam idParam) throws Exception{
        boolean flag = rechargeLogService.removeById(idParam.getId());
        return ApiResult.result(flag);
    }

    /**
    * 获取储值记录表
    */
    @PostMapping("/info")
    @ApiOperation(value = "获取RechargeLog对象详情",notes = "查看储值记录表",response = RechargeLogQueryVo.class)
    public ApiResult<RechargeLogQueryVo> getRechargeLog(@Valid @RequestBody IdParam idParam) throws Exception{
        RechargeLogQueryVo rechargeLogQueryVo = rechargeLogService.getRechargeLogById(idParam.getId());
        return ApiResult.ok(rechargeLogQueryVo);
    }

    /**
     * 储值记录表分页列表
     */
    @PostMapping("/getPageList")
    @ApiOperation(value = "获取RechargeLog分页列表",notes = "储值记录表分页列表",response = RechargeLogQueryVo.class)
    public ApiResult<Paging<RechargeLogQueryVo>> getRechargeLogPageList(@Valid @RequestBody(required = false) RechargeLogQueryParam rechargeLogQueryParam) throws Exception{
        Paging<RechargeLogQueryVo> paging = rechargeLogService.getRechargeLogPageList(rechargeLogQueryParam);
        return ApiResult.ok(paging);
    }

}

