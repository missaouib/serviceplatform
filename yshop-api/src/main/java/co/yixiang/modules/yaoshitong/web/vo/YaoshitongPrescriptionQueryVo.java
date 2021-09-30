package co.yixiang.modules.yaoshitong.web.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import java.io.Serializable;

import java.util.Date;

/**
 * <p>
 * 药师通-处方信息表 查询结果对象
 * </p>
 *
 * @author visa
 * @date 2020-07-17
 */
@Data
@ApiModel(value="YaoshitongPrescriptionQueryVo对象", description="药师通-处方信息表查询参数")
public class YaoshitongPrescriptionQueryVo implements Serializable{
    private static final long serialVersionUID = 1L;

@ApiModelProperty(value = "主键")
private Integer id;

@ApiModelProperty(value = "处方编号")
private String prescriptionNo;

@ApiModelProperty(value = "患者id")
private Integer patientId;

@ApiModelProperty(value = "药师id")
private String pharmacistId;

@ApiModelProperty(value = "是否处方药")
private String isPrescription;

@ApiModelProperty(value = "处方日期")
private Date prescriptionDate;

@ApiModelProperty(value = "处方医院")
private String hospitalName;

@ApiModelProperty(value = "处方医生")
private String doctorName;

@ApiModelProperty(value = "科室")
private String departName;

@ApiModelProperty(value = "诊断")
private String diagnosis;

@ApiModelProperty(value = "药品明细")
private String medDetail;

@ApiModelProperty(value = "生成时间")
private Date createTime;

@ApiModelProperty(value = "更新时间")
private Date updateTime;

@ApiModelProperty(value = "处方图片")
private String imagePath;

@ApiModelProperty(value = "患者姓名")
private String name;

@ApiModelProperty(value = "患者手机号")
private String phone;

@ApiModelProperty(value = "患者年龄")
private Integer age;

@ApiModelProperty(value = "地址")
private String address;

@ApiModelProperty(value = "性别")
private String sex;

}