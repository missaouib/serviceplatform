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

/**
 * <p>
 * 互联网医院导入的需求单药品明细
 * </p>
 *
 * @author visa
 * @since 2020-12-04
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value="InternetHospitalDemandDetail对象", description="互联网医院导入的需求单药品明细")
public class InternetHospitalDemandDetail extends BaseEntity {

    private static final long serialVersionUID = 1L;

@ApiModelProperty(value = "主键")
@TableId(value = "id", type = IdType.AUTO)
private Integer id;

@ApiModelProperty(value = "需求单id")
private Integer demandId;

@ApiModelProperty(value = "处方编号")
private String prescriptionCode;

@ApiModelProperty(value = "药品编码")
private String drugCode;

@ApiModelProperty(value = "药品名称")
private String drugName;

@ApiModelProperty(value = "药品数量")
private Integer drugNum;

@ApiModelProperty(value = "记录生成时间")
private Date createTime;

@ApiModelProperty(value = "记录更新时间")
private Date updateTime;

@ApiModelProperty(value = "药品照片")
@TableField(exist = false)
private String drugImage;

@ApiModelProperty(value = "药品唯一码")
private String productAttrUnique;
    @ApiModelProperty(value = "益药宝sku")
private String yiyaobaoSku;

}
