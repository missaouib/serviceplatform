/**
* Copyright (C) 2018-2020
* All rights reserved, Designed By www.yixiang.co
* 注意：
* 本软件为www.yixiang.co开发研制，未经购买不得使用
* 购买后可获得全部源代码（禁止转卖、分享、上传到码云、github等开源平台）
* 一经发现盗用、分享等行为，将追究法律责任，后果自负
*/
package co.yixiang.modules.shop.service.mapper;

import co.yixiang.common.mapper.CoreMapper;
import co.yixiang.modules.shop.domain.YxStoreProductAttrValue;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
* @author visa
* @date 2020-05-29
*/
@Repository
@Mapper
public interface YxStoreProductAttrValueMapper extends CoreMapper<YxStoreProductAttrValue> {

    @Update("update yx_store_product_attr_value set stock=stock-#{num}, sales=sales+#{num}" +
            " where product_id=#{productId} and `unique`=#{unique}")
    int decStockIncSales(@Param("num") int num, @Param("productId") int productId,
                         @Param("unique")  String unique);

    @Select("SELECT  DISTINCT yspav.yiyaobao_sku  FROM yx_store_product_attr_value yspav  " +
            " WHERE yspav.`suk` != #{name} AND yspav.is_del = 0;")
    List<String> findNotGuangZhou(@Param("name")String name);
}
