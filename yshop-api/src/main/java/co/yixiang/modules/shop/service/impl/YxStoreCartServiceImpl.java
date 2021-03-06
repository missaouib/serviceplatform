/**
 * Copyright (C) 2018-2019
 * All rights reserved, Designed By www.yixiang.co
 * 注意：
 * 本软件为www.yixiang.co开发研制，未经购买不得使用
 * 购买后可获得全部源代码（禁止转卖、分享、上传到码云、github等开源平台）
 * 一经发现盗用、分享等行为，将追究法律责任，后果自负
 */
package co.yixiang.modules.shop.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import co.yixiang.common.api.ApiResult;
import co.yixiang.common.service.impl.BaseServiceImpl;
import co.yixiang.constant.ShopConstants;
import co.yixiang.enums.ProjectNameEnum;
import co.yixiang.exception.ErrorRequestException;
import co.yixiang.modules.activity.entity.YxStoreBargain;
import co.yixiang.modules.activity.entity.YxStoreCombination;
import co.yixiang.modules.activity.entity.YxStoreSeckill;
import co.yixiang.modules.activity.mapper.YxStoreBargainMapper;
import co.yixiang.modules.activity.mapper.YxStoreCombinationMapper;
import co.yixiang.modules.activity.mapper.YxStoreSeckillMapper;
import co.yixiang.modules.activity.service.YxStoreBargainService;
import co.yixiang.modules.activity.service.YxStoreCombinationService;
import co.yixiang.modules.activity.service.YxStoreSeckillService;
import co.yixiang.modules.manage.entity.YxStoreCartProject;
import co.yixiang.modules.order.entity.YxStoreOrder;
import co.yixiang.modules.order.service.YxStoreOrderService;
import co.yixiang.modules.shop.entity.Product4project;
import co.yixiang.modules.shop.entity.YxStoreCart;
import co.yixiang.modules.shop.entity.YxStoreProductAttrValue;
import co.yixiang.modules.shop.entity.YxSystemStore;
import co.yixiang.modules.shop.mapper.YxStoreCartMapper;
import co.yixiang.modules.shop.mapping.CartMap;
import co.yixiang.modules.shop.service.*;
import co.yixiang.modules.shop.web.vo.YxStoreCartQueryVo;
import co.yixiang.modules.shop.web.vo.YxStoreProductQueryVo;
import co.yixiang.modules.user.entity.YxUser;
import co.yixiang.modules.user.service.YxUserService;
import co.yixiang.modules.user.web.vo.YxUserQueryVo;
import co.yixiang.modules.yiyaobao.entity.ProductStoreMapping;
import co.yixiang.modules.yiyaobao.service.ProductStoreMappingService;
import co.yixiang.modules.yiyaobao.web.vo.StoreCartVo;
import co.yixiang.utils.OrderUtil;
import co.yixiang.utils.SecurityUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.*;

import static co.yixiang.constant.ShopConstants.INNER_DISCOUNT_RATE;


/**
 * <p>
 * 购物车表 服务实现类
 * </p>
 *
 * @author hupeng
 * @since 2019-10-25
 */
@Slf4j
@Service
@Builder
@Transactional(rollbackFor = Exception.class)
public class YxStoreCartServiceImpl extends BaseServiceImpl<YxStoreCartMapper, YxStoreCart> implements YxStoreCartService {

    @Autowired
    private YxStoreCartMapper yxStoreCartMapper;
    @Autowired
    private YxStoreSeckillMapper storeSeckillMapper;
    @Autowired
    private YxStoreBargainMapper yxStoreBargainMapper;
    @Autowired
    private YxStoreCombinationMapper storeCombinationMapper;

    @Autowired
    private YxStoreProductService productService;
    @Autowired
    private YxStoreProductAttrService productAttrService;
    @Autowired
    @Lazy
    private YxStoreCombinationService storeCombinationService;
    @Autowired
    private YxStoreSeckillService storeSeckillService;
    @Autowired
    private YxStoreBargainService storeBargainService;
    @Autowired
    @Lazy
    private YxStoreOrderService storeOrderService;
    @Autowired
    @Lazy
    private YxUserService userService;
    @Autowired
    private YxSystemStoreService storeService;
    @Autowired
    private CartMap cartMap;

    @Autowired
    private YxStoreProductAttrValueService yxStoreProductAttrValueService;

    @Autowired
    private ProductStoreMappingService productStoreMappingService;
    @Autowired
    private Product4projectService product4projectService;

    /**
     * 删除购物车
     * @param uid
     * @param ids
     */
    @Override
    public void removeUserCart(int uid, List<String> ids) {
        QueryWrapper<YxStoreCart> wrapper = new QueryWrapper<>();
        wrapper.eq("uid",uid).in("id",ids);

        YxStoreCart storeCart = new YxStoreCart();
        storeCart.setIsDel(1);

        yxStoreCartMapper.update(storeCart,wrapper);
    }

    /**
     * 改购物车数量
     * @param cartId
     * @param cartNum
     * @param uid
     */
    @Override
    public void changeUserCartNum(int cartId, int cartNum, int uid) {
        QueryWrapper<YxStoreCart> wrapper = new QueryWrapper<>();
        wrapper.eq("uid",uid).eq("id",cartId);

        YxStoreCart cart = getOne(wrapper);
        if(ObjectUtil.isNull(cart)){
            throw new ErrorRequestException("购物车不存在");
        }

        if(cartNum <= 0){
            throw new ErrorRequestException("库存错误");
        }

        //todo 普通商品库存
        /*int stock = productService.getProductStock(cart.getProductId()
                ,cart.getProductAttrUnique());
        if(stock < cartNum){
            throw new ErrorRequestException("该产品库存不足"+cartNum);
        }*/

        if(cartNum == cart.getCartNum()) return;

        YxStoreCart storeCart = new YxStoreCart();
        storeCart.setCartNum(cartNum);
        storeCart.setId(Long.valueOf(cartId));

        yxStoreCartMapper.updateById(storeCart);


    }

    /**
     * 改购物车数量-多门店版
     * @param cartId
     * @param cartNum
     * @param uid
     */
    @Override
    public void changeUserCartNum4Store(int cartId, int cartNum, int uid) {
        QueryWrapper<YxStoreCart> wrapper = new QueryWrapper<>();
        wrapper.eq("uid",uid).eq("id",cartId);

        YxStoreCart cart = getOne(wrapper);
        if(ObjectUtil.isNull(cart)){
            throw new ErrorRequestException("购物车不存在");
        }

        if(cartNum <= 0){
            throw new ErrorRequestException("库存错误");
        }

        //todo 普通商品库存
       /* int stock = productService.getProductStock(cart.getProductId()
                ,cart.getProductAttrUnique());
        if(stock < cartNum){
            throw new ErrorRequestException("该产品库存不足"+cartNum);
        }*/

        if(cartNum == cart.getCartNum()) return;

        YxStoreCart storeCart = new YxStoreCart();
        storeCart.setCartNum(cartNum);
        storeCart.setId(Long.valueOf(cartId));

        yxStoreCartMapper.updateById(storeCart);


    }

    /**
     * 购物车列表
     * @param uid 用户id
     * @param cartIds 购物车id，多个逗号隔开
     * @param status 0-购购物车列表
     * @return
     */
    @Override
    public Map<String, Object> getUserProductCartList(int uid, String cartIds, int status,String projectCode) {
        QueryWrapper<YxStoreCart> wrapper = new QueryWrapper<>();
        wrapper.eq("uid",uid).eq("type","product").eq("is_pay",0)
                .eq("is_del",0)
                .orderByDesc("add_time");
        if(status == 0) wrapper.eq("is_new",0);
        if(StrUtil.isNotEmpty(cartIds)) {
            wrapper.in("id", Arrays.asList(cartIds.split(",")));
        } else {
            wrapper.eq("project_code",projectCode);
        }
        List<YxStoreCart> carts = yxStoreCartMapper.selectList(wrapper);

        List<YxStoreCartQueryVo> valid = new ArrayList<>();
        List<YxStoreCartQueryVo> invalid = new ArrayList<>();

        for (YxStoreCart storeCart : carts) {
            YxStoreProductQueryVo storeProduct = null;
            if(storeCart.getCombinationId() > 0){
                storeProduct = ObjectUtil.clone(storeCombinationMapper.combinatiionInfo(storeCart.getCombinationId()));
            }else if(storeCart.getSeckillId() > 0){
                storeProduct = ObjectUtil.clone(storeSeckillMapper.seckillInfo(storeCart.getSeckillId()));
            }else if(storeCart.getBargainId() > 0){
                storeProduct = ObjectUtil.clone(yxStoreBargainMapper.bargainInfo(storeCart.getBargainId()));
            }else{
                //必须得重新克隆创建一个新对象
                storeProduct = ObjectUtil.clone(productService
                        .getNewStoreProductById(storeCart.getProductId()));
            }

            YxStoreCartQueryVo storeCartQueryVo = cartMap.toDto(storeCart);

            if(ObjectUtil.isNull(storeProduct)){
                YxStoreCart yxStoreCart = new YxStoreCart();
                yxStoreCart.setIsDel(1);
                yxStoreCartMapper.update(yxStoreCart,
                        new QueryWrapper<YxStoreCart>()
                                .lambda().eq(YxStoreCart::getId,storeCart.getId()));
            }else if( storeProduct.getIsDel() == 1 || storeProduct.getStock() == 0){
                storeCartQueryVo.setProductInfo(storeProduct);
                invalid.add(storeCartQueryVo);
            }else{
                if(StrUtil.isNotEmpty(storeCart.getProductAttrUnique())){
                    YxStoreProductAttrValue productAttrValue = productAttrService
                            .uniqueByAttrInfo(storeCart.getProductAttrUnique());
                    if(ObjectUtil.isNull(productAttrValue) || productAttrValue.getStock() == 0){
                        storeCartQueryVo.setProductInfo(storeProduct);
                        invalid.add(storeCartQueryVo);
                    }else{
                        storeProduct.setAttrInfo(productAttrValue);
                        storeCartQueryVo.setProductInfo(storeProduct);

                        //设置真实价格
                        //设置VIP价格
                        double vipPrice = 0d;
                        if(storeCart.getCombinationId() > 0 || storeCart.getSeckillId() > 0
                                || storeCart.getBargainId() > 0){
                            vipPrice = productAttrValue.getPrice().doubleValue();
                        }else{
                            vipPrice = userService.setLevelPrice(
                                    productAttrValue.getPrice().doubleValue(),uid);
                        }
                        storeCartQueryVo.setTruePrice(vipPrice);
                        //设置会员价
                        storeCartQueryVo.setVipTruePrice(productAttrValue.getPrice()
                                .doubleValue());
                        storeCartQueryVo.setCostPrice(productAttrValue.getCost()
                                .doubleValue());
                        storeCartQueryVo.setTrueStock(productAttrValue.getStock());
                        storeCartQueryVo.setYiyaobaoSku(storeProduct.getYiyaobaoSku());
                        valid.add(storeCartQueryVo);

                    }
                }else{
                    //设置VIP价格
                    //设置VIP价格
                    double vipPrice = 0d;
                    if(storeCart.getCombinationId() > 0 || storeCart.getSeckillId() > 0
                            || storeCart.getBargainId() > 0){
                        vipPrice = storeProduct.getPrice().doubleValue();
                    }else{
                        vipPrice = userService.setLevelPrice(
                                storeProduct.getPrice().doubleValue(),uid);
                    }

                    storeCartQueryVo.setTruePrice(vipPrice);
                    //todo 设置会员价
                    storeCartQueryVo.setVipTruePrice(0d);
                    storeCartQueryVo.setCostPrice(storeProduct.getCost()
                            .doubleValue());
                    storeCartQueryVo.setTrueStock(storeProduct.getStock());
                    storeCartQueryVo.setProductInfo(storeProduct);
                    storeCartQueryVo.setYiyaobaoSku(storeProduct.getYiyaobaoSku());
                    valid.add(storeCartQueryVo);
                }
            }

        }

        Map<String,Object> map = new LinkedHashMap<>();
        map.put("valid",valid);
        map.put("invalid",invalid);
        return map;
    }

    /**
     * 购物车列表-多门店版
     * @param uid 用户id
     * @param cartIds 购物车id，多个逗号隔开
     * @param status 0-购购物车列表
     * @return
     */
    @Override
    public List<StoreCartVo> getUserProductCartList4Store(int uid, String cartIds, int status,String projectCode,String cardNmuber,String cardType,Integer demandId) {
        List<StoreCartVo> result = new ArrayList<>();
        List list = null;
        if(StrUtil.isNotEmpty(cartIds)) {
             list = Arrays.asList(cartIds.split(","));
        } else {
             list = new ArrayList();
        }
        String projectCode_temp = "";
        if(demandId != null) {
            projectCode_temp = String.valueOf(demandId);
        }else {
            projectCode_temp = projectCode;
        }

        if(StrUtil.isNotEmpty(cartIds)) {
            projectCode_temp = "";
        }

        List<String> projectCodes = new ArrayList<>();
        projectCodes.add(projectCode_temp);
        if("".equals(projectCode_temp)) {
            projectCodes.add(ProjectNameEnum.HEALTHCARE.getValue());
            projectCodes.add(ProjectNameEnum.ROCHE_SMA.getValue());
        }

        List<YxSystemStore> storeIdList = yxStoreCartMapper.getStoreInfo(uid,"product",status,list,projectCodes);
        for(YxSystemStore store:storeIdList) {
            QueryWrapper<YxStoreCart> wrapper = new QueryWrapper<>();
            wrapper.eq("uid",uid).eq("type","product").eq("is_pay",0)
                    .eq("is_del",0).eq("store_id",store.getId()).orderByDesc("add_time");
            if(status == 0) wrapper.eq("is_new",0);
            if(StrUtil.isNotEmpty(cartIds)) {
                wrapper.in("id", Arrays.asList(cartIds.split(",")));
            } else{
                //if(StringUtils.isNotEmpty(projectCode_temp)){
                   // wrapper.eq("project_code",projectCode_temp);
               // }
                wrapper.in("project_code",projectCodes);
            }
            List<YxStoreCart> carts = yxStoreCartMapper.selectList(wrapper);

            List<YxStoreCartQueryVo> valid = new ArrayList<>();
            List<YxStoreCartQueryVo> invalid = new ArrayList<>();

            for (YxStoreCart storeCart : carts) {
                String projectTmep2 = projectCode;
                YxStoreProductQueryVo storeProduct = null;
                if(storeCart.getCombinationId() > 0){
                    storeProduct = ObjectUtil.clone(storeCombinationMapper.combinatiionInfo(storeCart.getCombinationId()));
                }else if(storeCart.getSeckillId() > 0){
                    storeProduct = ObjectUtil.clone(storeSeckillMapper.seckillInfo(storeCart.getSeckillId()));
                }else if(storeCart.getBargainId() > 0){
                    storeProduct = ObjectUtil.clone(yxStoreBargainMapper.bargainInfo(storeCart.getBargainId()));
                }else{
                    //必须得重新克隆创建一个新对象
                    storeProduct = ObjectUtil.clone(productService
                            .getNewStoreProductById(storeCart.getProductId()));
                }

                YxStoreCartQueryVo storeCartQueryVo = cartMap.toDto(storeCart);

                if(ObjectUtil.isNull(storeProduct)){
                    YxStoreCart yxStoreCart = new YxStoreCart();
                    yxStoreCart.setIsDel(1);
                    yxStoreCartMapper.update(yxStoreCart,
                            new QueryWrapper<YxStoreCart>()
                                    .lambda().eq(YxStoreCart::getId,storeCart.getId()));
                }else if( storeProduct.getIsDel() == 1){
                    storeCartQueryVo.setProductInfo(storeProduct);
                    invalid.add(storeCartQueryVo);
                }else{
                    if(StrUtil.isNotEmpty(storeCart.getProductAttrUnique())){
                        YxStoreProductAttrValue productAttrValue = productAttrService
                                .uniqueByAttrInfo(storeCart.getProductAttrUnique());
                        if(ObjectUtil.isNull(productAttrValue) || productAttrValue.getStock() == 0){
                            storeCartQueryVo.setProductInfo(storeProduct);
                            invalid.add(storeCartQueryVo);
                        }else{


                            storeProduct.setPrice(productAttrValue.getPrice());

                            // 门店是广州店，项目编码没有的情况，指定项目名称是健康养生
                            if(StrUtil.isBlank(projectCode) && ShopConstants.STORENAME_GUANGZHOU_CLOUD.equals( productAttrValue.getSuk() )) {
                                projectTmep2 = ProjectNameEnum.HEALTHCARE.getValue();
                            }
                            if(StrUtil.isNotBlank(projectTmep2)) {
                                LambdaQueryWrapper<Product4project> lambdaQueryWrapper = new LambdaQueryWrapper();
                                lambdaQueryWrapper.in(Product4project::getProjectNo,projectTmep2);
                                lambdaQueryWrapper.eq(Product4project::getProductUniqueId,productAttrValue.getUnique());
                                lambdaQueryWrapper.eq(Product4project::getIsDel,0);
                                lambdaQueryWrapper.eq(Product4project::getIsShow,1);
                               // lambdaQueryWrapper.isNotNull(Product4project::getUnitPrice);
                                Product4project product4project = product4projectService.getOne(lambdaQueryWrapper,false);
                                if(ObjectUtil.isNull(product4project)) {
                                    /*storeCartQueryVo.setProductInfo(storeProduct);
                                    invalid.add(storeCartQueryVo);
                                    continue;*/
                                }else if(product4project != null && product4project.getUnitPrice() != null) {
                                    storeProduct.setPrice(product4project.getUnitPrice());
                                }
                            }

                            storeProduct.setStoreNameReal(productAttrValue.getSuk());
                            storeProduct.setAttrInfo(productAttrValue);
                            storeCartQueryVo.setProductInfo(storeProduct);
                            storeCartQueryVo.setPartnerId(storeCart.getPartnerId());
                            //设置真实价格
                            //设置VIP价格
                            double vipPrice = 0d;
                            if(storeCart.getCombinationId() > 0 || storeCart.getSeckillId() > 0
                                    || storeCart.getBargainId() > 0){
                                vipPrice = productAttrValue.getPrice().doubleValue();
                            }else{
                                /*vipPrice = userService.setLevelPrice(
                                        productAttrValue.getPrice().doubleValue(),uid);*/
                                vipPrice = userService.getVipPriceByProjectNo(projectCode,cardNmuber,cardType,uid,storeProduct).getVipPrice().doubleValue();
                            }
                            storeCartQueryVo.setDiscount(storeProduct.getDiscount());
                            storeCartQueryVo.setLabel1(storeProduct.getLabel1());
                            storeCartQueryVo.setLabel2(storeProduct.getLabel2());
                            storeCartQueryVo.setLabel3(storeProduct.getLabel3());
                            // 设置商品价格（会员价）
                            storeCartQueryVo.setVipTruePrice(vipPrice);

                            //设置商品价格（原价）
                            storeCartQueryVo.setTruePrice(storeProduct.getPrice()
                                    .doubleValue());
                            storeCartQueryVo.setCostPrice(storeProduct.getCost()
                                    .doubleValue());
                            storeCartQueryVo.setTrueStock(productAttrValue.getStock());


                            storeCartQueryVo.setYiyaobaoSku(storeProduct.getYiyaobaoSku());
                            storeCartQueryVo.setProductAttrUnique(productAttrValue.getUnique());
                            valid.add(storeCartQueryVo);

                        }
                    }else{
                        //设置VIP价格
                        //设置VIP价格
                        double vipPrice = 0d;
                        if(storeCart.getCombinationId() > 0 || storeCart.getSeckillId() > 0
                                || storeCart.getBargainId() > 0){
                            vipPrice = storeProduct.getPrice().doubleValue();
                        }else{
                            vipPrice = userService.setLevelPrice(
                                    storeProduct.getPrice().doubleValue(),uid);
                        }

                        storeCartQueryVo.setTruePrice(0d);
                        //todo 设置会员价
                        storeCartQueryVo.setVipTruePrice(vipPrice);
                        storeCartQueryVo.setCostPrice(storeProduct.getCost()
                                .doubleValue());
                        storeCartQueryVo.setTrueStock(storeProduct.getStock());
                        storeCartQueryVo.setProductInfo(storeProduct);
                        storeCartQueryVo.setPartnerId(storeCart.getPartnerId());
                        valid.add(storeCartQueryVo);
                    }
                }

            }

            Map<String,Object> map = new LinkedHashMap<>();
            map.put("valid",valid);
            map.put("invalid",invalid);
            StoreCartVo storeCartVo = new StoreCartVo();
            storeCartVo.setInfo(map);
            storeCartVo.setStoreId(store.getId());
            storeCartVo.setStoreName(store.getName());
            result.add( storeCartVo);
        }


        return result;
    }



    /**
     * 添加购物车
     * @param uid  用户id
     * @param productId 普通产品编号
     * @param cartNum  购物车数量
     * @param productAttrUnique 属性唯一值
     * @param type product
     * @param isNew 1 加入购物车直接购买  0 加入购物车
     * @param combinationId 拼团id
     * @param seckillId  秒杀id
     * @param bargainId  砍价id
     * @return
     */
    @Override
    public int addCart(int uid, int productId, int cartNum, String productAttrUnique,
                       String type, int isNew, int combinationId, int seckillId, int bargainId,String departmentCode,String partnerCode,String refereeCode,String projectCode) {
        //todo 拼团
        if(combinationId > 0){
            boolean isStock = storeCombinationService.judgeCombinationStock(combinationId
                    ,cartNum);
            if(!isStock) throw new ErrorRequestException("该产品库存不足");

            YxStoreCombination storeCombination = storeCombinationService.getCombination(combinationId);
            if(ObjectUtil.isNull(storeCombination)) throw new ErrorRequestException("该产品已下架或删除");
        }else if(seckillId > 0){//秒杀
            YxStoreSeckill yxStoreSeckill = storeSeckillService.getSeckill(seckillId);
            if(ObjectUtil.isNull(yxStoreSeckill)){
                throw new ErrorRequestException("该产品已下架或删除");
            }
            if(yxStoreSeckill.getStock() < cartNum){
                throw new ErrorRequestException("该产品库存不足");
            }
            int  seckillOrderCount = storeOrderService.count(new QueryWrapper<YxStoreOrder>()
                            .eq("uid", uid).eq("paid",1).eq("seckill_id",seckillId));
            if(yxStoreSeckill.getNum() <= seckillOrderCount || yxStoreSeckill.getNum() < cartNum){
                throw new ErrorRequestException("每人限购:"+yxStoreSeckill.getNum()+"件");
            }

        }else if(bargainId > 0){//砍价
            YxStoreBargain yxStoreBargain = storeBargainService.getBargain(bargainId);
            if(ObjectUtil.isNull(yxStoreBargain)){
                throw new ErrorRequestException("该产品已下架或删除");
            }
            if(yxStoreBargain.getStock() < cartNum){
                throw new ErrorRequestException("该产品库存不足");
            }

        }else{
            YxStoreProductQueryVo productQueryVo = productService
                    .getYxStoreProductById(productId);
            if(ObjectUtil.isNull(productQueryVo)){
                throw new ErrorRequestException("该产品已下架或删除");
            }

            int stock = productService.getProductStock(productId,productAttrUnique);
            if(stock < cartNum){
                throw new ErrorRequestException("该产品库存不足"+cartNum);
            }
        }

        YxStoreProductAttrValue yxStoreProductAttrValue = yxStoreProductAttrValueService.getOne(new QueryWrapper<YxStoreProductAttrValue>().eq("`unique`",productAttrUnique).eq("is_del",0));
        if(yxStoreProductAttrValue == null) {
            throw new ErrorRequestException("该产品属性不存在");
        }
        Integer storeId = yxStoreProductAttrValue.getStoreId();

        QueryWrapper<YxStoreCart> wrapper = new QueryWrapper<>();
        wrapper.eq("uid",uid).eq("type",type).eq("is_pay",0).eq("is_del",0)
                .eq("product_id",productId)
                .eq("is_new",isNew).eq("product_attr_unique",productAttrUnique)
                .eq("combination_id",combinationId).eq("bargain_id",bargainId)
                .eq("seckill_id",seckillId)
                .eq("project_code",projectCode)
                .orderByDesc("id").last("limit 1");

        YxStoreCart cart =yxStoreCartMapper.selectOne(wrapper);
        YxStoreCart storeCart = new YxStoreCart();

        storeCart.setBargainId(bargainId);
        storeCart.setCartNum(cartNum);
        storeCart.setCombinationId(combinationId);
        storeCart.setProductAttrUnique(productAttrUnique);
        storeCart.setProductId(productId);
        storeCart.setSeckillId(seckillId);
        storeCart.setType(type);
        storeCart.setUid(uid);
        storeCart.setIsNew(isNew);

        storeCart.setStoreId(storeId);
        storeCart.setDepartCode(departmentCode);
        storeCart.setProjectCode(projectCode);
        storeCart.setPartnerCode(partnerCode);
        storeCart.setRefereeCode(refereeCode);

        if(ObjectUtil.isNotNull(cart)){
            if(isNew == 0){
                storeCart.setCartNum(cartNum + cart.getCartNum());
            }
            storeCart.setId(cart.getId());
            yxStoreCartMapper.updateById(storeCart);
        }else{
            //判断是否已经添加过
            storeCart.setAddTime(OrderUtil.getSecondTimestampTwo());
            yxStoreCartMapper.insert(storeCart);
        }

        return storeCart.getId().intValue();
    }




    @Override
    public int getUserCartNum(int uid, String type, int numType,List<String> projectCodes) {
        int num = 0;
        QueryWrapper<YxStoreCart> wrapper = new QueryWrapper<>();
        wrapper.eq("uid",uid).eq("type",type).eq("is_pay",0).eq("is_del",0).eq("is_new",0).in("project_code",projectCodes);
        if(numType > 0){
            num = yxStoreCartMapper.selectCount(wrapper);
        }else{
            num = yxStoreCartMapper.cartSum(uid,type,projectCodes);
        }
        return num;
    }

    @Override
    public YxStoreCartQueryVo getYxStoreCartById(Serializable id){
        return yxStoreCartMapper.getYxStoreCartById(id);
    }

    @Override
    public List<YxSystemStore> getStoreInfo(int uid, String type, Integer is_new, List<String> cartIds,List<String> projectCodes) {
        return yxStoreCartMapper.getStoreInfo(uid,type,is_new,cartIds,projectCodes);
    }

    @Override
    public void add4Project(String projectNo,int uid) {


        QueryWrapper queryWrapper1 = new QueryWrapper();
        queryWrapper1.eq("uid",uid);
        queryWrapper1.eq("project_code",projectNo);
        yxStoreCartMapper.delete(queryWrapper1);


        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("project_no",projectNo);
        List<Product4project> product4projectList = product4projectService.list(queryWrapper);

        for(Product4project product4project : product4projectList) {
            Integer cartNum = product4project.getNum();
            Integer productId = product4project.getProductId();
            String uniqueId = product4project.getProductUniqueId();
            int isNew = 0;
            Integer storeId = product4project.getStoreId();
            //拼团
            int combinationId = 0;
            //秒杀
            int seckillId = 0;
            // 砍价
            int bargainId = 0;

            String departmentCode = "";
            String partnerCode = "";
            String refereeCode = "";
            String projectCode = projectNo;
            this.addCart(uid,productId,cartNum,uniqueId
                    ,"product",isNew,combinationId,seckillId,bargainId,departmentCode,partnerCode,refereeCode,projectCode);
        }
    }

    @Override
    public Boolean deleteCartByUidProductid(Integer uid, Integer productid, String productUnique) {
        return baseMapper.deleteCartByUidProductid(uid,productid,productUnique);
    }
}
