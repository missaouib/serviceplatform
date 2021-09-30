package co.yixiang.modules.hospitaldemand.web.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import java.io.Serializable;

import java.util.Date;

/**
 * <p>
 * 互联网医院导入的需求单药品明细 查询结果对象
 * </p>
 *
 * @author visa
 * @date 2020-12-04
 */
@Data
@ApiModel(value="InternetHospitalDemandDetailQueryVo对象", description="互联网医院导入的需求单药品明细查询参数")
public class InternetHospitalDemandDetailQueryVo implements Serializable{
    private static final long serialVersionUID = 1L;

@ApiModelProperty(value = "主键")
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

}