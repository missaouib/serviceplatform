/**
 * Copyright (C) 2018-2020
 * All rights reserved, Designed By www.yixiang.co

 */
package co.yixiang.modules.activity.service.mapper;

import co.yixiang.common.mapper.CoreMapper;
import co.yixiang.modules.activity.domain.YxStoreCombination;
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
public interface YxStoreCombinationMapper extends CoreMapper<YxStoreCombination> {
    @Update("update yx_store_combination set stock=stock-#{num}, sales=sales+#{num}" +
            " where id=#{combinationId}")
    int decStockIncSales(@Param("num") int num, @Param("combinationId") int combinationId);
}
