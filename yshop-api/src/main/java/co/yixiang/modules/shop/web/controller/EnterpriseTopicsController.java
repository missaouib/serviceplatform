package co.yixiang.modules.shop.web.controller;

import co.yixiang.modules.shop.entity.EnterpriseTopics;
import co.yixiang.modules.shop.entity.YxUserAppointment;
import co.yixiang.modules.shop.service.EnterpriseTopicsService;
import co.yixiang.modules.shop.web.param.EnterpriseTopicsQueryParam;
import co.yixiang.modules.shop.web.vo.EnterpriseTopicsQueryVo;
import co.yixiang.common.web.controller.BaseController;
import co.yixiang.common.api.ApiResult;
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
 *  前端控制器
 * </p>
 *
 * @author visazhou
 * @since 2020-06-05
 */
@Slf4j
@RestController
@RequestMapping("/enterpriseTopics")
@Api(value = "药企专栏", tags = "药企专栏管理", description = "药企专栏管理")
public class EnterpriseTopicsController extends BaseController {

    @Autowired
    private EnterpriseTopicsService enterpriseTopicsService;

    /**
    * 添加
    */
    @PostMapping("/add")
    @ApiOperation(value = "添加EnterpriseTopics对象",notes = "添加",response = ApiResult.class)
    public ApiResult<Boolean> addEnterpriseTopics(@Valid @RequestBody EnterpriseTopics enterpriseTopics) throws Exception{
        boolean flag = enterpriseTopicsService.save(enterpriseTopics);
        return ApiResult.result(flag);
    }

    /**
    * 修改
    */
    @PostMapping("/update")
    @ApiOperation(value = "修改EnterpriseTopics对象",notes = "修改",response = ApiResult.class)
    public ApiResult<Boolean> updateEnterpriseTopics(@Valid @RequestBody EnterpriseTopics enterpriseTopics) throws Exception{
        boolean flag = enterpriseTopicsService.updateById(enterpriseTopics);
        return ApiResult.result(flag);
    }

    /**
    * 删除
    */
    @PostMapping("/delete")
    @ApiOperation(value = "删除EnterpriseTopics对象",notes = "删除",response = ApiResult.class)
    public ApiResult<Boolean> deleteEnterpriseTopics(@Valid @RequestBody IdParam idParam) throws Exception{
        boolean flag = enterpriseTopicsService.removeById(idParam.getId());
        return ApiResult.result(flag);
    }

    /**
    * 获取
    */
    @PostMapping("/info")
    @ApiOperation(value = "获取EnterpriseTopics对象详情",notes = "查看",response = EnterpriseTopicsQueryVo.class)
    public ApiResult<EnterpriseTopicsQueryVo> getEnterpriseTopics(@Valid @RequestBody IdParam idParam) throws Exception{
        EnterpriseTopicsQueryVo enterpriseTopicsQueryVo = enterpriseTopicsService.getEnterpriseTopicsById(idParam.getId());
        return ApiResult.ok(enterpriseTopicsQueryVo);
    }

    /**
     * 分页列表
     */
    @PostMapping("/getPageList")
    @ApiOperation(value = "获取EnterpriseTopics分页列表",notes = "分页列表",response = EnterpriseTopicsQueryVo.class)
    public ApiResult<IPage<EnterpriseTopics>> getEnterpriseTopicsPageList(@Valid @RequestBody(required = false) EnterpriseTopicsQueryParam enterpriseTopicsQueryParam) throws Exception{
        QueryWrapper<EnterpriseTopics> queryWrapper = new QueryWrapper();

        queryWrapper.orderByDesc("add_time");

        Page<EnterpriseTopics> pageModel = new Page<>(enterpriseTopicsQueryParam.getPage(), enterpriseTopicsQueryParam.getLimit());
        IPage<EnterpriseTopics> pageList =  enterpriseTopicsService.page(pageModel,queryWrapper);

        return ApiResult.ok(pageList);
    }

}

