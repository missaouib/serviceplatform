package co.yixiang.modules.hospitaldemand.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import co.yixiang.common.entity.BaseEntity;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * 互联网医院导入的需求单
 * </p>
 *
 * @author visa
 * @since 2020-12-04
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value="InternetHospitalDemand对象", description="互联网医院导入的需求单")
public class InternetHospitalDemand extends BaseEntity {

    private static final long serialVersionUID = 1L;

@TableId(value = "id", type = IdType.AUTO)
private Integer id;

@ApiModelProperty(value = "患者姓名")
private String patientName;

@ApiModelProperty(value = "患者手机号")
private String phone;

@ApiModelProperty(value = "uid")
private Integer uid;

@ApiModelProperty(value = "卡类型")
private String cardType;

@ApiModelProperty(value = "卡号")
private String cardNumber;

@ApiModelProperty(value = "原始订单号,太平传过来的订单号")
private String orderNumber;

@ApiModelProperty(value = "项目名称")
private String projectCode;

private String prescriptionPdf;

private String image;

@ApiModelProperty(value = "记录生成时间")
private Date createTime;

@ApiModelProperty(value = "记录更新时间")
private Date updateTime;

@ApiModelProperty(value = "处方时间 yyyyMMddHHmmss ")
private String timeCreate;

@ApiModelProperty(value = "处方编码")
private String prescriptionCode;

@ApiModelProperty(value = "患者身份证号")
@TableField(value = "patient_id_card")
private String patientIDCard;

@ApiModelProperty(value = "医院名称")
private String hospitalName;

@TableField(exist = false)
private List<InternetHospitalDemandDetail> drugs;

private String attrs;

private Integer isUse;

private String orderId;

private Integer payNoticeFlag;
private Date payNoticeDate;
private Integer refundNoticeFlag;
private Date refundNoticeDate;
private Integer expressNoticeFlag;
private Date expressNoticeDate;
private Integer signNoticeFlag;
private Date signNoticeDate;

}
