package co.yixiang.modules.yiyaobao.task;


import co.yixiang.modules.shop.domain.YxStoreOrder;
import co.yixiang.modules.shop.service.YxStoreOrderService;
import co.yixiang.modules.yiyaobao.service.OrderServiceImpl;
import co.yixiang.modules.yiyaobao.service.StockServiceImpl;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Component
@Slf4j
public class YiyaobaoTask {
    @Autowired
    private OrderServiceImpl yiyaobaoOrderService;

    @Autowired
    private YxStoreOrderService orderService;


   public void syncOrderStatus(){
       // storeOrder.getStatus() != 1 && storeOrder.getStatus() != 2 && storeOrder.getStatus() != 3 && ! "慈善赠药".equals(storeOrder.getType())
       QueryWrapper<YxStoreOrder> queryWrapper = new QueryWrapper();
       queryWrapper.notIn("status",1,2,3);
       queryWrapper.ne("type","慈善赠药");

       List<YxStoreOrder> orderList = orderService.list(queryWrapper);
       for (YxStoreOrder order :orderList) {
           String orderId = order.getOrderId();
           String yiyaobaoOrderStatus =  yiyaobaoOrderService.getYiyaobaoOrderStatus(orderId);

           log.info("orderNo:[{}],status:[{}],yiyaobaoStatus:[{}]",orderId,order.getStatus(),yiyaobaoOrderStatus);
       }
   }


}
