/**
 * Copyright (C) 2018-2020
 * All rights reserved, Designed By www.yixiang.co

 */
package co.yixiang.modules.shop.service.mapper;

import co.yixiang.common.mapper.CoreMapper;
import co.yixiang.modules.shop.domain.YxStoreOrder;
import co.yixiang.modules.shop.service.dto.ChartDataDto;
import co.yixiang.modules.shop.service.dto.OrderStatisticsDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
* @author hupeng
* @date 2020-05-12
*/
@Repository
@Mapper
public interface StoreOrderMapper extends CoreMapper<YxStoreOrder> {

    @Select("SELECT COUNT(*) FROM yx_store_order WHERE pay_time >= ${today}")
    Integer countByPayTimeGreaterThanEqual(@Param("today")int today);
    @Select("SELECT COUNT(*) FROM yx_store_order WHERE pay_time < ${today}  and pay_time >= ${yesterday}")
    Integer countByPayTimeLessThanAndPayTimeGreaterThanEqual(@Param("today")int today, @Param("yesterday")int yesterday);
    @Select( "select IFNULL(sum(pay_price),0)  from yx_store_order " +
            "where refund_status=0 and is_del=0 and paid=1")
    Double sumTotalPrice();

    @Select("SELECT IFNULL(sum(pay_price),0) as num," +
            "FROM_UNIXTIME(add_time, '%m-%d') as time " +
            " FROM yx_store_order where refund_status=0 and is_del=0 and paid=1 and pay_time >= ${time}" +
            " GROUP BY FROM_UNIXTIME(add_time,'%Y-%m-%d') " +
            " ORDER BY add_time ASC")
    List<ChartDataDto> chartList(@Param("time") int time);
    @Select("SELECT count(id) as num," +
            "FROM_UNIXTIME(add_time, '%m-%d') as time " +
            " FROM yx_store_order where refund_status=0 and is_del=0 and paid=1 and pay_time >= ${time}" +
            " GROUP BY FROM_UNIXTIME(add_time,'%Y-%m-%d') " +
            " ORDER BY add_time ASC")
    List<ChartDataDto> chartListT(@Param("time")int time);

    @Select("SELECT t.statusName AS statusName,SUM(t.cc) AS countOrder,SUM(t.cs_1) AS countOrder4cs \n" +
            "  FROM (\n" +
            "  SELECT statusName,COUNT(1) AS cc,count(status4cs_1) AS cs_1 FROM (\n" +
            "SELECT order_id,yso.real_name,pay_type,\n" +
            "  CASE WHEN yso.status =7 THEN '药店取消订单' \n" +
            "  WHEN (yso.status = 2 || yso.status = 3) THEN '完成' \n" +
            "  WHEN status = 6 THEN '审核未通过'\n" +
            "  WHEN yso.paid = 1 AND yso.status = 0 THEN '已支付，待发货'\n" +
            "  WHEN yso.paid = 0 AND yso.status = 0 THEN '待支付'\n" +
            "  WHEN yso.status in (8) THEN '用户取消订单'\n" +
            "  WHEN yso.status = 1 THEN '待收货'\n" +
            "  WHEN yso.status = 5 THEN '待审核'\n" +
            "  \n" +
            "  ELSE NULL END statusName, \n" +
            "  CASE \n" +
            "\n" +
            "\n" +
            "  WHEN yso.pay_type = '慈善赠药' and yso.status =7 THEN '药店取消订单' \n" +
            "  WHEN yso.pay_type = '慈善赠药' AND (yso.status = 2 || yso.status = 3) THEN '完成' \n" +
            "  WHEN yso.pay_type = '慈善赠药' AND status = 6 THEN '审核未通过'\n" +
            "  WHEN yso.pay_type = '慈善赠药' AND yso.paid = 1 AND yso.status = 0 THEN '已支付，待发货'\n" +
            "  WHEN yso.pay_type = '慈善赠药' AND yso.paid = 0 AND yso.status = 0 THEN '待支付'\n" +
            "  WHEN yso.pay_type = '慈善赠药' AND yso.status in (8) THEN '用户取消订单'\n" +
            "  WHEN yso.pay_type = '慈善赠药' AND yso.status = 1 THEN '待收货'\n" +
            "  WHEN yso.pay_type = '慈善赠药' AND yso.status = 5 THEN '待审核'\n" +
            "  \n" +
            "  ELSE NULL\n" +
            "\n" +
            "  END AS  status4cs_1\n" +
            "   \n" +
            "    FROM yx_store_order yso\n" +
            "    ) x GROUP BY x.statusName,status4cs_1\n" +
            "  ) t GROUP BY t.statusName ORDER BY t.statusName")
    List<OrderStatisticsDto> getStatistics();

    @Update("UPDATE yx_store_product_attr_value yspav set yspav.stock = 0 WHERE yspav.suk != #{storeName} and attr_id != #{batchNo} and yspav.suk != '' ")
    void updateStock(@Param("storeName") String storeName,@Param("batchNo") Integer batchNo );

    @Select("<script> SELECT DATE_FORMAT(FROM_UNIXTIME(yso.pay_time),'%Y-%m-%d') AS '订单支付日期',\n" +
            "SUM(yso.total_num) AS '数量',\n" +
            "s.`name` AS '姓名',\n" +
            "s.`code` AS '代码',\n" +
            "s.`organization` AS '医院名称' \n" +
            "FROM yx_store_order yso \n" +
            "LEFT JOIN staff s ON yso.referee_code = s.`code`\n" +
            "WHERE yso.paid = 1  AND yso.refund_status = 0 "  +
            "<if test =\"startTime !=null and startTime !=''\">and DATE_FORMAT(FROM_UNIXTIME(yso.pay_time),'%Y-%m-%d %H:%i:%S') &gt;= #{startTime}</if> " +
            "<if test =\"endTime !=null and endTime !=''\">and DATE_FORMAT(FROM_UNIXTIME(yso.pay_time),'%Y-%m-%d %H:%i:%S') &lt;= #{endTime}</if> " +
            "<if test =\"projectCode !=null and projectCode !=''\">and yso.project_code=#{projectCode}</if> " +
            " GROUP BY DATE_FORMAT(FROM_UNIXTIME(yso.pay_time),'%Y-%m-%d') ,s.`name`,s.`code`,s.`organization` </script> ")
    List<Map<String,Object>> findByPayTimeAndProject(@Param("startTime")String startTime, @Param("endTime")String endTime, @Param("projectCode")String projectCode);
}
