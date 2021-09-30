package co.yixiang.modules.shop.mapper;

import co.yixiang.modules.meideyi.Goods;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import co.yixiang.modules.shop.entity.YxStoreProduct;
import co.yixiang.modules.shop.web.param.YxStoreProductQueryParam;
import co.yixiang.modules.shop.web.vo.YxStoreProductQueryVo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.util.List;

/**
 * <p>
 * 商品表 Mapper 接口
 * </p>
 *
 * @author hupeng
 * @since 2019-10-19
 */
@Repository
public interface YxStoreProductMapper extends BaseMapper<YxStoreProduct> {

    @Update("update yx_store_product set stock=stock-#{num}, sales=sales+#{num}" +
            " where id=#{productId}")
    int decStockIncSales(@Param("num") int num,@Param("productId") int productId);

    @Update("update yx_store_product set stock=stock+#{num}, sales=sales-#{num}" +
            " where id=#{productId}")
    int incStockDecSales(@Param("num") int num,@Param("productId") int productId);

    @Update("update yx_store_product set sales=sales+#{num}" +
            " where id=#{productId}")
    int incSales(@Param("num") int num,@Param("productId") int productId);

    @Update("update yx_store_product set sales=sales-#{num}" +
            " where id=#{productId}")
    int decSales(@Param("num") int num,@Param("productId") int productId);

    /**
     * 根据ID获取查询对象
     * @param id
     * @return
     */
    YxStoreProductQueryVo getYxStoreProductById(Serializable id);

    /**
     * 获取分页对象
     * @param page
     * @param yxStoreProductQueryParam
     * @return
     */
    IPage<YxStoreProductQueryVo> getYxStoreProductPageList(@Param("page") Page page, @Param("param") YxStoreProductQueryParam yxStoreProductQueryParam);

    @Select("SELECT dd.value  FROM dict d, dict_detail dd WHERE d.id = dd.dict_id AND d.name = 'productBatchNo' and dd.label = #{label}")
    String queryDefaultBatchNo(@Param("label") String label);

    IPage<Goods> getProduct4ProjectPageList(@Param("page") Page page, @Param("projectCode") String projectCode, @Param("keyword") String keyword, @Param("ids") List<String> ids);


    /**
     * 获取分页对象,普通门店商品查询
     * @param page
     * @param yxStoreProductQueryParam
     * @return
     */
    IPage<YxStoreProductQueryVo> getYxStoreProductPageList4Store(@Param("page") Page page, @Param("param") YxStoreProductQueryParam yxStoreProductQueryParam);

    /**
     * 获取分页对象,项目商品查询
     * @param page
     * @param yxStoreProductQueryParam
     * @return
     */
    IPage<YxStoreProductQueryVo> getYxStoreProductPageList4Project(@Param("page") Page page, @Param("param") YxStoreProductQueryParam yxStoreProductQueryParam);

}
