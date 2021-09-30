package co.yixiang.modules.shop.entity;

import java.math.BigDecimal;
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
 * 物流运费模板明细
 * </p>
 *
 * @author visa
 * @since 2020-11-28
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value="YxExpressTemplateDetail对象", description="物流运费模板明细")
public class YxExpressTemplateDetail extends BaseEntity {

    private static final long serialVersionUID = 1L;

@TableId(value = "id", type = IdType.AUTO)
private Integer id;

@ApiModelProperty(value = "模板id")
private Integer templateId;

@ApiModelProperty(value = "区域名称")
private String areaName;

@ApiModelProperty(value = "价格")
private BigDecimal price;

@ApiModelProperty(value = "记录生成时间")
private Date createTime;

@ApiModelProperty(value = "更新时间")
private Date updateTime;

@ApiModelProperty(value = "记录生成人")
private String creater;

@ApiModelProperty(value = "记录更新人")
private String maker;

    /** 区域级别，省份1，城市2 */
    @ApiModelProperty(value = "区域级别，省份1，城市2")
    private Integer level;

    /** 上级区域名称 */
    @ApiModelProperty(value = "上级区域名称")
    private String parentAreaName;
}
