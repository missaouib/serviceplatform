package co.yixiang.modules.shop.web.controller;

import cn.hutool.core.collection.CollUtil;
import co.yixiang.annotation.AnonymousAccess;
import co.yixiang.modules.shop.entity.YxStoreProduct;
import co.yixiang.modules.shop.entity.YxUserSearch;
import co.yixiang.modules.shop.service.YxUserSearchService;
import co.yixiang.modules.shop.web.param.YxUserSearchQueryParam;
import co.yixiang.modules.shop.web.vo.YxUserSearchQueryVo;
import co.yixiang.common.web.controller.BaseController;
import co.yixiang.common.api.ApiResult;
import co.yixiang.utils.SecurityUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import co.yixiang.common.web.vo.Paging;
import co.yixiang.common.web.param.IdParam;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 用户搜索词 前端控制器
 * </p>
 *
 * @author visazhou
 * @since 2020-06-03
 */
@Slf4j
@RestController
@RequestMapping("/yxUserSearch")
@Api("用户搜索词 API")
public class YxUserSearchController extends BaseController {

    @Autowired
    private YxUserSearchService yxUserSearchService;

    /**
    * 添加用户搜索词
    */
    @PostMapping("/add")
    @ApiOperation(value = "添加YxUserSearch对象",notes = "添加用户搜索词",response = ApiResult.class)
    public ApiResult<Boolean> addYxUserSearch(@Valid @RequestBody YxUserSearch yxUserSearch) throws Exception{
        boolean flag = yxUserSearchService.save(yxUserSearch);
        return ApiResult.result(flag);
    }

    /**
    * 修改用户搜索词
    */
    @PostMapping("/update")
    @ApiOperation(value = "修改YxUserSearch对象",notes = "修改用户搜索词",response = ApiResult.class)
    public ApiResult<Boolean> updateYxUserSearch(@Valid @RequestBody YxUserSearch yxUserSearch) throws Exception{
        boolean flag = yxUserSearchService.updateById(yxUserSearch);
        return ApiResult.result(flag);
    }

    /**
    * 删除用户搜索词
    */
    @PostMapping("/delete")
    @ApiOperation(value = "删除YxUserSearch对象",notes = "删除用户搜索词",response = ApiResult.class)
    public ApiResult<Boolean> deleteYxUserSearch(@Valid @RequestBody IdParam idParam) throws Exception{
        boolean flag = yxUserSearchService.removeById(idParam.getId());
        return ApiResult.result(flag);
    }

    /**
    * 获取用户搜索词
    */
    @PostMapping("/info")
    @ApiOperation(value = "获取YxUserSearch对象详情",notes = "查看用户搜索词",response = YxUserSearchQueryVo.class)
    public ApiResult<YxUserSearchQueryVo> getYxUserSearch(@Valid @RequestBody IdParam idParam) throws Exception{
        YxUserSearchQueryVo yxUserSearchQueryVo = yxUserSearchService.getYxUserSearchById(idParam.getId());
        return ApiResult.ok(yxUserSearchQueryVo);
    }

    /**
     * 用户搜索词分页列表
     */
    @PostMapping("/getPageList")
    @ApiOperation(value = "获取YxUserSearch分页列表",notes = "用户搜索词分页列表",response = YxUserSearchQueryVo.class)
    public ApiResult<Paging<YxUserSearchQueryVo>> getYxUserSearchPageList(@Valid @RequestBody(required = false) YxUserSearchQueryParam yxUserSearchQueryParam) throws Exception{
        Paging<YxUserSearchQueryVo> paging = yxUserSearchService.getYxUserSearchPageList(yxUserSearchQueryParam);
        return ApiResult.ok(paging);
    }


    @GetMapping("/keywords")
    @ApiOperation(value = "用户历史搜索关键字获取",notes = "用户历史搜索关键字获取")
    @AnonymousAccess
    public ApiResult<List<String>> userSearch(){
        List<String>  stringList = new ArrayList<>();
        try {
            Integer uid = SecurityUtils.getUserId().intValue();
            YxUserSearchQueryParam yxUserSearchQueryParam = new YxUserSearchQueryParam();
            yxUserSearchQueryParam.setUid(uid);
            yxUserSearchQueryParam.setIsDel(0);
            // Paging<YxUserSearchQueryVo> paging = yxUserSearchService.getYxUserSearchPageList(yxUserSearchQueryParam);
            QueryWrapper queryWrapper = new QueryWrapper();
            queryWrapper.eq("uid",uid);
            queryWrapper.eq("is_del",0);
            queryWrapper.orderByDesc("add_time");
            queryWrapper.last(" limit 10");
            List<YxUserSearch> yxStoreProductList = yxUserSearchService.list(queryWrapper);
            if(CollUtil.isNotEmpty(yxStoreProductList)) {
                for(YxUserSearch yxUserSearch : yxStoreProductList) {
                    stringList.add(yxUserSearch.getKeyword());
                }
            }
        }catch (Exception e) {

        }

        return ApiResult.ok(stringList);
    }

    /**
     * 删除用户搜索词
     */
    @PostMapping("/deleteAll")
    @ApiOperation(value = "删除YxUserSearch对象",notes = "删除用户搜索词",response = ApiResult.class)
    public ApiResult<Boolean> deleteYxUserSearchAll() throws Exception{

        Integer uid = SecurityUtils.getUserId().intValue();

        boolean flag = yxUserSearchService.deleteYxUserSearchAll(uid);

        return ApiResult.result(flag);
    }

}

