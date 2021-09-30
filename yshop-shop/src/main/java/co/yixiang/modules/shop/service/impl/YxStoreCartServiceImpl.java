/**
 * Copyright (C) 2018-2020
 * All rights reserved, Designed By www.yixiang.co

 */
package co.yixiang.modules.shop.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import co.yixiang.common.service.impl.BaseServiceImpl;
import co.yixiang.common.utils.QueryHelpPlus;
import co.yixiang.dozer.service.IGenerator;
import co.yixiang.exception.BadRequestException;
import co.yixiang.exception.ErrorRequestException;
import co.yixiang.modules.activity.service.YxStoreCombinationService;
import co.yixiang.modules.shop.domain.YxStoreCart;
import co.yixiang.modules.shop.domain.YxStoreProduct;
import co.yixiang.modules.shop.domain.YxStoreProductAttrValue;
import co.yixiang.modules.shop.service.YxStoreCartService;
import co.yixiang.modules.shop.service.YxStoreProductAttrValueService;
import co.yixiang.modules.shop.service.YxStoreProductService;
import co.yixiang.modules.shop.service.dto.*;
import co.yixiang.modules.shop.service.mapper.StoreCartMapper;
import co.yixiang.utils.FileUtil;
import co.yixiang.utils.OrderUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.PageInfo;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

// 默认不使用缓存
//import org.springframework.cache.annotation.CacheConfig;
//import org.springframework.cache.annotation.CacheEvict;
//import org.springframework.cache.annotation.Cacheable;

/**
* @author hupeng
* @date 2020-05-12
*/
@Service
@AllArgsConstructor
//@CacheConfig(cacheNames = "yxStoreCart")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class YxStoreCartServiceImpl extends BaseServiceImpl<StoreCartMapper, YxStoreCart> implements YxStoreCartService {

    private final IGenerator generator;

    private final StoreCartMapper storeCartMapper;

    @Autowired
    private YxStoreProductService productService;

    private YxStoreProductAttrValueService yxStoreProductAttrValueService;




    @Override
    //@Cacheable
    public Map<String, Object> queryAll(YxStoreCartQueryCriteria criteria, Pageable pageable) {
        getPage(pageable);
        PageInfo<YxStoreCart> page = new PageInfo<>(queryAll(criteria));
        Map<String, Object> map = new LinkedHashMap<>(2);
        map.put("content", generator.convert(page.getList(), YxStoreCartDto.class));
        map.put("totalElements", page.getTotal());
        return map;
    }


    @Override
    //@Cacheable
    public List<YxStoreCart> queryAll(YxStoreCartQueryCriteria criteria){
        return baseMapper.selectList(QueryHelpPlus.getPredicate(YxStoreCart.class, criteria));
    }


    @Override
    public void download(List<YxStoreCartDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (YxStoreCartDto yxStoreCart : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("用户ID", yxStoreCart.getUid());
            map.put("类型", yxStoreCart.getType());
            map.put("商品ID", yxStoreCart.getProductId());
            map.put("商品属性", yxStoreCart.getProductAttrUnique());
            map.put("商品数量", yxStoreCart.getCartNum());
            map.put("添加时间", yxStoreCart.getAddTime());
            map.put("0 = 未购买 1 = 已购买", yxStoreCart.getIsPay());
            map.put("是否删除", yxStoreCart.getIsDel());
            map.put("是否为立即购买", yxStoreCart.getIsNew());
            map.put("拼团id", yxStoreCart.getCombinationId());
            map.put("秒杀产品ID", yxStoreCart.getSeckillId());
            map.put("砍价id", yxStoreCart.getBargainId());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

    @Override
    public List<CountDto> findCateName() {
        return storeCartMapper.findCateName();
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
                       String type, int isNew, int combinationId, int seckillId, int bargainId,String departmentCode,String partnerCode,String refereeCode,String projectNo) {

        YxStoreProduct productQueryVo = productService
                .getById(productId);
        if(ObjectUtil.isNull(productQueryVo)){
            throw new BadRequestException("该产品已下架或删除");
        }

        int stock = productService.getProductStock(productId,productAttrUnique);
        if(stock < cartNum){
            throw new BadRequestException("该产品库存不足"+cartNum);
        }

        YxStoreProductAttrValue yxStoreProductAttrValue = yxStoreProductAttrValueService.getOne(new QueryWrapper<YxStoreProductAttrValue>().eq("`unique`",productAttrUnique).eq("is_del",0));
        if(yxStoreProductAttrValue == null) {
            throw new BadRequestException("该产品属性不存在");
        }
        Integer storeId = yxStoreProductAttrValue.getStoreId();

        QueryWrapper<YxStoreCart> wrapper = new QueryWrapper<>();
        wrapper.eq("uid",uid).eq("type",type).eq("is_pay",0).eq("is_del",0)
                .eq("product_id",productId)
                .eq("product_attr_unique",productAttrUnique)
                .eq("partner_code",partnerCode)
                .orderByDesc("id").last("limit 1");

        YxStoreCart cart =baseMapper.selectOne(wrapper);
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
        storeCart.setProjectCode(projectNo);
        storeCart.setPartnerCode(partnerCode);
        storeCart.setRefereeCode(refereeCode);

        if(ObjectUtil.isNotNull(cart)){
            if(isNew == 0){
                storeCart.setCartNum(cartNum + cart.getCartNum());
            }
            storeCart.setId(cart.getId());
            baseMapper.updateById(storeCart);
        }else{
            //判断是否已经添加过
            storeCart.setAddTime(OrderUtil.getSecondTimestampTwo());
            baseMapper.insert(storeCart);
        }

        return storeCart.getId().intValue();
    }

    @Override
    public YxStoreCart addTbCart(int uid, int productId, int cartNum, String productAttrUnique, String type, int isNew, int combinationId, int seckillId, int bargainId, String departmentCode, String partnerCode, String refereeCode, String projectNo) {

        YxStoreProduct productQueryVo = productService
                .getById(productId);
        if(ObjectUtil.isNull(productQueryVo)){
            throw new BadRequestException("该产品已下架或删除");
        }

        int stock = productService.getProductStock(productId,productAttrUnique);
        if(stock < cartNum){
            throw new BadRequestException("该产品库存不足"+cartNum);
        }

        YxStoreProductAttrValue yxStoreProductAttrValue = yxStoreProductAttrValueService.getOne(new QueryWrapper<YxStoreProductAttrValue>().eq("`unique`",productAttrUnique).eq("is_del",0));
        if(yxStoreProductAttrValue == null) {
            throw new BadRequestException("该产品属性不存在");
        }
        Integer storeId = yxStoreProductAttrValue.getStoreId();

        QueryWrapper<YxStoreCart> wrapper = new QueryWrapper<>();
        wrapper.eq("uid",uid).eq("type",type).eq("is_pay",0).eq("is_del",0)
                .eq("product_id",productId)
                .eq("product_attr_unique",productAttrUnique)
                .eq("partner_code",partnerCode)
                .orderByDesc("id").last("limit 1");

        YxStoreCart storeCart =baseMapper.selectOne(wrapper);
        if(storeCart==null){
            storeCart = new YxStoreCart();
        }
        storeCart.setBargainId(bargainId);
        storeCart.setCombinationId(combinationId);
        storeCart.setProductAttrUnique(productAttrUnique);
        storeCart.setProductId(productId);
        storeCart.setSeckillId(seckillId);
        storeCart.setType(type);
        storeCart.setUid(uid);
        storeCart.setIsNew(isNew);

        storeCart.setStoreId(storeId);
        storeCart.setDepartCode(departmentCode);
        storeCart.setProjectCode(projectNo);
        storeCart.setPartnerCode(partnerCode);
        storeCart.setRefereeCode(refereeCode);

        if(storeCart.getId()!=null){
            if(isNew == 0){
                storeCart.setCartNum(cartNum + storeCart.getCartNum());
            }
            baseMapper.updateById(storeCart);
        }else{
            storeCart.setCartNum(cartNum);
            //判断是否已经添加过
            storeCart.setAddTime(OrderUtil.getSecondTimestampTwo());
            baseMapper.insert(storeCart);
        }

        return storeCart;
    }


    /**
     * 购物车列表-多门店版
     * @param uid 用户id
     * @param cartIds 购物车id，多个逗号隔开
     * @param status 0-购购物车列表
     * @return
     */
    @Override
    public  Map<String, Object> getUserProductCartList4Store(int uid, String cartIds, int status,String projectCode) {



        QueryWrapper<YxStoreCart> wrapper = new QueryWrapper<>();
        wrapper.eq("uid", uid).eq("type", "product").eq("is_pay", 0)
                .eq("is_del", 0).orderByDesc("add_time");
        if (status == 0) wrapper.eq("is_new", 0);
        if (StrUtil.isNotEmpty(cartIds)) {
            wrapper.in("id", Arrays.asList(cartIds.split(",")));
        } else {
            wrapper.eq("project_code", projectCode);
        }
        List<YxStoreCart> carts = baseMapper.selectList(wrapper);

        List<YxStoreCartQueryVo> valid = new ArrayList<>();
        List<YxStoreCartQueryVo> invalid = new ArrayList<>();

        for (YxStoreCart storeCart : carts) {
            YxStoreProductQueryVo storeProduct = null;

            //必须得重新克隆创建一个新对象
            YxStoreProduct storeProduct_tmp = ObjectUtil.clone(productService
                    .getById(storeCart.getProductId()));

            storeProduct = generator.convert(storeProduct_tmp, YxStoreProductQueryVo.class);
            YxStoreCartQueryVo storeCartQueryVo = generator.convert(storeCart, YxStoreCartQueryVo.class);


            if (StrUtil.isNotEmpty(storeCart.getProductAttrUnique())) {
                YxStoreProductAttrValue productAttrValue = yxStoreProductAttrValueService.getOne(new QueryWrapper<YxStoreProductAttrValue>().eq("`unique`", storeCart.getProductAttrUnique()));
                if (ObjectUtil.isNull(productAttrValue) || productAttrValue.getStock() == 0) {
                    storeCartQueryVo.setProductInfo(storeProduct);
                    invalid.add(storeCartQueryVo);
                } else {
                    storeProduct.setAttrInfo(productAttrValue);
                    storeCartQueryVo.setProductInfo(storeProduct);
                    storeCartQueryVo.setPartnerId(storeCart.getPartnerId());


                    storeCartQueryVo.setVipTruePrice(productAttrValue.getPrice().doubleValue());

                    //设置商品价格（原价）
                    storeCartQueryVo.setTruePrice(productAttrValue.getPrice()
                            .doubleValue());
                    storeCartQueryVo.setCostPrice(productAttrValue.getCost()
                            .doubleValue());
                    storeCartQueryVo.setTrueStock(productAttrValue.getStock());


                    storeCartQueryVo.setYiyaobaoSku(storeProduct.getYiyaobaoSku());
                    storeCartQueryVo.setProductAttrUnique(productAttrValue.getUnique());
                    valid.add(storeCartQueryVo);

                }
            }


        }

        Map<String, Object> map = new LinkedHashMap<>();
        map.put("valid", valid);
        map.put("invalid", invalid);
        return map;
    }
}
