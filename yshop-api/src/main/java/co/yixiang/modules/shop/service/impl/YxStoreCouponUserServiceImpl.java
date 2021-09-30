/**
 * Copyright (C) 2018-2019
 * All rights reserved, Designed By www.yixiang.co
 * 注意：
 * 本软件为www.yixiang.co开发研制，未经购买不得使用
 * 购买后可获得全部源代码（禁止转卖、分享、上传到码云、github等开源平台）
 * 一经发现盗用、分享等行为，将追究法律责任，后果自负
 */
package co.yixiang.modules.shop.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import co.yixiang.enums.OrderSourceEnum;
import co.yixiang.enums.ProjectNameEnum;
import co.yixiang.enums.TaipingCardTypeEnum;
import co.yixiang.exception.ErrorRequestException;
import co.yixiang.modules.order.service.YxStoreOrderService;
import co.yixiang.modules.order.web.dto.CacheDTO;
import co.yixiang.modules.shop.entity.YxStoreCouponUser;
import co.yixiang.modules.shop.mapper.YxStoreCouponUserMapper;
import co.yixiang.modules.shop.mapping.CouponMap;
import co.yixiang.modules.shop.service.YxStoreCouponService;
import co.yixiang.modules.shop.service.YxStoreCouponUserService;
import co.yixiang.modules.shop.web.param.YxStoreCouponUserQueryParam;
import co.yixiang.modules.shop.web.vo.YxStoreCartQueryVo;
import co.yixiang.modules.shop.web.vo.YxStoreCouponQueryVo;
import co.yixiang.modules.shop.web.vo.YxStoreCouponUserQueryVo;
import co.yixiang.common.service.impl.BaseServiceImpl;
import co.yixiang.common.web.vo.Paging;
import co.yixiang.modules.user.entity.YxUser;
import co.yixiang.modules.user.service.YxUserService;
import co.yixiang.modules.zhongan.ZhongAnParamDto;
import co.yixiang.modules.zhongan.ZhongAnRequestUtil;
import co.yixiang.utils.DateUtils;
import co.yixiang.utils.OrderUtil;
import co.yixiang.utils.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * <p>
 * 优惠券发放记录表 服务实现类
 * </p>
 *
 * @author hupeng
 * @since 2019-10-27
 */
@Slf4j
@Service
// @AllArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class YxStoreCouponUserServiceImpl extends BaseServiceImpl<YxStoreCouponUserMapper, YxStoreCouponUser> implements YxStoreCouponUserService {
    @Autowired
    private YxStoreCouponUserMapper yxStoreCouponUserMapper;

    @Autowired
    @Lazy
    private  YxStoreCouponService storeCouponService;

    @Autowired
    private  CouponMap couponMap;


    @Autowired
    private YxUserService userService;

    @Autowired
    @Lazy
    private YxStoreOrderService yxStoreOrderService;

    @Value("${zhonganpuyao.appKey}")
    private String appKey;

    @Value("${zhonganpuyao.url}")
    private String url;

    @Value("${zhonganpuyao.version}")
    private String version;

    @Value("${zhonganpuyao.privateKey}")
    private String privateKey;

    @Override
    public int getUserValidCouponCount(int uid,String projectCode) {
        checkInvalidCoupon(uid);
        QueryWrapper<YxStoreCouponUser> wrapper= new QueryWrapper<>();
        wrapper.eq("status",0)
                .eq("project_code", StringUtils.isEmpty(projectCode)?"":projectCode)
                .eq("uid",uid)
                .ne("coupon_detail_type",3);
        return yxStoreCouponUserMapper.selectCount(wrapper);
    }

    @Override
    public List<YxStoreCouponUser> beUsableCouponList(int uid, double price) {
        QueryWrapper<YxStoreCouponUser> wrapper= new QueryWrapper<>();
        wrapper.eq("is_fail",0).eq("status",0).le("use_min_price",price).eq("uid",uid).gt("coupon_price",0);
        return yxStoreCouponUserMapper.selectList(wrapper);
    }

    /**
     * 获取可用优惠券
     * @param uid
     * @param price
     * @return
     */
    @Override
    public YxStoreCouponUser beUsableCoupon(int uid, double price) {
        QueryWrapper<YxStoreCouponUser> wrapper= new QueryWrapper<>();
        wrapper.eq("is_fail",0).eq("status",0).eq("uid",uid)
                .le("use_min_price",price).last("limit 1") ;
        return getOne(wrapper);
    }

    @Override
    public YxStoreCouponUser getCoupon(int id, int uid) {
        QueryWrapper<YxStoreCouponUser> wrapper= new QueryWrapper<>();
        wrapper.eq("is_fail",0).eq("status",0).eq("uid",uid)
                .eq("id",id) ;
        return getOne(wrapper);
    }

    @Override
    public void useCoupon(int id) {
        YxStoreCouponUser couponUser = new YxStoreCouponUser();
        couponUser.setId(id);
        couponUser.setStatus(1);
        couponUser.setUseTime(OrderUtil.getSecondTimestampTwo());
        yxStoreCouponUserMapper.updateById(couponUser);
    }

    @Override
    public void useCoupon(int id,double factDeductionAmount,Integer status) {
        YxStoreCouponUser couponUser = new YxStoreCouponUser();
        couponUser.setId(id);
        couponUser.setStatus(status);
        couponUser.setUseTime(OrderUtil.getSecondTimestampTwo());
        couponUser.setFactDeductionAmount((factDeductionAmount==0)?null:new BigDecimal(factDeductionAmount));
        yxStoreCouponUserMapper.updateById(couponUser);
    }

    /**
     * 检查优惠券状态
     * @param uid
     */
    @Override
    public void checkInvalidCoupon(int uid) {
        int nowTime = OrderUtil.getSecondTimestampTwo();
        QueryWrapper<YxStoreCouponUser> wrapper= new QueryWrapper<>();
        wrapper.lt("end_time",nowTime).eq("status",0);
        YxStoreCouponUser couponUser = new YxStoreCouponUser();
        couponUser.setStatus(2);
        yxStoreCouponUserMapper.update(couponUser,wrapper);

    }

    /**
     * 获取用户优惠券
     * @param uid uid
     * @param type type
     * @return
     */
    @Override
    public List<YxStoreCouponUserQueryVo> getUserCoupon(int uid, int type,String projectCode,String cardNumber) {

        checkInvalidCoupon(uid);
        QueryWrapper<YxStoreCouponUser> wrapper= new QueryWrapper<>();
        wrapper.eq("uid",uid);//默认获取所有
        if(ProjectNameEnum.ZHONGANMANBING.getValue().equals(projectCode) || ProjectNameEnum.ZHONGANPUYAO.getValue().equals(projectCode)){
//            ZhongAnParamDto param=new ZhongAnParamDto();
//            param.setProjectCode(projectCode);
//            param.setCardNumber(cardNumber);
//            updateCoupon(param,uid);
            if(type == 1){//获取用户优惠券（未使用）
                wrapper.eq("status",0);
            }else if(type == 2){//获取用户优惠券（已使用）
                String listStr="1,2,3,4,5";
                wrapper.in("status", Arrays.asList(listStr.split(",")));
            }
        }else{
            if(type == 1){//获取用户优惠券（未使用）
                wrapper.eq("status",0);
            }else if(type == 2){//获取用户优惠券（已使用）
                wrapper.eq("status",1);
            }else if(type > 2){//获取用户优惠券（已过期）
                wrapper.eq("status",2);
            }
        }
        wrapper.ne("coupon_detail_type",3);
        wrapper.eq("project_code",projectCode);

        List<YxStoreCouponUser> storeCouponUsers = yxStoreCouponUserMapper.selectList(wrapper);

        List<YxStoreCouponUserQueryVo> storeCouponUserQueryVoList = new ArrayList<>();
        int nowTime = OrderUtil.getSecondTimestampTwo();
        for (YxStoreCouponUser couponUser : storeCouponUsers) {
            YxStoreCouponUserQueryVo queryVo = couponMap.toDto(couponUser);
            if(couponUser.getIsFail() == 1){
                queryVo.set_type(0);
                queryVo.set_msg("已失效");
            }else if (couponUser.getStatus() == 1){
                queryVo.set_type(0);
                queryVo.set_msg("已使用");
            }else if (couponUser.getStatus() == 2){
                queryVo.set_type(0);
                queryVo.set_msg("已过期");
            }else if(couponUser.getAddTime() > nowTime || couponUser.getEndTime() < nowTime){
                queryVo.set_type(0);
                queryVo.set_msg("已过期");
            }else{
                if(couponUser.getAddTime()+ 3600*24 > nowTime){
                    queryVo.set_type(2);
                    queryVo.set_msg("可使用");
                }else{
                    queryVo.set_type(1);
                    queryVo.set_msg("可使用");
                }
            }

            storeCouponUserQueryVoList.add(queryVo);

        }
        return storeCouponUserQueryVoList;
    }

    @Override
    public void addUserCoupon(int uid, int cid) {
        YxStoreCouponQueryVo storeCouponQueryVo = storeCouponService.
                getYxStoreCouponById(cid);
        if(ObjectUtil.isNull(storeCouponQueryVo)) throw new ErrorRequestException("优惠劵不存在");

        YxStoreCouponUser couponUser = new YxStoreCouponUser();
        couponUser.setCid(cid);
        couponUser.setUid(uid);
        couponUser.setCouponTitle(storeCouponQueryVo.getTitle());
        couponUser.setCouponPrice(storeCouponQueryVo.getCouponPrice());
        couponUser.setUseMinPrice(storeCouponQueryVo.getUseMinPrice());
        int addTime = OrderUtil.getSecondTimestampTwo();
        couponUser.setAddTime(addTime);
        int endTime = addTime + storeCouponQueryVo.getCouponTime() * 86400;
        couponUser.setEndTime(endTime);
        couponUser.setType("get");

        save(couponUser);

    }

    @Override
    public YxStoreCouponUserQueryVo getYxStoreCouponUserById(Serializable id) throws Exception{
        return yxStoreCouponUserMapper.getYxStoreCouponUserById(id);
    }

    @Override
    public Paging<YxStoreCouponUserQueryVo> getYxStoreCouponUserPageList(YxStoreCouponUserQueryParam yxStoreCouponUserQueryParam) throws Exception{
        Page page = setPageParam(yxStoreCouponUserQueryParam,OrderItem.desc("create_time"));
        IPage<YxStoreCouponUserQueryVo> iPage = yxStoreCouponUserMapper.getYxStoreCouponUserPageList(page,yxStoreCouponUserQueryParam);
        return new Paging(iPage);
    }

    @Override
    public YxStoreCouponUser beUsableCoupon4project(int uid, double price, String projectCode, String cardType,String orderSource) {
        if(ProjectNameEnum.TAIPING_LEXIANG.getValue().equals(projectCode) && TaipingCardTypeEnum.card_advanced.getValue().equals(cardType) ) {  // 太平项目优惠券
            // 每月抵扣次数不超过2次 ，全年最高享受6000元
            // 查询用于本月使用优惠券次数
            QueryWrapper queryWrapper = new QueryWrapper();
            queryWrapper.eq("uid",uid);
            // 月初的时间戳
            int startMonthTimestamp = OrderUtil.dateToTimestampT(DateUtil.beginOfMonth(DateUtil.date()));
            // 月末的时间戳
            int endMonthTimestamp = OrderUtil.dateToTimestampT(DateUtil.endOfMonth(DateUtil.date()));

            queryWrapper.between("use_time",startMonthTimestamp,endMonthTimestamp);
            queryWrapper.eq("status",1);

            int useTimes = this.count(queryWrapper);
            if(useTimes >= 2) {
                return  null;
            }


            // 全年最高享受6000元
         /*   double totalFactDeductionAmount = baseMapper.getTotalFactDeductionAmount(uid);

            if(totalFactDeductionAmount >= 6000d) {
                return null;
            }*/

            // 如果是互联网医院
            if(OrderSourceEnum.internetHospital.getDesc().equals(orderSource)) {
                QueryWrapper<YxStoreCouponUser> wrapper= new QueryWrapper<>();
                wrapper.eq("is_fail",0).eq("status",0).eq("uid",uid)
                        .last("limit 1") ;
                return getOne(wrapper);
            } else {  // 不是全场通用券
                QueryWrapper<YxStoreCouponUser> wrapper= new QueryWrapper<>();
                wrapper.eq("is_fail",0).eq("status",0).eq("uid",uid).eq("coupon_type",0)
                        .last("limit 1") ;
                return getOne(wrapper);
            }



        } else {
            return beUsableCoupon(uid,price);
        }

    }

    @Override
    public double getTotalFactDeductionAmount(Integer uid) {

        return yxStoreCouponUserMapper.getTotalFactDeductionAmount(uid);
    }

    @Override
    public List<YxStoreCouponUser> beUsableCouponList4project(int uid, double price, String projectCode, String cardType, String orderSource,String orderkey) {
        if(ProjectNameEnum.TAIPING_LEXIANG.getValue().equals(projectCode) && TaipingCardTypeEnum.card_advanced.getValue().equals(cardType) ) {  // 太平项目优惠券

            CacheDTO cacheDTO = yxStoreOrderService.getCacheOrderInfo(uid,orderkey);
            if(cacheDTO!= null) {
                List<YxStoreCartQueryVo> cartList = cacheDTO.getCartInfo();
                Boolean flag = true;
                for(YxStoreCartQueryVo cart : cartList) {
                    if( "Y".equals(cart.getLabel3())) {
                        flag = false;
                    }
                }
                // 没有5折类商品，不需要查优惠券
                if(flag) {
                    return new ArrayList<YxStoreCouponUser>();
                }
            }



            // 每月抵扣次数不超过2次 ，全年最高享受6000元
            // 查询用于本月使用优惠券次数
            /*QueryWrapper queryWrapper = new QueryWrapper();
            queryWrapper.eq("uid",uid);
            // 月初的时间戳
            int startMonthTimestamp = OrderUtil.dateToTimestampT(DateUtil.beginOfMonth(DateUtil.date()));
            // 月末的时间戳
            int endMonthTimestamp = OrderUtil.dateToTimestampT(DateUtil.endOfMonth(DateUtil.date()));

            queryWrapper.between("use_time",startMonthTimestamp,endMonthTimestamp);
            queryWrapper.eq("status",1);

            int useTimes = this.count(queryWrapper);
            if(useTimes >= 2) {
                return  new ArrayList<YxStoreCouponUser>();
            }*/


            // 全年最高享受6000元
         /*   double totalFactDeductionAmount = baseMapper.getTotalFactDeductionAmount(uid);

            if(totalFactDeductionAmount >= 6000d) {
                return null;
            }*/

            // 如果是互联网医院
            if(OrderSourceEnum.internetHospital.getValue().equals(orderSource)) {
                QueryWrapper<YxStoreCouponUser> wrapper= new QueryWrapper<>();
                wrapper.eq("is_fail",0).eq("status",0).eq("uid",uid).eq("project_code",projectCode) ;
                return list(wrapper);
            } else {  // 不是互联网医院的话，通用券
                QueryWrapper<YxStoreCouponUser> wrapper= new QueryWrapper<>();
                wrapper.eq("is_fail",0).eq("status",0).eq("uid",uid).eq("coupon_type",1).eq("project_code",projectCode) ;
                return list(wrapper);
            }
        }else if(ProjectNameEnum.ZHONGANPUYAO.getValue().equals(projectCode) || ProjectNameEnum.ZHONGANMANBING.getValue().equals(projectCode) || ProjectNameEnum.LINGYUANZHI.getValue().equals(projectCode)){
            QueryWrapper<YxStoreCouponUser> wrapper= new QueryWrapper<>();
            wrapper.eq("is_fail",0).eq("status",0).ne("coupon_detail_type",3).eq("uid",uid).eq("project_code",projectCode) ;
            return list(wrapper);
        }else{
            return new ArrayList<>();
        }

    }

    @Override
    public void updateCoupon(ZhongAnParamDto param,Integer uid) {
        LambdaUpdateWrapper<YxUser> selectWrapper = new LambdaUpdateWrapper<>();
        if(uid==null){
            selectWrapper.eq(YxUser::getPhone,param.getCardType());
            YxUser user=userService.getOne(selectWrapper);
            uid=user.getUid();
        }
        String body= ZhongAnRequestUtil.syncCoupons(param,appKey,version,privateKey,url);
        JSONObject jsonObject= JSONUtil.parseObj(body);
        if (jsonObject != null && "200".equals(jsonObject.get("code"))) {
            jsonObject = JSONUtil.parseObj(jsonObject.get("bizContent"));
            if (jsonObject != null && "0".equals(jsonObject.get("code"))) {
                JSONArray jsonArray = jsonObject.getJSONArray("result");

                List<YxStoreCouponUser> couponUsers = new ArrayList<>();
                for (int i = 0; i < jsonArray.size(); i++) {
                    JSONObject js = jsonArray.getJSONObject(i);
                    LambdaQueryWrapper<YxStoreCouponUser> lambdaQueryWrapper = new LambdaQueryWrapper<>();
                    lambdaQueryWrapper.eq(YxStoreCouponUser::getCouponNo, js.get("userCouponNo").toString());
                    lambdaQueryWrapper.eq(YxStoreCouponUser::getProjectCode, param.getProjectCode());
                    YxStoreCouponUser couponUser = this.getOne(lambdaQueryWrapper, false);

                    YxStoreCouponUser storeCouponUser = new YxStoreCouponUser();
                    if (couponUser != null) {
                        //众安可使用 、益药锁定，状态不变
                        if(Integer.valueOf(js.get("couponStatus").toString())==1 && couponUser.getStatus()==5){
                            continue;
                        }
                        storeCouponUser.setId(couponUser.getId());
                    } else {
                        storeCouponUser.setAddTime(OrderUtil.getSecondTimestampTwo());
                    }
                    storeCouponUser.setCouponTitle(js.get("couponName").toString());
                    storeCouponUser.setCouponNo(js.get("userCouponNo").toString());
                    storeCouponUser.setCouponDetailType(Integer.valueOf(js.get("couponType").toString()));
                    storeCouponUser.setUseMinPrice((js.get("minConsumeAmount") == null || js.get("minConsumeAmount").toString().equals("")) ? null : new BigDecimal(js.get("minConsumeAmount").toString()));
                    storeCouponUser.setCouponPrice((js.get("preferentialValues") == null || js.get("preferentialValues").toString().equals("")) ? null : new BigDecimal(js.get("preferentialValues").toString()));
                    storeCouponUser.setCouponEffectiveTime(DateUtils.dateTime(DateUtils.YYYY_MM_DD_HH_MM_SS,js.get("couponEffectiveTime").toString()));
                    storeCouponUser.setCouponExpiryTime( DateUtils.dateTime(DateUtils.YYYY_MM_DD_HH_MM_SS,js.get("couponExpiryTime").toString()));
                    storeCouponUser.setCouponType(0);
                    storeCouponUser.setUid(uid);
                    if (js.get("couponType").toString().equals("2")) {
                        storeCouponUser.setDeductionRate((js.get("preferentialValues") == null || js.get("preferentialValues").toString().equals("")) ? null
                                : (new BigDecimal(js.get("preferentialValues").toString())).divide(new BigDecimal(10), 2, BigDecimal.ROUND_HALF_UP));
                    }
                    storeCouponUser.setEndTime(OrderUtil.dateToTimestamp(DateUtils.dateTime(DateUtils.YYYY_MM_DD_HH_MM_SS,js.get("couponExpiryTime").toString())));
                    storeCouponUser.setProjectCode(param.getProjectCode());
                    storeCouponUser.setStatus(Integer.valueOf(js.get("couponStatus").toString())-1);
                    couponUsers.add(storeCouponUser);
                }
                if (couponUsers.size() > 0) {
                    this.saveOrUpdateBatch(couponUsers);
                }
            }
        }
    }

    public static void main(String[] args) {
        JSONObject jsonObject= JSONUtil.parseObj("{\"code\":\"200\",\"msg\":\"操作成功\",\"bizContent\":\"{\\\"code\\\":\\\"0\\\",\\\"result\\\":[{\\\"id\\\":1100002,\\\"channelSource\\\":\\\"za_app\\\",\\\"bizId\\\":235002,\\\"bizType\\\":\\\"1\\\",\\\"isUsedUnlimit\\\":\\\"N\\\",\\\"couponId\\\":275001,\\\"accountId\\\":1560010,\\\"userId\\\":1705018,\\\"userCouponNo\\\":\\\"2nL7HggaZb8UYBy2\\\",\\\"receiveMobile\\\":\\\"13834700771\\\",\\\"userCouponStatus\\\":1,\\\"userCouponAmount\\\":199.00,\\\"couponReceiveTime\\\":\\\"2021-09-02 14:18:16\\\",\\\"couponEffectiveTime\\\":\\\"2021-09-02 00:00:00\\\",\\\"couponExpiryTime\\\":\\\"2021-10-02 23:59:59\\\",\\\"coupon\\\":{\\\"id\\\":275001,\\\"couponNo\\\":\\\"CN21090200470001\\\",\\\"couponName\\\":\\\"上药满减券-超\\\",\\\"couponSubtitle\\\":\\\"上药满减券-超\\\",\\\"couponType\\\":1,\\\"usageMode\\\":\\\"\\\",\\\"couponSource\\\":\\\"1\\\",\\\"couponAmount\\\":199.00,\\\"minConsumeAmount\\\":200.00,\\\"couponExpectedCirculation\\\":20,\\\"couponActualCirculation\\\":15,\\\"useEffectiveMode\\\":1,\\\"designateDays\\\":30,\\\"couponUseScope\\\":\\\"mallDrugs\\\",\\\"couponStatus\\\":1,\\\"remark\\\":\\\"上药满减券-超\\\",\\\"couponDesc\\\":\\\"上药满减券-超\\\",\\\"releaseNum\\\":5,\\\"usedByActivity\\\":false}},{\\\"id\\\":1100004,\\\"channelSource\\\":\\\"za_app\\\",\\\"bizId\\\":235002,\\\"bizType\\\":\\\"1\\\",\\\"isUsedUnlimit\\\":\\\"N\\\",\\\"couponId\\\":275001,\\\"accountId\\\":1560010,\\\"userId\\\":1705018,\\\"userCouponNo\\\":\\\"Rq2PIDzyezGdTlsv\\\",\\\"receiveMobile\\\":\\\"13834700771\\\",\\\"userCouponStatus\\\":1,\\\"userCouponAmount\\\":199.00,\\\"couponReceiveTime\\\":\\\"2021-09-02 14:18:16\\\",\\\"couponEffectiveTime\\\":\\\"2021-09-02 00:00:00\\\",\\\"couponExpiryTime\\\":\\\"2021-10-02 23:59:59\\\",\\\"coupon\\\":{\\\"id\\\":275001,\\\"couponNo\\\":\\\"CN21090200470001\\\",\\\"couponName\\\":\\\"上药满减券-超\\\",\\\"couponSubtitle\\\":\\\"上药满减券-超\\\",\\\"couponType\\\":1,\\\"usageMode\\\":\\\"\\\",\\\"couponSource\\\":\\\"1\\\",\\\"couponAmount\\\":199.00,\\\"minConsumeAmount\\\":200.00,\\\"couponExpectedCirculation\\\":20,\\\"couponActualCirculation\\\":15,\\\"useEffectiveMode\\\":1,\\\"designateDays\\\":30,\\\"couponUseScope\\\":\\\"mallDrugs\\\",\\\"couponStatus\\\":1,\\\"remark\\\":\\\"上药满减券-超\\\",\\\"couponDesc\\\":\\\"上药满减券-超\\\",\\\"releaseNum\\\":5,\\\"usedByActivity\\\":false}},{\\\"id\\\":1100007,\\\"channelSource\\\":\\\"za_app\\\",\\\"bizId\\\":235002,\\\"bizType\\\":\\\"1\\\",\\\"isUsedUnlimit\\\":\\\"N\\\",\\\"couponId\\\":275001,\\\"accountId\\\":1560010,\\\"userId\\\":1705018,\\\"userCouponNo\\\":\\\"nygSYws4xIDOCY9J\\\",\\\"receiveMobile\\\":\\\"13834700771\\\",\\\"userCouponStatus\\\":1,\\\"userCouponAmount\\\":199.00,\\\"couponReceiveTime\\\":\\\"2021-09-02 14:18:16\\\",\\\"couponEffectiveTime\\\":\\\"2021-09-02 00:00:00\\\",\\\"couponExpiryTime\\\":\\\"2021-10-02 23:59:59\\\",\\\"coupon\\\":{\\\"id\\\":275001,\\\"couponNo\\\":\\\"CN21090200470001\\\",\\\"couponName\\\":\\\"上药满减券-超\\\",\\\"couponSubtitle\\\":\\\"上药满减券-超\\\",\\\"couponType\\\":1,\\\"usageMode\\\":\\\"\\\",\\\"couponSource\\\":\\\"1\\\",\\\"couponAmount\\\":199.00,\\\"minConsumeAmount\\\":200.00,\\\"couponExpectedCirculation\\\":20,\\\"couponActualCirculation\\\":15,\\\"useEffectiveMode\\\":1,\\\"designateDays\\\":30,\\\"couponUseScope\\\":\\\"mallDrugs\\\",\\\"couponStatus\\\":1,\\\"remark\\\":\\\"上药满减券-超\\\",\\\"couponDesc\\\":\\\"上药满减券-超\\\",\\\"releaseNum\\\":5,\\\"usedByActivity\\\":false}},{\\\"id\\\":1100010,\\\"channelSource\\\":\\\"za_app\\\",\\\"bizId\\\":235002,\\\"bizType\\\":\\\"1\\\",\\\"isUsedUnlimit\\\":\\\"N\\\",\\\"couponId\\\":275001,\\\"accountId\\\":1560010,\\\"userId\\\":1705018,\\\"userCouponNo\\\":\\\"6MHCvPoApFOkeLkH\\\",\\\"receiveMobile\\\":\\\"13834700771\\\",\\\"userCouponStatus\\\":1,\\\"userCouponAmount\\\":199.00,\\\"couponReceiveTime\\\":\\\"2021-09-02 14:18:16\\\",\\\"couponEffectiveTime\\\":\\\"2021-09-02 00:00:00\\\",\\\"couponExpiryTime\\\":\\\"2021-10-02 23:59:59\\\",\\\"coupon\\\":{\\\"id\\\":275001,\\\"couponNo\\\":\\\"CN21090200470001\\\",\\\"couponName\\\":\\\"上药满减券-超\\\",\\\"couponSubtitle\\\":\\\"上药满减券-超\\\",\\\"couponType\\\":1,\\\"usageMode\\\":\\\"\\\",\\\"couponSource\\\":\\\"1\\\",\\\"couponAmount\\\":199.00,\\\"minConsumeAmount\\\":200.00,\\\"couponExpectedCirculation\\\":20,\\\"couponActualCirculation\\\":15,\\\"useEffectiveMode\\\":1,\\\"designateDays\\\":30,\\\"couponUseScope\\\":\\\"mallDrugs\\\",\\\"couponStatus\\\":1,\\\"remark\\\":\\\"上药满减券-超\\\",\\\"couponDesc\\\":\\\"上药满减券-超\\\",\\\"releaseNum\\\":5,\\\"usedByActivity\\\":false}},{\\\"id\\\":1100013,\\\"channelSource\\\":\\\"za_app\\\",\\\"bizId\\\":235002,\\\"bizType\\\":\\\"1\\\",\\\"isUsedUnlimit\\\":\\\"N\\\",\\\"couponId\\\":275001,\\\"accountId\\\":1560010,\\\"userId\\\":1705018,\\\"userCouponNo\\\":\\\"CebqIJb4Y7Qkl978\\\",\\\"receiveMobile\\\":\\\"13834700771\\\",\\\"userCouponStatus\\\":1,\\\"userCouponAmount\\\":199.00,\\\"couponReceiveTime\\\":\\\"2021-09-02 14:18:16\\\",\\\"couponEffectiveTime\\\":\\\"2021-09-02 00:00:00\\\",\\\"couponExpiryTime\\\":\\\"2021-10-02 23:59:59\\\",\\\"coupon\\\":{\\\"id\\\":275001,\\\"couponNo\\\":\\\"CN21090200470001\\\",\\\"couponName\\\":\\\"上药满减券-超\\\",\\\"couponSubtitle\\\":\\\"上药满减券-超\\\",\\\"couponType\\\":1,\\\"usageMode\\\":\\\"\\\",\\\"couponSource\\\":\\\"1\\\",\\\"couponAmount\\\":199.00,\\\"minConsumeAmount\\\":200.00,\\\"couponExpectedCirculation\\\":20,\\\"couponActualCirculation\\\":15,\\\"useEffectiveMode\\\":1,\\\"designateDays\\\":30,\\\"couponUseScope\\\":\\\"mallDrugs\\\",\\\"couponStatus\\\":1,\\\"remark\\\":\\\"上药满减券-超\\\",\\\"couponDesc\\\":\\\"上药满减券-超\\\",\\\"releaseNum\\\":5,\\\"usedByActivity\\\":false}},{\\\"id\\\":1100016,\\\"channelSource\\\":\\\"za_app\\\",\\\"channelResourceCode\\\":\\\"HYH5\\\",\\\"bizId\\\":235004,\\\"bizType\\\":\\\"1\\\",\\\"isUsedUnlimit\\\":\\\"N\\\",\\\"couponId\\\":275002,\\\"accountId\\\":1560010,\\\"userId\\\":1705018,\\\"userCouponNo\\\":\\\"B4kRRtWHZu5JlZ95\\\",\\\"receiveMobile\\\":\\\"13834700771\\\",\\\"userCouponStatus\\\":1,\\\"couponReceiveTime\\\":\\\"2021-09-02 14:20:34\\\",\\\"couponEffectiveTime\\\":\\\"2021-09-02 00:00:00\\\",\\\"couponExpiryTime\\\":\\\"2021-10-02 23:59:59\\\",\\\"coupon\\\":{\\\"id\\\":275002,\\\"couponNo\\\":\\\"CN21090200470002\\\",\\\"couponName\\\":\\\"上药折扣券-超\\\",\\\"couponSubtitle\\\":\\\"上药折扣券-超\\\",\\\"couponType\\\":2,\\\"usageMode\\\":\\\"\\\",\\\"couponSource\\\":\\\"1\\\",\\\"minConsumeAmount\\\":80.00,\\\"discount\\\":0.10,\\\"couponExpectedCirculation\\\":20,\\\"couponActualCirculation\\\":15,\\\"useEffectiveMode\\\":1,\\\"designateDays\\\":30,\\\"couponUseScope\\\":\\\"mallDrugs\\\",\\\"couponStatus\\\":1,\\\"remark\\\":\\\"上药折扣券-超\\\",\\\"couponDesc\\\":\\\"上药折扣券-超\\\",\\\"releaseNum\\\":5,\\\"usedByActivity\\\":false}},{\\\"id\\\":1100018,\\\"channelSource\\\":\\\"za_app\\\",\\\"channelResourceCode\\\":\\\"HYH5\\\",\\\"bizId\\\":235004,\\\"bizType\\\":\\\"1\\\",\\\"isUsedUnlimit\\\":\\\"N\\\",\\\"couponId\\\":275002,\\\"accountId\\\":1560010,\\\"userId\\\":1705018,\\\"userCouponNo\\\":\\\"aCT1qkQOQtx48Pqe\\\",\\\"receiveMobile\\\":\\\"13834700771\\\",\\\"userCouponStatus\\\":1,\\\"couponReceiveTime\\\":\\\"2021-09-02 14:20:34\\\",\\\"couponEffectiveTime\\\":\\\"2021-09-02 00:00:00\\\",\\\"couponExpiryTime\\\":\\\"2021-10-02 23:59:59\\\",\\\"coupon\\\":{\\\"id\\\":275002,\\\"couponNo\\\":\\\"CN21090200470002\\\",\\\"couponName\\\":\\\"上药折扣券-超\\\",\\\"couponSubtitle\\\":\\\"上药折扣券-超\\\",\\\"couponType\\\":2,\\\"usageMode\\\":\\\"\\\",\\\"couponSource\\\":\\\"1\\\",\\\"minConsumeAmount\\\":80.00,\\\"discount\\\":0.10,\\\"couponExpectedCirculation\\\":20,\\\"couponActualCirculation\\\":15,\\\"useEffectiveMode\\\":1,\\\"designateDays\\\":30,\\\"couponUseScope\\\":\\\"mallDrugs\\\",\\\"couponStatus\\\":1,\\\"remark\\\":\\\"上药折扣券-超\\\",\\\"couponDesc\\\":\\\"上药折扣券-超\\\",\\\"releaseNum\\\":5,\\\"usedByActivity\\\":false}},{\\\"id\\\":1100020,\\\"channelSource\\\":\\\"za_app\\\",\\\"channelResourceCode\\\":\\\"HYH5\\\",\\\"bizId\\\":235004,\\\"bizType\\\":\\\"1\\\",\\\"isUsedUnlimit\\\":\\\"N\\\",\\\"couponId\\\":275002,\\\"accountId\\\":1560010,\\\"userId\\\":1705018,\\\"userCouponNo\\\":\\\"Z82FVus0P9kZUzR3\\\",\\\"receiveMobile\\\":\\\"13834700771\\\",\\\"userCouponStatus\\\":1,\\\"couponReceiveTime\\\":\\\"2021-09-02 14:20:34\\\",\\\"couponEffectiveTime\\\":\\\"2021-09-02 00:00:00\\\",\\\"couponExpiryTime\\\":\\\"2021-10-02 23:59:59\\\",\\\"coupon\\\":{\\\"id\\\":275002,\\\"couponNo\\\":\\\"CN21090200470002\\\",\\\"couponName\\\":\\\"上药折扣券-超\\\",\\\"couponSubtitle\\\":\\\"上药折扣券-超\\\",\\\"couponType\\\":2,\\\"usageMode\\\":\\\"\\\",\\\"couponSource\\\":\\\"1\\\",\\\"minConsumeAmount\\\":80.00,\\\"discount\\\":0.10,\\\"couponExpectedCirculation\\\":20,\\\"couponActualCirculation\\\":15,\\\"useEffectiveMode\\\":1,\\\"designateDays\\\":30,\\\"couponUseScope\\\":\\\"mallDrugs\\\",\\\"couponStatus\\\":1,\\\"remark\\\":\\\"上药折扣券-超\\\",\\\"couponDesc\\\":\\\"上药折扣券-超\\\",\\\"releaseNum\\\":5,\\\"usedByActivity\\\":false}},{\\\"id\\\":1100022,\\\"channelSource\\\":\\\"za_app\\\",\\\"channelResourceCode\\\":\\\"HYH5\\\",\\\"bizId\\\":235004,\\\"bizType\\\":\\\"1\\\",\\\"isUsedUnlimit\\\":\\\"N\\\",\\\"couponId\\\":275002,\\\"accountId\\\":1560010,\\\"userId\\\":1705018,\\\"userCouponNo\\\":\\\"FqY20sAn5Dw9Imhx\\\",\\\"receiveMobile\\\":\\\"13834700771\\\",\\\"userCouponStatus\\\":1,\\\"couponReceiveTime\\\":\\\"2021-09-02 14:20:35\\\",\\\"couponEffectiveTime\\\":\\\"2021-09-02 00:00:00\\\",\\\"couponExpiryTime\\\":\\\"2021-10-02 23:59:59\\\",\\\"coupon\\\":{\\\"id\\\":275002,\\\"couponNo\\\":\\\"CN21090200470002\\\",\\\"couponName\\\":\\\"上药折扣券-超\\\",\\\"couponSubtitle\\\":\\\"上药折扣券-超\\\",\\\"couponType\\\":2,\\\"usageMode\\\":\\\"\\\",\\\"couponSource\\\":\\\"1\\\",\\\"minConsumeAmount\\\":80.00,\\\"discount\\\":0.10,\\\"couponExpectedCirculation\\\":20,\\\"couponActualCirculation\\\":15,\\\"useEffectiveMode\\\":1,\\\"designateDays\\\":30,\\\"couponUseScope\\\":\\\"mallDrugs\\\",\\\"couponStatus\\\":1,\\\"remark\\\":\\\"上药折扣券-超\\\",\\\"couponDesc\\\":\\\"上药折扣券-超\\\",\\\"releaseNum\\\":5,\\\"usedByActivity\\\":false}},{\\\"id\\\":1100024,\\\"channelSource\\\":\\\"za_app\\\",\\\"channelResourceCode\\\":\\\"HYH5\\\",\\\"bizId\\\":235004,\\\"bizType\\\":\\\"1\\\",\\\"isUsedUnlimit\\\":\\\"N\\\",\\\"couponId\\\":275002,\\\"accountId\\\":1560010,\\\"userId\\\":1705018,\\\"userCouponNo\\\":\\\"JSPiX040LoW7SAQi\\\",\\\"receiveMobile\\\":\\\"13834700771\\\",\\\"userCouponStatus\\\":1,\\\"couponReceiveTime\\\":\\\"2021-09-02 14:20:35\\\",\\\"couponEffectiveTime\\\":\\\"2021-09-02 00:00:00\\\",\\\"couponExpiryTime\\\":\\\"2021-10-02 23:59:59\\\",\\\"coupon\\\":{\\\"id\\\":275002,\\\"couponNo\\\":\\\"CN21090200470002\\\",\\\"couponName\\\":\\\"上药折扣券-超\\\",\\\"couponSubtitle\\\":\\\"上药折扣券-超\\\",\\\"couponType\\\":2,\\\"usageMode\\\":\\\"\\\",\\\"couponSource\\\":\\\"1\\\",\\\"minConsumeAmount\\\":80.00,\\\"discount\\\":0.10,\\\"couponExpectedCirculation\\\":20,\\\"couponActualCirculation\\\":15,\\\"useEffectiveMode\\\":1,\\\"designateDays\\\":30,\\\"couponUseScope\\\":\\\"mallDrugs\\\",\\\"couponStatus\\\":1,\\\"remark\\\":\\\"上药折扣券-超\\\",\\\"couponDesc\\\":\\\"上药折扣券-超\\\",\\\"releaseNum\\\":5,\\\"usedByActivity\\\":false}}]}\\n\",\"format\":\"JSON\",\"charset\":\"utf-8\",\"sign\":\"JxYPL2NIAeqPGh0+LmkJXC7ZrYGvyhSYs2moAOphXt/wexqpGmreKwp/zTDkXlqrrRtmufptiXY9o/GqLwvx5uZtT7iujwc0cQNDIeGMpZruT3RfHe5bud8SKBTypOfpO0FKmF1Wl0xYIJDnIp83Wwy6dcodY2U+pbeuXZR1Skc1n5wKNK7a/9iTjSoMdjqeVFE3UqDQW02xeRMwndgDNhY7V6YOfQocTAZ+wQScq7NanwFTMLuBngpuSI2a7rv2c15l+9kSbhVtjjgIRkQWjumWsXu4DwsTCJJ8dkK7n4E5IgSybpvDeZBUlvNZ0FbgcSCUgZpu1M/c+ckDVgo5yw==\",\"signType\":\"RSA2\",\"timestamp\":\"2021-09-02 16:14:10\"}");
        if (jsonObject != null && "200".equals(jsonObject.get("code"))) {
            jsonObject = JSONUtil.parseObj(jsonObject.get("bizContent"));
            if (jsonObject != null && "0".equals(jsonObject.get("code"))) {
                JSONArray jsonArray = jsonObject.getJSONArray("result");

                List<YxStoreCouponUser> couponUsers = new ArrayList<>();
                for (int i = 0; i < jsonArray.size(); i++){

                }
            }
        }

    }
}
