package co.yixiang.modules.shop.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import co.yixiang.common.entity.BaseEntity;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * <p>
 * 生产厂家主数据表
 * </p>
 *
 * @author visa
 * @since 2020-12-07
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value="Manufacturer对象", description="生产厂家主数据表")
public class Manufacturer extends BaseEntity {

    private static final long serialVersionUID = 1L;

@ApiModelProperty(value = "主键")
@TableId(value = "id", type = IdType.AUTO)
private Integer id;

@ApiModelProperty(value = "厂家名称")
private String name;

@ApiModelProperty(value = "图片")
private String image;

private Date createTime;

private Date updateTime;

@ApiModelProperty(value = "是否显示 1 显示 0隐藏")
private Integer isShow;

@ApiModelProperty(value = "是否删除 0 否，1是")
private Integer isDel;

@ApiModelProperty(value = "描述")
private String profile;

}
