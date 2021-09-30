package co.yixiang.modules.shop.service;

import co.yixiang.modules.shop.entity.YxStoreCouponCard;
import co.yixiang.common.service.BaseService;
import co.yixiang.modules.shop.web.param.YxStoreCouponCardQueryParam;
import co.yixiang.modules.shop.web.vo.YxStoreCouponCardQueryVo;
import co.yixiang.common.web.vo.Paging;

import java.io.Serializable;

/**
 * <p>
 * 优惠券发放记录表 服务类
 * </p>
 *
 * @author visa
 * @since 2020-12-10
 */
public interface YxStoreCouponCardService extends BaseService<YxStoreCouponCard> {

    /**
     * 根据ID获取查询对象
     * @param id
     * @return
     */
    YxStoreCouponCardQueryVo getYxStoreCouponCardById(Serializable id) throws Exception;

    /**
     * 获取分页对象
     * @param yxStoreCouponCardQueryParam
     * @return
     */
    Paging<YxStoreCouponCardQueryVo> getYxStoreCouponCardPageList(YxStoreCouponCardQueryParam yxStoreCouponCardQueryParam) throws Exception;

}
