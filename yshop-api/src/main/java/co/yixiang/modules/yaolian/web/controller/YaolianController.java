package co.yixiang.modules.yaolian.web.controller;



import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import co.yixiang.annotation.AnonymousAccess;
import co.yixiang.logging.aop.log.Log;
import co.yixiang.modules.yaolian.dto.*;
import co.yixiang.modules.yaolian.entity.YaolianOrder;
import co.yixiang.modules.yaolian.service.YaolianOrderService;
import co.yixiang.modules.yaolian.service.impl.YaolianServiceImpl;
import co.yixiang.modules.yaolian.utils.Sha1Util;
import co.yixiang.utils.OrderUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/yaolian")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Api(value = "药联接口", tags = "药联接口")
public class YaolianController  {
    @Autowired
    private YaolianServiceImpl yaolianService;

    @Autowired
    private YaolianOrderService yaolianOrderService;

    @AnonymousAccess
    @Log(value = "药联订单推送")
    @PostMapping("/order/add")
    @ApiOperation(value = "药联订单回推接口",notes = "药联订单回推接口",response = Result4Order.class)
    public Result4Order addOrder(@RequestBody OrdersDTO resource){

        log.info("药联订单回推接口{}",resource);
        Result4Order result4Order = new Result4Order();
        RequestHead requestHead = resource.getRequestHead();

        if(ObjectUtil.isNull(requestHead)) {
            result4Order.setMsg("请求校验头不能为空");
            result4Order.setResult("0");
            return result4Order;
        }

       String sign =  Sha1Util.getSign(requestHead.getNonce(),requestHead.getTimestamp(),"uniondrug");
       if(!sign.equals(requestHead.getSign()))  {
           result4Order.setMsg("签名不一致");
           result4Order.setResult("0");
           return result4Order;
       }


        Order order = resource.getOrders().get(0);
        int count_exists = yaolianOrderService.count(new LambdaQueryWrapper<YaolianOrder>().eq(YaolianOrder::getId,order.getId()));

        if(count_exists > 0) {
            result4Order.setMsg("订单已经存在，不支持更新");
            result4Order.setResult("0");
            return result4Order;
        }

        // 校验处方是否能获取
        Elecrx elecrx = yaolianService.getElecrx(order.getRx_id());

        if(elecrx == null) {
            elecrx = new Elecrx();
            elecrx.setRx_id(IdUtil.simpleUUID());
            elecrx.setPic("https://wechat-api.yiyaogo.com/api/file/static/defaultMed.jpg");
        }

        if(elecrx == null || StrUtil.isBlank(elecrx.getPic())) {
            result4Order.setMsg("查询处方单失败,处方id："+ order.getRx_id());
            result4Order.setResult("0");
            return result4Order;
        } else {
            order.setElecrx(elecrx);
        }


        String orderNo = yaolianService.addOrder(resource);

        if(StrUtil.isNotBlank(orderNo)) {
            result4Order.setOrder_id(orderNo);
            result4Order.setMsg("");
            result4Order.setResult("1");
        } else {
            result4Order.setOrder_id("");
            result4Order.setMsg("");
            result4Order.setResult("0");
        }

        return result4Order;
    }

    @AnonymousAccess
    @Log(value = "药联退单")
    @PostMapping("/order/refund")
    @ApiOperation(value = "药联退单接口",notes = "药联退单接口",response = Result4RefundOrder.class)
    public Result4RefundOrder refundOrder(@RequestBody RefundOrderDTO resource){
        log.info("{}",resource);
        Result4RefundOrder result4Order = new Result4RefundOrder();
        RequestHead requestHead = resource.getRequestHead();

        if(ObjectUtil.isNull(requestHead)) {
            result4Order.setMsg("请求校验头不能为空");
            result4Order.setResult("0");
            return result4Order;
        }

        String sign =  Sha1Util.getSign(requestHead.getNonce(),requestHead.getTimestamp(),"uniondrug");
        if(!sign.equals(requestHead.getSign()))  {

            result4Order.setMsg("签名不一致");
            result4Order.setResult("0");
            return result4Order;
        }

      //  yaolianService.addOrder(resource);
        result4Order.setMsg("");
        result4Order.setOrder_id(resource.getRefund().get(0).getId());
        result4Order.setRefund_id(OrderUtil.generateOrderNoByUUId16());
        result4Order.setResult("1");
        return result4Order;
    }
}
