/**
 * Copyright (C) 2018-2019
 * All rights reserved, Designed By www.yixiang.co
 * 注意：
 * 本软件为www.yixiang.co开发研制，未经购买不得使用
 * 购买后可获得全部源代码（禁止转卖、分享、上传到码云、github等开源平台）
 * 一经发现盗用、分享等行为，将追究法律责任，后果自负
 */
package co.yixiang.modules.shop.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import co.yixiang.common.service.impl.BaseServiceImpl;
import co.yixiang.common.web.vo.Paging;
import co.yixiang.constant.ShopConstants;
import co.yixiang.enums.*;
import co.yixiang.exception.ErrorRequestException;
import co.yixiang.modules.monitor.service.RedisService;
import co.yixiang.modules.shop.entity.*;
import co.yixiang.modules.shop.mapper.YxStoreProductAttrValueMapper;
import co.yixiang.modules.shop.mapper.YxStoreProductMapper;
import co.yixiang.modules.shop.mapping.YxStoreProductGroupMap;
import co.yixiang.modules.shop.mapping.YxStoreProductMap;
import co.yixiang.modules.shop.service.*;
import co.yixiang.modules.shop.web.dto.PriceMinMaxDTO;
import co.yixiang.modules.shop.web.dto.ProductDTO;
import co.yixiang.modules.shop.web.param.YxStoreProductQueryParam;
import co.yixiang.modules.shop.web.vo.YxStoreProductAttrQueryVo;
import co.yixiang.modules.shop.web.vo.YxStoreProductGroupQueryVo;
import co.yixiang.modules.shop.web.vo.YxStoreProductQueryVo;
import co.yixiang.modules.shop.web.vo.YxSystemStoreQueryVo;
import co.yixiang.modules.taiping.entity.TaipingCard;
import co.yixiang.modules.taiping.service.TaipingCardService;
import co.yixiang.modules.user.entity.YxUser;
import co.yixiang.modules.user.entity.YxWechatUser;
import co.yixiang.modules.user.service.YxUserService;
import co.yixiang.modules.user.service.YxWechatUserService;
import co.yixiang.modules.user.web.vo.YxUserQueryVo;
import co.yixiang.modules.yiyaobao.entity.ProductStoreMapping;
import co.yixiang.modules.yiyaobao.service.ProductStoreMappingService;
import co.yixiang.utils.OrderUtil;
import co.yixiang.utils.PinYinUtils;
import co.yixiang.utils.RedisUtil;
import co.yixiang.utils.SecurityUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Struct;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static co.yixiang.constant.ShopConstants.INNER_DISCOUNT_RATE;


/**
 * <p>
 * 商品表 服务实现类
 * </p>
 *
 * @author hupeng
 * @since 2019-10-19
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
@SuppressWarnings("unchecked")
public class YxStoreProductServiceImpl extends BaseServiceImpl<YxStoreProductMapper, YxStoreProduct> implements YxStoreProductService {

    @Autowired
    private YxStoreProductMapper yxStoreProductMapper;
    @Autowired
    private YxStoreProductAttrValueMapper storeProductAttrValueMapper;

    @Autowired
    private YxStoreProductAttrService storeProductAttrService;
    @Autowired
    private YxStoreProductRelationService relationService;
    @Autowired
    private YxStoreProductReplyService replyService;
    @Autowired
    @Lazy
    private YxUserService userService;
    @Autowired
    private YxSystemStoreService systemStoreService;

    @Autowired
    private YxStoreProductMap storeProductMap;
    @Autowired
    private YxSystemStoreService storeService;

    @Autowired
    private YxUserSearchService yxUserSearchService;

    @Autowired
    private MdPharmacistServiceService mdPharmacistService;

    @Autowired
    private YxStoreProductRelationService yxStoreProductRelationService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private YxStoreProductGroupService yxStoreProductGroupService;

    @Autowired
    private YxStoreProductGroupMap yxStoreProductGroupMap;

    /**
     * 增加库存 减少销量
     * @param num
     * @param productId
     * @param unique
     */
    @Override
    public void incProductStock(int num, int productId, String unique) {
        if(StrUtil.isNotEmpty(unique)){
            storeProductAttrService.incProductAttrStock(num,productId,unique);
            yxStoreProductMapper.decSales(num,productId);
        }else{
            yxStoreProductMapper.incStockDecSales(num,productId);
        }
    }

    /**
     * 库存与销量
     * @param num
     * @param productId
     * @param unique
     */
    @Override
    public void decProductStock(int num, int productId, String unique) {
        if(StrUtil.isNotEmpty(unique)){
            storeProductAttrService.decProductAttrStock(num,productId,unique);
            yxStoreProductMapper.incSales(num,productId);
        }else{
            yxStoreProductMapper.decStockIncSales(num,productId);
        }
    }

    /**
     * 返回商品库存
     * @param productId
     * @param unique
     * @return
     */
    @Override
    public int getProductStock(int productId, String unique) {
        if(StrUtil.isEmpty(unique)){
            return getYxStoreProductById(productId).getStock();
        }else{
            return storeProductAttrService.uniqueByStock(unique);
        }

    }

    @Override
    public YxStoreProduct getProductInfo(int id) {
        QueryWrapper<YxStoreProduct> wrapper = new QueryWrapper<>();
        wrapper.eq("is_del",0).eq("is_show",1).eq("id",id);
        YxStoreProduct storeProduct = yxStoreProductMapper.selectOne(wrapper);
        if(ObjectUtil.isNull(storeProduct)){
            throw new ErrorRequestException("商品不存在或已下架");
        }

        return storeProduct;
    }

    @Override
    public ProductDTO goodsDetail(int id, int type,int uid,String latitude,String longitude) {
        QueryWrapper<YxStoreProduct> wrapper = new QueryWrapper<>();
        wrapper.eq("is_del",0).eq("is_show",1).eq("id",id);
        YxStoreProduct storeProduct = yxStoreProductMapper.selectOne(wrapper);
        if(ObjectUtil.isNull(storeProduct)){
            throw new ErrorRequestException("商品不存在或已下架");
        }
        Map<String, Object> returnMap = storeProductAttrService.getProductAttrDetail(id,0,0);
        ProductDTO productDTO = new ProductDTO();
        YxStoreProductQueryVo storeProductQueryVo  = storeProductMap.toDto(storeProduct);

        //处理库存
        Integer newStock = storeProductAttrValueMapper.sumStock(id);
        if(newStock != null)  storeProductQueryVo.setStock(newStock);

        //设置VIP价格
        double vipPrice = userService.setLevelPrice(
                storeProductQueryVo.getPrice().doubleValue(),uid);
        storeProductQueryVo.setVipPrice(BigDecimal.valueOf(vipPrice));
        storeProductQueryVo.setUserCollect(relationService
                .isProductRelation(id,"product",uid,"collect",""));
        productDTO.setStoreInfo(storeProductQueryVo);
        productDTO.setProductAttr((List<YxStoreProductAttrQueryVo>)returnMap.get("productAttr"));
        productDTO.setProductValue((Map<String, YxStoreProductAttrValue>)returnMap.get("productValue"));

        productDTO.setReply(replyService.getReply(id));
        int replyCount = replyService.productReplyCount(id);
        productDTO.setReplyCount(replyCount);
        productDTO.setReplyChance(replyService.doReply(id,replyCount));//百分比

        //门店
        productDTO.setSystemStore(systemStoreService.getStoreInfo(latitude,longitude));
        productDTO.setMapKey(RedisUtil.get(RedisKeyEnum.TENGXUN_MAP_KEY.getValue()));

        return productDTO;
    }

    @Override
    public YxStoreProductQueryVo goodsDetail4Store(int id, int type,int uid,String latitude,String longitude,String uniqueId,String projectCode,String cardNumber,String cardType,List<Integer> storeIds) {
        if(!NumberUtil.isNumber(latitude)){
            latitude = "";
        }
        if(!NumberUtil.isNumber(longitude)){
            longitude = "";
        }
        QueryWrapper<YxStoreProduct> wrapper = new QueryWrapper<>();
        wrapper.eq("is_del",0).eq("id",id);
        wrapper.select("id","image","slider_image","store_name","store_info","common_name","license_number","drug_form","spec","manufacturer","storage_condition","unit","indication","quality_period","contraindication","label1","label2","label3","yiyaobao_sku","pregnancy_lactation_directions","children_directions","elderly_patient_directions","type","description","is_group","is_need_cloud_produce");
        if(CollUtil.isNotEmpty(storeIds)) {
            // 限制药店
            String ids = CollUtil.join(storeIds,",");
            wrapper.exists("SELECT 1 FROM yx_store_product_attr_value  WHERE yx_store_product_attr_value.product_id = yx_store_product.id  and yx_store_product_attr_value.is_del = 0 and yx_store_product_attr_value.stock > 0 AND yx_store_product_attr_value.store_id in ( "+ ids +")");
        } else if( StrUtil.isNotBlank(projectCode)  ) {
          //  String str = "'"+projectCode + "'" + ',' + "'"+ ProjectNameEnum.HEALTHSTORE.getValue() + "'";

          //  String str = "'"+projectCode + "'" ;
            /*项目 限制药品*/
            wrapper.exists("SELECT 1 FROM product4project p  WHERE p.product_id = yx_store_product.id and p.is_del = 0 and p.is_show = 1 and p.project_no = '"+ projectCode +"'");
           // wrapper.exists("SELECT 1 FROM product4project p  WHERE p.product_id = yx_store_product.id and p.is_del = 0 and p.is_show = 1 and p.project_no in ("+ str +")");
        } else {
            wrapper.exists("SELECT 1 FROM yx_store_product_attr_value  WHERE yx_store_product_attr_value.product_id = yx_store_product.id  and yx_store_product_attr_value.is_del = 0 and yx_store_product_attr_value.stock > 0 AND yx_store_product_attr_value.suk != '" + ShopConstants.STORENAME_GUANGZHOU_CLOUD +"' ");
        }


        YxStoreProduct storeProduct = yxStoreProductMapper.selectOne(wrapper);
        if(ObjectUtil.isNull(storeProduct)){
            throw new ErrorRequestException("商品不存在或已下架");
        }

        if(storeProduct != null && storeProduct.getIsNeedCloudProduce() != null && storeProduct.getIsNeedCloudProduce() == 1) {
            projectCode = ProjectNameEnum.ROCHE_SMA.getValue();
        }

        YxStoreProductQueryVo yxStoreProductQueryVo = storeProductMap.toDto(storeProduct);

        List<YxSystemStoreQueryVo> storeList = new ArrayList<>();
        if(StrUtil.isNotBlank(latitude) && StrUtil.isNotBlank(longitude)) {
            if(CollUtil.isNotEmpty(storeIds)) {
                storeList  = storeService.getStoreListByProductIdStoreIds(latitude,longitude,1,200,id,storeIds);
            }else  {
                storeList  = storeService.getStoreListByProductId(latitude,longitude,1,200,id,projectCode);
            }
        } else {  // 没有经纬度
            if(CollUtil.isNotEmpty(storeIds)) {
              //  storeList  = storeService.getStoreListByProductIdStoreIds(latitude,longitude,1,200,id,storeIds);
                storeList  = storeService.getStoreListByProductIdStoreIdsNoGPS(1,200,id,storeIds);
            } else {
                storeList  = storeService.getStoreListByProductIdNoGPS(1,200,id,projectCode);
            }
        }
        if(CollUtil.isNotEmpty(storeList)) {   // 有配置了门店
            for(YxSystemStoreQueryVo systemStoreQueryVo:storeList) {
                YxStoreProductQueryVo productQueryVo = new YxStoreProductQueryVo();
                productQueryVo.setPrice(systemStoreQueryVo.getPrice());
                productQueryVo.setStoreNameReal(systemStoreQueryVo.getName());
                productQueryVo.setLabel1(storeProduct.getLabel1());
                productQueryVo.setLabel2(storeProduct.getLabel2());
                productQueryVo.setLabel3(storeProduct.getLabel3());

                // 查询会员价
                userService.getVipPriceByProjectNo(projectCode,cardNumber,cardType,uid,productQueryVo);

                systemStoreQueryVo.setVipPrice(productQueryVo.getVipPrice());
                systemStoreQueryVo.setUserLevel(productQueryVo.getUserLevel());
                // 获取药师列表
                // List<MdPharmacistService> pharmacists = mdPharmacistService.list(new QueryWrapper<MdPharmacistService>().eq("FOREIGN_ID",String.valueOf(systemStoreQueryVo.getId())).isNotNull("uid"));

                // systemStoreQueryVo.setPharmacists(pharmacists);
                systemStoreQueryVo.setBenefitsDesc(productQueryVo.getBenefitsDesc());
            }


            YxSystemStoreQueryVo store = storeList.get(0);
            if(ProjectNameEnum.ROCHE_SMA.getValue().equals(projectCode)) {
                store.setPhone("400-606-5711");
            }
            yxStoreProductQueryVo.setPrice(store.getPrice());
            yxStoreProductQueryVo.setStoreNameReal(store.getName());
            yxStoreProductQueryVo.setStoreIdReal(store.getId());
            yxStoreProductQueryVo.setDistance(store.getDistance());
            yxStoreProductQueryVo.setStock(store.getStock());
            yxStoreProductQueryVo.setUnique(store.getUnique());
            yxStoreProductQueryVo.setStoreList(storeList);
            yxStoreProductQueryVo.setInnerPrice(store.getInnerPrice());
            yxStoreProductQueryVo.setIsInner(0);
            yxStoreProductQueryVo.setVipPrice(store.getVipPrice());
            yxStoreProductQueryVo.setUserLevel(store.getUserLevel());
            yxStoreProductQueryVo.setPharmacists(store.getPharmacists());
            yxStoreProductQueryVo.setBenefitsDesc(store.getBenefitsDesc());
        }



        // 商品评论
        yxStoreProductQueryVo.setReply(replyService.getReply(id));
        int replyCount = replyService.productReplyCount(id);
        yxStoreProductQueryVo.setReplyCount(replyCount);
        yxStoreProductQueryVo.setReplyChance(replyService.doReply(id,replyCount));


        // 商品是否已经关注
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("uid",uid);
        queryWrapper.eq("product_id",id);
        queryWrapper.eq("type","collect");
        queryWrapper.eq("category","product");
        queryWrapper.eq("project_code",projectCode);
        int count =  yxStoreProductRelationService.count(queryWrapper);
        if(count >0 ) {
            yxStoreProductQueryVo.setUserCollect(true);
        }

        // 组合商品，展示子商品信息
        if(storeProduct.getIsGroup() != null && storeProduct.getIsGroup() == 1) {
            LambdaQueryWrapper<YxStoreProductGroup> yxStoreProductGroupLambdaQueryWrapper = new LambdaQueryWrapper<>();
            yxStoreProductGroupLambdaQueryWrapper.eq(YxStoreProductGroup::getParentProductId,storeProduct.getId());
            List<YxStoreProductGroupQueryVo> voList = yxStoreProductGroupMap.toDto(yxStoreProductGroupService.list(yxStoreProductGroupLambdaQueryWrapper));

            for(YxStoreProductGroupQueryVo vo :voList) {
                // 获取子商品的详细信息
                QueryWrapper<YxStoreProduct> queryWrapper1 = new QueryWrapper<>();
                queryWrapper1.eq("is_del",0).eq("id",vo.getProductId());
                queryWrapper1.select("id","image","slider_image","store_name","store_info","common_name","license_number","drug_form","spec","manufacturer","storage_condition","unit","indication","quality_period","contraindication","label1","label2","label3","yiyaobao_sku","pregnancy_lactation_directions","children_directions","elderly_patient_directions","type","description","is_group","is_need_cloud_produce");

                YxStoreProduct yxStoreProduct = this.getOne(queryWrapper1);
                if(yxStoreProduct != null) {
                    BeanUtil.copyProperties(yxStoreProduct,vo);
                }
            }
            yxStoreProductQueryVo.setGroupDetailList(voList);
        }

        return yxStoreProductQueryVo;
    }


    /**
     * 商品列表
     * @return
     */
    @Override
    public List<YxStoreProductQueryVo> getGoodsList(YxStoreProductQueryParam productQueryParam) {

        QueryWrapper<YxStoreProduct> wrapper = new QueryWrapper<>();
        wrapper.eq("is_del", CommonEnum.DEL_STATUS_0.getValue()).eq("is_show",CommonEnum.SHOW_STATUS_1.getValue());

        //分类搜索
        if(StrUtil.isNotBlank(productQueryParam.getSid()) && !productQueryParam.getSid().equals("0")){
            wrapper.eq("cate_id",productQueryParam.getSid());
        }
        //关键字搜索
        if(StrUtil.isNotEmpty(productQueryParam.getKeyword())){
            wrapper.like("store_name",productQueryParam.getKeyword());
        }

        //新品搜索
        if(StrUtil.isNotBlank(productQueryParam.getNews()) && productQueryParam.getNews().equals("1")){
            wrapper.eq("is_new",1);
        }
        //销量排序
        if(productQueryParam.getSalesOrder().equals("desc")){
            wrapper.orderByDesc("sales");
        }else if(productQueryParam.getSalesOrder().equals("asc")) {
            wrapper.orderByAsc("sales");
        }
        //价格排序
        if(productQueryParam.getPriceOrder().equals("desc")){
            wrapper.orderByDesc("price");
        }else if(productQueryParam.getPriceOrder().equals("asc")){
            wrapper.orderByAsc("price");
        }
        wrapper.orderByDesc("sort");
       /* String partnerId = productQueryParam.getPartnerId();
        if(StrUtil.isNotBlank(partnerId )) {
            wrapper.apply(" id IN (SELECT ppm.product_id FROM product_partner_mapping ppm WHERE  ppm.partner_id = {0})",Integer.valueOf(partnerId));
        }*/

        Page<YxStoreProduct> pageModel = new Page<>(productQueryParam.getPage(),
                productQueryParam.getLimit());

        IPage<YxStoreProduct> pageList = yxStoreProductMapper.selectPage(pageModel,wrapper);

        List<YxStoreProductQueryVo> list = storeProductMap.toDto(pageList.getRecords());

//        for (GoodsDTO goodsDTO : list) {
//            goodsDTO.setIsCollect(isCollect(goodsDTO.getGoodsId(),userId));
//        }

        return list;
    }

    /**
     * 商品列表
     * @param page
     * @param limit
     * @param order
     * @return
     */
    @Override
    public List<YxStoreProductQueryVo> getList(int page, int limit, int order) {

        QueryWrapper<YxStoreProduct> wrapper = new QueryWrapper<>();
        wrapper.eq("is_del",0).eq("is_show",1).orderByDesc("sort");
        wrapper.select("id","store_name","image","price","common_name","manufacturer","drug_form","spec","unit");
        // order
        switch (ProductEnum.toType(order)){
            case TYPE_1:
                wrapper.eq("is_best",1); //精品推荐
                break;
            case TYPE_3:
                wrapper.eq("is_new",1); //// 首发新品
                break;
            case TYPE_4:
                wrapper.eq("is_benefit",1); //// 促销单品
                break;
            case TYPE_2:
                wrapper.eq("is_hot",1);//// 热门榜单
                break;
        }

        wrapper.exists("select 1 from yx_store_product_attr_value yspav WHERE yx_store_product.id = yspav.product_id and yspav.stock > 0 AND yspav.is_del = 0 and yspav.suk != '"+ ShopConstants.STORENAME_GUANGZHOU_CLOUD +"'");

        Page<YxStoreProduct> pageModel = new Page<>(page, limit);

        IPage<YxStoreProduct> pageList = yxStoreProductMapper.selectPage(pageModel,wrapper);

        List<YxStoreProductQueryVo> list = storeProductMap.toDto(pageList.getRecords());
        for(YxStoreProductQueryVo yxStoreProductQueryVo:list) {
            Integer productId = yxStoreProductQueryVo.getId();
            PriceMinMaxDTO priceMinMaxDTO = storeProductAttrValueMapper.getPriceMinMax(productId,ShopConstants.STORENAME_GUANGZHOU_CLOUD);
            if(priceMinMaxDTO != null) {
                yxStoreProductQueryVo.setPrice(priceMinMaxDTO.getPriceMin());
            }

        }

        return list;
    }




    @Override
    public YxStoreProductQueryVo getYxStoreProductById(Serializable id){
        return yxStoreProductMapper.getYxStoreProductById(id);
    }

    @Override
    public YxStoreProductQueryVo getNewStoreProductById(int id) {
        return storeProductMap.toDto(yxStoreProductMapper.selectById(id));
    }

    @Override
    public Paging<YxStoreProductQueryVo> getYxStoreProductPageList(YxStoreProductQueryParam yxStoreProductQueryParam) throws Exception{
        Page page = setPageParam(yxStoreProductQueryParam,OrderItem.desc("create_time"));
        IPage<YxStoreProductQueryVo> iPage = yxStoreProductMapper.getYxStoreProductPageList(page,yxStoreProductQueryParam);
        return new Paging(iPage);
    }


    /**
     * 商品列表
     * @return
     */
    @Override
    public List<YxStoreProductQueryVo> getGoodsList4Store(YxStoreProductQueryParam productQueryParam) {

        // 搜索词保存
        if(StrUtil.isNotBlank(productQueryParam.getKeyword())) {
            try {
                int uid = SecurityUtils.getUserId().intValue();
                QueryWrapper queryWrapper = new QueryWrapper();
                queryWrapper.eq("uid",uid);
                queryWrapper.eq("keyword",productQueryParam.getKeyword());
                queryWrapper.eq("is_del",0);
                YxUserSearch yxUserSearch = yxUserSearchService.getOne(queryWrapper,false);
                if(yxUserSearch == null) {
                    yxUserSearch = new YxUserSearch();
                    yxUserSearch.setUid(uid);
                    yxUserSearch.setIsDel(0);
                    yxUserSearch.setKeyword(productQueryParam.getKeyword());
                    yxUserSearch.setAddTime(OrderUtil.getSecondTimestampTwo());

                }else {
                    yxUserSearch.setAddTime(OrderUtil.getSecondTimestampTwo());
                }
                yxUserSearchService.saveOrUpdate(yxUserSearch);
            }catch (Exception e) {

            }

        }






        //关键字搜索
        if(StrUtil.isNotEmpty(productQueryParam.getKeyword())){
          //  wrapper.like("store_name",productQueryParam.getKeyword());
            String pinYin = PinYinUtils.getHanziPinYin(productQueryParam.getKeyword());
            productQueryParam.setPinYin(pinYin);
        }



        /*门店 限制药品*/
        String storeIds = productQueryParam.getStoreIds();

         if( StrUtil.isNotBlank(productQueryParam.getProjectCode())  ) {   // 项目存在

           Project project = projectService.getOne(new LambdaQueryWrapper<Project>().eq(Project::getProjectCode,productQueryParam.getProjectCode()),false);
            if( StrUtil.isNotBlank(project.getStoreIds())) {  // 配置了门店信息，用门店的药品做限制
                storeIds = project.getStoreIds();
            }
        }

        Page<YxStoreProduct> pageModel = new Page<>(productQueryParam.getPage(),
                productQueryParam.getLimit());

        if(StrUtil.isNotBlank(storeIds)) {
            List<Integer> storeList = new ArrayList<>();
            for(String storeid: Arrays.asList(storeIds.split(","))) {
                storeList.add(Integer.valueOf(storeid));
            }
            productQueryParam.setStoreList(storeList );
        }

      //  IPage<YxStoreProduct> pageList = yxStoreProductMapper.selectPage(pageModel,wrapper);
        List<YxStoreProductQueryVo> list = new ArrayList<>();
        if( StrUtil.isBlank(productQueryParam.getProjectCode()) || CollUtil.isNotEmpty(productQueryParam.getStoreList())) {
            IPage<YxStoreProductQueryVo> yxStoreProductQueryVoIPage = yxStoreProductMapper.getYxStoreProductPageList4Store(pageModel,productQueryParam);
            list = yxStoreProductQueryVoIPage.getRecords();
        } else {
            IPage<YxStoreProductQueryVo> yxStoreProductQueryVoIPage = yxStoreProductMapper.getYxStoreProductPageList4Project(pageModel,productQueryParam);
            list = yxStoreProductQueryVoIPage.getRecords();
        }

        return list;
    }

}
