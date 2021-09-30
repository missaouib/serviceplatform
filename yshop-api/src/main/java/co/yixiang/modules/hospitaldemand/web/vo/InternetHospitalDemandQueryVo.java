package co.yixiang.modules.hospitaldemand.web.vo;

import co.yixiang.modules.hospitaldemand.entity.InternetHospitalDemandDetail;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import java.io.Serializable;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * 互联网医院导入的需求单 查询结果对象
 * </p>
 *
 * @author visa
 * @date 2020-12-04
 */
@Data
@ApiModel(value="InternetHospitalDemandQueryVo对象", description="互联网医院导入的需求单查询参数")
public class InternetHospitalDemandQueryVo implements Serializable{
    private static final long serialVersionUID = 1L;

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

@ApiModelProperty(value = "原始订单号")
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
private String patientIDCard;

@ApiModelProperty(value = "医院名称")
private String hospitalName;

private List<InternetHospitalDemandDetail> drugs;

private String attrs;

}