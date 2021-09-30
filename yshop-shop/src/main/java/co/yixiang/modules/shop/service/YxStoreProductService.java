/**
 * Copyright (C) 2018-2020
 * All rights reserved, Designed By www.yixiang.co

 */
package co.yixiang.modules.shop.service;

import co.yixiang.common.service.BaseService;
import co.yixiang.modules.shop.domain.YxStoreProduct;
import co.yixiang.modules.shop.service.dto.ProductFormatDto;
import co.yixiang.modules.shop.service.dto.YxStoreProductDto;
import co.yixiang.modules.shop.service.dto.YxStoreProductQueryCriteria;
import co.yixiang.modules.shop.service.dto.YxStoreProductQueryVo;
import org.springframework.data.domain.Pageable;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
* @author hupeng
* @date 2020-05-12
*/
public interface YxStoreProductService  extends BaseService<YxStoreProduct>{

/**
    * 查询数据分页
    * @param criteria 条件
    * @param pageable 分页参数
    * @return Map<String,Object>
    */
    Map<String,Object> queryAll(YxStoreProductQueryCriteria criteria, Pageable pageable);

    /**
    * 查询所有数据不分页
    * @param criteria 条件参数
    * @return List<YxStoreProductDto>
    */
    List<YxStoreProduct> queryAll(YxStoreProductQueryCriteria criteria);

    /**
    * 导出数据
    * @param all 待导出的数据
    * @param response /
    * @throws IOException /
    */
    void download(List<YxStoreProduct> all, HttpServletResponse response,String projectCode) throws IOException;

    void downloadSample(List<YxStoreProduct> all, HttpServletResponse response,String projectCode) throws IOException;

    void downloadCommon(List<YxStoreProduct> all, HttpServletResponse response) throws IOException;

    YxStoreProduct saveProduct(YxStoreProduct storeProduct);

    void recovery(Integer id);

    void onSale(Integer id, int status);

    List<ProductFormatDto> isFormatAttr(Integer id, String jsonStr);

    void createProductAttr(Integer id, String jsonStr);

    void clearProductAttr(Integer id,boolean isActice);

    void setResult(Map<String, Object> map,Integer id);

    String getStoreProductAttrResult(Integer id);

    YxStoreProduct updateProduct(YxStoreProduct resources);

    void delete(Integer id);

    void updateIsShowByExistsAttr();

    void convertImage();

    int uploadProduct(List<Map<String,Object>> list);

    int uploadProduct4Project(List<Map<String,Object>> list,String projectCode);

    int uploadProduct4Project2(List<Map<String,Object>> list,String projectCode);

    int uploadProduct4ProjectSimple(List<Map<String,Object>> list);

    int uploadProduct4Lingyuanzhi(List<Map<String,Object>> list,String projectCode);

    int getProductStock(int productId,String unique);
    void decProductStock(int num,int productId,String unique);


    /**
     * 查询数据分页
     * @param criteria 条件
     * @param pageable 分页参数
     * @return Map<String,Object>
     */
    Map<String,Object> queryAll4pc(YxStoreProductQueryCriteria criteria, Pageable pageable);

    void updateDideaseParent();

    void updatePriceStock();

    void updatePrice(YxStoreProduct resources);

    YxStoreProductQueryVo selectById(Integer productId);

    Boolean dualProuctDisease2Redis(String projectCode);

    void syncEBSProductStockBySku(YxStoreProductQueryCriteria criteria);

    int uploadProductGroup(List<Map<String,Object>> list);

    void downloadProductGroup(List<YxStoreProduct> all, HttpServletResponse response,String projectCode) throws IOException;

    BigDecimal queryProductPrice(String skuCode,String projectCode);
}
