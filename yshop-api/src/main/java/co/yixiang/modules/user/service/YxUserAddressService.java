/**
 * Copyright (C) 2018-2019
 * All rights reserved, Designed By www.yixiang.co
 * 注意：
 * 本软件为www.yixiang.co开发研制，未经购买不得使用
 * 购买后可获得全部源代码（禁止转卖、分享、上传到码云、github等开源平台）
 * 一经发现盗用、分享等行为，将追究法律责任，后果自负
 */
package co.yixiang.modules.user.service;

import co.yixiang.modules.user.entity.YxUserAddress;
import co.yixiang.common.service.BaseService;
import co.yixiang.modules.user.web.param.YxUserAddressQueryParam;
import co.yixiang.modules.user.web.vo.YxUserAddressQueryVo;
import co.yixiang.common.web.vo.Paging;

import java.io.Serializable;

/**
 * <p>
 * 用户地址表 服务类
 * </p>
 *
 * @author hupeng
 * @since 2019-10-28
 */
public interface YxUserAddressService extends BaseService<YxUserAddress> {

    YxUserAddress getUserDefaultAddress(int uid);

    YxUserAddress getUserDefaultAddressType(int uid,Integer type);

    /**
     * 根据ID获取查询对象
     * @param id
     * @return
     */
    YxUserAddressQueryVo getYxUserAddressById(Serializable id);

    /**
     * 获取分页对象
     * @param yxUserAddressQueryParam
     * @return
     */
    Paging<YxUserAddressQueryVo> getYxUserAddressPageList(YxUserAddressQueryParam yxUserAddressQueryParam);

    YxUserAddress getYxUserAddressByInfo(int uid, String addressDetail,String provinceName,String cityName,String districtName,int type,String name,String phone);

}
