package co.yixiang.modules.manage.web.controller;

import co.yixiang.modules.manage.entity.Charities;
import co.yixiang.modules.manage.service.CharitiesService;
import co.yixiang.modules.manage.web.param.CharitiesQueryParam;
import co.yixiang.modules.manage.web.vo.CharitiesQueryVo;
import co.yixiang.common.web.controller.BaseController;
import co.yixiang.common.api.ApiResult;
import co.yixiang.modules.shop.entity.YxSystemStore;
import co.yixiang.modules.shop.service.YxSystemStoreService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import co.yixiang.common.web.vo.Paging;
import co.yixiang.common.web.param.IdParam;

import java.util.Arrays;
import java.util.List;

/**
 * <p>
 * 慈善活动表 前端控制器
 * </p>
 *
 * @author visa
 * @since 2020-08-20
 */
@Slf4j
@RestController
@RequestMapping("/charities")
@Api("慈善活动表 API")
public class CharitiesController extends BaseController {

    @Autowired
    private CharitiesService charitiesService;
    @Autowired
    private YxSystemStoreService yxSystemStoreService;
    /**
    * 添加慈善活动表
    */
    @PostMapping("/add")
    @ApiOperation(value = "添加Charities对象",notes = "添加慈善活动表",response = ApiResult.class)
    public ApiResult<Boolean> addCharities(@Valid @RequestBody Charities charities) throws Exception{
        boolean flag = charitiesService.save(charities);
        return ApiResult.result(flag);
    }

    /**
    * 修改慈善活动表
    */
    @PostMapping("/update")
    @ApiOperation(value = "修改Charities对象",notes = "修改慈善活动表",response = ApiResult.class)
    public ApiResult<Boolean> updateCharities(@Valid @RequestBody Charities charities) throws Exception{
        boolean flag = charitiesService.updateById(charities);
        return ApiResult.result(flag);
    }

    /**
    * 删除慈善活动表
    */
    @PostMapping("/delete")
    @ApiOperation(value = "删除Charities对象",notes = "删除慈善活动表",response = ApiResult.class)
    public ApiResult<Boolean> deleteCharities(@Valid @RequestBody IdParam idParam) throws Exception{
        boolean flag = charitiesService.removeById(idParam.getId());
        return ApiResult.result(flag);
    }

    /**
    * 获取慈善活动表
    */
    @PostMapping("/info")
    @ApiOperation(value = "获取Charities对象详情",notes = "查看慈善活动表",response = CharitiesQueryVo.class)
    public ApiResult<CharitiesQueryVo> getCharities(@Valid @RequestBody IdParam idParam) throws Exception{
        CharitiesQueryVo charitiesQueryVo = charitiesService.getCharitiesById(idParam.getId());

        List<String> drugstoreNames = Arrays.asList(charitiesQueryVo.getDrugstoreName().split("；"));

        QueryWrapper queryWrapper1 = new QueryWrapper();
        queryWrapper1.in("name",drugstoreNames);
        List<YxSystemStore> drugstoreList =  yxSystemStoreService.list(queryWrapper1);
        charitiesQueryVo.setDrugstoreList(drugstoreList);

        return ApiResult.ok(charitiesQueryVo);
    }

    /**
     * 慈善活动表分页列表
     */
    @PostMapping("/getPageList")
    @ApiOperation(value = "获取Charities分页列表",notes = "慈善活动表分页列表",response = CharitiesQueryVo.class)
    public ApiResult<Paging<Charities>> getCharitiesPageList(@Valid @RequestBody(required = false) CharitiesQueryParam charitiesQueryParam) throws Exception{
        Paging<Charities> paging = charitiesService.getCharitiesPageList(charitiesQueryParam);
        return ApiResult.ok(paging);
    }

}

