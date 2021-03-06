package co.yixiang.modules.yiyaobao.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import co.yixiang.modules.shop.domain.YxStoreProduct;
import co.yixiang.modules.shop.domain.YxStoreProductAttr;
import co.yixiang.modules.shop.domain.YxStoreProductAttrValue;
import co.yixiang.modules.shop.domain.YxSystemStore;
import co.yixiang.modules.shop.service.YxStoreProductAttrService;
import co.yixiang.modules.shop.service.YxStoreProductAttrValueService;
import co.yixiang.modules.shop.service.YxStoreProductService;
import co.yixiang.modules.shop.service.YxSystemStoreService;
import co.yixiang.mp.yiyaobao.domain.Medicine;
import co.yixiang.mp.yiyaobao.domain.Seller;
import co.yixiang.mp.yiyaobao.domain.SkuSellerPriceStock;
import co.yixiang.mp.yiyaobao.entity.CmdStockDetailEbs;
import co.yixiang.mp.yiyaobao.mapper.CmdStockDetailEbsMapper;
import co.yixiang.mp.yiyaobao.service.CmdStockDetailEbsService;
import co.yixiang.tools.domain.QiniuContent;
import co.yixiang.tools.service.QiNiuService;
import co.yixiang.utils.OrderUtil;
import co.yixiang.utils.RedisUtils;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Update;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

@Service
@Slf4j
public class StockServiceImpl {
    @Autowired
    private CmdStockDetailEbsService cmdStockDetailEbsService;

    @Autowired
    private YxStoreProductAttrValueService yxStoreProductAttrValueService;

    @Autowired
    private YxStoreProductService yxStoreProductService;

    @Autowired
    private YxSystemStoreService yxSystemStoreService;

    @Autowired
    private YxStoreProductAttrService yxStoreProductAttrService;

    @Autowired
    private CmdStockDetailEbsMapper cmdStockDetailEbsMapper;

    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private QiNiuService qiNiuService;


    @Value("${file.localUrl}")
    private String localUrl;
    @Value("${yiyaobao.yiyaobaoImageUrlPrefix}")
    private String yiyaobaoImageUrlPrefix;

    @DS("multi-datasource1")
    public List<CmdStockDetailEbs> getCmdStockDetailEbs(){
        List<CmdStockDetailEbs> list = cmdStockDetailEbsMapper.getYiyaobaoStock();

        log.info("???????????????????????????{}???",list.size());
        return list;
    }

    @DS("multi-datasource1")
    public Map<String,HashMap> getYiyaobaoMedicineData(){
        // ????????????-???????????????????????? ?????????
        List<SkuSellerPriceStock> skuSellerPriceStockByCityList = cmdStockDetailEbsMapper.getSellerPriceStockByCity();
        // ????????????-???????????????????????? ?????????
        List<SkuSellerPriceStock> skuSellerPriceStockBySellerList = cmdStockDetailEbsMapper.getSellerPriceStockBySeller();
        // ???????????????
     //   HashMap<String, Medicine> skuMap = new HashMap<>();

        HashMap<String,SkuSellerPriceStock> skuSellerPriceStockMap = new HashMap<>();
        // ????????????????????????-??????-??????
        for(SkuSellerPriceStock skuSellerPriceStock:skuSellerPriceStockByCityList){
           /* if( !skuMap.containsKey(skuSellerPriceStock.getSku())) {
                Medicine medicine = cmdStockDetailEbsMapper.getMedicineBySku(skuSellerPriceStock.getSku());
                skuMap.put(skuSellerPriceStock.getSku(),medicine);
            }*/
            skuSellerPriceStockMap.put(skuSellerPriceStock.getSku()+"_"+skuSellerPriceStock.getSellerId(),skuSellerPriceStock);
        }

        //?????? ????????????????????????-??????-?????????
        for(SkuSellerPriceStock skuSellerPriceStock:skuSellerPriceStockBySellerList){
         /*   if( !skuMap.containsKey(skuSellerPriceStock.getSku())) {
                Medicine medicine = cmdStockDetailEbsMapper.getMedicineBySku(skuSellerPriceStock.getSku());
                skuMap.put(skuSellerPriceStock.getSku(),medicine);
            }*/

            skuSellerPriceStockMap.put(skuSellerPriceStock.getSku()+"_"+skuSellerPriceStock.getSellerId(),skuSellerPriceStock);
        }

        // ???????????????????????????????????????
  /*      for(Map.Entry<String, Medicine> entry : skuMap.entrySet()){
            Medicine medicine = entry.getValue();
            List<String> imageList = cmdStockDetailEbsMapper.getMedicineImageBySku(medicine.getYiyaobaoSku());
            if(CollUtil.isNotEmpty(imageList)) {
                medicine.setImage(imageList.get(0));
                medicine.setSliderImage(CollUtil.join(imageList,","));
            }

        }*/

        Map<String,HashMap> map = new HashMap<>();
       // map.put("skuMap",skuMap);
        map.put("skuSellerPriceStockMap",skuSellerPriceStockMap);

        return map;
    }



    @DS("multi-datasource1")
    public List<Seller> getYiyaobaoSeller(String startDate){


        // ?????????????????????
        List<Seller> sellerList = cmdStockDetailEbsMapper.getSellerByDate(startDate);
        // ??????????????????
        for(Seller seller : sellerList) {
            if (StrUtil.isNotBlank(seller.getImage())) {
                String imageId = seller.getImage();
                String imagePath = cmdStockDetailEbsMapper.getSellerImageById(imageId);
                if(StrUtil.isNotBlank(imagePath)) {
                    imagePath = "https://www.yiyaogo.com/yyadmin" +  imagePath;
                    seller.setImage(imagePath);
                }
            }
        }
        return sellerList;
    }
    public void syncStock(){
        syncStock(getCmdStockDetailEbs());
    }

    @DS("master")
    public void syncStock(List<CmdStockDetailEbs> list) {
        // ????????????????????????ebs??????
       // List<CmdStockDetailEbs> list = cmdStockDetailEbsService.list4ds();

        QueryWrapper queryWrapper = new QueryWrapper();
        // ??????????????????
        cmdStockDetailEbsService.remove(queryWrapper);
        log.info("??????ebs??????");
        // ??????????????????
        cmdStockDetailEbsService.saveBatch(list);
        log.info("????????????ebs??????");

        // ?????? ebs????????????????????????sku???????????????????????????sku

        for(CmdStockDetailEbs detailEbs:list) {
            String yiyaobaosku = detailEbs.getSku();
            int count = yxStoreProductService.count(new QueryWrapper<YxStoreProduct>().eq("yiyaobao_sku",yiyaobaosku));

        }

        QueryWrapper queryWrapper1 = new QueryWrapper();
        queryWrapper1.isNotNull("store_id");
        // ?????????????????????????????????
        queryWrapper1.ne("product_id",101717);
        List<YxStoreProductAttrValue> attrValueList = yxStoreProductAttrValueService.list(queryWrapper1);
        for(YxStoreProductAttrValue attrValue:attrValueList) {
            Integer productId = attrValue.getProductId();
            YxStoreProduct yxStoreProduct = yxStoreProductService.getById(productId);
            if(yxStoreProduct == null) {
                log.error("??????id{}?????????",productId);
                continue;
            }
            String yiyaobaoSku = yxStoreProduct.getYiyaobaoSku();

            YxSystemStore yxSystemStore = yxSystemStoreService.getById(attrValue.getStoreId());
            if(yxStoreProduct == null) {
                log.error("??????id{}?????????",attrValue.getStoreId());
                continue;
            }
            String yiyaobaoSellerId = yxSystemStore.getYiyaobaoId();
            QueryWrapper queryWrapper2 = new QueryWrapper();
            queryWrapper2.eq("SKU",yiyaobaoSku);
            queryWrapper2.eq("SELLER_ID",yiyaobaoSellerId);
            CmdStockDetailEbs cmdStockDetailEbs = cmdStockDetailEbsService.getOne(queryWrapper2,false);
            Integer stock = 0;
            if(cmdStockDetailEbs != null) {
                stock = cmdStockDetailEbs.getUsableAmount().intValue();
            } else {
                log.error("yiyaobaoSku={},SELLER_ID={} ????????????",yiyaobaoSku,yiyaobaoSellerId);
            }
            attrValue.setStock(stock);

            yxStoreProductAttrValueService.updateById(attrValue);
        }


/*
        for(CmdStockDetailEbs cmdStockDetailEbs:list) {
            // ??????sku ????????????????????????
            QueryWrapper<YxStoreProduct> queryWrapper1 = new QueryWrapper();
            queryWrapper1.eq("yiyaobao_sku",cmdStockDetailEbs.getSku());
            YxStoreProduct yxStoreProduct =  yxStoreProductService.getOne(queryWrapper1,false);
            if(yxStoreProduct == null) {
                continue;
            }

            // ???????????? ?????????????????????????????????
            QueryWrapper<YxSystemStore> queryWrapper2 = new QueryWrapper();
            queryWrapper2.eq("yiyaobao_id",cmdStockDetailEbs.getSellerId());
            YxSystemStore yxSystemStore = yxSystemStoreService.getOne(queryWrapper2,false);
            if(yxSystemStore == null) {
                continue;
            }

            QueryWrapper queryWrapper3 = new QueryWrapper();
            queryWrapper3.eq("product_id",yxStoreProduct.getId());
            queryWrapper3.eq("attr_name","??????");
            YxStoreProductAttr yxStoreProductAttr = yxStoreProductAttrService.getOne(queryWrapper3,false);
            if(yxStoreProductAttr == null) {
                yxStoreProductAttr = new YxStoreProductAttr();
                yxStoreProductAttr.setProductId(yxStoreProduct.getId());
                yxStoreProductAttr.setAttrName("??????");
                yxStoreProductAttr.setAttrValues(yxSystemStore.getName());
                yxStoreProductAttrService.save(yxStoreProductAttr);
            }

            QueryWrapper queryWrapper4 = new QueryWrapper();
            queryWrapper4.eq("product_id",yxStoreProduct.getId());
            queryWrapper4.eq("store_id",yxSystemStore.getId());
            YxStoreProductAttrValue yxStoreProductAttrValue = yxStoreProductAttrValueService.getOne(queryWrapper4,false);
            if(yxStoreProductAttrValue == null) {
                yxStoreProductAttrValue = new YxStoreProductAttrValue();
                yxStoreProductAttrValue.setProductName(yxStoreProduct.getStoreName());
                yxStoreProductAttrValue.setAttrId(yxStoreProductAttr.getId());
                yxStoreProductAttrValue.setProductId(yxStoreProduct.getId());
                yxStoreProductAttrValue.setStock(cmdStockDetailEbs.getUsableAmount().intValue());
                yxStoreProductAttrValue.setStoreId(yxSystemStore.getId());
                yxStoreProductAttrValue.setSuk(yxSystemStore.getName());
                yxStoreProductAttrValue.setUnique(UUID.randomUUID().toString());
                yxStoreProductAttrValueService.save(yxStoreProductAttrValue);
            } else {
                yxStoreProductAttrValue.setStock(cmdStockDetailEbs.getUsableAmount().intValue());
                yxStoreProductAttrValueService.updateById(yxStoreProductAttrValue);
            }
        }*/

    }

/* ???????????????????????????*/
    public void syncYiyaobaoMedicine(){

        // ?????????????????????????????????
     /*   String startDate = "2000-01-01 00:00:01";
        if(redisUtils.get("SyncYiyaobaoSellDate") != null) {
            startDate = (String) redisUtils.get("SyncYiyaobaoSellDate");
        }*/


        // ??????????????????
/*        List<Seller> sellerList = getYiyaobaoSeller(startDate);
        for(Seller seller:sellerList) {
            YxSystemStore yxSystemStore = yxSystemStoreService.getOne(new QueryWrapper<YxSystemStore>().eq("yiyaobao_id",seller.getYiyaobaoId()),true);
            if(yxSystemStore ==  null) {
                yxSystemStore = new YxSystemStore();
                BeanUtils.copyProperties(seller,yxSystemStore);
                yxSystemStore.setAddTime(OrderUtil.getSecondTimestampTwo());
                yxSystemStoreService.save(yxSystemStore);
            }
        }*/

        // ???????????????????????????
        Map<String,HashMap> map = getYiyaobaoMedicineData();
   /*     HashMap<String,Medicine> skuMap = map.get("skuMap");

        for(Map.Entry<String, Medicine> entry : skuMap.entrySet()) {
            Medicine medicine = entry.getValue();
            // ??????sku ?????????????????????????????????
            YxStoreProduct yxStoreProduct = yxStoreProductService.getOne(new QueryWrapper<YxStoreProduct>().eq("yiyaobao_sku",medicine.getYiyaobaoSku()),true);
            if(yxStoreProduct == null) { // ????????????????????????????????????

            }
        }*/
        List<YxStoreProductAttrValue> attrValueList1 = new ArrayList<>();
        // ????????????-??????-??????-????????????
        HashMap<String,SkuSellerPriceStock> skuSellerPriceStockMap = map.get("skuSellerPriceStockMap");
        for (Map.Entry<String, SkuSellerPriceStock> entry : skuSellerPriceStockMap.entrySet()) {
           // System.out.println("key= " + entry.getKey() + " and value= " + entry.getValue());
            SkuSellerPriceStock skuSellerPriceStock = entry.getValue();
            // ??????sku ?????????????????????????????????
            QueryWrapper queryWrapper = new QueryWrapper<YxStoreProduct>().eq("yiyaobao_sku",skuSellerPriceStock.getSku()).select("price","stock","id");
            YxStoreProduct yxStoreProduct = yxStoreProductService.getOne(queryWrapper,false);
            //Medicine medicine = cmdStockDetailEbsMapper.getMedicineBySku(skuSellerPriceStock.getSku());
            if(yxStoreProduct != null) { // ?????????????????????????????????
                yxStoreProduct.setStock(yxStoreProduct.getStock() + skuSellerPriceStock.getStock().intValue());
                if( skuSellerPriceStock.getPrice().doubleValue() < yxStoreProduct.getPrice().doubleValue() || yxStoreProduct.getPrice().intValue() == 0 ) {
                    yxStoreProduct.setPrice(skuSellerPriceStock.getPrice());
                    yxStoreProduct.setCost(skuSellerPriceStock.getPrice());
                    yxStoreProduct.setVipPrice(skuSellerPriceStock.getPrice());
                    yxStoreProduct.setOtPrice(skuSellerPriceStock.getPrice());
                }
                yxStoreProduct.setIsDel(0);
                yxStoreProduct.setIsShow(1);
                // yxStoreProduct.setType(medicine.getType());
                yxStoreProductService.updateById(yxStoreProduct);
            }else{

                Medicine medicine = cmdStockDetailEbsMapper.getMedicineBySku(skuSellerPriceStock.getSku());
                List<String> imageList = cmdStockDetailEbsMapper.getMedicineImageBySku(skuSellerPriceStock.getSku());
                List<String> imageConvertList = new ArrayList<>();
                for(String imageUrl:imageList) {
                    log.info("????????????????????????{}",imageUrl);
                    QiniuContent qiniuContent = qiNiuService.uploadByUrl(imageUrl, qiNiuService.find());

                    if(qiniuContent != null ) {
                        imageConvertList.add(qiniuContent.getUrl());
                    }
                }

                if(CollUtil.isNotEmpty(imageConvertList)) {
                    medicine.setImage(imageConvertList.get(0));
                    medicine.setSliderImage(CollUtil.join(imageConvertList,","));
                }


                yxStoreProduct = new YxStoreProduct();
                BeanUtils.copyProperties(medicine,yxStoreProduct);
                // ???????????????????????????????????????
                if(StrUtil.isBlank(yxStoreProduct.getStoreName())) {
                    yxStoreProduct.setStoreName(yxStoreProduct.getCommonName());
                }

                if(StrUtil.isBlank(yxStoreProduct.getImage())) {
                    yxStoreProduct.setImage("http://pic.yiyao-mall.com/%E7%9B%8A%E8%8D%AF-%E8%8D%AF%E5%93%81.jpg");
                    yxStoreProduct.setSliderImage("http://pic.yiyao-mall.com/%E7%9B%8A%E8%8D%AF-%E8%8D%AF%E5%93%81.jpg");
                }

                // ??????????????????????????????
                yxStoreProduct.setGiveIntegral(new BigDecimal(0));
                yxStoreProduct.setIsDel(0);
                yxStoreProduct.setIsShow(1);
                yxStoreProduct.setStoreInfo(yxStoreProduct.getStoreName());
                yxStoreProduct.setPrice(skuSellerPriceStock.getPrice());
                yxStoreProduct.setCost(skuSellerPriceStock.getPrice());
                yxStoreProduct.setVipPrice(skuSellerPriceStock.getPrice());
                yxStoreProduct.setOtPrice(skuSellerPriceStock.getPrice());
                yxStoreProduct.setStock(skuSellerPriceStock.getStock().intValue());

                yxStoreProductService.save(yxStoreProduct);
                log.info("??????1???????????????????????????sku:[{}]",medicine.getYiyaobaoSku());
            }

            // ???????????? ?????????????????????????????????
            QueryWrapper<YxSystemStore> queryWrapper2 = new QueryWrapper();
            queryWrapper2.eq("yiyaobao_id",skuSellerPriceStock.getSellerId());
            YxSystemStore yxSystemStore = yxSystemStoreService.getOne(queryWrapper2,false);
            if(yxSystemStore == null) {
                continue;
            }

            QueryWrapper queryWrapper3 = new QueryWrapper();
            queryWrapper3.eq("product_id",yxStoreProduct.getId());
            queryWrapper3.eq("attr_name","??????");
            YxStoreProductAttr yxStoreProductAttr = yxStoreProductAttrService.getOne(queryWrapper3,false);
            if(yxStoreProductAttr == null) {
                yxStoreProductAttr = new YxStoreProductAttr();
                yxStoreProductAttr.setProductId(yxStoreProduct.getId());
                yxStoreProductAttr.setAttrName("??????");
                yxStoreProductAttr.setAttrValues(yxSystemStore.getName());
                yxStoreProductAttrService.save(yxStoreProductAttr);
            }


            QueryWrapper queryWrapper4 = new QueryWrapper();
            queryWrapper4.eq("product_id",yxStoreProduct.getId());
            queryWrapper4.eq("store_id",yxSystemStore.getId());
            YxStoreProductAttrValue yxStoreProductAttrValue = yxStoreProductAttrValueService.getOne(queryWrapper4,false);
            if(yxStoreProductAttrValue == null) {
                yxStoreProductAttrValue = new YxStoreProductAttrValue();
                yxStoreProductAttrValue.setProductName(yxStoreProduct.getStoreName());
                yxStoreProductAttrValue.setAttrId(yxStoreProductAttr.getId());
                yxStoreProductAttrValue.setProductId(yxStoreProduct.getId());
                yxStoreProductAttrValue.setStock(skuSellerPriceStock.getStock().intValue());
                yxStoreProductAttrValue.setStoreId(yxSystemStore.getId());
                yxStoreProductAttrValue.setSuk(yxSystemStore.getName());
                yxStoreProductAttrValue.setUnique(UUID.randomUUID().toString().replace("-",""));
                yxStoreProductAttrValue.setPrice(skuSellerPriceStock.getPrice());
                yxStoreProductAttrValue.setCost(skuSellerPriceStock.getPrice());
                yxStoreProductAttrValue.setImage(yxStoreProduct.getImage());
                yxStoreProductAttrValue.setIsDel(0);
                yxStoreProductAttrValue.setYiyaobaoSku(skuSellerPriceStock.getSku());
                yxStoreProductAttrValue.setYiyaobaoSellerId(skuSellerPriceStock.getSellerId());
               // yxStoreProductAttrValueService.save(yxStoreProductAttrValue);

            } else {
                yxStoreProductAttrValue.setStock(skuSellerPriceStock.getStock().intValue());
                yxStoreProductAttrValue.setPrice(skuSellerPriceStock.getPrice());
                yxStoreProductAttrValue.setCost(skuSellerPriceStock.getPrice());
                yxStoreProductAttrValue.setImage(yxStoreProduct.getImage());
                yxStoreProductAttrValue.setIsDel(0);
                yxStoreProductAttrValue.setYiyaobaoSku(skuSellerPriceStock.getSku());
                yxStoreProductAttrValue.setYiyaobaoSellerId(skuSellerPriceStock.getSellerId());
                yxStoreProductAttrValue.setAttrId(yxStoreProductAttr.getId());
               // yxStoreProductAttrValueService.updateById(yxStoreProductAttrValue);
            }
            attrValueList1.add(yxStoreProductAttrValue);

        }

        yxStoreProductAttrValueService.saveOrUpdateBatch(attrValueList1);

    /*    YxStoreProduct yxStoreProduct = yxStoreProductService.getOne(new QueryWrapper<YxStoreProduct>().eq("store_name","??????"));

        // ???????????????????????????-??????-?????? ??????????????????????????????
        QueryWrapper<YxStoreProductAttrValue> queryWrapper1 = new QueryWrapper();
        queryWrapper1.isNotNull("store_id");
        queryWrapper1.isNotNull("product_id");
        queryWrapper1.eq("is_del",0);
        if(yxStoreProduct != null) {
            queryWrapper1.ne("product_id",yxStoreProduct.getId());
        }

        queryWrapper1.select("yiyaobao_sku","yiyaobao_seller_id","id");
        List<YxStoreProductAttrValue> attrValueList = yxStoreProductAttrValueService.list(queryWrapper1);
        for(YxStoreProductAttrValue attr:attrValueList){
            String sku = attr.getYiyaobaoSku();
            String sellerId = attr.getYiyaobaoSellerId();
            if( StrUtil.isBlank(sku) || StrUtil.isBlank(sellerId) || !skuSellerPriceStockMap.containsKey(sku+"_"+sellerId) ) {
                attr.setIsDel(1);
                yxStoreProductAttrValueService.updateById(attr);
            }
        }


        // ?????????????????????????????????????????????????????????
        yxStoreProductService.updateIsShowByExistsAttr();*/


        // ????????????????????????
     //   redisUtils.set("SyncYiyaobaoSellDate",DateUtil.now());
    }

    void updateImage(){
        log.info("yiyaobaoImageUrlPrefix====={}" + yiyaobaoImageUrlPrefix);
        List<YxStoreProduct> yxStoreProductList = yxStoreProductService.list();
        for(YxStoreProduct yxStoreProduct: yxStoreProductList) {
            String image = "";
            String sliderImage = "";
            List<String> imageList = cmdStockDetailEbsMapper.getMedicineImageBySku(yxStoreProduct.getYiyaobaoSku());
            List<String> imageListDual = new ArrayList<>();
            if(CollUtil.isNotEmpty(imageList)) {
                 image = imageList.get(0);
                 image = yiyaobaoImageUrlPrefix + image;

                 for(String imageTemp:imageList) {
                    imageListDual.add(yiyaobaoImageUrlPrefix + imageTemp);
                }

                 sliderImage = CollUtil.join(imageListDual,",");

            }else {
               String defaultImage = localUrl + "/file/static/defaultMed.jpg";
                image = defaultImage;
                sliderImage = defaultImage;
            }
            yxStoreProduct.setImage(image);
            yxStoreProduct.setSliderImage(sliderImage);
            // ??????????????????
            UpdateWrapper updateWrapperMed = new UpdateWrapper();
            updateWrapperMed.set("image",image);
            updateWrapperMed.set("slider_image",sliderImage);
            updateWrapperMed.eq("yiyaobao_sku",yxStoreProduct.getYiyaobaoSku());
            yxStoreProductService.update(updateWrapperMed);

            // ???????????????????????????
            UpdateWrapper updateWrapper = new UpdateWrapper();
            updateWrapper.set("image",image);
            updateWrapper.eq("yiyaobao_sku",yxStoreProduct.getYiyaobaoSku());
            yxStoreProductAttrValueService.update(updateWrapper);

        }
    }
}
