package co.yixiang.modules.shop.web.controller;

import co.yixiang.modules.shop.entity.YxStoreCouponCard;
import co.yixiang.modules.shop.service.YxStoreCouponCardService;
import co.yixiang.modules.shop.web.param.YxStoreCouponCardQueryParam;
import co.yixiang.modules.shop.web.vo.YxStoreCouponCardQueryVo;
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
 * 优惠券发放记录表 前端控制器
 * </p>
 *
 * @author visa
 * @since 2020-12-10
 */
@Slf4j
@RestController
@RequestMapping("/yxStoreCouponCard")
@Api("优惠券发放记录表 API")
public class YxStoreCouponCardController extends BaseController {

    @Autowired
    private YxStoreCouponCardService yxStoreCouponCardService;

    /**
    * 添加优惠券发放记录表
    */
    @PostMapping("/add")
    @ApiOperation(value = "添加YxStoreCouponCard对象",notes = "添加优惠券发放记录表",response = ApiResult.class)
    public ApiResult<Boolean> addYxStoreCouponCard(@Valid @RequestBody YxStoreCouponCard yxStoreCouponCard) throws Exception{
        boolean flag = yxStoreCouponCardService.save(yxStoreCouponCard);
        return ApiResult.result(flag);
    }

    /**
    * 修改优惠券发放记录表
    */
    @PostMapping("/update")
    @ApiOperation(value = "修改YxStoreCouponCard对象",notes = "修改优惠券发放记录表",response = ApiResult.class)
    public ApiResult<Boolean> updateYxStoreCouponCard(@Valid @RequestBody YxStoreCouponCard yxStoreCouponCard) throws Exception{
        boolean flag = yxStoreCouponCardService.updateById(yxStoreCouponCard);
        return ApiResult.result(flag);
    }

    /**
    * 删除优惠券发放记录表
    */
    @PostMapping("/delete")
    @ApiOperation(value = "删除YxStoreCouponCard对象",notes = "删除优惠券发放记录表",response = ApiResult.class)
    public ApiResult<Boolean> deleteYxStoreCouponCard(@Valid @RequestBody IdParam idParam) throws Exception{
        boolean flag = yxStoreCouponCardService.removeById(idParam.getId());
        return ApiResult.result(flag);
    }

    /**
    * 获取优惠券发放记录表
    */
    @PostMapping("/info")
    @ApiOperation(value = "获取YxStoreCouponCard对象详情",notes = "查看优惠券发放记录表",response = YxStoreCouponCardQueryVo.class)
    public ApiResult<YxStoreCouponCardQueryVo> getYxStoreCouponCard(@Valid @RequestBody IdParam idParam) throws Exception{
        YxStoreCouponCardQueryVo yxStoreCouponCardQueryVo = yxStoreCouponCardService.getYxStoreCouponCardById(idParam.getId());
        return ApiResult.ok(yxStoreCouponCardQueryVo);
    }

    /**
     * 优惠券发放记录表分页列表
     */
    @PostMapping("/getPageList")
    @ApiOperation(value = "获取YxStoreCouponCard分页列表",notes = "优惠券发放记录表分页列表",response = YxStoreCouponCardQueryVo.class)
    public ApiResult<Paging<YxStoreCouponCardQueryVo>> getYxStoreCouponCardPageList(@Valid @RequestBody(required = false) YxStoreCouponCardQueryParam yxStoreCouponCardQueryParam) throws Exception{
        Paging<YxStoreCouponCardQueryVo> paging = yxStoreCouponCardService.getYxStoreCouponCardPageList(yxStoreCouponCardQueryParam);
        return ApiResult.ok(paging);
    }

}

