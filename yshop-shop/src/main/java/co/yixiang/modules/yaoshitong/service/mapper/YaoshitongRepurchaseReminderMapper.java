/**
* Copyright (C) 2018-2020
* All rights reserved, Designed By www.yixiang.co
* 注意：
* 本软件为www.yixiang.co开发研制，未经购买不得使用
* 购买后可获得全部源代码（禁止转卖、分享、上传到码云、github等开源平台）
* 一经发现盗用、分享等行为，将追究法律责任，后果自负
*/
package co.yixiang.modules.yaoshitong.service.mapper;

import co.yixiang.common.mapper.CoreMapper;
import co.yixiang.modules.yaoshitong.domain.YaoshitongRepurchaseReminder;
import co.yixiang.modules.yaoshitong.service.dto.MedSales4DrugstoreDto;
import co.yixiang.modules.yaoshitong.service.dto.MedCycleNoticeDto;
import co.yixiang.modules.yaoshitong.service.dto.SalesInfoDto;
import com.baomidou.dynamic.datasource.annotation.DS;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

/**
* @author visa
* @date 2020-10-21
*/
@Repository
@Mapper
public interface YaoshitongRepurchaseReminderMapper extends CoreMapper<YaoshitongRepurchaseReminder> {

    @DS("multi-datasource1")
    @Select("SELECT oo.USER_ID AS userId,oo.SELLER_ID AS sellerId,MAX(oo.ORDER_TIME) AS lastOrderDate,MIN(oo.ORDER_TIME) AS firstOrderDate,SUM(ood.AMOUNT) AS ttlQty,COUNT(DISTINCT oo.id) AS purchaseTimes,MAX(ood.UNIT_PRICE) AS unitPrice\n" +
            "  FROM yiyao_b2c.ord_order oo, ord_order_detail ood ,yiyao_meta.md_seller ms\n" +
            "  WHERE  oo.ID = ood.ORDER_ID \n" +
            "  AND ood.SKU = #{sku}\n" +
            "  AND oo.SELLER_ID = ms.ID\n" +
            "  AND ms.IS_SELF_SUPPORT = '1'\n" +
            "  AND oo.USER_ID != ''\n" +
            " AND oo.ORDER_TIME >= DATE_SUB(NOW(),INTERVAL 2 MONTH)\n" +
            "  GROUP BY oo.USER_ID,oo.SELLER_ID")
     List<MedSales4DrugstoreDto> queryMedSales4Drugstore(@Param("sku") String sku);

     @DS("multi-datasource1")
     @Select("SELECT  SUM(ood.AMOUNT) AS qty,oo.PROVINCE_NAME AS provinceName,oo.CITY_NAME AS cityName,oo.DISTRICT_NAME AS districtName,oo.ADDRESS AS address,oo.RECEIVER AS receiver,oo.MOBILE AS mobile from yiyao_b2c.ord_order oo, ord_order_detail ood \n" +
             "  WHERE  oo.ID = ood.ORDER_ID \n" +
             "  AND ood.SKU = #{sku}\n" +
             "  AND oo.SELLER_ID = #{sellerId}\n" +
             "  AND oo.USER_ID = #{userId}\n" +
             "  AND oo.ORDER_TIME = #{orderDate}")
     SalesInfoDto queryMedLastSales(@Param("sku") String sku, @Param("sellerId") String sellerId, @Param("userId") String userId, @Param("orderDate") Timestamp orderDate);

    @DS("multi-datasource1")
    @Select("SELECT uu.MOBILE as mobile,uu.DISPLAY_NAME as name FROM yiyao_user.usr_user uu WHERE uu.ID = #{userId}")
    Map<String,String> queryUserInfoById(@Param("userId") String userId);

    @Select("SELECT drugstore_name AS drugstoreName,COUNT(1) AS amount,drugstore_id AS drugstoreId  FROM yaoshitong_repurchase_reminder WHERE status = '否' GROUP BY drugstore_name")
    List<MedCycleNoticeDto> queryMedCycleNotice();
}
