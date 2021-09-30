/**
 * Copyright (C) 2018-2020
 * All rights reserved, Designed By www.yixiang.co
 * 注意：
 * 本软件为www.yixiang.co开发研制，未经购买不得使用
 * 购买后可获得全部源代码（禁止转卖、分享、上传到码云、github等开源平台）
 * 一经发现盗用、分享等行为，将追究法律责任，后果自负
 */
package co.yixiang.modules.user.service.impl;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import cn.binarywang.wx.miniapp.bean.WxMaUserInfo;
import cn.binarywang.wx.miniapp.config.impl.WxMaDefaultConfigImpl;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import co.yixiang.common.api.ApiResult;
import co.yixiang.common.service.impl.BaseServiceImpl;
import co.yixiang.common.web.vo.Paging;
import co.yixiang.constant.ShopConstants;
import co.yixiang.enums.*;
import co.yixiang.exception.BadRequestException;
import co.yixiang.exception.ErrorRequestException;
import co.yixiang.modules.order.service.YxStoreOrderService;
import co.yixiang.modules.order.web.vo.YxStoreOrderQueryVo;
import co.yixiang.modules.security.rest.param.LoginParam;
import co.yixiang.modules.security.security.TokenProvider;
import co.yixiang.modules.security.security.vo.JwtUser;
import co.yixiang.modules.security.service.OnlineUserService;
import co.yixiang.modules.shop.entity.RechargeLog;
import co.yixiang.modules.shop.entity.YxStoreCoupon;
import co.yixiang.modules.shop.entity.YxStoreCouponCard;
import co.yixiang.modules.shop.entity.YxStoreCouponUser;
import co.yixiang.modules.shop.service.*;
import co.yixiang.modules.shop.web.vo.YxStoreProductQueryVo;
import co.yixiang.modules.taiping.entity.TaipingCard;
import co.yixiang.modules.taiping.service.TaipingCardService;
import co.yixiang.modules.user.entity.YxUser;
import co.yixiang.modules.user.entity.YxUserBill;
import co.yixiang.modules.user.entity.YxUserLevel;
import co.yixiang.modules.user.entity.YxWechatUser;
import co.yixiang.modules.user.mapper.YxUserMapper;
import co.yixiang.modules.user.service.YxUserBillService;
import co.yixiang.modules.user.service.YxUserLevelService;
import co.yixiang.modules.user.service.YxUserService;
import co.yixiang.modules.user.service.YxWechatUserService;
import co.yixiang.modules.user.web.dto.PromUserDTO;
import co.yixiang.modules.user.web.param.PromParam;
import co.yixiang.modules.user.web.param.YxUserQueryParam;
import co.yixiang.modules.user.web.vo.YxUserQueryVo;
import co.yixiang.modules.user.web.vo.YxWechatUserQueryVo;
import co.yixiang.modules.wechat.web.param.BindPhoneParam;
import co.yixiang.mp.config.WxMpConfiguration;
import co.yixiang.mp.domain.YxWechatUserInfo;
import co.yixiang.mp.service.YxWechatUserInfoService;
import co.yixiang.utils.OrderUtil;
import co.yixiang.utils.RedisUtil;
import co.yixiang.utils.SecurityUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.vdurmont.emoji.EmojiParser;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.result.WxMpOAuth2AccessToken;
import me.chanjar.weixin.mp.bean.result.WxMpUser;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;


/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author hupeng
 * @since 2019-10-16
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class YxUserServiceImpl extends BaseServiceImpl<YxUserMapper, YxUser> implements YxUserService {

    @Autowired
    private YxUserMapper yxUserMapper;

    @Autowired
    private YxStoreOrderService orderService;
    @Autowired
    private YxSystemConfigService systemConfigService;
    @Autowired
    private YxUserBillService billService;
    @Autowired
    private YxUserLevelService userLevelService;
    @Autowired
    private YxStoreCouponUserService storeCouponUserService;
    @Autowired
    private YxSystemStoreStaffService systemStoreStaffService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private OnlineUserService onlineUserService;
    @Autowired
    private TokenProvider tokenProvider;
    @Autowired
    private AuthenticationManagerBuilder authenticationManagerBuilder;
    @Autowired
    private WxMaService wxMaService;

    @Autowired
    private YxWechatUserService wechatUserService;
    @Value("${single.login:true}")
    private Boolean singleLogin;
    @Value("${yshop.notify.sms.enable}")
    private Boolean enableSms;

    @Autowired
    private TaipingCardService taipingCardService;

    @Autowired
    private YxWechatUserInfoService yxWechatUserInfoService;

    @Autowired
    private YxStoreCouponUserService couponUserService;

    @Autowired
    private YxStoreCouponCardService couponCardService;


    @Autowired
    private RechargeLogService rechargeLogService;

    /**
     * 返回会员价
     * @param price
     * @param uid
     * @return
     */
    @Override
    public double setLevelPrice(double price, int uid) {
        QueryWrapper<YxUserLevel> wrapper = new QueryWrapper<>();
        wrapper.eq("is_del",0).eq("status",1)
                .eq("uid",uid).orderByDesc("grade").last("limit 1");
        YxUserLevel userLevel = userLevelService.getOne(wrapper);
        int discount = 100;
        if(ObjectUtil.isNotNull(userLevel)) discount = userLevel.getDiscount();
        return NumberUtil.mul(NumberUtil.div(discount,100),price);
    }



    /**
     * 更新用户余额
     * @param uid
     * @param price
     */
    @Override
    public void incMoney(int uid, double price) {
        yxUserMapper.incMoney(uid,price);
    }

    @Override
    public void incIntegral(int uid, double integral) {
        yxUserMapper.incIntegral(integral,uid);
    }

    /**
     * 一级返佣
     * @param order
     * @return
     */
    @Override
    public boolean backOrderBrokerage(YxStoreOrderQueryVo order) {
        //如果分销没开启直接返回
        String open = systemConfigService.getData("store_brokerage_open");
        if(StrUtil.isEmpty(open) || open.equals("2")) return false;
        //支付金额减掉邮费
        double payPrice = 0d;
        payPrice = NumberUtil.sub(order.getPayPrice(),order.getPayPostage()).doubleValue();

        //获取购买商品的用户
        YxUserQueryVo userInfo = getYxUserById(order.getUid());
        //当前用户不存在 没有上级  直接返回
        if(ObjectUtil.isNull(userInfo) || userInfo.getSpreadUid() == 0) return true;

        //获取后台分销类型  1 指定分销 2 人人分销
        int storeBrokerageStatus = 1;
        if(StrUtil.isNotEmpty(systemConfigService.getData("store_brokerage_statu"))){
            storeBrokerageStatus = Integer.valueOf(systemConfigService
                    .getData("store_brokerage_statu"));
        }

        //指定分销 判断 上级是否时推广员  如果不是推广员直接跳转二级返佣
        YxUserQueryVo preUser = getYxUserById(userInfo.getSpreadUid());
        if(storeBrokerageStatus == 1){

            if(preUser.getIsPromoter() == 0){
                return backOrderBrokerageTwo(order);
            }
        }

        //获取后台一级返佣比例
        String storeBrokerageRatioStr = systemConfigService.getData("store_brokerage_ratio");
        int storeBrokerageRatio = 0;
        if(StrUtil.isNotEmpty(storeBrokerageRatioStr)){
            storeBrokerageRatio = Integer.valueOf(storeBrokerageRatioStr);
        }
        //一级返佣比例 等于零时直接返回 不返佣
        if(storeBrokerageRatio == 0) return true;

        //计算获取一级返佣比例
        double brokerageRatio = NumberUtil.div(storeBrokerageRatio,100);
        //成本价
        double cost = order.getCost().doubleValue();

        //成本价大于等于支付价格时直接返回
        if(cost >= payPrice) return true;

        //获取订单毛利
        payPrice = NumberUtil.sub(payPrice,cost);

        //返佣金额 = 毛利 / 一级返佣比例
        double brokeragePrice = NumberUtil.mul(payPrice,brokerageRatio);

        //返佣金额小于等于0 直接返回不返佣金
        if(brokeragePrice <=0 ) return true;

        //计算上级推广员返佣之后的金额
        double balance = NumberUtil.add(preUser.getBrokeragePrice(),brokeragePrice)
                .doubleValue();
        String mark = userInfo.getNickname()+"成功消费"+order.getPayPrice()+"元,奖励推广佣金"+
                brokeragePrice;
        //插入流水
        YxUserBill userBill = new YxUserBill();
        userBill.setUid(userInfo.getSpreadUid());
        userBill.setTitle("获得推广佣金");
        userBill.setLinkId(order.getId().toString());
        userBill.setCategory("now_money");
        userBill.setType("brokerage");
        userBill.setNumber(BigDecimal.valueOf(brokeragePrice));
        userBill.setBalance(BigDecimal.valueOf(balance));
        userBill.setMark(mark);
        userBill.setStatus(1);
        userBill.setPm(1);
        userBill.setAddTime(OrderUtil.getSecondTimestampTwo());
        billService.save(userBill);

        //添加用户余额
        yxUserMapper.incBrokeragePrice(brokeragePrice,
                userInfo.getSpreadUid());

        //一级返佣成功 跳转二级返佣
        backOrderBrokerageTwo(order);

        return false;
    }

    /**
     * 二级返佣
     * @param order
     * @return
     */
    @Override
    public boolean backOrderBrokerageTwo(YxStoreOrderQueryVo order) {

        double payPrice = 0d;
        payPrice = NumberUtil.sub(order.getPayPrice(),order.getPayPostage()).doubleValue();

        YxUserQueryVo userInfo = getYxUserById(order.getUid());

        //获取上推广人
        YxUserQueryVo userInfoTwo = getYxUserById(userInfo.getSpreadUid());

        //上推广人不存在 或者 上推广人没有上级    直接返回
        if(ObjectUtil.isNull(userInfoTwo) || userInfoTwo.getSpreadUid() == 0) return true;

        //获取后台分销类型  1 指定分销 2 人人分销
        int storeBrokerageStatus = 1;
        if(StrUtil.isNotEmpty(systemConfigService.getData("store_brokerage_statu"))){
            storeBrokerageStatus = Integer.valueOf(systemConfigService
                    .getData("store_brokerage_statu"));
        }
        //指定分销 判断 上上级是否时推广员  如果不是推广员直接返回
        YxUserQueryVo preUser = getYxUserById(userInfoTwo.getSpreadUid());
        if(storeBrokerageStatus == 1){

            if(preUser.getIsPromoter() == 0){
                return true;
            }
        }

        //获取二级返佣比例
        String storeBrokerageTwoStr = systemConfigService.getData("store_brokerage_two");
        int storeBrokerageTwo = 0;
        if(StrUtil.isNotEmpty(storeBrokerageTwoStr)){
            storeBrokerageTwo = Integer.valueOf(storeBrokerageTwoStr);
        }
        //一级返佣比例 等于零时直接返回 不返佣
        if(storeBrokerageTwo == 0) return true;

        //计算获取二级返佣比例
        double brokerageRatio = NumberUtil.div(storeBrokerageTwo,100);
        //成本价
        double cost = order.getCost().doubleValue();

        //成本价大于等于支付价格时直接返回
        if(cost >= payPrice) return true;

        //获取订单毛利
        payPrice = NumberUtil.sub(payPrice,cost);

        //返佣金额 = 毛利 / 二级返佣比例
        double brokeragePrice = NumberUtil.mul(payPrice,brokerageRatio);

        //返佣金额小于等于0 直接返回不返佣金
        if(brokeragePrice <=0 ) return true;

        //获取上上级推广员信息
        double balance = NumberUtil.add(preUser.getBrokeragePrice(),brokeragePrice)
                .doubleValue();
        String mark = "二级推广人"+userInfo.getNickname()+"成功消费"+order.getPayPrice()+"元,奖励推广佣金"+
                brokeragePrice;
        //插入流水
        YxUserBill userBill = new YxUserBill();
        userBill.setUid(userInfoTwo.getSpreadUid());
        userBill.setTitle("获得推广佣金");
        userBill.setLinkId(order.getId().toString());
        userBill.setCategory("now_money");
        userBill.setType("brokerage");
        userBill.setNumber(BigDecimal.valueOf(brokeragePrice));
        userBill.setBalance(BigDecimal.valueOf(balance));
        userBill.setMark(mark);
        userBill.setStatus(1);
        userBill.setPm(1);
        userBill.setAddTime(OrderUtil.getSecondTimestampTwo());
        billService.save(userBill);

        //添加用户余额
        yxUserMapper.incBrokeragePrice(brokeragePrice,
                userInfoTwo.getSpreadUid());


        return false;
    }

    @Override
    public void setUserSpreadCount(int uid) {
        QueryWrapper<YxUser> wrapper = new QueryWrapper<>();
        wrapper.eq("spread_uid",uid);
        int count = yxUserMapper.selectCount(wrapper);

        YxUser user = new YxUser();
        user.setUid(uid);
        user.setSpreadCount(count);

        yxUserMapper.updateById(user);
    }

    @Override
    public int getSpreadCount(int uid, int type) {
        QueryWrapper<YxUser> wrapper = new QueryWrapper<>();
        wrapper.eq("spread_uid",uid);
        int count = 0;
        if(type == 1){
            count = yxUserMapper.selectCount(wrapper);
        }else{
            List<YxUser> userList = yxUserMapper.selectList(wrapper);
            List<Integer> userIds = userList.stream().map(YxUser::getUid)
                    .collect(Collectors.toList());
            if(userIds.isEmpty()) {
                count = 0;
            }else{
                QueryWrapper<YxUser> wrapperT = new QueryWrapper<>();
                wrapperT.in("spread_uid",userIds);

                count = yxUserMapper.selectCount(wrapperT);
            }

        }
        return count;
    }

    @Override
    public List<PromUserDTO> getUserSpreadGrade(PromParam promParam,int uid) {
        QueryWrapper<YxUser> wrapper = new QueryWrapper<>();
        wrapper.eq("spread_uid",uid);
        List<YxUser> userList = yxUserMapper.selectList(wrapper);
        List<Integer> userIds = userList.stream().map(YxUser::getUid)
                .collect(Collectors.toList());
        List<PromUserDTO> list = new ArrayList<>();
        if(userIds.isEmpty()) return list;
        String sort;
        if(StrUtil.isEmpty(promParam.getSort())){
            sort = "u.add_time desc";
        }else{
            sort = promParam.getSort();
        }
        String keyword = null;
        if(StrUtil.isNotEmpty(promParam.getKeyword())){
            keyword = promParam.getKeyword();
        }
        Page<YxUser> pageModel = new Page<>(promParam.getPage(), promParam.getLimit());
        if(promParam.getGrade() == 0){//-级
            list = yxUserMapper.getUserSpreadCountList(pageModel,userIds,
                    keyword,sort);
        }else{//二级
            QueryWrapper<YxUser> wrapperT = new QueryWrapper<>();
            wrapperT.in("spread_uid",userIds);
            List<YxUser> userListT = yxUserMapper.selectList(wrapperT);
            List<Integer> userIdsT = userListT.stream().map(YxUser::getUid)
                    .collect(Collectors.toList());
            if(userIdsT.isEmpty()) return list;
            list = yxUserMapper.getUserSpreadCountList(pageModel,userIdsT,
                    keyword,sort);

        }
        return list;
    }

    /**
     * 设置推广关系
     * @param spread
     * @param uid
     */
    @Override
    public boolean setSpread(int spread, int uid) {
        //如果分销没开启直接返回
        String open = systemConfigService.getData("store_brokerage_open");
        if(StrUtil.isEmpty(open) || open.equals("2")){
            return false;
        }
        //当前用户信息
        YxUserQueryVo userInfo = getYxUserById(uid);
        if(ObjectUtil.isNull(userInfo)) {
            return true;
        }

        //当前用户有上级直接返回
        if(userInfo.getSpreadUid() > 0) {
            return true;
        }
        //没有推广编号直接返回
        if(spread == 0) {
            return true;
        }
        if(spread == uid) {
            return true;
        }

        //不能互相成为上下级
        YxUserQueryVo userInfoT = getYxUserById(spread);
        if(ObjectUtil.isNull(userInfoT)) {
            return true;
        }

        if(userInfoT.getSpreadUid() == uid) {
            return true;
        }

        //1-指定分销 2-人人分销
        int storeBrokerageStatus = Integer.valueOf(systemConfigService
                .getData("store_brokerage_statu"));
        //如果是指定分销，如果 推广人不是分销员不能形成关系
        if(storeBrokerageStatus == 1 && userInfoT.getIsPromoter() == 0){
            return true;
        }
        YxUser yxUser = new YxUser();

        yxUser.setSpreadUid(spread);
        yxUser.setSpreadTime(OrderUtil.getSecondTimestampTwo());
        yxUser.setUid(uid);
        yxUserMapper.updateById(yxUser);

        return true;

    }

    @Override
    public void incPayCount(int uid) {
        yxUserMapper.incPayCount(uid);
    }

    @Override
    public void decPrice(int uid, double payPrice) {
        yxUserMapper.decPrice(payPrice,uid);
    }

    @Override
    public void decIntegral(int uid, double integral) {
        yxUserMapper.decIntegral(integral,uid);
    }

    @Override
    public YxUserQueryVo getYxUserById(Serializable id){
        YxUserQueryVo userQueryVo = yxUserMapper.getYxUserById(id);
        return userQueryVo;
    }

    @Override
    public YxUserQueryVo getNewYxUserById(Serializable id,String projectCode) {
        YxUserQueryVo userQueryVo = yxUserMapper.getYxUserById(id);
        if(userQueryVo == null){
            throw new ErrorRequestException("用户不存在");
        }
       // userQueryVo.setOrderStatusNum(orderService.orderData((int)id));
        userQueryVo.setCouponCount(storeCouponUserService.getUserValidCouponCount((int)id,projectCode));
        //判断分销类型
        String statu = systemConfigService.getData("store_brokerage_statu");
        if(StrUtil.isNotEmpty(statu)){
            userQueryVo.setStatu(Integer.valueOf(statu));
        }else{
            userQueryVo.setStatu(0);
        }
        userQueryVo.setNowMoney(userQueryVo.getNowMoney()==null?BigDecimal.ZERO:userQueryVo.getNowMoney());
        //获取核销权限
        userQueryVo.setCheckStatus(systemStoreStaffService.checkStatus((int)id,0));

        // 判断微信是否已经授权过
        YxWechatUserQueryVo yxWechatUser = wechatUserService.getYxWechatUserById(id);

        if( yxWechatUser != null && StrUtil.isNotBlank(yxWechatUser.getRoutineOpenid()) ) {
            userQueryVo.setWechatMiniAppAuthFlag("Y");
        }
       // userQueryVo.setWechatMiniAppAuthFlag("Y");
        if( yxWechatUser != null && StrUtil.isNotBlank(yxWechatUser.getOpenid()) ) {
            userQueryVo.setWechatAuthFlag("Y");
        }

        return userQueryVo;
    }

    @Override
    public Paging<YxUserQueryVo> getYxUserPageList(YxUserQueryParam yxUserQueryParam) throws Exception{
        Page page = setPageParam(yxUserQueryParam,OrderItem.desc("add_time"));
        IPage<YxUserQueryVo> iPage = yxUserMapper.getYxUserPageList(page,yxUserQueryParam);
        return new Paging(iPage);
    }

    @Override
    public YxUser findByName(String name) {
        QueryWrapper<YxUser> wrapper = new QueryWrapper<>();
        wrapper.eq("username",name);
        return getOne(wrapper);
    }
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Object authLogin(String code, String spread, HttpServletRequest request,String wechatName){
        try {
            WxMpService wxService = WxMpConfiguration.getWxMpService(wechatName);
            WxMpOAuth2AccessToken wxMpOAuth2AccessToken = wxService.oauth2getAccessToken(code);
            WxMpUser wxMpUser = wxService.oauth2getUserInfo(wxMpOAuth2AccessToken, null);
            String openid = wxMpUser.getOpenId();

            //如果开启了UnionId
            if (StrUtil.isNotBlank(wxMpUser.getUnionId())) {
                openid = wxMpUser.getUnionId();
            }
            YxUser yxUser = this.findByName(openid);

            String username = "";
            if(ObjectUtil.isNull(yxUser)){
                //过滤掉表情
                String nickname = EmojiParser.removeAllEmojis(wxMpUser.getNickname());
                log.info("昵称：{}", nickname);
                //用户保存
                YxUser user = new YxUser();
                user.setAccount(nickname);
                //如果开启了UnionId
                if (StrUtil.isNotBlank(wxMpUser.getUnionId())) {
                    username = wxMpUser.getUnionId();
                    user.setUsername(wxMpUser.getUnionId());
                }else{
                    username = wxMpUser.getOpenId();
                    user.setUsername(wxMpUser.getOpenId());
                }
                user.setPassword(passwordEncoder.encode(ShopConstants.YSHOP_DEFAULT_PWD));
                user.setPwd(passwordEncoder.encode(ShopConstants.YSHOP_DEFAULT_PWD));
                user.setPhone("");
                user.setUserType(AppFromEnum.WECHAT.getValue());
                user.setLoginType(AppFromEnum.WECHAT.getValue());
                user.setAddTime(OrderUtil.getSecondTimestampTwo());
                user.setLastTime(OrderUtil.getSecondTimestampTwo());
                user.setNickname(nickname);
                user.setAvatar(wxMpUser.getHeadImgUrl());
                user.setNowMoney(BigDecimal.ZERO);
                user.setBrokeragePrice(BigDecimal.ZERO);
                user.setIntegral(BigDecimal.ZERO);

                this.save(user);


                //保存微信用户
                YxWechatUser yxWechatUser = new YxWechatUser();
                yxWechatUser.setAddTime(OrderUtil.getSecondTimestampTwo());
                yxWechatUser.setNickname(nickname);
                yxWechatUser.setOpenid(wxMpUser.getOpenId());
                int sub = 0;
                if (ObjectUtil.isNotNull(wxMpUser.getSubscribe()) && wxMpUser.getSubscribe()) sub = 1;
                yxWechatUser.setSubscribe(sub);
                yxWechatUser.setSex(wxMpUser.getSex());
                yxWechatUser.setLanguage(wxMpUser.getLanguage());
                yxWechatUser.setCity(wxMpUser.getCity());
                yxWechatUser.setProvince(wxMpUser.getProvince());
                yxWechatUser.setCountry(wxMpUser.getCountry());
                yxWechatUser.setHeadimgurl(wxMpUser.getHeadImgUrl());
                if (ObjectUtil.isNotNull(wxMpUser.getSubscribeTime())) {
                    yxWechatUser.setSubscribeTime(wxMpUser.getSubscribeTime().intValue());
                }
                if (StrUtil.isNotBlank(wxMpUser.getUnionId())) {
                    yxWechatUser.setUnionid(wxMpUser.getUnionId());
                }
                if (StrUtil.isNotEmpty(wxMpUser.getRemark())) {
                    yxWechatUser.setUnionid(wxMpUser.getRemark());
                }
                if (ObjectUtil.isNotEmpty(wxMpUser.getGroupId())) {
                    yxWechatUser.setGroupid(wxMpUser.getGroupId());
                }
                yxWechatUser.setUid(user.getUid());

                wechatUserService.save(yxWechatUser);

            }else{
                username = yxUser.getUsername();
                if(StrUtil.isNotBlank(wxMpUser.getOpenId()) || StrUtil.isNotBlank(wxMpUser.getUnionId())){
                    YxWechatUser wechatUser = new YxWechatUser();
                    wechatUser.setUid(yxUser.getUid());
                    wechatUser.setUnionid(wxMpUser.getUnionId());
                    wechatUser.setOpenid(wxMpUser.getOpenId());

                    wechatUserService.updateById(wechatUser);
                }
            }


            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(username,
                            ShopConstants.YSHOP_DEFAULT_PWD);

            Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            // 生成令牌
            String token = tokenProvider.createToken(authentication);
            final JwtUser jwtUserT = (JwtUser) authentication.getPrincipal();
            // 保存在线信息
            onlineUserService.save(jwtUserT, token, request);

            Date expiresTime = tokenProvider.getExpirationDateFromToken(token);
            String expiresTimeStr = DateUtil.formatDateTime(expiresTime);

            Map<String, String> map = new LinkedHashMap<>();
            map.put("token", token);
            map.put("expires_time", expiresTimeStr);
            map.put("openid", openid);
            if (singleLogin) {
                //踢掉之前已经登录的token
                onlineUserService.checkLoginOnUser(jwtUserT.getUsername(), token);
            }

            //设置推广关系
            if (StrUtil.isNotEmpty(spread) && !spread.equals("NaN")) {
                this.setSpread(Integer.valueOf(spread),
                        jwtUserT.getId().intValue());
            }

            // 返回 token
            return map;
        } catch (WxErrorException e) {
            e.printStackTrace();
            log.error(e.getMessage());
            throw new BadRequestException(e.toString());
        }
    }



    @Override
    @Transactional(rollbackFor = Exception.class)
    public Object authLogin2(String code, HttpServletRequest request,String wechatName){
        int uid = SecurityUtils.getUserId().intValue();
        try {
            WxMpService wxService = WxMpConfiguration.getWxMpService(wechatName);
            WxMpOAuth2AccessToken wxMpOAuth2AccessToken = wxService.oauth2getAccessToken(code);
            WxMpUser wxMpUser = wxService.oauth2getUserInfo(wxMpOAuth2AccessToken, null);
            String openid = wxMpUser.getOpenId();

            LambdaQueryWrapper<YxWechatUser> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(YxWechatUser::getUid,uid);
            YxWechatUser yxWechatUser = wechatUserService.getOne(lambdaQueryWrapper,false);

            if(yxWechatUser == null) {
                yxWechatUser = new YxWechatUser();

            }
            yxWechatUser.setOpenid(wxMpUser.getOpenId());
            //过滤掉表情
            String nickname = EmojiParser.removeAllEmojis(wxMpUser.getNickname());
            //保存微信用户
            yxWechatUser = new YxWechatUser();
            yxWechatUser.setAddTime(OrderUtil.getSecondTimestampTwo());
            yxWechatUser.setNickname(nickname);
            yxWechatUser.setOpenid(openid);
            int sub = 0;
            yxWechatUser.setSubscribe(sub);
            yxWechatUser.setSex(wxMpUser.getSex());
            yxWechatUser.setLanguage(wxMpUser.getLanguage());
            yxWechatUser.setCity(wxMpUser.getCity());
            yxWechatUser.setProvince(wxMpUser.getProvince());
            yxWechatUser.setCountry(wxMpUser.getCountry());
            yxWechatUser.setHeadimgurl(wxMpUser.getHeadImgUrl());
            if (StrUtil.isNotBlank(wxMpUser.getUnionId())) {
                yxWechatUser.setUnionid(wxMpUser.getUnionId());

            }
            yxWechatUser.setUid(uid);
            wechatUserService.saveOrUpdate(yxWechatUser);

            Map<String, String> map = new LinkedHashMap<>();

            map.put("openid", openid);



            // 返回 token
            return map;
        } catch (WxErrorException e) {
            e.printStackTrace();
            log.error(e.getMessage());
            throw new BadRequestException(e.toString());
        }
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public Object wxappAuth(LoginParam loginParam, HttpServletRequest request){
        String code = loginParam.getCode();
        String encryptedData = loginParam.getEncryptedData();
        String iv = loginParam.getIv();
        String spread = loginParam.getSpread();
        try {
            //读取redis配置
            String appId = RedisUtil.get(RedisKeyEnum.WXAPP_APPID.getValue());
            String secret = RedisUtil.get(RedisKeyEnum.WXAPP_SECRET.getValue());
            log.info("miniApp 登录 appid=[{}],secret=[{}]",appId,secret);
            if (StrUtil.isBlank(appId) || StrUtil.isBlank(secret)) {
                throw new ErrorRequestException("请先配置小程序");
            }
            WxMaDefaultConfigImpl wxMaConfig = new WxMaDefaultConfigImpl();
            wxMaConfig.setAppid(appId);
            wxMaConfig.setSecret(secret);

            wxMaService.setWxMaConfig(wxMaConfig);
            WxMaJscode2SessionResult session = wxMaService.getUserService().getSessionInfo(code);
            String openid = session.getOpenid();
            String unionid = session.getUnionid();
            log.info("miniapp auth session.getSessionKey()={},encryptedData={},iv={},session.getUnionid()={},session.getOpenid()={}",session.getSessionKey(),encryptedData,iv,unionid,openid);
            WxMaUserInfo wxMpUser = wxMaService.getUserService()
                    .getUserInfo(session.getSessionKey(), encryptedData, iv);
            log.info("miniapp auth wxMpUser_temp login wxMpUser_temp.getOpenId()={},wxMpUser_temp.getUnionId()={}",wxMpUser.getOpenId(),wxMpUser.getUnionId());
            log.info("wxMpUser={}", JSONUtil.parse(wxMpUser));
            //如果开启了UnionId
      /*      if (StrUtil.isNotBlank(unionid)) {
                log.info("开启了微信公众号和小程序关联");
                openid = unionid;
            }*/
            //如果开启了UnionId
            String username_temp = "";
            if (StrUtil.isNotBlank(unionid)) {
                username_temp = unionid;

            }else{
                username_temp = openid;

            }

            YxUser yxUser = this.findByName(username_temp);
            String username = "";
            if(ObjectUtil.isNull(yxUser)){

              /*  WxMaUserInfo wxMpUser = wxMaService.getUserService()
                        .getUserInfo(session.getSessionKey(), encryptedData, iv);*/
                //过滤掉表情
                String nickname = EmojiParser.removeAllEmojis(wxMpUser.getNickName());
                //用户保存
                YxUser user = new YxUser();
                user.setAccount(nickname);
                log.info(" session.getOpenid()={},openid={}, wxMpUser.getUnionId()={},wxMpUser.getOpenId()={}",session.getOpenid(),openid, wxMpUser.getUnionId(),wxMpUser.getOpenId());
                //如果开启了UnionId
                if (StrUtil.isNotBlank(unionid)) {
                    username = unionid;
                    user.setUsername(unionid);
                }else{
                    username = openid;
                    user.setUsername(openid);
                }
                user.setPassword(passwordEncoder.encode(ShopConstants.YSHOP_DEFAULT_PWD));
                user.setPwd(passwordEncoder.encode(ShopConstants.YSHOP_DEFAULT_PWD));
                user.setPhone("");
                user.setUserType(AppFromEnum.ROUNTINE.getValue());
                user.setAddTime(OrderUtil.getSecondTimestampTwo());
                user.setLastTime(OrderUtil.getSecondTimestampTwo());
                user.setNickname(nickname);
                user.setAvatar(wxMpUser.getAvatarUrl());
                user.setNowMoney(BigDecimal.ZERO);
                user.setBrokeragePrice(BigDecimal.ZERO);
                user.setIntegral(BigDecimal.ZERO);

                this.save(user);


                //保存微信用户
                YxWechatUser yxWechatUser = new YxWechatUser();
                // System.out.println("wxMpUser:"+wxMpUser);
                yxWechatUser.setAddTime(OrderUtil.getSecondTimestampTwo());
                yxWechatUser.setNickname(nickname);
                yxWechatUser.setRoutineOpenid(openid);
                int sub = 0;
                yxWechatUser.setSubscribe(sub);
                yxWechatUser.setSex(Integer.valueOf(wxMpUser.getGender()));
                yxWechatUser.setLanguage(wxMpUser.getLanguage());
                yxWechatUser.setCity(wxMpUser.getCity());
                yxWechatUser.setProvince(wxMpUser.getProvince());
                yxWechatUser.setCountry(wxMpUser.getCountry());
                yxWechatUser.setHeadimgurl(wxMpUser.getAvatarUrl());
                if (StrUtil.isNotBlank(unionid)) {
                    yxWechatUser.setUnionid(unionid);
                    // 获取益药微信公众号的openid
                    YxWechatUserInfo yxWechatUserInfo = yxWechatUserInfoService.getOne(new LambdaQueryWrapper<YxWechatUserInfo>().eq(YxWechatUserInfo::getUnionId,wxMpUser.getUnionId()),false);
                    if(yxWechatUserInfo != null) {
                        yxWechatUser.setOpenid(yxWechatUserInfo.getOpenId());
                    }
                }
                yxWechatUser.setUid(user.getUid());



                wechatUserService.save(yxWechatUser);

            }else{
                username = yxUser.getUsername();
                if(StrUtil.isNotBlank(openid) || StrUtil.isNotBlank(unionid)){
                    YxWechatUser wechatUser = new YxWechatUser();
                    wechatUser.setUid(yxUser.getUid());
                    wechatUser.setUnionid(unionid);
                    wechatUser.setRoutineOpenid(openid);

                    // 获取益药微信公众号的openid
                    YxWechatUserInfo yxWechatUserInfo = yxWechatUserInfoService.getOne(new LambdaQueryWrapper<YxWechatUserInfo>().eq(YxWechatUserInfo::getUnionId,wxMpUser.getUnionId()),false);
                    if(yxWechatUserInfo != null) {
                        wechatUser.setOpenid(yxWechatUserInfo.getOpenId());
                    }

                    wechatUserService.updateById(wechatUser);
                }
            }
            // 优惠券
            if( ProjectNameEnum.TAIPING_LEXIANG.getValue().equals(loginParam.getProjectCode()) && TaipingCardTypeEnum.card_advanced.getValue().equals(loginParam.getCardType()) && StrUtil.isNotBlank(loginParam.getCardNumber())) {

                if (StrUtil.isNotBlank(unionid)) {
                    username_temp = unionid;

                }else{
                    username_temp = openid;

                }

                YxUser yxUser2 = this.findByName(username_temp);

                if(StrUtil.isNotBlank(yxUser2.getTaipingCardNumber())) {
                    List<String> taipingCardList = Arrays.asList(yxUser2.getTaipingCardNumber().split(","));
                    List arrList = new ArrayList(taipingCardList);
                    if(! arrList.contains(loginParam.getCardNumber())) {
                        arrList.add(loginParam.getCardNumber());

                        yxUser2.setTaipingCardNumber(CollUtil.join(arrList,","));
                        updateById(yxUser2);
                    }
                }else {
                    yxUser2.setTaipingCardNumber(loginParam.getCardNumber());
                    updateById(yxUser2);
                }


               // 发放优惠券

                this.sendCouponToUser(yxUser2.getUid(),loginParam.getCardNumber(),loginParam.getProjectCode());


                // 更新卡号对应的uid
                TaipingCard taipingCard = taipingCardService.getTaipingCardByNumber(loginParam.getCardNumber());
                if(taipingCard != null) {
                    taipingCard.setUid(yxUser2.getUid());
                    taipingCardService.updateById(taipingCard);
                }


            }


            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(username,
                            ShopConstants.YSHOP_DEFAULT_PWD);

            Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            // 生成令牌
            String token = tokenProvider.createToken(authentication);
            final JwtUser jwtUserT = (JwtUser) authentication.getPrincipal();
            // 保存在线信息
            onlineUserService.save(jwtUserT, token, request);

            Date expiresTime = tokenProvider.getExpirationDateFromToken(token);
            String expiresTimeStr = DateUtil.formatDateTime(expiresTime);


            Map<String, String> map = new LinkedHashMap<>();
            map.put("token", token);
            map.put("expires_time", expiresTimeStr);
            map.put("sessionKey",session.getSessionKey());

            if (singleLogin) {
                //踢掉之前已经登录的token
                onlineUserService.checkLoginOnUser(jwtUserT.getUsername(), token);
            }

            //设置推广关系
            if (StrUtil.isNotEmpty(spread)) {
                this.setSpread(Integer.valueOf(spread),
                        jwtUserT.getId().intValue());
            }
            // 返回 token
            return map;
        } catch (WxErrorException e) {
            log.error(e.getMessage(), e);
            throw new BadRequestException(e.toString());
        }
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public Object wxappAuth2(LoginParam loginParam, HttpServletRequest request){
        int uid = SecurityUtils.getUserId().intValue();
        String code = loginParam.getCode();
        String encryptedData = loginParam.getEncryptedData();
        String iv = loginParam.getIv();
        String spread = loginParam.getSpread();
        try {
            //读取redis配置
            String appId = RedisUtil.get(RedisKeyEnum.WXAPP_APPID.getValue());
            String secret = RedisUtil.get(RedisKeyEnum.WXAPP_SECRET.getValue());
            log.info("miniApp 登录 appid=[{}],secret=[{}]",appId,secret);
            if (StrUtil.isBlank(appId) || StrUtil.isBlank(secret)) {
                throw new ErrorRequestException("请先配置小程序");
            }
            WxMaDefaultConfigImpl wxMaConfig = new WxMaDefaultConfigImpl();
            wxMaConfig.setAppid(appId);
            wxMaConfig.setSecret(secret);

            wxMaService.setWxMaConfig(wxMaConfig);
            WxMaJscode2SessionResult session = wxMaService.getUserService().getSessionInfo(code);
            String openid = session.getOpenid();
            String unionid = session.getUnionid();
            log.info("微信小程序授权根据code获取openid={},unionid=",openid,unionid);
          //  log.info("miniapp auth session.getSessionKey()={},encryptedData={},iv={},session.getUnionid()={},session.getOpenid()={}",session.getSessionKey(),encryptedData,iv,unionid,openid);
           /* WxMaUserInfo wxMpUser = wxMaService.getUserService()
                    .getUserInfo(session.getSessionKey(), encryptedData, iv);
            log.info("miniapp auth wxMpUser_temp login wxMpUser_temp.getOpenId()={},wxMpUser_temp.getUnionId()={}",wxMpUser.getOpenId(),wxMpUser.getUnionId());
            log.info("wxMpUser={}", JSONUtil.parse(wxMpUser));*/
            //如果开启了UnionId
      /*      if (StrUtil.isNotBlank(unionid)) {
                log.info("开启了微信公众号和小程序关联");
                openid = unionid;
            }*/
            //如果开启了UnionId
            String username_temp = "";
            if (StrUtil.isNotBlank(unionid)) {
                username_temp = unionid;

            }else{
                username_temp = openid;

            }

            LambdaQueryWrapper<YxWechatUser> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(YxWechatUser::getUid,uid);
            YxWechatUser yxWechatUser = wechatUserService.getOne(lambdaQueryWrapper,false);

            if(yxWechatUser == null) {
                yxWechatUser = new YxWechatUser();

            }

            //过滤掉表情
           // String nickname = EmojiParser.removeAllEmojis(wxMpUser.getNickName());
            String nickname = "";
            //保存微信用户
            yxWechatUser = new YxWechatUser();
            yxWechatUser.setAddTime(OrderUtil.getSecondTimestampTwo());
            yxWechatUser.setNickname(nickname);
            yxWechatUser.setRoutineOpenid(openid);
            int sub = 0;
            yxWechatUser.setSubscribe(sub);
           /* yxWechatUser.setSex(Integer.valueOf(wxMpUser.getGender()));
            yxWechatUser.setLanguage(wxMpUser.getLanguage());
            yxWechatUser.setCity(wxMpUser.getCity());
            yxWechatUser.setProvince(wxMpUser.getProvince());
            yxWechatUser.setCountry(wxMpUser.getCountry());
            yxWechatUser.setHeadimgurl(wxMpUser.getAvatarUrl());*/
            if (StrUtil.isNotBlank(unionid)) {
                yxWechatUser.setUnionid(unionid);
                // 获取益药微信公众号的openid
                YxWechatUserInfo yxWechatUserInfo = yxWechatUserInfoService.getOne(new LambdaQueryWrapper<YxWechatUserInfo>().eq(YxWechatUserInfo::getUnionId,unionid),false);
                if(yxWechatUserInfo != null) {
                    yxWechatUser.setOpenid(yxWechatUserInfo.getOpenId());
                }
            }
            yxWechatUser.setUid(uid);
            wechatUserService.saveOrUpdate(yxWechatUser);

            // 优惠券
            if( ProjectNameEnum.TAIPING_LEXIANG.getValue().equals(loginParam.getProjectCode()) && TaipingCardTypeEnum.card_advanced.getValue().equals(loginParam.getCardType()) && StrUtil.isNotBlank(loginParam.getCardNumber())) {

                if (StrUtil.isNotBlank(unionid)) {
                    username_temp = unionid;

                }else{
                    username_temp = openid;

                }

                YxUser yxUser2 = this.findByName(username_temp);

                if(StrUtil.isNotBlank(yxUser2.getTaipingCardNumber())) {
                    List<String> taipingCardList = Arrays.asList(yxUser2.getTaipingCardNumber().split(","));
                    List arrList = new ArrayList(taipingCardList);
                    if(! arrList.contains(loginParam.getCardNumber())) {
                        arrList.add(loginParam.getCardNumber());

                        yxUser2.setTaipingCardNumber(CollUtil.join(arrList,","));
                        updateById(yxUser2);
                    }
                }else {
                    yxUser2.setTaipingCardNumber(loginParam.getCardNumber());
                    updateById(yxUser2);
                }


                // 发放优惠券

                this.sendCouponToUser(yxUser2.getUid(),loginParam.getCardNumber(),loginParam.getProjectCode());


                // 更新卡号对应的uid
                TaipingCard taipingCard = taipingCardService.getTaipingCardByNumber(loginParam.getCardNumber());
                if(taipingCard != null) {
                    taipingCard.setUid(yxUser2.getUid());
                    taipingCardService.updateById(taipingCard);
                }


            }




            Map<String, String> map = new LinkedHashMap<>();

            map.put("openId",openid);



            return map;
        } catch (WxErrorException e) {
            log.error(e.getMessage(), e);
            throw new BadRequestException(e.toString());
        }
    }

    /*
    * 针对项目的会员价
    * */
    @Override
    public YxStoreProductQueryVo getVipPriceByProjectNo(String projectCode,  String cardNumber, String cardType, Integer uid, YxStoreProductQueryVo yxStoreProductQueryVo) {
        BigDecimal vipPrice = yxStoreProductQueryVo.getPrice();
        String userLevel = "";
        BigDecimal discountRate = new BigDecimal(1);
        List<String> benefitsDescList = new ArrayList<>();
        if(ProjectNameEnum.TAIPING_LEXIANG.getValue().equals(projectCode)  ) {
            // 如果是太平项目并且传了卡类型的，根据卡类型对应的优惠比例计算商品价格
            if(TaipingCardTypeEnum.card_base.getValue().equals(cardType) &&  "Y".equals(yxStoreProductQueryVo.getLabel1())) {  //  家庭药房基础版 -- 85折
                vipPrice = NumberUtil.mul(NumberUtil.div(85,100),yxStoreProductQueryVo.getPrice());
                userLevel = TaipingCardTypeEnum.card_base.getDesc();
                String benefitsDesc = "您是"+ TaipingCardTypeEnum.card_base.getDesc() + ",商品享有85折优惠";
                benefitsDescList.add(benefitsDesc);
                discountRate = new BigDecimal(0.85).setScale(2,BigDecimal.ROUND_HALF_UP);
            } else if(TaipingCardTypeEnum.card_chronic.getValue().equals(cardType)  &&  "Y".equals(yxStoreProductQueryVo.getLabel2())) { // 家庭药房慢病版 -- 88折
                vipPrice = NumberUtil.mul(NumberUtil.div(88,100),yxStoreProductQueryVo.getPrice());
                userLevel = TaipingCardTypeEnum.card_chronic.getDesc();
                String benefitsDesc = "您是"+ TaipingCardTypeEnum.card_chronic.getDesc() + ",商品享有88折优惠";
                benefitsDescList.add(benefitsDesc);
                discountRate = new BigDecimal(0.88).setScale(2,BigDecimal.ROUND_HALF_UP);
            } else if(TaipingCardTypeEnum.card_advanced.getValue().equals(cardType) && StrUtil.isNotBlank(cardNumber)   ) {  // 家庭药房升级版用户可以85折优惠买指定商品
                TaipingCard taipingCard = taipingCardService.getTaipingCardByNumber(cardNumber);
                if(taipingCard != null ) {
                    if(  "Y".equals(yxStoreProductQueryVo.getLabel1())) {
                        vipPrice = NumberUtil.mul(NumberUtil.div(85,100),yxStoreProductQueryVo.getPrice());
                        userLevel = TaipingCardTypeEnum.card_advanced.getDesc();
                        String benefitsDesc = "您是"+ TaipingCardTypeEnum.card_advanced.getDesc() + ",商品享有85折优惠";
                        benefitsDescList.add(benefitsDesc);
                        discountRate = new BigDecimal(0.85).setScale(2,BigDecimal.ROUND_HALF_UP);
                    }

                    // 查询用于本月使用优惠券次数
                   /* QueryWrapper queryWrapper = new QueryWrapper();
                    queryWrapper.eq("uid",uid);
                    // 月初的时间戳
                    int startMonthTimestamp = OrderUtil.dateToTimestampT(DateUtil.beginOfMonth(DateUtil.date()));
                    // 月末的时间戳
                    int endMonthTimestamp = OrderUtil.dateToTimestampT(DateUtil.endOfMonth(DateUtil.date()));

                    queryWrapper.between("use_time",startMonthTimestamp,endMonthTimestamp);
                    queryWrapper.eq("status",1);

                    int useTimes = couponUserService.count(queryWrapper);
                    if(useTimes < 2) {
                        // 全场通用券5折券
                        QueryWrapper<YxStoreCouponUser> wrapper0 = new QueryWrapper<>();
                        wrapper0.eq("is_fail",0).eq("status",0).eq("uid",uid).eq("coupon_type",1).last("limit 1") ;
                        YxStoreCouponUser yxStoreCouponUser0 = couponUserService.getOne(wrapper0,false);
                        if(yxStoreCouponUser0 != null) {
                            String benefitsDesc = "您是"+ TaipingCardTypeEnum.card_advanced.getDesc() + ",可使用"+ yxStoreCouponUser0.getCouponTitle();
                            benefitsDescList.add(benefitsDesc);
                        }
                        if("Y".equals(yxStoreProductQueryVo.getLabel3())) {
                            // 是5折类商品
                            QueryWrapper<YxStoreCouponUser> wrapper1= new QueryWrapper<>();
                            wrapper1.eq("is_fail",0).eq("status",0).eq("uid",uid).eq("coupon_type",0).last("limit 1") ;
                            YxStoreCouponUser yxStoreCouponUser1  = couponUserService.getOne(wrapper1,false);
                            if(yxStoreCouponUser1 != null) {
                                String benefitsDesc = "您是"+ TaipingCardTypeEnum.card_advanced.getDesc() + ",可使用"+ yxStoreCouponUser0.getCouponTitle();
                                benefitsDescList.add(benefitsDesc);
                            }

                        }

                    }*/


                    if("Y".equals(yxStoreProductQueryVo.getLabel3())) {

                        // 全场通用券5折券
                        QueryWrapper<YxStoreCouponUser> wrapper0 = new QueryWrapper<>();
                        wrapper0.eq("is_fail",0).eq("status",0).eq("uid",uid).eq("coupon_type",1).last("limit 1") ;
                        YxStoreCouponUser yxStoreCouponUser0 = couponUserService.getOne(wrapper0,false);
                        if(yxStoreCouponUser0 != null) {
                            String benefitsDesc = "您是"+ TaipingCardTypeEnum.card_advanced.getDesc() + ",可使用"+ yxStoreCouponUser0.getCouponTitle();
                            benefitsDescList.add(benefitsDesc);
                        }

                        // 是5折类商品
                        QueryWrapper<YxStoreCouponUser> wrapper1= new QueryWrapper<>();
                        wrapper1.eq("is_fail",0).eq("status",0).eq("uid",uid).eq("coupon_type",0).last("limit 1") ;
                        YxStoreCouponUser yxStoreCouponUser1  = couponUserService.getOne(wrapper1,false);
                        if(yxStoreCouponUser1 != null) {
                            String benefitsDesc = "您是"+ TaipingCardTypeEnum.card_advanced.getDesc() + ",可使用"+ yxStoreCouponUser1.getCouponTitle();
                            benefitsDescList.add(benefitsDesc);
                        }

                    }

                }
            } else {
                vipPrice = yxStoreProductQueryVo.getPrice();
            }
        } else if(ProjectNameEnum.ZHONGANMANBING.getValue().equals(projectCode)) {
            vipPrice = NumberUtil.mul(NumberUtil.div(80,100),yxStoreProductQueryVo.getPrice());
            userLevel = "慢无忧会员";
            String benefitsDesc = "您是众安“慢无忧”会员,商品享有8折优惠";
            benefitsDescList.add(benefitsDesc);
            discountRate = new BigDecimal(0.88).setScale(2,BigDecimal.ROUND_HALF_UP);
        } else if(ProjectNameEnum.LINGYUANZHI.getValue().equals(projectCode)) {
            vipPrice = new BigDecimal(0);
            userLevel = "0元治会员";
            String benefitsDesc = "您是众安“0元治”会员,商品享有0元领取";
            benefitsDescList.add(benefitsDesc);
            discountRate = new BigDecimal(0).setScale(2,BigDecimal.ROUND_HALF_UP);
        }
        else {
            QueryWrapper<YxUserLevel> wrapper = new QueryWrapper<>();
            wrapper.eq("is_del",0).eq("status",1)
                    .eq("uid",uid).orderByDesc("grade").last("limit 1");
            YxUserLevel yxuserLevel = userLevelService.getOne(wrapper);
            int discount = 100;
            if(ObjectUtil.isNotNull(yxuserLevel)) discount = yxuserLevel.getDiscount();
            vipPrice = NumberUtil.mul(NumberUtil.div(discount,100),yxStoreProductQueryVo.getPrice());
        }

        vipPrice = vipPrice.setScale(2,BigDecimal.ROUND_HALF_UP);
        yxStoreProductQueryVo.setVipPrice(vipPrice);
        yxStoreProductQueryVo.setUserLevel(userLevel);
        yxStoreProductQueryVo.setBenefitsDesc(benefitsDescList);
        yxStoreProductQueryVo.setDiscount(discountRate);
        return yxStoreProductQueryVo;
    }

    @Override
    public void sendCouponToUser(int uid, String cardNumber,String projectCode) {
        // 更新卡号对应的uid
        TaipingCard taipingCard = taipingCardService.getTaipingCardByNumber(cardNumber);
        if(taipingCard != null && ObjectUtil.isNull(taipingCard.getUid())) {
            taipingCard.setUid(uid);
            taipingCardService.updateById(taipingCard);
        }

        // 更新用户表上的太平card
        LambdaUpdateWrapper<YxUser> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(YxUser::getTaipingCardNumber,cardNumber);
        updateWrapper.set(YxUser::getUid,uid);
        this.update(updateWrapper);

        // 判断此用户这个月是否有优惠券
        QueryWrapper queryWrapperCouponUser = new QueryWrapper<YxStoreCouponUser>();
        queryWrapperCouponUser.eq("uid",uid);
        queryWrapperCouponUser.le("add_time",OrderUtil.dateToTimestamp( DateUtil.beginOfDay(new Date())));
        queryWrapperCouponUser.ge("end_time",OrderUtil.dateToTimestamp( DateUtil.beginOfDay(new Date())));
        queryWrapperCouponUser.eq("project_code",projectCode);
        int couponCount = couponUserService.count(queryWrapperCouponUser);
        if(couponCount == 0) {
            QueryWrapper queryWrapper = new QueryWrapper();
            queryWrapper.eq("card_number",cardNumber);
            queryWrapper.le("add_time",OrderUtil.dateToTimestamp( DateUtil.beginOfDay(new Date())));
          //  queryWrapper.ge("end_time",OrderUtil.dateToTimestamp( DateUtil.beginOfDay(new Date())));

            List<YxStoreCouponCard> couponCardList = couponCardService.list(queryWrapper);
            for(YxStoreCouponCard couponCard:couponCardList) {
                QueryWrapper queryWrapper1 = new QueryWrapper();
                queryWrapper1.eq("coupon_card_id",couponCard.getId());
                queryWrapper1.eq("uid",uid);

                int couponUserCount = couponUserService.count(queryWrapper1);
                if(couponUserCount == 0) {
                    YxStoreCouponUser couponUser = new YxStoreCouponUser();
                    BeanUtils.copyProperties(couponCard,couponUser);
                    couponUser.setCouponCardId(couponCard.getId());
                    couponUser.setUid(uid);
                    couponUser.setId(null);
                    couponUser.setProjectCode(projectCode);
                    if(couponCard.getAddTime()!=null) {
                        String time_str2 = OrderUtil.stampToDate(couponCard.getAddTime().toString());
                        Date date = DateUtil.parse(time_str2);
                        couponUser.setCouponEffectiveTime(date);
                    }
                    if(couponCard.getEndTime()!=null) {
                        String time_str2 = OrderUtil.stampToDate(couponCard.getEndTime().toString());
                        Date date = DateUtil.parse(time_str2);
                        couponUser.setCouponExpiryTime(date);
                    }
                    //couponUser.setCouponEffectiveTime(OrderUtil.stampToDate());
                    couponUserService.save(couponUser);
                }
            }
        }

    }


    @Override
    public YxUser getYxUserByPhone(String phone) {

        YxUser user = this.getOne(new QueryWrapper<YxUser>().eq("phone",phone),false);
        if(user == null) {
            //用户保存
            user = new YxUser();
            user.setAccount(phone);
            user.setUsername(phone);
            user.setPassword(passwordEncoder.encode(ShopConstants.YSHOP_DEFAULT_PWD));
            user.setPwd(passwordEncoder.encode(ShopConstants.YSHOP_DEFAULT_PWD));
            user.setPhone(phone);
            user.setUserType(AppFromEnum.PATNER.getValue());
            user.setLoginType(AppFromEnum.PATNER.getValue());
            user.setAddTime(OrderUtil.getSecondTimestampTwo());
            user.setLastTime(OrderUtil.getSecondTimestampTwo());
            user.setNickname(phone);
            user.setAvatar("");
            user.setNowMoney(BigDecimal.ZERO);
            user.setBrokeragePrice(BigDecimal.ZERO);
            user.setIntegral(BigDecimal.ZERO);
            save(user);
        }
        return user;
    }

    @Override
    public void updateUidAndPhoneById(Integer id,String phone, int uid) {
        yxUserMapper.updateUidAndPhoneById(id,phone,uid);
    }

    @Override
    public YxUser getNewYxUserByPhone(String phone) {
        return this.getOne(new QueryWrapper<YxUser>().eq("phone",phone),false);
    }



    @Override
    public Object binding(BindPhoneParam param, HttpServletRequest request) {
        int uid = 0;
        YxUser yxUserExists = null;
        /*if( PhoneBindSourceEnum.SOOURCE_1.getValue().equals(param.getBindSource())) {
            // 微信小程序绑定
            uid = SecurityUtils.getUserId().intValue();
            yxUserExists= this.getById(uid);
        } else if (PhoneBindSourceEnum.SOOURCE_2.getValue().equals(param.getBindSource())) {*/
            // H5绑定
            yxUserExists = this.getNewYxUserByPhone(param.getPhone());

            if(yxUserExists == null) {
                yxUserExists = new YxUser();
                yxUserExists.setAccount(param.getPhone());
                yxUserExists.setUsername(param.getPhone());
                yxUserExists.setPassword(passwordEncoder.encode(ShopConstants.YSHOP_DEFAULT_PWD));
                yxUserExists.setPwd(passwordEncoder.encode(ShopConstants.YSHOP_DEFAULT_PWD));
                yxUserExists.setPhone(param.getPhone());
                yxUserExists.setUserType(AppFromEnum.H5.getValue());
                yxUserExists.setAddTime(OrderUtil.getSecondTimestampTwo());
                yxUserExists.setLastTime(OrderUtil.getSecondTimestampTwo());
                yxUserExists.setNickname(param.getPhone());
                yxUserExists.setAvatar(ShopConstants.YSHOP_DEFAULT_AVATAR);
                yxUserExists.setNowMoney(BigDecimal.ZERO);
                yxUserExists.setBrokeragePrice(BigDecimal.ZERO);
                yxUserExists.setIntegral(BigDecimal.ZERO);

                // 是否在储值白名单内，如果是，更新余额,真实姓名，会员标志，身份证号码
                RechargeLog rechargeLog = rechargeLogService.getOne(new LambdaQueryWrapper<RechargeLog>().eq(RechargeLog::getPhone,param.getPhone()),false);
                if(rechargeLog!= null && rechargeLog.getMoney() != null) {
                    yxUserExists.setNowMoney(new BigDecimal(rechargeLog.getMoney()));
                    yxUserExists.setRealName(rechargeLog.getName());
                    yxUserExists.setCardId(rechargeLog.getCardId());
                    yxUserExists.setVipFlag(1);
                }
                this.save(yxUserExists);
            }
            uid = yxUserExists.getUid();
       // }

        // 微信小程序
        YxUser byPhone= this.getNewYxUserByPhone(param.getPhone());
        // 判断是否已有此手机号绑定
        if(byPhone==null){
            // 没有，则本用户绑定
            YxUser yxUser = new YxUser();
            yxUser.setPhone(param.getPhone());
            yxUser.setUid(uid);
            this.updateById(yxUser);
        }else{
            // 有，将原用户的id保留
            Integer id=byPhone.getUid();
            if(! id.equals(uid)) {
                // 删除原用户数据
                this.removeById(id);
                wechatUserService.removeById(id);
                // 将本用户的uid 更新为 原绑定用户的uid
                this.updateUidAndPhoneById(id,param.getPhone(),uid);
                wechatUserService.updateUidById(id,uid);
            }


        }
        // 生成token
                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(yxUserExists.getUsername(),
                                ShopConstants.YSHOP_DEFAULT_PWD);

                Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
                SecurityContextHolder.getContext().setAuthentication(authentication);
                // 生成令牌
                String token = tokenProvider.createToken(authentication);
                final JwtUser jwtUserT = (JwtUser) authentication.getPrincipal();
                // 保存在线信息
                onlineUserService.save(jwtUserT, token, request);

                Date expiresTime = tokenProvider.getExpirationDateFromToken(token);
                String expiresTimeStr = DateUtil.formatDateTime(expiresTime);


                Map<String, String> map = new LinkedHashMap<>();
                map.put("token", token);
                map.put("expires_time", expiresTimeStr);
        map.put("uid",String.valueOf(uid));
                return  map;
    }
}
