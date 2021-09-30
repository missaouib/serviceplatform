package co.yixiang.modules.shop.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import co.yixiang.common.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * <p>
 * 病种
 * </p>
 *
 * @author visazhou
 * @since 2020-06-03
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value="YxStoreDisease对象", description="病种")
public class YxStoreDisease extends BaseEntity {

    private static final long serialVersionUID = 1L;

@ApiModelProperty(value = "商品分类表ID")
@TableId(value = "id", type = IdType.AUTO)
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
    @ApiModelProperty(value = "项目编码")
private String projectCode;
}
