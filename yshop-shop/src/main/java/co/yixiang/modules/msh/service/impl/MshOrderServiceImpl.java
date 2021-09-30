/**
* Copyright (C) 2018-2020
* All rights reserved, Designed By www.yixiang.co
* 注意：
* 本软件为www.yixiang.co开发研制，未经购买不得使用
* 购买后可获得全部源代码（禁止转卖、分享、上传到码云、github等开源平台）
* 一经发现盗用、分享等行为，将追究法律责任，后果自负
*/
package co.yixiang.modules.msh.service.impl;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;

import cn.hutool.core.util.ObjectUtil;
import co.yixiang.constant.ShopConstants;
import co.yixiang.constant.SystemConfigConstants;
import co.yixiang.enums.MSHOrderChangeTypeEnum;
import co.yixiang.enums.ProjectNameEnum;
import co.yixiang.modules.api.common.Result;
import co.yixiang.modules.msh.service.enume.MshStatusEnum;
import co.yixiang.modules.shop.domain.*;
import co.yixiang.modules.shop.service.ProjectService;
import co.yixiang.modules.shop.service.YxStoreOrderStatusService;
import co.yixiang.modules.taibao.service.vo.ClaimEventVo;
import co.yixiang.modules.taibao.util.MyBeanUtils;
import co.yixiang.modules.yiyaobao.service.OrderServiceImpl;
import co.yixiang.rabbitmq.send.MqProducer;
import co.yixiang.utils.*;
import co.yixiang.mp.yiyaobao.enums.YiyaobaoPayMethodEnum;
import co.yixiang.mp.yiyaobao.enums.YiyaobaoPayTypeEnum;
import co.yixiang.mp.yiyaobao.service.dto.YiyaobaoOrderInfo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.github.pagehelper.PageInfo;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import co.yixiang.common.service.impl.BaseServiceImpl;
import co.yixiang.common.utils.QueryHelpPlus;
import co.yixiang.dozer.service.IGenerator;
import co.yixiang.enums.OrderStatusEnum;
import co.yixiang.exception.BadRequestException;
import co.yixiang.exception.ErrorRequestException;
import co.yixiang.modules.msh.domain.MshDemandList;
import co.yixiang.modules.msh.domain.MshDemandListFile;
import co.yixiang.modules.msh.domain.MshOrder;
import co.yixiang.modules.msh.domain.MshOrderItem;
import co.yixiang.modules.msh.domain.MshRepurchaseReminder;
import co.yixiang.modules.msh.service.MshOrderService;
import co.yixiang.modules.msh.service.dto.MshDemandListItemDto;
import co.yixiang.modules.msh.service.dto.MshOrderDto;
import co.yixiang.modules.msh.service.dto.MshOrderQueryCriteria;
import co.yixiang.modules.msh.service.dto.ServiceResult;
import co.yixiang.modules.msh.service.mapper.MshDemandListItemMapper;
import co.yixiang.modules.msh.service.mapper.MshDemandListMapper;
import co.yixiang.modules.msh.service.mapper.MshOrderItemMapper;
import co.yixiang.modules.msh.service.mapper.MshOrderMapper;
import co.yixiang.modules.msh.service.mapper.MshRepurchaseReminderMapper;
import co.yixiang.modules.shop.service.Product4projectService;
import co.yixiang.modules.shop.service.YxSystemStoreService;
import co.yixiang.modules.shop.service.mapper.StoreProductMapper;
import co.yixiang.modules.shop.service.mapper.SystemStoreMapper;
import co.yixiang.modules.shop.service.param.ExpressParam;
import co.yixiang.modules.yiyaobao.dto.OrderResultDTO;
import co.yixiang.modules.yiyaobao.dto.Prescription;
import co.yixiang.modules.yiyaobao.dto.PrescriptionDTO;
import co.yixiang.modules.yiyaobao.dto.PrescriptionDetail;
import co.yixiang.modules.yiyaobao.utils.CryptUtils;
import co.yixiang.mp.yiyaobao.service.mapper.OrdOrderMapper;
import co.yixiang.mp.yiyaobao.vo.OrderVo;
import co.yixiang.tools.express.dao.ExpressInfo;
import co.yixiang.tools.express.dao.Traces;
import co.yixiang.tools.utils.AppSiganatureUtils;
import co.yixiang.tools.utils.HttpUtils;
import lombok.extern.slf4j.Slf4j;

/**
* @author cq
* @date 2020-12-25
*/
@Slf4j
@Service
public class MshOrderServiceImpl extends BaseServiceImpl<MshOrderMapper, MshOrder> implements MshOrderService {
	@Autowired
    private IGenerator generator;

	@Autowired
    private YxSystemStoreService yxSystemStoreService;

    @Autowired
    private MshOrderMapper mshOrderMapper;

    @Autowired
    private MshOrderItemMapper mshOrderItemMapper;

    @Autowired
    private StoreProductMapper storeProductMapper;

    @Autowired
    private MshDemandListMapper mshDemandListMapper;

    @Autowired
    private OrdOrderMapper ordOrderMapper;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private Product4projectService product4projectService;

    @Autowired
    private SystemStoreMapper systemStoreMapper;

    @Autowired
    private OrdOrderMapper yiyaobaoOrdOrderMapper;

    @Autowired
    private MshRepurchaseReminderMapper mshRepurchaseReminderMapper;

    @Autowired
    private MshDemandListItemMapper mshDemandListItemMapper;

	@Autowired
	private ProjectService projectService;

    @Value("${yiyaobao.needEncrypt}")
    private Boolean needEncrypt ;

    @Value("${yiyaobao.appId}")
    private String appId;

    @Value("${yiyaobao.appSecret}")
    private String appSecret;

    @Value("${yiyaobao.addSingleUrl}")
    private String addSingleUrl;

    @Value("${yiyaobao.apiUrlExternal}")
    private String yiyaobao_apiUrl_external;

    @Value("${yiyaobao.apiUrl}")
    private String apiUrl;

    @Value("${yiyaobao.orderLogisticsByOrderIdUrl}")
    private String orderLogisticsByOrderIdUrl;

	@Value("${yiyaobao.mshGetProcessByOrderId}")
	private String mshGetProcessByOrderId;

    @Autowired
    private MqProducer mqProducer;

    @Value("${msh.delayQueueName}")
    private String mshQueueName;

    @Autowired
    private OrderServiceImpl yiyaobaoOrderService;

    @Autowired
    private YxStoreOrderStatusService yxStoreOrderStatusService;

    @Override
    //@Cacheable
    public Map<String, Object> queryAll(MshOrderQueryCriteria criteria, Pageable pageable) {
        getPage(pageable);
        PageInfo<MshOrder> page = new PageInfo<>(queryAll(criteria));
        Map<String, Object> map = new LinkedHashMap<>(2);
        map.put("content", generator.convert(page.getList(), MshOrderDto.class));
        map.put("totalElements", page.getTotal());
        return map;
    }


    @Override
    //@Cacheable
    public List<MshOrder> queryAll(MshOrderQueryCriteria criteria){
        return baseMapper.selectList(QueryHelpPlus.getPredicate(MshOrder.class, criteria));
    }


    @Override
    public void download(List<MshOrderDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (MshOrderDto mshOrder : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("需求单主表ID", mshOrder.getDemandListId());
            map.put("订单状态", mshOrder.getOrderStatus());
            map.put("药房名称", mshOrder.getDrugstoreName());
            map.put("药房id", mshOrder.getDrugstoreId());
            map.put("创建时间", mshOrder.getCreateTime());
            map.put("物流单号", mshOrder.getLogisticsNum());
            map.put("物流状态", mshOrder.getLogisticsStatus());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }


	@Override
	public ServiceResult<Boolean> makeOrder(JSONObject jsonObject) {

		ServiceResult<Boolean> serviceResult = new ServiceResult<>();
		String jsonStr = jsonObject.toString();
		List<MshDemandListItemDto> mshDemandListItemDtoList = JSONObject.parseArray(JSON.parseObject(jsonStr).getString("OrderList"), MshDemandListItemDto.class);
		if(mshDemandListItemDtoList!=null&&mshDemandListItemDtoList.size()>0){
			MshDemandList mshDemandList= mshDemandListMapper.selectById(mshDemandListItemDtoList.get(0).getDemandListId());
			if(mshDemandList==null){
				serviceResult.setOk(false);
				serviceResult.setMsg("未找到需求单。");
				return serviceResult;
			}
			if(mshDemandList.getLssueStatus()==1){
				serviceResult.setOk(false);
				serviceResult.setMsg("该需求单已下发，不能添加订单。");
				return serviceResult;
			}
            Integer allNum = mshDemandListItemMapper.getCountByMshDemandListId(mshDemandListItemDtoList.get(0).getDemandListId());
			Integer oreadyNum=mshOrderMapper.getCountByMshDemandListId(mshDemandListItemDtoList.get(0).getDemandListId());
			if((oreadyNum+mshDemandListItemDtoList.size())>allNum){
                serviceResult.setOk(false);
                serviceResult.setMsg("该需求单已生成订单明细总条数："+oreadyNum+"，现新增订单明细条数："+mshDemandListItemDtoList.size()+",订单总条数不能大于需求单明细条数："+allNum);
                return serviceResult;
            }
			String username = SecurityUtils.getUsername();

			//插入订单表
			MshOrder mshOrder = new MshOrder();
			mshOrder.setDemandListId(mshDemandListItemDtoList.get(0).getDemandListId());
			mshOrder.setDrugstoreId(mshDemandListItemDtoList.get(0).getDrugstoreId());
			mshOrder.setDrugstoreName(mshDemandListItemDtoList.get(0).getDrugstoreName());
			mshOrder.setOrderStatus("0");
			mshOrder.setCreateTime(DateUtil.date().toTimestamp());
			mshOrder.setCreateUser(username);
			int num = mshOrderMapper.insert(mshOrder);
			if(num==0){
				serviceResult.setOk(false);
				serviceResult.setMsg("插入订单主表信息失败！");
				return serviceResult;
			}
			//插入订单子表
			for(int i = 0; i<mshDemandListItemDtoList.size(); i++){
				YxStoreProduct yxStoreProduct = storeProductMapper.selectById(mshDemandListItemDtoList.get(i).getMedId());
				//查询商品是否存在
				if(yxStoreProduct!=null){
					MshOrderItem mshOrderItem = new MshOrderItem();
					mshOrderItem.setCreateTime(DateUtil.date().toTimestamp());
					mshOrderItem.setDemandListItemId(mshDemandListItemDtoList.get(i).getId());
					mshOrderItem.setDrugstoreId(mshDemandListItemDtoList.get(i).getDrugstoreId());
					mshOrderItem.setDrugstoreName(mshDemandListItemDtoList.get(i).getDrugstoreName());
					mshOrderItem.setMedCommonName(yxStoreProduct.getCommonName());
					mshOrderItem.setMedId(yxStoreProduct.getId());
					mshOrderItem.setMedManufacturer(yxStoreProduct.getManufacturer());
					mshOrderItem.setMedName(yxStoreProduct.getStoreName());
					mshOrderItem.setMedSku(yxStoreProduct.getYiyaobaoSku());
					mshOrderItem.setMedSpec(yxStoreProduct.getSpec());
					mshOrderItem.setMedUnit(yxStoreProduct.getUnit());
					mshOrderItem.setOrderId(mshOrder.getId());
					mshOrderItem.setPurchaseQty(mshDemandListItemDtoList.get(i).getPurchaseQty());
					mshOrderItem.setUnitPrice(mshDemandListItemDtoList.get(i).getUnitPrice());
					mshOrderItemMapper.insert(mshOrderItem);
				}
			}
            mshDemandList.setUpdateTime(new Date());
            mshDemandListMapper.updateById(mshDemandList);
			/*
			String prescriptionNo = mshOrder.getId() + "_msh";
			//判断药房，发送报文
			if(ShopConstants.STORENAME_GUANGZHOU_CLOUD.equals(mshDemandListItemDtoList.get(0).getDrugstoreName())){
				//获取患者字段信息
				MshDemandList mshDemandList = mshDemandListMapper.selectById(mshDemandListItemDtoList.get(0).getDemandListId());
				//设置处方字段
		        Prescription pres = new Prescription();
		        // 病人名称 必填
		        pres.setName(mshDemandList.getPatientname());
		        // 患者手机号
		        pres.setMobile(mshDemandList.getPhone());
		        // 医生名称
		        pres.setDoctorName("汪志方");
		        // 科室名称
		        pres.setDepartment("普通全科");
		        pres.setDeptCode("200301");
		        // 医院名称 必填
		        pres.setHospitalName("益药商城");
		        // 收货信息
		        pres.setAddress(mshDemandList.getDetail());
		        pres.setProvinceName(mshDemandList.getProvince());
		        pres.setCityName(mshDemandList.getCity());
		        pres.setDistrictName(mshDemandList.getDistrict());
		        pres.setReceiver(mshDemandList.getPatientname());
		        pres.setReceiverMobile(mshDemandList.getPhone());

		        // 处方日期
		        pres.setPrescribeDate(DateUtil.date().getTime());

		        // 处方号 必填
		        pres.setPrescripNo(prescriptionNo);
		        // 挂号类别 必填
		        pres.setRegisterType(0L);
		        //费别(0:自费;1:医保)
		        pres.setFeeType("0");



		        // 付款方式(01-现金;02-刷卡;70-门店已收款)
		        pres.setPayMethod("70");

		        //配送类型(00-自提；10-送货上门；99-无需配送)
		        pres.setDeliverType("10");
		        pres.setRemark("");
		        pres.setRegisterDate(DateUtil.date().getTime());
		        pres.setRegisterType(1L);

		        // pres.setDiscount();

		          设置处方明细列表（以不同药品区分).

		        List<PrescriptionDetail> details = new ArrayList<PrescriptionDetail>();
		        // 商品原价总和
		        Double totalTruePrice = 0d;

		        // 商品会员折价总和
		        Double totalVipDiscountAmount = 0d;

		        for(int i = 0; i<mshDemandListItemDtoList.size(); i++) {
		        	YxStoreProduct yxStoreProduct = storeProductMapper.selectById(mshDemandListItemDtoList.get(i).getMedId());
		            // 药品主数据
		            if(yxStoreProduct == null) {
		                throw new BadRequestException("无法找到药品详情信息");
		            }
		            PrescriptionDetail detail = new PrescriptionDetail();
		            // 药品编码 必填
		            detail.setMedCode(yxStoreProduct.getYiyaobaoSku());
		            // 药品名称 必填
		            detail.setMedName(yxStoreProduct.getStoreName());
		            // 药品数量 必填
		            detail.setAmount(new BigDecimal(mshDemandListItemDtoList.get(i).getPurchaseQty()));
		            // 单价
		            detail.setUnitPrice( mshDemandListItemDtoList.get(i).getUnitPrice().setScale(2,BigDecimal.ROUND_HALF_UP));
		            // 折扣金额 = (原价 - 会员价)*药品数量
		          //  BigDecimal vipDiscountAmount = NumberUtil.mul( new BigDecimal(mshDemandListItemDtoList.get(i).getPurchaseQty()),  NumberUtil.sub(yxStoreProduct.getPrice(),mshDemandListItemDtoList.get(i).getUnitPrice().setScale(2,BigDecimal.ROUND_HALF_UP))).setScale(2,BigDecimal.ROUND_HALF_UP);
					BigDecimal vipDiscountAmount = new BigDecimal(0);
					detail.setDiscountAmount(vipDiscountAmount);
		            // 折扣率
		           // detail.setDiscount(NumberUtil.div(yxStoreProduct.getPrice(), mshDemandListItemDtoList.get(i).getUnitPrice().setScale(2,BigDecimal.ROUND_HALF_UP)));
					detail.setDiscount(new BigDecimal(1));
					detail.setSpec(yxStoreProduct.getSpec());
		            details.add(detail);

		            totalTruePrice = NumberUtil.add(totalTruePrice.doubleValue(), NumberUtil.mul(mshDemandListItemDtoList.get(i).getPurchaseQty().doubleValue() , mshDemandListItemDtoList.get(i).getUnitPrice().setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue() ));
		            totalVipDiscountAmount = NumberUtil.add(totalVipDiscountAmount.doubleValue(), vipDiscountAmount.doubleValue() );
		        }


		        // 设置处方明细。当一次只能上传一条明细时，调用setDetail而非setDetails;
		        pres.setDetails(details);

		        //总金额
		        pres.setTotalAmount(new BigDecimal(totalTruePrice).setScale(2,BigDecimal.ROUND_HALF_UP));

		        //已收费金额
		        pres.setPaidAmount(new BigDecimal(totalTruePrice).setScale(2,BigDecimal.ROUND_HALF_UP));

		        // 折扣金额
		        // 折扣金额 = 会员折扣金额  + 优惠券折扣金额 + 积分折扣金额
		        Double totalDiscountAmount = NumberUtil.add(totalVipDiscountAmount.doubleValue() , 0 , 0 ).doubleValue();
		        pres.setDiscountAmount(new BigDecimal(totalDiscountAmount).setScale(2,BigDecimal.ROUND_HALF_UP));

		        //折扣率(采用小数表示)
		        // 折扣率 = (原价-现价)/原价
		      //  pres.setDiscount( new BigDecimal( 1- NumberUtil.div(totalDiscountAmount,totalTruePrice)).setScale(2,BigDecimal.ROUND_HALF_UP));

				pres.setDiscount(new BigDecimal(1));
		        String url = yiyaobao_apiUrl_external + addSingleUrl;

		        String requestBody = JSONUtil.parseObj(pres).toString(); //

		        try {
		            long timestamp = System.currentTimeMillis(); // 生成签名时间戳
		            Map<String, String> headers = new HashMap<String, String>();
		            headers.put("ACCESS_APPID", appId); // 设置APP
		            headers.put("ACCESS_TIMESTAMP", String.valueOf(timestamp)); // 设置签名时间戳
		            String ACCESS_SIGANATURE = AppSiganatureUtils
		                    .createSiganature(requestBody, appId, appSecret,
		                            timestamp);
		            headers.put("ACCESS_SIGANATURE", ACCESS_SIGANATURE); // 生成并设置签名
		            log.info("ACCESS_APPID={}",appId);
		            log.info("ACCESS_TIMESTAMP={}",String.valueOf(timestamp));
		            log.info("ACCESS_SIGANATURE={}",ACCESS_SIGANATURE);
		            log.info("url={}",url);
		            log.info("requestBody={}",requestBody);
		            String result = HttpUtils.postJsonHttps(url, requestBody,
		                    headers); // 发起调用
		            log.info("下发益药宝订单，结果：{}" ,result);
		            cn.hutool.json.JSONObject object = JSONUtil.parseObj(result);


		            if(object.getBool("success")) {
		            	mshOrder.setOrderStatus("0");
						String orderId = yiyaobaoOrdOrderMapper.queryYiyaobaoOrderIdByPrescription(prescriptionNo);
						mshOrder.setYiyaobaoId(orderId);
		            	mshOrderMapper.updateById(mshOrder);
		            	serviceResult.setOk(true);

		            	//根据项目名称更新益药宝订单来源
						//yiyaobaoOrdOrderMapper.updateYiyaobaoOrderSourceByPrescripNo(prescriptionNo, "32");

						YiyaobaoOrderInfo yiyaobaoOrderInfo = new YiyaobaoOrderInfo();
						yiyaobaoOrderInfo.setOrderSource("32");
						yiyaobaoOrderInfo.setPrsNo(prescriptionNo);
						yiyaobaoOrderInfo.setPayMethod(YiyaobaoPayMethodEnum.payMethod_21.getValue());  // 金融支付
						yiyaobaoOrderInfo.setPayResult("10"); // 已支付
						yiyaobaoOrderInfo.setPayTime(DateUtil.formatDateTime(new Date()));
						yiyaobaoOrderInfo.setPayType(YiyaobaoPayTypeEnum.payType_40.getValue());

						// 更新益药宝订单的信息
						yiyaobaoOrdOrderMapper.updateYiyaobaoOrderInfoByPrescripNo(yiyaobaoOrderInfo);


		                return serviceResult;
		            }

		        } catch (Exception e) {
		            e.printStackTrace();
		        }
			}else{
				//非广州环境
		        String base64_str =  "";
		        String imagePath = mshDemandListItemDtoList.get(0).getPicUrl();
		        try {
		            if(StrUtil.isBlank(imagePath)) {
		                base64_str = new ImageUtil().localImageToBase64("otc.jpg");
		            } else {
		                //String imagePathConvert = imagePath.replace(localUrl,imageUrl);
		                String imagePathConvert = imagePath;
		                log.info("imagePathConvert === {}",imagePathConvert);
		                base64_str = ImageUtil.encodeImageToBase64(new URL(imagePathConvert));
		                //  log.info("base64_str.length==={}",base64_str.length());
		            }
		            base64_str = "data:image/jpeg;base64,"+ base64_str;
		            base64_str = URLEncoder.encode(base64_str,"UTF-8")  ;
		        } catch (Exception e) {
		            e.printStackTrace();
		        }
				//获取患者字段信息
				MshDemandList mshDemandList = mshDemandListMapper.selectById(mshDemandListItemDtoList.get(0).getDemandListId());
		        PrescriptionDTO prescriptionDTO = new PrescriptionDTO();
		        prescriptionDTO.setAddress(mshDemandList.getDetail());
		        prescriptionDTO.setCityCode(mshDemandList.getCityCode());
		        prescriptionDTO.setProvinceCode(mshDemandList.getProvinceCode());
		        prescriptionDTO.setDistrictCode(mshDemandList.getDistrictCode());

		        prescriptionDTO.setCustomerRequirement("");
		        prescriptionDTO.setPatientMobile(mshDemandList.getPhone());
		        prescriptionDTO.setPatientName(mshDemandList.getPatientname());
		        Project project = projectService.getOne(new LambdaQueryWrapper<Project>().eq(Project::getProjectCode, ProjectNameEnum.MSH.getValue()));
				if(project != null) {
					prescriptionDTO.setProjectNo(project.getYiyaobaoProjectCode());
				}

		        YxSystemStore yxSystemStore = yxSystemStoreService.getById(mshDemandListItemDtoList.get(0).getDrugstoreId());

		        if(yxSystemStore!=null){
		        	prescriptionDTO.setSellerId(yxSystemStore.getYiyaobaoId());
		        }

		        // 生成短信验证码
		        String verifyCode = generateVerifyCode2(mshDemandList.getPhone());

		        prescriptionDTO.setVerifyCode(verifyCode);
		        JSONArray jsonArray = JSONUtil.createArray();
		        for (int i = 0; i<mshDemandListItemDtoList.size(); i++) {
		        	YxStoreProduct yxStoreProduct = storeProductMapper.selectById(mshDemandListItemDtoList.get(i).getMedId());

		            cn.hutool.json.JSONObject jsonObject1 = JSONUtil.createObj();
		            jsonObject1.put("sku",yxStoreProduct.getYiyaobaoSku());
		            jsonObject1.put("unitPrice",mshDemandListItemDtoList.get(i).getUnitPrice());
		            jsonObject1.put("amount",mshDemandListItemDtoList.get(i).getPurchaseQty());

		            jsonArray.add(jsonObject1);

		        }

		        prescriptionDTO.setItems(jsonArray.toString());
		        prescriptionDTO.setImagePath(base64_str);
		        // 发送处方
		        // 获取到外部需求单
		        String orderSn = uploadOrder(prescriptionDTO);
		        //更新订单号，订单状态
		        OrderVo orderVo = yiyaobaoOrdOrderMapper.getYiyaobaoOrderbyOrderIdSample(orderSn);
		        mshOrder.setOrderStatus("0");
		        mshOrder.setExternalOrderId(orderSn);
		        mshOrder.setYiyaobaoId(orderVo.getId());
            	mshOrderMapper.updateById(mshOrder);

            	//更新插入复购信息表
            	createMshRepurchaseReminder(mshOrder.getId());

				//根据项目名称更新益药宝订单来源
				// yiyaobaoOrdOrderMapper.updateOrderSourceByOrderno(orderVo.getOrderNo(), "32");

				YiyaobaoOrderInfo yiyaobaoOrderInfo = new YiyaobaoOrderInfo();
				yiyaobaoOrderInfo.setOrderSource("32");
				yiyaobaoOrderInfo.setOrderNo(orderSn);
				yiyaobaoOrderInfo.setPayMethod(YiyaobaoPayMethodEnum.payMethod_21.getValue());  // 微信支付
				yiyaobaoOrderInfo.setPayResult("10"); // 已支付
				yiyaobaoOrderInfo.setPayTime(DateUtil.formatDateTime(new Date()));
				yiyaobaoOrderInfo.setPayType(YiyaobaoPayTypeEnum.payType_40.getValue());
				yiyaobaoOrdOrderMapper.updateYiyaobaoOrderInfoByOrderNo(yiyaobaoOrderInfo);
			}
			*/
		}else{
			serviceResult.setOk(false);
			serviceResult.setMsg("未勾选数据");
		}

		serviceResult.setOk(true);
		return serviceResult;
	}

    @DS("multi-datasource1")
    public String generateVerifyCode2(String phone){
        String verifyCode = "";

        verifyCode =  RandomUtil.randomNumbers(6);
        ordOrderMapper.updateVerifyCodeInvalid(phone);
        ordOrderMapper.insertVerifyCode(phone,verifyCode);

        return verifyCode;
    };

    public String  uploadOrder(PrescriptionDTO prescriptionDTO){
        try {
            cn.hutool.json.JSONObject jsonObject = JSONUtil.parseObj(prescriptionDTO);
            PrescriptionDTO prescriptionDTO1 = new PrescriptionDTO();
            BeanUtils.copyProperties(prescriptionDTO,prescriptionDTO1);
            prescriptionDTO1.setImagePath("");
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            MultiValueMap map = new LinkedMultiValueMap();
            log.info("发送至益药宝={}",prescriptionDTO1);
            String data = "";

            if(needEncrypt) {
                data = CryptUtils.encryptString(jsonObject.toString(), "b2ctestkey");
            }else {
                data = jsonObject.toString();
            }

            //   log.info("CryptUtils.encryptString={}",data);
            map.add("data",data);
            map.add("token","22");
            map.add("action","YM22");
            map.add("method","saveYiyaoMallPrs");
            HttpEntity request = new HttpEntity(map, headers);
            ResponseEntity<String> resultEntity = restTemplate.exchange(apiUrl, HttpMethod.POST, request, String.class);
            String body = resultEntity.getBody();
            log.info(jsonObject.toString());
            log.info(body);
            OrderResultDTO orderResultDTO = JSONUtil.toBean(body, OrderResultDTO.class);
            String orderNo = "";
            if("ok".equals(orderResultDTO.getStatus())) {
                log.info("{}",orderResultDTO);

                log.info("解密={}", CryptUtils.decryptString(orderResultDTO.getData(), "b2ctestkey", "GBK"));

                String ret = URLDecoder.decode(URLEncoder.encode(orderResultDTO.getData()));
                String jsonStr = CryptUtils.decryptString(ret, "b2ctestkey", "GBK");
                cn.hutool.json.JSONObject obj = JSONUtil.parseObj(jsonStr);
                //外部订单号
                orderNo = obj.getStr("orderNo");

            } else {

                throw new ErrorRequestException(orderResultDTO.getMsg());
            }
            return orderNo;
        }catch (Exception e) {
            e.printStackTrace();
            throw new ErrorRequestException("调用益药宝生成订单异常");
        }
	}


	@Override
	public void syncOrderStatusMsh() {
		QueryWrapper<MshOrder> queryWrapper = new QueryWrapper();
        // 状态5，已完成
        queryWrapper.notIn("order_status", OrderStatusEnum.STATUS_4.getValue());
        queryWrapper.notIn("drugstore_name", "广州上药益药药房有限公司（云药房）");
        List<MshOrder> orderList = baseMapper.selectList(queryWrapper);
        for (MshOrder order :orderList) {
        	//外部订单号
            String orderId = order.getExternalOrderId();
            log.info("MSH:"+orderId);
            if(orderId!=null&&!"".equals(orderId)){
            	OrderVo orderVo = yiyaobaoOrdOrderMapper.getYiyaobaoOrderbyOrderIdSample(orderId);
                String yiyaobaoStatus = orderVo.getStatusCode();
                Integer status ;

                if(yiyaobaoStatus.equals("01")) {  //待审核
                    status=0;


                }else if(yiyaobaoStatus.equals("20") || yiyaobaoStatus.equals("25")  || yiyaobaoStatus.equals("30") ||
                        yiyaobaoStatus.equals("31") || yiyaobaoStatus.equals("35")  || yiyaobaoStatus.equals("36") ||
                        yiyaobaoStatus.equals("38") || yiyaobaoStatus.equals("40")  || yiyaobaoStatus.equals("41") ||
                        yiyaobaoStatus.equals("42")
                ) { //待发货
                    status=3;


                } else if(yiyaobaoStatus.equals("43")){ //待收货
                    status=3;


                } else if (yiyaobaoStatus.equals("50") ){  // 已收货，待评价
                    status=4;

                } else if( yiyaobaoStatus.equals("45")) {
                    status=4;
                }
                else if(yiyaobaoStatus.equals("98")) {
                    status = 5;
                }else if(yiyaobaoStatus.equals("80")) {
                    status = 1;
                }
                else {  // 其他状态
                    status=1;

                }
                UpdateWrapper<MshOrder> updateWrapper = new UpdateWrapper<>();
                updateWrapper.set("order_status",status);
                updateWrapper.eq("id",order.getId());
                updateWrapper.set("yiyaobao_id", orderVo.getId());
                if(StrUtil.isNotBlank(orderVo.getFreightNo())) {
                    updateWrapper.set("logistics_name",orderVo.getLogisticsName());
                    updateWrapper.set("logistics_num",orderVo.getFreightNo());
                }
                update(updateWrapper);
            }
        }
      }


	//更新，插入MSH复购信息表
	public void createMshRepurchaseReminder(Integer orderId) {
		//通过订单号
		MshOrder mshOrder = mshOrderMapper.selectById(orderId);
		//查询需求单信息
		MshDemandList mshDemandList = mshDemandListMapper.selectById(mshOrder.getDemandListId());
		QueryWrapper<MshOrderItem> queryWrapper = new QueryWrapper<>();
		queryWrapper.eq("order_id", orderId);
		List<MshOrderItem> list = mshOrderItemMapper.selectList(queryWrapper);
		for(int i=0;i<list.size();i++){
			//通过商品id和患者手机号判断之前是否有数据，有的话更新，没有做插入操作
			QueryWrapper<MshRepurchaseReminder> queryWrapper2 = new QueryWrapper<>();
			queryWrapper2.eq("med_id", list.get(i).getMedId());
			queryWrapper2.eq("phone", mshDemandList.getPhone());
			List<MshRepurchaseReminder> list2 = mshRepurchaseReminderMapper.selectList(queryWrapper2);
			if(list2!=null&&list2.size()>0){
				//更新复购信息表
				//上次购药时间
				list2.get(0).setLastPurchaseDate(list.get(i).getCreateTime());

				//这次购药时间-下次购药时间
				long l = list.get(i).getCreateTime().getTime()-list2.get(0).getNextPurchaseDate().getTime();
				//疗程时间 = 用药周期*购买数量
				YxStoreProduct yxStoreProduct = storeProductMapper.selectById(list.get(i).getMedId());
				long num = Integer.valueOf(yxStoreProduct.getMedicationCycle())*list.get(i).getPurchaseQty();
				//下次购药时间
				if(list2!=null && l>0){
					//下次购药时间 = 这次购药时间+疗程时间
					list2.get(0).setNextPurchaseDate(DateUtil.date(list.get(i).getCreateTime().getTime()+3600*24*num*1000).toTimestamp());
				}else{
					//下次购药时间 = 下次购药时间+疗程时间
					list2.get(0).setNextPurchaseDate(DateUtil.date(list2.get(0).getNextPurchaseDate().getTime()+3600*24*num*1000).toTimestamp());
				}
				//购药次数
				list2.get(0).setPurchaseTimes(list2.get(0).getPurchaseTimes()+1);
				//总计购药数量
				list2.get(0).setPurchaseQty(list2.get(0).getPurchaseQty()+list.get(i).getPurchaseQty());
				//上次购药数量
				list2.get(0).setLastPurchasseQty(list.get(i).getPurchaseQty());
				//更新时间
				list2.get(0).setUpdateTime(DateUtil.date().toTimestamp());
				//商品信息
				list2.get(0).setMedCommonName(yxStoreProduct.getCommonName());
				list2.get(0).setMedCycle(Integer.valueOf(yxStoreProduct.getMedicationCycle()));
				list2.get(0).setMedName(yxStoreProduct.getStoreName());
				list2.get(0).setMedManufacturer(yxStoreProduct.getManufacturer());
				list2.get(0).setMedSku(yxStoreProduct.getYiyaobaoSku());
				list2.get(0).setUnitPrice(list.get(i).getUnitPrice());
				list2.get(0).setMedSpec(yxStoreProduct.getSpec());
				list2.get(0).setMedUnit(yxStoreProduct.getUnit());
				list2.get(0).setImage(StringUtils.isEmpty(yxStoreProduct.getImage())?null:yxStoreProduct.getImage().split(",")[0]);
				mshRepurchaseReminderMapper.updateById(list2.get(0));
			}else{
				YxStoreProduct yxStoreProduct = storeProductMapper.selectById(list.get(i).getMedId());
				//插入复购信息表

				if(yxStoreProduct.getMedicationCycle()!=null&& NumberUtil.isNumber(yxStoreProduct.getMedicationCycle())){
					MshRepurchaseReminder mshRepurchaseReminder = new MshRepurchaseReminder();
					mshRepurchaseReminder.setDrugstoreId(mshOrder.getDrugstoreId());
					mshRepurchaseReminder.setDrugstoreName(mshOrder.getDrugstoreName());
					mshRepurchaseReminder.setDrugstoreYiyaobaoId("");
					mshRepurchaseReminder.setName(mshDemandList.getPatientname());
					mshRepurchaseReminder.setPhone(mshDemandList.getPhone());
					mshRepurchaseReminder.setUserYiyaobaoId("");
					mshRepurchaseReminder.setFirstPurchaseDate(list.get(i).getCreateTime());
					mshRepurchaseReminder.setLastPurchaseDate(list.get(i).getCreateTime());
					mshRepurchaseReminder.setLastPurchasseQty(list.get(i).getPurchaseQty());
					mshRepurchaseReminder.setPurchaseQty(list.get(i).getPurchaseQty());
					mshRepurchaseReminder.setPurchaseTimes(1);
					mshRepurchaseReminder.setStatus("否");
					//下次购药时间 = 下单时间+疗程周期
					//需要查询之前的订单子表的所有数据依次循环
					List<MshOrderItem> mshOrderItemList = mshDemandListItemMapper.selectListByPhoneAndMedId(mshDemandList.getPhone(),list.get(i).getMedId());
					//设置初始值为第一单的下次购药日期
					long num0 = Integer.valueOf(yxStoreProduct.getMedicationCycle())*mshOrderItemList.get(0).getPurchaseQty();
					long date = mshOrderItemList.get(0).getCreateTime().getTime()+3600*24*num0*1000;
					for (MshOrderItem mshOrderItem : mshOrderItemList) {
						//当下次购药日期小于下一单的生成日期时
						if(date<mshOrderItem.getCreateTime().getTime()){
							long num = Integer.valueOf(yxStoreProduct.getMedicationCycle())*mshOrderItem.getPurchaseQty();
							date = mshOrderItem.getCreateTime().getTime()+3600*24*num*1000;
						}else{
							//大于等于的情况下，处于提前购买
							if(mshOrderItemList.size()>1){
								long num = Integer.valueOf(yxStoreProduct.getMedicationCycle())*mshOrderItem.getPurchaseQty();
								date = date + 3600*24*num*1000;
							}
						}
					}
	                mshRepurchaseReminder.setNextPurchaseDate(DateUtil.date(date).toTimestamp());
	                mshRepurchaseReminder.setProvinceName(mshDemandList.getProvince());
	                mshRepurchaseReminder.setCityName(mshDemandList.getCity());
	                mshRepurchaseReminder.setDistrictName(mshDemandList.getDistrict());
	                mshRepurchaseReminder.setAddress(mshDemandList.getDetail());
	                mshRepurchaseReminder.setReceiver(mshDemandList.getPatientname());
	                mshRepurchaseReminder.setReceiverMobile(mshDemandList.getPhone());
					//商品信息
	                mshRepurchaseReminder.setMedId(yxStoreProduct.getId());
					mshRepurchaseReminder.setMedCommonName(yxStoreProduct.getCommonName());
					mshRepurchaseReminder.setMedCycle(Integer.valueOf(yxStoreProduct.getMedicationCycle()));
					mshRepurchaseReminder.setMedName(yxStoreProduct.getStoreName());
					mshRepurchaseReminder.setMedManufacturer(yxStoreProduct.getManufacturer());
					mshRepurchaseReminder.setMedSku(yxStoreProduct.getYiyaobaoSku());
					mshRepurchaseReminder.setUnitPrice(list.get(i).getUnitPrice());
					mshRepurchaseReminder.setMedSpec(yxStoreProduct.getSpec());
					mshRepurchaseReminder.setMedUnit(yxStoreProduct.getUnit());
					mshRepurchaseReminder.setImage(StringUtils.isEmpty(yxStoreProduct.getImage())?"":yxStoreProduct.getImage().split(",")[0]);

					mshRepurchaseReminderMapper.insert(mshRepurchaseReminder);
				}
			}

		}

	}

    @Override
    public List<MshOrderDto> getMshOrderByDemandListId(Integer demandListId) {
		List<MshOrderDto> list= mshOrderMapper.getMshOrderByDemandListId(demandListId);
		for (MshOrderDto mshOrderDto : list) {
			if("0".equals(mshOrderDto.getOrderStatus())){
				mshOrderDto.setOrderStatusStr("待审核");
			}else if("1".equals(mshOrderDto.getOrderStatus())){
				mshOrderDto.setOrderStatusStr("审核通过");
			}else if("2".equals(mshOrderDto.getOrderStatus())){
				mshOrderDto.setOrderStatusStr("审核不通过");
			}else if("3".equals(mshOrderDto.getOrderStatus())){
				mshOrderDto.setOrderStatusStr("已发货");
			}else if("4".equals(mshOrderDto.getOrderStatus())){
				mshOrderDto.setOrderStatusStr("已完成");
			}else if("5".equals(mshOrderDto.getOrderStatus())){
				mshOrderDto.setOrderStatusStr("已退货");
			}else if("6".equals(mshOrderDto.getOrderStatus())){
				mshOrderDto.setOrderStatusStr("驳回");
			}else{
				mshOrderDto.setOrderStatusStr("");
			}
		}
		return list;
    }

    @Override
    public ExpressInfo queryOrderLogisticsProcess(ExpressParam expressInfoDo) {
        ExpressInfo expressInfo = new ExpressInfo();
        expressInfo.setSuccess(false);
        String url = yiyaobao_apiUrl_external + orderLogisticsByOrderIdUrl;
        cn.hutool.json.JSONObject jsonObject = JSONUtil.createObj();
        jsonObject.put("orderId",expressInfoDo.getYiyaobaoOrderId());

        String requestBody = jsonObject.toString(); //
        String express = "";
        try {
            long timestamp = System.currentTimeMillis(); // 生成签名时间戳
            Map<String, String> headers = new HashMap<String, String>();
            headers.put("ACCESS_APPID", appId); // 设置APP
            headers.put("ACCESS_TIMESTAMP", String.valueOf(timestamp)); // 设置签名时间戳
            String ACCESS_SIGANATURE = AppSiganatureUtils
                    .createSiganature(requestBody, appId, appSecret,
                            timestamp);
            headers.put("ACCESS_SIGANATURE", ACCESS_SIGANATURE); // 生成并设置签名
            log.info("ACCESS_APPID={}",appId);
            log.info("ACCESS_TIMESTAMP={}",String.valueOf(timestamp));
            log.info("ACCESS_SIGANATURE={}",ACCESS_SIGANATURE);
            log.info("url={}",url);
            log.info("requestBody={}",requestBody);
            String result = HttpUtils.postJsonHttps(url, requestBody,
                    headers); // 发起调用
            log.info("查询益药宝订单物流信息，结果：{}" ,result);
            cn.hutool.json.JSONObject object = JSONUtil.parseObj(result);


            if(object.getBool("success")) {
                JSONArray jsonArray = object.getJSONArray("result");
                List<Traces> tracesList= new ArrayList<>();

                expressInfo.setLogisticCode(expressInfoDo.getLogisticCode());
                expressInfo.setOrderCode(expressInfoDo.getOrderCode());
                expressInfo.setShipperCode(expressInfoDo.getShipperCode());
                expressInfo.setShipperName("");
                expressInfo.setSuccess(true);
                for(int i=0;i< jsonArray.size();i++) {
                    cn.hutool.json.JSONObject js = jsonArray.getJSONObject(i);
                    Integer processNo = js.getInt("processNo");
                    String processTime = js.getStr("processTime");
                    String processRemark = js.getStr("processRemark");

                    Traces trace = new Traces();
                    trace.setAcceptStation(processRemark);
                    trace.setAcceptTime(processTime);
                    tracesList.add(trace);
                }
                expressInfo.setTraces(tracesList);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return expressInfo;
    }

	@Override
	public Result<?> queryMshOrderLogisticsProcess(String phaOrderNo) {
        OrderVo orderVo = yiyaobaoOrdOrderMapper.getYiyaobaoOrderbyOrderIdSample(phaOrderNo);

        String url = yiyaobao_apiUrl_external + orderLogisticsByOrderIdUrl;
        cn.hutool.json.JSONObject jsonObject = JSONUtil.createObj();
        jsonObject.put("orderNo",phaOrderNo);

		String requestBody = jsonObject.toString();

		cn.hutool.json.JSONObject jsonObject1=new cn.hutool.json.JSONObject();
		try {
			long timestamp = System.currentTimeMillis(); // 生成签名时间戳
			Map<String, String> headers = new HashMap<String, String>();
			headers.put("ACCESS_APPID", appId); // 设置APP
			headers.put("ACCESS_TIMESTAMP", String.valueOf(timestamp)); // 设置签名时间戳
			String ACCESS_SIGANATURE = AppSiganatureUtils
					.createSiganature(requestBody, appId, appSecret,
							timestamp);
			headers.put("ACCESS_SIGANATURE", ACCESS_SIGANATURE); // 生成并设置签名
			String result = HttpUtils.postJsonHttps(url, requestBody,
					headers); // 发起调用
			log.info("查询new益药宝订单物流信息，结果：{}" ,result);
			cn.hutool.json.JSONObject object = JSONUtil.parseObj(result);


			if(object.getBool("success")) {
				JSONArray jsonArray = object.getJSONArray("result");
				if(jsonArray.size()>0 && StringUtils.isNotEmpty(orderVo.getFreightNo())){
                    List<Map> processMap = new ArrayList<Map>();

                    HashMap<String, Object> data = new HashMap<String, Object>();
                    data.put("processNum",orderVo.getFreightNo());

                    List<Map<String, Object>> detailMap=new ArrayList<>();
                    for(int i=0;i< jsonArray.size();i++) {
                        cn.hutool.json.JSONObject js = jsonArray.getJSONObject(i);
                        Integer processNo = js.getInt("processNo");
                        String processTime = js.getStr("processTime");
                        String processRemark = js.getStr("processRemark");
                        Map<String, Object> u = new HashMap<String, Object>();
                        u.put("processNo", processNo);
                        u.put("processRemark",processRemark);
                        u.put("processTime",processTime);
                        detailMap.add(u);
                    }
                    data.put("processDetails",detailMap);
                    processMap.add(data);
                    jsonObject1.put("processInfos",processMap);
                }
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return Result.OK(jsonObject1);
	}

	@Override
	public ResponseEntity queryNewOrderLogisticsProcess(ExpressParam expressInfoDo) {
        OrderVo orderVo = yiyaobaoOrdOrderMapper.getYiyaobaoOrderbyYiyaobaoId(expressInfoDo.getYiyaobaoOrderId());

        String url = yiyaobao_apiUrl_external + orderLogisticsByOrderIdUrl;
		cn.hutool.json.JSONObject jsonObject = JSONUtil.createObj();
		jsonObject.put("orderId",expressInfoDo.getYiyaobaoOrderId());

		String requestBody = jsonObject.toString();

		cn.hutool.json.JSONObject jsonObject1=new cn.hutool.json.JSONObject();
		try {
			long timestamp = System.currentTimeMillis(); // 生成签名时间戳
			Map<String, String> headers = new HashMap<String, String>();
			headers.put("ACCESS_APPID", appId); // 设置APP
			headers.put("ACCESS_TIMESTAMP", String.valueOf(timestamp)); // 设置签名时间戳
			String ACCESS_SIGANATURE = AppSiganatureUtils
					.createSiganature(requestBody, appId, appSecret,
							timestamp);
			headers.put("ACCESS_SIGANATURE", ACCESS_SIGANATURE); // 生成并设置签名
			String result = HttpUtils.postJsonHttps(url, requestBody,
					headers); // 发起调用
			log.info("查询new益药宝订单物流信息，结果：{}" ,result);
			cn.hutool.json.JSONObject object = JSONUtil.parseObj(result);


            if(object.getBool("success")) {
                JSONArray jsonArray = object.getJSONArray("result");
                if(jsonArray.size()>0 && StringUtils.isNotEmpty(orderVo.getFreightNo())){
                    List<Map> processMap = new ArrayList<Map>();

                    HashMap<String, Object> data = new HashMap<String, Object>();
                    data.put("processNum",orderVo.getFreightNo());

                    List<Map<String, Object>> detailMap=new ArrayList<>();
                    for(int i=0;i< jsonArray.size();i++) {
                        cn.hutool.json.JSONObject js = jsonArray.getJSONObject(i);
                        Integer processNo = js.getInt("processNo");
                        String processTime = js.getStr("processTime");
                        String processRemark = js.getStr("processRemark");
                        Map<String, Object> u = new HashMap<String, Object>();
                        u.put("processNo", processNo);
                        u.put("processRemark",processRemark);
                        u.put("processTime",processTime);
                        detailMap.add(u);
                    }
                    data.put("processDetails",detailMap);
                    processMap.add(data);
                    jsonObject1.put("processInfos",processMap);
                }
            }
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ResponseEntity(jsonObject1, HttpStatus.OK);
	}

	@Override
	public Result<?> queryMshOrderDetailInfo(String phaOrderNo) {
		MshOrderDto mshOrderDto= mshOrderMapper.getByExternalOrderId(phaOrderNo);
		if(mshOrderDto!=null){
			Map<String,Object> map=new HashMap<>();
			map.put("orderNo",mshOrderDto.getExternalOrderId());//订单号
			map.put("orderDate", DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD_HH_MM_SS,mshOrderDto.getCreateTime()));
            Integer total=0;
			List<Map<String,Object>> mapDetailList=new ArrayList<>();
			for (MshDemandListItemDto mshDemandListItemDto : mshOrderDto.getOrderItemList()) {
				Map<String,Object> mapDetail=new HashMap<>();
				mapDetail.put("drugCode",mshDemandListItemDto.getMedSku());
				mapDetail.put("qty",mshDemandListItemDto.getPurchaseQty());
				mapDetail.put("unitPrice",mshDemandListItemDto.getUnitPrice().multiply(new BigDecimal("100")));
				mapDetail.put("totalAmount",mshDemandListItemDto.getPurchaseQty() * mshDemandListItemDto.getUnitPrice().multiply(new BigDecimal("100")).intValue());
				mapDetail.put("name",mshDemandListItemDto.getMedName());
				mapDetail.put("commonName",mshDemandListItemDto.getMedCommonName());
				mapDetail.put("spec",mshDemandListItemDto.getMedSpec());
				mapDetail.put("unit",mshDemandListItemDto.getMedUnit());
				mapDetail.put("manufacturer",mshDemandListItemDto.getMedManufacturer());
				mapDetailList.add(mapDetail);

                total=total+ mshDemandListItemDto.getPurchaseQty() * mshDemandListItemDto.getUnitPrice().multiply(new BigDecimal("100")).intValue();
			}
            map.put("totalAmount",total);

            map.put("drugList",mapDetailList);

			return Result.OK(map);
		}else{
			return Result.error("订单不存在，请确认。");
		}
	}

	@Override
	public void lssueOrderByDemandListId(Integer demandListId) {
        MshDemandList mshDemandList= mshDemandListMapper.selectById(demandListId);
        if(mshDemandList.getLssueStatus()==1){
            new ErrorRequestException("该需求单已下发，不能重复下发。");
        }
		List<MshOrderDto> mshOrders=mshOrderMapper.getMshOrderByDemandListId(demandListId);
		if(mshOrders.size()>0){
			for (MshOrderDto mshOrder : mshOrders) {
			    if("0".equals(mshOrder.getOrderStatus()) && StringUtils.isEmpty(mshOrder.getYiyaobaoId())){
                    addMSHStoreOrderStatusTime(mshOrder);
                    
                    String prescriptionNo = mshOrder.getId() + "_msh";
                    //判断药房，发送报文
                    if(ShopConstants.STORENAME_GUANGZHOU_CLOUD.equals(mshOrder.getDrugstoreName())){
                        //设置处方字段
                        Prescription pres = new Prescription();

                        String orderSource = yiyaobaoOrderService.queryOrderSourceCode("MSH项目");
                        if(StrUtil.isBlank(orderSource)) {
                            orderSource = "23";
                        }
                        pres.setOrderSource(orderSource);
                        pres.setPayType("90");

                        // 病人名称 必填
                        pres.setName(mshDemandList.getPatientname());
                        // 患者手机号
                        pres.setMobile(mshDemandList.getPhone());
                        // 医生名称
                        pres.setDoctorName("汪志方");
                        // 科室名称
                        pres.setDepartment("普通全科");
                        pres.setDeptCode("200301");
                        // 医院名称 必填
                        pres.setHospitalName("益药商城");
                        // 收货信息
                        pres.setAddress(mshDemandList.getDetail());
                        pres.setProvinceName(mshDemandList.getProvince());
                        pres.setCityName(mshDemandList.getCity());
                        pres.setDistrictName(mshDemandList.getDistrict());
                        pres.setReceiver(mshDemandList.getReceivingName());
                        pres.setReceiverMobile(mshDemandList.getPhone());

                        // 处方日期
                        pres.setPrescribeDate(DateUtil.date().getTime());

                        // 处方号 必填
                        pres.setPrescripNo(prescriptionNo);
                        // 挂号类别 必填
                        pres.setRegisterType(0L);
                        //费别(0:自费;1:医保)
                        pres.setFeeType("0");



                        // 付款方式(01-现金;02-刷卡;70-门店已收款)
                        pres.setPayMethod("70");

                        //配送类型(00-自提；10-送货上门；99-无需配送)
                        pres.setDeliverType("10");
                        pres.setRemark("");
                        pres.setRegisterDate(DateUtil.date().getTime());
                        pres.setRegisterType(1L);

                        // pres.setDiscount();

                        //			设置处方明细列表（以不同药品区分).

                        List<PrescriptionDetail> details = new ArrayList<PrescriptionDetail>();
                        // 商品原价总和
                        Double totalTruePrice = 0d;

                        // 商品会员折价总和
                        Double totalVipDiscountAmount = 0d;

                        for (MshDemandListItemDto mshDemandListItemDto : mshOrder.getOrderItemList()) {
                            YxStoreProduct yxStoreProduct = storeProductMapper.selectById(mshDemandListItemDto.getMedId());
                            // 药品主数据
                            if(yxStoreProduct == null) {
                                throw new BadRequestException("无法找到药品详情信息");
                            }
                            PrescriptionDetail detail = new PrescriptionDetail();
                            // 药品编码 必填
                            detail.setMedCode(yxStoreProduct.getYiyaobaoSku());
                            // 药品名称 必填
                            detail.setMedName(yxStoreProduct.getStoreName());
                            // 药品数量 必填
                            detail.setAmount(new BigDecimal(mshDemandListItemDto.getPurchaseQty()));
                            // 单价
                            // 按优先级逻辑获取结算价
                            BigDecimal price = storeProductMapper.queryProductPrice(yxStoreProduct.getYiyaobaoSku(),ProjectNameEnum.MSH.getValue());
                            if(ObjectUtil.isNull(price)) {
                                throw new BadRequestException("商品["+ yxStoreProduct.getYiyaobaoSku() + "],项目["+ ProjectNameEnum.MSH.getValue() +"]无法找到价格配置信息");
                            }
                            detail.setUnitPrice( price.setScale(2,BigDecimal.ROUND_HALF_UP));
                            // 折扣金额 = (原价 - 会员价)*药品数量
                            //  BigDecimal vipDiscountAmount = NumberUtil.mul( new BigDecimal(mshDemandListItemDtoList.get(i).getPurchaseQty()),  NumberUtil.sub(yxStoreProduct.getPrice(),mshDemandListItemDtoList.get(i).getUnitPrice().setScale(2,BigDecimal.ROUND_HALF_UP))).setScale(2,BigDecimal.ROUND_HALF_UP);
                            BigDecimal vipDiscountAmount = new BigDecimal(0);
                            detail.setDiscountAmount(vipDiscountAmount);
                            // 折扣率
                            // detail.setDiscount(NumberUtil.div(yxStoreProduct.getPrice(), mshDemandListItemDtoList.get(i).getUnitPrice().setScale(2,BigDecimal.ROUND_HALF_UP)));
                            detail.setDiscount(new BigDecimal(0));
                            detail.setSpec(yxStoreProduct.getSpec());
                            details.add(detail);

                            totalTruePrice = NumberUtil.add(totalTruePrice.doubleValue(), NumberUtil.mul(mshDemandListItemDto.getPurchaseQty().doubleValue() , detail.getUnitPrice().setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue() ));
                          //  totalVipDiscountAmount = NumberUtil.add(totalVipDiscountAmount.doubleValue(), vipDiscountAmount.doubleValue() );

                        }


                        // 设置处方明细。当一次只能上传一条明细时，调用setDetail而非setDetails;
                        pres.setDetails(details);

                        //总金额
                        pres.setTotalAmount(new BigDecimal(totalTruePrice).setScale(2,BigDecimal.ROUND_HALF_UP));

                        //已收费金额
                        pres.setPaidAmount(new BigDecimal(totalTruePrice).setScale(2,BigDecimal.ROUND_HALF_UP));

                        // 折扣金额
                        // 折扣金额 = 会员折扣金额  + 优惠券折扣金额 + 积分折扣金额
                        Double totalDiscountAmount = NumberUtil.add(totalVipDiscountAmount.doubleValue() , 0 , 0 ).doubleValue();
                        pres.setDiscountAmount(new BigDecimal(totalDiscountAmount).setScale(2,BigDecimal.ROUND_HALF_UP));

                        //折扣率(采用小数表示)
                        // 折扣率 = (原价-现价)/原价
                        //  pres.setDiscount( new BigDecimal( 1- NumberUtil.div(totalDiscountAmount,totalTruePrice)).setScale(2,BigDecimal.ROUND_HALF_UP));

                        pres.setDiscount(new BigDecimal(1));
                        String url = yiyaobao_apiUrl_external + addSingleUrl;

                        String requestBody = JSONUtil.parseObj(pres).toString(); //

                        try {
                            long timestamp = System.currentTimeMillis(); // 生成签名时间戳
                            Map<String, String> headers = new HashMap<String, String>();
                            headers.put("ACCESS_APPID", appId); // 设置APP
                            headers.put("ACCESS_TIMESTAMP", String.valueOf(timestamp)); // 设置签名时间戳
                            String ACCESS_SIGANATURE = AppSiganatureUtils
                                    .createSiganature(requestBody, appId, appSecret,
                                            timestamp);
                            headers.put("ACCESS_SIGANATURE", ACCESS_SIGANATURE); // 生成并设置签名
                            log.info("ACCESS_APPID={}",appId);
                            log.info("ACCESS_TIMESTAMP={}",String.valueOf(timestamp));
                            log.info("ACCESS_SIGANATURE={}",ACCESS_SIGANATURE);
                            log.info("url={}",url);
                            log.info("requestBody={}",requestBody);
                            String result = HttpUtils.postJsonHttps(url, requestBody,
                                    headers); // 发起调用
                            log.info("下发益药宝订单，结果：{}" ,result);
                            cn.hutool.json.JSONObject object = JSONUtil.parseObj(result);


                            if(object.getBool("success")) {
                                MshOrder order= MyBeanUtils.convert(mshOrder, MshOrder.class);
                                order.setOrderStatus("0");
                                String orderId = yiyaobaoOrdOrderMapper.queryYiyaobaoOrderIdByPrescription(prescriptionNo);
                                order.setYiyaobaoId(orderId);
                                mshOrderMapper.updateById(order);

                                //根据项目名称更新益药宝订单来源
                                //yiyaobaoOrdOrderMapper.updateYiyaobaoOrderSourceByPrescripNo(prescriptionNo, "32");

//                                YiyaobaoOrderInfo yiyaobaoOrderInfo = new YiyaobaoOrderInfo();
//                                yiyaobaoOrderInfo.setOrderSource("32");
//                                yiyaobaoOrderInfo.setPrsNo(prescriptionNo);
//                                yiyaobaoOrderInfo.setPayMethod(YiyaobaoPayMethodEnum.payMethod_21.getValue());  // 金融支付
//                                yiyaobaoOrderInfo.setPayResult("10"); // 已支付
//                                yiyaobaoOrderInfo.setPayTime(DateUtil.formatDateTime(new Date()));
//                                yiyaobaoOrderInfo.setPayType(YiyaobaoPayTypeEnum.payType_40.getValue());
//                                // 更新益药宝订单的信息
//                                yiyaobaoOrdOrderMapper.updateYiyaobaoOrderInfoByPrescripNo(yiyaobaoOrderInfo);

                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }else{
                        //非广州环境
                        String base64_str =  "";
                        String imagePath = mshOrder.getOrderItemList().get(0).getPicUrl();
                        try {
                            if(StrUtil.isBlank(imagePath)) {
                                base64_str = new ImageUtil().localImageToBase64("otc.jpg");
                            } else {
                                //String imagePathConvert = imagePath.replace(localUrl,imageUrl);
                                String imagePathConvert = imagePath;
                                log.info("imagePathConvert === {}",imagePathConvert);
                                base64_str = ImageUtil.encodeImageToBase64(new URL(imagePathConvert));
                                //  log.info("base64_str.length==={}",base64_str.length());
                            }
                            base64_str = "data:image/jpeg;base64,"+ base64_str;
                            base64_str = URLEncoder.encode(base64_str,"UTF-8")  ;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        //获取患者字段信息
                        PrescriptionDTO prescriptionDTO = new PrescriptionDTO();
                        prescriptionDTO.setAddress(mshDemandList.getDetail());
                        prescriptionDTO.setCityCode(mshDemandList.getCityCode());
                        prescriptionDTO.setProvinceCode(mshDemandList.getProvinceCode());
                        prescriptionDTO.setDistrictCode(mshDemandList.getDistrictCode());

                        prescriptionDTO.setCustomerRequirement("");
                        prescriptionDTO.setPatientMobile(mshDemandList.getPhone());
                        prescriptionDTO.setPatientName(mshDemandList.getPatientname());
                        Project project = projectService.getOne(new LambdaQueryWrapper<Project>().eq(Project::getProjectCode, ProjectNameEnum.MSH.getValue()));
                        if(project != null) {
                            prescriptionDTO.setProjectNo(project.getYiyaobaoProjectCode());
                        }

                        YxSystemStore yxSystemStore = yxSystemStoreService.getById(mshOrder.getOrderItemList().get(0).getDrugstoreId());

                        if(yxSystemStore!=null){
                            prescriptionDTO.setSellerId(yxSystemStore.getYiyaobaoId());
                        }

                        // 生成短信验证码
                        String verifyCode = generateVerifyCode2(mshDemandList.getPhone());

                        prescriptionDTO.setVerifyCode(verifyCode);
                        JSONArray jsonArray = JSONUtil.createArray();
                        for (int i = 0; i<mshOrder.getOrderItemList().size(); i++) {
                            YxStoreProduct yxStoreProduct = storeProductMapper.selectById(mshOrder.getOrderItemList().get(i).getMedId());

                            cn.hutool.json.JSONObject jsonObject1 = JSONUtil.createObj();
                            jsonObject1.put("sku",yxStoreProduct.getYiyaobaoSku());
                            jsonObject1.put("unitPrice",mshOrder.getOrderItemList().get(i).getUnitPrice());
                            jsonObject1.put("amount",mshOrder.getOrderItemList().get(i).getPurchaseQty());

                            jsonArray.add(jsonObject1);

                        }

                        prescriptionDTO.setItems(jsonArray.toString());
                        prescriptionDTO.setImagePath(base64_str);
                        prescriptionDTO.setInvoiceType("10");
                        prescriptionDTO.setInvoiceAmount("0");

                        // 发送处方
                        // 获取到外部需求单
                        String orderSn = uploadOrder(prescriptionDTO);
                        //更新订单号，订单状态
                        OrderVo orderVo = yiyaobaoOrdOrderMapper.getYiyaobaoOrderbyOrderIdSample(orderSn);
                        MshOrder order= MyBeanUtils.convert(mshOrder, MshOrder.class);

                        order.setOrderStatus("0");
                        order.setExternalOrderId(orderSn);
                        order.setYiyaobaoId(orderVo.getId());
                        mshOrderMapper.updateById(order);

                        //更新插入复购信息表
                        createMshRepurchaseReminder(mshOrder.getId());

                        //根据项目名称更新益药宝订单来源
                        // yiyaobaoOrdOrderMapper.updateOrderSourceByOrderno(orderVo.getOrderNo(), "32");

                        YiyaobaoOrderInfo yiyaobaoOrderInfo = new YiyaobaoOrderInfo();
                        yiyaobaoOrderInfo.setOrderSource("32");
                        yiyaobaoOrderInfo.setOrderNo(orderSn);
                        yiyaobaoOrderInfo.setPayMethod(YiyaobaoPayMethodEnum.payMethod_21.getValue());  // 微信支付
                        yiyaobaoOrderInfo.setPayResult("10"); // 已支付
                        yiyaobaoOrderInfo.setPayTime(DateUtil.formatDateTime(new Date()));
                        yiyaobaoOrderInfo.setPayType(YiyaobaoPayTypeEnum.payType_40.getValue());
                        yiyaobaoOrdOrderMapper.updateYiyaobaoOrderInfoByOrderNo(yiyaobaoOrderInfo);
                    }
                }
			}
			mshDemandList.setLssueStatus(1);
            mshDemandList.setUpdateTime(new Date());
			mshDemandListMapper.updateById(mshDemandList);
            mqProducer.sendDelayQueue(mshQueueName,mshDemandList.getId().toString(),2000);
        }
	}

    /**
     * 定时取消
     * @return
     */
    public void synchOrderStatusJob() {
        long c1 = System.currentTimeMillis();
        log.info("***************msh订单驳回10天未回复定时更改为不通过start******************");
        try {
            List<MshOrder> list= mshOrderMapper.getAllTenDayNotAnswer();
            Map<Integer, List<MshOrder>> collect = list.stream().collect(Collectors.groupingBy(MshOrder::getDemandListId));
            for (Integer integer : collect.keySet()) {
                MshDemandList mshDemandList=new MshDemandList();
                mshDemandList.setId(integer);
                mshDemandList.setSaveStatus(MshStatusEnum.SaveStatus.SHTJ.getCode());
                List<MshOrder> mshOrders=collect.get(integer);
                if(mshOrders.size()>1){
                    mshDemandList.setCancelReason("配送药物剂量与处方不符或超过福利规定药量，无法配送");
                }else{
                    mshDemandList.setCancelReason(MshStatusEnum.CancelReason.getCancelReason(mshOrders.get(0).getAuditReasons())==null?mshOrders.get(0).getAuditReasons():MshStatusEnum.CancelReason.getCancelReason(mshOrders.get(0).getAuditReasons()).getCode());
                }
                for (MshOrder mshOrder : mshOrders) {
                    mshOrder.setOrderStatus("2");
                    mshOrderMapper.updateById(mshOrder);
                }
                mshDemandList.setUpdateTime(new Date());
                mshDemandListMapper.updateById(mshDemandList);
                mqProducer.sendDelayQueue(mshQueueName,integer.toString(),2000);
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("msh订单驳回10天未回复定时更改为不通过失败：{}", e.getMessage());
        }
        log.info("***************msh订单驳回10天未回复定时更改为不通过end******************" + (System.currentTimeMillis() - c1));
    }


    /**
     *  添加MSH订单状态变更记录
     * @param mshOrder
     */
    public  void addMSHStoreOrderStatusTime(MshOrderDto mshOrder){
        if(mshOrder.getOrderStatus() != null && mshOrder.getOrderStatus().equals(MshStatusEnum.OrderStatus.DSH.getCode().toString())) {
            YxStoreOrderStatus storeOrderStatus = new YxStoreOrderStatus();
            storeOrderStatus.setOid(mshOrder.getId());
            storeOrderStatus.setChangeType(MSHOrderChangeTypeEnum.DSH.getValue());
            storeOrderStatus.setChangeMessage(MSHOrderChangeTypeEnum.DSH.getDesc());
            storeOrderStatus.setChangeTime(OrderUtil.getSecondTimestampTwo());
            storeOrderStatus.setOrderType("MSH");
            yxStoreOrderStatusService.save(storeOrderStatus);
        }else if(mshOrder.getOrderStatus()!= null && mshOrder.getOrderStatus().equals(MshStatusEnum.OrderStatus.SHTG.getCode().toString())) {
            YxStoreOrderStatus storeOrderStatus = new YxStoreOrderStatus();
            storeOrderStatus.setOid(mshOrder.getId());
            storeOrderStatus.setChangeType(MSHOrderChangeTypeEnum.SHTG.getValue());
            storeOrderStatus.setChangeMessage(MSHOrderChangeTypeEnum.SHTG.getDesc());
            storeOrderStatus.setChangeTime(OrderUtil.getSecondTimestampTwo());
            storeOrderStatus.setOrderType("MSH");
            yxStoreOrderStatusService.save(storeOrderStatus);
        }else if(mshOrder.getOrderStatus()!= null && mshOrder.getOrderStatus().equals(MshStatusEnum.OrderStatus.SHBTG.getCode().toString())) {
            YxStoreOrderStatus storeOrderStatus = new YxStoreOrderStatus();
            storeOrderStatus.setOid(mshOrder.getId());
            storeOrderStatus.setChangeType(MSHOrderChangeTypeEnum.SHBTG.getValue());
            storeOrderStatus.setChangeMessage(MSHOrderChangeTypeEnum.SHBTG.getDesc());
            storeOrderStatus.setChangeTime(OrderUtil.getSecondTimestampTwo());
            storeOrderStatus.setOrderType("MSH");
            yxStoreOrderStatusService.save(storeOrderStatus);
        }else if(mshOrder.getOrderStatus()!= null && mshOrder.getOrderStatus().equals(MshStatusEnum.OrderStatus.YFH.getCode().toString()) ) {
            YxStoreOrderStatus storeOrderStatus = new YxStoreOrderStatus();
            storeOrderStatus.setOid(mshOrder.getId());
            storeOrderStatus.setChangeType(MSHOrderChangeTypeEnum.YFH.getValue());
            storeOrderStatus.setChangeMessage(MSHOrderChangeTypeEnum.YFH.getDesc());
            storeOrderStatus.setChangeTime(OrderUtil.getSecondTimestampTwo());
            storeOrderStatus.setOrderType("MSH");
            yxStoreOrderStatusService.save(storeOrderStatus);
        }else if(mshOrder.getOrderStatus()!= null && mshOrder.getOrderStatus().equals(MshStatusEnum.OrderStatus.YWC.getCode().toString()) ) {
            YxStoreOrderStatus storeOrderStatus = new YxStoreOrderStatus();
            storeOrderStatus.setOid(mshOrder.getId());
            storeOrderStatus.setChangeType(MSHOrderChangeTypeEnum.YWC.getValue());
            storeOrderStatus.setChangeMessage(MSHOrderChangeTypeEnum.YWC.getDesc());
            storeOrderStatus.setChangeTime(OrderUtil.getSecondTimestampTwo());
            storeOrderStatus.setOrderType("MSH");
            yxStoreOrderStatusService.save(storeOrderStatus);
        }else if(mshOrder.getOrderStatus()!= null && mshOrder.getOrderStatus().equals(MshStatusEnum.OrderStatus.YTH.getCode().toString()) ) {
            YxStoreOrderStatus storeOrderStatus = new YxStoreOrderStatus();
            storeOrderStatus.setOid(mshOrder.getId());
            storeOrderStatus.setChangeType(MSHOrderChangeTypeEnum.YTH.getValue());
            storeOrderStatus.setChangeMessage(MSHOrderChangeTypeEnum.YTH.getDesc());
            storeOrderStatus.setChangeTime(OrderUtil.getSecondTimestampTwo());
            storeOrderStatus.setOrderType("MSH");
            yxStoreOrderStatusService.save(storeOrderStatus);
        }else if(mshOrder.getOrderStatus()!= null && mshOrder.getOrderStatus().equals(MshStatusEnum.OrderStatus.BH.getCode().toString()) ) {
            YxStoreOrderStatus storeOrderStatus = new YxStoreOrderStatus();
            storeOrderStatus.setOid(mshOrder.getId());
            storeOrderStatus.setChangeType(MSHOrderChangeTypeEnum.BH.getValue());
            storeOrderStatus.setChangeMessage(MSHOrderChangeTypeEnum.BH.getDesc());
            storeOrderStatus.setChangeTime(OrderUtil.getSecondTimestampTwo());
            storeOrderStatus.setOrderType("MSH");
            yxStoreOrderStatusService.save(storeOrderStatus);
        }
    }
}
