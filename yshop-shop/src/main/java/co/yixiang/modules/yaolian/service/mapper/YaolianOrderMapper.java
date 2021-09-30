/**
* Copyright (C) 2018-2020
* All rights reserved, Designed By www.yixiang.co
* 注意：
* 本软件为www.yixiang.co开发研制，未经购买不得使用
* 购买后可获得全部源代码（禁止转卖、分享、上传到码云、github等开源平台）
* 一经发现盗用、分享等行为，将追究法律责任，后果自负
*/
package co.yixiang.modules.yaolian.service.mapper;

import co.yixiang.common.mapper.CoreMapper;
import co.yixiang.modules.yaolian.domain.YaolianOrder;
import co.yixiang.modules.yaolian.service.dto.Drug;
import com.baomidou.dynamic.datasource.annotation.DS;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
* @author visa
* @date 2021-03-02
*/
@Repository
@Mapper
public interface YaolianOrderMapper extends CoreMapper<YaolianOrder> {

    @DS("master")
    @Select("SELECT\n" +
            "\tysp.id AS id,\n" +
            "\tysp.create_time AS createTime,\n" +
            "\tysp.update_time AS updateTime,\n" +
            "\tysp.store_name AS name,\n" +
            "\tysp.common_name AS commonName,\n" +
            "\tysp.license_number AS number,\n" +
            "\tysp.spec AS form,\n" +
            "\tifnull(p.unit_price,yspv.price) AS price,\n" +
            "\tyspv.store_id AS groupId,\n" +
            "\tysp.unit AS pack,\n" +
            "\tysp.manufacturer AS manufacturer,\n" +
            "\tyspv.stock AS quantity,\n" +
            "\t case when p.is_del = 0 and p.is_show = 1 then 0 else 1 end AS status,\n" +
            "\tysp.type AS type,\n" +
            "\tysp.bar_code as barCode, \n" +
            " p.ignore_stock as ignoreStock  " +
            "FROM\n" +
            "\tyx_store_product ysp\n" +
            "JOIN yx_store_product_attr_value yspv ON ysp.id = yspv.product_id" +
            " join product4project p on p.product_id = ysp.id and p.project_no = #{projectCode} AND yspv.`unique` = p.product_unique_id" )
    List<Drug> pushMedInfos(@Param("projectCode")String projectCode);

    @DS("multi-datasource1")
    @Select("SELECT\n" +
            "\tool.SKU AS sku,\n" +
            "\tool.AMOUNT AS amount,\n" +
            "\tool.LOT_NO AS lotNo,\n" +
            "\tool.PERIOD AS period\n" +
            "\t\n" +
            "FROM\n" +
            "\tyiyao_b2c.ord_order oo\n" +
            "JOIN yiyao_b2c.ord_order_lot ool ON oo.ID = ool.ORDER_ID\n" +
            "WHERE\n" +
            "\too.ORDER_NO = #{orderNo}")
    List<Map> pushOrderIfo(@Param("orderNo")String orderNo);

    @DS("master")
    @Select("SELECT\n" +
            "\tyo.order_id AS orderId,\n" +
            "\tyo.create_time AS createTime,\n" +
            "\tyo.store_id AS storeId,\n" +
            "\tyo.total_price AS totalPrice,\n" +
            "\tyo.id AS id,\n" +
            "\tyod.drug_id AS drugId,\n" +
            "\tyxp.license_number AS number,\n" +
            "\tyxp.common_name AS commonName,\n" +
            "\tyxp.spec AS form,\n" +
            "\tyxp.unit AS pack,\n" +
            "\tyxp.price AS price,\n" +
            "\tyxp.ot_price AS unitPrice,\n" +
            "\tyxp.yiyaobao_sku AS sku\n" +
            "FROM\n" +
            "\tyaolian_order yo\n" +
            "JOIN yaolian_order_detail yod ON yo.id = yod.order_id\n" +
            "JOIN yx_store_product yxp ON yxp.id = yod.drug_id\n" +
            "WHERE yo.order_id=#{orderNo}")
    List<Map> getYaolianOrder(@Param("orderNo")String orderNo);
}
