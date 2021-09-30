package co.yixiang.modules.yaolian.web.controller;

import co.yixiang.modules.yaolian.entity.YaolianOrder;
import co.yixiang.modules.yaolian.service.YaolianOrderService;
import co.yixiang.modules.yaolian.web.param.YaolianOrderQueryParam;
import co.yixiang.modules.yaolian.web.vo.YaolianOrderQueryVo;
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
 * 药联订单表 前端控制器
 * </p>
 *
 * @author visa
 * @since 2021-03-05
 */
@Slf4j
@RestController
@RequestMapping("/yaolianOrder")
@Api("药联订单表 API")
public class YaolianOrderController extends BaseController {

    @Autowired
    private YaolianOrderService yaolianOrderService;

    /**
    * 添加药联订单表
    */
    @PostMapping("/add")
    @ApiOperation(value = "添加YaolianOrder对象",notes = "添加药联订单表",response = ApiResult.class)
    public ApiResult<Boolean> addYaolianOrder(@Valid @RequestBody YaolianOrder yaolianOrder) throws Exception{
        boolean flag = yaolianOrderService.save(yaolianOrder);
        return ApiResult.result(flag);
    }

    /**
    * 修改药联订单表
    */
    @PostMapping("/update")
    @ApiOperation(value = "修改YaolianOrder对象",notes = "修改药联订单表",response = ApiResult.class)
    public ApiResult<Boolean> updateYaolianOrder(@Valid @RequestBody YaolianOrder yaolianOrder) throws Exception{
        boolean flag = yaolianOrderService.updateById(yaolianOrder);
        return ApiResult.result(flag);
    }

    /**
    * 删除药联订单表
    */
    @PostMapping("/delete")
    @ApiOperation(value = "删除YaolianOrder对象",notes = "删除药联订单表",response = ApiResult.class)
    public ApiResult<Boolean> deleteYaolianOrder(@Valid @RequestBody IdParam idParam) throws Exception{
        boolean flag = yaolianOrderService.removeById(idParam.getId());
        return ApiResult.result(flag);
    }

    /**
    * 获取药联订单表
    */
    @PostMapping("/info")
    @ApiOperation(value = "获取YaolianOrder对象详情",notes = "查看药联订单表",response = YaolianOrderQueryVo.class)
    public ApiResult<YaolianOrderQueryVo> getYaolianOrder(@Valid @RequestBody IdParam idParam) throws Exception{
        YaolianOrderQueryVo yaolianOrderQueryVo = yaolianOrderService.getYaolianOrderById(idParam.getId());
        return ApiResult.ok(yaolianOrderQueryVo);
    }

    /**
     * 药联订单表分页列表
     */
    @PostMapping("/getPageList")
    @ApiOperation(value = "获取YaolianOrder分页列表",notes = "药联订单表分页列表",response = YaolianOrderQueryVo.class)
    public ApiResult<Paging<YaolianOrderQueryVo>> getYaolianOrderPageList(@Valid @RequestBody(required = false) YaolianOrderQueryParam yaolianOrderQueryParam) throws Exception{
        Paging<YaolianOrderQueryVo> paging = yaolianOrderService.getYaolianOrderPageList(yaolianOrderQueryParam);
        return ApiResult.ok(paging);
    }

}

