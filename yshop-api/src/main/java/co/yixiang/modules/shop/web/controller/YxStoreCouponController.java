package co.yixiang.modules.shop.web.controller;

import co.yixiang.annotation.AnonymousAccess;
import co.yixiang.modules.shop.entity.YxStoreCoupon;
import co.yixiang.modules.shop.service.YxStoreCouponService;
import co.yixiang.modules.shop.web.param.YxStoreCouponQueryParam;
import co.yixiang.modules.shop.web.vo.YxStoreCouponQueryVo;
import co.yixiang.common.web.controller.BaseController;
import co.yixiang.common.api.ApiResult;
import co.yixiang.utils.OrderUtil;
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
 * 优惠券表 前端控制器
 * </p>
 *
 * @author visa
 * @since 2021-04-25
 */
@Slf4j
@RestController
@RequestMapping("/yxStoreCoupon")
@Api("优惠券表 API")
public class YxStoreCouponController extends BaseController {

    @Autowired
    private YxStoreCouponService yxStoreCouponService;

    /**
    * 添加优惠券表
    */
    @PostMapping("/add")
    @ApiOperation(value = "添加YxStoreCoupon对象",notes = "添加优惠券表",response = ApiResult.class)
    public ApiResult<Boolean> addYxStoreCoupon(@Valid @RequestBody YxStoreCoupon yxStoreCoupon) throws Exception{
        yxStoreCoupon.setAddTime(OrderUtil.getSecondTimestampTwo());
        boolean flag = yxStoreCouponService.save(yxStoreCoupon);
        return ApiResult.result(flag);
    }

    /**
    * 修改优惠券表
    */
    @PostMapping("/update")
    @ApiOperation(value = "修改YxStoreCoupon对象",notes = "修改优惠券表",response = ApiResult.class)
    public ApiResult<Boolean> updateYxStoreCoupon(@Valid @RequestBody YxStoreCoupon yxStoreCoupon) throws Exception{
        boolean flag = yxStoreCouponService.updateById(yxStoreCoupon);
        return ApiResult.result(flag);
    }

    /**
    * 删除优惠券表
    */
    @PostMapping("/delete")
    @ApiOperation(value = "删除YxStoreCoupon对象",notes = "删除优惠券表",response = ApiResult.class)
    public ApiResult<Boolean> deleteYxStoreCoupon(@Valid @RequestBody IdParam idParam) throws Exception{
        boolean flag = yxStoreCouponService.removeById(idParam.getId());
        return ApiResult.result(flag);
    }

    /**
    * 获取优惠券表
    */
    @PostMapping("/info")
    @ApiOperation(value = "获取YxStoreCoupon对象详情",notes = "查看优惠券表",response = YxStoreCouponQueryVo.class)
    public ApiResult<YxStoreCouponQueryVo> getYxStoreCoupon(@Valid @RequestBody IdParam idParam) throws Exception{
        YxStoreCouponQueryVo yxStoreCouponQueryVo = yxStoreCouponService.getYxStoreCouponById(idParam.getId());
        return ApiResult.ok(yxStoreCouponQueryVo);
    }

    /**
     * 优惠券表分页列表
     */
    @PostMapping("/getPageList")
    @ApiOperation(value = "获取YxStoreCoupon分页列表",notes = "优惠券表分页列表",response = YxStoreCouponQueryVo.class)
    public ApiResult<Paging<YxStoreCouponQueryVo>> getYxStoreCouponPageList(@Valid @RequestBody(required = false) YxStoreCouponQueryParam yxStoreCouponQueryParam) throws Exception{
        Paging<YxStoreCouponQueryVo> paging = yxStoreCouponService.getYxStoreCouponPageList(yxStoreCouponQueryParam);
        return ApiResult.ok(paging);
    }





}

