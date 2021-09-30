package co.yixiang.modules.hospitaldemand.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import co.yixiang.annotation.AnonymousAccess;

import co.yixiang.common.utils.QueryHelpPlus;
import co.yixiang.constant.ShopConstants;
import co.yixiang.enums.OrderSourceEnum;
import co.yixiang.enums.OrderStatusEnum;
import co.yixiang.enums.ProjectNameEnum;
import co.yixiang.exception.BadRequestException;
import co.yixiang.modules.hospitaldemand.entity.AttrDTO;
import co.yixiang.modules.hospitaldemand.entity.InternetHospitalDemand;
import co.yixiang.modules.hospitaldemand.entity.InternetHospitalDemandDetail;
import co.yixiang.modules.hospitaldemand.mapper.InternetHospitalDemandMapper;
import co.yixiang.modules.hospitaldemand.service.InternetHospitalDemandDetailService;
import co.yixiang.modules.hospitaldemand.service.InternetHospitalDemandService;
import co.yixiang.modules.hospitaldemand.web.param.InternetHospitalDemandOrderParam;
import co.yixiang.modules.hospitaldemand.web.param.InternetHospitalDemandQueryParam;
import co.yixiang.modules.hospitaldemand.web.vo.InternetHospitalCart;
import co.yixiang.modules.hospitaldemand.web.vo.InternetHospitalDemandQueryVo;
import co.yixiang.common.service.impl.BaseServiceImpl;
import co.yixiang.common.web.vo.Paging;
import co.yixiang.modules.order.entity.YxStoreOrder;
import co.yixiang.modules.order.service.YxStoreOrderService;
import co.yixiang.modules.order.web.dto.CacheDTO;
import co.yixiang.modules.shop.entity.YxStoreCart;
import co.yixiang.modules.shop.entity.YxStoreProduct;
import co.yixiang.modules.shop.entity.YxStoreProductAttrValue;
import co.yixiang.modules.shop.service.YxStoreCartService;
import co.yixiang.modules.shop.service.YxStoreProductAttrValueService;
import co.yixiang.modules.shop.service.YxStoreProductService;
import co.yixiang.modules.user.entity.YxUser;
import co.yixiang.modules.user.entity.YxWechatUser;
import co.yixiang.modules.user.service.YxUserService;
import co.yixiang.modules.user.service.YxWechatUserService;
import co.yixiang.modules.xikang.entity.XikangMedMapping;
import co.yixiang.modules.xikang.service.XikangMedMappingService;
import co.yixiang.modules.xikang.service.XkProcessService;
import co.yixiang.mp.domain.DictDetail;
import co.yixiang.mp.rest.param.DictDetailQueryParam;
import co.yixiang.mp.service.DictDetailService;
import co.yixiang.mp.service.WxMpTemplateMessageService;
import co.yixiang.mp.service.YxTemplateService;
import co.yixiang.mp.service.dto.OrderTemplateMessage;
import co.yixiang.mp.utils.JsonUtils;
import co.yixiang.rabbitmq.send.MqProducer;
import co.yixiang.tools.service.impl.SmsServiceImpl;
import co.yixiang.utils.Base64Util;
import co.yixiang.utils.FileUtil;
import co.yixiang.utils.ImageUtil;
import co.yixiang.utils.OrderUtil;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;


/**
 * <p>
 * 互联网医院导入的需求单 服务实现类
 * </p>
 *
 * @author visa
 * @since 2020-12-04
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class InternetHospitalDemandServiceImpl extends BaseServiceImpl<InternetHospitalDemandMapper, InternetHospitalDemand> implements InternetHospitalDemandService {

    @Autowired
    private InternetHospitalDemandMapper internetHospitalDemandMapper;

    @Autowired
    private InternetHospitalDemandDetailService internetHospitalDemandDetailService;

    @Autowired
    private YxStoreProductAttrValueService productAttrValueService;

    @Autowired
    @Lazy
    private YxStoreCartService yxStoreCartService;

    @Autowired
    private YxStoreProductService yxStoreProductService;

    @Autowired
    private WxMpTemplateMessageService wxMpTemplateMessageService;

    @Value("${file.path}")
    private String filePath;

    @Value("${file.localUrl}")
    private String localUrl;

    @Autowired
    private XikangMedMappingService xikangMedMappingService;

    @Autowired
    private YxStoreOrderService yxStoreOrderService;

    @Autowired
    private YxWechatUserService wechatUserService;
    @Autowired
    YxTemplateService templateService;

    @Autowired
    @Lazy
    private XkProcessService xkProcessService;

    @Autowired
    private DictDetailService dictDetailService;

    @Autowired
    private SmsServiceImpl smsService;

    @Autowired
    private YxUserService yxUserService;

/*    @Autowired
    private SendPrsProducer sendPrsProducer;*/



    @Override
    public InternetHospitalDemandQueryVo getInternetHospitalDemandById(Serializable id) throws Exception{
        return internetHospitalDemandMapper.getInternetHospitalDemandById(id);
    }


    @Override
    public Paging<InternetHospitalDemand> getInternetHospitalDemandPageList(InternetHospitalDemandQueryParam internetHospitalDemandQueryParam) throws Exception{
        Page page = setPageParam(internetHospitalDemandQueryParam,OrderItem.desc("create_time"));
        QueryWrapper queryWrapper = QueryHelpPlus.getPredicate(InternetHospitalDemand.class, internetHospitalDemandQueryParam);
        IPage<InternetHospitalDemand> iPage = internetHospitalDemandMapper.selectPage(page,queryWrapper);

        for(InternetHospitalDemand demand:iPage.getRecords()) {
            List<InternetHospitalDemandDetail> details = internetHospitalDemandDetailService.list(new QueryWrapper<InternetHospitalDemandDetail>().eq("demand_id",demand.getId()));
            for(InternetHospitalDemandDetail detail :details) {
                QueryWrapper queryWrapper1 =  new QueryWrapper<>().eq("yiyaobao_sku",detail.getYiyaobaoSku());
                queryWrapper1.select("image");
                YxStoreProduct yxStoreProduct =  yxStoreProductService.getOne(queryWrapper1,false);
                if(yxStoreProduct != null) {
                    detail.setDrugImage(yxStoreProduct.getImage());
                }
            }
            demand.setDrugs(details);
        }



        return new Paging(iPage);
    }

    @Override
    public YxStoreOrder saveDemand(InternetHospitalDemand internetHospitalDemand) {
        YxStoreOrder yxStoreOrder = null;

        String picWebUrl = "";
        if(StrUtil.isNotBlank(internetHospitalDemand.getPrescriptionPdf())) {
            // pdf base64  转换成图片
            // 1.base64 转 pdf
            String dateStr = DateUtil.format(new Date(), DatePattern.PURE_DATETIME_FORMAT);
            String pdfPath = filePath + "prescriptionPdf" + File.separator + internetHospitalDemand.getPrescriptionCode()+"_"+ dateStr + ".pdf";
            String pdfWebUrl = localUrl + "/file/prescriptionPdf/" + internetHospitalDemand.getPrescriptionCode()+"_"+ dateStr + ".pdf";
            Base64Util.base64StringToFile(internetHospitalDemand.getPrescriptionPdf(),pdfPath);

            // 2.pdf 转成图片
            String picPath = filePath + "prescriptionPdf" + File.separator + internetHospitalDemand.getPrescriptionCode()+"_"+ dateStr ;
            ImageUtil.pdf2Pic(pdfPath,picPath);
            picWebUrl = localUrl + "/file/prescriptionPdf/" + internetHospitalDemand.getPrescriptionCode()+"_"+ dateStr + "0.png";

            internetHospitalDemand.setImage(picWebUrl);

            internetHospitalDemand.setPrescriptionPdf(pdfWebUrl);
        }


        // 解析 attr
        if(StrUtil.isNotBlank(internetHospitalDemand.getAttrs())) {
            AttrDTO attrDTO = JSONUtil.toBean(internetHospitalDemand.getAttrs(), AttrDTO.class);
            internetHospitalDemand.setCardNumber(attrDTO.getCardNumber());
            internetHospitalDemand.setCardType(attrDTO.getCardType());
            internetHospitalDemand.setProjectCode(attrDTO.getProjectCode());
            internetHospitalDemand.setUid(attrDTO.getUid());
            internetHospitalDemand.setOrderNumber(attrDTO.getOrderNumber());

        }


        if(StrUtil.isNotBlank(internetHospitalDemand.getOrderId())) {
            yxStoreOrder = yxStoreOrderService.getOne(new QueryWrapper<YxStoreOrder>().eq("order_id",internetHospitalDemand.getOrderId()),false);
            if(yxStoreOrder != null) {
                internetHospitalDemand.setCardNumber(yxStoreOrder.getCardNumber());
                internetHospitalDemand.setCardType(yxStoreOrder.getCardType());
                internetHospitalDemand.setProjectCode(yxStoreOrder.getProjectCode());
                internetHospitalDemand.setUid(yxStoreOrder.getUid());
                internetHospitalDemand.setOrderNumber(yxStoreOrder.getTaipingOrderNumber());

                internetHospitalDemand.setIsUse(1);
                internetHospitalDemand.setOrderId(internetHospitalDemand.getOrderId());

                UpdateWrapper updateWrapper = new UpdateWrapper();
                updateWrapper.eq("order_id",internetHospitalDemand.getOrderId());
                updateWrapper.set("need_internet_hospital_prescription",2);
                updateWrapper.set("image_path",picWebUrl);
                yxStoreOrderService.update(updateWrapper);

            }
        }


        internetHospitalDemand.setCreateTime(new Date());
        save(internetHospitalDemand);

        for(InternetHospitalDemandDetail demandDetail: internetHospitalDemand.getDrugs()) {
            demandDetail.setDemandId(internetHospitalDemand.getId());
            demandDetail.setPrescriptionCode(internetHospitalDemand.getPrescriptionCode());
            // 获取商城的药品sku
            QueryWrapper queryWrapper1 = new QueryWrapper();
            queryWrapper1.eq("xikang_code",demandDetail.getDrugCode());
            XikangMedMapping xikangMedMapping = xikangMedMappingService.getOne(queryWrapper1,false);
            if(xikangMedMapping == null) {
                log.error("药品编码["+ demandDetail.getDrugCode() +"]没有找到映射数据");
                throw new BadRequestException("药品编码["+ demandDetail.getDrugCode() +"]没有找到映射数据");
            }
            // 获取 药品唯一码
            QueryWrapper queryWrapper = new QueryWrapper();
            queryWrapper.eq("yiyaobao_sku",xikangMedMapping.getYiyaobaoSku());
            queryWrapper.eq("suk", ShopConstants.STORENAME_GUANGZHOU_CLOUD);
            YxStoreProductAttrValue yxStoreProductAttrValue =  productAttrValueService.getOne(queryWrapper,false);
            String unique = "" ;
            if(yxStoreProductAttrValue != null) {
                unique = yxStoreProductAttrValue.getUnique();
            }
            demandDetail.setProductAttrUnique(unique);
            demandDetail.setYiyaobaoSku(xikangMedMapping.getYiyaobaoSku());
        }
         internetHospitalDemandDetailService.saveBatch(internetHospitalDemand.getDrugs());
        if( StrUtil.isBlank(internetHospitalDemand.getOrderId())) {
            // 小程序订阅通知
            try {
                YxWechatUser wechatUser = wechatUserService.getOne(new QueryWrapper<YxWechatUser>().eq("uid",internetHospitalDemand.getUid()));
                if (ObjectUtil.isNotNull(wechatUser)) {
                    //公众号与小程序打通统一公众号模板通知
                    if (StrUtil.isNotBlank(wechatUser.getRoutineOpenid())) {
                        String page = "/pages/ShoppingCart/submitOrder?prescriptionId="+internetHospitalDemand.getId()+"&type=2";
                        OrderTemplateMessage message = new OrderTemplateMessage();
                        message.setOrderDate( DateUtil.now());
                        message.setOrderId(internetHospitalDemand.getPrescriptionCode());
                        message.setOrderStatus("已收到处方");
                        message.setRemark("请点击，完善需求单信息。");
                        templateService.sendDYTemplateMessage(wechatUser.getRoutineOpenid(),page,message);
                    }
                }
            } catch (Exception e) {
                log.info("发送小程序订阅通知失败!");
            }
            YxUser yxUser = yxUserService.getById(internetHospitalDemand.getUid());
            if(yxUser != null && StrUtil.isNotBlank(yxUser.getPhone())) {
                // 发送短信通知
                String remindmessage = "【益药】已收到互联网医院电子处方，请尽快到益药商-我的处方中完善需求单。处方编号：%s";
                remindmessage = String.format(remindmessage, internetHospitalDemand.getPrescriptionCode());
                smsService.sendTeddy("",remindmessage,yxUser.getPhone());
            }


        } else {
            xkProcessService.payNotice(internetHospitalDemand.getPrescriptionCode());
        }

        return yxStoreOrder;
    }

    @Override
    public InternetHospitalCart generateCart(Integer demandId) {
        InternetHospitalDemand demand = getById(demandId);
        if(demand == null) {
            throw new BadRequestException("处方单无法找到");
        }else if(demand.getIsUse() != null &&  demand.getIsUse() == 1) {
            InternetHospitalCart result = new InternetHospitalCart();
            result.setCartIds("");
            result.setImagePath(demand.getImage());
            result.setOrderSource(OrderSourceEnum.internetHospital.getValue());
            result.setCardNumber(demand.getCardNumber());
            result.setCardType(demand.getCardType());
            result.setOrderNumber(demand.getOrderNumber());
            result.setProjectCode(demand.getProjectCode());
            result.setDemandId(demandId);
            result.setOrderId(demand.getOrderId());
            result.setIsUse(demand.getIsUse());
            return result;
        }

        // 先删除旧的记录
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("project_code",String.valueOf(demandId));
       // queryWrapper.eq("uid",demand.getUid());
        yxStoreCartService.remove(queryWrapper);


        List<Integer> cartIdsList = new ArrayList<>();
        List<InternetHospitalDemandDetail> demandDetailList = internetHospitalDemandDetailService.list(new QueryWrapper<InternetHospitalDemandDetail>().eq("demand_id",demandId));
        for(InternetHospitalDemandDetail demandDetail:demandDetailList) {
            String productAttrUnique = demandDetail.getProductAttrUnique();
            YxStoreProductAttrValue attrValue = productAttrValueService.getOne(new QueryWrapper<YxStoreProductAttrValue>().eq("`unique`",productAttrUnique),false);
            if(attrValue != null) {
                Integer productId = attrValue.getProductId();

                YxStoreCart storeCart = new YxStoreCart();

                storeCart.setBargainId(0);
                storeCart.setCartNum(demandDetail.getDrugNum());
                storeCart.setCombinationId(0);
                storeCart.setProductAttrUnique(productAttrUnique);
                storeCart.setProductId(productId);
                storeCart.setSeckillId(0);
                storeCart.setType("product");
                storeCart.setUid(demand.getUid());
                storeCart.setIsNew(0);

                storeCart.setStoreId(attrValue.getStoreId());
                storeCart.setDepartCode("");
                storeCart.setProjectCode(String.valueOf(demandId));
                storeCart.setPartnerCode("");
                storeCart.setRefereeCode("");

                //判断是否已经添加过
                storeCart.setAddTime(OrderUtil.getSecondTimestampTwo());
                yxStoreCartService.save(storeCart);

                cartIdsList.add(storeCart.getId().intValue());
            }
        }

        String cartIds = CollUtil.join(cartIdsList,",");

        InternetHospitalCart result = new InternetHospitalCart();
        result.setCartIds(cartIds);
        result.setImagePath(demand.getImage());
        result.setOrderSource(OrderSourceEnum.internetHospital.getValue());
        result.setCardNumber(demand.getCardNumber());
        result.setCardType(demand.getCardType());
        result.setOrderNumber(demand.getOrderNumber());
        result.setProjectCode(demand.getProjectCode());
        result.setDemandId(demandId);
        result.setIsUse(demand.getIsUse());
        return result;
    }

    @Override
    public InternetHospitalCart queryInternetHospitalPrescriptionImage(Integer uid, String orderKey) {

        CacheDTO cacheDTO = yxStoreOrderService.getCacheOrderInfo(uid,orderKey);
        String projectCode = cacheDTO.getOther().getProjectCode();
        String cardNumber = cacheDTO.getOther().getCardNumber();
        String cardType = cacheDTO.getOther().getCardType();
        String orderNumber = cacheDTO.getOther().getOriginalOrderNo();

        InternetHospitalCart internetHospitalCart = new InternetHospitalCart();
        internetHospitalCart.setImagePath("https://test.yiyao-mall.com/api/file/pic/20201222142524989580.jpg");
        internetHospitalCart.setProjectCode(projectCode);
        internetHospitalCart.setOrderNumber(orderNumber);
        internetHospitalCart.setCardType(cardType);
        internetHospitalCart.setCardNumber(cardNumber);

        return internetHospitalCart;
    }

    @Override
    public Boolean noticeDemand(InternetHospitalDemandOrderParam orderParam) {

        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("order_id",orderParam.getOrderId());
        int count = yxStoreOrderService.count(queryWrapper);
        if(count >0) {
            UpdateWrapper updateWrapper = new UpdateWrapper();
            updateWrapper.eq("order_id",orderParam.getOrderId());
            updateWrapper.set("internet_hospital_notice_flag",orderParam.getApplyFlag());

            if( orderParam.getApplyFlag() != null && orderParam.getApplyFlag() == 3) {
                updateWrapper.set("check_fail_reason",orderParam.getReason());
                updateWrapper.set("check_fail_remark","互联网医院处方申请驳回");
                updateWrapper.set("check_time",new Date());
                updateWrapper.set("check_status","不通过");
                updateWrapper.set("need_refund",1);
                updateWrapper.set("status",OrderStatusEnum.STATUS_6.getValue());


                // 发送短信，通知管理员处理退款
                //todo 推送
                DictDetailQueryParam dictDetailQueryParam = new DictDetailQueryParam();
                dictDetailQueryParam.setName("guangZhouManger");
                List<DictDetail> phoneList =  dictDetailService.getDictDetailList(dictDetailQueryParam);
                //发送短信
                for(DictDetail detail :phoneList) {
                    if(StrUtil.isNotBlank(detail.getValue())) {
                        String remindmessage = "【益药】您有订单待处理，订单状态：%s。订单编号：%s";
                        remindmessage = String.format(remindmessage, "申请退款", orderParam.getOrderId());
                        smsService.sendTeddy("",remindmessage,detail.getValue());
                    }
                }
            }

            // 处方申请成功,更改状态是 待开具处方
            if(orderParam.getApplyFlag() != null && orderParam.getApplyFlag() == 1) {
                updateWrapper.set("status", OrderStatusEnum.STATUS_13.getValue());
            }

            return yxStoreOrderService.update(updateWrapper);
        } else {
            return true;
        }



    }
}
