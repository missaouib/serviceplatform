package co.yixiang.modules.baiji.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import co.yixiang.constant.ShopConstants;
import co.yixiang.enums.ProjectNameEnum;
import co.yixiang.modules.api.param.ProductMedStockParam;
import co.yixiang.modules.baiji.domain.BaiJiMed;
import co.yixiang.modules.baiji.domain.BaiJiStock;
import co.yixiang.modules.shop.domain.*;
import co.yixiang.modules.shop.service.*;
import co.yixiang.mp.domain.DictDetail;
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
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

@Service
@Slf4j
public class ProductMedStockServiceImpl {

    @Autowired
    private YxStoreProductAttrValueService yxStoreProductAttrValueService;

    @Autowired
    private Product4projectService  product4projectService;

    @Autowired
    private YxStoreProductService yxStoreProductService;

    @Autowired
    private YxSystemStoreService yxSystemStoreService;

    @Autowired
    private YxStoreProductAttrService yxStoreProductAttrService;

    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private BaiJiServiceImpl baiJiService;

    //定时同步百济药品库存
    public int syncBaiJiStoreMedStock(){
        Long t=System.currentTimeMillis();
        log.info("定时同步百济药品库存start");
        ProductMedStockParam productMedStockParam=new ProductMedStockParam();
        Integer count=omsSyncBaiJiStoreMedStock(productMedStockParam);
        log.info("定时同步百济药品库存end---:{}",System.currentTimeMillis()-t);
        return count;
    }

    //同步百济药品库存
    public int omsSyncBaiJiStoreMedStock(ProductMedStockParam productMedStockParam) {
        // 页码数
        Integer pageNo=1;
        // 每页条数
        Integer pageSize= 1000;
        boolean f=true;
        List<YxStoreProductAttrValue> productAttrValues = new ArrayList<>();
        List<Product4project> product4projects = new ArrayList<>();

        List<BaiJiStock> baiJiStockList=new ArrayList<>();
        do{
            List<BaiJiStock> baiJiStocks = baiJiService.queryStock(pageNo,pageSize,productMedStockParam.getPharmacyCode(),productMedStockParam.getGoodsCode());
            baiJiStockList.addAll(baiJiStocks);
            if(baiJiStocks.size()==pageSize){
                pageNo=pageNo+1;
            }else{
                f=false;
            }
        }while(f);

        for(BaiJiStock baiJiStock:baiJiStockList) {
            saveBaiJiStock(baiJiStock,productAttrValues,product4projects);
        }
        log.info("百济药品库存同步条数 count={}",baiJiStockList.size());
        if(productAttrValues.size()>0){
            yxStoreProductAttrValueService.saveOrUpdateBatch(productAttrValues);
        }
        if(product4projects.size()>0){
            product4projectService.saveOrUpdateBatch(product4projects);
        }

        return new Long(baiJiStockList.size()).intValue();
    }

    private void saveBaiJiStock(BaiJiStock baiJiStock,List<YxStoreProductAttrValue> list,List<Product4project> product4projects){
        int batchNo = OrderUtil.getSecondTimestampTwo();

        QueryWrapper queryWrapper_store = new QueryWrapper();
        queryWrapper_store.eq("yiyaobao_sku",baiJiStock.getGoodsCode());
        queryWrapper_store.eq("yiyaobao_seller_id",baiJiStock.getPharmacyCode());
        queryWrapper_store.select("id","stock","unique","product_id");
        YxStoreProductAttrValue productAttrValue =  yxStoreProductAttrValueService.getOne(queryWrapper_store,false);
        if(ObjectUtil.isEmpty(productAttrValue)) {
            QueryWrapper queryWrapper = new QueryWrapper();
            queryWrapper.eq("yiyaobao_sku",baiJiStock.getGoodsCode());
            YxStoreProduct yxStoreProduct =  yxStoreProductService.getOne(queryWrapper,false);
            if(ObjectUtil.isNotEmpty(yxStoreProduct)) {
                queryWrapper = new QueryWrapper();
                queryWrapper.eq("yiyaobao_id",baiJiStock.getPharmacyCode());
                queryWrapper.eq("is_del",0);
                YxSystemStore yxSystemStore=  yxSystemStoreService.getOne(queryWrapper,false);
                if(ObjectUtil.isNotEmpty(yxSystemStore)) {
                    YxStoreProductAttrValue yxStoreProductAttrValue = new YxStoreProductAttrValue();
                    yxStoreProductAttrValue.setProductId(yxStoreProduct.getId());
                    yxStoreProductAttrValue.setPrice(new BigDecimal(baiJiStock.getUnitPrice()/100));
                    yxStoreProductAttrValue.setStock(baiJiStock.getStock());
                    yxStoreProductAttrValue.setCost(new BigDecimal(baiJiStock.getUnitPrice()/100));
                    yxStoreProductAttrValue.setSales(0);
                    yxStoreProductAttrValue.setIsDel(0);
                    yxStoreProductAttrValue.setYiyaobaoSku(baiJiStock.getGoodsCode());
                    yxStoreProductAttrValue.setYiyaobaoSellerId(baiJiStock.getPharmacyCode());
                    yxStoreProductAttrValue.setUnique(UUID.randomUUID().toString());
                    yxStoreProductAttrValue.setStoreId(yxSystemStore.getId());
                    yxStoreProductAttrValue.setSuk(yxSystemStore.getName());
                    yxStoreProductAttrValue.setAttrId(batchNo);
                    list.add(yxStoreProductAttrValue);

                    Product4project product4project=new Product4project();
                    product4project.setProductId(yxStoreProduct.getId());
                    product4project.setProductName(yxStoreProduct.getStoreName());
                    product4project.setProductUniqueId(yxStoreProductAttrValue.getUnique());
                    product4project.setProjectNo(ProjectNameEnum.BAIJI.getValue());
                    product4project.setProjectNo(ProjectNameEnum.BAIJI.getDesc());
                    product4project.setStoreId(yxSystemStore.getId());
                    product4project.setStoreName(yxSystemStore.getName());
                    product4project.setIsShow(1);
                    product4project.setIsDel(0);
                    product4projects.add(product4project);
                }
            }
        } else {
            YxStoreProductAttrValue yxStoreProductAttrValue = new YxStoreProductAttrValue();
            yxStoreProductAttrValue.setId(productAttrValue.getId());
            yxStoreProductAttrValue.setStock(baiJiStock.getStock());
            yxStoreProductAttrValue.setPrice(new BigDecimal(baiJiStock.getUnitPrice()/100));
            yxStoreProductAttrValue.setCost(new BigDecimal(baiJiStock.getUnitPrice()/100));
            yxStoreProductAttrValue.setAttrId(batchNo);
            list.add(yxStoreProductAttrValue);
        }
    }

    //定时同步百济药品主数据
    public int syncBaiJiStoreMed(){
        Long t=System.currentTimeMillis();
        log.info("定时同步百济药品主数据start");
        ProductMedStockParam productMedStockParam=new ProductMedStockParam();
        Integer count=omsSyncBaiJiStoreMed(productMedStockParam);
        log.info("定时同步百济药品主数据end---:{}",System.currentTimeMillis()-t);
        return count;
    }

    /**
     * 同步百济药品主数据
     * @return
     */
    public int omsSyncBaiJiStoreMed(ProductMedStockParam productMedStockParam) {
        Date date=new Date();
        // 页码数
        Integer pageNo=1;
        // 每页条数
        Integer pageSize= 1000;
        boolean f=true;
        List<YxStoreProduct> yxStoreProducts = new ArrayList<>();
        List<Product4project> product4projects = new ArrayList<>();
        List<YxStoreProductAttrValue> yxStoreProductAttrValues = new ArrayList<>();

        List<BaiJiMed> baiJiMedList =new ArrayList<>();
        do{
            List<BaiJiMed> baiJiMeds = baiJiService.queryMed(pageNo,pageSize,productMedStockParam.getPharmacyCode(),productMedStockParam.getGoodsCode(),"");
            baiJiMedList.addAll(baiJiMeds);
            if(baiJiMeds.size()==pageSize){
                pageNo=pageNo+1;
            }else{
                f=false;
            }
        }while(f);

        for(BaiJiMed baiJiMed:baiJiMedList) {
            saveBaiJiMed(baiJiMed,yxStoreProducts,product4projects,yxStoreProductAttrValues);
        }

        if(yxStoreProducts.size()>0){
            yxStoreProductService.saveOrUpdateBatch(yxStoreProducts);
        }
        if(product4projects.size()>0){
            product4projectService.updateBatchById(product4projects);
        }
        if(yxStoreProductAttrValues.size()>0){
            yxStoreProductAttrValueService.updateBatchById(yxStoreProductAttrValues);
        }
        log.info("百济药品主数据同步条数 count={}",baiJiMedList.size());
        return new Long(baiJiMedList.size()).intValue();
    }


    private void saveBaiJiMed(BaiJiMed baiJiMed,List<YxStoreProduct> yxStoreProducts, List<Product4project> product4projectList,List<YxStoreProductAttrValue> yxStoreProductAttrValues){
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("yiyaobao_sku",baiJiMed.getCode());
        YxStoreProduct yxStoreProduct =  yxStoreProductService.getOne(queryWrapper,false);

        YxStoreProduct storeProduct=new YxStoreProduct();
        BeanUtils.copyProperties(baiJiMed, storeProduct);
        storeProduct.setStoreName(baiJiMed.getName());//商品名称
        storeProduct.setTaxRate(StringUtils.isEmpty(baiJiMed.getTaxRate())?BigDecimal.ZERO :new BigDecimal(baiJiMed.getTaxRate()));//交易税率
        storeProduct.setYiyaobaoSku(baiJiMed.getCode());//药品sku编码
        storeProduct.setIsDel(Integer.valueOf(baiJiMed.getIzDel()));//是否删除（0 /否，1/是）


        if(ObjectUtil.isEmpty(yxStoreProduct)) {
            storeProduct.setId(yxStoreProduct.getId());
        }else{
            QueryWrapper queryWrapper_value = new QueryWrapper();
            queryWrapper_value.eq("yiyaobao_sku",baiJiMed.getCode());
            List<YxStoreProductAttrValue> productAttrValues =  yxStoreProductAttrValueService.list(queryWrapper_value);
            for (YxStoreProductAttrValue productAttrValue : productAttrValues) {
                YxStoreProductAttrValue yxStoreProductAttrValue=new YxStoreProductAttrValue();
                yxStoreProductAttrValue.setIsDel(storeProduct.getIsDel());
                yxStoreProductAttrValue.setId(productAttrValue.getId());
                yxStoreProductAttrValues.add(yxStoreProductAttrValue);
            }

            QueryWrapper queryWrapper_project = new QueryWrapper();
            queryWrapper_project.eq("product_id",baiJiMed.getCode());
            queryWrapper_project.eq("project_no",ProjectNameEnum.BAIJI.getValue());
            List<Product4project> product4projects =  product4projectService.list(queryWrapper_project);
            for (Product4project project : product4projects) {
                Product4project product4project=new Product4project();
                product4project.setIsDel(storeProduct.getIsDel());
                product4project.setId(project.getId());
                product4projectList.add(product4project);
            }
        }
        yxStoreProducts.add(storeProduct);
    }

}
