/**
 * Copyright (C) 2018-2020
 * All rights reserved, Designed By www.yixiang.co

 */
package co.yixiang.modules.shop.service.mapper;

import co.yixiang.common.mapper.CoreMapper;
import co.yixiang.modules.shop.domain.YxSystemStore;
import co.yixiang.modules.shop.service.dto.YxSystemStoreQueryVo;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
* @author hupeng
* @date 2020-05-12
*/
@Repository
@Mapper
public interface SystemStoreMapper extends CoreMapper<YxSystemStore> {

    YxSystemStoreQueryVo getYxSystemStoreById(Integer id);
}
