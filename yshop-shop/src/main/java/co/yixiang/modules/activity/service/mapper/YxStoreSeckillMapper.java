/**
 * Copyright (C) 2018-2020
 * All rights reserved, Designed By www.yixiang.co

 */
package co.yixiang.modules.activity.service.mapper;

import co.yixiang.common.mapper.CoreMapper;
import co.yixiang.modules.activity.domain.YxStoreSeckill;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

/**
* @author hupeng
* @date 2020-05-13
*/
@Repository
@Mapper
public interface YxStoreSeckillMapper extends CoreMapper<YxStoreSeckill> {

    @Update("update yx_store_seckill set stock=stock-#{num}, sales=sales+#{num}" +
            " where id=#{seckillId}")
    int decStockIncSales(@Param("num") int num, @Param("seckillId") int seckillId);
}
