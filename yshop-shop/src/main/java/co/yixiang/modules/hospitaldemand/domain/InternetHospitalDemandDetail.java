/**
* Copyright (C) 2018-2020
* All rights reserved, Designed By www.yixiang.co
* 注意：
* 本软件为www.yixiang.co开发研制，未经购买不得使用
* 购买后可获得全部源代码（禁止转卖、分享、上传到码云、github等开源平台）
* 一经发现盗用、分享等行为，将追究法律责任，后果自负
*/
package co.yixiang.modules.hospitaldemand.domain;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import com.baomidou.mybatisplus.annotation.TableName;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import javax.validation.constraints.*;
import java.sql.Timestamp;
import java.io.Serializable;

/**
* @author visazhou
* @date 2021-01-22
*/
@Data
@TableName("internet_hospital_demand_detail")
public class InternetHospitalDemandDetail implements Serializable {

    /** 主键 */
    @TableId
    private Integer id;


    /** 需求单id */
    private Integer demandId;


    /** 处方编号 */
    private String prescriptionCode;


    /** 药品编码 */
    private String drugCode;


    /** 药品名称 */
    private String drugName;


    /** 药品数量 */
    private Integer drugNum;


    /** 记录生成时间 */
    @TableField(fill= FieldFill.INSERT)
    private Timestamp createTime;


    /** 记录更新时间 */
    @TableField(fill= FieldFill.INSERT_UPDATE)
    private Timestamp updateTime;


    /** 药品唯一码 */
    private String productAttrUnique;


    /** 益药宝sku */
    private String yiyaobaoSku;

    @TableField(exist = false)
    private String storeName = "";

    @ApiModelProperty(value = "通用名")
    @TableField(exist = false)
    private String commonName = "";

    @ApiModelProperty(value = "生产厂家")
    @TableField(exist = false)
    private String manufacturer = "";

    @ApiModelProperty(value = "规格(如500ml)")
    @TableField(exist = false)
    private String spec = "";

    public void copy(InternetHospitalDemandDetail source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}
