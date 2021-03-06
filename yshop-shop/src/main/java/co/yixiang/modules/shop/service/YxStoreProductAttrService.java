/**
 * Copyright (C) 2018-2020
 * All rights reserved, Designed By www.yixiang.co

 */
package co.yixiang.modules.shop.service;

import co.yixiang.common.service.BaseService;
import co.yixiang.modules.shop.domain.YxStoreProductAttr;

/**
* @author hupeng
* @date 2020-05-12
*/
public interface YxStoreProductAttrService  extends BaseService<YxStoreProductAttr>{
    void decProductAttrStock(int num, int productId, String unique);
}
