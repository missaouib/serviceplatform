/**
* Copyright (C) 2018-2020
* All rights reserved, Designed By www.yixiang.co
* 注意：
* 本软件为www.yixiang.co开发研制，未经购买不得使用
* 购买后可获得全部源代码（禁止转卖、分享、上传到码云、github等开源平台）
* 一经发现盗用、分享等行为，将追究法律责任，后果自负
*/
package co.yixiang.modules.msh.service;
import co.yixiang.common.service.BaseService;
import co.yixiang.modules.api.common.Result;
import co.yixiang.modules.msh.domain.MshOrder;
import co.yixiang.modules.msh.service.dto.MshOrderDto;
import co.yixiang.modules.msh.service.dto.MshOrderQueryCriteria;
import co.yixiang.modules.msh.service.dto.ServiceResult;
import co.yixiang.modules.shop.domain.YxSystemStore;
import co.yixiang.modules.shop.service.param.ExpressParam;
import co.yixiang.tools.express.dao.ExpressInfo;

import org.springframework.data.domain.Pageable;

import com.alibaba.fastjson.JSONObject;
import org.springframework.http.ResponseEntity;

import java.util.Map;
import java.util.List;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;

/**
* @author cq
* @date 2020-12-25
*/
public interface MshOrderService  extends BaseService<MshOrder>{

/**
    * 查询数据分页
    * @param criteria 条件
    * @param pageable 分页参数
    * @return Map<String,Object>
    */
    Map<String,Object> queryAll(MshOrderQueryCriteria criteria, Pageable pageable);

    /**
    * 查询所有数据不分页
    * @param criteria 条件参数
    * @return List<MshOrderDto>
    */
    List<MshOrder> queryAll(MshOrderQueryCriteria criteria);

    /**
    * 导出数据
    * @param all 待导出的数据
    * @param response /
    * @throws IOException /
    */
    void download(List<MshOrderDto> all, HttpServletResponse response) throws IOException;

     /**
      * 新增订单相关
      * @param all 新增订单相关
      * @param response /
      * @throws IOException /
      */
     ServiceResult<Boolean> makeOrder(JSONObject jsonObject);

     /**
      * 定时任务
      */
     public void syncOrderStatusMsh();

     /**
      * 获取物流信息
      */
     public ExpressInfo queryOrderLogisticsProcess(ExpressParam expressInfoDo);

    List<MshOrderDto> getMshOrderByDemandListId(Integer demandListId);

    Result<?> queryMshOrderLogisticsProcess(String phaOrderNo);

    ResponseEntity queryNewOrderLogisticsProcess(ExpressParam expressInfoDo);

    Result<?> queryMshOrderDetailInfo(String phaOrderNo);

    void lssueOrderByDemandListId(Integer demandListId);
}
