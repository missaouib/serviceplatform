/**
* Copyright (C) 2018-2020
* All rights reserved, Designed By www.yixiang.co
* 注意：
* 本软件为www.yixiang.co开发研制，未经购买不得使用
* 购买后可获得全部源代码（禁止转卖、分享、上传到码云、github等开源平台）
* 一经发现盗用、分享等行为，将追究法律责任，后果自负
*/
package co.yixiang.mp.yiyaobao.service.impl;

import co.yixiang.mp.yiyaobao.domain.OrdOrder;
import co.yixiang.common.service.impl.BaseServiceImpl;
import lombok.AllArgsConstructor;
import co.yixiang.dozer.service.IGenerator;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import co.yixiang.common.utils.QueryHelpPlus;
import co.yixiang.utils.ValidationUtil;
import co.yixiang.utils.FileUtil;
import co.yixiang.mp.yiyaobao.service.OrdOrderService;
import co.yixiang.mp.yiyaobao.service.dto.OrdOrderDto;
import co.yixiang.mp.yiyaobao.service.dto.OrdOrderQueryCriteria;
import co.yixiang.mp.yiyaobao.service.mapper.OrdOrderMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import cn.hutool.core.util.IdUtil;
// 默认不使用缓存
//import org.springframework.cache.annotation.CacheConfig;
//import org.springframework.cache.annotation.CacheEvict;
//import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Map;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
* @author visa
* @date 2020-06-28
*/
@Service
@AllArgsConstructor
//@CacheConfig(cacheNames = "ordOrder")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class OrdOrderServiceImpl extends BaseServiceImpl<OrdOrderMapper, OrdOrder> implements OrdOrderService {

    private final IGenerator generator;

    @Override
    //@Cacheable
    public Map<String, Object> queryAll(OrdOrderQueryCriteria criteria, Pageable pageable) {
        getPage(pageable);
        PageInfo<OrdOrder> page = new PageInfo<>(queryAll(criteria));
        Map<String, Object> map = new LinkedHashMap<>(2);
        map.put("content", generator.convert(page.getList(), OrdOrderDto.class));
        map.put("totalElements", page.getTotal());
        return map;
    }


    @Override
    //@Cacheable
    public List<OrdOrder> queryAll(OrdOrderQueryCriteria criteria){
        return baseMapper.selectList(QueryHelpPlus.getPredicate(OrdOrder.class, criteria));
    }


    @Override
    public void download(List<OrdOrderDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (OrdOrderDto ordOrder : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("订单号", ordOrder.getOrderNo());
            map.put("EBS订单号", ordOrder.getEbsOrderNo());
            map.put("订单类型(-;10-特药;20-普通;30-寄售)", ordOrder.getOrderType());
            map.put("药店ID", ordOrder.getSellerId());
            map.put("订货用户ID", ordOrder.getUserId());
            map.put("流通项目ID", ordOrder.getProjectId());
            map.put("金融项目ID", ordOrder.getFinProjectId());
            map.put("订单来源（01-APP;02-社区;03-医院;04-网页）", ordOrder.getOrderSource());
            map.put("患者名称", ordOrder.getPatientName());
            map.put("患者身份证号码", ordOrder.getPatientIdCard());
            map.put("订单总金额(元)", ordOrder.getTotalAmount());
            map.put("折扣率(如0.92)", ordOrder.getDiscount());
            map.put("折扣金额(元)", ordOrder.getDiscountAmount());
            map.put("优惠券金额", ordOrder.getCouponAmount());
            map.put("特殊折扣金额", ordOrder.getSpecialAmount());
            map.put("运费", ordOrder.getFreightFee());
            map.put("实际运费", ordOrder.getActualFreightFee());
            map.put("实名认证费", ordOrder.getRealCertificateFee());
            map.put("税费", ordOrder.getTaxFee());
            map.put("实际结算金额", ordOrder.getActualAmount());
            map.put("下单时间", ordOrder.getOrderTime());
            map.put("订单状态", ordOrder.getStatus());
            map.put("收货人", ordOrder.getReceiver());
            map.put("国家代码", ordOrder.getCountryCode());
            map.put("省市代码", ordOrder.getProvinceCode());
            map.put("城市代码", ordOrder.getCityCode());
            map.put("地区代码", ordOrder.getDistrictCode());
            map.put("联系地址", ordOrder.getAddress());
            map.put("完整地址", ordOrder.getFullAddress());
            map.put("邮政编码", ordOrder.getZipcode());
            map.put("移动电话", ordOrder.getMobile());
            map.put("电话号码", ordOrder.getTel());
            map.put("联系人电话", ordOrder.getContactMobile());
            map.put("邮件", ordOrder.getEmail());
            map.put("支付类别(00-货到付款;10-在线支付;20-金融支付)", ordOrder.getPayType());
            map.put("支付方法(01-现金;02-刷卡;11-银联;12-网上银行;13-微信支付;14-支付宝支付;21-金融支付)", ordOrder.getPayMethod());
            map.put("支付时间", ordOrder.getPayTime());
            map.put("支付结果", ordOrder.getPayResult());
            map.put("预支付交易会话标识", ordOrder.getPrepayId());
            map.put("二维码链接", ordOrder.getQrCodeUrl());
            map.put("发票类型(00-不开发票;10-普通发票;20-电子发票;30-增值税发票)", ordOrder.getInvoiceType());
            map.put("备注", ordOrder.getRemark());
            map.put("配送方式(00-自提;10-快递上门)", ordOrder.getFreightType());
            map.put("预计配送日期", ordOrder.getPredictFreightDate());
            map.put("预计配送时间", ordOrder.getPredictFreightTime());
            map.put("时间送达时间", ordOrder.getActualFreightTime());
            map.put("承运人ID", ordOrder.getFreighterId());
            map.put("承运人", ordOrder.getFreighter());
            map.put("货运单号", ordOrder.getFreightNo());
            map.put("物流公司配送站代码", ordOrder.getSiteCode());
            map.put("物流公司配送站名称", ordOrder.getSiteName());
            map.put("合作伙伴ID(处方来源者在益药宝系统ID)", ordOrder.getPartnerId());
            map.put("JD订单号", ordOrder.getJdOrderId());
            map.put("父订单号", ordOrder.getParentOrderId());
            map.put("JD父订单号", ordOrder.getJdParentOrderId());
            map.put("JD订单类型(22-SOP；23-LBP ；25-SOPL)", ordOrder.getJdOrderType());
            map.put("JD用户名", ordOrder.getJdPin());
            map.put("供应商ID", ordOrder.getVenderId());
            map.put("供应商备注", ordOrder.getVenderRemark());
            map.put("JD订单来源", ordOrder.getJdOrderSource());
            map.put("余额支付金额", ordOrder.getBalanceUsed());
            map.put("用户最终支付的金额(订单总金额-优惠+商品运费)", ordOrder.getOrderPayment());
            map.put("订单结束时间", ordOrder.getOrderEndTime());
            map.put("订单状态描述", ordOrder.getJdOrderStateRemark());
            map.put("国家名称", ordOrder.getCountryName());
            map.put("省市名称", ordOrder.getProvinceName());
            map.put("城市名称", ordOrder.getCityName());
            map.put("区域名称", ordOrder.getDistrictName());
            map.put("街镇", ordOrder.getTownCode());
            map.put("街镇名称", ordOrder.getTownName());
            map.put("JD支付方式（10货到付款, 20邮局汇款, 30自提, 40在线支付, 50公司转账, 60银行卡转账,70商保支付）", ordOrder.getJdPayType());
            map.put("发票信息", ordOrder.getInvoiceInfo());
            map.put("是否已开发票(0-否;1-是)", ordOrder.getIsInvoiceIssued());
            map.put("是否已核销(0-否;1-是)", ordOrder.getIsRepaid());
            map.put("保税区信息", ordOrder.getCustoms());
            map.put("保税模型", ordOrder.getCustomsModel());
            map.put("送货时间类型(10-只工作日送货(双休日、假日不用送);20-只双休日、假日送货(工作日不用送);30-工作日、双休日与假日均可送货;其他值-返回“任意时间”)", ordOrder.getDeliveryType());
            map.put("物流公司ID", ordOrder.getLogisticsId());
            map.put("JD物流公司ID", ordOrder.getJdLogisticsId());
            map.put("JD仓单", ordOrder.getJdStoreOrder());
            map.put("JD修改时间", ordOrder.getJdModified());
            map.put("同步状态位", ordOrder.getSynStatus());
            map.put("售后订单标记(0:不是换货订单 1返修发货,直接赔偿,客服补件 2售后调货)", ordOrder.getReturnOrder());
            map.put("KJT系统订单号", ordOrder.getKjtSosysNo());
            map.put("KJT计算的运费金额", ordOrder.getKjtShippingAmount());
            map.put("是否删除(0-否;1-是)", ordOrder.getIsDelete());
            map.put("特殊要求", ordOrder.getCustomerRequirement());
            map.put("获得的积分", ordOrder.getObtainPoint());
            map.put("消耗积分", ordOrder.getConsumePoint());
            map.put("兑换的金额", ordOrder.getConsumeAmount());
            map.put("订单是否回写EBS(0-否;1-是)", ordOrder.getIsReturnEbs());
            map.put("最后打印时间", ordOrder.getPrintTime());
            map.put("结算批次号", ordOrder.getSettleBatchNo());
            map.put("金融消费金额", ordOrder.getFinanceConsumeAmount());
            map.put("是否多次配送（0. 否  1.是）", ordOrder.getIsMultipleDelivery());
            map.put("取消原因", ordOrder.getCancelRemark());
            map.put("合并配送单ID", ordOrder.getDeliveryBillId());
            map.put("门店备注", ordOrder.getSellerRemark());
            map.put("是否首单(0-否；1-是)", ordOrder.getIsFirst());
            map.put("是否拒收(0-否；1-是)", ordOrder.getRejectFlag());
            map.put("拣货单需打印次数", ordOrder.getPickingbillRequireNum());
            map.put("拣货单已打印次数", ordOrder.getPickingbillPrintNum());
            map.put("门店三联单需打印次数", ordOrder.getTrigeminybillRequireNum());
            map.put("门店三联单已打印次数", ordOrder.getTrigeminybillPrintNum());
            map.put("发货单需打印次数", ordOrder.getSendgoodsbillRequireNum());
            map.put("发货单已打印次数", ordOrder.getSendgoodsbillPrintNum());
            map.put("运单需打印次数", ordOrder.getWaybillRequireNum());
            map.put("运单已打印次数", ordOrder.getWaybillPrintNum());
            map.put("需打印次数扩展字段1", ordOrder.getRequireNumExt1());
            map.put("已打印次数扩展字段1", ordOrder.getPrintNumExt1());
            map.put("需打印次数扩展字段2", ordOrder.getRequireNumExt2());
            map.put("已打印次数扩展字段2", ordOrder.getPrintNumExt2());
            map.put("需打印次数扩展字段3", ordOrder.getRequireNumExt3());
            map.put("已打印次数扩展字段3", ordOrder.getPrintNumExt3());
            map.put("需打印次数扩展字段4", ordOrder.getRequireNumExt4());
            map.put("已打印次数扩展字段4", ordOrder.getPrintNumExt4());
            map.put("需打印次数扩展字段5", ordOrder.getRequireNumExt5());
            map.put("已打印次数扩展字段5", ordOrder.getPrintNumExt5());
            map.put("产线", ordOrder.getPipelineCode());
            map.put("是否自动发药", ordOrder.getIsAutoDispensing());
            map.put("发药机标识", ordOrder.getDispensingId());
            map.put("发货仓库ID", ordOrder.getWarehouseId());
            map.put("订单重量(KG)", ordOrder.getOrdWeight());
            map.put("创建时间", ordOrder.getCreateTime());
            map.put("创建人(记录帐号）", ordOrder.getCreateUser());
            map.put("更新时间", ordOrder.getUpdateTime());
            map.put("更新人(记录帐号）", ordOrder.getUpdateUser());
            map.put("处方导入时间", ordOrder.getPrsImportTime());
            map.put("自动发送消息(00-不发送；10-发送)", ordOrder.getAutomationMessage());
            map.put("用药医院id", ordOrder.getHospitalId());
            map.put("用药医院名称", ordOrder.getHospitalName());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }
}
