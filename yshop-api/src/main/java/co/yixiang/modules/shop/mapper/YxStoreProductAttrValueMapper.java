package co.yixiang.modules.shop.mapper;

import co.yixiang.modules.shop.web.dto.PriceMinMaxDTO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import co.yixiang.modules.shop.entity.YxStoreProductAttrValue;
import co.yixiang.modules.shop.web.param.YxStoreProductAttrValueQueryParam;
import co.yixiang.modules.shop.web.vo.YxStoreProductAttrValueQueryVo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import java.io.Serializable;

/**
 * <p>
 * 商品属性值表 Mapper 接口
 * </p>
 *
 * @author hupeng
 * @since 2019-10-23
 */
@Repository
public interface YxStoreProductAttrValueMapper extends BaseMapper<YxStoreProductAttrValue> {

    @Select("select sum(stock) from yx_store_product_attr_value " +
            "where product_id = #{productId} and is_del = 0")
    Integer sumStock(Integer productId);

    @Update("update yx_store_product_attr_value set stock=stock-#{num}, sales=sales+#{num}" +
            " where product_id=#{productId} and `unique`=#{unique}")
    int decStockIncSales(@Param("num") int num,@Param("productId") int productId,
                 @Param("unique")  String unique);

    @Update("update yx_store_product_attr_value set stock=stock+#{num}, sales=sales-#{num}" +
            " where product_id=#{productId} and `unique`=#{unique}")
    int incStockDecSales(@Param("num") int num,@Param("productId") int productId,
                         @Param("unique")  String unique);



    /**
     * 根据ID获取查询对象
     * @param id
     * @return
     */
    YxStoreProductAttrValueQueryVo getYxStoreProductAttrValueById(Serializable id);

    /**
     * 获取分页对象
     * @param page
     * @param yxStoreProductAttrValueQueryParam
     * @return
     */
    IPage<YxStoreProductAttrValueQueryVo> getYxStoreProductAttrValuePageList(@Param("page") Page page, @Param("param") YxStoreProductAttrValueQueryParam yxStoreProductAttrValueQueryParam);

    @Select("SELECT MIN(yspav.price) AS priceMin,MAX(yspav.price) AS priceMax FROM yx_store_product_attr_value yspav WHERE yspav.product_id = #{productId} AND yspav.stock >0 and yspav.is_del = 0 and yspav.suk != #{storeName}")
    PriceMinMaxDTO getPriceMinMax(@Param("productId") Integer productId,@Param("storeName") String storeName);

    @Select("SELECT MIN(yspav.price) AS priceMin,MAX(yspav.price) AS priceMax FROM yx_store_product_attr_value yspav WHERE yspav.product_id = #{productId} AND yspav.stock >0 and yspav.is_del = 0 and yspav.suk = #{storeName}")
    PriceMinMaxDTO getPriceMinMaxEq(@Param("productId") Integer productId,@Param("storeName") String storeName);

    @Select("SELECT IFNULL( MAX( IFNULL(p.unit_price,yspav.price)),0) AS priceMax,  IFNULL( MIN( IFNULL(p.unit_price,yspav.price)),0) AS priceMin FROM product4project p,yx_store_product_attr_value yspav WHERE p.product_unique_id = yspav.`unique` AND yspav.stock >0 and yspav.is_del = 0 and p.project_no = #{projectCode} AND p.product_id = #{productId} AND p.is_del = 0 AND p.is_show = 1")
    PriceMinMaxDTO getPriceMinMaxProjectCode(@Param("projectCode") String projectCode,@Param("productId") Integer productId);


    @Select("SELECT ifnull(MIN(yspav.price),0) AS priceMin, ifnull(MAX(yspav.price),0) AS priceMax FROM yx_store_product_attr_value yspav WHERE yspav.product_id = #{productId} AND yspav.stock >0 and yspav.is_del = 0 and yspav.store_id in ( ${storeIds})")
    PriceMinMaxDTO getPriceMinMaxStoreIds(@Param("storeIds") String storeIds,@Param("productId") Integer productId);
}
