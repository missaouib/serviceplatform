package co.yixiang.modules.hospitaldemand.web.controller;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import co.yixiang.annotation.AnonymousAccess;
import co.yixiang.logging.aop.log.Log;
import co.yixiang.modules.hospitaldemand.entity.AttrDTO;
import co.yixiang.modules.hospitaldemand.entity.InternetHospitalDemand;
import co.yixiang.modules.hospitaldemand.entity.OrderParam;
import co.yixiang.modules.hospitaldemand.service.InternetHospitalDemandService;
import co.yixiang.modules.hospitaldemand.web.param.InternetHospitalDemandOrderParam;
import co.yixiang.modules.hospitaldemand.web.param.InternetHospitalDemandQueryParam;
import co.yixiang.modules.hospitaldemand.web.vo.InternetHospitalCart;
import co.yixiang.modules.hospitaldemand.web.vo.InternetHospitalDemandQueryVo;
import co.yixiang.common.web.controller.BaseController;
import co.yixiang.common.api.ApiResult;
import co.yixiang.modules.order.entity.YxStoreOrder;
import co.yixiang.modules.user.entity.YxWechatUser;
import co.yixiang.modules.user.service.YxWechatUserService;
import co.yixiang.modules.xikang.service.XkProcessService;
import co.yixiang.mp.service.YxTemplateService;
import co.yixiang.mp.service.dto.OrderTemplateMessage;
import co.yixiang.rabbitmq.send.MqProducer;
import co.yixiang.utils.SecurityUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import co.yixiang.common.web.vo.Paging;
import co.yixiang.common.web.param.IdParam;

/**
 * <p>
 * 互联网医院导入的需求单 前端控制器
 * </p>
 *
 * @author visa
 * @since 2020-12-04
 */
@Slf4j
@RestController
@RequestMapping("/internetHospitalDemand")
@Api("互联网医院导入的需求单 API")
public class InternetHospitalDemandController extends BaseController {

    @Autowired
    private InternetHospitalDemandService internetHospitalDemandService;

    @Autowired
    private XkProcessService xkProcessService;

    @Autowired
    private YxWechatUserService wechatUserService;

    @Autowired
    YxTemplateService templateService;

    @Autowired
    private MqProducer mqProducer;

    // 业务队列绑定业务交换机的routeKey
    @Value("${yiyaobao.delayQueueName}")
    private String bizRoutekeyYiyaobao;
    /**
    * 添加互联网医院导入的需求单
    */
    @PostMapping("/add")
    @ApiOperation(value = "添加InternetHospitalDemand对象",notes = "添加互联网医院导入的需求单",response = ApiResult.class)
    @Log
    public ApiResult<Boolean> addInternetHospitalDemand(@Valid @RequestBody InternetHospitalDemand internetHospitalDemand) throws Exception{
        log.info("互联网医院导入的需求单={}",internetHospitalDemand);
        YxStoreOrder yxStoreOrder = internetHospitalDemandService.saveDemand(internetHospitalDemand);

        // 未上传
        if( ObjectUtil.isNotNull(yxStoreOrder) &&  Integer.valueOf(0).compareTo(yxStoreOrder.getUploadYiyaobaoFlag())==0 &&  Integer.valueOf(1).equals(yxStoreOrder.getPaid())){
            // sendPrsProducer.sendMsg("prsShop-topic", yxStoreOrder.getOrderId());
            log.info("投递延时订单id： [{}]：", yxStoreOrder.getOrderId());

            cn.hutool.json.JSONObject jsonObject = JSONUtil.createObj();
            jsonObject.put("orderNo",yxStoreOrder.getOrderId());
            jsonObject.put("desc","熙康互联网医院推送处方" );
            jsonObject.put("projectCode",yxStoreOrder.getProjectCode());
            jsonObject.put("time", DateUtil.now());
            mqProducer.sendDelayQueue(bizRoutekeyYiyaobao,jsonObject.toString(),2000);

        }

        return ApiResult.result(true);
    }

    /**
    * 修改互联网医院导入的需求单
    */
    @PostMapping("/update")
    @ApiOperation(value = "修改InternetHospitalDemand对象",notes = "修改互联网医院导入的需求单",response = ApiResult.class)
    public ApiResult<Boolean> updateInternetHospitalDemand(@Valid @RequestBody InternetHospitalDemand internetHospitalDemand) throws Exception{
        boolean flag = internetHospitalDemandService.updateById(internetHospitalDemand);
        return ApiResult.result(flag);
    }

    /**
    * 删除互联网医院导入的需求单
    */
    @PostMapping("/delete")
    @ApiOperation(value = "删除InternetHospitalDemand对象",notes = "删除互联网医院导入的需求单",response = ApiResult.class)
    public ApiResult<Boolean> deleteInternetHospitalDemand(@Valid @RequestBody IdParam idParam) throws Exception{
        boolean flag = internetHospitalDemandService.removeById(idParam.getId());
        return ApiResult.result(flag);
    }

    /**
    * 获取互联网医院导入的需求单
    */
    @PostMapping("/info")
    @ApiOperation(value = "获取InternetHospitalDemand对象详情",notes = "查看互联网医院导入的需求单",response = InternetHospitalDemandQueryVo.class)
    public ApiResult<InternetHospitalDemandQueryVo> getInternetHospitalDemand(@Valid @RequestBody IdParam idParam) throws Exception{
        InternetHospitalDemandQueryVo internetHospitalDemandQueryVo = internetHospitalDemandService.getInternetHospitalDemandById(idParam.getId());
        return ApiResult.ok(internetHospitalDemandQueryVo);
    }

    /**
     * 互联网医院导入的需求单分页列表
     */
    @PostMapping("/getPageList")
    @ApiOperation(value = "获取InternetHospitalDemand分页列表",notes = "互联网医院导入的需求单分页列表",response = InternetHospitalDemandQueryVo.class)
    public ApiResult<Paging<InternetHospitalDemand>> getInternetHospitalDemandPageList(@Valid @RequestBody(required = false) InternetHospitalDemandQueryParam internetHospitalDemandQueryParam) throws Exception{

        internetHospitalDemandQueryParam.setUid(SecurityUtils.getUserId().intValue());

        Paging<InternetHospitalDemand> paging = internetHospitalDemandService.getInternetHospitalDemandPageList(internetHospitalDemandQueryParam);
        return ApiResult.ok(paging);
    }


    /**
     * 根据互联网医院的需求单生成购物车
     */
    @GetMapping("/generateCart/{id}")
    @ApiOperation(value = "根据互联网医院的需求单生成购物车",notes = "根据互联网医院的需求单生成购物车",response = ApiResult.class)
    @AnonymousAccess
    public ApiResult<InternetHospitalCart> generateCart( @PathVariable Integer id) throws Exception{
        InternetHospitalCart cart = internetHospitalDemandService.generateCart(id);
        return ApiResult.ok(cart);
    }


    /**
     * 后补处方单，从互联网医院获取处方图片
     */
    @PostMapping("/queryImage")
    @ApiOperation(value = "从互联网医院获取处方图片",notes = "从互联网医院获取处方图片",response = ApiResult.class)
    public ApiResult<InternetHospitalCart> queryInternetHospitalPrescriptionImage(  @RequestBody OrderParam orderParam) {

        log.info("从互联网医院获取处方图片,orderKey={}",orderParam.getOrderKey());
        Integer uid = SecurityUtils.getUserId().intValue();
        InternetHospitalCart internetHospitalCart = internetHospitalDemandService.queryInternetHospitalPrescriptionImage(uid,orderParam.getOrderKey());

        return ApiResult.ok(internetHospitalCart);
    }


    /**
     * 获取互联网医院的医生问诊的页面url
     */
    @PostMapping("/queryDoctorUrl")
    @AnonymousAccess
    @ApiOperation(value = "获取互联网医院的医生问诊的页面url",notes = "获取互联网医院的医生问诊的页面url",response = ApiResult.class)
    public ApiResult<String> queryDoctorUrl(  @RequestBody AttrDTO attrDTO) {

        log.info("获取互联网医院的医生问诊的页面url,orderParam={}",attrDTO);
       /// Integer uid = SecurityUtils.getUserId().intValue();
       // InternetHospitalCart internetHospitalCart = internetHospitalDemandService.queryInternetHospitalPrescriptionImage(uid,orderParam.getOrderKey());
       // String url="https://wechat-api-test.yiyaogo.com/api/file/static/policy.htm";
        attrDTO.setUid(SecurityUtils.getUserId().intValue());
        String url = xkProcessService.h5Url4doctor(attrDTO);
        return ApiResult.ok(url);
    }


    /**
     * 互联网医院处方申请状态回传
     */
    @PostMapping("/notice")
    @AnonymousAccess
    @ApiOperation(value = "互联网医院处方申请状态回传",notes = "互联网医院处方申请状态回传",response = ApiResult.class)
    public ApiResult<Boolean> notice( @RequestBody InternetHospitalDemandOrderParam orderParam) throws Exception{
        log.info("互联网医院处方申请状态回传：{}",orderParam);
        boolean flag = internetHospitalDemandService.noticeDemand(orderParam);
        return ApiResult.ok(flag);
    }


    @PostMapping("/pattest")
    @AnonymousAccess
    public ApiResult<Boolean> pattest( @RequestBody InternetHospitalDemand internetHospitalDemand) throws Exception{
        log.info("pattest：{}",internetHospitalDemand);
        xkProcessService.payNotice(internetHospitalDemand.getPrescriptionCode());
        return ApiResult.ok();
    }

    @PostMapping("/sendNotice")
    @AnonymousAccess
    public ApiResult<Boolean> sendNotice( @RequestBody InternetHospitalDemand internetHospitalDemand) throws Exception{
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

        return ApiResult.ok();
    }

}

