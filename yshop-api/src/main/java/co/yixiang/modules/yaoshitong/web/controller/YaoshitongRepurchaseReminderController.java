package co.yixiang.modules.yaoshitong.web.controller;

import co.yixiang.annotation.AnonymousAccess;
import co.yixiang.modules.shop.entity.MdPharmacistService;
import co.yixiang.modules.shop.service.MdPharmacistServiceService;
import co.yixiang.modules.yaoshitong.entity.YaoshitongRepurchaseReminder;
import co.yixiang.modules.yaoshitong.service.YaoshitongRepurchaseReminderService;
import co.yixiang.modules.yaoshitong.web.param.YaoshitongRepurchaseReminderQueryParam;
import co.yixiang.modules.yaoshitong.web.vo.YaoshitongRepurchaseReminderQueryVo;
import co.yixiang.common.web.controller.BaseController;
import co.yixiang.common.api.ApiResult;
import co.yixiang.utils.SecurityUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.models.auth.In;
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
 * 药品复购提醒 前端控制器
 * </p>
 *
 * @author visa
 * @since 2020-10-21
 */
@Slf4j
@RestController
@RequestMapping("/yaoshitongRepurchaseReminder")
@Api("药品复购提醒 API")
public class YaoshitongRepurchaseReminderController extends BaseController {

    @Autowired
    private YaoshitongRepurchaseReminderService yaoshitongRepurchaseReminderService;

    @Autowired
    private MdPharmacistServiceService mdPharmacistService;
    /**
    * 添加药品复购提醒
    */
    @PostMapping("/add")
    @ApiOperation(value = "添加YaoshitongRepurchaseReminder对象",notes = "添加药品复购提醒",response = ApiResult.class)
    public ApiResult<Boolean> addYaoshitongRepurchaseReminder(@Valid @RequestBody YaoshitongRepurchaseReminder yaoshitongRepurchaseReminder) throws Exception{
        boolean flag = yaoshitongRepurchaseReminderService.save(yaoshitongRepurchaseReminder);
        return ApiResult.result(flag);
    }

    /**
    * 修改药品复购提醒
    */
    @PostMapping("/update")
    @ApiOperation(value = "修改YaoshitongRepurchaseReminder对象",notes = "修改药品复购提醒",response = ApiResult.class)
    public ApiResult<Boolean> updateYaoshitongRepurchaseReminder(@Valid @RequestBody YaoshitongRepurchaseReminder yaoshitongRepurchaseReminder) throws Exception{
        boolean flag = yaoshitongRepurchaseReminderService.updateById(yaoshitongRepurchaseReminder);
        return ApiResult.result(flag);
    }

    /**
    * 删除药品复购提醒
    */
    @PostMapping("/delete")
    @ApiOperation(value = "删除YaoshitongRepurchaseReminder对象",notes = "删除药品复购提醒",response = ApiResult.class)
    public ApiResult<Boolean> deleteYaoshitongRepurchaseReminder(@Valid @RequestBody IdParam idParam) throws Exception{
        boolean flag = yaoshitongRepurchaseReminderService.removeById(idParam.getId());
        return ApiResult.result(flag);
    }

    /**
    * 获取药品复购提醒
    */
    @PostMapping("/info")
    @ApiOperation(value = "获取YaoshitongRepurchaseReminder对象详情",notes = "查看药品复购提醒",response = YaoshitongRepurchaseReminderQueryVo.class)
    public ApiResult<YaoshitongRepurchaseReminderQueryVo> getYaoshitongRepurchaseReminder(@Valid @RequestBody IdParam idParam) throws Exception{
        YaoshitongRepurchaseReminderQueryVo yaoshitongRepurchaseReminderQueryVo = yaoshitongRepurchaseReminderService.getYaoshitongRepurchaseReminderById(idParam.getId());
        return ApiResult.ok(yaoshitongRepurchaseReminderQueryVo);
    }

    /**
     * 药品复购提醒分页列表
     */
    @PostMapping("/getPageList")
    @AnonymousAccess
    @ApiOperation(value = "获取YaoshitongRepurchaseReminder分页列表",notes = "药品复购提醒分页列表",response = YaoshitongRepurchaseReminderQueryVo.class)
    public ApiResult<Paging<YaoshitongRepurchaseReminderQueryVo>> getYaoshitongRepurchaseReminderPageList(@Valid @RequestBody(required = false) YaoshitongRepurchaseReminderQueryParam yaoshitongRepurchaseReminderQueryParam) throws Exception{
        Integer uid = SecurityUtils.getUserId().intValue();
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("uid",uid);
        MdPharmacistService mdPharmacist = mdPharmacistService.getOne(queryWrapper,false);

        if(mdPharmacist != null) {
            yaoshitongRepurchaseReminderQueryParam.setDrugstoreId(Integer.valueOf(mdPharmacist.getForeignId()));
        }else{
            yaoshitongRepurchaseReminderQueryParam.setDrugstoreId(0);
        }

        Paging<YaoshitongRepurchaseReminderQueryVo> paging = yaoshitongRepurchaseReminderService.getYaoshitongRepurchaseReminderPageList(yaoshitongRepurchaseReminderQueryParam);
        return ApiResult.ok(paging);
    }

}

