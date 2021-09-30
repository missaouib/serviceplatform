/**
* Copyright (C) 2018-2020
* All rights reserved, Designed By www.yixiang.co
* 注意：
* 本软件为www.yixiang.co开发研制，未经购买不得使用
* 购买后可获得全部源代码（禁止转卖、分享、上传到码云、github等开源平台）
* 一经发现盗用、分享等行为，将追究法律责任，后果自负
*/
package co.yixiang.mp.yiyaobao.service.mapper;

import co.yixiang.common.mapper.CoreMapper;
import co.yixiang.mp.yiyaobao.domain.OrdOrder;
import co.yixiang.mp.yiyaobao.domain.SkuSellerPriceStock;
import co.yixiang.mp.yiyaobao.domain.YiyaobaoMed;
import co.yixiang.mp.yiyaobao.param.OrderQueryParam;
import co.yixiang.mp.yiyaobao.service.dto.YiyaobaoOrderInfo;
import co.yixiang.mp.yiyaobao.vo.OrderDetailVo;
import co.yixiang.mp.yiyaobao.vo.OrderPartInfoVo;
import co.yixiang.mp.yiyaobao.vo.OrderVo;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
* @author visa
* @date 2020-06-28
*/
@Repository
@Mapper
public interface OrdOrderMapper extends CoreMapper<OrdOrder> {

    @Select("SELECT o.ID as id, \n" +
            "   o.ORDER_NO AS orderNo,\n" +
            "  o.ORDER_TIME AS orderDate,\n" +
            "  usr.DISPLAY_NAME\tas name,\n" +
            " usr.MOBILE\tas mobile,\n" +
            "  (SELECT t.ITEM_CNAME from iplatv5.tedcm01 t where t.CODESET_CODE ='b2c.orderStatus' and t.ITEM_CODE = o.STATUS) as status,\n" +
            "  o.TOTAL_AMOUNT AS totalAmount,\n" +
            "  o.ACTUAL_AMOUNT AS discountTotalAmount,\n" +
            " (SELECT t.ITEM_CNAME from iplatv5.tedcm01 t where t.CODESET_CODE ='b2c.orderSource' and t.ITEM_CODE = o.ORDER_SOURCE) as channelName,\n" +
            "  ppt.SALE_HOSPITAL_NAME AS hospitalName,\n" +
            "  ppt.DOCTOR_NAME AS doctorName,\n" +
            " IFNULL(ms.SELLER_NAME,'') as storeName\n" +
            " ,IFNULL(ml.NAME,'') as logisticsName\n" +
            ",o.FREIGHT_NO AS freightNo\n" +
            ",ppt.ID as prescriptionTempId,\n" +
            "  ppt.IMAGE_ID AS imageId\n" +
            "  ppt.PARTNER_ID AS partnerId,\n" +
            "  ppt.PRESCRIP_NO AS prescripNo\n" +
            " FROM yiyao_b2c.ord_order o\n" +
            " inner JOIN yiyao_user.usr_user usr ON usr.ID = o.USER_ID\n" +
            " LEFT JOIN yiyao_meta.md_seller ms ON ms.id = o.SELLER_ID\n" +
            " LEFT JOIN yiyao_meta.md_logistics ml ON ml.id = o.LOGISTICS_ID\n" +
            " JOIN yiyao_prs.prs_prescription_temp ppt on ppt.ORDER_ID = o.ID\n" +
            " WHERE o.IS_DELETE = 0 AND o.ORDER_TYPE != 50 AND o.ACTUAL_AMOUNT >0 AND usr.MOBILE = #{mobile}  ORDER BY o.ORDER_TIME desc")
    public List<OrderVo> getYiyaobaoOrderByMobile(@Param("mobile") String mobile);

    @Select("SELECT pid.FILE_PATH as imagePath FROM yiyao_prs.prs_image ci,yiyao_prs.prs_image_detail pid \n" +
            " WHERE ci.ID = pid.IMAGE_ID\n" +
            "  and ci.id = #{imageId} LIMIT 1")
    String getImagePath(@Param("imageId") String imageId);

    @Select("SELECT ood.id as id,ood.sku as sku, ood.MED_NAME AS productName,ood.SPEC AS spec,ood.AMOUNT AS qty,\n" +
            "  ood.UNIT_PRICE AS unitPrice,ood.TOTAL_AMOUNT AS totalAmount,\n" +
            "  ood.DISCOUNT AS discountRate,ood.DISCOUNT_AMOUNT AS discountPrice,ood.ACTUAL_AMOUNT  AS discountTotalAmount \n" +
            "  FROM yiyao_b2c.ord_order_detail ood WHERE ood.ORDER_ID = #{orderId}")
    List<OrderDetailVo> getOrderDetail(@Param("orderId") String orderId);


    /**
     * 获取分页对象
     * @param page
     * @param orderQueryParam
     * @return
     */
    @Select("SELECT o.ID as id, \n" +
            "   o.ORDER_NO AS orderNo,\n" +
            "  o.ORDER_TIME AS orderDate,\n" +
            "  usr.DISPLAY_NAME\tas name,\n" +
            " usr.MOBILE\tas mobile,\n" +
            "  (SELECT t.ITEM_CNAME from iplatv5.tedcm01 t where t.CODESET_CODE ='b2c.orderStatus' and t.ITEM_CODE = o.STATUS) as status,\n" +
            "  o.TOTAL_AMOUNT AS totalAmount,\n" +
            "  o.ACTUAL_AMOUNT AS discountTotalAmount,\n" +
            " (SELECT t.ITEM_CNAME from iplatv5.tedcm01 t where t.CODESET_CODE ='b2c.orderSource' and t.ITEM_CODE = o.ORDER_SOURCE) as channelName,\n" +
            "  ppt.SALE_HOSPITAL_NAME AS hospitalName,\n" +
            "  ppt.DOCTOR_NAME AS doctorName,\n" +
            " IFNULL(ms.SELLER_NAME,'') as storeName\n" +
            " ,IFNULL(ml.NAME,'') as logisticsName\n" +
            ",o.FREIGHT_NO AS freightNo\n" +
            ",ppt.ID as prescriptionTempId,\n" +
            "  ppt.IMAGE_ID AS imageId,\n" +
            "  ppt.PARTNER_ID AS partnerId,\n" +
            "  ppt.PRESCRIP_NO AS prescripNo,\n" +
            " mp.PARTNER_CODE AS partnerCode,mp.PRIVATE_KEY AS privateKey \n" +
            " FROM yiyao_b2c.ord_order o\n" +
            " inner JOIN yiyao_user.usr_user usr ON usr.ID = o.USER_ID\n" +
            " LEFT JOIN yiyao_meta.md_seller ms ON ms.id = o.SELLER_ID\n" +
            " LEFT JOIN yiyao_meta.md_logistics ml ON ml.id = o.LOGISTICS_ID\n" +
            " JOIN yiyao_prs.prs_prescription_temp ppt on ppt.ORDER_ID = o.ID\n" +
            " LEFT JOIN yiyao_meta.md_partner mp ON mp.id = o.PARTNER_ID \n" +
            " WHERE o.IS_DELETE = 0 AND o.ORDER_TYPE != 50 AND o.ACTUAL_AMOUNT >0 AND usr.MOBILE = #{orderQueryParam.mobile} ")
    IPage<OrderVo> getOrderPageList(@Param("page") Page page, @Param("orderQueryParam") OrderQueryParam orderQueryParam);

    @DS("multi-datasource1")
    @Select("SELECT ORDER_NO AS  orderNo,ORDER_TIME AS orderDate, oo.STATUS as status, oo.PAY_TIME as payTime FROM yiyao_b2c.ord_order oo WHERE oo.ORDER_NO = #{orderNo}")
    OrderPartInfoVo getOrderPartInfoByOrderNo(@Param("orderNo") String orderNo);

    @DS("multi-datasource1")
    @Select(" SELECT oo.ID FROM yiyao_b2c.ord_order oo WHERE oo.ORDER_NO = #{orderNo}")
    String getOrderIdByNo(@Param("orderNo") String orderNo);

    @DS("multi-datasource1")
    @Update("UPDATE yiyao_prs.prs_prescription SET IS_DELETE = 1 WHERE ORDER_ID = #{orderId} ")
    void updatePrescription(@Param("orderId") String orderId);

    @DS("multi-datasource1")
    @Update("UPDATE yiyao_prs.prs_prescription_app SET IS_DELETE = 1 WHERE ORDER_ID = #{orderId} ")
    void updatePrescriptionApp(@Param("orderId") String orderId);

    @DS("multi-datasource1")
    @Update("UPDATE yiyao_b2c.ord_order SET status = 94 WHERE ID = #{orderId} ")
    void updateOrderStatus(@Param("orderId") String orderId);

    /**
     * 获取分页对象
     * @param page
     * @param orderQueryParam
     * @return
     */

    IPage<OrderVo> getOrderPageList_2(@Param("page") Page page, @Param("orderQueryParam") OrderQueryParam orderQueryParam);

    @DS("multi-datasource1")
    @Select("\n" +
            "SELECT o.ID as id, \n" +
            "               o.ORDER_NO AS orderNo,\n" +
            "              o.ORDER_TIME AS orderDate,\n" +
            "              usr.DISPLAY_NAME as name,\n" +
            "             usr.MOBILE as mobile,\n" +
            "              (SELECT t.ITEM_CNAME from iplatv5.tedcm01 t where t.CODESET_CODE ='b2c.orderStatus' and t.ITEM_CODE = o.STATUS) as status,\n" +
            " o.status as statusCode,\n"+
            "              o.TOTAL_AMOUNT AS totalAmount,\n" +
            "              o.ACTUAL_AMOUNT AS discountTotalAmount,\n" +
            "             (SELECT t.ITEM_CNAME from iplatv5.tedcm01 t where t.CODESET_CODE ='b2c.orderSource' and t.ITEM_CODE = o.ORDER_SOURCE) as channelName,\n" +
            "             IFNULL(ms.SELLER_NAME,'') as storeName\n" +
            "             ,IFNULL(ml.NAME,'') as logisticsName\n" +
            "            ,o.FREIGHT_NO AS freightNo,\n" +
            "             mp.PARTNER_CODE AS partnerCode,mp.PRIVATE_KEY AS privateKey,o.RECEIVER as receiveName,o.MOBILE as receiveMobile,o.FULL_ADDRESS as address,o.CONTACT_MOBILE as factUserPhone,o.PAY_TIME as payTime,o.PAY_RESULT as payResult \n" +
            "             FROM yiyao_b2c.ord_order o\n" +
            "             inner JOIN yiyao_user.usr_user usr ON usr.ID = o.USER_ID\n" +
            "             LEFT JOIN yiyao_meta.md_seller ms ON ms.id = o.SELLER_ID\n" +
            "             LEFT JOIN yiyao_meta.md_logistics ml ON ml.id = o.LOGISTICS_ID\n" +
            "             LEFT JOIN yiyao_meta.md_partner mp ON mp.id = o.PARTNER_ID \n" +
            "             WHERE o.IS_DELETE = 0  \n" +
            "             AND o.ORDER_TYPE != 50 \n" +
            "             AND o.ACTUAL_AMOUNT >0 \n" +
            "             AND o.ORDER_NO = #{orderId}  ")
    OrderVo getYiyaobaoOrderbyOrderId(@Param("orderId") String orderId);


    @DS("multi-datasource1")
    @Select("SELECT  CONCAT('http://www.yiyaogo.com/yyadmin' , cid.FILE_PATH) AS filePath  \n" +
            "FROM yiyao_b2c.cmd_commodity cc JOIN yiyao_b2c.cmd_image_detail cid \n" +
            "ON cc.IMAGE_ID = cid.IMAGE_ID WHERE sku = #{sku}  AND cid.FILE_PATH != '' \n" +
            "ORDER BY cid.CREATE_TIME DESC LIMIT 1 ")
    String getMedicineImageBySku(@Param("sku") String sku);

    @DS("multi-datasource1")
    @Update("update yiyao_b2c.ord_order set STATUS = '50' where ORDER_NO = #{orderNo}")
    void takeOrder(@Param("orderNo") String orderNo);

    @DS("multi-datasource1")
    @Update("UPDATE yiyao_system.sys_validate_code SET IS_Valid = '0' WHERE MOBILE = #{mobile} AND IS_Valid ='1' ")
    void updateVerifyCodeInvalid(@Param("mobile") String mobile);

    @DS("multi-datasource1")
    @Update("delete FROM yiyao_system.sys_validate_code  WHERE MOBILE = #{mobile}  ")
    void deleteVerifyCode(@Param("mobile") String mobile);

    @DS("multi-datasource1")
    @Insert("INSERT INTO yiyao_system.sys_validate_code (ID, NICK_NAME, EMAIL, MOBILE, VALIDATE_CODE, VALIDATE_CODE_TYPE, IS_Valid, MAC, SEND_RESULT, RESULT_CODE, RESULT_MSG, EXPIRE_TIME, SESSION_ID, CREATE_USER, CREATE_TIME, UPDATE_USER, UPDATE_TIME)\n" +
            "  VALUES (UUID(), '', '', #{mobile}, #{verifyCode}, '0', '1', '', 0, '', '', ADDDATE(NOW(),INTERVAL 1 HOUR), '', '', NOW(), '', NOW())")
    void insertVerifyCode(@Param("mobile") String mobile,@Param("verifyCode") String verifyCode);

    @DS("multi-datasource1")
    @Update(" UPDATE yiyao_b2c.ord_order oo set oo.ORDER_NO = #{targetOrderNo} WHERE oo.ORDER_NO = #{sourceOrderNo} ")
    Boolean changeOrderNo(@Param("sourceOrderNo") String sourceOrderNo,@Param("targetOrderNo") String targetOrderNo);

    @DS("multi-datasource1")
    @Select(" select id from yiyao_b2c.ord_order where ORDER_NO = #{orderNo} limit 1 ")
    String queryYiyaobaoOrderId(@Param("orderNo") String orderNo);

    @DS("multi-datasource1")
    @Update("UPDATE yiyao_prs.prs_prescription_app a, yiyao_b2c.ord_order oo " +
            "  SET a.CHECK_STATUS = 1,a.CHECK_USER = 'sunweijuan',a.CHECK_TIME = NOW(),a.CHECK_DESC = '通过' ," +
            "  oo.STATUS = 20,oo.PAY_RESULT = '10' " +
            "  WHERE a.ORDER_ID = oo.ID " +
            "  AND oo.ORDER_NO = #{orderNo}")
    Boolean checkPassPrescription(@Param("orderNo") String orderNo);

    @DS("multi-datasource1")
    @Update("UPDATE yiyao_b2c.ord_order SET STATUS = '43',LOGISTICS_ID = 1,FREIGHT_NO= #{FREIGHT_NO} WHERE ORDER_NO = #{orderNo}")
    Boolean updateYiyaobaoExpress(@Param("FREIGHT_NO") String FREIGHT_NO,@Param("orderNo") String orderNo);


    @DS("multi-datasource1")
    @Update("UPDATE yiyao_b2c.ord_order oo SET oo.RECEIVER = #{receiverName},oo.MOBILE = #{receiverPhone},oo.CONTACT_MOBILE = #{factUserPhone} WHERE oo.ORDER_NO = #{OrderNo}")
    Boolean changeOrderReceiver(@Param("OrderNo")  String OrderNo,@Param("receiverName")  String receiverName,@Param("receiverPhone")String receiverPhone,@Param("factUserPhone")String factUserPhone);


    @DS("multi-datasource1")
    @Select("SELECT\n" +
            "  o.ID AS id,\n" +
            "  o.ORDER_NO AS orderNo,\n" +
            "  o.ORDER_TIME AS orderDate,\n" +
            "  (SELECT\n" +
            "      t.ITEM_CNAME\n" +
            "    FROM iplatv5.tedcm01 t\n" +
            "    WHERE t.CODESET_CODE = 'b2c.orderStatus'\n" +
            "    AND t.ITEM_CODE = o.STATUS) AS status,\n" +
            "  o.status AS statusCode,\n" +
            "  IFNULL(ml.NAME, '') AS logisticsName,\n" +
            "  o.FREIGHT_NO AS freightNo,\n" +
            "  o.PAY_TIME AS payTime,\n" +
            "  o.PAY_RESULT AS payResult\n" +
            "FROM yiyao_b2c.ord_order o\n" +
            "  LEFT JOIN yiyao_meta.md_logistics ml\n" +
            "    ON ml.id = o.LOGISTICS_ID\n" +
            "WHERE o.ORDER_NO = #{orderId}")
    OrderVo getYiyaobaoOrderbyOrderIdSample(@Param("orderId") String orderId);

    @DS("multi-datasource1")
    @Select("SELECT\n" +
            "  o.ID AS id,\n" +
            "  o.ORDER_NO AS orderNo,\n" +
            "  o.ORDER_TIME AS orderDate,\n" +
            "  (SELECT\n" +
            "      t.ITEM_CNAME\n" +
            "    FROM iplatv5.tedcm01 t\n" +
            "    WHERE t.CODESET_CODE = 'b2c.orderStatus'\n" +
            "    AND t.ITEM_CODE = o.STATUS) AS status,\n" +
            "  o.status AS statusCode,\n" +
            "  IFNULL(ml.NAME, '') AS logisticsName,\n" +
            "  o.FREIGHT_NO AS freightNo,\n" +
            "  o.PAY_TIME AS payTime,\n" +
            "  o.PAY_RESULT AS payResult\n" +
            "FROM yiyao_b2c.ord_order o\n" +
            "  LEFT JOIN yiyao_meta.md_logistics ml\n" +
            "    ON ml.id = o.LOGISTICS_ID\n" +
            "WHERE o.id = #{orderId}")
    OrderVo getYiyaobaoOrderbyYiyaobaoId(@Param("orderId") String orderId);


    @DS("multi-datasource1")
    @Update("UPDATE\n" +
            "\tyiyao_prs.prs_prescription_temp ppt\n" +
            "JOIN yiyao_b2c.ord_order oo ON ppt.ORDER_ID = oo.ID\n" +
            "SET oo.ORDER_SOURCE = #{orderSource}\n" +
            "WHERE ppt.PRESCRIP_NO=#{prsNo}")
    void updateYiyaobaoOrderSourceByPrescripNo(@Param("prsNo") String prsNo, @Param("orderSource") String orderSource);


    @DS("multi-datasource1")
    @Update("UPDATE\n" +
            "\tyiyao_prs.prs_prescription_temp ppt\n" +
            "JOIN yiyao_b2c.ord_order oo ON ppt.ORDER_ID = oo.ID\n" +
            "SET oo.ORDER_SOURCE = #{orderInfo.orderSource},oo.PAY_TIME = #{orderInfo.payTime},oo.PAY_RESULT = #{orderInfo.payResult}, oo.PAY_METHOD = #{orderInfo.payMethod},oo.PAY_TYPE = #{orderInfo.payType}\n" +
            "WHERE ppt.PRESCRIP_NO=#{orderInfo.prsNo}")
    void updateYiyaobaoOrderInfoByPrescripNo(@Param("orderInfo") YiyaobaoOrderInfo orderInfo);


    @DS("multi-datasource1")
    @Update("UPDATE yiyao_b2c.ord_order oo set oo.ORDER_SOURCE = #{orderSource} WHERE oo.ORDER_NO = #{orderNo}")
    void updateYiyaobaoOrderSourceByOrderNo(@Param("orderNo") String orderNo, @Param("orderSource") String orderSource);


    @DS("multi-datasource1")
    @Update("UPDATE yiyao_b2c.ord_order oo set oo.ORDER_SOURCE = #{orderInfo.orderSource},oo.PAY_TIME = #{orderInfo.payTime},oo.PAY_RESULT = #{orderInfo.payResult}, oo.PAY_METHOD = #{orderInfo.payMethod},oo.PAY_TYPE = #{orderInfo.payType} WHERE oo.ORDER_NO = #{orderInfo.orderNo}")
    void updateYiyaobaoOrderInfoByOrderNo(@Param("orderInfo") YiyaobaoOrderInfo orderInfo);


    @DS("multi-datasource1")
    @Update("UPDATE\n" +
            "\tyiyao_b2c.ord_order oo SET oo.ORDER_SOURCE = #{orderSource}\n" +
            "WHERE oo.id=#{orderId}")
    void updateOrderSourceByOrderno(@Param("orderId") String orderId, @Param("orderSource") String orderSource);

    @Insert("INSERT INTO yiyao_meta.med_project(ID, MANUFACTURER, PROJECT_NO, POJECT_NAME, PROJECT_DESC, STATUS, START_TIME, END_TIME, IMAGE, IS_APPLY, CREATE_USER, CREATE_TIME, UPDATE_USER, UPDATE_TIME) VALUES\n" +
            "(#{projectId}, #{MANUFACTURER}, #{PROJECT_NO}, #{POJECT_NAME}, #{PROJECT_DESC}, '', now(), '2025-12-22 15:58:37', '', 0, '', now(), '', now())")
    boolean saveProject(@Param("projectId") String projectId,@Param("MANUFACTURER") String MANUFACTURER,@Param("PROJECT_NO") String PROJECT_NO,@Param("POJECT_NAME") String POJECT_NAME,@Param("PROJECT_DESC") String PROJECT_DESC);

    @Insert("INSERT INTO yiyao_meta.med_project_attr(ID, PROJECT_ID, ATTR_KEY, ATTR_VALUE, EXT_VALUE, EXT_VALUE2, ATTR_DESC, ORDER_NO, CREATE_TIME, CREATE_USER, UPDATE_USER, UPDATE_TIME) VALUES\n" +
            "(UUID(), #{projectId}, 'MESSAGE_PAY_SUCCESS', '【益药】您已支付成功，订单号：%s。详情可查询“益药商城”微信小程序，我们将为您提供更多、更好的服务！', ' ', ' ', '支付成功短信提醒用户内容', 0, '2020-02-21 10:35:56', 'admin', 'admin', '2020-02-21 10:35:56'),\n" +
            "(UUID(), #{projectId}, 'MESSAGE_PRESCRIPT_LINK', 'https://wxtest.yiyaogo.com/pay/wxPsPay.html?orderId=%s', ' ', ' ', '用户支付预订链接地址', 0, '2020-02-17 21:53:57', 'admin', 'admin', '2020-02-17 21:53:57'),\n" +
            "(UUID(), #{projectId}, 'MESSAGE_VERIFY_CODE', '【益药】尊敬的%s,您的验证码为：%s（30分钟有效，如非本人操作，请忽略）。', ' ', ' ', '验证码短信推送内容', 0, '2020-02-17 21:53:57', 'admin', 'admin', '2020-02-17 21:53:57'),\n" +
            "(UUID(), #{projectId}, 'MESSAGE_SUBIMIT_PRESCRIPT', '【益药】您有新的处方需要审核，请及时处理！', '123456', ' ', '用户提交处方短信推送内容', 0, '2020-02-17 21:53:57', 'admin', 'admin', '2020-02-17 21:53:57'),\n" +
            "(UUID(), #{projectId}, 'MESSAGE_PAY_NOTIFY', '【益药】客户%s已完成支付,订单号：%s，请您及时发货！', ' ', ' ', '支付成功短信提醒内容', 0, '2020-02-17 21:53:57', 'admin', 'admin', '2020-02-17 21:53:57')")
    boolean saveProjectAttr(@Param("projectId") String projectId);

    @Select("SELECT COUNT(1) AS existsCount FROM yiyao_meta.med_project mp WHERE mp.PROJECT_NO = #{projectNo}")
    int countProject(@Param("projectNo") String projectNo);



    @DS("multi-datasource1")
    @Select(" SELECT pp.ORDER_ID FROM yiyao_prs.prs_prescription pp WHERE   pp.PRESCRIP_NO = #{prescriptionNO} limit 1 ")
    String queryYiyaobaoOrderIdByPrescription(@Param("prescriptionNO") String prescriptionNO);

    @DS("multi-datasource1")
    @Select("SELECT ITEM_CODE FROM iplatv5.tedcm01 WHERE CODESET_CODE = 'b2c.orderSource' AND ITEM_CNAME = #{projectName} limit 1 ")
    String queryOrderSourceCode(String projectName);


    @DS("multi-datasource1")
    @Select(" SELECT oo.ID FROM yiyao_b2c.ord_order oo WHERE oo.ORDER_NO = #{orderNO} limit 1 ")
    String queryYiyaobaoOrderIdByOrderNo(@Param("orderNO") String orderNO);

    @DS("multi-datasource1")
    @Select(" SELECT oo.ORDER_SOURCE FROM yiyao_b2c.ord_order oo WHERE oo.ID = #{orderId} ")
    String queryOrderSourceByOrderId(@Param("orderId") String orderId);

    @DS("multi-datasource1")
    IPage<YiyaobaoMed> queryYiyaobaoPartnerMed( @Param("page") Page page,   @Param("partnerId") String partnerId, @Param("lastupdate") Date lastupdate);

    @DS("multi-datasource1")
    IPage<YiyaobaoMed> queryYiyaobaoMedImages(@Param("page") Page page,@Param("lastupdate") Date lastupdate);

    @DS("multi-datasource1")
    IPage<YiyaobaoMed> queryYiyaobaoStoreMed(@Param("page") Page page,@Param("lastupdate") Date lastupdate);

    @DS("multi-datasource1")
    @Select("SELECT cp.SKU AS sku, ms.id AS sellerId,ms.SELLER_NAME AS sellerName,cp.UNIT_PRICE AS price FROM yiyao_b2c.cmd_price cp,yiyao_meta.md_seller ms \n" +
            "  WHERE  ms.status = 1 AND ms.IS_SELF_SUPPORT = 1\n" +
            " and LENGTH(TRIM( IFNULL(cp.SELLER_ID,''))) = 0 AND cp.PROVINCE_CODE = ms.PROVINCE_CODE AND cp.CITY_CODE = ms.CITY_CODE\n" +
            "AND ( cp.CREATE_TIME >= #{lastupdate} OR cp.UPDATE_TIME >= #{lastupdate} )")
    List<SkuSellerPriceStock> queryYiyaobaoMedPriceByCity(@Param("lastupdate") Date lastupdate);

    @DS("multi-datasource1")
    @Select("SELECT cp.SKU AS sku, ms.id AS sellerId,ms.SELLER_NAME AS sellerName,cp.UNIT_PRICE AS price FROM yiyao_b2c.cmd_price cp,yiyao_meta.md_seller ms \n" +
            "  WHERE ms.status = 1 AND ms.IS_SELF_SUPPORT = 1\n" +
            "  AND ms.id = cp.SELLER_ID\n" +
            "  AND ( cp.CREATE_TIME >= #{lastupdate} OR cp.UPDATE_TIME >= #{lastupdate} )")
    List<SkuSellerPriceStock> queryYiyaobaoMedPriceBySeller(@Param("lastupdate") Date lastupdate);


    @DS("multi-datasource1")
    @Select("SELECT csde.SKU AS sku,csde.SELLER_ID AS sellerId,sum(csde.USABLE_AMOUNT) AS stock FROM cmd_stock_detail_ebs csde GROUP BY csde.SKU,csde.SELLER_ID order by ID ")
    IPage<SkuSellerPriceStock> queryYiyaobaoMedStock(@Param("page") Page page);


    @DS("multi-datasource1")
    YiyaobaoMed queryYiyaobaoMedImagesBySku(@Param("sku") String sku);
}
