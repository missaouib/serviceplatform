package co.yixiang.modules.shop.mapper;

import co.yixiang.modules.shop.entity.YxSystemStore;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import co.yixiang.modules.shop.entity.YxStoreCart;
import co.yixiang.modules.shop.web.param.YxStoreCartQueryParam;
import co.yixiang.modules.shop.web.vo.YxStoreCartQueryVo;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.util.List;

/**
 * <p>
 * 购物车表 Mapper 接口
 * </p>
 *
 * @author hupeng
 * @since 2019-10-25
 */
@Repository
public interface YxStoreCartMapper extends BaseMapper<YxStoreCart> {


    int cartSum(@Param("uid") int uid,@Param("type") String type,@Param("projectCodes") List<String> projectCodes);

    /**
     * 根据ID获取查询对象
     * @param id
     * @return
     */
    YxStoreCartQueryVo getYxStoreCartById(Serializable id);

    /**
     * 获取分页对象
     * @param page
     * @param yxStoreCartQueryParam
     * @return
     */
    IPage<YxStoreCartQueryVo> getYxStoreCartPageList(@Param("page") Page page, @Param("param") YxStoreCartQueryParam yxStoreCartQueryParam);


    List<YxSystemStore> getStoreInfo(@Param("uid") int uid, @Param("type") String type, @Param("is_new") Integer is_new,@Param("cartIds") List<String> cartIds,@Param("projectCodes") List<String> projectCodes);

    @Delete("DELETE ysc FROM \n" +
            "yx_store_cart ysc \n" +
            "WHERE NOT EXISTS (SELECT 1 FROM yx_store_order yso WHERE FIND_IN_SET(ysc.id,yso.cart_id) )\n" +
            "AND ysc.product_id = #{productid}\n" +
            "AND ysc.uid = #{uid}\n" +
            "AND ysc.product_attr_unique = #{productUnique}")
    Boolean deleteCartByUidProductid(@Param("uid") Integer uid,@Param("productid") Integer productid,@Param("productUnique")  String productUnique);
}
