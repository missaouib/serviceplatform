/**
 * Copyright (C) 2018-2019
 * All rights reserved, Designed By www.yixiang.co
 * 注意：
 * 本软件为www.yixiang.co开发研制，未经购买不得使用
 * 购买后可获得全部源代码（禁止转卖、分享、上传到码云、github等开源平台）
 * 一经发现盗用、分享等行为，将追究法律责任，后果自负
 */
package co.yixiang.modules.shop.web.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.file.FileReader;
import cn.hutool.core.io.resource.ClassPathResource;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import co.yixiang.annotation.AnonymousAccess;
import co.yixiang.common.api.ApiResult;
import co.yixiang.common.web.vo.Paging;
import co.yixiang.constant.ShopConstants;
import co.yixiang.enums.ProjectNameEnum;
import co.yixiang.enums.RedisKeyEnum;
import co.yixiang.modules.shop.entity.MdPharmacistService;
import co.yixiang.modules.shop.entity.Product4project;
import co.yixiang.modules.shop.service.*;
import co.yixiang.modules.shop.web.param.YxSystemStoreQueryParam;
import co.yixiang.modules.shop.web.param.YxUserSearchQueryParam;
import co.yixiang.modules.shop.web.vo.CountryChildVo;
import co.yixiang.modules.shop.web.vo.CountryVo;
import co.yixiang.modules.shop.web.vo.YxSystemStoreQueryVo;
import co.yixiang.modules.shop.web.vo.YxUserSearchQueryVo;
import co.yixiang.modules.xikang.service.XkProcessService;
import co.yixiang.modules.yiyaobao.entity.MdCountry;
import co.yixiang.modules.yiyaobao.service.MdCountryService;
import co.yixiang.mp.domain.DictDetail;
import co.yixiang.mp.rest.param.DictDetailQueryParam;
import co.yixiang.mp.service.DictDetailService;
import co.yixiang.utils.FileUtil;
import co.yixiang.utils.RedisUtil;
import co.yixiang.utils.SecurityUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName IndexController
 * @Author hupeng <610796224@qq.com>
 * @Date 2019/10/19
 **/

@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Api(value = "首页模块", tags = "商城:首页模块", description = "首页模块")
public class IndexController {

    private final YxSystemGroupDataService systemGroupDataService;
    private final YxStoreProductService storeProductService;
    private final YxSystemStoreService systemStoreService;
    private final MdPharmacistServiceService pharmacistService;

    @Autowired
    private XkProcessService xkProcessService;
    @Autowired
    private MdCountryService countryService;

    @Autowired
    private DictDetailService dictDetailService;

    @AnonymousAccess
   // @Cacheable(cacheNames = ShopConstants.YSHOP_REDIS_INDEX_KEY)
    @GetMapping("/index")
    @ApiOperation(value = "首页数据",notes = "首页数据")
    public ApiResult<Map<String,Object>> index(@RequestParam(value = "",required = false) String projectCode){

        Map<String,Object> map = new LinkedHashMap<>();
        //banner
        map.put("banner",systemGroupDataService.getDatas(ShopConstants.YSHOP_HOME_BANNER));

        // 我要找药中的banner
       // map.put("findDrugBanner",systemGroupDataService.getDatas(ShopConstants.YSHOP_HOME_FIND_DRUG_BANNER));

        //首页按钮
        map.put("menus",systemGroupDataService.getDatas(ShopConstants.YSHOP_HOME_MENUS));
        //首页活动区域图片
        map.put("activity",new String[]{});


        //精品推荐
        map.put("bastList",storeProductService.getList(1,6,1));
        //首发新品
        map.put("firstList",storeProductService.getList(1,6,3));
        //促销单品
        map.put("benefit",storeProductService.getList(1,3,4));
        //热门榜单
        map.put("likeInfo",storeProductService.getList(1,3,2));

        //滚动
        map.put("roll",systemGroupDataService.getDatas(ShopConstants.YSHOP_HOME_ROLL_NEWS));

        map.put("mapKey",RedisUtil.get(RedisKeyEnum.TENGXUN_MAP_KEY.getValue()));

        map.put("article","相关资讯");

        DictDetailQueryParam dictDetailQueryParam = new DictDetailQueryParam();
        dictDetailQueryParam.setName("serviceGroupId");
        String label = "yiyao";
        if(StrUtil.isNotBlank(projectCode)) {
            label = projectCode;
        }
        dictDetailQueryParam.setLabel(label);
        List<DictDetail> dictDetailList = dictDetailService.queryAll(dictDetailQueryParam);
        String serviceGroupId = "";
        if(CollUtil.isNotEmpty(dictDetailList)) {
            serviceGroupId = dictDetailList.get(0).getValue();
        }
        map.put("serviceGroupId",serviceGroupId);
        return ApiResult.ok(map);
    }




    @AnonymousAccess
    @GetMapping("/search/keyword")
    @ApiOperation(value = "热门搜索关键字获取",notes = "热门搜索关键字获取")
    public ApiResult<List<String>> search(){
        List<Map<String,Object>> list = systemGroupDataService.getDatas(ShopConstants.YSHOP_HOT_SEARCH);
        List<String>  stringList = new ArrayList<>();
        for (Map<String,Object> map : list) {
            stringList.add(map.get("title").toString());
        }
        return ApiResult.ok(stringList);
    }

    @AnonymousAccess
    @PostMapping("/image_base64")
    @ApiOperation(value = "获取图片base64",notes = "获取图片base64")
    @Deprecated
    public ApiResult<List<String>> imageBase64(){
        return ApiResult.ok(null);
    }

    @AnonymousAccess
    @GetMapping("/citys")
    @ApiOperation(value = "获取城市json",notes = "获取城市json")
    public ApiResult<String> cityJson(){
        String path = "city.json";
        String name = "city.json";
        try {
            File file = FileUtil.inputStreamToFile(new ClassPathResource(path).getStream(), name);
            FileReader fileReader = new FileReader(file,"UTF-8");
            String string = fileReader.readString();
            System.out.println(string);
            JSONObject jsonObject = JSON.parseObject(string);
            return ApiResult.ok(jsonObject);
        } catch (Exception e) {
            e.printStackTrace();

            return ApiResult.fail("无数据");
        }

    }



    @AnonymousAccess
    @GetMapping("/store_list")
    @ApiOperation(value = "获取门店列表",notes = "获取门店列表")
    public ApiResult<Object> storeList( YxSystemStoreQueryParam param){
        if(StrUtil.isNotBlank(param.getProvinceName()) && "全国药房".equals(param.getProvinceName())) {
            param.setProvinceName("");
        }
        Map<String,Object> map = new LinkedHashMap<>();
        List<YxSystemStoreQueryVo> lists;
        if(StrUtil.isBlank(param.getLatitude()) || StrUtil.isBlank(param.getLongitude())){
            lists = systemStoreService.getYxSystemStorePageList(param).getRecords();
        }else{
            lists = systemStoreService.getStoreList(
                    param.getLatitude(),
                    param.getLongitude(),
                    param.getPage(),param.getLimit(),param.getSelectCountrys(),param.getKeyword(),param.getProvinceName());
        }
        for(YxSystemStoreQueryVo systemStoreQueryVo:lists) {
            List<MdPharmacistService> pharmacists = pharmacistService.list(new QueryWrapper<MdPharmacistService>().eq("FOREIGN_ID",systemStoreQueryVo.getId()).isNotNull("uid"));
            systemStoreQueryVo.setPharmacists(pharmacists);
        }
        map.put("list",lists);
       // map.put("mapKey",RedisUtil.get("tengxun_map_key"));
        return ApiResult.ok(map);

    }


    @AnonymousAccess
    @PostMapping("/store_list")
    @ApiOperation(value = "获取门店列表",notes = "获取门店列表")
    public ApiResult<Object> storeList_post(@RequestBody YxSystemStoreQueryParam param){
        Map<String,Object> map = new LinkedHashMap<>();
        List<YxSystemStoreQueryVo> lists;

        if(NumberUtil.isNumber(param.getLatitude()) && NumberUtil.isNumber(param.getLongitude())) {
            lists = systemStoreService.getStoreList(
                    param.getLatitude(),
                    param.getLongitude(),
                    param.getPage(),param.getLimit(),param.getSelectCountrys(),param.getKeyword(),param.getProvinceName());
        } else  {
            lists = systemStoreService.getYxSystemStorePageList(param).getRecords();
        }

        for(YxSystemStoreQueryVo systemStoreQueryVo:lists) {
           List<MdPharmacistService> pharmacists = pharmacistService.list(new QueryWrapper<MdPharmacistService>().eq("FOREIGN_ID",systemStoreQueryVo.getId()).isNotNull("uid"));
            systemStoreQueryVo.setPharmacists(pharmacists);
        }

        map.put("list",lists);
        // map.put("mapKey",RedisUtil.get("tengxun_map_key"));
        return ApiResult.ok(map);

    }

    @AnonymousAccess
    @GetMapping("/store_list_product")
    @ApiOperation(value = "根据药品Id获取门店列表",notes = "根据药品Id获取门店列表")
    public ApiResult<Object> storeListByProductId( YxSystemStoreQueryParam param){
        Map<String,Object> map = new LinkedHashMap<>();
        List<YxSystemStoreQueryVo> lists;
        if(StrUtil.isBlank(param.getLatitude()) || StrUtil.isBlank(param.getLongitude())){
            lists = systemStoreService.getYxSystemStorePageList(param).getRecords();
        }else{
            lists = systemStoreService.getStoreListByProductId(
                    param.getLatitude(),
                    param.getLongitude(),
                    param.getPage(),param.getLimit(),param.getProductId(),"");
        }

        map.put("list",lists);
        // map.put("mapKey",RedisUtil.get("tengxun_map_key"));
        return ApiResult.ok(map);

    }

    @AnonymousAccess
    @GetMapping("/provinceCity")
    @ApiOperation(value = "获取省市",notes = "获取省市")
    public ApiResult<String> provinceCity(){

        try {
            QueryWrapper queryWrapper = new QueryWrapper();
            queryWrapper.eq("tree_id",1);
            List<MdCountry> countryList = countryService.list(queryWrapper);
            List<CountryVo> countryVoList = new ArrayList<>();
            for(MdCountry country : countryList) {
                CountryVo countryVo = new CountryVo();
                countryVo.setText(country.getName());
                countryVo.setId(country.getCode());
                List<CountryChildVo> childVoList = new ArrayList<>();
                CountryChildVo childVo_all = new CountryChildVo();
                childVo_all.setId(country.getCode());
                childVo_all.setText("全部");
                childVo_all.setName(country.getName());
                childVoList.add(childVo_all);

                QueryWrapper queryWrapper1 = new QueryWrapper();
                queryWrapper1.eq("PARENT_ID",country.getId());
                List<MdCountry> children = countryService.list(queryWrapper1);
                for(MdCountry mdCountry : children) {
                    CountryChildVo childVo = new CountryChildVo();
                    childVo.setId(mdCountry.getCode());
                    childVo.setText(mdCountry.getName());
                    childVoList.add(childVo);
                }
                countryVo.setChildren(childVoList);
                countryVoList.add(countryVo);
            }
            return ApiResult.ok(countryVoList);
        } catch (Exception e) {
            e.printStackTrace();

            return ApiResult.fail("无数据");
        }

    }


    @AnonymousAccess
    @GetMapping("/test")
    @ApiOperation(value = "",notes = "")
    public ApiResult<String> test(){

        xkProcessService.h5Url4ApplyPrescriptionTest();

        return  ApiResult.ok();
    }

    @AnonymousAccess
    @GetMapping("/test2")
    @ApiOperation(value = "",notes = "")
    public ApiResult<String> test2(){

        xkProcessService.h5Url4doctorTest();

        return  ApiResult.ok();
    }
}
