/**
* Copyright (C) 2018-2020
* All rights reserved, Designed By www.yixiang.co
* 注意：
* 本软件为www.yixiang.co开发研制，未经购买不得使用
* 购买后可获得全部源代码（禁止转卖、分享、上传到码云、github等开源平台）
* 一经发现盗用、分享等行为，将追究法律责任，后果自负
*/
package co.yixiang.modules.taiping.service.impl;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import co.yixiang.enums.ProjectNameEnum;
import co.yixiang.modules.activity.domain.YxStoreCoupon;
import co.yixiang.modules.activity.service.YxStoreCouponService;
import co.yixiang.modules.activity.service.YxStoreCouponUserService;
import co.yixiang.modules.taiping.domain.YxStoreCouponCard;
import co.yixiang.common.service.impl.BaseServiceImpl;
import co.yixiang.utils.OrderUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import lombok.AllArgsConstructor;
import co.yixiang.dozer.service.IGenerator;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import co.yixiang.common.utils.QueryHelpPlus;
import co.yixiang.utils.ValidationUtil;
import co.yixiang.utils.FileUtil;
import co.yixiang.modules.taiping.service.YxStoreCouponCardService;
import co.yixiang.modules.taiping.service.dto.YxStoreCouponCardDto;
import co.yixiang.modules.taiping.service.dto.YxStoreCouponCardQueryCriteria;
import co.yixiang.modules.taiping.service.mapper.YxStoreCouponCardMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
// 默认不使用缓存
//import org.springframework.cache.annotation.CacheConfig;
//import org.springframework.cache.annotation.CacheEvict;
//import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.*;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;

/**
* @author visa
* @date 2020-12-10
*/
@Service
@AllArgsConstructor
//@CacheConfig(cacheNames = "yxStoreCouponCard")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class YxStoreCouponCardServiceImpl extends BaseServiceImpl<YxStoreCouponCardMapper, YxStoreCouponCard> implements YxStoreCouponCardService {

    private final IGenerator generator;

    @Autowired
    private YxStoreCouponService yxStoreCouponService;

    @Autowired
    private YxStoreCouponUserService yxStoreCouponUserService;

    @Override
    //@Cacheable
    public Map<String, Object> queryAll(YxStoreCouponCardQueryCriteria criteria, Pageable pageable) {
        getPage(pageable);
        PageInfo<YxStoreCouponCard> page = new PageInfo<>(queryAll(criteria));
        Map<String, Object> map = new LinkedHashMap<>(2);
        map.put("content", generator.convert(page.getList(), YxStoreCouponCardDto.class));
        map.put("totalElements", page.getTotal());
        return map;
    }


    @Override
    //@Cacheable
    public List<YxStoreCouponCard> queryAll(YxStoreCouponCardQueryCriteria criteria){
        return baseMapper.selectList(QueryHelpPlus.getPredicate(YxStoreCouponCard.class, criteria));
    }


    @Override
    public void download(List<YxStoreCouponCardDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (YxStoreCouponCardDto yxStoreCouponCard : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("兑换的项目id", yxStoreCouponCard.getCid());
            map.put("优惠券所属卡号", yxStoreCouponCard.getCardNumber());
            map.put("优惠券名称", yxStoreCouponCard.getCouponTitle());
            map.put("优惠券的面值", yxStoreCouponCard.getCouponPrice());
            map.put("最低消费多少金额可用优惠券", yxStoreCouponCard.getUseMinPrice());
            map.put("优惠券创建时间", yxStoreCouponCard.getAddTime());
            map.put("优惠券结束时间", yxStoreCouponCard.getEndTime());
            map.put("使用时间", yxStoreCouponCard.getUseTime());
            map.put("获取方式", yxStoreCouponCard.getType());
            map.put("状态（0：未使用，1：已使用, 2:已过期）", yxStoreCouponCard.getStatus());
            map.put("是否有效", yxStoreCouponCard.getIsFail());
            map.put("实际抵扣金额", yxStoreCouponCard.getFactDeductionAmount());
            map.put("最高抵扣金额", yxStoreCouponCard.getMaxDeductionAmount());
            map.put("折扣率", yxStoreCouponCard.getDeductionRate());
            map.put("是否全场通用 1 表示全场通用 0表示否", yxStoreCouponCard.getCouponType());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

    @Override
    public void generateCouponByCardNumber(String cardNumber, Date beginDate) {

        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("card_number",cardNumber);
        int amount  = this.count(queryWrapper);
        if(amount > 0) {
            return;
        }
       YxStoreCoupon coupon = yxStoreCouponService.getOne(new QueryWrapper<YxStoreCoupon>().eq("title","尊享问诊券"),false);
       YxStoreCoupon coupon_all = yxStoreCouponService.getOne(new QueryWrapper<YxStoreCoupon>().eq("title","尊享通用券"),false);

       if(ObjectUtil.isNotEmpty(coupon) && ObjectUtil.isNotEmpty(coupon_all)) {

           DateTime startdate = DateUtil.beginOfDay(beginDate);
           DateTime enddate =  DateUtil.offsetMonth(startdate,12);
           int i= 1;
           while (i <= 12 ) {
               YxStoreCouponCard couponCard = new YxStoreCouponCard();
               couponCard.setCardNumber(cardNumber);
               couponCard.setAddTime(OrderUtil.dateToTimestamp(startdate));
               couponCard.setEndTime(OrderUtil.dateToTimestamp( DateUtil.offsetDay(DateUtil.offsetDay(startdate,30),-1)));
               couponCard.setCid(coupon.getId());
               couponCard.setCouponTitle(coupon.getTitle());
               couponCard.setMaxDeductionAmount(coupon.getMaxDeductionAmount());
               couponCard.setDeductionRate(coupon.getDeductionRate());
               couponCard.setStatus(0);
               couponCard.setIsFail(0);
               couponCard.setCouponType(coupon.getCouponType());
               couponCard.setType("send");
               if(i== 1 || i == 2) {

                   YxStoreCouponCard couponCard_all = new YxStoreCouponCard();
                   couponCard_all.setCardNumber(cardNumber);
                   couponCard_all.setAddTime(OrderUtil.dateToTimestampT(startdate));
                   couponCard_all.setEndTime(OrderUtil.dateToTimestamp( DateUtil.offsetDay(DateUtil.offsetDay(startdate,30),-1)));
                   couponCard_all.setCid(coupon_all.getId());
                   couponCard_all.setCouponTitle(coupon_all.getTitle());
                   couponCard_all.setMaxDeductionAmount(coupon_all.getMaxDeductionAmount());
                   couponCard_all.setDeductionRate(coupon_all.getDeductionRate());
                   couponCard_all.setStatus(0);
                   couponCard_all.setIsFail(0);
                   couponCard_all.setCouponType(coupon_all.getCouponType());
                   couponCard_all.setType("send");

                   this.save(couponCard_all);
               } else {
                   this.save(couponCard);
               }
               this.save(couponCard);
               i = i+1;
               startdate = DateUtil.offsetDay(startdate,30);

           }




       }
    }

    @Override
    public void generateCouponByCardNumber2(String parm) {
        List<String> list = Arrays.asList(parm.split(","));
        String cardNumber = list.get(0);
        String beginDateStr = list.get(1);

        generateCouponByCardNumber(cardNumber, DateUtil.parse(beginDateStr));

    }


    @Override
    public void updateInvalidStatus() {
       int current = OrderUtil.dateToTimestamp( DateUtil.beginOfDay(new Date()));
        UpdateWrapper updateWrapper = new UpdateWrapper();
        updateWrapper.set("status",2);
        updateWrapper.le("end_time",current);
        updateWrapper.eq("status",1);
        updateWrapper.eq("project_code", ProjectNameEnum.TAIPING_LEXIANG.getValue());
        this.update(updateWrapper);

        yxStoreCouponUserService.update(updateWrapper);
    }
}
