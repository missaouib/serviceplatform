package co.yixiang.modules.manage.web.controller;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import co.yixiang.annotation.AnonymousAccess;
import co.yixiang.enums.ProjectNameEnum;
import co.yixiang.logging.aop.log.Log;
import co.yixiang.modules.manage.entity.YxStoreCartProject;
import co.yixiang.modules.manage.service.YxStoreCartProjectService;
import co.yixiang.modules.manage.web.param.YxStoreCartProjectQueryParam;
import co.yixiang.modules.manage.web.vo.YxStoreCartProjectQueryVo;
import co.yixiang.common.web.controller.BaseController;
import co.yixiang.common.api.ApiResult;
import co.yixiang.modules.shop.service.Product4projectService;
import co.yixiang.modules.shop.service.YxStoreCartService;
import co.yixiang.modules.shop.web.dto.SpecialProjectDTO;
import co.yixiang.modules.shop.web.param.CartIdsParm;
import co.yixiang.modules.shop.web.param.YxStoreCartQueryParam;
import co.yixiang.modules.yiyaobao.web.vo.StoreCartVo;
import co.yixiang.utils.SecurityUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import co.yixiang.common.web.vo.Paging;
import co.yixiang.common.web.param.IdParam;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 购物车表-项目 前端控制器
 * </p>
 *
 * @author visa
 * @since 2020-08-24
 */
@Slf4j
@RestController
@RequestMapping("/yxStoreCartProject")
@Api("购物车表-项目 API")
public class YxStoreCartProjectController extends BaseController {

    @Autowired
    private YxStoreCartService storeCartService;

    @Autowired
    private Product4projectService product4projectService;

    /**
     * 购物车列表
     */
    @Log(value = "查看购物车",type = 1)
    @GetMapping("/cart/list")
    @ApiOperation(value = "购物车列表",notes = "购物车列表")
    public ApiResult<List<StoreCartVo>> getList(YxStoreCartProjectQueryParam yxStoreCartProjectQueryParam){
        int uid = SecurityUtils.getUserId().intValue();

        storeCartService.add4Project(yxStoreCartProjectQueryParam.getProjectCode(),uid);

        return ApiResult.ok(storeCartService.getUserProductCartList4Store(uid,"",0,yxStoreCartProjectQueryParam.getProjectCode(),yxStoreCartProjectQueryParam.getCardNmuber(),yxStoreCartProjectQueryParam.getCardType(),null));
    }

    /**
     * 修改产品数量
     */
    @PostMapping("/cart/num")
    @ApiOperation(value = "修改产品数量",notes = "修改产品数量")
    public ApiResult<Object> cartNum(@RequestBody String jsonStr){
        int uid = SecurityUtils.getUserId().intValue();
        JSONObject jsonObject = JSON.parseObject(jsonStr);
        if(ObjectUtil.isNull(jsonObject.get("id")) || ObjectUtil.isNull(jsonObject.get("number"))){
            ApiResult.fail("参数错误");
        }
        storeCartService.changeUserCartNum(jsonObject.getInteger("id"),
                jsonObject.getInteger("number"),uid);
        return ApiResult.ok("ok");
    }



    /**
     * 购物车删除产品
     */
    @PostMapping("/cart/del")
    @ApiOperation(value = "购物车删除产品",notes = "购物车删除产品")
    public ApiResult<Object> cartDel(@Validated @RequestBody CartIdsParm parm){
        int uid = SecurityUtils.getUserId().intValue();
        storeCartService.removeUserCart(uid, parm.getIds());

        return ApiResult.ok("ok");
    }

    /**
     * 购物车 获取数量
     */
    @GetMapping("/cart/count")
    @ApiOperation(value = "获取数量",notes = "获取数量")
    public ApiResult<Map<String,Object>> count(YxStoreCartProjectQueryParam queryParam){
        Map<String,Object> map = new LinkedHashMap<>();
        int uid = SecurityUtils.getUserId().intValue();
        List<String> projectCodes = new ArrayList<>();
        projectCodes.add(queryParam.getProjectCode());
        if(StrUtil.isBlank(queryParam.getProjectCode())) {
            projectCodes.add(ProjectNameEnum.HEALTHCARE.getValue());
            projectCodes.add(ProjectNameEnum.ROCHE_SMA.getValue());
        }
        map.put("count",storeCartService.getUserCartNum(uid,"product",queryParam.getNumType().intValue(),projectCodes));
        return ApiResult.ok(map);
    }




}

