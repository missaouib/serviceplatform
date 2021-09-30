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

        log.info("获取益药宝库存数据{}条",list.size());
        return list;
    }

    @DS("multi-datasource1")
    public Map<String,HashMap> getYiyaobaoMedicineData(){
        // 获取药品-药房的价格和库存 按城市
        List<SkuSellerPriceStock> skuSellerPriceStockByCityList = cmdStockDetailEbsMapper.getSellerPriceStockByCity();
        // 获取药品-药房的价格和库存 按药房
        List<SkuSellerPriceStock> skuSellerPriceStockBySellerList = cmdStockDetailEbsMapper.getSellerPriceStockBySeller();
        // 药品主数据
     //   HashMap<String, Medicine> skuMap = new HashMap<>();

        HashMap<String,SkuSellerPriceStock> skuSellerPriceStockMap = new HashMap<>();
        // 按城市获取的药品-药店-库存
        for(SkuSellerPriceStock skuSellerPriceStock:skuSellerPriceStockByCityList){
           /* if( !skuMap.containsKey(skuSellerPriceStock.getSku())) {
                Medicine medicine = cmdStockDetailEbsMapper.getMedicineBySku(skuSellerPriceStock.getSku());
                skuMap.put(skuSellerPriceStock.getSku(),medicine);
            }*/
            skuSellerPriceStockMap.put(skuSellerPriceStock.getSku()+"_"+skuSellerPriceStock.getSellerId(),skuSellerPriceStock);
        }

        //优先 按药店获取的药品-药店-库存；
        for(SkuSellerPriceStock skuSellerPriceStock:skuSellerPriceStockBySellerList){
         /*   if( !skuMap.containsKey(skuSellerPriceStock.getSku())) {
                Medicine medicine = cmdStockDetailEbsMapper.getMedicineBySku(skuSellerPriceStock.getSku());
                skuMap.put(skuSellerPriceStock.getSku(),medicine);
            }*/

            skuSellerPriceStockMap.put(skuSellerPriceStock.getSku()+"_"+skuSellerPriceStock.getSellerId(),skuSellerPriceStock);
        }

        // 更新药品主数据中的图片信息
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


        // 获取药房主数据
        List<Seller> sellerList = cmdStockDetailEbsMapper.getSellerByDate(startDate);
        // 获取药房图片
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
        // 获取益药宝所有的ebs库存
       // List<CmdStockDetailEbs> list = cmdStockDetailEbsService.list4ds();

        QueryWrapper queryWrapper = new QueryWrapper();
        // 删除历史数据
        cmdStockDetailEbsService.remove(queryWrapper);
        log.info("删除ebs库存");
        // 保存最新数据
        cmdStockDetailEbsService.saveBatch(list);
        log.info("批量新增ebs库存");

        // 根据 ebs库存中存在的药品sku，同步益药公众号的sku

        for(CmdStockDetailEbs detailEbs:list) {
            String yiyaobaosku = detailEbs.getSku();
            int count = yxStoreProductService.count(new QueryWrapper<YxStoreProduct>().eq("yiyaobao_sku",yiyaobaosku));

        }

        QueryWrapper queryWrapper1 = new QueryWrapper();
        queryWrapper1.isNotNull("store_id");
        // 平适药品不需要更新库存
        queryWrapper1.ne("product_id",101717);
        List<YxStoreProductAttrValue> attrValueList = yxStoreProductAttrValueService.list(queryWrapper1);
        for(YxStoreProductAttrValue attrValue:attrValueList) {
            Integer productId = attrValue.getProductId();
            YxStoreProduct yxStoreProduct = yxStoreProductService.getById(productId);
            if(yxStoreProduct == null) {
                log.error("药品id{}找不到",productId);
                continue;
            }
            String yiyaobaoSku = yxStoreProduct.getYiyaobaoSku();

            YxSystemStore yxSystemStore = yxSystemStoreService.getById(attrValue.getStoreId());
            if(yxStoreProduct == null) {
                log.error("药点id{}找不到",attrValue.getStoreId());
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
                log.error("yiyaobaoSku={},SELLER_ID={} 数据为空",yiyaobaoSku,yiyaobaoSellerId);
            }
            attrValue.setStock(stock);

            yxStoreProductAttrValueService.updateById(attrValue);
        }


/*
        for(CmdStockDetailEbs cmdStockDetailEbs:list) {
            // 判断sku 药品主数据中存在
            QueryWrapper<YxStoreProduct> queryWrapper1 = new QueryWrapper();
            queryWrapper1.eq("yiyaobao_sku",cmdStockDetailEbs.getSku());
            YxStoreProduct yxStoreProduct =  yxStoreProductService.getOne(queryWrapper1,false);
            if(yxStoreProduct == null) {
                continue;
            }

            // 判断药店 在药房主数据中是否存在
            QueryWrapper<YxSystemStore> queryWrapper2 = new QueryWrapper();
            queryWrapper2.eq("yiyaobao_id",cmdStockDetailEbs.getSellerId());
            YxSystemStore yxSystemStore = yxSystemStoreService.getOne(queryWrapper2,false);
            if(yxSystemStore == null) {
                continue;
            }

            QueryWrapper queryWrapper3 = new QueryWrapper();
            queryWrapper3.eq("product_id",yxStoreProduct.getId());
            queryWrapper3.eq("attr_name","药店");
            YxStoreProductAttr yxStoreProductAttr = yxStoreProductAttrService.getOne(queryWrapper3,false);
            if(yxStoreProductAttr == null) {
                yxStoreProductAttr = new YxStoreProductAttr();
                yxStoreProductAttr.setProductId(yxStoreProduct.getId());
                yxStoreProductAttr.setAttrName("药店");
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

/* 普通门店的药品库存*/
    public void syncYiyaobaoMedicine(){

        // 获取上次同步库存的时间
     /*   String startDate = "2000-01-01 00:00:01";
        if(redisUtils.get("SyncYiyaobaoSellDate") != null) {
            startDate = (String) redisUtils.get("SyncYiyaobaoSellDate");
        }*/


        // 同步药房信息
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

        // 同步药品主数据信息
        Map<String,HashMap> map = getYiyaobaoMedicineData();
   /*     HashMap<String,Medicine> skuMap = map.get("skuMap");

        for(Map.Entry<String, Medicine> entry : skuMap.entrySet()) {
            Medicine medicine = entry.getValue();
            // 判断sku 是否在益药公众号中存在
            YxStoreProduct yxStoreProduct = yxStoreProductService.getOne(new QueryWrapper<YxStoreProduct>().eq("yiyaobao_sku",medicine.getYiyaobaoSku()),true);
            if(yxStoreProduct == null) { // 如果不存在，新增商品信息

            }
        }*/
        List<YxStoreProductAttrValue> attrValueList1 = new ArrayList<>();
        // 同步药品-药房-库存-价格信息
        HashMap<String,SkuSellerPriceStock> skuSellerPriceStockMap = map.get("skuSellerPriceStockMap");
        for (Map.Entry<String, SkuSellerPriceStock> entry : skuSellerPriceStockMap.entrySet()) {
           // System.out.println("key= " + entry.getKey() + " and value= " + entry.getValue());
            SkuSellerPriceStock skuSellerPriceStock = entry.getValue();
            // 判断sku 是否在益药公众号中存在
            QueryWrapper queryWrapper = new QueryWrapper<YxStoreProduct>().eq("yiyaobao_sku",skuSellerPriceStock.getSku()).select("price","stock","id");
            YxStoreProduct yxStoreProduct = yxStoreProductService.getOne(queryWrapper,false);
            //Medicine medicine = cmdStockDetailEbsMapper.getMedicineBySku(skuSellerPriceStock.getSku());
            if(yxStoreProduct != null) { // 更新库存信息和价格信息
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
                    log.info("药品图片的地址：{}",imageUrl);
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
                // 商品名称如果为空，用通用名
                if(StrUtil.isBlank(yxStoreProduct.getStoreName())) {
                    yxStoreProduct.setStoreName(yxStoreProduct.getCommonName());
                }

                if(StrUtil.isBlank(yxStoreProduct.getImage())) {
                    yxStoreProduct.setImage("http://pic.yiyao-mall.com/%E7%9B%8A%E8%8D%AF-%E8%8D%AF%E5%93%81.jpg");
                    yxStoreProduct.setSliderImage("http://pic.yiyao-mall.com/%E7%9B%8A%E8%8D%AF-%E8%8D%AF%E5%93%81.jpg");
                }

                // 更新单价，库存，积分
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
                log.info("新增1条药品主数据信息，sku:[{}]",medicine.getYiyaobaoSku());
            }

            // 判断药店 在药房主数据中是否存在
            QueryWrapper<YxSystemStore> queryWrapper2 = new QueryWrapper();
            queryWrapper2.eq("yiyaobao_id",skuSellerPriceStock.getSellerId());
            YxSystemStore yxSystemStore = yxSystemStoreService.getOne(queryWrapper2,false);
            if(yxSystemStore == null) {
                continue;
            }

            QueryWrapper queryWrapper3 = new QueryWrapper();
            queryWrapper3.eq("product_id",yxStoreProduct.getId());
            queryWrapper3.eq("attr_name","药店");
            YxStoreProductAttr yxStoreProductAttr = yxStoreProductAttrService.getOne(queryWrapper3,false);
            if(yxStoreProductAttr == null) {
                yxStoreProductAttr = new YxStoreProductAttr();
                yxStoreProductAttr.setProductId(yxStoreProduct.getId());
                yxStoreProductAttr.setAttrName("药店");
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

    /*    YxStoreProduct yxStoreProduct = yxStoreProductService.getOne(new QueryWrapper<YxStoreProduct>().eq("store_name","平适"));

        // 益药公众号中的药品-库存-价格 不在益药宝中，则删掉
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


        // 益药公众号中的药品不在益药宝中，则删掉
        yxStoreProductService.updateIsShowByExistsAttr();*/


        // 更新药房同步时间
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
            // 更新药品照片
            UpdateWrapper updateWrapperMed = new UpdateWrapper();
            updateWrapperMed.set("image",image);
            updateWrapperMed.set("slider_image",sliderImage);
            updateWrapperMed.eq("yiyaobao_sku",yxStoreProduct.getYiyaobaoSku());
            yxStoreProductService.update(updateWrapperMed);

            // 更新属性表上的照片
            UpdateWrapper updateWrapper = new UpdateWrapper();
            updateWrapper.set("image",image);
            updateWrapper.eq("yiyaobao_sku",yxStoreProduct.getYiyaobaoSku());
            yxStoreProductAttrValueService.update(updateWrapper);

        }
    }
}
