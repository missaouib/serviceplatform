package co.yixiang.modules.shop.service.dto;

import co.yixiang.modules.activity.domain.YxStoreCouponUser;
import co.yixiang.modules.shop.domain.YxUserAddress;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @ClassName ConfirmOrderDTO
 * @Author hupeng <610796224@qq.com>
 * @Date 2019/10/27
 **/
@Data
public class ConfirmOrderDTO implements Serializable {
    //地址信息
    private YxUserAddress addressInfo;

    //砍价id
    private Integer bargainId = 0;

    private List<YxStoreCartQueryVo> cartInfo;

    private Integer combinationId = 0;

    //优惠券减
    private Boolean deduction = false;

    private Boolean enableIntegral = true;

    private Double enableIntegralNum = 0d;

    //积分抵扣
    private Integer integralRatio = 0;

    private String orderKey;

    private PriceGroupDTO priceGroup;

    private Integer seckillId = 0;

    //店铺自提
    private Integer storeSelfMention = 1;

    //店铺信息
    private YxSystemStoreQueryVo systemStore;


    private YxStoreCouponUser usableCoupon;

    private YxUserQueryVo userInfo;

    private Boolean needImageFlag;



}
