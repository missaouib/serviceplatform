package co.yixiang.modules.manage.web.controller;

import co.yixiang.modules.manage.entity.PurchaseForm;
import co.yixiang.modules.manage.service.PurchaseFormService;
import co.yixiang.modules.manage.web.param.PurchaseFormQueryParam;
import co.yixiang.modules.manage.web.vo.PurchaseFormQueryVo;
import co.yixiang.common.web.controller.BaseController;
import co.yixiang.common.api.ApiResult;
import co.yixiang.utils.SecurityUtils;
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
 * 采购需求单 前端控制器
 * </p>
 *
 * @author visazhou
 * @since 2020-05-20
 */
@Slf4j
@RestController
@RequestMapping("/purchaseForm")
@Api("采购需求单 API")
public class PurchaseFormController extends BaseController {

    @Autowired
    private PurchaseFormService purchaseFormService;

    /**
    * 添加采购需求单
    */
    @PostMapping("/add")
    @ApiOperation(value = "添加PurchaseForm对象",notes = "添加采购需求单",response = ApiResult.class)
    public ApiResult<Boolean> addPurchaseForm(@Valid @RequestBody PurchaseForm purchaseForm) throws Exception{
        int uid = SecurityUtils.getUserId().intValue();
        purchaseForm.setUid(uid);
        boolean flag = purchaseFormService.save(purchaseForm);
        return ApiResult.result(flag);
    }

    /**
    * 修改采购需求单
    */
    @PostMapping("/update")
    @ApiOperation(value = "修改PurchaseForm对象",notes = "修改采购需求单",response = ApiResult.class)
    public ApiResult<Boolean> updatePurchaseForm(@Valid @RequestBody PurchaseForm purchaseForm) throws Exception{
        boolean flag = purchaseFormService.updateById(purchaseForm);
        return ApiResult.result(flag);
    }

    /**
    * 删除采购需求单
    */
    @PostMapping("/delete")
    @ApiOperation(value = "删除PurchaseForm对象",notes = "删除采购需求单",response = ApiResult.class)
    public ApiResult<Boolean> deletePurchaseForm(@Valid @RequestBody IdParam idParam) throws Exception{
        boolean flag = purchaseFormService.removeById(idParam.getId());
        return ApiResult.result(flag);
    }

    /**
    * 获取采购需求单
    */
    @PostMapping("/info")
    @ApiOperation(value = "获取PurchaseForm对象详情",notes = "查看采购需求单",response = PurchaseFormQueryVo.class)
    public ApiResult<PurchaseFormQueryVo> getPurchaseForm(@Valid @RequestBody IdParam idParam) throws Exception{
        PurchaseFormQueryVo purchaseFormQueryVo = purchaseFormService.getPurchaseFormById(idParam.getId());
        return ApiResult.ok(purchaseFormQueryVo);
    }

    /**
     * 采购需求单分页列表
     */
    @PostMapping("/getPageList")
    @ApiOperation(value = "获取PurchaseForm分页列表",notes = "采购需求单分页列表",response = PurchaseFormQueryVo.class)
    public ApiResult<Paging<PurchaseFormQueryVo>> getPurchaseFormPageList(@Valid @RequestBody(required = false) PurchaseFormQueryParam purchaseFormQueryParam) throws Exception{
        Paging<PurchaseFormQueryVo> paging = purchaseFormService.getPurchaseFormPageList(purchaseFormQueryParam);
        return ApiResult.ok(paging);
    }

    /**
     * 采购需求单分页列表
     */
    @PostMapping("/getPageListByUid")
    @ApiOperation(value = "获取PurchaseForm分页列表",notes = "采购需求单分页列表",response = PurchaseFormQueryVo.class)
    public ApiResult<Paging<PurchaseFormQueryVo>> getPurchaseFormPageListByUid() throws Exception{
        PurchaseFormQueryParam purchaseFormQueryParam = new PurchaseFormQueryParam();
        purchaseFormQueryParam.setUid(SecurityUtils.getUserId().intValue());
        Paging<PurchaseFormQueryVo> paging = purchaseFormService.getPurchaseFormPageList(purchaseFormQueryParam);
        return ApiResult.ok(paging);
    }

}

