package co.yixiang.modules.shop.mapper;


import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import co.yixiang.modules.shop.entity.YxSystemStore;
import co.yixiang.modules.shop.web.param.YxSystemStoreQueryParam;
import co.yixiang.modules.shop.web.vo.YxSystemStoreQueryVo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.util.List;

/**
 * <p>
 * 门店自提 Mapper 接口
 * </p>
 *
 * @author hupeng
 * @since 2020-03-04
 */
@Repository
public interface YxSystemStoreMapper extends BaseMapper<YxSystemStore> {
    @Select("SELECT *,ROUND(6378.138 * 2 * ASIN(SQRT(POW(SIN((#{lat} * PI() / 180 - latitude * PI() / 180" +
            "    ) / 2),2) + COS(40.0497810000 * PI() / 180) * COS(latitude * PI() / 180) * POW(" +
            "    SIN((#{lon} * PI() / 180 - longitude * PI() / 180) / 2),2))) * 1000) AS distance " +
            "    FROM yx_system_store WHERE is_del=0 AND is_show = 1 ORDER BY distance ASC"
    )
    List<YxSystemStoreQueryVo> getStoreList(Page page,@Param("lon") double lon,@Param("lat") double lat);



    List<YxSystemStoreQueryVo> getStoreList4County(Page page,@Param("lon") double lon,@Param("lat") double lat,@Param("selectCountryList") List<String> selectCountryList,@Param("keyword") String keyword,String provinceName );



    @Select("SELECT a.*,ROUND(6378.138 * 2 * ASIN(SQRT(POW(SIN((#{lat} * PI() / 180 - latitude * PI() / 180 " +
            "   ) / 2),2) + COS(40.0497810000 * PI() / 180) * COS(latitude * PI() / 180) * POW(" +
            "   SIN((#{lon} * PI() / 180 - longitude * PI() / 180) / 2),2))) * 1000) AS distance,yspav.price AS price,yspav.stock as stock,yspav.`unique` as `unique` " +
            " FROM yx_system_store a,yx_store_product_attr_value yspav" +
            " WHERE a.is_del=0 AND a.is_show = 1 and yspav.stock > 0 " +
            "  AND a.id = yspav.store_id AND yspav.product_id = #{productId} and yspav.is_del = 0 and yspav.suk != #{storeName} ORDER BY distance ASC "
    )
    List<YxSystemStoreQueryVo> getStoreListByProductId(Page page,@Param("lon") double lon,@Param("lat") double lat,@Param("productId") Integer productId,@Param("storeName") String storeName);





    List<YxSystemStoreQueryVo> getStoreListByProductIdStoreIds(Page page,@Param("lon") double lon,@Param("lat") double lat,@Param("productId") Integer productId,@Param("storeIds") List<Integer> storeIds);

    List<YxSystemStoreQueryVo> getStoreListByProductIdStoreIdsNoGPS(Page page,@Param("productId") Integer productId,@Param("storeIds") List<Integer> storeIds);

    @Select("SELECT a.*,yspav.price AS price,yspav.stock as stock,yspav.`unique` as `unique` \n" +
            "             FROM yx_system_store a,yx_store_product_attr_value yspav\n" +
            "             WHERE a.is_del=0 AND a.is_show = 1 \n" +
            "              AND a.id = yspav.store_id AND yspav.product_id = #{productId} and yspav.is_del = 0 and yspav.stock > 0 ORDER BY yspav.price ASC "
    )
    List<YxSystemStoreQueryVo> getStoreListByProductIdNoGPS(Page page,@Param("productId") Integer productId);



    @Select("SELECT a.*,ROUND(6378.138 * 2 * ASIN(SQRT(POW(SIN((#{lat} * PI() / 180 - latitude * PI() / 180 " +
            "   ) / 2),2) + COS(40.0497810000 * PI() / 180) * COS(latitude * PI() / 180) * POW(" +
            "   SIN((#{lon} * PI() / 180 - longitude * PI() / 180) / 2),2))) * 1000) AS distance,yspav.price AS price,yspav.stock as stock,yspav.`unique` as `unique` " +
            " FROM yx_system_store a,yx_store_product_attr_value yspav" +
            " WHERE a.is_del=0 AND a.is_show = 1  " +
            "  AND a.id = yspav.store_id AND yspav.product_id = #{productId} and yspav.suk= #{drugstoreName} and yspav.is_del = 0 ORDER BY distance ASC "
    )
    List<YxSystemStoreQueryVo> getStoreListByProductIdDrugstoreName(Page page,@Param("lon") double lon,@Param("lat") double lat,@Param("productId") Integer productId,@Param("drugstoreName") String drugstoreName);



    @Select("SELECT a.*,ROUND(6378.138 * 2 * ASIN(SQRT(POW(SIN((#{lat} * PI() / 180 - latitude * PI() / 180 " +
            "   ) / 2),2) + COS(40.0497810000 * PI() / 180) * COS(latitude * PI() / 180) * POW(" +
            "   SIN((#{lon} * PI() / 180 - longitude * PI() / 180) / 2),2))) * 1000) AS distance, ifnull( p.unit_price,yspav.price) AS price,yspav.stock as stock,yspav.`unique` as `unique` " +
            " FROM yx_system_store a,yx_store_product_attr_value yspav,product4project p" +
            " WHERE a.is_del=0 AND a.is_show = 1  AND p.product_unique_id = yspav.`unique` AND p.project_no = #{projectCode} and p.is_del = 0 and p.is_show = 1 and yspav.stock >0 " +
            "  AND a.id = yspav.store_id AND yspav.product_id = #{productId} and yspav.is_del = 0 ORDER BY distance ASC "
    )
    List<YxSystemStoreQueryVo> getStoreListByProductIdProjectCode(Page page,@Param("lon") double lon,@Param("lat") double lat,@Param("productId") Integer productId,@Param("projectCode") String projectCode);



    @Select("SELECT a.*,ifnull(p.unit_price,yspav.price) AS price,yspav.stock as stock,yspav.`unique` as `unique` \n" +
            "             FROM yx_system_store a,yx_store_product_attr_value yspav,product4project p\n" +
            "             WHERE a.is_del=0 AND a.is_show = 1  AND p.product_unique_id = yspav.`unique` AND p.project_no = #{projectCode} and p.is_del = 0 and p.is_show = 1 \n" +
            "              AND a.id = yspav.store_id AND yspav.product_id = #{productId} and yspav.is_del = 0 and yspav.stock >0 ORDER BY price ASC"
    )
    List<YxSystemStoreQueryVo> getStoreListByProductIdProjectCodeNoGPS(Page page,@Param("productId") Integer productId,@Param("projectCode") String projectCode);


    /**
     * 根据ID获取查询对象
     * @param id
     * @return
     */
    YxSystemStoreQueryVo getYxSystemStoreById(Serializable id);

    /**
     * 获取分页对象
     * @param page
     * @param yxSystemStoreQueryParam
     * @return
     */
    IPage<YxSystemStoreQueryVo> getYxSystemStorePageList(@Param("page") Page page, @Param("param") YxSystemStoreQueryParam yxSystemStoreQueryParam);

}
