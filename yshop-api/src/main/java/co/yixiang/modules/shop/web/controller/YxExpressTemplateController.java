package co.yixiang.modules.shop.web.controller;

import co.yixiang.annotation.AnonymousAccess;
import co.yixiang.modules.shop.entity.YxExpressTemplate;
import co.yixiang.modules.shop.service.YxExpressTemplateService;
import co.yixiang.modules.shop.web.param.YxExpressTemplateQueryParam;
import co.yixiang.modules.shop.web.vo.YxExpressTemplateQueryVo;
import co.yixiang.common.web.controller.BaseController;
import co.yixiang.common.api.ApiResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import co.yixiang.common.web.vo.Paging;
import co.yixiang.common.web.param.IdParam;

/**
 * <p>
 * 物流运费模板 前端控制器
 * </p>
 *
 * @author visa
 * @since 2020-11-28
 */
@Slf4j
@RestController
@RequestMapping("/yxExpressTemplate")
@Api("物流运费模板 API")
public class YxExpressTemplateController extends BaseController {

    @Autowired
    private YxExpressTemplateService yxExpressTemplateService;

    /**
    * 添加物流运费模板
    */
    @PostMapping("/add")
    @ApiOperation(value = "添加YxExpressTemplate对象",notes = "添加物流运费模板",response = ApiResult.class)
    public ApiResult<Boolean> addYxExpressTemplate(@Valid @RequestBody YxExpressTemplate yxExpressTemplate) throws Exception{
        boolean flag = yxExpressTemplateService.saveTemplate(yxExpressTemplate);
        return ApiResult.result(flag);
    }

    /**
    * 修改物流运费模板
    */
    @PostMapping("/update")
    @ApiOperation(value = "修改YxExpressTemplate对象",notes = "修改物流运费模板",response = ApiResult.class)
    public ApiResult<Boolean> updateYxExpressTemplate(@Valid @RequestBody YxExpressTemplate yxExpressTemplate) throws Exception{
        boolean flag = yxExpressTemplateService.saveTemplate(yxExpressTemplate);
        return ApiResult.result(flag);
    }

    /**
    * 删除物流运费模板
    */
    @PostMapping("/delete")
    @ApiOperation(value = "删除YxExpressTemplate对象",notes = "删除物流运费模板",response = ApiResult.class)
    public ApiResult<Boolean> deleteYxExpressTemplate(@Valid @RequestBody IdParam idParam) throws Exception{
        boolean flag = yxExpressTemplateService.removeById(idParam.getId());
        return ApiResult.result(flag);
    }

    /**
    * 获取物流运费模板
    */
    @PostMapping("/info")
    @ApiOperation(value = "获取YxExpressTemplate对象详情",notes = "查看物流运费模板",response = YxExpressTemplateQueryVo.class)
    public ApiResult<YxExpressTemplateQueryVo> getYxExpressTemplate(@Valid @RequestBody IdParam idParam) throws Exception{
        YxExpressTemplateQueryVo yxExpressTemplateQueryVo = yxExpressTemplateService.getYxExpressTemplateById(idParam.getId());
        return ApiResult.ok(yxExpressTemplateQueryVo);
    }

    /**
     * 物流运费模板分页列表
     */
    @GetMapping("/getPageList")
    @AnonymousAccess
    @ApiOperation(value = "获取YxExpressTemplate分页列表",notes = "物流运费模板分页列表",response = YxExpressTemplateQueryVo.class)
    public ApiResult<Paging<YxExpressTemplateQueryVo>> getYxExpressTemplatePageList( YxExpressTemplateQueryParam yxExpressTemplateQueryParam) throws Exception{
        Paging<YxExpressTemplateQueryVo> paging = yxExpressTemplateService.getYxExpressTemplatePageList(yxExpressTemplateQueryParam);
        return ApiResult.ok(paging);
    }

}

