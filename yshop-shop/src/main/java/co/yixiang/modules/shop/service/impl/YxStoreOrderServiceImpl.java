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

// ?????????????????????
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

    // ????????????????????????????????????routeKey
    @Value("${meideyi.delayQueueName}")
    private String bizRoutekeyMeideyi;

    // ????????????????????????????????????routeKey
    @Value("${yaolian.delayQueueName}")
    private String bizRoutekeyYaolian;

    // ????????????????????????????????????routeKey
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
        //???????????????????????????????????????
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
     * ????????????
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

        //????????????
        String orderStatusStr = OrderUtil.orderStatusStr(yxStoreOrder.getPaid()
                ,yxStoreOrder.getStatus(),yxStoreOrder.getShippingType()
                ,yxStoreOrder.getRefundStatus());

        if(_status == 3){

            if(yxStoreOrder.getNeedRefund() == 1 ) {
                String refundTime = DateUtil.formatDateTime(yxStoreOrder.getCheckTime()) ;
                String str = "<b style='color:#f124c7'>????????????</b><br/>"+
                        "<span>???????????????"+"???????????????"+"</span><br/>" +
                        "<span>???????????????"+yxStoreOrder.getCheckFailRemark()+"</span><br/>" +
                        "<span>???????????????"+refundTime+"</span><br/>";
                orderStatusStr = str;
            } else {
                String refundTime= "";
                if(yxStoreOrder.getRefundReasonTime() != null) {
                    refundTime = OrderUtil.stampToDate(String.valueOf(yxStoreOrder
                            .getRefundReasonTime()));
                }

                String str = "<b style='color:#f124c7'>????????????</b><br/>"+
                        "<span>???????????????"+yxStoreOrder.getRefundReasonWap()+"</span><br/>" +
                        "<span>???????????????"+yxStoreOrder.getRefundReasonWapExplain()+"</span><br/>" +
                        "<span>???????????????"+refundTime+"</span><br/>";
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

        // ???????????????
        if(yxStoreOrderDto.getNeedCloudProduceFlag() != null && yxStoreOrderDto.getNeedCloudProduceFlag() == 1 ) {
            // ??????????????????
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
            map.put("?????????", yxStoreOrder.getOrderId());
            if(StrUtil.isNotBlank(yxStoreOrder.getUserDTO().getRealName())) {
                map.put("???????????????",yxStoreOrder.getUserDTO().getRealName());
            } else {
                map.put("???????????????",yxStoreOrder.getUserDTO().getNickname());
            }


            map.put("??????????????????",yxStoreOrder.getUserDTO().getPhone());

            map.put("????????????",yxStoreOrder.getDrugUserName());
            map.put("????????????",yxStoreOrder.getDrugUserPhone());

            map.put("???????????????", yxStoreOrder.getRealName());
            map.put("???????????????", yxStoreOrder.getUserPhone());
            map.put("?????????????????????", yxStoreOrder.getUserAddress());

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

        //    map.put("?????????id", yxStoreOrder.getCartId());

            map.put("????????????", cartString);
          //  map.put("????????????", yxStoreOrder.getFreightPrice());
         //   map.put("??????????????????", yxStoreOrder.getTotalNum());
            map.put("????????????", yxStoreOrder.getTotalPrice());
            map.put("??????", yxStoreOrder.getTotalPostage());
            map.put("??????????????????", yxStoreOrder.getPayPrice());
          //  map.put("????????????", yxStoreOrder.getPayPostage());
          //  map.put("????????????", yxStoreOrder.getDeductionPrice());
        //    map.put("?????????id", yxStoreOrder.getCouponId());
        //    map.put("???????????????", yxStoreOrder.getCouponPrice());
            map.put("????????????",yxStoreOrder.getPayTypeName());
            if(yxStoreOrder.getPaid() == 1) {
                map.put("????????????", "?????????");
            } else {
                map.put("????????????", "?????????");
            }

            if(yxStoreOrder.getPayTime() == null) {
                map.put("????????????", "");
            } else {
                map.put("????????????", OrderUtil.stampToDate(yxStoreOrder.getPayTime().toString()));
            }

          //  map.put("????????????", yxStoreOrder.getPayType());
            map.put("????????????", OrderUtil.stampToDate(yxStoreOrder.getAddTime().toString()));
            map.put("????????????", yxStoreOrder.getStatusName());
            if( yxStoreOrder.getRefundStatus() == null) {
                map.put("????????????", "?????????");
            }else{
                if( yxStoreOrder.getRefundStatus() == 0) {
                    map.put("????????????", "?????????");
                } else if(yxStoreOrder.getRefundStatus() == 1) {
                    map.put("????????????", "?????????");
                } else if (yxStoreOrder.getRefundStatus() == 2){
                    map.put("????????????", "?????????");
                }else {
                    map.put("????????????", "");
                }
            }
          //  map.put("????????????", yxStoreOrder.getRefundReasonWapImg());
            map.put("??????????????????", yxStoreOrder.getRefundReasonWapExplain());
            if(yxStoreOrder.getRefundReasonTime() == null) {
                map.put("??????????????????", "");
            } else {
                map.put("??????????????????", OrderUtil.stampToDate(yxStoreOrder.getRefundReasonTime().toString()));
            }
            if(yxStoreOrder.getRefundFactTime() != null) {
                map.put("??????????????????", DateUtil.formatDateTime(yxStoreOrder.getRefundFactTime()));
            } else {
                map.put("??????????????????", "");
            }

        //    map.put("??????????????????", yxStoreOrder.getRefundReasonWap());
         //   map.put("??????????????????", yxStoreOrder.getRefundReason());
         //   map.put("????????????", yxStoreOrder.getRefundPrice());
        //    map.put("??????????????????", yxStoreOrder.getDeliverySn());
        //    map.put("????????????/???????????????", yxStoreOrder.getDeliveryName());
        //    map.put("????????????", yxStoreOrder.getDeliveryType());
      //      map.put("????????????/?????????", yxStoreOrder.getDeliveryId());
          //  map.put("??????????????????", yxStoreOrder.getGainIntegral());
           // map.put("????????????", yxStoreOrder.getUseIntegral());
          //  map.put("???????????????????????????", yxStoreOrder.getBackIntegral());
            map.put("??????", yxStoreOrder.getMark());
       //     map.put("????????????", yxStoreOrder.getIsDel());
           // map.put("??????id(md5??????)??????id", yxStoreOrder.getUnique());
       //     map.put("???????????????", yxStoreOrder.getRemark());
            /*map.put("??????ID", yxStoreOrder.getMerId());
            map.put(" isMerCheck",  yxStoreOrder.getIsMerCheck());
            map.put("????????????id0????????????", yxStoreOrder.getCombinationId());
            map.put("??????id 0????????????", yxStoreOrder.getPinkId());
            map.put("?????????", yxStoreOrder.getCost());
            map.put("????????????ID", yxStoreOrder.getSeckillId());
            map.put("??????id", yxStoreOrder.getBargainId());
            map.put("?????????", yxStoreOrder.getVerifyCode());*/
            map.put("??????", yxStoreOrder.getStoreName());
          //  map.put("???????????? 1=?????? ???2=????????????", yxStoreOrder.getShippingType());
          //  map.put("????????????(0???????????????1???????????????)", yxStoreOrder.getIsChannel());
          //  map.put(" isRemind",  yxStoreOrder.getIsRemind());
          //  map.put(" isSystemDel",  yxStoreOrder.getIsSystemDel());
            /*ProjectNameEnum  projectNameEnum = ProjectNameEnum.toType(yxStoreOrder.getProjectCode());
            if(projectNameEnum == null) {
                map.put("????????????", "");
            } else{
                map.put("????????????", projectNameEnum.getDesc());
            }*/

            map.put("????????????", yxStoreOrder.getProjectName());

            map.put("???????????????", StringUtils.isEmpty(yxStoreOrder.getPayOutTradeNo())?yxStoreOrder.getOrderId():yxStoreOrder.getPayOutTradeNo());
            map.put("?????????(??????)???APPID(?????????)",yxStoreOrder.getMerchantNumber());
            map.put("????????????",yxStoreOrder.getMerchantName());
         /*  LambdaQueryWrapper<Project> projectLambdaQueryWrapper = new LambdaQueryWrapper<>();
           projectLambdaQueryWrapper.eq(Project::getProjectCode,yxStoreOrder.getProjectCode());
           Project project = projectService.getOne(projectLambdaQueryWrapper,false);
           if(project == null) {
               map.put("????????????", "");
           } else {
               map.put("????????????", project.getProjectName());
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
        String str = "[????????????]";
        if(pinkId > 0 || combinationId > 0){
            YxStorePink storePink = storePinkService.getOne(new QueryWrapper<YxStorePink>().
                    eq("order_id_key",id));
            if(ObjectUtil.isNull(storePink)) {
                str = "[????????????]";
            }else{
                switch (storePink.getStatus()){
                    case 1:
                        str = "[????????????]???????????????";
                        break;
                    case 2:
                        str = "[????????????]?????????";
                        break;
                    case 3:
                        str = "[????????????]?????????";
                        break;
                    default:
                        str = "[????????????]????????????";
                        break;
                }
            }

        }else if(seckillId > 0){
            str = "[????????????]";
        }else if(bargainId > 0){
            str = "[????????????]";
        }
        if(shippingType == 2) str = "[????????????]";
        return str;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void refund(YxStoreOrder resources) {
        if(resources.getPayPrice().doubleValue() < 0){
            throw new BadRequestException("?????????????????????");
        }
        YxStoreOrder storeOrder =  getOne(new QueryWrapper<YxStoreOrder>().eq("order_id",resources.getOrderId()));
        // needRefund ???????????????????????????????????????????????????????????????????????????needRefund ??? /order/prescripStatus ????????????
        // ???????????????????????????????????? 2???80???94???98 ??????needRefund????????? 1
        if( storeOrder.getNeedRefund() == 0) {
            storeOrder.setReturnType(resources.getReturnType());
            yiyaobaoCancelOrder(storeOrder);
        }

        if(resources.getPayType().equals("yue")){
            //????????????
            resources.setRefundStatus(2);
            resources.setRefundPrice(resources.getPayPrice());
            resources.setRefundFactTime(new Date());
            this.updateById(resources);

            //???????????????
            YxUserDto userDTO = generator.convert(userService.getOne(new QueryWrapper<YxUser>().eq("uid",storeOrder.getUid())),YxUserDto.class);
            userMapper.updateMoney(resources.getPayPrice().doubleValue(),
                    storeOrder.getUid());

            YxUserBill userBill = new YxUserBill();
            userBill.setUid(resources.getUid());

            userBill.setLinkId(resources.getId().toString());
            userBill.setPm(1);
            userBill.setTitle("????????????");
            userBill.setCategory("now_money");
            userBill.setType("pay_product_refund");
            userBill.setNumber(resources.getPayPrice());
            userBill.setBalance(NumberUtil.add(resources.getPayPrice(),userDTO.getNowMoney()));
            userBill.setMark("?????????????????????");
            userBill.setAddTime(OrderUtil.getSecondTimestampTwo());
            userBill.setStatus(1);
            yxUserBillService.save(userBill);


            YxStoreOrderStatus storeOrderStatus = new YxStoreOrderStatus();
            storeOrderStatus.setOid(resources.getId());
            storeOrderStatus.setChangeType("refund_price");
            storeOrderStatus.setChangeMessage("??????????????????"+resources.getPayPrice() +"???");
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
                    log.info("???????????????"+ com.alibaba.fastjson.JSONObject.toJSONString(storeOrder));

                    String alipayParm= alipayConfigurationService.refundOrder(storeOrder.getTradeNo(),new DecimalFormat("0.00").format(resources.getPayPrice()),storeOrder.getMerchantNumber());
//                    if("alipayH5".equals(storeOrder.getPayFrom())){
//                       alipayParm =  AlipayUtils.refundOrder(storeOrder.getTradeNo(),new DecimalFormat("0.00").format(resources.getPayPrice()), AlipayProperties.serverUrlH5,AlipayProperties.appIdH5,AlipayProperties.publicKeyH5,AlipayProperties.notifyUrlH5);
//                    }else if("alipay".equals(storeOrder.getPayFrom())){
//                       alipayParm = AlipayUtils.refundOrder(storeOrder.getTradeNo(),new DecimalFormat("0.00").format(resources.getPayPrice()), AlipayProperties.serverUrl,AlipayProperties.appId,AlipayProperties.publicKey,AlipayProperties.notifyUrl);
//                    }
                    if(StringUtils.isEmpty(alipayParm) || net.sf.json.JSONObject.fromObject(alipayParm).get("alipay_trade_refund_response")==null){
                        throw new BadRequestException("????????????????????????????????????");
                    }else {
                        AlipayTradeRefundResponse wdq = JsonUtil.getJsonToBean(net.sf.json.JSONObject.fromObject(alipayParm).get("alipay_trade_refund_response").toString(), AlipayTradeRefundResponse.class);
                        if (StringUtils.isNotEmpty(wdq.getCode()) && !wdq.getCode().equals("10000")) {
                            throw new BadRequestException("???????????????" + wdq.getSubMsg());
                        }
                        if (storeOrder.getRefundStatus() == 2 || storeOrder.getNeedRefund() == 2) {
                            return;
                        }
                        //????????????
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
                                jsonObject.put("desc", "???????????????????????????");
                                jsonObject.put("time", DateUtil.now());
                                mqProducer.sendDelayQueue(bizRoutekeyZhonganpuyao, jsonObject.toString(),2000);

                            } else if (ProjectNameEnum.MEIDEYI.getValue().equals(storeOrder.getProjectCode())) {
                                cn.hutool.json.JSONObject jsonObject = JSONUtil.createObj();
                                jsonObject.put("orderNo", storeOrder.getOrderId());
                                jsonObject.put("status", "-2");
                                jsonObject.put("desc", "????????????????????????");
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
                        throw new BadRequestException("????????????????????????????????????");
                    }

                    if (storeOrder.getRefundStatus() == 2 || storeOrder.getNeedRefund() == 2) {
                        return;
                    }
                    net.sf.json.JSONObject jsonObjectMapiParm=  net.sf.json.JSONObject.fromObject(mapiParm);
                    //????????????
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
                            jsonObject.put("desc", "???????????????????????????");
                            jsonObject.put("time", DateUtil.now());
                            mqProducer.sendDelayQueue(bizRoutekeyZhonganpuyao, jsonObject.toString(),2000);

                        } else if (ProjectNameEnum.MEIDEYI.getValue().equals(storeOrder.getProjectCode())) {
                            cn.hutool.json.JSONObject jsonObject = JSONUtil.createObj();
                            jsonObject.put("orderNo", storeOrder.getOrderId());
                            jsonObject.put("status", "-2");
                            jsonObject.put("desc", "????????????????????????");
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
                // ?????????????????????????????????????????????????????????????????????????????????????????????
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
//            long timestamp = System.currentTimeMillis(); // ?????????????????????
//            Map<String, String> headers = new HashMap<String, String>();
//            headers.put("ACCESS_APPID", "SYXKYYY"); // ??????APP
//            headers.put("ACCESS_TIMESTAMP", String.valueOf(timestamp)); // ?????????????????????
//            String ACCESS_SIGANATURE = AppSiganatureUtils
//                    .createSiganature(requestBody, "SYXKYYY", "ffa2d2b6-885c-47dc-a5f8-df9be3d5ce80",
//                            timestamp);
//            headers.put("ACCESS_SIGANATURE", ACCESS_SIGANATURE); // ?????????????????????
//            log.info("ACCESS_APPID={}", "SYXKYYY");
//            log.info("ACCESS_TIMESTAMP={}", String.valueOf(timestamp));
//            log.info("ACCESS_SIGANATURE={}", ACCESS_SIGANATURE);
//            log.info("url={}", url);
//            log.info("requestBody={}", requestBody);
//            String result = HttpUtils.postJsonHttps(url, requestBody, headers); // ????????????
//            log.info("???????????????????????????????????????{}", result);
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
        // ??????2?????????????????????
        queryWrapper.notIn("status", OrderStatusEnum.STATUS_3.getValue(),OrderStatusEnum.STATUS_7.getValue(),OrderStatusEnum.STATUS_8.getValue());
        queryWrapper.ne("type","????????????");
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

            if(yiyaobaoStatus.equals("01")) {  //?????????
                status=5;


            }else if(yiyaobaoStatus.equals("14") || yiyaobaoStatus.equals("15")) {  //?????????
                status=0;


            }else if(yiyaobaoStatus.equals("20") || yiyaobaoStatus.equals("25")  || yiyaobaoStatus.equals("30") ||
                    yiyaobaoStatus.equals("31") || yiyaobaoStatus.equals("35")  || yiyaobaoStatus.equals("36") ||
                    yiyaobaoStatus.equals("38") || yiyaobaoStatus.equals("40")  || yiyaobaoStatus.equals("41") ||
                    yiyaobaoStatus.equals("42")
            ) { //?????????
                status=0;


            } else if(yiyaobaoStatus.equals("43")){ //?????????
                status=1;


            } else if (yiyaobaoStatus.equals("50") ){  // ?????????????????????
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
            else {  // ????????????
                status=6;

            }
            if(ProjectNameEnum.ROCHE_SMA.getValue().equals(order.getProjectCode())) {
                if(status == 1 || status == 3) {  // ????????? ?????? ?????????
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
        // ?????? ?????????
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

            if(status == 1 || status == 3) {  // ????????? ?????? ?????????
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
        /*?????????????????????????????????????????????????????????*/
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

            //????????????
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

        // ???????????????
        List<Integer> cartIdList = new ArrayList<>();
        for(OrderDetailDto detailDto : orderDto.getDetails()) {
            Integer storeCartId = storeCartService.addCart(uid,detailDto.getDrug_id(),detailDto.getQuantity(),detailDto.getUnique()
                    ,"product",isNew,combinationId,seckillId,bargainId,departmentCode,partnerCode,refereeCode,projectCode);
            cartIdList.add(storeCartId);
        }


        String cartId = CollUtil.join(cartIdList,",");

        Map<String, Object> cartGroup = storeCartService.getUserProductCartList4Store(uid,cartId,1,projectCode);


        if(ObjectUtil.isNotEmpty(cartGroup.get("invalid"))){
            log.error("?????????????????????????????????");
            return order;
        }
        if(ObjectUtil.isEmpty(cartGroup.get("valid"))){
            log.error("???????????????????????????????????????");
            return order;
        }
        List<YxStoreCartQueryVo> cartInfo = (List<YxStoreCartQueryVo>)cartGroup.get("valid");


        PriceGroupDTO priceGroup = getOrderPriceGroup(cartInfo);

        ConfirmOrderDTO confirmOrderDTO = new ConfirmOrderDTO();

        confirmOrderDTO.setUsableCoupon(couponUserService
                .beUsableCoupon(uid,priceGroup.getTotalPrice()));
        //????????????
        OtherDTO other = new OtherDTO();
        other.setIntegralRatio(systemConfigService.getData("integral_ratio"));
        other.setIntegralFull(systemConfigService.getData("integral_full"));
        other.setIntegralMax(systemConfigService.getData("integral_max"));
        other.setStoreId(storeId);
        other.setPartnerCode(partnerCode);
        other.setProjectCode(projectCode);

        String orderKey = cacheOrderInfo(uid,cartInfo,
                priceGroup,other);


        // ????????????



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
        param.setPayType("????????????");
        param.setAddress(userAddress);
        param.setMark("????????????");
        param.setType("????????????");
        param.setOrderNo(orderDto.getOrder_sn());*/
        //????????????

        try{
            lock.lock();
            order = createOrder4Store(uid,orderKey,param);
        }finally {
            lock.unlock();
        }

        return order;
    }

    public PriceGroupDTO getOrderPriceGroup(List<YxStoreCartQueryVo> cartInfo) {

        String storePostageStr = systemConfigService.getData("store_postage");//???????????????
        Double storePostage = 0d;
        if(StrUtil.isNotEmpty(storePostageStr)) storePostage = Double.valueOf(storePostageStr);

        String storeFreePostageStr = systemConfigService.getData("store_free_postage");//????????????
        Double storeFreePostage = 0d;
        if(StrUtil.isNotEmpty(storeFreePostageStr)) storeFreePostage = Double.valueOf(storeFreePostageStr);

        Double totalPrice = getOrderSumPrice(cartInfo, "truePrice");//????????????????????? ??????????????????
        Double costPrice = getOrderSumPrice(cartInfo, "costPrice");//?????????????????????
        Double vipPrice = getOrderSumPrice(cartInfo, "vipTruePrice");//?????????????????????????????? ???????????????
        Double innerPrice = getOrderSumPrice(cartInfo, "innerPrice");//????????????????????????

        if(storeFreePostage == 0){//??????
            storePostage = 0d;
        }else{
            for (YxStoreCartQueryVo storeCart : cartInfo) {
                if(storeCart.getProductInfo().getIsPostage() == 0){//?????????
                    storePostage = NumberUtil.add(storePostage
                            ,storeCart.getProductInfo().getPostage()).doubleValue();
                }
            }
            //???????????????????????????????????? ????????????0
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
        // ????????????
        if(key.equals("truePrice")){
            for (YxStoreCartQueryVo storeCart : cartInfo) {
                sumPrice = NumberUtil.add(sumPrice,NumberUtil.mul(storeCart.getCartNum(),storeCart.getTruePrice()));
            }
        }else if(key.equals("costPrice")){
            for (YxStoreCartQueryVo storeCart : cartInfo) {
                sumPrice = NumberUtil.add(sumPrice,
                        NumberUtil.mul(storeCart.getCartNum(),storeCart.getCostPrice()));
            }
        }else if(key.equals("vipTruePrice")){  // vip???
            for (YxStoreCartQueryVo storeCart : cartInfo) {
                sumPrice = NumberUtil.add(sumPrice,
                        NumberUtil.mul(storeCart.getCartNum(),storeCart.getVipTruePrice()));
            }
        }else if(key.equals("innerPrice")){  // ?????????
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
     * ????????????-?????????
     * @param uid uid
     * @param key key
     * @param param param
     * @return
     */

    public YxStoreOrder createOrder4Store(int uid, String key, OrderParam param) {
        JSONArray jsonArray = JSONUtil.createArray();

       // YxUserQueryVo userInfo = userService.getYxUserById(uid);
      //  if(ObjectUtil.isNull(userInfo)) throw new ErrorRequestException("???????????????");

        CacheDTO cacheDTO = getCacheOrderInfo(uid,key);
        if(ObjectUtil.isNull(cacheDTO)){
            throw new ErrorRequestException("???????????????,?????????????????????");
        }

        List<YxStoreCartQueryVo> cartInfo = cacheDTO.getCartInfo();
        Double totalPrice =  cacheDTO.getPriceGroup().getTotalPrice();
        Double payPrice = cacheDTO.getPriceGroup().getTotalPrice();
        Double payPostage = cacheDTO.getPriceGroup().getStorePostage();
        OtherDTO other = cacheDTO.getOther();
        YxUserAddress userAddress = null;
        if(OrderInfoEnum.SHIPPIING_TYPE_1.getValue().equals(param.getShippingType())){
            if(StrUtil.isEmpty(param.getAddressId())) throw new ErrorRequestException("?????????????????????");
            userAddress = yxUserAddressService.getById(param.getAddressId());
            if(ObjectUtil.isNull(userAddress)) throw new ErrorRequestException("??????????????????");
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
            //????????????
            BigDecimal cartInfoGainIntegral = BigDecimal.ZERO;
            if(combinationId == 0 && seckillId == 0 && bargainId == 0){//??????????????????????????????
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


        //??????

        if(OrderInfoEnum.SHIPPIING_TYPE_1.getValue().equals(param.getShippingType())){
            payPrice = NumberUtil.add(payPrice,payPostage);
        }else{
            payPostage = 0d;
        }

        //?????????
        int couponId = 0;
        if(ObjectUtil.isNotEmpty(param.getCouponId())){
            couponId = param.getCouponId().intValue();
        }

        int useIntegral = param.getUseIntegral().intValue();

        boolean deduction = false;//?????????
        //????????????????????????
        if(combinationId > 0 || seckillId > 0 || bargainId > 0) deduction = true;
        if(deduction){
            couponId = 0;
            useIntegral = 0;
        }
        double couponPrice = 0; //???????????????

        // ????????????
        double deductionPrice = 0; //????????????
        double usedIntegral = 0; //???????????????



        if(payPrice <= 0) payPrice = 0d;

        // ?????????????????????????????????

        // ??????????????????
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

        // ???????????????????????????????????????
      //  String orderSn = uploadOrder2Yiyaobao(param,userAddress,yiyaobao_projectNo,yiyaobao_store_id,jsonArray.toString());

        //????????????????????????
         String orderSn = IdUtil.getSnowflake(0,0).nextIdStr();
        //????????????
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
            storeOrder.setType("????????????");
        } else if(OrderInfoEnum.PAY_CHANNEL_3.getValue() == param.getIsChannel()) {
            storeOrder.setPaid(OrderInfoEnum.PAY_STATUS_1.getValue());
            storeOrder.setPayTime(OrderUtil.getSecondTimestampTwo());
            storeOrder.setStatus(OrderStatusEnum.STATUS_5.getValue());
            storeOrder.setType("?????????????????????");
        } else {
            storeOrder.setPaid(OrderInfoEnum.PAY_STATUS_0.getValue());
            storeOrder.setStatus(OrderStatusEnum.STATUS_5.getValue());
            storeOrder.setType("?????????");
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
        if(!res) throw new ErrorRequestException("??????????????????");

        //??????????????????
        for (YxStoreCartQueryVo cart : cartInfo) {
            productService.decProductStock(cart.getCartNum(),cart.getProductId(),
                    cart.getProductAttrUnique());

        }

        //???????????????????????????
        orderCartInfoService.saveCartInfo(storeOrder.getId(),cartInfo);

        //?????????????????????
        QueryWrapper<YxStoreCart> wrapper = new QueryWrapper<>();
        wrapper.in("id",cartIds);
        YxStoreCart cartObj = new YxStoreCart();
        cartObj.setIsPay(1);
        storeCartMapper.update(cartObj,wrapper);

        //????????????
        delCacheOrderInfo(uid,key);

        //????????????
        orderStatusService.create(storeOrder.getId(),"cache_key_create_order","????????????");


        //??????MQ????????????
        //mqProducer.sendMsg("yshop-topic",storeOrder.getId().toString());
        //log.info("??????????????????id??? [{}]???", storeOrder.getId());

        //??????redis???30??????????????????
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
            throw new BadRequestException("??????????????????");
        }
        String userName = SecurityUtils.getUsername();
        UpdateWrapper updateWrapper = new UpdateWrapper();
        updateWrapper.set("check_fail_reason",resources.getCheckFailReason());
        updateWrapper.set("check_fail_remark",resources.getCheckFailRemark());
        updateWrapper.set("check_user",userName);
        updateWrapper.set("check_time", new Date());
        updateWrapper.set("check_status",resources.getCheckStatus());
        if( StrUtil.isNotBlank(resources.getCheckStatus()) && "?????????".equals(resources.getCheckStatus()) ) {
            updateWrapper.set("status",OrderStatusEnum.STATUS_6.getValue());
        } else if (StrUtil.isNotBlank(resources.getCheckStatus()) && "??????".equals(resources.getCheckStatus())) {
            updateWrapper.set("status",OrderStatusEnum.STATUS_0.getValue());
            if(ProjectNameEnum.ROCHE_SMA.getValue().equals(yxStoreOrder.getProjectCode())) {
                if(yxStoreOrder.getStoreId()==null){
                    throw new BadRequestException("???????????????");
                }
                RocheStore rocheStore =rocheStoreService.getById(yxStoreOrder.getStoreId());
                if(rocheStore == null) {
                    throw new BadRequestException("???????????????");
                }
                if(StringUtils.isEmpty(rocheStore.getPayeeBankName()) || StringUtils.isEmpty(rocheStore.getPayeeAccountName()) || StringUtils.isEmpty(rocheStore.getPayeeBankAccount())){
                    throw new BadRequestException("??????????????????????????????");
                }
                updateWrapper.set("payee_account_name",rocheStore.getPayeeAccountName());
                updateWrapper.set("payee_bank_name",rocheStore.getPayeeBankName());
                updateWrapper.set("payee_bank_account",rocheStore.getPayeeBankAccount());
            }
        }
        updateWrapper.eq("id",resources.getId());

        this.update(updateWrapper);

        //??????????????????
        try {
            YxWechatUserDto wechatUser = generator.convert(wechatUserService.getOne(new QueryWrapper<YxWechatUser>().eq("uid",yxStoreOrder.getUid())),YxWechatUserDto.class);
            if (ObjectUtil.isNotNull(wechatUser)) {
                log.info("??????????????????1");
                //??????????????????????????????????????????????????????
                if (StrUtil.isNotBlank(wechatUser.getRoutineOpenid())) {
                    log.info("??????????????????2");
                    String page = "pages/wode/orderDetail?orderId="+yxStoreOrder.getOrderId();
                    OrderTemplateMessage message = new OrderTemplateMessage();
                    message.setOrderDate( OrderUtil.stampToDate(yxStoreOrder.getAddTime().toString()));
                    message.setOrderId(yxStoreOrder.getOrderId());
                    String orderStatus = "";
                    String remark = "";
                    if("??????".equals(resources.getCheckStatus())) {
                        orderStatus = "????????????";
                        remark = "??????????????? ?????????????????? ?????????????????????";
                    } else {
                        orderStatus = "???????????????";
                        remark = StrUtil.emptyToDefault(resources.getCheckFailReason(),"") + "  " + StrUtil.emptyToDefault(resources.getCheckFailRemark(),"");
                    }
                    message.setOrderStatus(orderStatus);
                    message.setRemark(remark);
                    templateService.sendDYTemplateMessage(wechatUser.getRoutineOpenid(),page,message);
                }
            }
        } catch (Exception e) {
            log.info("????????????????????????????????????:{}",yxStoreOrder.getOrderId());
            e.printStackTrace();

        }
        if( StrUtil.isNotBlank(resources.getCheckStatus()) && "?????????".equals(resources.getCheckStatus()) ) {
            YxStoreOrderStatus storeOrderStatus = new YxStoreOrderStatus();
            storeOrderStatus.setOid(resources.getId());
            storeOrderStatus.setChangeType(OrderChangeTypeEnum.CHECK_FAIL.getValue());
            storeOrderStatus.setChangeMessage("??????????????????");
            storeOrderStatus.setChangeTime(OrderUtil.getSecondTimestampTwo());
            yxStoreOrderStatusService.save(storeOrderStatus);
        }else if(StrUtil.isNotBlank(resources.getCheckStatus()) && "??????".equals(resources.getCheckStatus())) {
            YxStoreOrderStatus storeOrderStatus = new YxStoreOrderStatus();
            storeOrderStatus.setOid(resources.getId());
            storeOrderStatus.setChangeType(OrderChangeTypeEnum.CHECK_PASS.getValue());
            storeOrderStatus.setChangeMessage("??????????????????");
            storeOrderStatus.setChangeTime(OrderUtil.getSecondTimestampTwo());
            yxStoreOrderStatusService.save(storeOrderStatus);
        }



    }

    @Override
    public void prescripStatus(PrescripStatusParam prescripStatusParam) {

        if( StrUtil.isBlank(prescripStatusParam.getPrescripNo())) {
           throw new BadRequestException("??????????????????");
        }

        if(StrUtil.isBlank(prescripStatusParam.getOrderId())) {
            throw new BadRequestException("???????????????id?????????");
        }

        // ???????????????id??????????????????
        String yiyaobaoOrderId = prescripStatusParam.getOrderId();
        String orderSource = yiyaobaoOrdOrderMapper.queryOrderSourceByOrderId(yiyaobaoOrderId);

        if("37".equals(orderSource)) {
            //oms-????????????
            prescripStatusParam.setProjectCode("ant");
            prescripStatusParam.setProjectName("????????????");
            JSONObject jsonObject = JSONUtil.parseObj(prescripStatusParam);

            log.info("??????oms?????????[{}]",jsonObject.toString());

            mqProducer.sendDelayQueue(antQueueName,jsonObject.toString(),2000);
            return;
        } else if ("14".equals(orderSource)) {
            // oms-????????????
            prescripStatusParam.setProjectCode("meditrust");
            prescripStatusParam.setProjectName("????????????");
            JSONObject jsonObject = JSONUtil.parseObj(prescripStatusParam);

            log.info("??????oms?????????[{}]",jsonObject.toString());

            mqProducer.sendDelayQueue(meditrustQueueName,jsonObject.toString(),2000);
        } else if ("48".equals(orderSource)) {
            // oms-????????????
            prescripStatusParam.setProjectCode("junling");
            prescripStatusParam.setProjectName("????????????");
            JSONObject jsonObject = JSONUtil.parseObj(prescripStatusParam);

            log.info("??????oms?????????[{}]",jsonObject.toString());

            mqProducer.sendDelayQueue(junlingQueueName,jsonObject.toString(),2000);
        } else if ("25".equals(orderSource)) {
            // oms-????????????
            prescripStatusParam.setProjectCode("yaolian");
            prescripStatusParam.setProjectName("????????????");
            JSONObject jsonObject = JSONUtil.parseObj(prescripStatusParam);

            log.info("??????oms?????????[{}]",jsonObject.toString());

            mqProducer.sendDelayQueue(bizRoutekeyYaolian,jsonObject.toString(),2000);
        } else if("32".equals(orderSource)) {
            // msh??????
            LambdaQueryWrapper<MshOrder> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(MshOrder::getYiyaobaoId,yiyaobaoOrderId);
            MshOrder mshOrder =  mshOrderMapper.selectOne(lambdaQueryWrapper);
            mshOrder.setExternalOrderId(prescripStatusParam.getOrderNo());
            YxStoreOrderStatus storeOrderStatus = new YxStoreOrderStatus();

            if("10".equals(prescripStatusParam.getPrescripStatus())) {
                mshOrder.setOrderStatus("1");
                mshOrder.setAuditTime(new Date());
                mshOrder.setAuditName(prescripStatusParam.getAuditName());
                //???????????????????????????
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
            // ????????????
            YxStoreOrder resources = getOne(new LambdaQueryWrapper<YxStoreOrder>().eq(YxStoreOrder::getYiyaobaoOrderId,yiyaobaoOrderId));
            Integer taipingStatus = 0;

            // ????????????????????????????????? ????????????
            if( resources != null &&   ProjectNameEnum.ROCHE_SMA.getValue().equals(resources.getProjectCode())) {
                return;
            }
            String status = "";
            String remark = "??????????????????????????????";
            String remindmessage = "";
            if("10".equals(prescripStatusParam.getPrescripStatus())) {
                resources.setStatus(OrderStatusEnum.STATUS_0.getValue());
                resources.setCheckTime(new Date());
                resources.setCheckStatus("??????");
                resources.setYiyaobaoOrderId(prescripStatusParam.getOrderId());
                resources.setYiyaobaoOrderNo(prescripStatusParam.getOrderNo());
                this.update(resources);
                status = "????????????";
                remark = "?????????????????????????????????";

                taipingStatus = TaipingOrderStatusEnum.STATUS_11.getValue();

                remindmessage = "????????????????????????????????????????????????%s??????????????????%s???%s??? ?????????????????????????????????????????????????????????????????????????????????400-9200-036";
                remindmessage = String.format(remindmessage,resources.getOrderId(), "????????????","?????????????????????????????????");
            }else if("2".equals(prescripStatusParam.getPrescripStatus())) {
              //  DateTime date = DateUtil.parse(prescripStatusParam.getDealDate(), DatePattern.NORM_DATE_FORMAT);
                resources.setStatus(OrderStatusEnum.STATUS_6.getValue());
                resources.setCheckTime(new Date());
                resources.setCheckStatus("?????????");
                resources.setCheckFailRemark(prescripStatusParam.getCheckFailRemark());
                resources.setCheckFailReason(prescripStatusParam.getCheckFailReason());
                resources.setYiyaobaoOrderId(prescripStatusParam.getOrderId());
                resources.setYiyaobaoOrderNo(prescripStatusParam.getOrderNo());
                if( (PayTypeEnum.WEIXIN.getValue().equals(resources.getPayType()) || PayTypeEnum.ALIPAY.getValue().equals(resources.getPayType()) || PayTypeEnum.ZhongAnPay.getValue().equals(resources.getPayType()) )&& new Integer(1).equals(resources.getPaid())) {
                    resources.setRefundStatus(1);
                    resources.setRefundReasonTime(OrderUtil.getSecondTimestampTwo());
                    resources.setRefundReasonWap( resources.getCheckFailReason() +" " + resources.getCheckFailRemark());
                }

                // ?????????????????????
                resources.setNeedRefund(1);
                this.update(resources);

                status = "?????????";
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
                // ??????????????????????????????????????????
                //todo ??????
                if( resources.getStoreId()!= null) {
                    YxSystemStore yxSystemStore = yxSystemStoreService.getById(resources.getStoreId());
                    if(yxSystemStore != null && StrUtil.isNotBlank(yxSystemStore.getLinkPhone())) {
                        List<String> phoneList= Arrays.asList(yxSystemStore.getLinkPhone().split(",")) ;
                        //????????????
                        for(String phone :phoneList) {

                            String remindmessage_manager = "???????????????????????????????????????????????????%s??????????????????%s";
                            remindmessage_manager = String.format(remindmessage_manager, "????????????", resources.getOrderId());
                            smsService.sendTeddy("",remindmessage_manager,phone);
                        }
                    }
                }

                remindmessage = "????????????????????????????????????????????????%s??????????????????%s???%s??? ?????????????????????????????????????????????????????????????????????????????????400-9200-036";
                remindmessage = String.format(remindmessage,resources.getOrderId(), "???????????????", checkFailReason);

            } else if ("43".equals(prescripStatusParam.getPrescripStatus())) {  // ??????
                if(prescripStatusParam.getPrescripNo()!=null&&prescripStatusParam.getPrescripNo().contains("msh")){

                }else{
                    resources.setStatus(OrderStatusEnum.STATUS_1.getValue());
                    resources.setDeliveryName(prescripStatusParam.getDeliveryName());
                    resources.setDeliveryId(prescripStatusParam.getDeliveryId());
                    resources.setYiyaobaoOrderId(prescripStatusParam.getOrderId());

                    this.update(resources);

                    status = "?????????";
                    InternetHospitalDemand internetHospitalDemand = internetHospitalDemandService.getOne( new QueryWrapper<InternetHospitalDemand>().eq("order_id",resources.getOrderId()).select("prescription_code"),false);
                    if(internetHospitalDemand != null) {
                        XkExpress xkExpress = new XkExpress();
                        xkExpress.setPrescriptionCode(internetHospitalDemand.getPrescriptionCode());
                        xkExpress.setExpressCode(prescripStatusParam.getDeliveryId());
                        xkExpress.setExpressCompanyCode("shunfeng");
                        xkExpress.setExpressCompanyName("??????");
                        xkProcessService.expressNotice(xkExpress);
                    }

                    // ??????ebs????????????????????????
                    if(ProjectNameEnum.DIAO.getValue().equals(resources.getProjectCode())) {
                        ebsService.send(resources.getOrderId(),resources.getRefereeCode());
                    }
                    taipingStatus = TaipingOrderStatusEnum.STATUS_13.getValue();

                    remindmessage = "????????????????????????????????????????????????%s??????????????????%s???%s??? ?????????????????????????????????????????????????????????????????????????????????400-9200-036";
                    remindmessage = String.format(remindmessage,resources.getOrderId(), "?????????", "???????????????"+prescripStatusParam.getDeliveryName() + " ????????????" + prescripStatusParam.getDeliveryId() );
                }
            } else if ("45".equals(prescripStatusParam.getPrescripStatus()) || "50".equals(prescripStatusParam.getPrescripStatus()) ) {  // ??????
                resources.setStatus(OrderStatusEnum.STATUS_3.getValue());
                resources.setDeliveryName(prescripStatusParam.getDeliveryName());
                resources.setDeliveryId(prescripStatusParam.getDeliveryId());
                resources.setYiyaobaoOrderId(prescripStatusParam.getOrderId());
                this.update(resources);
                status = "?????????";

                InternetHospitalDemand internetHospitalDemand = internetHospitalDemandService.getOne( new QueryWrapper<InternetHospitalDemand>().eq("order_id",resources.getOrderId()).select("prescription_code"),false);
                if(internetHospitalDemand != null) {
                    XkSign xkSign = new XkSign();
                    xkSign.setExpressCode(prescripStatusParam.getDeliveryId());
                    xkSign.setExpressCompanyCode("shunfeng");
                    xkSign.setExpressCompanyName("??????");
                    xkSign.setPrescriptionCode(internetHospitalDemand.getPrescriptionCode());
                    xkSign.setSignPerson(resources.getRealName());
                    xkSign.setSignDatetime(DateUtil.now());
                    xkProcessService.signNotice(xkSign);
                }
                taipingStatus = TaipingOrderStatusEnum.STATUS_14.getValue();

                remindmessage = "????????????????????????????????????????????????%s??????????????????%s??? ?????????????????????????????????????????????????????????????????????????????????400-9200-036";
                remindmessage = String.format(remindmessage,resources.getOrderId(), "?????????" );

                //????????????????????????
                if(ProjectNameEnum.YAOLIAN.getValue().equals(resources.getProjectCode())) {
                    /*try {
                        yaolianserviceImpl.pushOrderInfo(resources.getOrderId());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }*/

                    cn.hutool.json.JSONObject jsonObject = JSONUtil.createObj();
                    jsonObject.put("orderNo",resources.getOrderId());
                    jsonObject.put("status",OrderStatusEnum.STATUS_3.getValue().toString());
                    jsonObject.put("desc","?????????????????????");
                    jsonObject.put("time", DateUtil.now());
                    mqProducer.sendDelayQueue(bizRoutekeyYaolian,jsonObject.toString(),2000);

                } else if(ProjectNameEnum.MEIDEYI.getValue().equals(resources.getProjectCode())) {
                    cn.hutool.json.JSONObject jsonObject = JSONUtil.createObj();
                    jsonObject.put("orderNo",resources.getOrderId());
                    jsonObject.put("status","12");
                    jsonObject.put("desc","????????????????????????");
                    jsonObject.put("time", DateUtil.now());
                    mqProducer.sendDelayQueue(bizRoutekeyMeideyi, jsonObject.toString(),2000);
                } else if(ProjectNameEnum.DIAO.getValue().equals(resources.getProjectCode())) {
                    // ??????ebs????????????????????????
                    ebsService.send(resources.getOrderId(),resources.getRefereeCode());
                }
            } else if ("98".equals(prescripStatusParam.getPrescripStatus())) { // ??????
                status = "?????????";
                resources.setStatus(OrderStatusEnum.STATUS_7.getValue());
                resources.setCheckFailRemark("?????????????????????");
                resources.setNeedRefund(1);
                resources.setYiyaobaoOrderId(prescripStatusParam.getOrderId());
                this.update(resources);

                taipingStatus = TaipingOrderStatusEnum.STATUS_15.getValue();

                remindmessage = "????????????????????????????????????????????????%s??????????????????%s??? ?????????????????????????????????????????????????????????????????????????????????400-9200-036";
                remindmessage = String.format(remindmessage,resources.getOrderId(), "?????????" );
            } else if("30".equals(prescripStatusParam.getPrescripStatus())) {  // ?????????
                resources.setStatus(OrderStatusEnum.STATUS_9.getValue());
                this.update(resources);
                status = "?????????";

                // ??????ebs????????????????????????
                if(ProjectNameEnum.DIAO.getValue().equals(resources.getProjectCode())) {
                    ebsService.send(resources.getOrderId(),resources.getRefereeCode());
                }

            }else if("1".equals(prescripStatusParam.getPrescripStatus())){
                resources.setPaid(OrderInfoEnum.PAY_STATUS_1.getValue());
                resources.setPayTime(OrderUtil.getSecondTimestampTwo());
                resources.setYiyaobaoOrderId(prescripStatusParam.getOrderId());
                resources.setYiyaobaoOrderNo(prescripStatusParam.getOrderNo());
                this.update(resources);
                status = "?????????";
                remark = "???????????????????????????";

                taipingStatus = TaipingOrderStatusEnum.STATUS_10.getValue();

                remindmessage = "????????????????????????????????????????????????%s??????????????????%s???%s??? ?????????????????????????????????????????????????????????????????????????????????400-9200-036";
                remindmessage = String.format(remindmessage,resources.getOrderId(), "????????????","?????????????????????????????????");

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

                // ?????????????????????
                resources.setNeedRefund(1);
                this.update(resources);

                // ??????????????????????????????????????????
                //todo ??????
                if( resources.getStoreId()!= null) {
                    YxSystemStore yxSystemStore = yxSystemStoreService.getById(resources.getStoreId());
                    if(yxSystemStore != null && StrUtil.isNotBlank(yxSystemStore.getLinkPhone())) {
                        List<String> phoneList= Arrays.asList(yxSystemStore.getLinkPhone().split(",")) ;
                        //????????????
                        for(String phone :phoneList) {
                            String remindmessage_manager = "???????????????????????????????????????????????????%s??????????????????%s";
                            remindmessage_manager = String.format(remindmessage_manager, "????????????", resources.getOrderId());
                            smsService.sendTeddy("",remindmessage_manager,phone);
                        }
                    }
                }

            }

            if(resources != null) {
                //??????????????????????????????
                addStoreOrderStatusTime(resources);
                //??????????????????
                try {
                    YxWechatUserDto wechatUser = generator.convert(wechatUserService.getOne(new QueryWrapper<YxWechatUser>().eq("uid",resources.getUid())),YxWechatUserDto.class);
                    if (ObjectUtil.isNotNull(wechatUser)) {
                        //??????????????????????????????????????????????????????
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

                    // ??????????????????
                    YxUser yxuser = yxUserService.getById(resources.getUid());
                    if(yxuser != null && StrUtil.isNotBlank(yxuser.getPhone()) && StrUtil.isNotBlank(remindmessage)) {
                        smsService.sendTeddy("",remindmessage,yxuser.getPhone());
                    }
                } catch (Exception e) {
                    log.info("????????????????????????????????????:{}",resources.getOrderId());
                    e.printStackTrace();
                }

                // ????????????????????????????????????????????????????????????
                if( ProjectNameEnum.TAIPING_LEXIANG.getValue().equals(resources.getProjectCode()) && StrUtil.isNotBlank(resources.getTaipingOrderNumber()) && taipingStatus != 0) {
                    taipingCardService.sendOrderStatus(resources.getOrderId(),taipingStatus);
                }
                if(ProjectNameEnum.ZHONGANPUYAO.getValue().equals(resources.getProjectCode())) {
                    //   zhongAnPuYaoService.sendOrderInfo(resources.getOrderId());

                    cn.hutool.json.JSONObject jsonObject = JSONUtil.createObj();
                    jsonObject.put("orderNo",resources.getOrderId());
                    jsonObject.put("status",resources.getStatus().toString());
                    jsonObject.put("desc","??????????????????");
                    jsonObject.put("time", DateUtil.now());
                    mqProducer.sendDelayQueue(bizRoutekeyZhonganpuyao, jsonObject.toString(),2000);

                }
                if(ProjectNameEnum.ZHONGANMANBING.getValue().equals(resources.getProjectCode())) {
                    //   zhongAnPuYaoService.sendOrderInfo(resources.getOrderId());
                    cn.hutool.json.JSONObject jsonObject = JSONUtil.createObj();
                    jsonObject.put("orderNo",resources.getOrderId());
                    jsonObject.put("status",resources.getStatus().toString());
                    jsonObject.put("desc","??????????????????");
                    jsonObject.put("time", DateUtil.now());
                    mqProducer.sendDelayQueue(bizRoutekeyZhonganpuyao, jsonObject.toString(),2000);
                }
                if(ProjectNameEnum.LINGYUANZHI.getValue().equals(resources.getProjectCode())) {
                    //   zhongAnPuYaoService.sendOrderInfo(resources.getOrderId());
                    cn.hutool.json.JSONObject jsonObject = JSONUtil.createObj();
                    jsonObject.put("orderNo",resources.getOrderId());
                    jsonObject.put("status",resources.getStatus().toString());
                    jsonObject.put("desc","??????0????????????");
                    jsonObject.put("time", DateUtil.now());
                    mqProducer.sendDelayQueue(bizRoutekeyZhonganpuyao, jsonObject.toString(),2000);
                }
            }

        }

    }

    /**
     *  ??????????????????????????????
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
        }else if(resources.getStatus()!= null && resources.getStatus() == 3) {  // ?????????
            YxStoreOrderStatus storeOrderStatus = new YxStoreOrderStatus();
            storeOrderStatus.setOid(resources.getId());
            storeOrderStatus.setChangeType(OrderChangeTypeEnum.ClOSE_ORDER.getValue());
            storeOrderStatus.setChangeMessage(OrderChangeTypeEnum.ClOSE_ORDER.getDesc());
            storeOrderStatus.setChangeTime(OrderUtil.getSecondTimestampTwo());
            yxStoreOrderStatusService.save(storeOrderStatus);
        }else if(resources.getStatus()!= null &&resources.getStatus() == 11) {  // ???????????????
            YxStoreOrderStatus storeOrderStatus = new YxStoreOrderStatus();
            storeOrderStatus.setOid(resources.getId());
            storeOrderStatus.setChangeType(OrderChangeTypeEnum.TO_BE_CONFIRMED_PAY.getValue());
            storeOrderStatus.setChangeMessage(OrderChangeTypeEnum.TO_BE_CONFIRMED_PAY.getDesc());
            storeOrderStatus.setChangeTime(OrderUtil.getSecondTimestampTwo());
            yxStoreOrderStatusService.save(storeOrderStatus);
        }else if(resources.getStatus()!= null && resources.getPaid() != null && resources.getStatus() == 0 && resources.getPaid() == 1) {  //?????????
            YxStoreOrderStatus storeOrderStatus = new YxStoreOrderStatus();
            storeOrderStatus.setOid(resources.getId());
            storeOrderStatus.setChangeType(OrderChangeTypeEnum.PAID.getValue());
            storeOrderStatus.setChangeMessage(OrderChangeTypeEnum.PAID.getDesc());
            storeOrderStatus.setChangeTime(OrderUtil.getSecondTimestampTwo());
            yxStoreOrderStatusService.save(storeOrderStatus);
        }else if(resources.getStatus()!= null && resources.getPaid() != null && resources.getStatus() == 1 && resources.getPaid() == 1) {  //?????????
            YxStoreOrderStatus storeOrderStatus = new YxStoreOrderStatus();
            storeOrderStatus.setOid(resources.getId());
            storeOrderStatus.setChangeType(OrderChangeTypeEnum.DELIVERY_GOODS.getValue());
            storeOrderStatus.setChangeMessage(OrderChangeTypeEnum.DELIVERY_GOODS.getDesc());
            storeOrderStatus.setChangeTime(OrderUtil.getSecondTimestampTwo());
            yxStoreOrderStatusService.save(storeOrderStatus);
        }
    }


    /**
     *  ??????MSH????????????????????????
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

        // ??????????????????
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
        // ??????????????????
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


        // ??????????????????
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

            if( orderUserInfo.getDrugUserType()!= null && orderUserInfo.getDrugUserType() == 1 && StrUtil.isNotBlank(orderUserInfo.getDrugUserIdcard())) {  // ??????
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
        queryWrapper.eq("upload_yiyaobao_flag",0); // ?????????
       // queryWrapper.eq("paid",1);// ?????????
        queryWrapper.eq("refund_status",0); //?????????
        queryWrapper.ne("project_code",ProjectNameEnum.ROCHE_SMA.getValue());
        queryWrapper.in("status",OrderStatusEnum.STATUS_5.getValue());
        queryWrapper.select("order_id","project_code","paid","need_internet_hospital_prescription");
        List<YxStoreOrder> yxStoreOrderList = this.list(queryWrapper);
        for(YxStoreOrder yxStoreOrder:yxStoreOrderList) {
            if(ProjectNameEnum.TAIPING_LEXIANG.getValue().equals(yxStoreOrder.getProjectCode())) {
                if(yxStoreOrder.getPaid() == 1 && (yxStoreOrder.getNeedInternetHospitalPrescription() == 0 ||  yxStoreOrder.getNeedInternetHospitalPrescription() == 2)) {  // ?????????

                    yiyaobaoOrderService.sendOrder2YiyaobaoCloud(yxStoreOrder.getOrderId(),ProjectNameEnum.ROCHE_SMA.getValue());
                }

            } else {
                yiyaobaoOrderService.sendOrder2YiyaobaoStore(yxStoreOrder.getOrderId());
            }
        }


        // ??????????????????
        QueryWrapper queryWrapper_refund = new QueryWrapper();
        queryWrapper_refund.eq("upload_yiyaobao_refund_flag",0);
        queryWrapper_refund.eq("upload_yiyaobao_flag",1);
        queryWrapper_refund.eq("paid",1);
        queryWrapper_refund.in("refund_status",1,2);
        queryWrapper_refund.ne("check_status","?????????");
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
        if( "??????".equals(checkResult)) {
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

        // ??????????????????
        String projectCode = order4ProjectParam.getProjectCode();
        if(StrUtil.isBlank(projectCode)) {
            throw new BadRequestException("????????????????????????");
        }

        int uid =0;

        YxUser yxUser = yxUserService.getOne(new QueryWrapper<YxUser>().eq("phone",order4ProjectParam.getPhone()),false);
            if(yxUser == null) {
            uid = 10000000 + Long.valueOf(redisUtils.incr("patient",1)).intValue();

            //????????????
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


        // 1.???????????????
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
            // ?????????????????????????????????
            storeCartQueryVo.setVipTruePrice(price.doubleValue());
            //??????????????????????????????
            storeCartQueryVo.setTruePrice(price.doubleValue());
            storeCartQueryVo.setCostPrice(price.doubleValue());
            storeCartQueryVo.setTrueStock(productAttrValue.getStock());
            storeCartQueryVo.setYiyaobaoSku(storeProduct.getYiyaobaoSku());
            storeCartQueryVo.setProductAttrUnique(productAttrValue.getUnique());

            cartInfo.add(storeCartQueryVo);

        }

        //??????????????????name
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

        // 2.confirm??????

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

        // 3.????????????

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
            throw new ErrorRequestException("???????????????????????????????????????????????????");
        }
        String templateMessage=resources.getTemplateMessage();
        yxStoreOrderMapper.updateById(resources);
        resources=yxStoreOrderMapper.selectById(resources);
        YxWechatUserDto wechatUser = generator.convert(wechatUserService.getOne(new QueryWrapper<YxWechatUser>().eq("uid",resources.getUid())),YxWechatUserDto.class);
        if (ObjectUtil.isNotNull(wechatUser)) {
            //??????????????????????????????????????????????????????
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
            throw new ErrorRequestException("??????????????????????????????????????????????????????");
        }
    }

    @Override
    public void updateStatusSendTemplateMessage(YxStoreOrder resources) {
        resources=yxStoreOrderMapper.selectById(resources);
        YxWechatUserDto wechatUser = generator.convert(wechatUserService.getOne(new QueryWrapper<YxWechatUser>().eq("uid",resources.getUid())),YxWechatUserDto.class);
        if (ObjectUtil.isNotNull(wechatUser)) {
            //??????????????????????????????????????????????????????
            if (StrUtil.isNotBlank(wechatUser.getRoutineOpenid())) {
                String orderStatusStr = OrderUtil.orderStatusStr(resources.getPaid()
                        ,resources.getStatus(),resources.getShippingType()
                        ,resources.getRefundStatus());
                if(orderStatusStr.equals("??????????????????") || orderStatusStr.equals("??????????????????")){
                    orderStatusStr="????????????";
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
            map.put("??????????????????",objectMap.get("??????????????????"));
            map.put("??????",objectMap.get("??????"));
            map.put("??????",objectMap.get("??????"));
            map.put("??????",objectMap.get("??????"));
            map.put("????????????",objectMap.get("????????????"));
            list.add(map);
        }
        if(list.size()==0){
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("??????????????????","");
            map.put("??????","");
            map.put("??????","");
            map.put("??????","");
            map.put("????????????","");
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

                long timestamp = System.currentTimeMillis(); // ?????????????????????
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("ACCESS_APPID", appId); // ??????APP
                headers.put("ACCESS_TIMESTAMP", String.valueOf(timestamp)); // ?????????????????????
                String ACCESS_SIGANATURE = AppSiganatureUtils
                        .createSiganature(requestBody, appId, appSecret,
                                timestamp);
                headers.put("ACCESS_SIGANATURE", ACCESS_SIGANATURE); // ?????????????????????
                log.info("ACCESS_APPID={}", appId);
                log.info("ACCESS_TIMESTAMP={}", String.valueOf(timestamp));
                log.info("ACCESS_SIGANATURE={}", ACCESS_SIGANATURE);
                log.info("url={}", url);
                log.info("requestBody={}", requestBody);
                String result = null; // ????????????
                try {
                    result = HttpUtils.postJsonHttps(url, requestBody, headers);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                log.info("???????????????????????????????????????{}", result);
                if(StringUtils.isEmpty(result)){
                    throw new BadRequestException("???????????????????????????");
                }
                JSONObject object=JSONUtil.parseObj(result);
                if(object.get("code")==null){
                    throw new BadRequestException("???????????????????????????");
                }
                if(object.get("code").equals("1")){
                    throw new BadRequestException("????????????????????????,???????????????");
                }
                if(object.get("code").equals("2")){
                    throw new BadRequestException("????????????????????????,??????????????????");
                }
                if(object.get("code").equals("3")){
                    throw new BadRequestException("????????????????????????,??????????????????");
                }
                if(object.get("code").equals("5")){
                    throw new BadRequestException("????????????????????????,EBS??????????????????");
                }
                if(object.get("code").equals("90")){
                    throw new BadRequestException("????????????????????????,??????????????????");
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
                throw new BadRequestException("??????????????????");
            }
            if(StrUtil.isBlank(orderFreightParam.getOrderSource())) {
                throw new BadRequestException("?????????????????????");
            }
            if("37".equals(orderFreightParam.getOrderSource())) {
                //oms-????????????
                JSONObject jsonObject = JSONUtil.parseObj(orderFreightParam);
                log.info("??????oms?????????[{}]",jsonObject.toString());
                mqProducer.sendDelayQueue(orderFreightQueueName,jsonObject.toString(),2000);
                return;
            }
        }
    }

    /**
     * ????????????????????????
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
            messageRemark = "????????????????????????";//?????????
        } else if (paid == 1 && status == 0 && shipping_type == 1 && refund_status == 0) {
            messageRemark = "?????????????????????????????????????????????"; //?????????
        } else if (paid == 1 && status == 1 && shipping_type == 1 && refund_status == 0) {
            messageRemark = "???????????????";// ?????????
        }else if ( status == 3 && refund_status == 0) {
            messageRemark = "????????????????????????";//?????????
        } else if (paid == 1 && refund_status == 1) {
            messageRemark = "????????????????????????";//?????????
        } else if (paid == 1 && refund_status == 2) {
            messageRemark = "????????????????????????";//?????????
        } else if( status == 5) {
            messageRemark = "????????????????????????";//?????????
        } else if( status == 6) {
            messageRemark = "????????????????????????";//???????????????
        } else if( status == 7) {
            messageRemark = "????????????????????????";//??????????????????
        }else if( status == 8) {
            messageRemark = "????????????????????????";//??????????????????
        }else {
            messageRemark = "???????????????????????????";
        }

        return messageRemark;
    }

    public YxStoreOrder createTbOrderProject(Long id,int uid, String key, OrderParam param) {
        YxUserQueryVo userInfo = userService.getYxUserById(uid);
        if(ObjectUtil.isNull(userInfo)) throw new ErrorRequestException("???????????????");

        CacheDTO cacheDTO = getCacheOrderInfo(uid,key);
        if(ObjectUtil.isNull(cacheDTO)){
            throw new ErrorRequestException("???????????????,?????????????????????");
        }

        List<YxStoreCartQueryVo> cartInfo = cacheDTO.getCartInfo();

        OtherDTO other = cacheDTO.getOther();


        YxUserAddressQueryVo userAddress = null;
        if(OrderInfoEnum.SHIPPIING_TYPE_1.getValue().equals(param.getShippingType())){
            if(StrUtil.isEmpty(param.getAddressId())) throw new ErrorRequestException("?????????????????????");
            userAddress = userAddressService.getYxUserAddressById(param.getAddressId());
            if(ObjectUtil.isNull(userAddress)) throw new ErrorRequestException("??????????????????");
        }else{ //??????
            if(StrUtil.isBlank(param.getRealName()) || StrUtil.isBlank(param.getPhone())) {
                throw new ErrorRequestException("????????????????????????");
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
        //?????????
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
                throw new BadRequestException("["+ cart.getProductInfo().getStoreName() +"]"+"?????????????????????"+cart.getCartNum());
            }

            combinationId = cart.getCombinationId();
            seckillId = cart.getSeckillId();
            bargainId = cart.getBargainId();
            cartIds.add(cart.getId().toString());
            totalNum += cart.getCartNum();
            //????????????
            BigDecimal cartInfoGainIntegral = BigDecimal.ZERO;
            if(combinationId == 0 && seckillId == 0 && bargainId == 0){//??????????????????????????????
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


        //????????????????????????
        String orderSn = OrderUtil.generateOrderNoByUUId16();
        //????????????
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

        // ?????????????????????
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
        // ????????????
        storeOrder.setNeedInvoiceFlag(param.getNeedInvoiceFlag());
        storeOrder.setInvoiceName(param.getInvoiceName());
        storeOrder.setInvoiceMail(param.getInvoiceMail());


        // ????????????????????????????????????
        storeOrder.setProvinceName(userAddress.getProvince());
        storeOrder.setCityName(userAddress.getCity());
        storeOrder.setDistrictName(userAddress.getDistrict());
        storeOrder.setAddress(userAddress.getDetail());

        // ?????????????????????
        storeOrder.setUploadYiyaobaoFlag(0);
        storeOrder.setDemandId(other.getDemandId());

        // ?????????????????????
        storeOrder.setCloudProduceAddress(param.getCloudProduceAddress());
        storeOrder.setRocheHospitalName(param.getRocheHospitalName());
        storeOrder.setPayeeAccountName("");
        storeOrder.setPayeeBankName("");
        storeOrder.setPayeeBankAccount("");
        storeOrder.setPayerAccountName("");
        storeOrder.setPayerVoucherImage("");

        if(param.getInsteadFlag() !=null && param.getInsteadFlag() == 1) { // ???????????????
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


        //?????????
        storeOrder.setStatus(0);
        if(BigDecimal.valueOf(payPrice).compareTo(BigDecimal.ZERO)==0){
            storeOrder.setStatus(5);
        }
        // ???????????????????????????????????? ???????????????
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


        if(!res) throw new ErrorRequestException("???????????????????????????");

        //???????????????????????????
        orderCartInfoService.saveCartInfo(storeOrder.getId(),cartInfo);

        //?????????????????????
        QueryWrapper<YxStoreCart> wrapper = new QueryWrapper<>();
        wrapper.in("id",cartIds);
        YxStoreCart cartObj = new YxStoreCart();
        cartObj.setIsPay(0);
        if(BigDecimal.valueOf(payPrice).compareTo(BigDecimal.ZERO)==0){
            cartObj.setIsPay(1);
        }
        storeCartMapper.update(cartObj,wrapper);

        //????????????
        delCacheOrderInfo(uid,key);

        //????????????
        orderStatusService.create(storeOrder.getId(),"cache_key_create_order","????????????");

        // ???????????????????????????

        if("5".equals(storeOrder.getStatus())){
            cn.hutool.json.JSONObject jsonObject = JSONUtil.createObj();
            jsonObject.put("orderNo",storeOrder.getOrderId());
            jsonObject.put("desc","????????????" );
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
        // ?????????????????????
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
            if (NumberUtil.isGreaterOrEqual(totalPrice, new BigDecimal(projectSalesArea.getFreePostage()))) {  // ??????????????? >= ????????????
                isFreePostage = true;
            }
        }

        if(isFreePostage) {
            payPostage = new BigDecimal(0);
        } else { // ????????????????????????
            YxExpressTemplate yxExpressTemplate = yxExpressTemplateService.getById(project.getExpressTemplateId());
            if(ObjectUtil.isNotEmpty(yxExpressTemplate)) {
                // ?????? ????????????
                YxExpressTemplateDetail yxExpressTemplateDetail =  yxExpressTemplateDetailService.getOne(new QueryWrapper<YxExpressTemplateDetail>().eq("template_id",yxExpressTemplate.getId()).eq("area_name", city).eq("level",2),false);
                if(ObjectUtil.isNotEmpty(yxExpressTemplateDetail)) {
                    payPostage = yxExpressTemplateDetail.getPrice();
                } else {
                    // ?????? ????????????
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
            //????????????
            map.put("????????????",yxStoreOrder.getOrderDate());
            // ??????????????????
            map.put("??????????????????",yxStoreOrder.getUserName());
            //???????????????
            map.put("???????????????",yxStoreOrder.getOrderNo());
            //????????????
            map.put("????????????",yxStoreOrder.getPatientName());
            //????????????
            map.put("????????????",yxStoreOrder.getPatientAge());
            //????????????
            map.put("????????????",yxStoreOrder.getPatientSex());
            //????????????
            map.put("????????????",yxStoreOrder.getPatientWeight());
            //????????????
            map.put("????????????",yxStoreOrder.getPurchaseQty());
            //????????????
            map.put("????????????",yxStoreOrder.getDosage());
            //????????????
            map.put("????????????",yxStoreOrder.getDiagnosis());
            //????????????
            map.put("????????????",yxStoreOrder.getHospitalName());
            //????????????
            map.put("????????????",yxStoreOrder.getDoctorName());
            //????????????
            map.put("????????????",yxStoreOrder.getPrescriptionDate());
            // ????????????
            map.put("????????????",yxStoreOrder.getPayDate());
            //???????????????
            map.put("???????????????",yxStoreOrder.getPayerAccountName());
            //???????????????
            map.put("???????????????",yxStoreOrder.getReceiverName());
            //????????????
            map.put("????????????",yxStoreOrder.getAddress());
            //????????????
            map.put("????????????",yxStoreOrder.getReceiverMobile());
            //????????????
            map.put("????????????",yxStoreOrder.getStoreName());
            //????????????
            map.put("????????????",yxStoreOrder.getServiceDrugstoreName());
            //??????????????????
            map.put("??????????????????",yxStoreOrder.getNeedCloudProduceFlag());
            //??????????????????
            map.put("??????????????????",yxStoreOrder.getGiveIceFlag());
            //???????????????
            map.put("???????????????",yxStoreOrder.getIceGiver());
            //???????????????????????????????????????
            map.put("???????????????????????????",yxStoreOrder.getReason());
            //????????????
            map.put("????????????",yxStoreOrder.getSpecialSituation());
            //??????????????????
            map.put("??????????????????",yxStoreOrder.getDrugPreparationDate());
            // ??????????????????
            map.put("??????????????????",yxStoreOrder.getDrugReceiptDate());
            //??????????????????
            map.put("??????????????????",yxStoreOrder.getDrugUseUpDay());
            //??????????????????
            map.put("??????????????????",yxStoreOrder.getStartDate());
            //??????????????????
            map.put("??????????????????",yxStoreOrder.getDrugUseUpDate());
            //???????????????
            map.put("???????????????",yxStoreOrder.getDrugExpiryDate());
            //????????????????????????
            map.put("????????????????????????",yxStoreOrder.getUsedUpFlag());
            //??????????????????
            map.put("??????????????????",yxStoreOrder.getRepurchaseReminderDate());
            //??????????????????
            map.put("??????????????????",yxStoreOrder.getRepurchaseFlag());
            //??????????????????
            map.put("??????????????????",yxStoreOrder.getLastFollowUpDate());
            //????????????
            map.put("????????????",yxStoreOrder.getFollowUpMethod());
            //????????????
            map.put("????????????",yxStoreOrder.getFamilyFeedback());
            //????????????
            map.put("????????????",yxStoreOrder.getServiceChemist());
            //????????????
            map.put("????????????",yxStoreOrder.getTotalAmount());
            //????????????
            map.put("????????????",yxStoreOrder.getStatusName());
            //????????????
            map.put("????????????",yxStoreOrder.getRefundStatusName());
            //??????????????????
            map.put("??????????????????",yxStoreOrder.getRefundDesc());
            //??????????????????
            map.put("??????????????????",yxStoreOrder.getApplyRefundDate());
            //??????????????????
            map.put("??????????????????",yxStoreOrder.getFactRefundDate());
            //????????????
            map.put("????????????",yxStoreOrder.getCompleteDate());
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
                rocheOrderDto.setNeedCloudProduceFlag("???");
            } else {
                rocheOrderDto.setNeedCloudProduceFlag("???");
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