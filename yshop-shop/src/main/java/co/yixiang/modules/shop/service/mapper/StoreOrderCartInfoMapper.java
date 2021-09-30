/**
 * Copyright (C) 2018-2020
 * All rights reserved, Designed By www.yixiang.co

 */
package co.yixiang.modules.shop.service.mapper;

import co.yixiang.common.mapper.CoreMapper;
import co.yixiang.modules.shop.domain.YxStoreOrderCartInfo;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
* @author hupeng
* @date 2020-05-12
*/
@Repository
@Mapper
public interface StoreOrderCartInfoMapper extends CoreMapper<YxStoreOrderCartInfo> {

    @Delete("delete from yx_store_order_cart_info where oid = #{id}")
    void deleteByOrderId(Integer id);
}
