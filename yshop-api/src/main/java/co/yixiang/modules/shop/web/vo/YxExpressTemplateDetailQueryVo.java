package co.yixiang.modules.shop.web.vo;

import co.yixiang.modules.shop.entity.YxExpressTemplateDetail;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import java.io.Serializable;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 物流运费模板明细 查询结果对象
 * </p>
 *
 * @author visa
 * @date 2020-11-28
 */
@Data
@ApiModel(value="YxExpressTemplateDetailQueryVo对象", description="物流运费模板明细查询参数")
public class YxExpressTemplateDetailQueryVo implements Serializable{
    private static final long serialVersionUID = 1L;

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