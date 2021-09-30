/**
 * Copyright (C) 2018-2020
 * All rights reserved, Designed By www.yixiang.co

 */
package co.yixiang.modules.shop.service.mapper;

import co.yixiang.common.mapper.CoreMapper;
import co.yixiang.modules.shop.domain.YxStoreProduct;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

/**
* @author hupeng
* @date 2020-05-12
*/
@Repository
@Mapper
public interface StoreProductMapper extends CoreMapper<YxStoreProduct> {


    @Update("update yx_store_product set is_del = #{status} where id = #{id}")
    void updateDel(@Param("status")int status,@Param("id") Integer id);
    @Update("update yx_store_product set is_show = #{status} where id = #{id}")
    void updateOnsale(@Param("status")int status, @Param("id")Integer id);

    @Select("SELECT dd.value  FROM dict d, dict_detail dd WHERE d.id = dd.dict_id AND d.name = 'productBatchNo' and dd.label = #{label}")
    String queryDefaultBatchNo(@Param("label") String label);

    @Update("UPDATE yx_store_product ysp set ysp.is_show = 0 \n" +
            "     WHERE not EXISTS(SELECT 1 FROM yx_store_product_attr_value yspav WHERE ysp.id = yspav.product_id AND yspav.is_del = 0 AND yspav.store_id IS NOT null)")
    void updateIsShowByExistsAttr();

    @Update("update yx_store_product set sales=sales+#{num}" +
            " where id=#{productId}")
    int incSales(@Param("num") int num,@Param("productId") int productId);

    @Update("update yx_store_product set sales=sales-#{num}" +
            " where id=#{productId}")
    int decSales(@Param("num") int num,@Param("productId") int productId);

    @Update("update yx_store_product set stock=stock-#{num}, sales=sales+#{num}" +
            " where id=#{productId}")
    int decStockIncSales(@Param("num") int num,@Param("productId") int productId);

    @Update("update yx_store_product set stock=stock+#{num}, sales=sales-#{num}" +
            " where id=#{productId}")
    int incStockDecSales(@Param("num") int num,@Param("productId") int productId);

    @Select("SELECT dd.value  FROM dict d,dict_detail dd WHERE d.id= dd.dict_id AND d.name ='productType' AND dd.label= #{label} LIMIT 1")
    String queryProductType(@Param("label") String label);

    @Select("SELECT dd.label  FROM dict d,dict_detail dd WHERE d.id= dd.dict_id AND d.name ='productType' AND dd.value= #{value} LIMIT 1")
    String queryProductTypeName(@Param("value") String value);

    @Update("UPDATE yx_store_product ysp ,(SELECT yspav.product_id,MIN(yspav.price) AS mini_price,SUM(yspav.stock) AS sum_stock FROM yx_store_product_attr_value yspav WHERE yspav.is_del = 0 GROUP BY yspav.product_id) x\n" +
            "  set ysp.price = X.mini_price,ysp.stock = X.sum_stock,ysp.vip_price = x.mini_price,ysp.ot_price = x.mini_price\n" +
            "  WHERE ysp.id = X.product_id")
    void updatePriceStock();

    @Select("SELECT ysp.disease_id FROM yx_store_product ysp,product4project p\n" +
            "  WHERE ysp.id = p.product_id\n" +
            "  AND ysp.is_show= 1\n" +
            "  AND ysp.is_del = 0\n" +
            "  AND p.project_no = #{projectCode} \n" +
            "  AND ysp.disease_id !=''" +
            " and ysp.disease_id is not null" +
            " AND p.is_del = 0 \n" +
            " AND p.is_show = 1" +
            " " +
            "")
    List<String> queryDiseaseByProjectCode(@Param("projectCode") String projectCode);


    @Select("SELECT ysp.disease_id FROM yx_store_product ysp,product4project p\n" +
            "  WHERE ysp.id = p.product_id\n" +
            "  AND ysp.is_show= 1\n" +
            "  AND ysp.is_del = 0\n" +
            "  AND p.project_no = #{projectCode} \n" +
            "  AND ysp.disease_id !=''" +
            " and ysp.disease_id is not null" +
            " and ysp.label1 = 'Y'" +
            " AND p.is_del = 0 \n" +
            " AND p.is_show = 1" +
            "")
    List<String> queryDiseaseByProjectCodeLabel1(@Param("projectCode") String projectCode);


    @Select("SELECT ysp.disease_id FROM yx_store_product ysp,product4project p\n" +
            "  WHERE ysp.id = p.product_id\n" +
            "  AND ysp.is_show= 1\n" +
            "  AND ysp.is_del = 0\n" +
            "  AND p.project_no = #{projectCode} \n" +
            "  AND ysp.disease_id !=''" +
            " and ysp.disease_id is not null" +
            " and ysp.label2 = 'Y'" +
            " AND p.is_del = 0 \n" +
            " AND p.is_show = 1")
    List<String> queryDiseaseByProjectCodeLabel2(@Param("projectCode") String projectCode);

    @Select("SELECT ysp.disease_id FROM yx_store_product ysp,product4project p\n" +
            "  WHERE ysp.id = p.product_id\n" +
            "  AND ysp.is_show= 1\n" +
            "  AND ysp.is_del = 0\n" +
            "  AND p.project_no = #{projectCode} \n" +
            "  AND ysp.disease_id !=''" +
            " and ysp.disease_id is not null" +
            " and ysp.label3 = 'Y'" +
            " AND p.is_del = 0 \n" +
            " AND p.is_show = 1" +
            "")
    List<String> queryDiseaseByProjectCodeLabel3(@Param("projectCode") String projectCode);

    @Select("SELECT ysp.disease_id FROM yx_store_product ysp,yx_store_product_attr_value yspav\n" +
            "  WHERE ysp.id = yspav.product_id\n" +
            "  AND ysp.is_show = 1\n" +
            "  AND ysp.is_del = 0\n" +
            "  AND yspav.is_del = 0\n" +
            "  AND  ysp.disease_id !=''\n" +
            "  AND ysp.disease_id IS NOT null\n" +
            "  AND yspav.store_id IN (${storeIds})")
    List<String> queryDiseaseByStoreids(@Param("storeIds") String storeIds);

    BigDecimal queryProductPrice(@Param("skuCode") String skuCode,@Param("projectCode") String projectCode);
}


