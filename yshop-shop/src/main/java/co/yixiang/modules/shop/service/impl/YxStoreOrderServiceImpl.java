/**
 * Copyright (C) 2018-2020
 * All rights reserved, Designed By www.yixiang.co

 */
package co.yixiang.modules.shop.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import co.yixiang.common.service.impl.BaseServiceImpl;
import co.yixiang.common.utils.QueryHelpPlus;
import co.yixiang.constant.ShopConstants;
import co.yixiang.dozer.service.IGenerator;
import co.yixiang.enums.*;
import co.yixiang.exception.BadRequestException;
import co.yixiang.exception.EntityExistException;
import co.yixiang.exception.ErrorRequestException;
import co.yixiang.modules.activity.domain.YxStoreCouponUser;
import co.yixiang.modules.activity.domain.YxStorePink;
import co.yixiang.modules.activity.service.*;
import co.yixiang.modules.api.domain.UserAgreement;
import co.yixiang.modules.api.param.OrderFreightParam;
import co.yixiang.modules.api.param.OrderInfoParam;
import co.yixiang.modules.api.param.PrescripStatusParam;
import co.yixiang.modules.api.service.UserAgreementService;
import co.yixiang.modules.ebs.service.EbsServiceImpl;
import co.yixiang.modules.hospitaldemand.domain.InternetHospitalDemand;
import co.yixiang.modules.hospitaldemand.service.InternetHospitalDemandService;
import co.yixiang.modules.msh.domain.MshOrder;
import co.yixiang.modules.msh.service.MshDemandListService;
import co.yixiang.modules.msh.service.enume.MshStatusEnum;
import co.yixiang.modules.msh.service.impl.MshOrderServiceImpl;
import co.yixiang.modules.msh.service.mapper.MshOrderMapper;
import co.yixiang.modules.shop.domain.*;

import co.yixiang.modules.shop.service.*;
import co.yixiang.modules.shop.service.dto.*;
import co.yixiang.modules.shop.service.mapper.*;
import co.yixiang.modules.shop.service.vo.YxUserAddressQueryVo;
import co.yixiang.modules.taibao.domain.TbOrderDetailProjectParam;
import co.yixiang.modules.taibao.domain.TbOrderProjectParam;
import co.yixiang.modules.taiping.enums.TaipingOrderStatusEnum;
import co.yixiang.modules.taiping.service.TaipingCardService;
import co.yixiang.modules.xikang.dto.XkExpress;
import co.yixiang.modules.xikang.dto.XkSign;
import co.yixiang.modules.xikang.service.XkProcessService;
import co.yixiang.modules.yiyaobao.service.OrderServiceImpl;
import co.yixiang.mp.domain.DictDetail;
import co.yixiang.mp.rest.param.DictDetailQueryParam;
import co.yixiang.mp.service.DictDetailService;
import co.yixiang.mp.service.YxMiniPayService;
import co.yixiang.mp.service.YxPayService;
import co.yixiang.mp.service.YxTemplateService;
import co.yixiang.mp.service.dto.OrderTemplateMessage;
import co.yixiang.mp.yiyaobao.service.mapper.OrdOrderMapper;
import co.yixiang.mp.yiyaobao.vo.OrderPartInfoVo;
import co.yixiang.mp.yiyaobao.vo.OrderVo;
import co.yixiang.rabbitmq.send.MqProducer;
import co.yixiang.tools.domain.AlipayConfiguration;
import co.yixiang.tools.domain.QiniuContent;
import co.yixiang.tools.domain.WechatConfiguration;
import co.yixiang.tools.service.AlipayConfigurationService;
import co.yixiang.tools.service.WechatConfigurationService;
import co.yixiang.tools.service.impl.SmsServiceImpl;
import co.yixiang.tools.utils.AlipayProperties;
import co.yixiang.tools.utils.AlipayUtils;
import co.yixiang.tools.utils.AppSiganatureUtils;
import co.yixiang.tools.utils.HttpUtils;
import co.yixiang.tools.utils.mpai.MapiPayUtils;
import co.yixiang.tools.utils.mpai.MapiProperties;
import co.yixiang.utils.*;
import com.alibaba.fastjson.JSON;
import com.alipay.api.response.AlipayTradeRefundResponse;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.github.binarywang.wxpay.bean.notify.WxPayNotifyResponse;
import com.github.binarywang.wxpay.exception.WxPayException;
import com.github.pagehelper.PageInfo;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Pageable;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

// 默认不使用缓存
//import org.springframework.cache.annotation.CacheConfig;
//import org.springframework.cache.annotation.CacheEvict;
//import org.springframework.cache.annotation.Cacheable;

/**
* @author hupeng
* @date 2020-05-12
*/
@Slf4j
@Service
//@AllArgsConstructor
//@CacheConfig(cacheNames = "yxStoreOrder")
public class YxStoreOrderServiceImpl extends BaseServiceImpl<StoreOrderMapper, YxStoreOrder> implements YxStoreOrderService {
    @Autowired
    private  IGenerator generator;
    @Autowired
    private YxUserService userService;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private YxStorePinkService storePinkService;
    @Autowired
    private YxStoreOrderCartInfoService storeOrderCartInfoService;
    @Autowired
    private  YxUserBillService yxUserBillService;
    @Autowired
    private  YxStoreOrderStatusService yxStoreOrderStatusService;

    @Autowired
    private  YxPayService payService;
    @Autowired
    private  YxMiniPayService miniPayService;
    @Autowired
    private  YxSystemStoreService systemStoreService;
    @Autowired
    private  StoreOrderMapper yxStoreOrderMapper;
    @Autowired
    private  StoreProductMapper yxStoreProductMapper;
    @Autowired
    private  YxWechatUserService wechatUserService;
    @Autowired
    private  YxTemplateService templateService;
    @Autowired
    private YxSystemStoreService yxSystemStoreService;

    @Autowired
    @Lazy
    private OrderServiceImpl yiyaobaoOrderService;


    @Autowired
    private MshOrderServiceImpl mshOrderServiceImpl;

    @Autowired
    private YxUserService yxUserService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private YxStoreCouponUserService couponUserService;

    @Autowired
    private YxUserAddressService yxUserAddressService;

    @Autowired
    private YxStoreOrderCartInfoService orderCartInfoService;

    @Autowired
    private YxStoreOrderStatusService orderStatusService;

    @Autowired
    private OrdOrderMapper yiyaobaoOrdOrderMapper;

    @Autowired
    private MshOrderMapper mshOrderMapper;

    @Autowired
    private MdCountryMapper mdCountryMapper;

    @Autowired
    private MshDemandListService mshDemandListService;

    @Autowired
    private UserAgreementService userAgreementService;

    @Value("${specialProject.rochesma.payeeAccountName}")
    private String payeeAccountName;

    @Value("${specialProject.rochesma.payeeBankName}")
    private String  payeeBankName;

    @Value("${specialProject.rochesma.payeeBankAccount}")
    private String payeeBankAccount;

    @Autowired
    private InternetHospitalDemandService internetHospitalDemandService;

    @Autowired
    private XkProcessService xkProcessService;

    @Autowired
    private DictDetailService dictDetailService;

    @Autowired
    private SmsServiceImpl smsService;

    @Autowired
    private TaipingCardService taipingCardService;

    @Autowired
    private RocheStoreService rocheStoreService;

    @Autowired
    private RestTemplate restTemplate;
    @Value("${oms.updateStatusUrl}")
    private String omsUpdateStatusUrl;

    @Autowired
    private MqProducer mqProducer;

    // 业务队列绑定业务交换机的routeKey
    @Value("${meideyi.delayQueueName}")
    private String bizRoutekeyMeideyi;

    // 业务队列绑定业务交换机的routeKey
    @Value("${yaolian.delayQueueName}")
    private String bizRoutekeyYaolian;

    // 业务队列绑定业务交换机的routeKey
    @Value("${zhonganpuyao.delayQueueName}")
    private String bizRoutekeyZhonganpuyao;

    @Autowired
    private ProjectSalesAreaService projectSalesAreaService;

    @Autowired
    private YxUserAddressService addressService;

    @Autowired
    @Lazy
    private YxStoreCartService storeCartService;

    @Autowired
    private StoreCartMapper storeCartMapper;

    @Autowired
    private YxStoreProductService productService;

    @Autowired
    private YxStoreProductAttrValueService yxStoreProductAttrValueService;

    @Autowired
    private Product4projectService product4projectService;

    @Autowired
    private YxSystemConfigService systemConfigService;

    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private YxUserAddressService userAddressService;
    @Autowired
    private ProjectService projectService;

/*    @Autowired
    private SendPrsProducer sendPrsProducer;*/
    @Autowired
    private YxExpressTemplateService yxExpressTemplateService;


    @Autowired
    private YxExpressTemplateDetailService yxExpressTemplateDetailService;

    @Autowired
    private YxDrugUsersService yxDrugUsersService;

    @Value("${ant.delayQueueName}")
    private String antQueueName;

    @Value("${ant.delayOrderFreightQueueName}")
    private String orderFreightQueueName;

    @Value("${meditrust.delayQueueName}")
    private String meditrustQueueName;

    @Value("${junling.delayQueueName}")
    private String junlingQueueName;

    @Value("${yiyaobao.delayQueueName}")
    private String bizRoutekeyYiyaobao;

    @Value("${msh.delayQueueName}")
    private String mshQueueName;

    @Value("${yiyaobao.refundQueueName}")
    private String refundQueueName;

    @Value("${yaolian.delayQueueName}")
    private String yaolianQueueName;

    @Autowired
    private EbsServiceImpl ebsService;

    @Value("${yiyaobao.apiUrlExternal}")
    private String yiyaobao_apiUrl_external;

    @Value("${yiyaobao.cancelOrder}")
    private String cancelOrder;

    @Value("${yiyaobao.appId}")
    private String appId;

    @Value("${yiyaobao.appSecret}")
    private String appSecret;

    @Autowired
    private WechatConfigurationService  wechatConfigurationService;

    @Autowired
    private AlipayConfigurationService alipayConfigurationService;

    private static Lock lock = new ReentrantLock(false);

   // @Autowired
  //  private ZhengDaTianQingServiceImpl zhengDaTianQingService;
    @Override
    public OrderCountDto getOrderCount() {
        //获取所有订单转态为已支付的
        List<CountDto> nameList =  storeCartService.findCateName();
        System.out.println("nameList:"+nameList);
        Map<String,Integer> childrenMap = new HashMap<>();
        nameList.forEach(i ->{
            if(i != null) {
                if(childrenMap.containsKey(i.getCatename())) {
                    childrenMap.put(i.getCatename(), childrenMap.get(i.getCatename())+1);
                }else {
                    childrenMap.put(i.getCatename(), 1);
                }
            }

        });
        List<OrderCountDto.OrderCountData> list = new ArrayList<>();
        List<String> columns = new ArrayList<>();
        childrenMap.forEach((k,v) ->{
            OrderCountDto.OrderCountData orderCountData = new OrderCountDto.OrderCountData();
            orderCountData.setName(k);
            orderCountData.setValue(v);
            columns.add(k);
            list.add(orderCountData);
        });
        OrderCountDto orderCountDto = new OrderCountDto();
        orderCountDto.setColumn(columns);
        orderCountDto.setOrderCountDatas(list);
        return orderCountDto;
    }

    @Override
    public OrderTimeDataDto getOrderTimeData() {
        int today = OrderUtil.dateToTimestampT(DateUtil.beginOfDay(new Date()));
        int yesterday = OrderUtil.dateToTimestampT(DateUtil.beginOfDay(DateUtil.
                yesterday()));
        int lastWeek = OrderUtil.dateToTimestampT(DateUtil.beginOfDay(DateUtil.lastWeek()));
        int nowMonth = OrderUtil.dateToTimestampT(DateUtil
                .beginOfMonth(new Date()));
        OrderTimeDataDto orderTimeDataDTO = new OrderTimeDataDto();

        orderTimeDataDTO.setTodayCount(yxStoreOrderMapper.countByPayTimeGreaterThanEqual(today));
        //orderTimeDataDTO.setTodayPrice(yxStoreOrderMapper.sumPrice(today));

        orderTimeDataDTO.setProCount(yxStoreOrderMapper
                .countByPayTimeLessThanAndPayTimeGreaterThanEqual(today,yesterday));
        //orderTimeDataDTO.setProPrice(yxStoreOrderMapper.sumTPrice(today,yesterday));

        orderTimeDataDTO.setLastWeekCount(yxStoreOrderMapper.countByPayTimeGreaterThanEqual(lastWeek));
        //orderTimeDataDTO.setLastWeekPrice(yxStoreOrderMapper.sumPrice(lastWeek));

        orderTimeDataDTO.setMonthCount(yxStoreOrderMapper.countByPayTimeGreaterThanEqual(nowMonth));
        //orderTimeDataDTO.setMonthPrice(yxStoreOrderMapper.sumPrice(nowMonth));

        orderTimeDataDTO.setUserCount(userMapper.selectCount(new QueryWrapper<YxUser>()));
        orderTimeDataDTO.setOrderCount(yxStoreOrderMapper.selectCount(new QueryWrapper<YxStoreOrder>()));
        orderTimeDataDTO.setPriceCount(yxStoreOrderMapper.sumTotalPrice());
        orderTimeDataDTO.setGoodsCount(yxStoreProductMapper.selectCount(new QueryWrapper<YxStoreProduct>()));

        return orderTimeDataDTO;
    }

    @Override
    public Map<String, Object> chartCount() {
        Map<String, Object> map = new LinkedHashMap<>();
        int nowMonth = OrderUtil.dateToTimestampT(DateUtil
                .beginOfMonth(new Date()));

        map.put("chart",yxStoreOrderMapper.chartList(nowMonth));
        map.put("chartT",yxStoreOrderMapper.chartListT(nowMonth));

        return map;
    }
    @Override
    //@Cacheable
    public Map<String, Object> queryAll(YxStoreOrderQueryCriteria criteria, Pageable pageable) {

        if(StrUtil.isNotBlank(criteria.getStoreName())) {
            LambdaQueryWrapper<YxSystemStore> lambdaQueryWrapper = new LambdaQueryWrapper();
            lambdaQueryWrapper.like(YxSystemStore::getName,criteria.getStoreName());
            lambdaQueryWrapper.select(YxSystemStore::getId);
            List<YxSystemStore> systemStoreList = systemStoreService.list(lambdaQueryWrapper);
            List<Integer> systemIdList = new ArrayList<>();
            if(CollUtil.isNotEmpty(systemStoreList)) {
                for(YxSystemStore yxSystemStore:systemStoreList) {
                    systemIdList.add(yxSystemStore.getId());
                }
                criteria.setStoreId(systemIdList);
            }

        }

        getPage(pageable);
        PageInfo<YxStoreOrder> page = new PageInfo<>(queryAll(criteria));
        List<YxStoreOrderDto> storeOrderDTOS = new ArrayList<>();
        for (YxStoreOrder yxStoreOrder : page.getList()) {
            orderList(storeOrderDTOS, yxStoreOrder);

        }
        Map<String, Object> map = new LinkedHashMap<>(2);
        map.put("content", storeOrderDTOS);
        map.put("totalElements", page.getTotal());
        return map;
    }

    /**
     * 代码提取
     * @param storeOrderDTOS
     * @param yxStoreOrder
     */
    private void orderList(List<YxStoreOrderDto> storeOrderDTOS, YxStoreOrder yxStoreOrder) {

        YxStoreOrderDto yxStoreOrderDto = generator.convert(yxStoreOrder, YxStoreOrderDto.class);
        Integer _status = OrderUtil.orderStatus(yxStoreOrder.getPaid(),yxStoreOrder.getStatus(),
                yxStoreOrder.getRefundStatus(),yxStoreOrder.getNeedRefund());

        if(ProjectNameEnum.ROCHE_SMA.getValue().equals(yxStoreOrder.getProjectCode())) {
            if(yxStoreOrder.getStoreId() != null &&  yxStoreOrder.getStoreId() > 0) {
                RocheStore rocheStore = rocheStoreService.getById(yxStoreOrder.getStoreId());
                if(rocheStore != null) {
                    yxStoreOrderDto.setStoreName(rocheStore.getName());
                }else {
                    yxStoreOrderDto.setStoreName("");
                }
                YxSystemStore yxSystemStore = systemStoreService.getById(yxStoreOrder.getStoreId());
                if(yxSystemStore != null) {
                    yxStoreOrderDto.setIsyiyaostoreid(1);
                }else{
                    yxStoreOrderDto.setIsyiyaostoreid(0);
                }
            }
        } else {
            if( yxStoreOrder.getStoreId() != null &&  yxStoreOrder.getStoreId() > 0) {
                YxSystemStore yxSystemStore = systemStoreService.getById(yxStoreOrder.getStoreId());
                if(yxSystemStore != null) {
                    yxStoreOrderDto.setStoreName(yxSystemStore.getName());
                }else {
                    yxStoreOrderDto.setStoreName("");
                }
            }
        }

        //订单状态
        String orderStatusStr = OrderUtil.orderStatusStr(yxStoreOrder.getPaid()
                ,yxStoreOrder.getStatus(),yxStoreOrder.getShippingType()
                ,yxStoreOrder.getRefundStatus());

        if(_status == 3){

            if(yxStoreOrder.getNeedRefund() == 1 ) {
                String refundTime = DateUtil.formatDateTime(yxStoreOrder.getCheckTime()) ;
                String str = "<b style='color:#f124c7'>申请退款</b><br/>"+
                        "<span>退款原因："+"审核不通过"+"</span><br/>" +
                        "<span>备注说明："+yxStoreOrder.getCheckFailRemark()+"</span><br/>" +
                        "<span>退款时间："+refundTime+"</span><br/>";
                orderStatusStr = str;
            } else {
                String refundTime= "";
                if(yxStoreOrder.getRefundReasonTime() != null) {
                    refundTime = OrderUtil.stampToDate(String.valueOf(yxStoreOrder
                            .getRefundReasonTime()));
                }

                String str = "<b style='color:#f124c7'>申请退款</b><br/>"+
                        "<span>退款原因："+yxStoreOrder.getRefundReasonWap()+"</span><br/>" +
                        "<span>备注说明："+yxStoreOrder.getRefundReasonWapExplain()+"</span><br/>" +
                        "<span>退款时间："+refundTime+"</span><br/>";
                orderStatusStr = str;
            }


        }
        yxStoreOrderDto.setStatusName(orderStatusStr);

        yxStoreOrderDto.set_status(_status);

        String payTypeName = OrderUtil.payTypeName(yxStoreOrder.getPayType()
                ,yxStoreOrder.getPaid());
        yxStoreOrderDto.setPayTypeName(payTypeName);

        yxStoreOrderDto.setPinkName(orderType(yxStoreOrder.getId()
                ,yxStoreOrder.getPinkId(),yxStoreOrder.getCombinationId()
                ,yxStoreOrder.getSeckillId(),yxStoreOrder.getBargainId(),
                yxStoreOrder.getShippingType()));

        List<YxStoreOrderCartInfo> cartInfos = storeOrderCartInfoService.list(
                new QueryWrapper<YxStoreOrderCartInfo>().eq("oid",yxStoreOrder.getId()));
        List<StoreOrderCartInfoDto> cartInfoDTOS = new ArrayList<>();
        for (YxStoreOrderCartInfo cartInfo : cartInfos) {
            StoreOrderCartInfoDto cartInfoDTO = new StoreOrderCartInfoDto();
            cartInfoDTO.setCartInfoMap(JSON.parseObject(cartInfo.getCartInfo()));

            cartInfoDTOS.add(cartInfoDTO);
        }
        yxStoreOrderDto.setCartInfoList(cartInfoDTOS);
        yxStoreOrderDto.setUserDTO(generator.convert(userService.getById(yxStoreOrder.getUid()), YxUserDto.class));
        if(yxStoreOrderDto.getUserDTO()==null){
            yxStoreOrderDto.setUserDTO(new YxUserDto());
        }

        // 是否云配液
        if(yxStoreOrderDto.getNeedCloudProduceFlag() != null && yxStoreOrderDto.getNeedCloudProduceFlag() == 1 ) {
            // 获取电子签名
            QueryWrapper queryWrapper = new QueryWrapper();
            queryWrapper.eq("order_key",yxStoreOrderDto.getUnique());
            queryWrapper.eq("status",1);

            UserAgreement userAgreement = userAgreementService.getOne(queryWrapper,false);
            if(userAgreement != null) {
                yxStoreOrderDto.setCloudSignPdfPath(userAgreement.getSignFilePath());
            }
        }

        Project project = projectService.getOne(new LambdaQueryWrapper<Project>().eq(Project::getProjectCode,yxStoreOrderDto.getProjectCode()).select(Project::getProjectName));
        if(project != null) {
            yxStoreOrderDto.setProjectName(project.getProjectName());
        }

        if(StringUtils.isNotEmpty(yxStoreOrder.getMerchantNumber())){
            QueryWrapper<WechatConfiguration> wrapper = new QueryWrapper<>();
            wrapper.eq("mch_id",yxStoreOrder.getMerchantNumber());
            wrapper.eq("delete_flag",0);
            List<WechatConfiguration> resources = wechatConfigurationService.list(wrapper);
            if(resources!=null && resources.size()>0){
                yxStoreOrderDto.setMerchantName(resources.get(0).getName());
            }else{
                QueryWrapper<AlipayConfiguration> alipayWrapper = new QueryWrapper<>();
                alipayWrapper.eq("app_id",appId);
                alipayWrapper.eq("delete_flag",0);
                AlipayConfiguration alipayConfiguration = alipayConfigurationService.getOne(alipayWrapper);
                if(alipayConfiguration!=null){
                    yxStoreOrderDto.setMerchantName(alipayConfiguration.getName());
                }
            }
        }

        storeOrderDTOS.add(yxStoreOrderDto);
    }


    @Override
    //@Cacheable
    public List<YxStoreOrder> queryAll(YxStoreOrderQueryCriteria criteria){
        return baseMapper.selectList(QueryHelpPlus.getPredicate(YxStoreOrder.class, criteria));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public YxStoreOrderDto create(YxStoreOrder resources) {
        if(this.getOne(new QueryWrapper<YxStoreOrder>().eq("`unique`",resources.getUnique())) != null){
            throw new EntityExistException(YxStoreOrder.class,"unique",resources.getUnique());
        }
        this.save(resources);
        return generator.convert(resources,YxStoreOrderDto.class);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(YxStoreOrder resources) {
        YxStoreOrder yxStoreOrder = this.getById(resources.getId());
        YxStoreOrder yxStoreOrder1 = this.getOne(new QueryWrapper<YxStoreOrder>().eq("`unique`",resources.getUnique()));
        if(yxStoreOrder1 != null && !yxStoreOrder1.getId().equals(yxStoreOrder.getId())){
            throw new EntityExistException(YxStoreOrder.class,"unique",resources.getUnique());
        }
        yxStoreOrder.copy(resources);
        this.saveOrUpdate(yxStoreOrder);
    }


    @Override
    public void download(List<YxStoreOrderDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (YxStoreOrderDto yxStoreOrder : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("订单号", yxStoreOrder.getOrderId());
            if(StrUtil.isNotBlank(yxStoreOrder.getUserDTO().getRealName())) {
                map.put("购药人姓名",yxStoreOrder.getUserDTO().getRealName());
            } else {
                map.put("购药人姓名",yxStoreOrder.getUserDTO().getNickname());
            }


            map.put("购药人手机号",yxStoreOrder.getUserDTO().getPhone());

            map.put("患者姓名",yxStoreOrder.getDrugUserName());
            map.put("患者电话",yxStoreOrder.getDrugUserPhone());

            map.put("收货人姓名", yxStoreOrder.getRealName());
            map.put("收货人电话", yxStoreOrder.getUserPhone());
            map.put("收货人详细地址", yxStoreOrder.getUserAddress());

            List<String> stringList = new ArrayList<>();
            for(StoreOrderCartInfoDto cartInfoDto :  yxStoreOrder.getCartInfoList() ) {
                JSONObject jsonObject = JSONUtil.parseFromMap(cartInfoDto.getCartInfoMap());
                int cartNum = jsonObject.getInt("cartNum");
                JSONObject jsonObject_prod = jsonObject.getJSONObject("productInfo");
                String commonName = jsonObject_prod.getStr("commonName");
                String spec = jsonObject_prod.getStr("spec");
                String str = commonName + " "+ spec +  "  * " + cartNum;
                stringList.add(str);

            }

            String cartString = CollUtil.join(stringList,"\n");

        //    map.put("购物车id", yxStoreOrder.getCartId());

            map.put("订单明细", cartString);
          //  map.put("运费金额", yxStoreOrder.getFreightPrice());
         //   map.put("订单商品总数", yxStoreOrder.getTotalNum());
            map.put("订单总价", yxStoreOrder.getTotalPrice());
            map.put("邮费", yxStoreOrder.getTotalPostage());
            map.put("实际支付金额", yxStoreOrder.getPayPrice());
          //  map.put("支付邮费", yxStoreOrder.getPayPostage());
          //  map.put("抵扣金额", yxStoreOrder.getDeductionPrice());
        //    map.put("优惠券id", yxStoreOrder.getCouponId());
        //    map.put("优惠券金额", yxStoreOrder.getCouponPrice());
            map.put("支付方式",yxStoreOrder.getPayTypeName());
            if(yxStoreOrder.getPaid() == 1) {
                map.put("支付状态", "已支付");
            } else {
                map.put("支付状态", "未支付");
            }

            if(yxStoreOrder.getPayTime() == null) {
                map.put("支付时间", "");
            } else {
                map.put("支付时间", OrderUtil.stampToDate(yxStoreOrder.getPayTime().toString()));
            }

          //  map.put("支付方式", yxStoreOrder.getPayType());
            map.put("订单日期", OrderUtil.stampToDate(yxStoreOrder.getAddTime().toString()));
            map.put("订单状态", yxStoreOrder.getStatusName());
            if( yxStoreOrder.getRefundStatus() == null) {
                map.put("退款状态", "未退款");
            }else{
                if( yxStoreOrder.getRefundStatus() == 0) {
                    map.put("退款状态", "未退款");
                } else if(yxStoreOrder.getRefundStatus() == 1) {
                    map.put("退款状态", "申请中");
                } else if (yxStoreOrder.getRefundStatus() == 2){
                    map.put("退款状态", "已退款");
                }else {
                    map.put("退款状态", "");
                }
            }
          //  map.put("退款图片", yxStoreOrder.getRefundReasonWapImg());
            map.put("退款用户说明", yxStoreOrder.getRefundReasonWapExplain());
            if(yxStoreOrder.getRefundReasonTime() == null) {
                map.put("申请退款时间", "");
            } else {
                map.put("申请退款时间", OrderUtil.stampToDate(yxStoreOrder.getRefundReasonTime().toString()));
            }
            if(yxStoreOrder.getRefundFactTime() != null) {
                map.put("实际退款时间", DateUtil.formatDateTime(yxStoreOrder.getRefundFactTime()));
            } else {
                map.put("实际退款时间", "");
            }

        //    map.put("前台退款原因", yxStoreOrder.getRefundReasonWap());
         //   map.put("不退款的理由", yxStoreOrder.getRefundReason());
         //   map.put("退款金额", yxStoreOrder.getRefundPrice());
        //    map.put("快递公司编号", yxStoreOrder.getDeliverySn());
        //    map.put("快递名称/送货人姓名", yxStoreOrder.getDeliveryName());
        //    map.put("发货类型", yxStoreOrder.getDeliveryType());
      //      map.put("快递单号/手机号", yxStoreOrder.getDeliveryId());
          //  map.put("消费赚取积分", yxStoreOrder.getGainIntegral());
           // map.put("使用积分", yxStoreOrder.getUseIntegral());
          //  map.put("给用户退了多少积分", yxStoreOrder.getBackIntegral());
            map.put("备注", yxStoreOrder.getMark());
       //     map.put("是否删除", yxStoreOrder.getIsDel());
           // map.put("唯一id(md5加密)类似id", yxStoreOrder.getUnique());
       //     map.put("管理员备注", yxStoreOrder.getRemark());
            /*map.put("商户ID", yxStoreOrder.getMerId());
            map.put(" isMerCheck",  yxStoreOrder.getIsMerCheck());
            map.put("拼团产品id0一般产品", yxStoreOrder.getCombinationId());
            map.put("拼团id 0没有拼团", yxStoreOrder.getPinkId());
            map.put("成本价", yxStoreOrder.getCost());
            map.put("秒杀产品ID", yxStoreOrder.getSeckillId());
            map.put("砍价id", yxStoreOrder.getBargainId());
            map.put("核销码", yxStoreOrder.getVerifyCode());*/
            map.put("门店", yxStoreOrder.getStoreName());
          //  map.put("配送方式 1=快递 ，2=门店自提", yxStoreOrder.getShippingType());
          //  map.put("支付渠道(0微信公众号1微信小程序)", yxStoreOrder.getIsChannel());
          //  map.put(" isRemind",  yxStoreOrder.getIsRemind());
          //  map.put(" isSystemDel",  yxStoreOrder.getIsSystemDel());
            /*ProjectNameEnum  projectNameEnum = ProjectNameEnum.toType(yxStoreOrder.getProjectCode());
            if(projectNameEnum == null) {
                map.put("项目名称", "");
            } else{
                map.put("项目名称", projectNameEnum.getDesc());
            }*/

            map.put("项目名称", yxStoreOrder.getProjectName());

            map.put("商户订单号", StringUtils.isEmpty(yxStoreOrder.getPayOutTradeNo())?yxStoreOrder.getOrderId():yxStoreOrder.getPayOutTradeNo());
            map.put("商户号(微信)或APPID(支付宝)",yxStoreOrder.getMerchantNumber());
            map.put("商户名称",yxStoreOrder.getMerchantName());
         /*  LambdaQueryWrapper<Project> projectLambdaQueryWrapper = new LambdaQueryWrapper<>();
           projectLambdaQueryWrapper.eq(Project::getProjectCode,yxStoreOrder.getProjectCode());
           Project project = projectService.getOne(projectLambdaQueryWrapper,false);
           if(project == null) {
               map.put("项目名称", "");
           } else {
               map.put("项目名称", project.getProjectName());
           }*/

            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

    @Override
    public Map<String, Object> queryAll(List<String> ids) {
        List<YxStoreOrder> yxStoreOrders = this.list(new QueryWrapper<YxStoreOrder>().in("order_id",ids));
        List<YxStoreOrderDto> storeOrderDTOS = new ArrayList<>();
        for (YxStoreOrder yxStoreOrder :yxStoreOrders) {

            orderList(storeOrderDTOS, yxStoreOrder);

        }

        Map<String,Object> map = new LinkedHashMap<>(2);
        map.put("content",storeOrderDTOS);

        return map;
    }


    @Override
    public String orderType(int id,int pinkId, int combinationId,int seckillId,
                            int bargainId,int shippingType) {
        String str = "[普通订单]";
        if(pinkId > 0 || combinationId > 0){
            YxStorePink storePink = storePinkService.getOne(new QueryWrapper<YxStorePink>().
                    eq("order_id_key",id));
            if(ObjectUtil.isNull(storePink)) {
                str = "[拼团订单]";
            }else{
                switch (storePink.getStatus()){
                    case 1:
                        str = "[拼团订单]正在进行中";
                        break;
                    case 2:
                        str = "[拼团订单]已完成";
                        break;
                    case 3:
                        str = "[拼团订单]未完成";
                        break;
                    default:
                        str = "[拼团订单]历史订单";
                        break;
                }
            }

        }else if(seckillId > 0){
            str = "[秒杀订单]";
        }else if(bargainId > 0){
            str = "[砍价订单]";
        }
        if(shippingType == 2) str = "[核销订单]";
        return str;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void refund(YxStoreOrder resources) {
        if(resources.getPayPrice().doubleValue() < 0){
            throw new BadRequestException("请输入退款金额");
        }
        YxStoreOrder storeOrder =  getOne(new QueryWrapper<YxStoreOrder>().eq("order_id",resources.getOrderId()));
        // needRefund 表示是否需要退款，是，不调用益药宝的订单取消接口。needRefund 在 /order/prescripStatus 中更新，
        // 当益药宝的发过来的状态是 2，80，94，98 时，needRefund更新为 1
        if( storeOrder.getNeedRefund() == 0) {
            storeOrder.setReturnType(resources.getReturnType());
            yiyaobaoCancelOrder(storeOrder);
        }

        if(resources.getPayType().equals("yue")){
            //修改状态
            resources.setRefundStatus(2);
            resources.setRefundPrice(resources.getPayPrice());
            resources.setRefundFactTime(new Date());
            this.updateById(resources);

            //退款到余额
            YxUserDto userDTO = generator.convert(userService.getOne(new QueryWrapper<YxUser>().eq("uid",storeOrder.getUid())),YxUserDto.class);
            userMapper.updateMoney(resources.getPayPrice().doubleValue(),
                    storeOrder.getUid());

            YxUserBill userBill = new YxUserBill();
            userBill.setUid(resources.getUid());

            userBill.setLinkId(resources.getId().toString());
            userBill.setPm(1);
            userBill.setTitle("商品退款");
            userBill.setCategory("now_money");
            userBill.setType("pay_product_refund");
            userBill.setNumber(resources.getPayPrice());
            userBill.setBalance(NumberUtil.add(resources.getPayPrice(),userDTO.getNowMoney()));
            userBill.setMark("订单退款到余额");
            userBill.setAddTime(OrderUtil.getSecondTimestampTwo());
            userBill.setStatus(1);
            yxUserBillService.save(userBill);


            YxStoreOrderStatus storeOrderStatus = new YxStoreOrderStatus();
            storeOrderStatus.setOid(resources.getId());
            storeOrderStatus.setChangeType("refund_price");
            storeOrderStatus.setChangeMessage("退款给用户："+resources.getPayPrice() +"元");
            storeOrderStatus.setChangeTime(OrderUtil.getSecondTimestampTwo());

            yxStoreOrderStatusService.save(storeOrderStatus);
        }else{
            BigDecimal bigDecimal = new BigDecimal("100");
            try {
                String orderId = resources.getOrderId();
                if(StrUtil.isNotBlank(storeOrder.getExtendOrderId() )) {
                    orderId = storeOrder.getExtendOrderId();
                }
                if(storeOrder.getPayType().equals("alipay")){
                    log.info("退款订单："+ com.alibaba.fastjson.JSONObject.toJSONString(storeOrder));

                    String alipayParm= alipayConfigurationService.refundOrder(storeOrder.getTradeNo(),new DecimalFormat("0.00").format(resources.getPayPrice()),storeOrder.getMerchantNumber());
//                    if("alipayH5".equals(storeOrder.getPayFrom())){
//                       alipayParm =  AlipayUtils.refundOrder(storeOrder.getTradeNo(),new DecimalFormat("0.00").format(resources.getPayPrice()), AlipayProperties.serverUrlH5,AlipayProperties.appIdH5,AlipayProperties.publicKeyH5,AlipayProperties.notifyUrlH5);
//                    }else if("alipay".equals(storeOrder.getPayFrom())){
//                       alipayParm = AlipayUtils.refundOrder(storeOrder.getTradeNo(),new DecimalFormat("0.00").format(resources.getPayPrice()), AlipayProperties.serverUrl,AlipayProperties.appId,AlipayProperties.publicKey,AlipayProperties.notifyUrl);
//                    }
                    if(StringUtils.isEmpty(alipayParm) || net.sf.json.JSONObject.fromObject(alipayParm).get("alipay_trade_refund_response")==null){
                        throw new BadRequestException("退款失败，请联系管理员。");
                    }else {
                        AlipayTradeRefundResponse wdq = JsonUtil.getJsonToBean(net.sf.json.JSONObject.fromObject(alipayParm).get("alipay_trade_refund_response").toString(), AlipayTradeRefundResponse.class);
                        if (StringUtils.isNotEmpty(wdq.getCode()) && !wdq.getCode().equals("10000")) {
                            throw new BadRequestException("退款失败，" + wdq.getSubMsg());
                        }
                        if (storeOrder.getRefundStatus() == 2 || storeOrder.getNeedRefund() == 2) {
                            return;
                        }
                        //修改状态
                        storeOrder.setId(storeOrder.getId());
                        storeOrder.setRefundStatus(2);
                        storeOrder.setRefundPrice(new BigDecimal(wdq.getRefundFee()));
                        storeOrder.setRefundFactTime(new Date());
                        updateById(storeOrder);
                        try {
                            if (ProjectNameEnum.ZHONGANPUYAO.getValue().equals(storeOrder.getProjectCode()) || ProjectNameEnum.ZHONGANMANBING.getValue().equals(storeOrder.getProjectCode())) {
                                cn.hutool.json.JSONObject jsonObject = JSONUtil.createObj();
                                jsonObject.put("orderNo", storeOrder.getOrderId());
                                jsonObject.put("status", storeOrder.getStatus().toString());
                                jsonObject.put("desc", "众安普药已退款订单");
                                jsonObject.put("time", DateUtil.now());
                                mqProducer.sendDelayQueue(bizRoutekeyZhonganpuyao, jsonObject.toString(),2000);

                            } else if (ProjectNameEnum.MEIDEYI.getValue().equals(storeOrder.getProjectCode())) {
                                cn.hutool.json.JSONObject jsonObject = JSONUtil.createObj();
                                jsonObject.put("orderNo", storeOrder.getOrderId());
                                jsonObject.put("status", "-2");
                                jsonObject.put("desc", "美德医已退款订单");
                                jsonObject.put("time", DateUtil.now());
                                mqProducer.sendDelayQueue(bizRoutekeyMeideyi, jsonObject.toString(),2000);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }else if(storeOrder.getPayType().equals("mapi")) {
                    String mapiParm="";
                    if("h5".equals(storeOrder.getPayFrom())){
                        mapiParm=MapiPayUtils.refundOrder(storeOrder.getPayOutTradeNo(),new DecimalFormat("0.00").format(resources.getPayPrice()),storeOrder.getOrderId());
                    }
                    if(StringUtils.isEmpty(mapiParm) ){
                        throw new BadRequestException("退款失败，请联系管理员。");
                    }

                    if (storeOrder.getRefundStatus() == 2 || storeOrder.getNeedRefund() == 2) {
                        return;
                    }
                    net.sf.json.JSONObject jsonObjectMapiParm=  net.sf.json.JSONObject.fromObject(mapiParm);
                    //修改状态
                    storeOrder.setId(storeOrder.getId());
                    storeOrder.setRefundStatus(2);
                    storeOrder.setNeedRefund(2);
                    storeOrder.setRefundPrice(new BigDecimal(Integer.valueOf(jsonObjectMapiParm.get("refundAmt").toString())/100 ));
                    storeOrder.setRefundFactTime(new Date());
                    updateById(storeOrder);
                    try {
                        if (ProjectNameEnum.ZHONGANPUYAO.getValue().equals(storeOrder.getProjectCode()) || ProjectNameEnum.ZHONGANMANBING.getValue().equals(storeOrder.getProjectCode())) {
                            cn.hutool.json.JSONObject jsonObject = JSONUtil.createObj();
                            jsonObject.put("orderNo", storeOrder.getOrderId());
                            jsonObject.put("status", storeOrder.getStatus().toString());
                            jsonObject.put("desc", "众安普药已退款订单");
                            jsonObject.put("time", DateUtil.now());
                            mqProducer.sendDelayQueue(bizRoutekeyZhonganpuyao, jsonObject.toString(),2000);

                        } else if (ProjectNameEnum.MEIDEYI.getValue().equals(storeOrder.getProjectCode())) {
                            cn.hutool.json.JSONObject jsonObject = JSONUtil.createObj();
                            jsonObject.put("orderNo", storeOrder.getOrderId());
                            jsonObject.put("status", "-2");
                            jsonObject.put("desc", "美德医已退款订单");
                            jsonObject.put("time", DateUtil.now());
                            mqProducer.sendDelayQueue(bizRoutekeyMeideyi, jsonObject.toString(),2000);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }else{
                    String mchName="";
                    Project product=projectService.getOne(new LambdaQueryWrapper<Project>().eq(Project::getProjectCode,storeOrder.getProjectCode()),false);
                    if(product!=null && StringUtils.isNotEmpty(product.getMchName())){
                        mchName=product.getMchName();
                    }else{
                        YxSystemStoreQueryVo yxSystemStoreQueryVo= systemStoreService.getYxSystemStoreById(storeOrder.getStoreId());
                        if(yxSystemStoreQueryVo!=null && StringUtils.isNotEmpty(yxSystemStoreQueryVo.getMchName())){
                            mchName=yxSystemStoreQueryVo.getMchName();
                        }
                    }

                    if(OrderInfoEnum.PAY_CHANNEL_7.getValue().equals(storeOrder.getIsChannel())) {
                        miniPayService.refundOrder4zhongan(StringUtils.isEmpty(storeOrder.getTradeNo())?orderId:storeOrder.getTradeNo(),
                                bigDecimal.multiply(resources.getPayPrice()).intValue(),mchName);
                    }else if(StrUtil.isNotBlank(storeOrder.getPayType()) && storeOrder.getPayType().contains(PayTypeEnum.ZhongAnPay.getValue())) {
                        miniPayService.refundOrder4zhongan(StringUtils.isEmpty(storeOrder.getTradeNo())?orderId:storeOrder.getTradeNo(),
                                bigDecimal.multiply(resources.getPayPrice()).intValue(),mchName);
                    }/*else if(ProjectNameEnum.ZHONGANMANBING.getValue().equals(storeOrder.getProjectCode()) *//*|| ProjectNameEnum.ZHONGANPUYAO.getValue().equals(storeOrder.getProjectCode())*//*){
                        miniPayService.refundOrder4zhongan(StringUtils.isEmpty(storeOrder.getTradeNo())?orderId:storeOrder.getTradeNo(),
                                bigDecimal.multiply(resources.getPayPrice()).intValue(),mchName);
                    }*/else if(OrderInfoEnum.PAY_CHANNEL_1.getValue().equals(storeOrder.getIsChannel())){
//                        miniPayService.refundOrder(StringUtils.isEmpty(storeOrder.getTradeNo())?orderId:storeOrder.getTradeNo(),
//                                bigDecimal.multiply(resources.getPayPrice()).intValue(),mchName);
                        wechatConfigurationService.refundRoutineOrder(StringUtils.isEmpty(storeOrder.getTradeNo())?orderId:storeOrder.getTradeNo(),
                                bigDecimal.multiply(resources.getPayPrice()).intValue(),storeOrder.getMerchantNumber());

                    }else{
//                        payService.refundOrder(StringUtils.isEmpty(storeOrder.getTradeNo())?orderId:storeOrder.getTradeNo(),
//                                bigDecimal.multiply(resources.getPayPrice()).intValue());
                        wechatConfigurationService.refundH5Order(StringUtils.isEmpty(storeOrder.getTradeNo())?orderId:storeOrder.getTradeNo(),
                                bigDecimal.multiply(resources.getPayPrice()).intValue(),storeOrder.getMerchantNumber());
                    }
                }

              /*  if(ProjectNameEnum.TAIPING_LEXIANG.getValue().equals(storeOrder.getProjectCode())) {
                    yiyaobaoOrderService.sendOrder2YiyaobaoCloudCancel(resources.getOrderId());
                }
*/
                // 判断是否是互联网医院的处方生成的订单，如果是，则通知互联网医院
               InternetHospitalDemand internetHospitalDemand = internetHospitalDemandService.getOne( new QueryWrapper<InternetHospitalDemand>().eq("order_id",storeOrder.getOrderId()).select("prescription_code"),false);
               if(internetHospitalDemand != null) {
                   xkProcessService.refundNotice(internetHospitalDemand.getPrescriptionCode());
               }
            } catch (WxPayException e) {
                log.info("refund-error:{}",e.getMessage());
            }

        }
    }

    public static void main(String[] args) {
//        String url = "http://10.80.28.4:4000/prescriptionService/cancelOrder";
//        JSONObject jsonObject = new JSONObject();
//        jsonObject.put("voucher", "20210722009010080000000000008220000001043");
//        jsonObject.put("type", "1");
//        String requestBody = jsonObject.toString(); //
//
//        try {
//            long timestamp = System.currentTimeMillis(); // 生成签名时间戳
//            Map<String, String> headers = new HashMap<String, String>();
//            headers.put("ACCESS_APPID", "SYXKYYY"); // 设置APP
//            headers.put("ACCESS_TIMESTAMP", String.valueOf(timestamp)); // 设置签名时间戳
//            String ACCESS_SIGANATURE = AppSiganatureUtils
//                    .createSiganature(requestBody, "SYXKYYY", "ffa2d2b6-885c-47dc-a5f8-df9be3d5ce80",
//                            timestamp);
//            headers.put("ACCESS_SIGANATURE", ACCESS_SIGANATURE); // 生成并设置签名
//            log.info("ACCESS_APPID={}", "SYXKYYY");
//            log.info("ACCESS_TIMESTAMP={}", String.valueOf(timestamp));
//            log.info("ACCESS_SIGANATURE={}", ACCESS_SIGANATURE);
//            log.info("url={}", url);
//            log.info("requestBody={}", requestBody);
//            String result = HttpUtils.postJsonHttps(url, requestBody, headers); // 发起调用
//            log.info("取消订单下发益药宝，结果：{}", result);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        System.out.println(MshStatusEnum.OrderStatus.YFH.getCode());
        System.out.println("3".equals(MshStatusEnum.OrderStatus.YFH.getCode().toString()) );
    }



    @Override
    public void syncOrderStatus() {
        QueryWrapper<YxStoreOrder> queryWrapper = new QueryWrapper();
        // 状态2，已收货待评价
        queryWrapper.notIn("status", OrderStatusEnum.STATUS_3.getValue(),OrderStatusEnum.STATUS_7.getValue(),OrderStatusEnum.STATUS_8.getValue());
        queryWrapper.ne("type","慈善赠药");
        queryWrapper.eq("project_code","");
        ;

        List<YxStoreOrder> orderList = baseMapper.selectList(queryWrapper);
        for (YxStoreOrder order :orderList) {
            String orderId = order.getOrderId();
            OrderVo orderVo = yiyaobaoOrdOrderMapper.getYiyaobaoOrderbyOrderId(orderId);
            if(orderVo == null) {
                continue;
            }
            /*OrderPartInfoVo orderPartInfoVo = yiyaobaoOrderService.getYiyaobaoOrder(orderId);
            if(orderPartInfoVo == null) {
                continue;
            }*/
            String yiyaobaoStatus = orderVo.getStatusCode();
            log.info("orderNo:[{}],status:[{}],yiyaobaoStatus:[{}]",orderId,order.getStatus(),yiyaobaoStatus);
            Integer status ;

            Integer payTime = null;

            if(orderVo.getPayTime() != null) {
                payTime = OrderUtil.dateToTimestamp(orderVo.getPayTime()) ;
            }

            Integer paid = 0;
            if("10".equals(orderVo.getPayResult())) {
                paid = 1;
            } else {
                paid = 0;
            }

            if(yiyaobaoStatus.equals("01")) {  //待审核
                status=5;


            }else if(yiyaobaoStatus.equals("14") || yiyaobaoStatus.equals("15")) {  //待支付
                status=0;


            }else if(yiyaobaoStatus.equals("20") || yiyaobaoStatus.equals("25")  || yiyaobaoStatus.equals("30") ||
                    yiyaobaoStatus.equals("31") || yiyaobaoStatus.equals("35")  || yiyaobaoStatus.equals("36") ||
                    yiyaobaoStatus.equals("38") || yiyaobaoStatus.equals("40")  || yiyaobaoStatus.equals("41") ||
                    yiyaobaoStatus.equals("42")
            ) { //待发货
                status=0;


            } else if(yiyaobaoStatus.equals("43")){ //待收货
                status=1;


            } else if (yiyaobaoStatus.equals("50") ){  // 已收货，待评价
                status=3;
                paid = 1;

            } else if( yiyaobaoStatus.equals("45")) {
                status=3;
            }
            else if(yiyaobaoStatus.equals("98")) {
                status = 8;
            }else if(yiyaobaoStatus.equals("80")) {
                status = 7;
            }
            else {  // 其他状态
                status=6;

            }
            if(ProjectNameEnum.ROCHE_SMA.getValue().equals(order.getProjectCode())) {
                if(status == 1 || status == 3) {  // 已发货 或者 已关闭
                    UpdateWrapper<YxStoreOrder> updateWrapper = new UpdateWrapper<>();
                    updateWrapper.set("status",status);
                    updateWrapper.set("yiyaobao_order_id",orderVo.getId());
                    if(StrUtil.isNotBlank(orderVo.getFreightNo())) {
                        updateWrapper.set("delivery_name",orderVo.getLogisticsName());
                        updateWrapper.set("delivery_type","express");
                        updateWrapper.set("delivery_id",orderVo.getFreightNo());
                    }

                    updateWrapper.eq("id",order.getId());
                    update(updateWrapper);
                }

            } else {
                UpdateWrapper<YxStoreOrder> updateWrapper = new UpdateWrapper<>();
                updateWrapper.set("status",status);
                updateWrapper.set("paid",paid);
                updateWrapper.set("pay_time",payTime);
                updateWrapper.set("yiyaobao_order_id",orderVo.getId());
                if(StrUtil.isNotBlank(orderVo.getFreightNo())) {
                    updateWrapper.set("delivery_name",orderVo.getLogisticsName());
                    updateWrapper.set("delivery_type","express");
                    updateWrapper.set("delivery_id",orderVo.getFreightNo());
                }

                updateWrapper.eq("id",order.getId());
                update(updateWrapper);
            }

        }
    }


    @Override
    public void syncRocheOrderStatus() {
        QueryWrapper<YxStoreOrder> queryWrapper = new QueryWrapper();
        // 状态 待收货
        queryWrapper.in("status",OrderStatusEnum.STATUS_0.getValue(),OrderStatusEnum.STATUS_1.getValue());
        queryWrapper.eq("paid",1);
        queryWrapper.eq("upload_yiyaobao_flag",1);
        queryWrapper.eq("project_code",ProjectNameEnum.ROCHE_SMA.getValue());


        List<YxStoreOrder> orderList = baseMapper.selectList(queryWrapper);
        for (YxStoreOrder order :orderList) {
            String orderId = order.getOrderId();
            OrderVo orderVo = yiyaobaoOrdOrderMapper.getYiyaobaoOrderbyOrderIdSample(orderId);
            if(orderVo == null) {
                continue;
            }
            String yiyaobaoStatus = orderVo.getStatusCode();
            log.info("orderNo:[{}],status:[{}],yiyaobaoStatus:[{}]",orderId,order.getStatus(),yiyaobaoStatus);
            Integer status = order.getStatus();

            if(yiyaobaoStatus.equals("43")) {
                status = 1;
            } else if(yiyaobaoStatus.equals("50") || yiyaobaoStatus.equals("45")) {
                status = 3;
            }

            if(status == 1 || status == 3) {  // 已发货 或者 已关闭
                UpdateWrapper<YxStoreOrder> updateWrapper = new UpdateWrapper<>();
                updateWrapper.set("status",status);
                updateWrapper.set("yiyaobao_order_id",orderVo.getId());
                if(StrUtil.isNotBlank(orderVo.getFreightNo())) {
                    updateWrapper.set("delivery_name",orderVo.getLogisticsName());
                    updateWrapper.set("delivery_type","express");
                    updateWrapper.set("delivery_id",orderVo.getFreightNo());
                }
                updateWrapper.eq("id",order.getId());
                update(updateWrapper);
            }
        }
    }

    @Override
    public YxStoreOrder createOrder(YxStoreOrder4PCDto orderDto) {
        /*判断是否已经存在订单，如果存在，则退出*/
     /*   QueryWrapper<YxStoreOrder> queryWrapper_order = new QueryWrapper<>();
        queryWrapper_order.eq("order_id",orderDto.getOrder_sn());
        YxStoreOrder order = this.getOne(queryWrapper_order);
        if(order != null) {
            return order;
        }*/
        YxStoreOrder order = null;
        String realName = orderDto.getRealName();
        String phone = orderDto.getUserPhone();
     //   String province = orderDto.getProvince();
      //  String city = orderDto.getCity();
      //  String district = orderDto.getCounty();
        String detail = orderDto.getUserAddress();

        String imagePath= "";
        if(CollUtil.isNotEmpty(orderDto.getImagePathList())) {
            imagePath = CollUtil.join(orderDto.getImagePathList(),",");
        }

        Integer uid = 0;

        Integer isNew = 1;
        Integer combinationId = 0;
        Integer seckillId=0;
        Integer bargainId=0;
        Integer storeId=136;
        String departmentCode="";
        String partnerCode = "";
        if( StrUtil.isNotBlank(orderDto.getPartnerCode())) {
            partnerCode=orderDto.getPartnerCode();
        } else {
            partnerCode= "";
        }

        String projectCode = "";
        if( StrUtil.isNotBlank(orderDto.getProjectCode())) {
            projectCode=orderDto.getProjectCode();
        } else {
            projectCode= "";
        }

        String refereeCode="";

        YxUser yxUser = yxUserService.getOne(new QueryWrapper<YxUser>().eq("phone",phone),false);
        if(yxUser == null) {
            uid = 10000000 + Long.valueOf(redisUtils.incr("patient",1)).intValue();

            //用户保存
            YxUser user = new YxUser();
            user.setAccount(uid.toString());


            user.setUsername(uid.toString());

            user.setPassword(passwordEncoder.encode(ShopConstants.YSHOP_DEFAULT_PWD));
            user.setPwd(passwordEncoder.encode(ShopConstants.YSHOP_DEFAULT_PWD));
            user.setPhone("");
            user.setUserType(AppFromEnum.WECHAT.getValue());
            user.setLoginType(AppFromEnum.WECHAT.getValue());
            user.setAddTime(OrderUtil.getSecondTimestampTwo());
            user.setLastTime(OrderUtil.getSecondTimestampTwo());
            user.setNickname(uid.toString());
            user.setAvatar("");
            user.setNowMoney(BigDecimal.ZERO);
            user.setBrokeragePrice(BigDecimal.ZERO);
            user.setIntegral(BigDecimal.ZERO);

            yxUserService.save(user);

            uid = user.getUid();
        } else {
            uid = yxUser.getUid();
        }

        // 添加购物车
        List<Integer> cartIdList = new ArrayList<>();
        for(OrderDetailDto detailDto : orderDto.getDetails()) {
            Integer storeCartId = storeCartService.addCart(uid,detailDto.getDrug_id(),detailDto.getQuantity(),detailDto.getUnique()
                    ,"product",isNew,combinationId,seckillId,bargainId,departmentCode,partnerCode,refereeCode,projectCode);
            cartIdList.add(storeCartId);
        }


        String cartId = CollUtil.join(cartIdList,",");

        Map<String, Object> cartGroup = storeCartService.getUserProductCartList4Store(uid,cartId,1,projectCode);


        if(ObjectUtil.isNotEmpty(cartGroup.get("invalid"))){
            log.error("有失效的商品请重新提交");
            return order;
        }
        if(ObjectUtil.isEmpty(cartGroup.get("valid"))){
            log.error("没有选择有效商品请重新提交");
            return order;
        }
        List<YxStoreCartQueryVo> cartInfo = (List<YxStoreCartQueryVo>)cartGroup.get("valid");


        PriceGroupDTO priceGroup = getOrderPriceGroup(cartInfo);

        ConfirmOrderDTO confirmOrderDTO = new ConfirmOrderDTO();

        confirmOrderDTO.setUsableCoupon(couponUserService
                .beUsableCoupon(uid,priceGroup.getTotalPrice()));
        //积分抵扣
        OtherDTO other = new OtherDTO();
        other.setIntegralRatio(systemConfigService.getData("integral_ratio"));
        other.setIntegralFull(systemConfigService.getData("integral_full"));
        other.setIntegralMax(systemConfigService.getData("integral_max"));
        other.setStoreId(storeId);
        other.setPartnerCode(partnerCode);
        other.setProjectCode(projectCode);

        String orderKey = cacheOrderInfo(uid,cartInfo,
                priceGroup,other);


        // 收货地址



        YxUserAddress userAddress = new YxUserAddress();
        userAddress.setRealName(realName);
        userAddress.setPhone(phone);
        userAddress.setProvince(orderDto.getProvinceName());
        userAddress.setCity(orderDto.getCityName());
        userAddress.setDistrict(orderDto.getDistrictName());
        userAddress.setDetail(orderDto.getUserAddress());
        userAddress.setUid(uid);
        yxUserAddressService.save(userAddress);


        OrderParam param = new OrderParam();
        param.setAddressId(userAddress.getId().toString());
        param.setIsChannel(OrderInfoEnum.PAY_CHANNEL_3.getValue());
        param.setBargainId(0);
        param.setCombinationId(0);
        param.setCouponId(0);
        param.setFrom(AppFromEnum.PC.getValue());
        param.setImagePath(imagePath);
        param.setMark("");
        param.setPayType(PayTypeEnum.OFFLINE.getValue());
        param.setPhone(phone);
        param.setPinkId(0);
        param.setRealName(realName);
        param.setSeckillId(0);
        param.setShippingType(OrderInfoEnum.SHIPPIING_TYPE_1.getValue());
      //  param.setOrderNo(orderDto.getOrder_sn());
        param.setUseIntegral(0d);

       /* OrderExternalParam param = new OrderExternalParam();
        param.setImagePath(imagePath);
        param.setPayType("慈善赠药");
        param.setAddress(userAddress);
        param.setMark("正大天晴");
        param.setType("慈善赠药");
        param.setOrderNo(orderDto.getOrder_sn());*/
        //创建订单

        try{
            lock.lock();
            order = createOrder4Store(uid,orderKey,param);
        }finally {
            lock.unlock();
        }

        return order;
    }

    public PriceGroupDTO getOrderPriceGroup(List<YxStoreCartQueryVo> cartInfo) {

        String storePostageStr = systemConfigService.getData("store_postage");//邮费基础价
        Double storePostage = 0d;
        if(StrUtil.isNotEmpty(storePostageStr)) storePostage = Double.valueOf(storePostageStr);

        String storeFreePostageStr = systemConfigService.getData("store_free_postage");//满额包邮
        Double storeFreePostage = 0d;
        if(StrUtil.isNotEmpty(storeFreePostageStr)) storeFreePostage = Double.valueOf(storeFreePostageStr);

        Double totalPrice = getOrderSumPrice(cartInfo, "truePrice");//获取订单总金额 （商品原价）
        Double costPrice = getOrderSumPrice(cartInfo, "costPrice");//获取订单成本价
        Double vipPrice = getOrderSumPrice(cartInfo, "vipTruePrice");//获取订单会员优惠金额 （会员价）
        Double innerPrice = getOrderSumPrice(cartInfo, "innerPrice");//获取订单内购金额

        if(storeFreePostage == 0){//包邮
            storePostage = 0d;
        }else{
            for (YxStoreCartQueryVo storeCart : cartInfo) {
                if(storeCart.getProductInfo().getIsPostage() == 0){//不包邮
                    storePostage = NumberUtil.add(storePostage
                            ,storeCart.getProductInfo().getPostage()).doubleValue();
                }
            }
            //如果总价大于等于满额包邮 邮费等于0
            if (storeFreePostage <= totalPrice) storePostage = 0d;
        }

        PriceGroupDTO priceGroupDTO = new PriceGroupDTO();
        priceGroupDTO.setStorePostage(storePostage);
        priceGroupDTO.setStoreFreePostage(storeFreePostage);
        priceGroupDTO.setTotalPrice(totalPrice);
        priceGroupDTO.setCostPrice(costPrice);
        priceGroupDTO.setVipPrice(vipPrice);
        priceGroupDTO.setInnerPrice(innerPrice);
        return priceGroupDTO;
    }

    public Double getOrderSumPrice(List<YxStoreCartQueryVo> cartInfo, String key) {
        BigDecimal sumPrice = BigDecimal.ZERO;
        // 商品原价
        if(key.equals("truePrice")){
            for (YxStoreCartQueryVo storeCart : cartInfo) {
                sumPrice = NumberUtil.add(sumPrice,NumberUtil.mul(storeCart.getCartNum(),storeCart.getTruePrice()));
            }
        }else if(key.equals("costPrice")){
            for (YxStoreCartQueryVo storeCart : cartInfo) {
                sumPrice = NumberUtil.add(sumPrice,
                        NumberUtil.mul(storeCart.getCartNum(),storeCart.getCostPrice()));
            }
        }else if(key.equals("vipTruePrice")){  // vip价
            for (YxStoreCartQueryVo storeCart : cartInfo) {
                sumPrice = NumberUtil.add(sumPrice,
                        NumberUtil.mul(storeCart.getCartNum(),storeCart.getVipTruePrice()));
            }
        }else if(key.equals("innerPrice")){  // 内购价
            for (YxStoreCartQueryVo storeCart : cartInfo) {
                sumPrice = NumberUtil.add(sumPrice,
                        NumberUtil.mul(storeCart.getCartNum(),storeCart.getInnerPrice()));
            }
        }

        //System.out.println("sumPrice:"+sumPrice);
        return sumPrice.doubleValue();
    }


    public String cacheOrderInfo(int uid, List<YxStoreCartQueryVo> cartInfo, PriceGroupDTO priceGroup, OtherDTO other) {
        String key = IdUtil.simpleUUID();
        CacheDTO cacheDTO = new CacheDTO();
        cacheDTO.setCartInfo(cartInfo);
        cacheDTO.setPriceGroup(priceGroup);
        cacheDTO.setOther(other);
        // redisService.saveCode("user_order_"+uid+key,cacheDTO,600L);
        redisUtils.set("user_order_"+uid+key,cacheDTO,600L);
        return key;
    }


    /**
     * 创建订单-多门店
     * @param uid uid
     * @param key key
     * @param param param
     * @return
     */

    public YxStoreOrder createOrder4Store(int uid, String key, OrderParam param) {
        JSONArray jsonArray = JSONUtil.createArray();

       // YxUserQueryVo userInfo = userService.getYxUserById(uid);
      //  if(ObjectUtil.isNull(userInfo)) throw new ErrorRequestException("用户不存在");

        CacheDTO cacheDTO = getCacheOrderInfo(uid,key);
        if(ObjectUtil.isNull(cacheDTO)){
            throw new ErrorRequestException("订单已过期,请刷新当前页面");
        }

        List<YxStoreCartQueryVo> cartInfo = cacheDTO.getCartInfo();
        Double totalPrice =  cacheDTO.getPriceGroup().getTotalPrice();
        Double payPrice = cacheDTO.getPriceGroup().getTotalPrice();
        Double payPostage = cacheDTO.getPriceGroup().getStorePostage();
        OtherDTO other = cacheDTO.getOther();
        YxUserAddress userAddress = null;
        if(OrderInfoEnum.SHIPPIING_TYPE_1.getValue().equals(param.getShippingType())){
            if(StrUtil.isEmpty(param.getAddressId())) throw new ErrorRequestException("请选择收货地址");
            userAddress = yxUserAddressService.getById(param.getAddressId());
            if(ObjectUtil.isNull(userAddress)) throw new ErrorRequestException("地址选择有误");
        }

        Integer totalNum = 0;
        Integer gainIntegral = 0;
        List<String> cartIds = new ArrayList<>();
        int combinationId = 0;
        int seckillId = 0;
        int bargainId = 0;

        for (YxStoreCartQueryVo cart : cartInfo) {
            combinationId = cart.getCombinationId();
            seckillId = cart.getSeckillId();
            bargainId = cart.getBargainId();
            cartIds.add(cart.getId().toString());
            totalNum += cart.getCartNum();
            //计算积分
            BigDecimal cartInfoGainIntegral = BigDecimal.ZERO;
            if(combinationId == 0 && seckillId == 0 && bargainId == 0){//拼团等活动不参与积分
                if(cart.getProductInfo().getGiveIntegral().intValue() > 0){
                    cartInfoGainIntegral = NumberUtil.mul(cart.getCartNum(),cart.
                            getProductInfo().getGiveIntegral());
                }
                gainIntegral = NumberUtil.add(gainIntegral,cartInfoGainIntegral).intValue();
            }
            cn.hutool.json.JSONObject jsonObject = JSONUtil.createObj();
            jsonObject.put("sku",cart.getYiyaobaoSku());
            jsonObject.put("unitPrice",cart.getTruePrice());
            jsonObject.put("amount",cart.getCartNum());

            jsonArray.add(jsonObject);

        }


        //门店

        if(OrderInfoEnum.SHIPPIING_TYPE_1.getValue().equals(param.getShippingType())){
            payPrice = NumberUtil.add(payPrice,payPostage);
        }else{
            payPostage = 0d;
        }

        //优惠券
        int couponId = 0;
        if(ObjectUtil.isNotEmpty(param.getCouponId())){
            couponId = param.getCouponId().intValue();
        }

        int useIntegral = param.getUseIntegral().intValue();

        boolean deduction = false;//拼团等
        //拼团等不参与抵扣
        if(combinationId > 0 || seckillId > 0 || bargainId > 0) deduction = true;
        if(deduction){
            couponId = 0;
            useIntegral = 0;
        }
        double couponPrice = 0; //优惠券金额

        // 积分抵扣
        double deductionPrice = 0; //抵扣金额
        double usedIntegral = 0; //使用的积分



        if(payPrice <= 0) payPrice = 0d;

        // 整理数据，发送至益药宝

        // 获取药店信息
    //    Integer storeId = other.getStoreId();
      //  YxSystemStore yxSystemStore = systemStoreService.getById(storeId);


    //    String yiyaobao_store_id = yxSystemStore.getYiyaobaoId();

      //  String projectCode = other.getProjectCode();
   /*     if(StrUtil.isNotBlank(projectCode)) {
            List<Product4project> product4projectList = product4projectService.list(new QueryWrapper<Product4project>().eq("project_no",projectCode));
            if(CollUtil.isNotEmpty(product4projectList)) {
                yiyaobao_projectNo = product4projectList.get(0).getYiyaobaoProjectCode();
            }
        }*/

        // 发送到益药宝，获得订单号码
      //  String orderSn = uploadOrder2Yiyaobao(param,userAddress,yiyaobao_projectNo,yiyaobao_store_id,jsonArray.toString());

        //生成分布式唯一值
         String orderSn = IdUtil.getSnowflake(0,0).nextIdStr();
        //组合数据
        YxStoreOrder storeOrder = new YxStoreOrder();
        storeOrder.setUid(uid);
        storeOrder.setOrderId(orderSn);



        storeOrder.setRealName(userAddress.getRealName());
        storeOrder.setUserPhone(userAddress.getPhone());
        storeOrder.setUserAddress(userAddress.getProvince()+" "+userAddress.getCity()+
                " "+userAddress.getDistrict()+" "+userAddress.getDetail());
        storeOrder.setCartId(StrUtil.join(",",cartIds));
        storeOrder.setTotalNum(totalNum);
        storeOrder.setTotalPrice(BigDecimal.valueOf(totalPrice));
        storeOrder.setTotalPostage(BigDecimal.valueOf(payPostage));
        storeOrder.setCouponId(couponId);
        storeOrder.setCouponPrice(BigDecimal.valueOf(couponPrice));
        storeOrder.setPayPrice(BigDecimal.valueOf(payPrice));
        storeOrder.setPayPostage(BigDecimal.valueOf(payPostage));
        storeOrder.setDeductionPrice(BigDecimal.valueOf(deductionPrice));
        if(OrderInfoEnum.PAY_CHANNEL_2.getValue() == param.getIsChannel()  ) {
            storeOrder.setPaid(OrderInfoEnum.PAY_STATUS_1.getValue());
            storeOrder.setPayTime(OrderUtil.getSecondTimestampTwo());
            storeOrder.setStatus(OrderStatusEnum.STATUS_1.getValue());
            storeOrder.setType("慈善赠药");
        } else if(OrderInfoEnum.PAY_CHANNEL_3.getValue() == param.getIsChannel()) {
            storeOrder.setPaid(OrderInfoEnum.PAY_STATUS_1.getValue());
            storeOrder.setPayTime(OrderUtil.getSecondTimestampTwo());
            storeOrder.setStatus(OrderStatusEnum.STATUS_5.getValue());
            storeOrder.setType("第三方商城订单");
        } else {
            storeOrder.setPaid(OrderInfoEnum.PAY_STATUS_0.getValue());
            storeOrder.setStatus(OrderStatusEnum.STATUS_5.getValue());
            storeOrder.setType("需求单");
        }


        storeOrder.setPayType(param.getPayType());
        storeOrder.setUseIntegral(BigDecimal.valueOf(usedIntegral));
        storeOrder.setGainIntegral(BigDecimal.valueOf(gainIntegral));
        storeOrder.setMark(param.getMark());
        storeOrder.setCombinationId(combinationId);
        storeOrder.setPinkId(param.getPinkId());
        storeOrder.setSeckillId(seckillId);
        storeOrder.setBargainId(bargainId);
        storeOrder.setCost(BigDecimal.valueOf(cacheDTO.getPriceGroup().getCostPrice()));
        storeOrder.setImagePath(param.getImagePath());
        if(AppFromEnum.ROUNTINE.getValue().equals(param.getFrom())){
            storeOrder.setIsChannel(OrderInfoEnum.PAY_CHANNEL_1.getValue());
        }else if (AppFromEnum.CSZY.getValue().equals(param.getFrom())){
            storeOrder.setIsChannel(OrderInfoEnum.PAY_CHANNEL_2.getValue());
        }else if(AppFromEnum.PC.getValue().equals(param.getFrom())){
            storeOrder.setIsChannel(OrderInfoEnum.PAY_CHANNEL_3.getValue());
        } else{
            storeOrder.setIsChannel(OrderInfoEnum.PAY_CHANNEL_0.getValue());
        }
        storeOrder.setAddTime(OrderUtil.getSecondTimestampTwo());
        storeOrder.setUnique(key);
        storeOrder.setShippingType(param.getShippingType());

        storeOrder.setPartnerCode(other.getPartnerCode());
        storeOrder.setProjectCode(other.getProjectCode());
        storeOrder.setRefereeCode(other.getRefereeCode());
        storeOrder.setDepartCode(other.getDepartCode());


        boolean res = save(storeOrder);
        if(!res) throw new ErrorRequestException("订单生成失败");

        //减库存加销量
        for (YxStoreCartQueryVo cart : cartInfo) {
            productService.decProductStock(cart.getCartNum(),cart.getProductId(),
                    cart.getProductAttrUnique());

        }

        //保存购物车商品信息
        orderCartInfoService.saveCartInfo(storeOrder.getId(),cartInfo);

        //购物车状态修改
        QueryWrapper<YxStoreCart> wrapper = new QueryWrapper<>();
        wrapper.in("id",cartIds);
        YxStoreCart cartObj = new YxStoreCart();
        cartObj.setIsPay(1);
        storeCartMapper.update(cartObj,wrapper);

        //删除缓存
        delCacheOrderInfo(uid,key);

        //增加状态
        orderStatusService.create(storeOrder.getId(),"cache_key_create_order","订单生成");


        //使用MQ延时消息
        //mqProducer.sendMsg("yshop-topic",storeOrder.getId().toString());
        //log.info("投递延时订单id： [{}]：", storeOrder.getId());

        //加入redis，30分钟自动取消
      /*  String redisKey = String.valueOf(StrUtil.format("{}{}",
                ShopConstants.REDIS_ORDER_OUTTIME_UNPAY, storeOrder.getId()));
        redisTemplate.opsForValue().set(redisKey, storeOrder.getOrderId() ,
                ShopConstants.ORDER_OUTTIME_UNPAY, TimeUnit.MINUTES);*/

        return storeOrder;
    }


    public CacheDTO getCacheOrderInfo(int uid, String key) {

        return (CacheDTO)redisUtils.get("user_order_"+uid+key);
    }

    public void delCacheOrderInfo(int uid, String key) {
        redisUtils.del("user_order_"+uid+key);
    }



    @Override
    //@Cacheable
    public Map<String, Object> queryAll4PC(YxStoreOrderQueryCriteria criteria, Pageable pageable) {
        getPage(pageable);
        PageInfo<YxStoreOrder> page = new PageInfo<>(queryAll(criteria));
        List<YxStoreOrder4PCDto> storeOrderDTOS = generator.convert(page.getList(),YxStoreOrder4PCDto.class);
        for (YxStoreOrder4PCDto yxStoreOrder : storeOrderDTOS) {

           String statusName = OrderUtil.orderStatusStr(yxStoreOrder.getPaid(),yxStoreOrder.getStatus(),1,yxStoreOrder.getRefundStatus());
            yxStoreOrder.setStatusName(statusName);

            if(StrUtil.isNotBlank(yxStoreOrder.getImagePath())) {
                yxStoreOrder.setImagePathList(Arrays.asList(yxStoreOrder.getImagePath().split(",")));
            }

            String address =  yxStoreOrder.getUserAddress();
            if( StrUtil.isNotBlank(address )) {

                List<String> addresslist = StrUtil.split(address, " ", 4 ,true, true);

                if(CollUtil.isNotEmpty(addresslist) && addresslist.size() == 4) {
                    yxStoreOrder.setProvinceName(addresslist.get(0));
                    yxStoreOrder.setCityName(addresslist.get(1));
                    yxStoreOrder.setDistrictName(addresslist.get(2));
                    yxStoreOrder.setUserAddress(addresslist.get(3));
                }

            }


            List<OrderDetailDto> details = new ArrayList<>();

            List<YxStoreOrderCartInfo> cartInfos = storeOrderCartInfoService.list(
                    new QueryWrapper<YxStoreOrderCartInfo>().eq("oid",yxStoreOrder.getId()));
            for(YxStoreOrderCartInfo cartInfo : cartInfos) {
                YxStoreCartQueryVo yxStoreCartQueryVo = JSONUtil.toBean(cartInfo.getCartInfo(),YxStoreCartQueryVo.class);

                OrderDetailDto orderDetailDto = new OrderDetailDto();
                orderDetailDto.setDrug_id(yxStoreCartQueryVo.getProductId());
                orderDetailDto.setQuantity(yxStoreCartQueryVo.getCartNum());
                orderDetailDto.setUnique(yxStoreCartQueryVo.getProductAttrUnique());
                orderDetailDto.setPrice(yxStoreCartQueryVo.getTruePrice());
                orderDetailDto.setCommonName(yxStoreCartQueryVo.getProductInfo().getCommonName());
                orderDetailDto.setStoreName(yxStoreCartQueryVo.getProductInfo().getStoreName());
                orderDetailDto.setManufacturer(yxStoreCartQueryVo.getProductInfo().getManufacturer());
                orderDetailDto.setSpec(yxStoreCartQueryVo.getProductInfo().getSpec());
                orderDetailDto.setUnit(yxStoreCartQueryVo.getProductInfo().getUnit());

                details.add(orderDetailDto);
            }

            yxStoreOrder.setDetails(details);

        }
        Map<String, Object> map = new LinkedHashMap<>(2);
        map.put("content", storeOrderDTOS);
        map.put("totalElements", page.getTotal());
        return map;
    }

    @Override
    public List<OrderStatisticsDto> getStatistics() {

        return baseMapper.getStatistics();
    }

    @Override
    public void updateOrderInfo(OrderInfoParam orderInfoParam) {


        UpdateWrapper updateWrapper = new UpdateWrapper();
        updateWrapper.set("delivery_name",orderInfoParam.getDeliveryName());
        updateWrapper.set("delivery_id",orderInfoParam.getDeliveryId());
        updateWrapper.set("delivery_type","express");
        updateWrapper.set("status",1);
        updateWrapper.eq("order_id",orderInfoParam.getOrderNo());

        this.update(updateWrapper);

    }

    @Override
    public void orderCheck(YxStoreOrder resources) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("id",resources.getId());
        // queryWrapper.select("project_code","id");
        YxStoreOrder yxStoreOrder = getById(resources.getId());
        if(yxStoreOrder == null) {
            throw new BadRequestException("没有找到订单");
        }
        String userName = SecurityUtils.getUsername();
        UpdateWrapper updateWrapper = new UpdateWrapper();
        updateWrapper.set("check_fail_reason",resources.getCheckFailReason());
        updateWrapper.set("check_fail_remark",resources.getCheckFailRemark());
        updateWrapper.set("check_user",userName);
        updateWrapper.set("check_time", new Date());
        updateWrapper.set("check_status",resources.getCheckStatus());
        if( StrUtil.isNotBlank(resources.getCheckStatus()) && "不通过".equals(resources.getCheckStatus()) ) {
            updateWrapper.set("status",OrderStatusEnum.STATUS_6.getValue());
        } else if (StrUtil.isNotBlank(resources.getCheckStatus()) && "通过".equals(resources.getCheckStatus())) {
            updateWrapper.set("status",OrderStatusEnum.STATUS_0.getValue());
            if(ProjectNameEnum.ROCHE_SMA.getValue().equals(yxStoreOrder.getProjectCode())) {
                if(yxStoreOrder.getStoreId()==null){
                    throw new BadRequestException("请选择门店");
                }
                RocheStore rocheStore =rocheStoreService.getById(yxStoreOrder.getStoreId());
                if(rocheStore == null) {
                    throw new BadRequestException("请选择门店");
                }
                if(StringUtils.isEmpty(rocheStore.getPayeeBankName()) || StringUtils.isEmpty(rocheStore.getPayeeAccountName()) || StringUtils.isEmpty(rocheStore.getPayeeBankAccount())){
                    throw new BadRequestException("请维护门店收款信息。");
                }
                updateWrapper.set("payee_account_name",rocheStore.getPayeeAccountName());
                updateWrapper.set("payee_bank_name",rocheStore.getPayeeBankName());
                updateWrapper.set("payee_bank_account",rocheStore.getPayeeBankAccount());
            }
        }
        updateWrapper.eq("id",resources.getId());

        this.update(updateWrapper);

        //模板消息通知
        try {
            YxWechatUserDto wechatUser = generator.convert(wechatUserService.getOne(new QueryWrapper<YxWechatUser>().eq("uid",yxStoreOrder.getUid())),YxWechatUserDto.class);
            if (ObjectUtil.isNotNull(wechatUser)) {
                log.info("罗氏审核通知1");
                //公众号与小程序打通统一公众号模板通知
                if (StrUtil.isNotBlank(wechatUser.getRoutineOpenid())) {
                    log.info("罗氏审核通知2");
                    String page = "pages/wode/orderDetail?orderId="+yxStoreOrder.getOrderId();
                    OrderTemplateMessage message = new OrderTemplateMessage();
                    message.setOrderDate( OrderUtil.stampToDate(yxStoreOrder.getAddTime().toString()));
                    message.setOrderId(yxStoreOrder.getOrderId());
                    String orderStatus = "";
                    String remark = "";
                    if("通过".equals(resources.getCheckStatus())) {
                        orderStatus = "审核通过";
                        remark = "请点击进入 获取转账信息 请尽快完成支付";
                    } else {
                        orderStatus = "审核未通过";
                        remark = StrUtil.emptyToDefault(resources.getCheckFailReason(),"") + "  " + StrUtil.emptyToDefault(resources.getCheckFailRemark(),"");
                    }
                    message.setOrderStatus(orderStatus);
                    message.setRemark(remark);
                    templateService.sendDYTemplateMessage(wechatUser.getRoutineOpenid(),page,message);
                }
            }
        } catch (Exception e) {
            log.info("订单状态通知异常，订单号:{}",yxStoreOrder.getOrderId());
            e.printStackTrace();

        }
        if( StrUtil.isNotBlank(resources.getCheckStatus()) && "不通过".equals(resources.getCheckStatus()) ) {
            YxStoreOrderStatus storeOrderStatus = new YxStoreOrderStatus();
            storeOrderStatus.setOid(resources.getId());
            storeOrderStatus.setChangeType(OrderChangeTypeEnum.CHECK_FAIL.getValue());
            storeOrderStatus.setChangeMessage("订单审核完成");
            storeOrderStatus.setChangeTime(OrderUtil.getSecondTimestampTwo());
            yxStoreOrderStatusService.save(storeOrderStatus);
        }else if(StrUtil.isNotBlank(resources.getCheckStatus()) && "通过".equals(resources.getCheckStatus())) {
            YxStoreOrderStatus storeOrderStatus = new YxStoreOrderStatus();
            storeOrderStatus.setOid(resources.getId());
            storeOrderStatus.setChangeType(OrderChangeTypeEnum.CHECK_PASS.getValue());
            storeOrderStatus.setChangeMessage("订单审核完成");
            storeOrderStatus.setChangeTime(OrderUtil.getSecondTimestampTwo());
            yxStoreOrderStatusService.save(storeOrderStatus);
        }



    }

    @Override
    public void prescripStatus(PrescripStatusParam prescripStatusParam) {

        if( StrUtil.isBlank(prescripStatusParam.getPrescripNo())) {
           throw new BadRequestException("处方号缺失！");
        }

        if(StrUtil.isBlank(prescripStatusParam.getOrderId())) {
            throw new BadRequestException("益药宝主键id缺失！");
        }

        // 根据益药宝id获取订单来源
        String yiyaobaoOrderId = prescripStatusParam.getOrderId();
        String orderSource = yiyaobaoOrdOrderMapper.queryOrderSourceByOrderId(yiyaobaoOrderId);

        if("37".equals(orderSource)) {
            //oms-蚂蚁项目
            prescripStatusParam.setProjectCode("ant");
            prescripStatusParam.setProjectName("蚂蚁项目");
            JSONObject jsonObject = JSONUtil.parseObj(prescripStatusParam);

            log.info("转发oms数据：[{}]",jsonObject.toString());

            mqProducer.sendDelayQueue(antQueueName,jsonObject.toString(),2000);
            return;
        } else if ("14".equals(orderSource)) {
            // oms-镁信项目
            prescripStatusParam.setProjectCode("meditrust");
            prescripStatusParam.setProjectName("镁信商保");
            JSONObject jsonObject = JSONUtil.parseObj(prescripStatusParam);

            log.info("转发oms数据：[{}]",jsonObject.toString());

            mqProducer.sendDelayQueue(meditrustQueueName,jsonObject.toString(),2000);
        } else if ("48".equals(orderSource)) {
            // oms-君岭项目
            prescripStatusParam.setProjectCode("junling");
            prescripStatusParam.setProjectName("君岭项目");
            JSONObject jsonObject = JSONUtil.parseObj(prescripStatusParam);

            log.info("转发oms数据：[{}]",jsonObject.toString());

            mqProducer.sendDelayQueue(junlingQueueName,jsonObject.toString(),2000);
        } else if ("25".equals(orderSource)) {
            // oms-药联项目
            prescripStatusParam.setProjectCode("yaolian");
            prescripStatusParam.setProjectName("药联健康");
            JSONObject jsonObject = JSONUtil.parseObj(prescripStatusParam);

            log.info("转发oms数据：[{}]",jsonObject.toString());

            mqProducer.sendDelayQueue(bizRoutekeyYaolian,jsonObject.toString(),2000);
        } else if("32".equals(orderSource)) {
            // msh项目
            LambdaQueryWrapper<MshOrder> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(MshOrder::getYiyaobaoId,yiyaobaoOrderId);
            MshOrder mshOrder =  mshOrderMapper.selectOne(lambdaQueryWrapper);
            mshOrder.setExternalOrderId(prescripStatusParam.getOrderNo());
            YxStoreOrderStatus storeOrderStatus = new YxStoreOrderStatus();

            if("10".equals(prescripStatusParam.getPrescripStatus())) {
                mshOrder.setOrderStatus("1");
                mshOrder.setAuditTime(new Date());
                mshOrder.setAuditName(prescripStatusParam.getAuditName());
                //更新插入复购信息表
                mshOrderServiceImpl.createMshRepurchaseReminder(mshOrder.getId());
            } else if("2".equals(prescripStatusParam.getPrescripStatus())) {
                mshOrder.setOrderStatus("2");
                mshOrder.setAuditReasons(StringUtils.isEmpty(prescripStatusParam.getCheckFailReason())?prescripStatusParam.getCheckFailRemark():prescripStatusParam.getCheckFailReason());
                mshOrder.setAuditTime(new Date());
                mshOrder.setAuditName(prescripStatusParam.getAuditName());
            } else if("43".equals(prescripStatusParam.getPrescripStatus())) {
                mshOrder.setOrderStatus("3");
                mshOrder.setLogisticsNum(prescripStatusParam.getDeliveryId());
                mshOrder.setLogisticsName(prescripStatusParam.getDeliveryName());
                mshOrder.setShippingDate(new Date());
            } else if("45".equals(prescripStatusParam.getPrescripStatus())) {
                mshOrder.setOrderStatus("4");
                mshOrder.setLogisticsNum(prescripStatusParam.getDeliveryId());
                mshOrder.setLogisticsName(prescripStatusParam.getDeliveryName());
            } else if("98".equals(prescripStatusParam.getPrescripStatus())) {
                mshOrder.setOrderStatus("5");
            } else if("99".equals(prescripStatusParam.getPrescripStatus())){
                mshOrder.setOrderStatus("6");
                mshOrder.setAuditReasons(StringUtils.isEmpty(prescripStatusParam.getCheckFailReason())?prescripStatusParam.getCheckFailRemark():prescripStatusParam.getCheckFailReason());
                mshOrder.setAuditTime(new Date());
                mshOrder.setAuditName(prescripStatusParam.getAuditName());
            }
            mshOrderMapper.updateById(mshOrder);
            mshDemandListService.udpateMshDemandListAuditStatus(mshOrder.getDemandListId().toString());
            mqProducer.sendDelayQueue(mshQueueName,mshOrder.getDemandListId().toString(),2000);
            addMSHStoreOrderStatusTime(mshOrder);

        }  else {
            // 商城订单
            YxStoreOrder resources = getOne(new LambdaQueryWrapper<YxStoreOrder>().eq(YxStoreOrder::getYiyaobaoOrderId,yiyaobaoOrderId));
            Integer taipingStatus = 0;

            // 罗氏罕见病的订单不处理 审核状态
            if( resources != null &&   ProjectNameEnum.ROCHE_SMA.getValue().equals(resources.getProjectCode())) {
                return;
            }
            String status = "";
            String remark = "益药商城为您提供服务";
            String remindmessage = "";
            if("10".equals(prescripStatusParam.getPrescripStatus())) {
                resources.setStatus(OrderStatusEnum.STATUS_0.getValue());
                resources.setCheckTime(new Date());
                resources.setCheckStatus("通过");
                resources.setYiyaobaoOrderId(prescripStatusParam.getOrderId());
                resources.setYiyaobaoOrderNo(prescripStatusParam.getOrderNo());
                this.update(resources);
                status = "审核通过";
                remark = "您的需求单已经通过审核";

                taipingStatus = TaipingOrderStatusEnum.STATUS_11.getValue();

                remindmessage = "【益药】尊敬的客户，您的订单号：%s，订单状态：%s。%s。 请在益药商城小程序中及时查看订单详情。如有疑问，请致电400-9200-036";
                remindmessage = String.format(remindmessage,resources.getOrderId(), "审核通过","益药药房将尽快为您发货");
            }else if("2".equals(prescripStatusParam.getPrescripStatus())) {
              //  DateTime date = DateUtil.parse(prescripStatusParam.getDealDate(), DatePattern.NORM_DATE_FORMAT);
                resources.setStatus(OrderStatusEnum.STATUS_6.getValue());
                resources.setCheckTime(new Date());
                resources.setCheckStatus("不通过");
                resources.setCheckFailRemark(prescripStatusParam.getCheckFailRemark());
                resources.setCheckFailReason(prescripStatusParam.getCheckFailReason());
                resources.setYiyaobaoOrderId(prescripStatusParam.getOrderId());
                resources.setYiyaobaoOrderNo(prescripStatusParam.getOrderNo());
                if( (PayTypeEnum.WEIXIN.getValue().equals(resources.getPayType()) || PayTypeEnum.ALIPAY.getValue().equals(resources.getPayType()) || PayTypeEnum.ZhongAnPay.getValue().equals(resources.getPayType()) )&& new Integer(1).equals(resources.getPaid())) {
                    resources.setRefundStatus(1);
                    resources.setRefundReasonTime(OrderUtil.getSecondTimestampTwo());
                    resources.setRefundReasonWap( resources.getCheckFailReason() +" " + resources.getCheckFailRemark());
                }

                // 更新需退款字段
                resources.setNeedRefund(1);
                this.update(resources);

                status = "不通过";
                String checkFailReason = "";
                if(StrUtil.isNotBlank(resources.getCheckFailReason())) {
                    checkFailReason = resources.getCheckFailReason();
                }
                String checkFailRemark = "";
                if(StrUtil.isNotBlank(resources.getCheckFailRemark())) {
                    checkFailRemark = resources.getCheckFailRemark();
                }
                remark = checkFailReason + "  " + checkFailRemark;

                taipingStatus = TaipingOrderStatusEnum.STATUS_12.getValue();
                // 发送短信，通知管理员处理退款
                //todo 推送
                if( resources.getStoreId()!= null) {
                    YxSystemStore yxSystemStore = yxSystemStoreService.getById(resources.getStoreId());
                    if(yxSystemStore != null && StrUtil.isNotBlank(yxSystemStore.getLinkPhone())) {
                        List<String> phoneList= Arrays.asList(yxSystemStore.getLinkPhone().split(",")) ;
                        //发送短信
                        for(String phone :phoneList) {

                            String remindmessage_manager = "【益药】您有订单待处理，订单状态：%s。订单编号：%s";
                            remindmessage_manager = String.format(remindmessage_manager, "申请退款", resources.getOrderId());
                            smsService.sendTeddy("",remindmessage_manager,phone);
                        }
                    }
                }

                remindmessage = "【益药】尊敬的客户，您的订单号：%s，订单状态：%s。%s。 请在益药商城小程序中及时查看订单详情。如有疑问，请致电400-9200-036";
                remindmessage = String.format(remindmessage,resources.getOrderId(), "审核不通过", checkFailReason);

            } else if ("43".equals(prescripStatusParam.getPrescripStatus())) {  // 出库
                if(prescripStatusParam.getPrescripNo()!=null&&prescripStatusParam.getPrescripNo().contains("msh")){

                }else{
                    resources.setStatus(OrderStatusEnum.STATUS_1.getValue());
                    resources.setDeliveryName(prescripStatusParam.getDeliveryName());
                    resources.setDeliveryId(prescripStatusParam.getDeliveryId());
                    resources.setYiyaobaoOrderId(prescripStatusParam.getOrderId());

                    this.update(resources);

                    status = "已发货";
                    InternetHospitalDemand internetHospitalDemand = internetHospitalDemandService.getOne( new QueryWrapper<InternetHospitalDemand>().eq("order_id",resources.getOrderId()).select("prescription_code"),false);
                    if(internetHospitalDemand != null) {
                        XkExpress xkExpress = new XkExpress();
                        xkExpress.setPrescriptionCode(internetHospitalDemand.getPrescriptionCode());
                        xkExpress.setExpressCode(prescripStatusParam.getDeliveryId());
                        xkExpress.setExpressCompanyCode("shunfeng");
                        xkExpress.setExpressCompanyName("顺丰");
                        xkProcessService.expressNotice(xkExpress);
                    }

                    // 调用ebs，将推荐人传过去
                    if(ProjectNameEnum.DIAO.getValue().equals(resources.getProjectCode())) {
                        ebsService.send(resources.getOrderId(),resources.getRefereeCode());
                    }
                    taipingStatus = TaipingOrderStatusEnum.STATUS_13.getValue();

                    remindmessage = "【益药】尊敬的客户，您的订单号：%s，订单状态：%s。%s。 请在益药商城小程序中及时查看订单详情。如有疑问，请致电400-9200-036";
                    remindmessage = String.format(remindmessage,resources.getOrderId(), "已发货", "物流公司："+prescripStatusParam.getDeliveryName() + " 运单号：" + prescripStatusParam.getDeliveryId() );
                }
            } else if ("45".equals(prescripStatusParam.getPrescripStatus()) || "50".equals(prescripStatusParam.getPrescripStatus()) ) {  // 关闭
                resources.setStatus(OrderStatusEnum.STATUS_3.getValue());
                resources.setDeliveryName(prescripStatusParam.getDeliveryName());
                resources.setDeliveryId(prescripStatusParam.getDeliveryId());
                resources.setYiyaobaoOrderId(prescripStatusParam.getOrderId());
                this.update(resources);
                status = "已完成";

                InternetHospitalDemand internetHospitalDemand = internetHospitalDemandService.getOne( new QueryWrapper<InternetHospitalDemand>().eq("order_id",resources.getOrderId()).select("prescription_code"),false);
                if(internetHospitalDemand != null) {
                    XkSign xkSign = new XkSign();
                    xkSign.setExpressCode(prescripStatusParam.getDeliveryId());
                    xkSign.setExpressCompanyCode("shunfeng");
                    xkSign.setExpressCompanyName("顺丰");
                    xkSign.setPrescriptionCode(internetHospitalDemand.getPrescriptionCode());
                    xkSign.setSignPerson(resources.getRealName());
                    xkSign.setSignDatetime(DateUtil.now());
                    xkProcessService.signNotice(xkSign);
                }
                taipingStatus = TaipingOrderStatusEnum.STATUS_14.getValue();

                remindmessage = "【益药】尊敬的客户，您的订单号：%s，订单状态：%s。 请在益药商城小程序中及时查看订单详情。如有疑问，请致电400-9200-036";
                remindmessage = String.format(remindmessage,resources.getOrderId(), "已完成" );

                //订单信息下发药联
                if(ProjectNameEnum.YAOLIAN.getValue().equals(resources.getProjectCode())) {
                    /*try {
                        yaolianserviceImpl.pushOrderInfo(resources.getOrderId());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }*/

                    cn.hutool.json.JSONObject jsonObject = JSONUtil.createObj();
                    jsonObject.put("orderNo",resources.getOrderId());
                    jsonObject.put("status",OrderStatusEnum.STATUS_3.getValue().toString());
                    jsonObject.put("desc","药联已完成订单");
                    jsonObject.put("time", DateUtil.now());
                    mqProducer.sendDelayQueue(bizRoutekeyYaolian,jsonObject.toString(),2000);

                } else if(ProjectNameEnum.MEIDEYI.getValue().equals(resources.getProjectCode())) {
                    cn.hutool.json.JSONObject jsonObject = JSONUtil.createObj();
                    jsonObject.put("orderNo",resources.getOrderId());
                    jsonObject.put("status","12");
                    jsonObject.put("desc","美德医已完成订单");
                    jsonObject.put("time", DateUtil.now());
                    mqProducer.sendDelayQueue(bizRoutekeyMeideyi, jsonObject.toString(),2000);
                } else if(ProjectNameEnum.DIAO.getValue().equals(resources.getProjectCode())) {
                    // 调用ebs，将推荐人传过去
                    ebsService.send(resources.getOrderId(),resources.getRefereeCode());
                }
            } else if ("98".equals(prescripStatusParam.getPrescripStatus())) { // 退货
                status = "已退货";
                resources.setStatus(OrderStatusEnum.STATUS_7.getValue());
                resources.setCheckFailRemark("益药宝订单退货");
                resources.setNeedRefund(1);
                resources.setYiyaobaoOrderId(prescripStatusParam.getOrderId());
                this.update(resources);

                taipingStatus = TaipingOrderStatusEnum.STATUS_15.getValue();

                remindmessage = "【益药】尊敬的客户，您的订单号：%s，订单状态：%s。 请在益药商城小程序中及时查看订单详情。如有疑问，请致电400-9200-036";
                remindmessage = String.format(remindmessage,resources.getOrderId(), "已退货" );
            } else if("30".equals(prescripStatusParam.getPrescripStatus())) {  // 已备货
                resources.setStatus(OrderStatusEnum.STATUS_9.getValue());
                this.update(resources);
                status = "已备货";

                // 调用ebs，将推荐人传过去
                if(ProjectNameEnum.DIAO.getValue().equals(resources.getProjectCode())) {
                    ebsService.send(resources.getOrderId(),resources.getRefereeCode());
                }

            }else if("1".equals(prescripStatusParam.getPrescripStatus())){
                resources.setPaid(OrderInfoEnum.PAY_STATUS_1.getValue());
                resources.setPayTime(OrderUtil.getSecondTimestampTwo());
                resources.setYiyaobaoOrderId(prescripStatusParam.getOrderId());
                resources.setYiyaobaoOrderNo(prescripStatusParam.getOrderNo());
                this.update(resources);
                status = "已支付";
                remark = "您的需求单已经成功";

                taipingStatus = TaipingOrderStatusEnum.STATUS_10.getValue();

                remindmessage = "【益药】尊敬的客户，您的订单号：%s，订单状态：%s。%s。 请在益药商城小程序中及时查看订单详情。如有疑问，请致电400-9200-036";
                remindmessage = String.format(remindmessage,resources.getOrderId(), "支付成功","益药药房将尽快为您发货");

            }else if("80".equals(prescripStatusParam.getPrescripStatus()) || "94".equals(prescripStatusParam.getPrescripStatus()) || "98".equals(prescripStatusParam.getPrescripStatus()) ) {
                //  DateTime date = DateUtil.parse(prescripStatusParam.getDealDate(), DatePattern.NORM_DATE_FORMAT);
                resources.setStatus(OrderStatusEnum.STATUS_7.getValue());

                resources.setYiyaobaoOrderId(prescripStatusParam.getOrderId());
                resources.setYiyaobaoOrderNo(prescripStatusParam.getOrderNo());
                if( (PayTypeEnum.WEIXIN.getValue().equals(resources.getPayType()) || PayTypeEnum.ALIPAY.getValue().equals(resources.getPayType()) || PayTypeEnum.ZhongAnPay.getValue().equals(resources.getPayType()) )&& new Integer(1).equals(resources.getPaid())) {
                    resources.setRefundStatus(1);
                    resources.setRefundReasonTime(OrderUtil.getSecondTimestampTwo());
                    resources.setRefundReasonWap( resources.getCheckFailReason() +" " + resources.getCheckFailRemark());
                }

                // 更新需退款字段
                resources.setNeedRefund(1);
                this.update(resources);

                // 发送短信，通知管理员处理退款
                //todo 推送
                if( resources.getStoreId()!= null) {
                    YxSystemStore yxSystemStore = yxSystemStoreService.getById(resources.getStoreId());
                    if(yxSystemStore != null && StrUtil.isNotBlank(yxSystemStore.getLinkPhone())) {
                        List<String> phoneList= Arrays.asList(yxSystemStore.getLinkPhone().split(",")) ;
                        //发送短信
                        for(String phone :phoneList) {
                            String remindmessage_manager = "【益药】您有订单待处理，订单状态：%s。订单编号：%s";
                            remindmessage_manager = String.format(remindmessage_manager, "申请退款", resources.getOrderId());
                            smsService.sendTeddy("",remindmessage_manager,phone);
                        }
                    }
                }

            }

            if(resources != null) {
                //添加订单状态变更记录
                addStoreOrderStatusTime(resources);
                //模板消息通知
                try {
                    YxWechatUserDto wechatUser = generator.convert(wechatUserService.getOne(new QueryWrapper<YxWechatUser>().eq("uid",resources.getUid())),YxWechatUserDto.class);
                    if (ObjectUtil.isNotNull(wechatUser)) {
                        //公众号与小程序打通统一公众号模板通知
                        if (StrUtil.isNotBlank(wechatUser.getRoutineOpenid())) {
                            String page = "pages/wode/orderDetail?orderId="+resources.getOrderId();
                            OrderTemplateMessage message = new OrderTemplateMessage();
                            message.setOrderDate( OrderUtil.stampToDate(resources.getAddTime().toString()));
                            message.setOrderId(resources.getOrderId());
                            message.setOrderStatus( status);
                            message.setRemark(remark);
                            templateService.sendDYTemplateMessage(wechatUser.getRoutineOpenid(),page,message);
                        }
                    }

                    // 发送手机短信
                    YxUser yxuser = yxUserService.getById(resources.getUid());
                    if(yxuser != null && StrUtil.isNotBlank(yxuser.getPhone()) && StrUtil.isNotBlank(remindmessage)) {
                        smsService.sendTeddy("",remindmessage,yxuser.getPhone());
                    }
                } catch (Exception e) {
                    log.info("订单状态通知异常，订单号:{}",resources.getOrderId());
                    e.printStackTrace();
                }

                // 太平项目订单，需要订单状态回传到太平公司
                if( ProjectNameEnum.TAIPING_LEXIANG.getValue().equals(resources.getProjectCode()) && StrUtil.isNotBlank(resources.getTaipingOrderNumber()) && taipingStatus != 0) {
                    taipingCardService.sendOrderStatus(resources.getOrderId(),taipingStatus);
                }
                if(ProjectNameEnum.ZHONGANPUYAO.getValue().equals(resources.getProjectCode())) {
                    //   zhongAnPuYaoService.sendOrderInfo(resources.getOrderId());

                    cn.hutool.json.JSONObject jsonObject = JSONUtil.createObj();
                    jsonObject.put("orderNo",resources.getOrderId());
                    jsonObject.put("status",resources.getStatus().toString());
                    jsonObject.put("desc","众安普药订单");
                    jsonObject.put("time", DateUtil.now());
                    mqProducer.sendDelayQueue(bizRoutekeyZhonganpuyao, jsonObject.toString(),2000);

                }
                if(ProjectNameEnum.ZHONGANMANBING.getValue().equals(resources.getProjectCode())) {
                    //   zhongAnPuYaoService.sendOrderInfo(resources.getOrderId());
                    cn.hutool.json.JSONObject jsonObject = JSONUtil.createObj();
                    jsonObject.put("orderNo",resources.getOrderId());
                    jsonObject.put("status",resources.getStatus().toString());
                    jsonObject.put("desc","众安慢病订单");
                    jsonObject.put("time", DateUtil.now());
                    mqProducer.sendDelayQueue(bizRoutekeyZhonganpuyao, jsonObject.toString(),2000);
                }
                if(ProjectNameEnum.LINGYUANZHI.getValue().equals(resources.getProjectCode())) {
                    //   zhongAnPuYaoService.sendOrderInfo(resources.getOrderId());
                    cn.hutool.json.JSONObject jsonObject = JSONUtil.createObj();
                    jsonObject.put("orderNo",resources.getOrderId());
                    jsonObject.put("status",resources.getStatus().toString());
                    jsonObject.put("desc","众安0元治订单");
                    jsonObject.put("time", DateUtil.now());
                    mqProducer.sendDelayQueue(bizRoutekeyZhonganpuyao, jsonObject.toString(),2000);
                }
            }

        }

    }

    /**
     *  添加订单状态变更记录
     * @param resources
     */
    public  void addStoreOrderStatusTime(YxStoreOrder resources){
        if(resources.getRefundStatus() != null && resources.getRefundStatus()==2) {
            YxStoreOrderStatus storeOrderStatus = new YxStoreOrderStatus();
            storeOrderStatus.setOid(resources.getId());
            storeOrderStatus.setChangeType(OrderChangeTypeEnum.REFUND_PRICE.getValue());
            storeOrderStatus.setChangeMessage(OrderChangeTypeEnum.REFUND_PRICE.getDesc());
            storeOrderStatus.setChangeTime(OrderUtil.getSecondTimestampTwo());
            yxStoreOrderStatusService.save(storeOrderStatus);
        }else if(resources.getStatus()!= null && resources.getStatus() == 3) {  // 已完成
            YxStoreOrderStatus storeOrderStatus = new YxStoreOrderStatus();
            storeOrderStatus.setOid(resources.getId());
            storeOrderStatus.setChangeType(OrderChangeTypeEnum.ClOSE_ORDER.getValue());
            storeOrderStatus.setChangeMessage(OrderChangeTypeEnum.ClOSE_ORDER.getDesc());
            storeOrderStatus.setChangeTime(OrderUtil.getSecondTimestampTwo());
            yxStoreOrderStatusService.save(storeOrderStatus);
        }else if(resources.getStatus()!= null &&resources.getStatus() == 11) {  // 付款待确认
            YxStoreOrderStatus storeOrderStatus = new YxStoreOrderStatus();
            storeOrderStatus.setOid(resources.getId());
            storeOrderStatus.setChangeType(OrderChangeTypeEnum.TO_BE_CONFIRMED_PAY.getValue());
            storeOrderStatus.setChangeMessage(OrderChangeTypeEnum.TO_BE_CONFIRMED_PAY.getDesc());
            storeOrderStatus.setChangeTime(OrderUtil.getSecondTimestampTwo());
            yxStoreOrderStatusService.save(storeOrderStatus);
        }else if(resources.getStatus()!= null && resources.getPaid() != null && resources.getStatus() == 0 && resources.getPaid() == 1) {  //已付款
            YxStoreOrderStatus storeOrderStatus = new YxStoreOrderStatus();
            storeOrderStatus.setOid(resources.getId());
            storeOrderStatus.setChangeType(OrderChangeTypeEnum.PAID.getValue());
            storeOrderStatus.setChangeMessage(OrderChangeTypeEnum.PAID.getDesc());
            storeOrderStatus.setChangeTime(OrderUtil.getSecondTimestampTwo());
            yxStoreOrderStatusService.save(storeOrderStatus);
        }else if(resources.getStatus()!= null && resources.getPaid() != null && resources.getStatus() == 1 && resources.getPaid() == 1) {  //已发货
            YxStoreOrderStatus storeOrderStatus = new YxStoreOrderStatus();
            storeOrderStatus.setOid(resources.getId());
            storeOrderStatus.setChangeType(OrderChangeTypeEnum.DELIVERY_GOODS.getValue());
            storeOrderStatus.setChangeMessage(OrderChangeTypeEnum.DELIVERY_GOODS.getDesc());
            storeOrderStatus.setChangeTime(OrderUtil.getSecondTimestampTwo());
            yxStoreOrderStatusService.save(storeOrderStatus);
        }
    }


    /**
     *  添加MSH订单状态变更记录
     * @param mshOrder
     */
    public  void addMSHStoreOrderStatusTime(MshOrder mshOrder){
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

    @Override
    public void updateOrderUserInfo(OrderUserInfo orderUserInfo) {

        // 更新用户信息
        if(orderUserInfo.getUid() != null) {
            UpdateWrapper updateWrapper = new UpdateWrapper();
            updateWrapper.eq("uid",orderUserInfo.getUid());
            if(StrUtil.isNotBlank(orderUserInfo.getPhone())) {
                updateWrapper.set("phone",orderUserInfo.getPhone());
            }

            if(StrUtil.isNotBlank(orderUserInfo.getRealName())) {
                updateWrapper.set("real_name",orderUserInfo.getRealName());
            }

            if(StrUtil.isNotBlank(orderUserInfo.getCardId())) {
                updateWrapper.set("card_id",orderUserInfo.getCardId());
            }

            yxUserService.update(updateWrapper);
        }


        LambdaUpdateWrapper<YxStoreOrder> lambdaUpdateWrapper = new LambdaUpdateWrapper();
      // lambdaUpdateWrapper.set(YxStoreOrder::getOrderId,orderUserInfo.getOrderId());
        lambdaUpdateWrapper.eq(YxStoreOrder::getOrderId,orderUserInfo.getOrderId());
        lambdaUpdateWrapper.set(YxStoreOrder::getExpectedReceivingDate,orderUserInfo.getExpectedReceivingDate());
        // 更新收货信息
       // if(orderUserInfo.getAddressId() != null) {
            /*UpdateWrapper updateWrapper1 = new UpdateWrapper();
            updateWrapper1.eq("id",orderUserInfo.getAddressId());

            updateWrapper1.set("province",orderUserInfo.getProvince());
            updateWrapper1.set("city",orderUserInfo.getCity());
            updateWrapper1.set("district",orderUserInfo.getDistrict());
            updateWrapper1.set("phone",orderUserInfo.getAddressPhone());
            updateWrapper1.set("real_name",orderUserInfo.getAddressRealName());
            updateWrapper1.set("detail",orderUserInfo.getDetail());
            yxUserAddressService.update(updateWrapper1);*/

            String addressDetail = orderUserInfo.getProvinceName() + " " + orderUserInfo.getCityName() + " " + orderUserInfo.getDistrictName() + " " + orderUserInfo.getAddress();

            lambdaUpdateWrapper.set(YxStoreOrder::getUserAddress,addressDetail);
            lambdaUpdateWrapper.set(YxStoreOrder::getRealName,orderUserInfo.getAddressRealName());
            lambdaUpdateWrapper.set(YxStoreOrder::getUserPhone,orderUserInfo.getAddressPhone());
            lambdaUpdateWrapper.set(YxStoreOrder::getProvinceName,orderUserInfo.getProvinceName());
            lambdaUpdateWrapper.set(YxStoreOrder::getCityName,orderUserInfo.getCityName());
            lambdaUpdateWrapper.set(YxStoreOrder::getDistrictName,orderUserInfo.getDistrictName());
            lambdaUpdateWrapper.set(YxStoreOrder::getAddress,orderUserInfo.getAddress());
            lambdaUpdateWrapper.set(YxStoreOrder::getAddressType,orderUserInfo.getAddressType());
       // }


        // 更新药房信息
        if(orderUserInfo.getStoreId() != null && orderUserInfo.getStoreId() != 0) {
            lambdaUpdateWrapper.set(YxStoreOrder::getStoreId,orderUserInfo.getStoreId());
        }

        if(orderUserInfo.getServiceDrugstoreId() != null ) {
            lambdaUpdateWrapper.set(YxStoreOrder::getServiceDrugstoreId,orderUserInfo.getServiceDrugstoreId());
            RocheStore rocheStore= rocheStoreService.getById(orderUserInfo.getServiceDrugstoreId());
            lambdaUpdateWrapper.set(YxStoreOrder::getServiceDrugstore,rocheStore==null?"":rocheStore.getName());
        }

        if(StrUtil.isNotBlank(orderUserInfo.getImagePath())) {
            lambdaUpdateWrapper.set(YxStoreOrder::getImagePath,orderUserInfo.getImagePath());
        }

        lambdaUpdateWrapper.set(YxStoreOrder::getPayerAccountName,orderUserInfo.getPayerAccountName());

        if(ObjectUtil.isNotNull(orderUserInfo.getDrugUserId())) {
            lambdaUpdateWrapper.set(YxStoreOrder::getDrugUserName,orderUserInfo.getDrugUserName());
            lambdaUpdateWrapper.set(YxStoreOrder::getDrugUserPhone,orderUserInfo.getDrugUserPhone());
            lambdaUpdateWrapper.set(YxStoreOrder::getDrugUserType,orderUserInfo.getDrugUserType());
            lambdaUpdateWrapper.set(YxStoreOrder::getDrugUserBirth,orderUserInfo.getDrugUserBirth());
            lambdaUpdateWrapper.set(YxStoreOrder::getDrugUserSex,orderUserInfo.getDrugUserSex());
            lambdaUpdateWrapper.set(YxStoreOrder::getDrugUserIdcard,orderUserInfo.getDrugUserIdcard());
            lambdaUpdateWrapper.set(YxStoreOrder::getDrugUserWeight,orderUserInfo.getDrugUserWeight());

            if( orderUserInfo.getDrugUserType()!= null && orderUserInfo.getDrugUserType() == 1 && StrUtil.isNotBlank(orderUserInfo.getDrugUserIdcard())) {  // 成人
                String idCard = orderUserInfo.getDrugUserIdcard();
                String sex = IDCardUtil.getSex(idCard);
                lambdaUpdateWrapper.set(YxStoreOrder::getDrugUserSex,sex);
                String birth = IDCardUtil.getBirthday(idCard);
                birth = birth.substring(0,8);
                lambdaUpdateWrapper.set(YxStoreOrder::getDrugUserBirth,birth);
            }
        }
        this.update(lambdaUpdateWrapper);
    }

    @Override
    public void sendOrder2yiyaobao() {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("upload_yiyaobao_flag",0); // 未上传
       // queryWrapper.eq("paid",1);// 已支付
        queryWrapper.eq("refund_status",0); //未退款
        queryWrapper.ne("project_code",ProjectNameEnum.ROCHE_SMA.getValue());
        queryWrapper.in("status",OrderStatusEnum.STATUS_5.getValue());
        queryWrapper.select("order_id","project_code","paid","need_internet_hospital_prescription");
        List<YxStoreOrder> yxStoreOrderList = this.list(queryWrapper);
        for(YxStoreOrder yxStoreOrder:yxStoreOrderList) {
            if(ProjectNameEnum.TAIPING_LEXIANG.getValue().equals(yxStoreOrder.getProjectCode())) {
                if(yxStoreOrder.getPaid() == 1 && (yxStoreOrder.getNeedInternetHospitalPrescription() == 0 ||  yxStoreOrder.getNeedInternetHospitalPrescription() == 2)) {  // 已支付

                    yiyaobaoOrderService.sendOrder2YiyaobaoCloud(yxStoreOrder.getOrderId(),ProjectNameEnum.ROCHE_SMA.getValue());
                }

            } else {
                yiyaobaoOrderService.sendOrder2YiyaobaoStore(yxStoreOrder.getOrderId());
            }
        }


        // 推送退款订单
        QueryWrapper queryWrapper_refund = new QueryWrapper();
        queryWrapper_refund.eq("upload_yiyaobao_refund_flag",0);
        queryWrapper_refund.eq("upload_yiyaobao_flag",1);
        queryWrapper_refund.eq("paid",1);
        queryWrapper_refund.in("refund_status",1,2);
        queryWrapper_refund.ne("check_status","不通过");
        queryWrapper_refund.eq("project_code",ProjectNameEnum.TAIPING_LEXIANG.getValue());
        List<YxStoreOrder> yxStoreOrderList_refund = this.list(queryWrapper_refund);
        for(YxStoreOrder yxStoreOrder:yxStoreOrderList_refund) {
            yiyaobaoOrderService.sendOrder2YiyaobaoCloudCancel(yxStoreOrder.getOrderId());
        }

    }

    @Override
    public void cancelConfirm(String json) {
        JSONObject jsonObject = JSONUtil.parseObj(json);
        String orderId = jsonObject.getStr("orderId");
        String checkResult = jsonObject.getStr("checkResult");

        UpdateWrapper updateWrapper = new UpdateWrapper();
        if( "通过".equals(checkResult)) {
            updateWrapper.set("status",OrderStatusEnum.STATUS_8.getValue());
        } else {
            LambdaQueryWrapper<YxStoreOrder> lambdaQueryWrapper = new LambdaQueryWrapper();
            lambdaQueryWrapper.eq(YxStoreOrder::getOrderId,orderId);
            lambdaQueryWrapper.select(YxStoreOrder::getCheckStatus);
            YxStoreOrder yxStoreOrder = this.getOne(lambdaQueryWrapper);
            if(StrUtil.isNotBlank(yxStoreOrder.getCheckStatus())) {
                updateWrapper.set("status",OrderStatusEnum.STATUS_0.getValue());
            } else {
                updateWrapper.set("status",OrderStatusEnum.STATUS_5.getValue());
            }
        }

        updateWrapper.eq("order_id",orderId);

        this.update(updateWrapper);
    }

    @Override
    @Transactional
    public YxStoreOrder addTbOrderProject(TbOrderProjectParam order4ProjectParam) {

        // 校验销售区域
        String projectCode = order4ProjectParam.getProjectCode();
        if(StrUtil.isBlank(projectCode)) {
            throw new BadRequestException("项目代码不能为空");
        }

        int uid =0;

        YxUser yxUser = yxUserService.getOne(new QueryWrapper<YxUser>().eq("phone",order4ProjectParam.getPhone()),false);
            if(yxUser == null) {
            uid = 10000000 + Long.valueOf(redisUtils.incr("patient",1)).intValue();

            //用户保存
            YxUser user = new YxUser();
            user.setAccount(String.valueOf(uid));

            user.setUsername(String.valueOf(uid));

            user.setPassword(passwordEncoder.encode(ShopConstants.YSHOP_DEFAULT_PWD));
            user.setPwd(passwordEncoder.encode(ShopConstants.YSHOP_DEFAULT_PWD));
            user.setPhone(order4ProjectParam.getPhone());
            user.setUserType(AppFromEnum.WECHAT.getValue());
            user.setLoginType(AppFromEnum.WECHAT.getValue());
            user.setAddTime(OrderUtil.getSecondTimestampTwo());
            user.setLastTime(OrderUtil.getSecondTimestampTwo());
            user.setNickname(String.valueOf(uid));
            user.setAvatar("");
            user.setNowMoney(BigDecimal.ZERO);
            user.setBrokeragePrice(BigDecimal.ZERO);
            user.setIntegral(BigDecimal.ZERO);

            yxUserService.save(user);

            uid = user.getUid();
        } else {
            uid = yxUser.getUid();
        }


        // 1.添加购物车
        int isNew = 1;
        int combinationId = 0;
        int seckillId = 0;
        int bargainId = 0;
        String departmentCode = "";
        String partnerCode = "";
        String refereeCode = order4ProjectParam.getRefereeCode();
        List<Integer> cartIdList = new ArrayList<>();
        List<YxStoreCartQueryVo> cartInfo = new ArrayList<>();
        for( TbOrderDetailProjectParam detail : order4ProjectParam.getDetails()) {
            YxStoreCart storeCart = storeCartService.addTbCart(uid,detail.getProductId(),detail.getNum(),detail.getProductUniqueId()
                    ,"product",isNew,combinationId,seckillId,bargainId,departmentCode,partnerCode,refereeCode,projectCode);
            cartIdList.add(storeCart.getId().intValue());

            YxStoreCartQueryVo storeCartQueryVo = toYxStoreCartQueryVo(storeCart);

            YxStoreProductAttrValue productAttrValue = yxStoreProductAttrValueService.getOne(new LambdaQueryWrapper<YxStoreProductAttrValue>().eq(YxStoreProductAttrValue::getUnique,detail.getProductUniqueId()));
            BigDecimal price = new BigDecimal(0);
            YxStoreProductQueryVo storeProduct = productService.selectById(detail.getProductId());
            if(StrUtil.isNotBlank(projectCode)) {
                LambdaQueryWrapper<Product4project> lambdaQueryWrapper = new LambdaQueryWrapper<>();
                lambdaQueryWrapper.eq(Product4project::getProductUniqueId,detail.getProductUniqueId());
                lambdaQueryWrapper.eq(Product4project::getIsDel,0);
                lambdaQueryWrapper.eq(Product4project::getProjectNo,projectCode);
                Product4project product4project = product4projectService.getOne(lambdaQueryWrapper,false);
                price = product4project.getUnitPrice();
            } else {
                price = productAttrValue.getPrice();
            }

            storeProduct.setPrice(price);
            storeProduct.setStoreNameReal(productAttrValue.getSuk());
            storeProduct.setAttrInfo(productAttrValue);
            storeCartQueryVo.setProductInfo(storeProduct);
            // 设置商品价格（会员价）
            storeCartQueryVo.setVipTruePrice(price.doubleValue());
            //设置商品价格（原价）
            storeCartQueryVo.setTruePrice(price.doubleValue());
            storeCartQueryVo.setCostPrice(price.doubleValue());
            storeCartQueryVo.setTrueStock(productAttrValue.getStock());
            storeCartQueryVo.setYiyaobaoSku(storeProduct.getYiyaobaoSku());
            storeCartQueryVo.setProductAttrUnique(productAttrValue.getUnique());

            cartInfo.add(storeCartQueryVo);

        }

        //获取省市区的name
        QueryWrapper<MdCountry> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("CODE", order4ProjectParam.getProvinceCode());
        String province = mdCountryMapper.selectList(queryWrapper).get(0).getName();
        queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("CODE", order4ProjectParam.getCityCode());
        String city = mdCountryMapper.selectList(queryWrapper).get(0).getName();
        queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("CODE", order4ProjectParam.getDistrictCode());
        String district = mdCountryMapper.selectList(queryWrapper).get(0).getName();

        YxUserAddress userAddress= yxUserAddressService.getById(order4ProjectParam.getAddressId());
        if(userAddress==null){
            userAddress = new YxUserAddress();
            userAddress.setRealName(order4ProjectParam.getPhone());
            userAddress.setPhone(order4ProjectParam.getPhone());
            userAddress.setProvince(province);
            userAddress.setCity(city);
            userAddress.setDistrict(district);
            userAddress.setDetail(order4ProjectParam.getAddress());
            userAddress.setProvinceCode(order4ProjectParam.getProvinceCode());
            userAddress.setCityCode(order4ProjectParam.getCityCode());
            userAddress.setDistrictCode(order4ProjectParam.getDistrictCode());
            userAddress.setUid(uid);
            yxUserAddressService.save(userAddress);
        }else{
            userAddress.setRealName(order4ProjectParam.getPhone());
            userAddress.setPhone(order4ProjectParam.getPhone());
            userAddress.setProvince(province);
            userAddress.setCity(city);
            userAddress.setDistrict(district);
            userAddress.setDetail(order4ProjectParam.getAddress());
            userAddress.setUid(uid);
            userAddress.setProvinceCode(order4ProjectParam.getProvinceCode());
            userAddress.setCityCode(order4ProjectParam.getCityCode());
            userAddress.setDistrictCode(order4ProjectParam.getDistrictCode());
            yxUserAddressService.updateById(userAddress);
        }

        YxDrugUsers yxDrugUsers = yxDrugUsersService.getById(order4ProjectParam.getDrugUserId());
        if(yxDrugUsers==null){
            yxDrugUsers=new YxDrugUsers();
            yxDrugUsers.setPhone(order4ProjectParam.getDrugUserPhone());
            yxDrugUsers.setName(order4ProjectParam.getDrugUserName());
            yxDrugUsersService.save(yxDrugUsers);
        }else{
            yxDrugUsers.setPhone(order4ProjectParam.getDrugUserPhone());
            yxDrugUsers.setName(order4ProjectParam.getDrugUserName());
            yxDrugUsersService.updateById(yxDrugUsers);
        }

        // 2.confirm订单

        PriceGroupDTO priceGroup = this.getOrderPriceGroup(cartInfo);

        Double totalPrice= Double.valueOf(0);

        Double a= Math.max(0,NumberUtil.sub(priceGroup.getTotalPrice(),new Double(order4ProjectParam.getDeductibleTotal().doubleValue())));
        if(a>new Double(order4ProjectParam.getResponsibilityTotal().doubleValue())){
            totalPrice=Math.max(0,(new BigDecimal(priceGroup.getTotalPrice()).subtract(order4ProjectParam.getResponsibilityTotal())).doubleValue());
        }else{
            totalPrice=order4ProjectParam.getDeductibleTotal().doubleValue();
        }

        priceGroup.setTotalPrice(totalPrice.doubleValue());
        OtherDTO other = new OtherDTO();
        other.setIntegralRatio(systemConfigService.getData("integral_ratio"));
        other.setIntegralFull(systemConfigService.getData("integral_full"));
        other.setIntegralMax(systemConfigService.getData("integral_max"));

        other.setStoreId(order4ProjectParam.getStoreId());
        other.setStoreName(order4ProjectParam.getStoreName());
        other.setProjectCode(projectCode);
        other.setPartnerCode(partnerCode);
        other.setRefereeCode(refereeCode);
        other.setDepartCode("");
        other.setNeedImageFlag(true);
        String orderKey = this.cacheOrderInfo(uid,cartInfo,
                priceGroup,other);

        // 3.创建订单

        OrderParam param = new OrderParam();
        param.setProjectCode(projectCode);
        param.setStoreId(order4ProjectParam.getStoreId());
        param.setMark(order4ProjectParam.getMark());
        param.setAddressId(String.valueOf(userAddress.getId()));
        param.setDrugUserId(String.valueOf(yxDrugUsers.getId()));
        param.setImagePath(order4ProjectParam.getImagePath());
        param.setShippingType(OrderInfoEnum.SHIPPIING_TYPE_1.getValue());
        param.setUseIntegral(0d);
        param.setPinkId(0);
        param.setBargainId(0);
        param.setSeckillId(0);
        param.setCouponId(0);
        param.setFrom(AppFromEnum.PC.getValue());
        param.setPayType(order4ProjectParam.getPayType());
        param.setNeedInvoiceFlag(order4ProjectParam.getNeedInvoiceFlag());
        param.setInvoiceName(order4ProjectParam.getInvoiceName());
        param.setInvoiceMail(order4ProjectParam.getInvoiceMail());
        YxStoreOrder order =  this.createTbOrderProject(order4ProjectParam.getId()==null?null:order4ProjectParam.getId().longValue(),uid,orderKey,param);
        return order;
    }

    @Override
    public void sendTemplateMessage(YxStoreOrder resources) {
        if(StringUtils.isEmpty(resources.getTemplateMessage())){
            throw new ErrorRequestException("请维护该订单模板消息提示说明字段。");
        }
        String templateMessage=resources.getTemplateMessage();
        yxStoreOrderMapper.updateById(resources);
        resources=yxStoreOrderMapper.selectById(resources);
        YxWechatUserDto wechatUser = generator.convert(wechatUserService.getOne(new QueryWrapper<YxWechatUser>().eq("uid",resources.getUid())),YxWechatUserDto.class);
        if (ObjectUtil.isNotNull(wechatUser)) {
            //公众号与小程序打通统一公众号模板通知
            if (StrUtil.isNotBlank(wechatUser.getRoutineOpenid())) {
                String orderStatusStr = OrderUtil.orderStatusStr(resources.getPaid()
                        ,resources.getStatus(),resources.getShippingType()
                        ,resources.getRefundStatus());


                String page = "pages/wode/orderDetail?orderId="+resources.getOrderId();
                OrderTemplateMessage message = new OrderTemplateMessage();
                message.setOrderDate( OrderUtil.stampToDate(resources.getAddTime().toString()));
                message.setOrderId(resources.getOrderId());
                message.setOrderStatus(orderStatusStr);
                message.setRemark(templateMessage);
                templateService.sendDYTemplateMessage(wechatUser.getRoutineOpenid(),page,message);
            }
        }else{
            throw new ErrorRequestException("该用户未订阅授权，发送模板消息失败。");
        }
    }

    @Override
    public void updateStatusSendTemplateMessage(YxStoreOrder resources) {
        resources=yxStoreOrderMapper.selectById(resources);
        YxWechatUserDto wechatUser = generator.convert(wechatUserService.getOne(new QueryWrapper<YxWechatUser>().eq("uid",resources.getUid())),YxWechatUserDto.class);
        if (ObjectUtil.isNotNull(wechatUser)) {
            //公众号与小程序打通统一公众号模板通知
            if (StrUtil.isNotBlank(wechatUser.getRoutineOpenid())) {
                String orderStatusStr = OrderUtil.orderStatusStr(resources.getPaid()
                        ,resources.getStatus(),resources.getShippingType()
                        ,resources.getRefundStatus());
                if(orderStatusStr.equals("药店取消订单") || orderStatusStr.equals("用户取消订单")){
                    orderStatusStr="订单取消";
                }

                String page = "pages/wode/orderDetail?orderId="+resources.getOrderId();
                OrderTemplateMessage message = new OrderTemplateMessage();
                message.setOrderDate( OrderUtil.stampToDate(resources.getAddTime().toString()));
                message.setOrderId(resources.getOrderId());
                message.setOrderStatus(orderStatusStr);
                message.setRemark(orderMessageRemark(resources.getPaid()
                        ,resources.getStatus(),resources.getShippingType()
                        ,resources.getRefundStatus()));
                templateService.sendDYTemplateMessage(wechatUser.getRoutineOpenid(),page,message);
            }
        }
    }

    @Override
    public void downloadByProjectCode(String startTime, String endTime, String projectCode, HttpServletResponse response) {
        List<Map<String,Object>> maps=yxStoreOrderMapper.findByPayTimeAndProject(startTime,endTime,projectCode);
        List<Map<String, Object>> list = new ArrayList<>();
        for (Map<String, Object> objectMap : maps) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("订单支付日期",objectMap.get("订单支付日期"));
            map.put("数量",objectMap.get("数量"));
            map.put("姓名",objectMap.get("姓名"));
            map.put("代码",objectMap.get("代码"));
            map.put("医院名称",objectMap.get("医院名称"));
            list.add(map);
        }
        if(list.size()==0){
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("订单支付日期","");
            map.put("数量","");
            map.put("姓名","");
            map.put("代码","");
            map.put("医院名称","");
            list.add(map);
        }
        try {
            FileUtil.downloadExcel(list, response);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void yiyaobaoCancelOrder(YxStoreOrder resources) {
        YxStoreOrder storeOrder =  getOne(new QueryWrapper<YxStoreOrder>().eq("order_id",resources.getOrderId()));
        if(storeOrder.getUploadYiyaobaoFlag()==1){
            YxSystemStore yxSystemStore = systemStoreService.getOne(new LambdaQueryWrapper<YxSystemStore>().eq(YxSystemStore::getId,resources.getStoreId()));
             if(yxSystemStore != null  && !"1".equals(resources.getReturnType())){
                 String type="0";
                 if(ShopConstants.STORENAME_GUANGZHOU_CLOUD.equals(yxSystemStore.getName())){
                     type="1";
                }
                String url = yiyaobao_apiUrl_external+cancelOrder;
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("voucher", resources.getOrderId());
                jsonObject.put("type", type);
                 String requestBody = jsonObject.toString(); //

                long timestamp = System.currentTimeMillis(); // 生成签名时间戳
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("ACCESS_APPID", appId); // 设置APP
                headers.put("ACCESS_TIMESTAMP", String.valueOf(timestamp)); // 设置签名时间戳
                String ACCESS_SIGANATURE = AppSiganatureUtils
                        .createSiganature(requestBody, appId, appSecret,
                                timestamp);
                headers.put("ACCESS_SIGANATURE", ACCESS_SIGANATURE); // 生成并设置签名
                log.info("ACCESS_APPID={}", appId);
                log.info("ACCESS_TIMESTAMP={}", String.valueOf(timestamp));
                log.info("ACCESS_SIGANATURE={}", ACCESS_SIGANATURE);
                log.info("url={}", url);
                log.info("requestBody={}", requestBody);
                String result = null; // 发起调用
                try {
                    result = HttpUtils.postJsonHttps(url, requestBody, headers);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                log.info("取消订单下发益药宝，结果：{}", result);
                if(StringUtils.isEmpty(result)){
                    throw new BadRequestException("取消订单下发失败。");
                }
                JSONObject object=JSONUtil.parseObj(result);
                if(object.get("code")==null){
                    throw new BadRequestException("取消订单下发失败。");
                }
                if(object.get("code").equals("1")){
                    throw new BadRequestException("取消订单下发失败,订单号为空");
                }
                if(object.get("code").equals("2")){
                    throw new BadRequestException("取消订单下发失败,未查询到订单");
                }
                if(object.get("code").equals("3")){
                    throw new BadRequestException("取消订单下发失败,订单不可取消");
                }
                if(object.get("code").equals("5")){
                    throw new BadRequestException("取消订单下发失败,EBS订单取消失败");
                }
                if(object.get("code").equals("90")){
                    throw new BadRequestException("取消订单下发失败,其他未知错误");
                }
            }
        }
    }

    @Override
    public YxStoreOrderDto getDetalById(Integer id) {
        YxStoreOrder yxStoreOrder =getById(id);
        List<YxStoreOrderDto> yxStoreOrderDto=new ArrayList<>();
        orderList(yxStoreOrderDto, yxStoreOrder);
        if(yxStoreOrderDto.size()!=0){
            return yxStoreOrderDto.get(0);
        }
        return new YxStoreOrderDto();
    }

    @Override
    public void orderFreight(List<OrderFreightParam> orderFreightParams) {
        for (OrderFreightParam orderFreightParam : orderFreightParams) {
            if( StrUtil.isBlank(orderFreightParam.getOrderId())) {
                throw new BadRequestException("订单号缺失！");
            }
            if(StrUtil.isBlank(orderFreightParam.getOrderSource())) {
                throw new BadRequestException("订单来源缺失！");
            }
            if("37".equals(orderFreightParam.getOrderSource())) {
                //oms-蚂蚁项目
                JSONObject jsonObject = JSONUtil.parseObj(orderFreightParam);
                log.info("转发oms数据：[{}]",jsonObject.toString());
                mqProducer.sendDelayQueue(orderFreightQueueName,jsonObject.toString(),2000);
                return;
            }
        }
    }

    /**
     * 获取推送消息备注
     *
     * @param paid
     * @param status
     * @param shipping_type
     * @param refund_status
     * @return
     */
    public static String orderMessageRemark(int paid, int status,
                                        int shipping_type, int refund_status) {
        String messageRemark = "";
        if (paid == 0 && status == 0) {
            messageRemark = "益药商城为您服务";//未支付
        } else if (paid == 1 && status == 0 && shipping_type == 1 && refund_status == 0) {
            messageRemark = "钱款已经确认收到，药品待收货。"; //待发货
        } else if (paid == 1 && status == 1 && shipping_type == 1 && refund_status == 0) {
            messageRemark = "药品已发货";// 待收货
        }else if ( status == 3 && refund_status == 0) {
            messageRemark = "益药商城为您服务";//已完成
        } else if (paid == 1 && refund_status == 1) {
            messageRemark = "益药商城为您服务";//退款中
        } else if (paid == 1 && refund_status == 2) {
            messageRemark = "益药商城为您服务";//已退款
        } else if( status == 5) {
            messageRemark = "益药商城为您服务";//未审核
        } else if( status == 6) {
            messageRemark = "益药商城为您服务";//审核未通过
        } else if( status == 7) {
            messageRemark = "益药商城为您服务";//药店取消订单
        }else if( status == 8) {
            messageRemark = "益药商城为您服务";//用户取消订单
        }else {
            messageRemark = "益药商城为您服务。";
        }

        return messageRemark;
    }

    public YxStoreOrder createTbOrderProject(Long id,int uid, String key, OrderParam param) {
        YxUserQueryVo userInfo = userService.getYxUserById(uid);
        if(ObjectUtil.isNull(userInfo)) throw new ErrorRequestException("用户不存在");

        CacheDTO cacheDTO = getCacheOrderInfo(uid,key);
        if(ObjectUtil.isNull(cacheDTO)){
            throw new ErrorRequestException("订单已过期,请刷新当前页面");
        }

        List<YxStoreCartQueryVo> cartInfo = cacheDTO.getCartInfo();

        OtherDTO other = cacheDTO.getOther();


        YxUserAddressQueryVo userAddress = null;
        if(OrderInfoEnum.SHIPPIING_TYPE_1.getValue().equals(param.getShippingType())){
            if(StrUtil.isEmpty(param.getAddressId())) throw new ErrorRequestException("请选择收货地址");
            userAddress = userAddressService.getYxUserAddressById(param.getAddressId());
            if(ObjectUtil.isNull(userAddress)) throw new ErrorRequestException("地址选择有误");
        }else{ //门店
            if(StrUtil.isBlank(param.getRealName()) || StrUtil.isBlank(param.getPhone())) {
                throw new ErrorRequestException("请填写姓名和电话");
            }
            userAddress = new YxUserAddressQueryVo();
            userAddress.setRealName(param.getRealName());
            userAddress.setPhone(param.getPhone());
            userAddress.setProvince("");
            userAddress.setCity("");
            userAddress.setDistrict("");
            userAddress.setDetail("");
        }

        Integer totalNum = 0;
        Integer gainIntegral = 0;
        List<String> cartIds = new ArrayList<>();
        int combinationId = 0;
        int seckillId = 0;
        int bargainId = 0;
        //优惠券
        int couponId = 0;
        if(ObjectUtil.isNotEmpty(param.getCouponId())){
            couponId = param.getCouponId().intValue();
        }

        ComputeDTO computeDTO  = this.computedOrder4Project(uid,other.getProjectCode(),new BigDecimal(cacheDTO.getPriceGroup().getTotalPrice()),param.getAddressId());

        Double totalPrice = computeDTO.getTotalPrice();
        Double payPrice = computeDTO.getPayPrice();
        Double payPostage = computeDTO.getPayPostage();
        Double couponPrice = computeDTO.getCouponPrice();
        Double deductionPrice = computeDTO.getDeductionPrice();

        JSONArray jsonArray = JSONUtil.createArray();
        for (YxStoreCartQueryVo cart : cartInfo) {
            int stock = productService.getProductStock(cart.getProductId(),cart.getProductAttrUnique());
            if(stock < cart.getCartNum()){
                throw new BadRequestException("["+ cart.getProductInfo().getStoreName() +"]"+"该产品库存不足"+cart.getCartNum());
            }

            combinationId = cart.getCombinationId();
            seckillId = cart.getSeckillId();
            bargainId = cart.getBargainId();
            cartIds.add(cart.getId().toString());
            totalNum += cart.getCartNum();
            //计算积分
            BigDecimal cartInfoGainIntegral = BigDecimal.ZERO;
            if(combinationId == 0 && seckillId == 0 && bargainId == 0){//拼团等活动不参与积分
                if(cart.getProductInfo().getGiveIntegral().intValue() > 0){
                    cartInfoGainIntegral = NumberUtil.mul(cart.getCartNum(),cart.
                            getProductInfo().getGiveIntegral());
                }
                gainIntegral = NumberUtil.add(gainIntegral,cartInfoGainIntegral).intValue();
            }

            cn.hutool.json.JSONObject jsonObject = JSONUtil.createObj();
            jsonObject.put("sku",cart.getYiyaobaoSku());
            jsonObject.put("unitPrice",cart.getTruePrice());
            jsonObject.put("amount",cart.getCartNum());

            jsonArray.add(jsonObject);

            double discountAmount = (cart.getTruePrice() - cart.getVipTruePrice()) * cart.getCartNum();

            cart.setDiscountAmount(new BigDecimal(discountAmount).setScale(2, BigDecimal.ROUND_HALF_UP));
            cart.setDiscount(new BigDecimal(1));
        }


        //生成分布式唯一值
        String orderSn = OrderUtil.generateOrderNoByUUId16();
        //组合数据
        YxStoreOrder storeOrder = new YxStoreOrder();
        storeOrder.setId(id==null?null:id.intValue());
//        storeOrder.setOriginalOrderNo(other.getOriginalOrderNo());
        storeOrder.setUid(uid);
        storeOrder.setOrderId(orderSn);
        storeOrder.setRealName(userAddress.getRealName());
        storeOrder.setUserPhone(userAddress.getPhone());
        storeOrder.setUserAddress(userAddress.getProvince()+" "+userAddress.getCity()+
                " "+userAddress.getDistrict()+" "+userAddress.getDetail());
        storeOrder.setCartId(StrUtil.join(",",cartIds));
        storeOrder.setTotalNum(totalNum);
        storeOrder.setTotalPrice(BigDecimal.valueOf(totalPrice));
        storeOrder.setTotalPostage(BigDecimal.valueOf(payPostage));
        storeOrder.setCouponId(couponId);
        storeOrder.setCouponPrice(BigDecimal.valueOf(couponPrice));
        storeOrder.setPayPrice(BigDecimal.valueOf(payPrice));
        storeOrder.setPayPostage(BigDecimal.valueOf(payPostage));
        storeOrder.setDeductionPrice(BigDecimal.valueOf(deductionPrice));
        storeOrder.setPaid(OrderInfoEnum.PAY_STATUS_0.getValue());

        LambdaQueryWrapper<Project> lambdaQueryWrapper = new LambdaQueryWrapper();
        lambdaQueryWrapper.eq(Project::getProjectCode,other.getProjectCode());
        Project project = projectService.getOne(lambdaQueryWrapper,false);

        if( project != null ) {
            storeOrder.setPayType(project.getPayType());
        } else {
            storeOrder.setPayType("offline");
        }


        storeOrder.setUseIntegral(BigDecimal.valueOf(0));
        storeOrder.setGainIntegral(BigDecimal.valueOf(gainIntegral));
        storeOrder.setMark(param.getMark());
        storeOrder.setCombinationId(combinationId);
        storeOrder.setPinkId(param.getPinkId());
        storeOrder.setSeckillId(seckillId);
        storeOrder.setBargainId(bargainId);
        storeOrder.setCost(BigDecimal.valueOf(cacheDTO.getPriceGroup().getCostPrice()));
//        if(AppFromEnum.ROUNTINE.getValue().equals(param.getFrom())){
        storeOrder.setIsChannel(OrderInfoEnum.PAY_CHANNEL_1.getValue());
//        }else{
//        }
        storeOrder.setAddTime(OrderUtil.getSecondTimestampTwo());
        storeOrder.setUnique(key);
        storeOrder.setShippingType(param.getShippingType());
        storeOrder.setImagePath(param.getImagePath());
        storeOrder.setProjectCode(other.getProjectCode());
        storeOrder.setStoreId(other.getStoreId());

        storeOrder.setInsteadFlag(param.getInsteadFlag());
        storeOrder.setNeedCloudProduceFlag(param.getNeedCloudProduceFlag());
        storeOrder.setNeedInternetHospitalPrescription(param.getNeedInternetHospitalPrescription());
        storeOrder.setCardNumber(other.getCardNumber());
        storeOrder.setCardType(other.getCardType());
        storeOrder.setRefereeCode(other.getRefereeCode());
        if(StrUtil.isNotBlank(param.getAddressId())) {
            storeOrder.setAddressId( Integer.valueOf( param.getAddressId()));
        }

        // 更新用药人信息
        if(StrUtil.isNotBlank(param.getDrugUserId())) {
            YxDrugUsers yxDrugUsers = yxDrugUsersService.getById( Integer.valueOf(param.getDrugUserId()));
            if(yxDrugUsers != null) {
                storeOrder.setDrugUserId(yxDrugUsers.getId());
                storeOrder.setDrugUserName(yxDrugUsers.getName());
                storeOrder.setDrugUserPhone(yxDrugUsers.getPhone());
                storeOrder.setDrugUserBirth(yxDrugUsers.getBirth());
                storeOrder.setDrugUserIdcard(yxDrugUsers.getIdcard());
                storeOrder.setDrugUserSex(yxDrugUsers.getSex());
                storeOrder.setDrugUserWeight(yxDrugUsers.getWeight());
                storeOrder.setDrugUserType(yxDrugUsers.getUserType());
            }
        }
        // 是否开票
        storeOrder.setNeedInvoiceFlag(param.getNeedInvoiceFlag());
        storeOrder.setInvoiceName(param.getInvoiceName());
        storeOrder.setInvoiceMail(param.getInvoiceMail());


        // 更新订单的收货省市区地址
        storeOrder.setProvinceName(userAddress.getProvince());
        storeOrder.setCityName(userAddress.getCity());
        storeOrder.setDistrictName(userAddress.getDistrict());
        storeOrder.setAddress(userAddress.getDetail());

        // 益药宝下发标记
        storeOrder.setUploadYiyaobaoFlag(0);
        storeOrder.setDemandId(other.getDemandId());

        // 云配液收货地址
        storeOrder.setCloudProduceAddress(param.getCloudProduceAddress());
        storeOrder.setRocheHospitalName(param.getRocheHospitalName());
        storeOrder.setPayeeAccountName("");
        storeOrder.setPayeeBankName("");
        storeOrder.setPayeeBankAccount("");
        storeOrder.setPayerAccountName("");
        storeOrder.setPayerVoucherImage("");

        if(param.getInsteadFlag() !=null && param.getInsteadFlag() == 1) { // 替别人下单
            storeOrder.setFactUserPhone(param.getPhone());
            storeOrder.setFactUserName(param.getRealName());
            YxUser yxUser = userService.getOne(new QueryWrapper<YxUser>().eq("phone",param.getPhone()).last("limit 1"),false);
            if(yxUser != null){
                storeOrder.setFactUserId(yxUser.getUid());
            }
        } else {

            if(StrUtil.isNotBlank(userInfo.getRealName()) ) {
                storeOrder.setFactUserName(userInfo.getRealName());
            } else {
                storeOrder.setFactUserName(userInfo.getNickname());
            }


            if(StrUtil.isNotBlank(userInfo.getPhone()) ) {
                storeOrder.setFactUserPhone(userInfo.getPhone());
            }else {
                storeOrder.setFactUserPhone(userInfo.getYaoshiPhone());
            }
            storeOrder.setFactUserId(uid);

        }


        //待支付
        storeOrder.setStatus(0);
        if(BigDecimal.valueOf(payPrice).compareTo(BigDecimal.ZERO)==0){
            storeOrder.setStatus(5);
        }
        // 没有处方照片的，状态改为 处方待上传
        if( StrUtil.isBlank(storeOrder.getImagePath()) && other.getNeedImageFlag() && (storeOrder.getNeedInternetHospitalPrescription() == null || storeOrder.getNeedInternetHospitalPrescription() == 0)) {
            storeOrder.setStatus(OrderStatusEnum.STATUS_14.getValue());
        } else if( storeOrder.getNeedInternetHospitalPrescription() != null && storeOrder.getNeedInternetHospitalPrescription() == 1) {
            storeOrder.setStatus(OrderStatusEnum.STATUS_10.getValue());
        }

        boolean res =false;
        if(id==null){
            res = save(storeOrder);
        }else{
            res = updateById(storeOrder);

            orderCartInfoService.deleteByOrderId(storeOrder.getId());
        }


        if(!res) throw new ErrorRequestException("订单生成或修改失败");

        //保存购物车商品信息
        orderCartInfoService.saveCartInfo(storeOrder.getId(),cartInfo);

        //购物车状态修改
        QueryWrapper<YxStoreCart> wrapper = new QueryWrapper<>();
        wrapper.in("id",cartIds);
        YxStoreCart cartObj = new YxStoreCart();
        cartObj.setIsPay(0);
        if(BigDecimal.valueOf(payPrice).compareTo(BigDecimal.ZERO)==0){
            cartObj.setIsPay(1);
        }
        storeCartMapper.update(cartObj,wrapper);

        //删除缓存
        delCacheOrderInfo(uid,key);

        //增加状态
        orderStatusService.create(storeOrder.getId(),"cache_key_create_order","订单生成");

        // 将订单发送至益药宝

        if("5".equals(storeOrder.getStatus())){
            cn.hutool.json.JSONObject jsonObject = JSONUtil.createObj();
            jsonObject.put("orderNo",storeOrder.getOrderId());
            jsonObject.put("desc","生成订单" );
            jsonObject.put("projectCode",storeOrder.getProjectCode());
            jsonObject.put("time", DateUtil.now());
            mqProducer.sendDelayQueue(bizRoutekeyYiyaobao,jsonObject.toString(),2000);
        }
        return storeOrder;
    }

    public ComputeDTO computedOrder4Project(int uid, String projectCode,BigDecimal totalPrice,String addressId) {
        ComputeDTO computeDTO = new ComputeDTO();
        BigDecimal  payPostage = new BigDecimal(0);
        computeDTO.setCouponPrice(0d);
        computeDTO.setDeductionPrice(0d);
        computeDTO.setTotalPrice(totalPrice.doubleValue());
        computeDTO.setPayPostage(payPostage.doubleValue());
        computeDTO.setPayPrice(NumberUtil.add(totalPrice,payPostage).doubleValue());

        LambdaQueryWrapper<Project> lambdaQueryWrapper = new LambdaQueryWrapper();
        lambdaQueryWrapper.eq(Project::getProjectCode,projectCode);
        Project project = projectService.getOne(lambdaQueryWrapper);
        if(project == null) {
            return computeDTO;
        }
        // 满多少金额免邮
        YxUserAddressQueryVo  userAddress = userAddressService.getYxUserAddressById(addressId);
        if(userAddress == null) {
            return computeDTO;
        }
        String province = userAddress.getProvince();
        String city = userAddress.getCity();
        Boolean isFreePostage = false;

        LambdaQueryWrapper<ProjectSalesArea> lambdaQueryWrapper1 = new LambdaQueryWrapper();
        lambdaQueryWrapper1.eq(ProjectSalesArea::getProjectCode,projectCode);
        lambdaQueryWrapper1.eq(ProjectSalesArea::getAreaName,province);
        ProjectSalesArea projectSalesArea = projectSalesAreaService.getOne(lambdaQueryWrapper1,false);
        if(projectSalesArea == null) {
            return computeDTO;
        }
        if(projectSalesArea.getIsFree()!=null && projectSalesArea.getIsFree() == 1 ) {
            if (NumberUtil.isGreaterOrEqual(totalPrice, new BigDecimal(projectSalesArea.getFreePostage()))) {  // 商品总金额 >= 免邮金额
                isFreePostage = true;
            }
        }

        if(isFreePostage) {
            payPostage = new BigDecimal(0);
        } else { // 按照地区计算邮费
            YxExpressTemplate yxExpressTemplate = yxExpressTemplateService.getById(project.getExpressTemplateId());
            if(ObjectUtil.isNotEmpty(yxExpressTemplate)) {
                // 先找 城市邮费
                YxExpressTemplateDetail yxExpressTemplateDetail =  yxExpressTemplateDetailService.getOne(new QueryWrapper<YxExpressTemplateDetail>().eq("template_id",yxExpressTemplate.getId()).eq("area_name", city).eq("level",2),false);
                if(ObjectUtil.isNotEmpty(yxExpressTemplateDetail)) {
                    payPostage = yxExpressTemplateDetail.getPrice();
                } else {
                    // 再找 省份邮费
                    YxExpressTemplateDetail yxExpressTemplateDetail_province =  yxExpressTemplateDetailService.getOne(new QueryWrapper<YxExpressTemplateDetail>().eq("template_id",yxExpressTemplate.getId()).eq("area_name", province).eq("level",1),false);
                    if(ObjectUtil.isNotEmpty(yxExpressTemplateDetail_province)) {
                        payPostage = yxExpressTemplateDetail_province.getPrice();
                    } else {
                        payPostage = new BigDecimal(0);
                    }

                }
            }
        }

        computeDTO.setPayPostage(payPostage.doubleValue());
        computeDTO.setPayPrice(NumberUtil.add(totalPrice,payPostage).doubleValue());

        return computeDTO;
    }


    public YxStoreCartQueryVo toYxStoreCartQueryVo(YxStoreCart arg0) {
        if ( arg0 == null ) {
            return null;
        }

        YxStoreCartQueryVo yxStoreCartQueryVo = new YxStoreCartQueryVo();

        yxStoreCartQueryVo.setId( arg0.getId() );
        yxStoreCartQueryVo.setUid( arg0.getUid() );
        yxStoreCartQueryVo.setType( arg0.getType() );
        yxStoreCartQueryVo.setProductId( arg0.getProductId() );
        yxStoreCartQueryVo.setProductAttrUnique( arg0.getProductAttrUnique() );
        yxStoreCartQueryVo.setCartNum( arg0.getCartNum() );
        yxStoreCartQueryVo.setAddTime( arg0.getAddTime() );
        yxStoreCartQueryVo.setCombinationId( arg0.getCombinationId() );
        yxStoreCartQueryVo.setSeckillId( arg0.getSeckillId() );
        yxStoreCartQueryVo.setBargainId( arg0.getBargainId() );
        yxStoreCartQueryVo.setStoreId( arg0.getStoreId() );
        yxStoreCartQueryVo.setIsInner( 0 );
        yxStoreCartQueryVo.setPartnerId( arg0.getPartnerId() );
        yxStoreCartQueryVo.setProjectCode( arg0.getProjectCode() );
        yxStoreCartQueryVo.setRefereeCode( arg0.getRefereeCode() );
        yxStoreCartQueryVo.setPartnerCode( arg0.getPartnerCode() );
        yxStoreCartQueryVo.setDepartCode( arg0.getDepartCode() );

        return yxStoreCartQueryVo;
    }

    @Override
    public void download4RocheSma(List<RocheOrderDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (RocheOrderDto yxStoreOrder : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            //报单日期
            map.put("报单日期",yxStoreOrder.getOrderDate());
            // 需求单提交人
            map.put("需求单提交人",yxStoreOrder.getUserName());
            //需求单编号
            map.put("需求单编号",yxStoreOrder.getOrderNo());
            //患者姓名
            map.put("患者姓名",yxStoreOrder.getPatientName());
            //患者年龄
            map.put("患者年龄",yxStoreOrder.getPatientAge());
            //患者性别
            map.put("患者性别",yxStoreOrder.getPatientSex());
            //患者体重
            map.put("患者体重",yxStoreOrder.getPatientWeight());
            //购买数量
            map.put("购买数量",yxStoreOrder.getPurchaseQty());
            //给药剂量
            map.put("给药剂量",yxStoreOrder.getDosage());
            //疾病诊断
            map.put("疾病诊断",yxStoreOrder.getDiagnosis());
            //处方医院
            map.put("处方医院",yxStoreOrder.getHospitalName());
            //处方医生
            map.put("处方医生",yxStoreOrder.getDoctorName());
            //处方日期
            map.put("处方日期",yxStoreOrder.getPrescriptionDate());
            // 付款日期
            map.put("付款日期",yxStoreOrder.getPayDate());
            //汇款人姓名
            map.put("汇款人姓名",yxStoreOrder.getPayerAccountName());
            //收件人姓名
            map.put("收件人姓名",yxStoreOrder.getReceiverName());
            //收件地址
            map.put("收件地址",yxStoreOrder.getAddress());
            //联系电话
            map.put("联系电话",yxStoreOrder.getReceiverMobile());
            //发药药房
            map.put("发药药房",yxStoreOrder.getStoreName());
            //服务药房
            map.put("服务药房",yxStoreOrder.getServiceDrugstoreName());
            //是否委托配液
            map.put("是否委托配液",yxStoreOrder.getNeedCloudProduceFlag());
            //是否赠送冰包
            map.put("是否赠送冰包",yxStoreOrder.getGiveIceFlag());
            //冰包赠送方
            map.put("冰包赠送方",yxStoreOrder.getIceGiver());
            //非首选药房发药原因（如有）
            map.put("非首选药房发药原因",yxStoreOrder.getReason());
            //特殊情况
            map.put("特殊情况",yxStoreOrder.getSpecialSituation());
            //药品配制日期
            map.put("药品配制日期",yxStoreOrder.getDrugPreparationDate());
            // 药品收货日期
            map.put("药品收货日期",yxStoreOrder.getDrugReceiptDate());
            //药品用完天数
            map.put("药品用完天数",yxStoreOrder.getDrugUseUpDay());
            //开始服药日期
            map.put("开始服药日期",yxStoreOrder.getStartDate());
            //预计用完日期
            map.put("预计用完日期",yxStoreOrder.getDrugUseUpDate());
            //药品有效期
            map.put("药品有效期",yxStoreOrder.getDrugExpiryDate());
            //是否效期内能用完
            map.put("是否效期内能用完",yxStoreOrder.getUsedUpFlag());
            //复购提醒日期
            map.put("复购提醒日期",yxStoreOrder.getRepurchaseReminderDate());
            //是否同意复购
            map.put("是否同意复购",yxStoreOrder.getRepurchaseFlag());
            //最近随访日期
            map.put("最近随访日期",yxStoreOrder.getLastFollowUpDate());
            //随访方式
            map.put("随访方式",yxStoreOrder.getFollowUpMethod());
            //家属反馈
            map.put("家属反馈",yxStoreOrder.getFamilyFeedback());
            //服务药师
            map.put("服务药师",yxStoreOrder.getServiceChemist());
            //订单金额
            map.put("订单金额",yxStoreOrder.getTotalAmount());
            //订单状态
            map.put("订单状态",yxStoreOrder.getStatusName());
            //退款状态
            map.put("退款状态",yxStoreOrder.getRefundStatusName());
            //退款用户说明
            map.put("退款用户说明",yxStoreOrder.getRefundDesc());
            //申请退款时间
            map.put("申请退款时间",yxStoreOrder.getApplyRefundDate());
            //实际退款时间
            map.put("实际退款时间",yxStoreOrder.getFactRefundDate());
            //结单日期
            map.put("结单日期",yxStoreOrder.getCompleteDate());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

    @Override
    public List<RocheOrderDto> convert2RocheOrder(List<YxStoreOrderDto> all) {
        List<RocheOrderDto> result = new ArrayList<>();
        for(YxStoreOrderDto order : all) {
            RocheOrderDto rocheOrderDto = new RocheOrderDto();
            rocheOrderDto.setOrderNo(order.getOrderId());
            rocheOrderDto.setOrderDate(OrderUtil.stampToDate(order.getAddTime().toString()));
            rocheOrderDto.setAddress(order.getAddress());
            if(order.getStatus() == 3  ) {
                YxStoreOrderStatus yxStoreOrderStatus = orderStatusService.getOne(new LambdaQueryWrapper<YxStoreOrderStatus>().eq(YxStoreOrderStatus::getOid,order.getId()).eq(YxStoreOrderStatus::getChangeType,OrderChangeTypeEnum.ClOSE_ORDER.getValue()),false );
                if( ObjectUtil.isNotNull(yxStoreOrderStatus)) {
                    rocheOrderDto.setCompleteDate( OrderUtil.stampToDate(yxStoreOrderStatus.getChangeTime().toString()));
                }
            }

            if(order.getRefundReasonTime() != null) {
                rocheOrderDto.setApplyRefundDate( OrderUtil.stampToDate(order.getRefundReasonTime().toString()));
            }
            if(order.getRefundFactTime() != null) {
                rocheOrderDto.setFactRefundDate(DateUtil.formatDateTime(order.getRefundFactTime()));
            }

            if(order.getNeedCloudProduceFlag() == 1) {
                rocheOrderDto.setNeedCloudProduceFlag("是");
            } else {
                rocheOrderDto.setNeedCloudProduceFlag("否");
            }

            if(StrUtil.isNotBlank(order.getDrugUserBirth()) && order.getDrugUserBirth().length() == 8) {
                rocheOrderDto.setPatientAge(co.yixiang.tools.utils.DateUtil.getAge(order.getDrugUserBirth()) );
            }

            rocheOrderDto.setPatientName(order.getDrugUserName());
            rocheOrderDto.setPatientSex(order.getDrugUserSex());
            rocheOrderDto.setPatientWeight(order.getDrugUserWeight());
            if(order.getPayTime() != null) {
                rocheOrderDto.setPayDate(OrderUtil.stampToDate(order.getPayTime().toString()));
            }
            rocheOrderDto.setPayerAccountName(order.getPayerAccountName());
            rocheOrderDto.setPurchaseQty(order.getTotalNum());
            rocheOrderDto.setReceiverMobile(order.getUserPhone());
            rocheOrderDto.setReceiverName(order.getRealName());
            rocheOrderDto.setAddress(order.getUserAddress());
            rocheOrderDto.setRefundDesc(order.getRefundReason());
            rocheOrderDto.setServiceChemist(order.getServiceChemist());
            rocheOrderDto.setStatusName(order.getStatusName());
            rocheOrderDto.setStoreName(order.getStoreName());
            rocheOrderDto.setServiceDrugstoreName(order.getServiceDrugstore());
            rocheOrderDto.setTotalAmount(order.getPayPrice().toString());
            rocheOrderDto.setUserName(order.getUserDTO().getRealName());
            rocheOrderDto.setHospitalName(order.getRocheHospitalName());
            result.add(rocheOrderDto);

        }
        return result;
    }
}