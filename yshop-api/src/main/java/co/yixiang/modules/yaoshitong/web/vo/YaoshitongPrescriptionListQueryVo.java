package co.yixiang.modules.yaoshitong.web.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
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
@ApiModel(value="YaoshitongPrescriptionQueryVo对象", description="药师通-处方信息列表查询参数")
public class YaoshitongPrescriptionListQueryVo implements Serializable{
    private static final long serialVersionUID = 1L;

@ApiModelProperty(value = "主键")
private Integer id;

@ApiModelProperty(value = "处方编号")
private String prescriptionNo;

@ApiModelProperty(value = "处方日期")
private Date prescriptionDate;

@ApiModelProperty(value = "处方医院")
private String hospitalName;

@ApiModelProperty(value = "处方图片")
private String imagePath;

@ApiModelProperty(value = "患者姓名")
private String name;

@ApiModelProperty(value = "患者手机号")
private String phone;

@ApiModelProperty(value = "患者年龄")
private Integer age;
@ApiModelProperty(value = "患者Id")
private Integer patientId;

}