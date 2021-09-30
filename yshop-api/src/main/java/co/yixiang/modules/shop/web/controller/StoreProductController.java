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
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.qrcode.QrCodeUtil;
import cn.hutool.http.HttpUtil;
import co.yixiang.annotation.AnonymousAccess;
import co.yixiang.constant.SystemConfigConstants;
import co.yixiang.enums.ProjectNameEnum;
import co.yixiang.enums.TaipingCardTypeEnum;
import co.yixiang.logging.aop.log.Log;
import co.yixiang.common.api.ApiResult;
import co.yixiang.common.web.controller.BaseController;
import co.yixiang.enums.AppFromEnum;
import co.yixiang.enums.ProductEnum;
import co.yixiang.modules.shop.entity.*;
import co.yixiang.modules.shop.service.*;
import co.yixiang.modules.shop.web.dto.ProductDTO;
import co.yixiang.modules.shop.web.param.YxStoreProductQueryParam;
import co.yixiang.modules.shop.web.param.YxStoreProductRelationQueryParam;
import co.yixiang.modules.shop.web.vo.YxStoreProductQueryVo;
import co.yixiang.modules.user.entity.YxSystemAttachment;
import co.yixiang.modules.user.service.YxSystemAttachmentService;
import co.yixiang.modules.user.service.YxUserService;
import co.yixiang.modules.user.web.vo.YxUserQueryVo;
import co.yixiang.mp.domain.DictDetail;
import co.yixiang.mp.rest.param.DictDetailQueryParam;
import co.yixiang.mp.service.impl.DictDetailServiceImpl;
import co.yixiang.utils.OrderUtil;
import co.yixiang.utils.SecurityUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.List;

/**
 * <p>
 * 商品控制器
 * </p>
 *
 * @author hupeng
 * @since 2019-10-19
 */
@Slf4j
@RestController
@Api(value = "产品模块", tags = "商城:产品模块", description = "产品模块")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class StoreProductController extends BaseController {

    private final YxStoreProductService storeProductService;
    private final YxStoreProductRelationService productRelationService;
    private final YxStoreProductReplyService replyService;
    private final YxSystemConfigService systemConfigService;
    private final YxSystemAttachmentService systemAttachmentService;
    private final YxUserService yxUserService;
    private final CreatShareProductService creatShareProductService;
    @Value("${file.path}")
    private String path;

    @Value("${file.localUrl}")
    private String localUrl;
    @Autowired
    private YxUserService userService;

    @Autowired
    private DictDetailServiceImpl dictDetailService;

    @Autowired
    private Product4projectService product4projectService;


    @Autowired
    private ProjectService projectService;

    @Autowired
    private YxStoreProductAttrValueService yxStoreProductAttrValueService;
    /**
     * 获取首页更多产品
     */
    @AnonymousAccess
    @GetMapping("/groom/list/{type}")
    @ApiOperation(value = "获取首页更多产品",notes = "获取首页更多产品")
    public ApiResult<Map<String,Object>> moreGoodsList(@PathVariable Integer type){
        Map<String,Object> map = new LinkedHashMap<>();
        if(type.equals(ProductEnum.TYPE_1.getValue())){// 精品推荐
            map.put("list",storeProductService.getList(1,20,1));
        }else if(type.equals(ProductEnum.TYPE_2.getValue())){// 热门榜单
            map.put("list",storeProductService.getList(1,20,2));
        }else if(type.equals(ProductEnum.TYPE_3.getValue())){// 首发新品
            map.put("list",storeProductService.getList(1,20,3));
        }else if(type.equals(ProductEnum.TYPE_4.getValue())){// 促销单品
            map.put("list",storeProductService.getList(1,20,4));
        }

        return ApiResult.ok(map);
    }

    /**
     * 获取首页更多产品
     */
    @AnonymousAccess
    @GetMapping("/products")
    @ApiOperation(value = "商品列表",notes = "商品列表")
    public ApiResult<List<YxStoreProductQueryVo>> goodsList(YxStoreProductQueryParam productQueryParam){
        return ApiResult.ok(storeProductService.getGoodsList(productQueryParam));
    }

    /**
     * 为你推荐
     */
    @AnonymousAccess
    @GetMapping("/product/hot")
    @ApiOperation(value = "为你推荐",notes = "为你推荐")
    public ApiResult<List<YxStoreProductQueryVo>> productRecommend(YxStoreProductQueryParam queryParam){
        return ApiResult.ok(storeProductService.getList(queryParam.getPage().intValue(),
                queryParam.getLimit().intValue(),1));
    }


    /**
     * 商品详情海报
     */
    @GetMapping("/product/poster/{id}")
    @ApiOperation(value = "商品详情海报",notes = "商品详情海报")
    public ApiResult<String> prodoctPoster(@PathVariable Integer id,
                                           @RequestParam(value = "",required = false) String projectCode,
                                           @RequestParam(value = "",required = false) String unique
                                           ) throws IOException, FontFormatException {
        int uid = SecurityUtils.getUserId().intValue();

        YxStoreProduct storeProduct = storeProductService.getProductInfo(id);
        BigDecimal unitPrice = null;
        if(StrUtil.isNotBlank(projectCode) && StrUtil.isNotBlank(unique)) {
            LambdaQueryWrapper<Product4project> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(Product4project::getProjectNo,projectCode);
            lambdaQueryWrapper.eq(Product4project::getProductUniqueId,unique);
            lambdaQueryWrapper.eq(Product4project::getIsShow,1);
            lambdaQueryWrapper.eq(Product4project::getIsDel,0);
            Product4project product4project = product4projectService.getOne(lambdaQueryWrapper,false);
            if(product4project != null) {
                unitPrice= product4project.getUnitPrice();
            }
        }

        if(unitPrice == null) {
            if(StrUtil.isNotBlank(unique)) {
                LambdaQueryWrapper<YxStoreProductAttrValue> lambdaQueryWrapper = new LambdaQueryWrapper<>();
                lambdaQueryWrapper.eq(YxStoreProductAttrValue::getUnique,unique);
                lambdaQueryWrapper.eq(YxStoreProductAttrValue::getIsDel,0);
                lambdaQueryWrapper.select(YxStoreProductAttrValue::getPrice);
                YxStoreProductAttrValue yxStoreProductAttrValue = yxStoreProductAttrValueService.getOne(lambdaQueryWrapper,false);
                if(yxStoreProductAttrValue != null) {
                    unitPrice = yxStoreProductAttrValue.getPrice();
                }
            }
        }

        if(unitPrice != null) {
            storeProduct.setOtPrice(unitPrice);
            storeProduct.setPrice(unitPrice);
            storeProduct.setVipPrice(unitPrice);
        }


        // 海报
        String siteUrl = systemConfigService.getData(SystemConfigConstants.SITE_URL);
        if(StrUtil.isEmpty(siteUrl)){
            return ApiResult.fail("未配置h5地址");
        }
        String apiUrl = systemConfigService.getData(SystemConfigConstants.API_URL);
        if(StrUtil.isEmpty(apiUrl)){
            return ApiResult.fail("未配置api地址");
        }
        log.info("siteUrl----:"+siteUrl);
        log.info("apiUrl----:"+apiUrl);

        YxUserQueryVo userInfo = yxUserService.getYxUserById(uid);
        String userType = userInfo.getUserType();
        if(!userType.equals(AppFromEnum.ROUNTINE.getValue())) {
            userType = AppFromEnum.H5.getValue();
        }
        String name = id+"_"+uid + "_"+userType+"_" + projectCode+ "_" + unique + "_product_detail_wap.jpg";
        YxSystemAttachment attachment = systemAttachmentService.getInfo(name);
        String fileDir = path+"qrcode"+ File.separator;
        String qrcodeUrl = "";
        if(ObjectUtil.isNull(attachment)){
            File file = FileUtil.mkdir(new File(fileDir));
            //如果类型是小程序
            if(userType.equals(AppFromEnum.ROUNTINE.getValue())){
                //h5地址
                siteUrl = siteUrl+"/product/";
                //生成二维码
                QrCodeUtil.generate(siteUrl+"?productId="+id+"&spread="+uid+"&pageType=good&codeType="+AppFromEnum.ROUNTINE.getValue(), 180, 180,
                        FileUtil.file(fileDir+name));
            }
            else if(userType.equals(AppFromEnum.APP.getValue())){
                //h5地址
                siteUrl = siteUrl+"/product/";
                //生成二维码
                QrCodeUtil.generate(siteUrl+"?productId="+id+"&spread="+uid+"&pageType=good&codeType="+AppFromEnum.APP.getValue(), 180, 180,
                        FileUtil.file(fileDir+name));
            }else{//如果类型是h5
                //生成二维码
                /*QrCodeUtil.generate(siteUrl+"detail/"+id+"?spread="+uid, 180, 180,
                        FileUtil.file(fileDir+name));*/
                QrCodeUtil.generate(siteUrl+"#/pages/wode/goodsDetail?id="+id, 180, 180,
                        FileUtil.file(fileDir+name));
            }
            systemAttachmentService.attachmentAdd(name,String.valueOf(FileUtil.size(file)),
                    fileDir+name,"qrcode/"+name);

            qrcodeUrl = apiUrl + "/api/file/qrcode/"+name;
        }else{
            qrcodeUrl = apiUrl + "/api/file/" + attachment.getSattDir();
        }
        log.info("qrcodeUrl----:"+qrcodeUrl);
        String spreadPicName = id+"_"+uid + "_"+userType+"_" + projectCode+ "_" + unique +"_product_user_spread.jpg";
        String spreadPicPath = fileDir+spreadPicName;
        String rr =  creatShareProductService.creatProductPic(storeProduct,qrcodeUrl,
                spreadPicName,spreadPicPath,apiUrl);
        //productDTO.getStoreInfo().setCodeBase(rr);
        return ApiResult.ok(rr);
    }




    /**
     * 普通商品详情
     */
    @Log(value = "查看商品详情",type = 1)
    @GetMapping("/product/detail/{id}")
    @ApiOperation(value = "普通商品详情",notes = "普通商品详情")
     @AnonymousAccess
    public ApiResult<YxStoreProductQueryVo> detail(@PathVariable Integer id,
                                        @RequestParam(value = "",required=false) String latitude,
                                        @RequestParam(value = "",required=false) String longitude,
                                        @RequestParam(value = "",required=false) String from,
                                        @RequestParam(value = "",required = false) String uniqueId,
                                                   @RequestParam(value = "",required = false) String projectCode,
                                                   @RequestParam(value = "",required = false) String cardNumber,
                                                   @RequestParam(value = "",required = false) String cardType,
                                                   @RequestParam(value = "",required = false) String partnerCode
                                                   )  {
        int uid = 0;
        try {
             uid = SecurityUtils.getUserId().intValue();
        }catch (Exception e) {

        }

        log.info("商品详情入参：  projectcode={},cardType={},cardNumber={}",projectCode,cardType,cardNumber);

        // 如果是太平尊享会员，优惠券发放
      /*  if( uid !=0 && ProjectNameEnum.TAIPING_LEXIANG.getValue().equals(projectCode) && TaipingCardTypeEnum.card_advanced.getValue().equals(cardType) && StrUtil.isNotBlank(cardNumber)){
            yxUserService.sendCouponToUser(uid,cardNumber);
        }*/

        // 根据partnerCode 查药店id
        List<Integer> storeIds = new ArrayList<>();
        /*if(StrUtil.isNotBlank(partnerCode)) {
            Hospital hospital = hospitalService.getOne(new LambdaQueryWrapper<Hospital>().eq(Hospital::getCode,partnerCode),false);
            if(hospital != null && StrUtil.isNotBlank(hospital.getStoreIds())) {
                for(String str : hospital.getStoreIds().split(","))  {
                    storeIds.add(Integer.valueOf(str));
                }
            }
        }*/
        String serviceGroupId = "";
    /*    LambdaQueryWrapper<YxStoreProduct> lambdaQueryWrapper = new LambdaQueryWrapper<YxStoreProduct>();
        lambdaQueryWrapper.eq(YxStoreProduct::getId,id);
        lambdaQueryWrapper.select(YxStoreProduct::getIsNeedCloudProduce,YxStoreProduct::getIsGroup);
        YxStoreProduct yxStoreProduct = storeProductService.getOne(lambdaQueryWrapper,false);
        */

        if("null".equals(projectCode)) {
            projectCode = "";
        }

        if(StrUtil.isNotBlank(projectCode)) {
            Project project = projectService.getOne(new LambdaQueryWrapper<Project>().eq(Project::getProjectCode,projectCode),false);

            if(project!=null && StrUtil.isNotBlank(project.getStoreIds())) {
                List<String> storeIdList = Arrays.asList(project.getStoreIds().split(","));
                for(String storeId:storeIdList) {
                    storeIds.add(Integer.valueOf(storeId));
                }
            }
            serviceGroupId = project.getServiceGroupId();

        } else {
            Project project = projectService.getOne(new LambdaQueryWrapper<Project>().eq(Project::getProjectName,"益药商城").select(Project::getServiceGroupId),false);
            if(project !=null) {
                serviceGroupId = project.getServiceGroupId();
            } else {
                serviceGroupId = "480635963";
            }

        }


        YxStoreProductQueryVo productDTO = storeProductService.goodsDetail4Store(id,0,uid,latitude,longitude,uniqueId,projectCode,cardNumber,cardType,storeIds);

        if(ProjectNameEnum.TAIPING_LEXIANG.getValue().equals(projectCode) || ProjectNameEnum.ROCHE_SMA.getValue().equals(projectCode)) {
            productDTO.setFlowImagePath( localUrl +   "/file/static/flow2.png");
        }else if(ProjectNameEnum.LINGYUANZHI.getValue().equals(projectCode)) {

        } else {
            productDTO.setFlowImagePath(localUrl +   "/file/static/flow1.png");
        }



        productDTO.setServiceGroupId(serviceGroupId);

        // 获取网易七鱼的客服组号
     /*   DictDetailQueryParam dictDetailQueryParam = new DictDetailQueryParam();
        dictDetailQueryParam.setName("serviceGroupId");
        String label = "yiyao";
        if(ProjectNameEnum.TAIPING_LEXIANG.getValue().equals(projectCode)) {
            label = projectCode;
        } else {
            LambdaQueryWrapper<Product4project> lambdaQueryWrapper = new LambdaQueryWrapper();
            lambdaQueryWrapper.eq(Product4project::getProductId,productDTO.getId());
            Product4project product4project =  product4projectService.getOne(lambdaQueryWrapper,false);
            if(product4project != null) {
                label = product4project.getProjectNo();
            }
        }

        dictDetailQueryParam.setLabel(label);
        List<DictDetail> dictDetailList = dictDetailService.queryAll(dictDetailQueryParam);
        if(CollUtil.isNotEmpty(dictDetailList)) {
            productDTO.setServiceGroupId(dictDetailList.get(0).getValue());
        }*/

        return ApiResult.ok(productDTO);
    }

    /**
     * 添加收藏
     */
    @Log(value = "添加收藏",type = 1)
    @PostMapping("/collect/add")
    @ApiOperation(value = "添加收藏",notes = "添加收藏")
    public ApiResult<Object> collectAdd(@Validated @RequestBody YxStoreProductRelationQueryParam param){
        int uid = SecurityUtils.getUserId().intValue();
        productRelationService.addRroductRelation(param,uid,"collect");
        return ApiResult.ok("success");
    }

    /**
     * 取消收藏
     */
    @Log(value = "取消收藏",type = 1)
    @PostMapping("/collect/del")
    @ApiOperation(value = "取消收藏",notes = "取消收藏")
    public ApiResult<Object> collectDel(@Validated @RequestBody YxStoreProductRelationQueryParam param){
        int uid = SecurityUtils.getUserId().intValue();
        productRelationService.delRroductRelation(param,uid,"collect");
        return ApiResult.ok("success");
    }

    /**
     * 获取产品评论
     */
    @GetMapping("/reply/list/{id}")
    @ApiOperation(value = "获取产品评论",notes = "获取产品评论")
    public ApiResult<Object> replyList(@PathVariable Integer id,
                                       YxStoreProductQueryParam queryParam){
        return ApiResult.ok(replyService.getReplyList(id,Integer.valueOf(queryParam.getType()),
                queryParam.getPage().intValue(),queryParam.getLimit().intValue()));
    }

    /**
     * 获取产品评论数据
     */
    @GetMapping("/reply/config/{id}")
    @ApiOperation(value = "获取产品评论数据",notes = "获取产品评论数据")
    public ApiResult<Object> replyCount(@PathVariable Integer id){
        return ApiResult.ok(replyService.getReplyCount(id));
    }

    /**
     * 获取产品列表（多药店）
     */
    @AnonymousAccess
    @GetMapping("/products4Store")
    @ApiOperation(value = "商品列表-多门店",notes = "商品列表-多门店")
    public ApiResult<List<YxStoreProductQueryVo>> goodsList4Store(YxStoreProductQueryParam productQueryParam){
        log.info("productQueryParam=" + productQueryParam);

        List<YxStoreProductQueryVo> result = storeProductService.getGoodsList4Store(productQueryParam);
        String msg = "操作成功";

        return ApiResult.ok(result,msg);

    }


}

