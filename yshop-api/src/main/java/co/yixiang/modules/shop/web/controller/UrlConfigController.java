package co.yixiang.modules.shop.web.controller;

import co.yixiang.common.web.param.UrlParam;
import co.yixiang.modules.shop.entity.UrlConfig;
import co.yixiang.modules.shop.service.UrlConfigService;
import co.yixiang.modules.shop.web.param.UrlConfigQueryParam;
import co.yixiang.modules.shop.web.vo.UrlConfigQueryVo;
import co.yixiang.common.web.controller.BaseController;
import co.yixiang.common.api.ApiResult;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
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
 * @since 2020-06-10
 */
@Slf4j
@RestController
@RequestMapping("/urlConfig")
@Api(" API")
public class UrlConfigController extends BaseController {

    @Autowired
    private UrlConfigService urlConfigService;

    /**
    * 添加
    */
    @PostMapping("/add")
    @ApiOperation(value = "添加UrlConfig对象",notes = "添加",response = ApiResult.class)
    public ApiResult<Boolean> addUrlConfig(@Valid @RequestBody UrlConfig urlConfig) throws Exception{
        boolean flag = urlConfigService.save(urlConfig);
        return ApiResult.result(flag);
    }

    /**
    * 修改
    */
    @PostMapping("/update")
    @ApiOperation(value = "修改UrlConfig对象",notes = "修改",response = ApiResult.class)
    public ApiResult<Boolean> updateUrlConfig(@Valid @RequestBody UrlConfig urlConfig) throws Exception{
        boolean flag = urlConfigService.updateById(urlConfig);
        return ApiResult.result(flag);
    }

    /**
    * 删除
    */
    @PostMapping("/delete")
    @ApiOperation(value = "删除UrlConfig对象",notes = "删除",response = ApiResult.class)
    public ApiResult<Boolean> deleteUrlConfig(@Valid @RequestBody IdParam idParam) throws Exception{
        boolean flag = urlConfigService.removeById(idParam.getId());
        return ApiResult.result(flag);
    }

    /**
    * 获取
    */
    @PostMapping("/info")
    @ApiOperation(value = "获取UrlConfig对象详情",notes = "查看",response = UrlConfigQueryVo.class)
    public ApiResult<UrlConfigQueryVo> getUrlConfig(@Valid @RequestBody IdParam idParam) throws Exception{
        UrlConfigQueryVo urlConfigQueryVo = urlConfigService.getUrlConfigById(idParam.getId());
        return ApiResult.ok(urlConfigQueryVo);
    }

    /**
     * 获取
     */
    @PostMapping("/infoByUrl")
    @ApiOperation(value = "获取UrlConfig对象详情",notes = "查看",response = UrlConfigQueryVo.class)
    public ApiResult<UrlConfig> getUrlConfigByUrl(@Valid @RequestBody UrlParam urlParam) throws Exception{
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("url",urlParam.getUrl());
        UrlConfig urlConfig =  urlConfigService.getOne(queryWrapper,false);

        return ApiResult.ok(urlConfig);
    }

    /**
     * 分页列表
     */
    @PostMapping("/getPageList")
    @ApiOperation(value = "获取UrlConfig分页列表",notes = "分页列表",response = UrlConfigQueryVo.class)
    public ApiResult<Paging<UrlConfigQueryVo>> getUrlConfigPageList(@Valid @RequestBody(required = false) UrlConfigQueryParam urlConfigQueryParam) throws Exception{
        Paging<UrlConfigQueryVo> paging = urlConfigService.getUrlConfigPageList(urlConfigQueryParam);
        return ApiResult.ok(paging);
    }

}

