/**
* Copyright (C) 2018-2020
* All rights reserved, Designed By www.yixiang.co
* 注意：
* 本软件为www.yixiang.co开发研制，未经购买不得使用
* 购买后可获得全部源代码（禁止转卖、分享、上传到码云、github等开源平台）
* 一经发现盗用、分享等行为，将追究法律责任，后果自负
*/
package co.yixiang.modules.msh.domain;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import com.baomidou.mybatisplus.annotation.TableName;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import javax.validation.constraints.*;
import java.sql.Timestamp;
import java.math.BigDecimal;
import java.io.Serializable;

/**
* @author cq
* @date 2020-12-24
*/
@Data
@TableName("msh_repurchase_reminder")
public class MshRepurchaseReminder implements Serializable {

    @TableId
    private Integer id;


    /** 姓名 */
    private String name;


    /** 电话 */
    private String phone;


    /** 药房名称 */
    private String drugstoreName;


    /** 药房id */
    private Integer drugstoreId;


    /** 上次购买日期 */
    private Timestamp lastPurchaseDate;


    /** 下次购买日期 */
    private Timestamp nextPurchaseDate;


    /** 药品名称 */
    private String medName;


    /** 药品id */
    private Integer medId;


    /** 药品sku编码 */
    private String medSku;


    /** 药品通用名 */
    private String medCommonName;


    /** 药品规格 */
    private String medSpec;


    /** 药品单位 */
    private String medUnit;


    /** 药品生产厂家 */
    private String medManufacturer;


    /** 状态 */
    private String status;


    /** 首次购药日期 */
    private Timestamp firstPurchaseDate;


    /** 购药次数 */
    private Integer purchaseTimes;


    /** 总计购药数量 */
    private Integer purchaseQty;


    /** 上次购药数量 */
    private Integer lastPurchasseQty;


    /** 用药周期 */
    private Integer medCycle;


    /** 创建时间 */
    @TableField(fill= FieldFill.INSERT)
    private Timestamp createTime;


    /** 更新时间 */
    @TableField(fill= FieldFill.INSERT_UPDATE)
    private Timestamp updateTime;


    /** 药品图片 */
    private String image;


    /** 益药宝药房id */
    private String drugstoreYiyaobaoId;


    /** 益药宝用户id */
    private String userYiyaobaoId;


    /** 单价 */
    private BigDecimal unitPrice;


    /** 是否已购买 */
    private String repurchaseFlag;


    /** 没购买的原因 */
    private String repurchaseNoReason;


    /** 没购买的原因备注信息 */
    private String repurchaseNoReasonRemark;


    /** 购买方式 */
    private String repurchaseYesMethod;


    /** 省份 */
    private String provinceName;


    /** 城市 */
    private String cityName;


    /** 区县 */
    private String districtName;


    /** 收货地址 */
    private String address;


    /** 收货人 */
    private String receiver;


    /** 收货人电话 */
    private String receiverMobile;

    private String memberId;

    public void copy(MshRepurchaseReminder source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}
