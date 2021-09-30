/**
* Copyright (C) 2018-2020
* All rights reserved, Designed By www.yixiang.co
* 注意：
* 本软件为www.yixiang.co开发研制，未经购买不得使用
* 购买后可获得全部源代码（禁止转卖、分享、上传到码云、github等开源平台）
* 一经发现盗用、分享等行为，将追究法律责任，后果自负
*/
package co.yixiang.modules.yaolian.domain;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import com.baomidou.mybatisplus.annotation.TableName;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import javax.validation.constraints.*;
import java.sql.Timestamp;
import java.io.Serializable;

/**
* @author visa
* @date 2021-03-02
*/
@Data
@TableName("yaolian_order")
public class YaolianOrder implements Serializable {

    /** 交易流水号(即药联订单号) */
    @TableId(value = "id", type = IdType.INPUT)
    private String id;


    /** 药联下单时间 */

    private Timestamp createTime;


    /** 门店内码 */
    private String storeId;


    /** 会员ID（需要配合会员数据录入接口实现） */
    private String memberId;


    /** 店员手机号 */
    private String assistantMobile;


    /** 店员工号 */
    private String assistantNumber;


    /** 订单总价 */
    private String totalPrice;


    /** 药联直付金额 */
    private String freePrice;


    /** 顾客自付金额 */
    private String salePrice;


    /** 超级会员日订单标示 */
    private String issuper;


    /** 订单是否有处方单标示，1是存在处方单，0是没有 */
    private String isPrescription;


    /** 处方单流水号 */
    private String rxId;


    /** 益药宝订单id */
    private String yiyaobaoOrderId;


    /** 益药宝订单号 */
    private String yiyaobaoOrderNo;


    /** 是否已经下发至益药宝平台 0/否， 1/是 */
    private Integer uploadYiyaobaoFlag;


    /** 下发至益药宝平台的时间 */
    private Timestamp uploadYiyaobaoTime;

    /** 省份 */
    private String provinceName ;
    /** 城市 */
    private String cityName ;
    /** 区县 */
    private String districtName ;
    /** 地址 */
    private String address ;
    /** 收货人 */
    private String receiver ;
    /** 收货人电话 */
    private String receiverPhone ;
    /** 处方图片 */
    private String image ;
    /** 患者名称 */
    private String patientName;
    /** 患者电话 */
    private String patientPhone ;
    public void copy(YaolianOrder source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}
