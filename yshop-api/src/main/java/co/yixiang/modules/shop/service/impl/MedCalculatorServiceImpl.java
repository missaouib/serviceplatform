package co.yixiang.modules.shop.service.impl;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import co.yixiang.common.utils.QueryHelpPlus;
import co.yixiang.enums.ProjectNameEnum;
import co.yixiang.modules.shop.entity.MedCalculator;
import co.yixiang.modules.shop.entity.MedCalculatorDetail;
import co.yixiang.modules.shop.entity.Product4project;
import co.yixiang.modules.shop.mapper.MedCalculatorMapper;
import co.yixiang.modules.shop.service.MedCalculatorDetailService;
import co.yixiang.modules.shop.service.MedCalculatorService;
import co.yixiang.modules.shop.service.Product4projectService;
import co.yixiang.modules.shop.web.param.MedCalculatorQueryParam;
import co.yixiang.modules.shop.web.vo.MedCalculatorQueryVo;
import co.yixiang.common.service.impl.BaseServiceImpl;
import co.yixiang.common.web.vo.Paging;
import co.yixiang.utils.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;


/**
 * <p>
 * 用药计算器 服务实现类
 * </p>
 *
 * @author visa
 * @since 2021-01-03
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class MedCalculatorServiceImpl extends BaseServiceImpl<MedCalculatorMapper, MedCalculator> implements MedCalculatorService {

    @Autowired
    private MedCalculatorMapper medCalculatorMapper;

    @Autowired
    private Product4projectService product4projectService;

    @Autowired
    private MedCalculatorDetailService medCalculatorDetailService;

    @Override
    public MedCalculatorQueryVo getMedCalculatorById(Serializable id) throws Exception{
        return medCalculatorMapper.getMedCalculatorById(id);
    }

    @Override
    public Paging<MedCalculatorQueryVo> getMedCalculatorPageList(MedCalculatorQueryParam medCalculatorQueryParam) throws Exception{
        Page page = setPageParam(medCalculatorQueryParam,OrderItem.desc("create_time"));
        QueryWrapper queryWrapper = QueryHelpPlus.getPredicate(MedCalculatorQueryParam.class, medCalculatorQueryParam);
        IPage<MedCalculatorQueryVo> iPage = medCalculatorMapper.selectPage(page,queryWrapper);
        return new Paging(iPage);
    }

    @Override
    public MedCalculator getMedCalculatorByUid(Integer uid,Date calcuDate) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("uid",uid);
        MedCalculator medCalculator = this.getOne(queryWrapper,false);



        if(medCalculator == null) {
            medCalculator = new MedCalculator();
            medCalculator.setStartDate(new Date());
            medCalculator.setUid(uid);
            medCalculator.setCreateTime(new Date());
            medCalculator.setUpdateTime(new Date());
            medCalculator.setYear( new Integer(DateUtil.year(new Date())).toString());
            String month =  StrUtil.fillBefore(new Integer(DateUtil.month(new Date()) + 1 ).toString(),'0',2);
            String day = StrUtil.fillBefore(new Integer(DateUtil.dayOfMonth(new Date())).toString(),'0',2);
            medCalculator.setMonth(month);
            medCalculator.setDay(day);
            medCalculator.setResult(null);
            medCalculator.setDays(0);
            QueryWrapper queryWrapper1 = new QueryWrapper();
            queryWrapper1.eq("project_no", ProjectNameEnum.ROCHE_SMA.getValue());
            Product4project product4project =  product4projectService.getOne(queryWrapper1,false);
            medCalculator.setMedName(product4project.getProductName());
            this.save(medCalculator);
        } else {
            if(StrUtil.isBlank(medCalculator.getMedName())) {
                QueryWrapper queryWrapper1 = new QueryWrapper();
                queryWrapper1.eq("project_no", ProjectNameEnum.ROCHE_SMA.getValue());
                Product4project product4project =  product4projectService.getOne(queryWrapper1,false);
                medCalculator.setMedName(product4project.getProductName());
            }

            if (medCalculator.getMedAmount() == null || medCalculator.getUseAmount() == null|| medCalculator.getStartDate() == null) {
                /*QueryWrapper queryWrapper1 = new QueryWrapper();
                queryWrapper1.eq("project_no", ProjectNameEnum.ROCHE_SMA.getValue());
                Product4project product4project =  product4projectService.getOne(queryWrapper1,false);
                medCalculator = new MedCalculator();
                medCalculator.setMedName(product4project.getProductName());
                medCalculator.setStartDate(new Date());
                medCalculator.setUid(uid);*/
                medCalculator.setResult(null);
                medCalculator.setDays(0);
                medCalculator.setStartDate(new Date());
                medCalculator.setYear( new Integer(DateUtil.year(new Date())).toString());
                String month =  StrUtil.fillBefore(new Integer(DateUtil.month(new Date()) + 1 ).toString(),'0',2);
                String day = StrUtil.fillBefore(new Integer(DateUtil.dayOfMonth(new Date())).toString(),'0',2);
                medCalculator.setMonth(month);
                medCalculator.setDay(day);
                return medCalculator;
            }
            calculator(medCalculator,calcuDate);

            medCalculator.setYear( new Integer(DateUtil.year(medCalculator.getStartDate())).toString());
            String month =  StrUtil.fillBefore(new Integer(DateUtil.month(medCalculator.getStartDate()) + 1 ).toString(),'0',2);
            String day = StrUtil.fillBefore(new Integer(DateUtil.dayOfMonth(medCalculator.getStartDate())).toString(),'0',2);
            medCalculator.setMonth(month);
            medCalculator.setDay(day);
        }


        return medCalculator;
    }

    void calculator(MedCalculator medCalculator,Date calcuDate){
        // 计算结果
        Integer days =  0;
        if(DateUtil.beginOfDay(calcuDate).isBefore(DateUtil.beginOfDay(medCalculator.getStartDate()))) {
            medCalculator.setResult(null);
            medCalculator.setDays(0);

            return;
        }
        days = new Long(DateUtil.betweenDay( DateUtil.beginOfDay(calcuDate),DateUtil.beginOfDay(medCalculator.getStartDate()),true )).intValue();
        days = days + 1;

        medCalculator.setDays(days);
        // 总剂量
        Integer totalAmount = 80 * medCalculator.getMedAmount();

        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("uid",medCalculator.getUid());
        queryWrapper.orderByAsc("modify_date");
        List<MedCalculatorDetail> medCalculatorDetails = medCalculatorDetailService.list(queryWrapper);

        HashMap<DateTime,Integer> hashMap = new HashMap<>();
        for(MedCalculatorDetail medCalculatorDetail:medCalculatorDetails) {
            hashMap.put(DateUtil.date(medCalculatorDetail.getModifyDate()),medCalculatorDetail.getUseAmount());
        }
        Integer useAmountTemp = 0;
        Integer leftAmount = totalAmount;
        DateTime dateTemp =  DateUtil.beginOfDay(medCalculator.getStartDate());
        while (dateTemp.isBeforeOrEquals(DateUtil.beginOfDay(calcuDate))  ) {
            if(hashMap.containsKey(dateTemp)) {
                useAmountTemp = hashMap.get(dateTemp);
            }
            leftAmount = leftAmount - useAmountTemp;
            if(leftAmount <= 0) {
                break;
            }
            dateTemp = DateUtil.offsetDay(dateTemp,1);
        }



        // 还能服用多少天
        Integer result = 0;
        if(medCalculator.getUseAmount() != null && medCalculator.getUseAmount().intValue() != 0 ) {
            result = leftAmount / medCalculator.getUseAmount().intValue();
        }
        medCalculator.setResult(result);
        medCalculator.setDays(days);
        medCalculator.setLeftAmountTemp(leftAmount);
        // medCalculator.setCalcuDate(DateUtil.beginOfDay(calcuDate));
        this.updateById(medCalculator);
    };

    @Override
    public void updateMedCalculator(MedCalculator medCalculator) {

        String month = "";
        if(StrUtil.isBlank( medCalculator.getMonth())) {
            month = StrUtil.fillBefore(String.valueOf(DateUtil.date().month() + 1),'0',2);
        } else {
            month =  StrUtil.fillBefore(medCalculator.getMonth(),'0',2);
        }

        String day = "";
        if(StrUtil.isBlank( medCalculator.getDay())) {
            day = StrUtil.fillBefore(String.valueOf(DateUtil.date().dayOfMonth()),'0',2);
        } else {
            day =  StrUtil.fillBefore(medCalculator.getDay(),'0',2);
        }
        String year = "";
        if(StrUtil.isBlank( medCalculator.getYear())) {
            year = String.valueOf(DateUtil.date().year());
        } else {
            year =  medCalculator.getYear();
        }
        medCalculator.setStartDate( DateUtil.parse(year + "-" + month + "-" + day ));
        Integer uid = medCalculator.getUid();
        if(uid == null) {
            uid = SecurityUtils.getUserId().intValue();
        }
        if(medCalculator.getMedAmount() == null || medCalculator.getUseAmount() == null) {
            medCalculator.setResult(null);
            medCalculator.setDays(0);
            // 删除明细表
            QueryWrapper queryWrapper = new QueryWrapper();
            queryWrapper.eq("uid",uid);
            medCalculatorDetailService.remove(queryWrapper);
            this.saveOrUpdate(medCalculator);
            return;
        }

        // 获取明细表中最后一条
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("uid",uid);
        queryWrapper.orderByDesc("modify_date");
        queryWrapper.last("limit 1");
        MedCalculatorDetail medCalculatorDetail = medCalculatorDetailService.getOne(queryWrapper,false);
        if(medCalculatorDetail == null) {
            // 插入
            medCalculatorDetail = new MedCalculatorDetail();
            medCalculatorDetail.setCreateTime(new Date());
            medCalculatorDetail.setUpdateTime(new Date());
            medCalculatorDetail.setModifyDate( DateUtil.beginOfDay(medCalculator.getStartDate()));
            medCalculatorDetail.setUid(uid);
            medCalculatorDetail.setUseAmount(medCalculator.getUseAmount());
        } else {
           if( ! medCalculatorDetail.getUseAmount().equals( medCalculator.getUseAmount()) ) {  // 修改用药量
               // 日期一样，更新
               if(DateUtil.beginOfDay(medCalculatorDetail.getModifyDate()).equals(DateUtil.beginOfDay(new Date()))) {
                   medCalculatorDetail.setUpdateTime(new Date());
                   medCalculatorDetail.setModifyDate( DateUtil.beginOfDay(new Date()));
                   medCalculatorDetail.setUid(uid);
                   medCalculatorDetail.setUseAmount(medCalculator.getUseAmount());
               } else {  // 日期不一样，插入
                   medCalculatorDetail = new MedCalculatorDetail();
                   medCalculatorDetail.setCreateTime(new Date());
                   medCalculatorDetail.setUpdateTime(new Date());
                   medCalculatorDetail.setModifyDate( DateUtil.beginOfDay(new Date()));
                   medCalculatorDetail.setUid(uid);
                   medCalculatorDetail.setUseAmount(medCalculator.getUseAmount());
               }
           }

        }

        medCalculatorDetailService.saveOrUpdate(medCalculatorDetail);
        this.saveOrUpdate(medCalculator);
    }
}
