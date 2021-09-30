/**
 * Copyright (C) 2018-2020
 * All rights reserved, Designed By www.yixiang.co

 */
package co.yixiang.modules.shop.service;

import co.yixiang.common.service.BaseService;
import co.yixiang.modules.api.param.OrderFreightParam;
import co.yixiang.modules.api.param.OrderInfoParam;
import co.yixiang.modules.api.param.PrescripStatusParam;
import co.yixiang.modules.shop.domain.OrderUserInfo;
import co.yixiang.modules.shop.domain.YxStoreOrder;
import co.yixiang.modules.shop.service.dto.*;
import co.yixiang.modules.taibao.domain.TbOrderProjectParam;
import org.springframework.data.domain.Pageable;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
* @author hupeng
* @date 2020-05-12
*/
public interface YxStoreOrderService  extends BaseService<YxStoreOrder>{

/**
    * 查询数据分页
    * @param criteria 条件
    * @param pageable 分页参数
    * @return Map<String,Object>
    */
    Map<String,Object> queryAll(YxStoreOrderQueryCriteria criteria, Pageable pageable);

    /**
    * 查询所有数据不分页
    * @param criteria 条件参数
    * @return List<YxStoreOrderDto>
    */
    List<YxStoreOrder> queryAll(YxStoreOrderQueryCriteria criteria);


    YxStoreOrderDto create(YxStoreOrder resources);

    void update(YxStoreOrder resources);
    /**
    * 导出数据
    * @param all 待导出的数据
    * @param response /
    * @throws IOException /
    */
    void download(List<YxStoreOrderDto> all, HttpServletResponse response) throws IOException;


    Map<String,Object> queryAll(List<String> ids);


    String orderType(int id,int pinkId, int combinationId,int seckillId,
                     int bargainId,int shippingType);

    void refund(YxStoreOrder resources);

    OrderCountDto getOrderCount();

    OrderTimeDataDto getOrderTimeData();

    Map<String,Object> chartCount();

    void syncOrderStatus();

    void syncRocheOrderStatus();

    YxStoreOrder createOrder(YxStoreOrder4PCDto orderDto);


    /**
     * 查询数据分页
     * @param criteria 条件
     * @param pageable 分页参数
     * @return Map<String,Object>
     */
    Map<String,Object> queryAll4PC(YxStoreOrderQueryCriteria criteria, Pageable pageable);

    List<OrderStatisticsDto> getStatistics();


    void updateOrderInfo(OrderInfoParam orderInfoParam);

    void orderCheck(YxStoreOrder resources);

    void prescripStatus(PrescripStatusParam prescripStatusParam);

    void updateOrderUserInfo(OrderUserInfo orderUserInfo);

    void sendOrder2yiyaobao();

    void cancelConfirm(String json);

    /**
     * @param orderProjectParam
     * @return
     */
    YxStoreOrder addTbOrderProject(TbOrderProjectParam orderProjectParam);

    void sendTemplateMessage(YxStoreOrder resources);

    void updateStatusSendTemplateMessage(YxStoreOrder resources);

    void downloadByProjectCode(String startTime, String endTime, String projectCode, HttpServletResponse response);

    void yiyaobaoCancelOrder(YxStoreOrder storeOrder);

    YxStoreOrderDto getDetalById(Integer id);

    void orderFreight(List<OrderFreightParam> orderFreightParams);

    /**
     *  roche sma 项目导出数据
     * @param all 待导出的数据
     * @param response /
     * @throws IOException /
     */
    void download4RocheSma(List<RocheOrderDto> all, HttpServletResponse response) throws IOException;

    List<RocheOrderDto> convert2RocheOrder(List<YxStoreOrderDto> all);
}
