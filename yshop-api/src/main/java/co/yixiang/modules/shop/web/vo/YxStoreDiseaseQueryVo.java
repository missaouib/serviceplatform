package co.yixiang.modules.shop.web.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.io.Serializable;

import java.util.Date;

/**
 * <p>
 * 病种 查询结果对象
 * </p>
 *
 * @author visazhou
 * @date 2020-06-03
 */
@Data
@ApiModel(value="YxStoreDiseaseQueryVo对象", description="病种查询参数")
public class YxStoreDiseaseQueryVo implements Serializable{
    private static final long serialVersionUID = 1L;

@ApiModelProperty(value = "商品分类表ID")
private Integer id;

@ApiModelProperty(value = "父id")
private Integer pid;

@ApiModelProperty(value = "病种名称")
private String cateName;

@ApiModelProperty(value = "排序")
private Integer sort;

@ApiModelProperty(value = "图标")
private String pic;

@ApiModelProperty(value = "是否推荐")
private Boolean isShow;

@ApiModelProperty(value = "添加时间")
private Integer addTime;

@ApiModelProperty(value = "删除状态")
private Boolean isDel;

}