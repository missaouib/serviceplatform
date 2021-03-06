/**
 * Copyright (C) 2018-2020
 * All rights reserved, Designed By www.yixiang.co
 * 注意：
 * 本软件为www.yixiang.co开发研制，未经购买不得使用
 * 购买后可获得全部源代码（禁止转卖、分享、上传到码云、github等开源平台）
 * 一经发现盗用、分享等行为，将追究法律责任，后果自负
 */
package co.yixiang.modules.user.service;

import co.yixiang.common.service.BaseService;
import co.yixiang.common.web.vo.Paging;
import co.yixiang.modules.order.web.vo.YxStoreOrderQueryVo;
import co.yixiang.modules.security.rest.param.LoginParam;
import co.yixiang.modules.shop.web.vo.YxStoreProductQueryVo;
import co.yixiang.modules.user.entity.YxUser;
import co.yixiang.modules.user.web.dto.PromUserDTO;
import co.yixiang.modules.user.web.param.PromParam;
import co.yixiang.modules.user.web.param.YxUserQueryParam;
import co.yixiang.modules.user.web.vo.YxUserQueryVo;
import co.yixiang.modules.wechat.web.param.BindPhoneParam;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * <p>
 * 用户表 服务类
 * </p>
 *
 * @author hupeng
 * @since 2019-10-16
 */
public interface YxUserService extends BaseService<YxUser> {

    double setLevelPrice(double price, int uid);

    void incMoney(int uid,double price);

    void incIntegral(int uid,double integral);

    YxUserQueryVo getNewYxUserById(Serializable id,String projectCode);
    boolean backOrderBrokerage(YxStoreOrderQueryVo order);

    boolean backOrderBrokerageTwo(YxStoreOrderQueryVo order);

    void setUserSpreadCount(int uid);

    int getSpreadCount(int uid,int type);

    List<PromUserDTO> getUserSpreadGrade(PromParam promParam,int uid);

    boolean setSpread(int spread,int uid);

    void decIntegral(int uid,double integral);

    void incPayCount(int uid);

    void decPrice(int uid,double payPrice);

    YxUser findByName(String name);

    /**
     * 根据ID获取查询对象
     * @param id
     * @return
     */
    YxUserQueryVo getYxUserById(Serializable id);

    /**
     * 获取分页对象
     * @param yxUserQueryParam
     * @return
     */
    Paging<YxUserQueryVo> getYxUserPageList(YxUserQueryParam yxUserQueryParam) throws Exception;

    Object authLogin(String code, String spread, HttpServletRequest request,String wechatName);

    Object wxappAuth(LoginParam loginParam, HttpServletRequest request);

    YxStoreProductQueryVo getVipPriceByProjectNo(String projectCode, String cardNumber, String cardType, Integer uid, YxStoreProductQueryVo product);

    void sendCouponToUser(int uid, String cardNumber,String projectCode);

    YxUser getYxUserByPhone(String phone);

    void updateUidAndPhoneById(Integer id,String phone, int uid);

    YxUser getNewYxUserByPhone(String phone);

    Object binding(BindPhoneParam param,HttpServletRequest request);

    Object wxappAuth2(LoginParam loginParam, HttpServletRequest request);

    Object authLogin2(String code, HttpServletRequest request,String wechatName);
}
