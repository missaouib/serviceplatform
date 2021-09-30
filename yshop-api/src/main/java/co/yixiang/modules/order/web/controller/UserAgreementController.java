package co.yixiang.modules.order.web.controller;

import co.yixiang.modules.order.entity.UserAgreement;
import co.yixiang.modules.order.service.UserAgreementService;
import co.yixiang.modules.order.web.param.UserAgreementQueryParam;
import co.yixiang.modules.order.web.vo.UserAgreementQueryVo;
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
 * 用户同意书 前端控制器
 * </p>
 *
 * @author visa
 * @since 2020-11-30
 */
@Slf4j
@RestController
@RequestMapping("/userAgreement")
@Api("用户同意书 API")
public class UserAgreementController extends BaseController {

    @Autowired
    private UserAgreementService userAgreementService;

    /**
    * 添加用户同意书
    */
    @PostMapping("/add")
    @ApiOperation(value = "添加UserAgreement对象",notes = "添加用户同意书",response = ApiResult.class)
    public ApiResult<Boolean> addUserAgreement(@Valid @RequestBody UserAgreement userAgreement) throws Exception{
        boolean flag = userAgreementService.save(userAgreement);
        return ApiResult.result(flag);
    }

    /**
    * 修改用户同意书
    */
    @PostMapping("/update")
    @ApiOperation(value = "修改UserAgreement对象",notes = "修改用户同意书",response = ApiResult.class)
    public ApiResult<Boolean> updateUserAgreement(@Valid @RequestBody UserAgreement userAgreement) throws Exception{
        boolean flag = userAgreementService.updateById(userAgreement);
        return ApiResult.result(flag);
    }

    /**
    * 删除用户同意书
    */
    @PostMapping("/delete")
    @ApiOperation(value = "删除UserAgreement对象",notes = "删除用户同意书",response = ApiResult.class)
    public ApiResult<Boolean> deleteUserAgreement(@Valid @RequestBody IdParam idParam) throws Exception{
        boolean flag = userAgreementService.removeById(idParam.getId());
        return ApiResult.result(flag);
    }

    /**
    * 获取用户同意书
    */
    @PostMapping("/info")
    @ApiOperation(value = "获取UserAgreement对象详情",notes = "查看用户同意书",response = UserAgreementQueryVo.class)
    public ApiResult<UserAgreementQueryVo> getUserAgreement(@Valid @RequestBody IdParam idParam) throws Exception{
        UserAgreementQueryVo userAgreementQueryVo = userAgreementService.getUserAgreementById(idParam.getId());
        return ApiResult.ok(userAgreementQueryVo);
    }

    /**
     * 用户同意书分页列表
     */
    @PostMapping("/getPageList")
    @ApiOperation(value = "获取UserAgreement分页列表",notes = "用户同意书分页列表",response = UserAgreementQueryVo.class)
    public ApiResult<Paging<UserAgreementQueryVo>> getUserAgreementPageList(@Valid @RequestBody(required = false) UserAgreementQueryParam userAgreementQueryParam) throws Exception{
        Paging<UserAgreementQueryVo> paging = userAgreementService.getUserAgreementPageList(userAgreementQueryParam);
        return ApiResult.ok(paging);
    }

}

