package co.yixiang.modules.shop.web.controller;

import co.yixiang.modules.order.entity.YxStoreOrder;
import co.yixiang.modules.shop.entity.YxUserAppointment;
import co.yixiang.modules.shop.service.YxUserAppointmentService;
import co.yixiang.modules.shop.web.param.YxUserAppointmentQueryParam;
import co.yixiang.modules.shop.web.vo.YxUserAppointmentQueryVo;
import co.yixiang.common.web.controller.BaseController;
import co.yixiang.common.api.ApiResult;
import co.yixiang.modules.user.service.YxUserService;
import co.yixiang.utils.OrderUtil;
import co.yixiang.utils.SecurityUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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
 * 预约活动 前端控制器
 * </p>
 *
 * @author visazhou
 * @since 2020-06-05
 */
@Slf4j
@RestController
@RequestMapping("/yxUserAppointment")
@Api(value = "慈善预约", tags = "慈善预约", description = "慈善预约")
public class YxUserAppointmentController extends BaseController {

    @Autowired
    private YxUserAppointmentService yxUserAppointmentService;

    @Autowired
    private YxUserService yxUserService;
    /**
    * 添加预约活动
    */
    @PostMapping("/add")
    @ApiOperation(value = "添加YxUserAppointment对象",notes = "添加预约活动",response = ApiResult.class)
    public ApiResult<Boolean> addYxUserAppointment(@Valid @RequestBody YxUserAppointment yxUserAppointment) throws Exception{
        Integer uid = SecurityUtils.getUserId().intValue();
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("uid",uid);
        queryWrapper.eq("event_name",yxUserAppointment.getEventName());
        int count = yxUserAppointmentService.count(queryWrapper);
        if(count >0) {
            return ApiResult.fail("重复预约");
        }
        yxUserAppointment.setUid(uid);
        yxUserAppointment.setAddTime(OrderUtil.getSecondTimestampTwo());
        boolean flag = yxUserAppointmentService.save(yxUserAppointment);
        return ApiResult.result(flag);
    }

    /**
    * 修改预约活动
    */
    @PostMapping("/update")
    @ApiOperation(value = "修改YxUserAppointment对象",notes = "修改预约活动",response = ApiResult.class)
    public ApiResult<Boolean> updateYxUserAppointment(@Valid @RequestBody YxUserAppointment yxUserAppointment) throws Exception{
      //  yxUserAppointment.setAddTime(OrderUtil.getSecondTimestampTwo());
        boolean flag = yxUserAppointmentService.updateById(yxUserAppointment);
        return ApiResult.result(flag);
    }

    /**
    * 删除预约活动
    */
    @PostMapping("/delete")
    @ApiOperation(value = "删除YxUserAppointment对象",notes = "删除预约活动",response = ApiResult.class)
    public ApiResult<Boolean> deleteYxUserAppointment(@Valid @RequestBody IdParam idParam) throws Exception{
        boolean flag = yxUserAppointmentService.removeById(idParam.getId());
        return ApiResult.result(flag);
    }

    /**
    * 获取预约活动
    */
    @PostMapping("/info")
    @ApiOperation(value = "获取YxUserAppointment对象详情",notes = "查看预约活动",response = YxUserAppointmentQueryVo.class)
    public ApiResult<YxUserAppointmentQueryVo> getYxUserAppointment(@Valid @RequestBody IdParam idParam) throws Exception{
        YxUserAppointmentQueryVo yxUserAppointmentQueryVo = yxUserAppointmentService.getYxUserAppointmentById(idParam.getId());
        return ApiResult.ok(yxUserAppointmentQueryVo);
    }

    /**
     * 预约活动分页列表
     */
    @PostMapping("/getPageList")
    @ApiOperation(value = "获取YxUserAppointment分页列表",notes = "预约活动分页列表",response = YxUserAppointmentQueryVo.class)
    public ApiResult<IPage<YxUserAppointment>> getYxUserAppointmentPageList(@Valid @RequestBody(required = false) YxUserAppointmentQueryParam yxUserAppointmentQueryParam) throws Exception{
        Integer uid = SecurityUtils.getUserId().intValue();
        QueryWrapper<YxUserAppointment> queryWrapper = new QueryWrapper();
        queryWrapper.eq("uid",uid);
        queryWrapper.orderByDesc("add_time");
        Page<YxUserAppointment> pageModel = new Page<>(yxUserAppointmentQueryParam.getPage(), yxUserAppointmentQueryParam.getLimit());
        IPage<YxUserAppointment> pageList =  yxUserAppointmentService.page(pageModel,queryWrapper);
        return ApiResult.ok(pageList);
    }

}

