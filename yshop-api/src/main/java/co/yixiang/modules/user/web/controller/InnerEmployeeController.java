package co.yixiang.modules.user.web.controller;

import co.yixiang.modules.user.entity.InnerEmployee;
import co.yixiang.modules.user.service.InnerEmployeeService;
import co.yixiang.modules.user.web.param.InnerEmployeeQueryParam;
import co.yixiang.modules.user.web.vo.InnerEmployeeQueryVo;
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
 * 内部员工表 前端控制器
 * </p>
 *
 * @author visazhou
 * @since 2020-05-20
 */
@Slf4j
@RestController
@RequestMapping("/innerEmployee")
@Api("内部员工表 API")
public class InnerEmployeeController extends BaseController {

    @Autowired
    private InnerEmployeeService innerEmployeeService;

    /**
    * 添加内部员工表
    */
    @PostMapping("/add")
    @ApiOperation(value = "添加InnerEmployee对象",notes = "添加内部员工表",response = ApiResult.class)
    public ApiResult<Boolean> addInnerEmployee(@Valid @RequestBody InnerEmployee innerEmployee) throws Exception{
        boolean flag = innerEmployeeService.save(innerEmployee);
        return ApiResult.result(flag);
    }

    /**
    * 修改内部员工表
    */
    @PostMapping("/update")
    @ApiOperation(value = "修改InnerEmployee对象",notes = "修改内部员工表",response = ApiResult.class)
    public ApiResult<Boolean> updateInnerEmployee(@Valid @RequestBody InnerEmployee innerEmployee) throws Exception{
        boolean flag = innerEmployeeService.updateById(innerEmployee);
        return ApiResult.result(flag);
    }

    /**
    * 删除内部员工表
    */
    @PostMapping("/delete")
    @ApiOperation(value = "删除InnerEmployee对象",notes = "删除内部员工表",response = ApiResult.class)
    public ApiResult<Boolean> deleteInnerEmployee(@Valid @RequestBody IdParam idParam) throws Exception{
        boolean flag = innerEmployeeService.removeById(idParam.getId());
        return ApiResult.result(flag);
    }

    /**
    * 获取内部员工表
    */
    @PostMapping("/info")
    @ApiOperation(value = "获取InnerEmployee对象详情",notes = "查看内部员工表",response = InnerEmployeeQueryVo.class)
    public ApiResult<InnerEmployeeQueryVo> getInnerEmployee(@Valid @RequestBody IdParam idParam) throws Exception{
        InnerEmployeeQueryVo innerEmployeeQueryVo = innerEmployeeService.getInnerEmployeeById(idParam.getId());
        return ApiResult.ok(innerEmployeeQueryVo);
    }

    /**
     * 内部员工表分页列表
     */
    @PostMapping("/getPageList")
    @ApiOperation(value = "获取InnerEmployee分页列表",notes = "内部员工表分页列表",response = InnerEmployeeQueryVo.class)
    public ApiResult<Paging<InnerEmployeeQueryVo>> getInnerEmployeePageList(@Valid @RequestBody(required = false) InnerEmployeeQueryParam innerEmployeeQueryParam) throws Exception{
        Paging<InnerEmployeeQueryVo> paging = innerEmployeeService.getInnerEmployeePageList(innerEmployeeQueryParam);
        return ApiResult.ok(paging);
    }

}

