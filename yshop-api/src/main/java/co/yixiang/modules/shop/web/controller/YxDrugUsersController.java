package co.yixiang.modules.shop.web.controller;

import co.yixiang.common.api.ApiCode;
import co.yixiang.exception.BadRequestException;
import co.yixiang.modules.manage.service.CheckOneService;
import co.yixiang.modules.shop.entity.YxDrugUsers;
import co.yixiang.modules.shop.service.YxDrugUsersService;
import co.yixiang.modules.shop.web.param.YxDrugUsersQueryParam;
import co.yixiang.modules.shop.web.vo.YxDrugUsersQueryVo;
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

import java.util.Date;

/**
 * <p>
 * 用药人列表 前端控制器
 * </p>
 *
 * @author visa
 * @since 2020-12-20
 */
@Slf4j
@RestController
@RequestMapping("/yxDrugUsers")
@Api("用药人列表 API")
public class YxDrugUsersController extends BaseController {

    @Autowired
    private YxDrugUsersService yxDrugUsersService;

    @Autowired
    private CheckOneService checkOneService;
    /**
    * 添加用药人列表
    */
    @PostMapping("/add")
    @ApiOperation(value = "添加YxDrugUsers对象",notes = "添加用药人列表",response = ApiResult.class)
    public ApiResult<YxDrugUsers> addYxDrugUsers(@Valid @RequestBody YxDrugUsers yxDrugUsers) throws Exception{
        boolean flag = true;
        if(  yxDrugUsers.getUserType() == 1 ) {  // 用药人类型是成人，需要实名认证
            Boolean checkOneFlag = checkOneService.check(yxDrugUsers.getIdcard(),yxDrugUsers.getName());
            if(!checkOneFlag) {
                // return ApiResult.fail("实名认证失败，请正确填写信息（姓名，手机号，身份证号）");
                throw new BadRequestException("实名认证失败，请正确填写姓名，身份证号");
            }
        }


        yxDrugUsers.setUid(SecurityUtils.getUserId().intValue());
        YxDrugUsers yxDrugUsers1 = yxDrugUsersService.saveDrugUsers(yxDrugUsers);
        return ApiResult.ok(yxDrugUsers1);
    }

    /**
    * 修改用药人列表
    */
    @PostMapping("/update")
    @ApiOperation(value = "修改YxDrugUsers对象",notes = "修改用药人列表",response = ApiResult.class)
    public ApiResult<YxDrugUsers> updateYxDrugUsers(@Valid @RequestBody YxDrugUsers yxDrugUsers) throws Exception{
        //Boolean checkOneFlag = false;
        if(yxDrugUsers.getUserType() == null) {
            yxDrugUsers.setUserType(1);
        }
        if( yxDrugUsers.getUserType() == 1 ) {  // 用药人类型是成人，需要实名认证
            Boolean checkOneFlag = checkOneService.check(yxDrugUsers.getIdcard(),yxDrugUsers.getName());
            if(!checkOneFlag) {
                // return ApiResult.fail("实名认证失败，请正确填写信息（姓名，手机号，身份证号）");
                throw new BadRequestException("实名认证失败，请正确填写姓名，身份证号");
            }
        }


        yxDrugUsers.setUid(SecurityUtils.getUserId().intValue());
        YxDrugUsers yxDrugUsers1 =  yxDrugUsersService.saveDrugUsers(yxDrugUsers);
        return ApiResult.ok(yxDrugUsers1);
    }

    /**
    * 删除用药人列表
    */
    @PostMapping("/delete")
    @ApiOperation(value = "删除YxDrugUsers对象",notes = "删除用药人列表",response = ApiResult.class)
    public ApiResult<Boolean> deleteYxDrugUsers(@Valid @RequestBody IdParam idParam) throws Exception{
       // boolean flag = yxDrugUsersService.removeById(idParam.getId());
        YxDrugUsers yxDrugUsers = new YxDrugUsers();
        yxDrugUsers.setIsDel(1);
        yxDrugUsers.setId( Integer.valueOf(idParam.getId()));
        yxDrugUsers.setUpdateTime(new Date());
        boolean flag = yxDrugUsersService.updateById(yxDrugUsers);
        return ApiResult.result(flag);
    }

    /**
    * 获取用药人列表
    */
    @PostMapping("/info")
    @ApiOperation(value = "获取YxDrugUsers对象详情",notes = "查看用药人列表",response = YxDrugUsersQueryVo.class)
    public ApiResult<YxDrugUsersQueryVo> getYxDrugUsers(@Valid @RequestBody IdParam idParam) throws Exception{
        YxDrugUsersQueryVo yxDrugUsersQueryVo = yxDrugUsersService.getYxDrugUsersById(idParam.getId());
        return ApiResult.ok(yxDrugUsersQueryVo);
    }

    /**
     * 用药人列表分页列表
     */
    @PostMapping("/getPageList")
    @ApiOperation(value = "获取YxDrugUsers分页列表",notes = "用药人列表分页列表",response = YxDrugUsersQueryVo.class)
    public ApiResult<Paging<YxDrugUsers>> getYxDrugUsersPageList(@Valid @RequestBody(required = false) YxDrugUsersQueryParam yxDrugUsersQueryParam) throws Exception{
        Integer uid = SecurityUtils.getUserId().intValue();
        yxDrugUsersQueryParam.setUid(uid);
        yxDrugUsersQueryParam.setIsDel(0);
        Paging<YxDrugUsers> paging = yxDrugUsersService.getYxDrugUsersPageList(yxDrugUsersQueryParam);
        return ApiResult.ok(paging);
    }

}

