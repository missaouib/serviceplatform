package co.yixiang.modules.shop.web.controller;

import co.yixiang.modules.shop.entity.YxExpressTemplateDetail;
import co.yixiang.modules.shop.service.YxExpressTemplateDetailService;
import co.yixiang.modules.shop.web.param.YxExpressTemplateDetailQueryParam;
import co.yixiang.modules.shop.web.vo.YxExpressTemplateDetailQueryVo;
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
 * 物流运费模板明细 前端控制器
 * </p>
 *
 * @author visa
 * @since 2020-11-28
 */
@Slf4j
@RestController
@RequestMapping("/yxExpressTemplateDetail")
@Api("物流运费模板明细 API")
public class YxExpressTemplateDetailController extends BaseController {

    @Autowired
    private YxExpressTemplateDetailService yxExpressTemplateDetailService;

    /**
    * 添加物流运费模板明细
    */
    @PostMapping("/add")
    @ApiOperation(value = "添加YxExpressTemplateDetail对象",notes = "添加物流运费模板明细",response = ApiResult.class)
    public ApiResult<Boolean> addYxExpressTemplateDetail(@Valid @RequestBody YxExpressTemplateDetail yxExpressTemplateDetail) throws Exception{
        boolean flag = yxExpressTemplateDetailService.save(yxExpressTemplateDetail);
        return ApiResult.result(flag);
    }

    /**
    * 修改物流运费模板明细
    */
    @PostMapping("/update")
    @ApiOperation(value = "修改YxExpressTemplateDetail对象",notes = "修改物流运费模板明细",response = ApiResult.class)
    public ApiResult<Boolean> updateYxExpressTemplateDetail(@Valid @RequestBody YxExpressTemplateDetail yxExpressTemplateDetail) throws Exception{
        boolean flag = yxExpressTemplateDetailService.updateById(yxExpressTemplateDetail);
        return ApiResult.result(flag);
    }

    /**
    * 删除物流运费模板明细
    */
    @PostMapping("/delete")
    @ApiOperation(value = "删除YxExpressTemplateDetail对象",notes = "删除物流运费模板明细",response = ApiResult.class)
    public ApiResult<Boolean> deleteYxExpressTemplateDetail(@Valid @RequestBody IdParam idParam) throws Exception{
        boolean flag = yxExpressTemplateDetailService.removeById(idParam.getId());
        return ApiResult.result(flag);
    }

    /**
    * 获取物流运费模板明细
    */
    @PostMapping("/info")
    @ApiOperation(value = "获取YxExpressTemplateDetail对象详情",notes = "查看物流运费模板明细",response = YxExpressTemplateDetailQueryVo.class)
    public ApiResult<YxExpressTemplateDetailQueryVo> getYxExpressTemplateDetail(@Valid @RequestBody IdParam idParam) throws Exception{
        YxExpressTemplateDetailQueryVo yxExpressTemplateDetailQueryVo = yxExpressTemplateDetailService.getYxExpressTemplateDetailById(idParam.getId());
        return ApiResult.ok(yxExpressTemplateDetailQueryVo);
    }

    /**
     * 物流运费模板明细分页列表
     */
    @PostMapping("/getPageList")
    @ApiOperation(value = "获取YxExpressTemplateDetail分页列表",notes = "物流运费模板明细分页列表",response = YxExpressTemplateDetailQueryVo.class)
    public ApiResult<Paging<YxExpressTemplateDetailQueryVo>> getYxExpressTemplateDetailPageList(@Valid @RequestBody(required = false) YxExpressTemplateDetailQueryParam yxExpressTemplateDetailQueryParam) throws Exception{
        Paging<YxExpressTemplateDetailQueryVo> paging = yxExpressTemplateDetailService.getYxExpressTemplateDetailPageList(yxExpressTemplateDetailQueryParam);
        return ApiResult.ok(paging);
    }

}

