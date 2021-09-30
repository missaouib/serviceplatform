package co.yixiang.modules.shop.web.controller;

import cn.hutool.json.JSONUtil;
import co.yixiang.annotation.AnonymousAccess;
import co.yixiang.modules.shop.entity.YxStoreDisease;
import co.yixiang.modules.shop.service.YxStoreDiseaseService;
import co.yixiang.modules.shop.web.param.YxStoreDiseaseQueryParam;
import co.yixiang.modules.shop.web.vo.YxStoreCategoryQueryVo;
import co.yixiang.modules.shop.web.vo.YxStoreDiseaseQueryVo;
import co.yixiang.common.web.controller.BaseController;
import co.yixiang.common.api.ApiResult;
import co.yixiang.utils.DiseaseDTO;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import co.yixiang.common.web.vo.Paging;
import co.yixiang.common.web.param.IdParam;

import java.util.List;

/**
 * <p>
 * 病种 前端控制器
 * </p>
 *
 * @author visazhou
 * @since 2020-06-03
 */
@Slf4j
@RestController
@RequestMapping("/yxStoreDisease")
@Api(value = "病种管理", tags = "病种管理", description = "病种管理")
public class YxStoreDiseaseController extends BaseController {

    @Autowired
    private YxStoreDiseaseService yxStoreDiseaseService;

    /**
    * 添加病种
    */
    @PostMapping("/add")
    @ApiOperation(value = "添加YxStoreDisease对象",notes = "添加病种",response = ApiResult.class)
    public ApiResult<Boolean> addYxStoreDisease(@Valid @RequestBody YxStoreDisease yxStoreDisease) throws Exception{
        boolean flag = yxStoreDiseaseService.save(yxStoreDisease);
        return ApiResult.result(flag);
    }

    /**
    * 修改病种
    */
    @PostMapping("/update")
    @ApiOperation(value = "修改YxStoreDisease对象",notes = "修改病种",response = ApiResult.class)
    public ApiResult<Boolean> updateYxStoreDisease(@Valid @RequestBody YxStoreDisease yxStoreDisease) throws Exception{
        boolean flag = yxStoreDiseaseService.updateById(yxStoreDisease);
        return ApiResult.result(flag);
    }

    /**
    * 删除病种
    */
    @PostMapping("/delete")
    @ApiOperation(value = "删除YxStoreDisease对象",notes = "删除病种",response = ApiResult.class)
    public ApiResult<Boolean> deleteYxStoreDisease(@Valid @RequestBody IdParam idParam) throws Exception{
        boolean flag = yxStoreDiseaseService.removeById(idParam.getId());
        return ApiResult.result(flag);
    }

    /**
    * 获取病种
    */
    @PostMapping("/info")
    @ApiOperation(value = "获取YxStoreDisease对象详情",notes = "查看病种",response = YxStoreDiseaseQueryVo.class)
    public ApiResult<YxStoreDiseaseQueryVo> getYxStoreDisease(@Valid @RequestBody IdParam idParam) throws Exception{
        YxStoreDiseaseQueryVo yxStoreDiseaseQueryVo = yxStoreDiseaseService.getYxStoreDiseaseById(idParam.getId());
        return ApiResult.ok(yxStoreDiseaseQueryVo);
    }

    /**
     * 病种分页列表
     */
    @PostMapping("/getPageList")
    @ApiOperation(value = "获取YxStoreDisease分页列表",notes = "病种分页列表",response = YxStoreDiseaseQueryVo.class)
    public ApiResult<Paging<YxStoreDiseaseQueryVo>> getYxStoreDiseasePageList(@Valid @RequestBody(required = false) YxStoreDiseaseQueryParam yxStoreDiseaseQueryParam) throws Exception{
        Paging<YxStoreDiseaseQueryVo> paging = yxStoreDiseaseService.getYxStoreDiseasePageList(yxStoreDiseaseQueryParam);
        return ApiResult.ok(paging);
    }


    /**
     * 病种列表
     */
    @AnonymousAccess
    @GetMapping
    @ApiOperation(value = "病种列表",notes = "病种列表")
    public ApiResult<Paging<YxStoreCategoryQueryVo>> getYxStoreDiseaseList(YxStoreDiseaseQueryParam yxStoreDiseaseQueryParam){
        log.info("病种分类列表查询参数：{}", JSONUtil.parseObj(yxStoreDiseaseQueryParam));
        List<DiseaseDTO> list = yxStoreDiseaseService.getList(yxStoreDiseaseQueryParam);
        log.info("病种分类列表查询结果条数：{}",list.size());
        return ApiResult.ok(list);
    }

    /**
     * 病种列表
     */
    @AnonymousAccess
    @GetMapping("/getListChildren")
    @ApiOperation(value = "病种列表,叶子节点",notes = "叶子节点")
    public ApiResult<List<YxStoreDisease>> getYxStoreDiseaseChildren(){
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.notIn("pid",0);
         yxStoreDiseaseService.list(queryWrapper);
        return ApiResult.ok(yxStoreDiseaseService.list(queryWrapper));
    }

    /**
     * 病种列表
     */
    @AnonymousAccess
    @GetMapping("/getListChildren4patient")
    @ApiOperation(value = "病种列表,叶子节点",notes = "叶子节点")
    public ApiResult<List<YxStoreDisease>> getListChildren4patient(){

        return ApiResult.ok(yxStoreDiseaseService.getList4patient());
    }


    /**
     * 一级分类的查询
     */
    @AnonymousAccess
    @GetMapping("/firstLevel")
    @ApiOperation(value = "一级分类查询",notes = "一级分类查询")
    public ApiResult<Paging<YxStoreCategoryQueryVo>> getYxStoreDiseaseListFirstLevel(YxStoreDiseaseQueryParam yxStoreDiseaseQueryParam){
        return ApiResult.ok(yxStoreDiseaseService.getListFirstLevel(yxStoreDiseaseQueryParam));
    }
}

