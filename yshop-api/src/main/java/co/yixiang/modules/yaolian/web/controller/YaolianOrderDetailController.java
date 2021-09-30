package co.yixiang.modules.yaolian.web.controller;

import co.yixiang.modules.yaolian.entity.YaolianOrderDetail;
import co.yixiang.modules.yaolian.service.YaolianOrderDetailService;
import co.yixiang.modules.yaolian.web.param.YaolianOrderDetailQueryParam;
import co.yixiang.modules.yaolian.web.vo.YaolianOrderDetailQueryVo;
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
 * 药联订单明细 前端控制器
 * </p>
 *
 * @author visa
 * @since 2021-03-05
 */
@Slf4j
@RestController
@RequestMapping("/yaolianOrderDetail")
@Api("药联订单明细 API")
public class YaolianOrderDetailController extends BaseController {

    @Autowired
    private YaolianOrderDetailService yaolianOrderDetailService;

    /**
    * 添加药联订单明细
    */
    @PostMapping("/add")
    @ApiOperation(value = "添加YaolianOrderDetail对象",notes = "添加药联订单明细",response = ApiResult.class)
    public ApiResult<Boolean> addYaolianOrderDetail(@Valid @RequestBody YaolianOrderDetail yaolianOrderDetail) throws Exception{
        boolean flag = yaolianOrderDetailService.save(yaolianOrderDetail);
        return ApiResult.result(flag);
    }

    /**
    * 修改药联订单明细
    */
    @PostMapping("/update")
    @ApiOperation(value = "修改YaolianOrderDetail对象",notes = "修改药联订单明细",response = ApiResult.class)
    public ApiResult<Boolean> updateYaolianOrderDetail(@Valid @RequestBody YaolianOrderDetail yaolianOrderDetail) throws Exception{
        boolean flag = yaolianOrderDetailService.updateById(yaolianOrderDetail);
        return ApiResult.result(flag);
    }

    /**
    * 删除药联订单明细
    */
    @PostMapping("/delete")
    @ApiOperation(value = "删除YaolianOrderDetail对象",notes = "删除药联订单明细",response = ApiResult.class)
    public ApiResult<Boolean> deleteYaolianOrderDetail(@Valid @RequestBody IdParam idParam) throws Exception{
        boolean flag = yaolianOrderDetailService.removeById(idParam.getId());
        return ApiResult.result(flag);
    }

    /**
    * 获取药联订单明细
    */
    @PostMapping("/info")
    @ApiOperation(value = "获取YaolianOrderDetail对象详情",notes = "查看药联订单明细",response = YaolianOrderDetailQueryVo.class)
    public ApiResult<YaolianOrderDetailQueryVo> getYaolianOrderDetail(@Valid @RequestBody IdParam idParam) throws Exception{
        YaolianOrderDetailQueryVo yaolianOrderDetailQueryVo = yaolianOrderDetailService.getYaolianOrderDetailById(idParam.getId());
        return ApiResult.ok(yaolianOrderDetailQueryVo);
    }

    /**
     * 药联订单明细分页列表
     */
    @PostMapping("/getPageList")
    @ApiOperation(value = "获取YaolianOrderDetail分页列表",notes = "药联订单明细分页列表",response = YaolianOrderDetailQueryVo.class)
    public ApiResult<Paging<YaolianOrderDetailQueryVo>> getYaolianOrderDetailPageList(@Valid @RequestBody(required = false) YaolianOrderDetailQueryParam yaolianOrderDetailQueryParam) throws Exception{
        Paging<YaolianOrderDetailQueryVo> paging = yaolianOrderDetailService.getYaolianOrderDetailPageList(yaolianOrderDetailQueryParam);
        return ApiResult.ok(paging);
    }

}

