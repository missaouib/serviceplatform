package co.yixiang.modules.yaoshitong.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
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

/**
 * <p>
 * 药师通-处方信息表
 * </p>
 *
 * @author visa
 * @since 2020-07-17
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value="YaoshitongPrescription对象", description="药师通-处方信息表")
public class YaoshitongPrescription extends BaseEntity {

    private static final long serialVersionUID = 1L;

@ApiModelProperty(value = "主键")
@TableId(value = "id", type = IdType.AUTO)
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
@TableField(fill= FieldFill.INSERT)
private Date createTime;

@ApiModelProperty(value = "更新时间")
@TableField(fill= FieldFill.INSERT_UPDATE)
private Date updateTime;

@ApiModelProperty(value = "处方图片")
private String imagePath;


}
