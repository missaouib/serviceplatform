package co.yixiang.modules.shop.web.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import java.io.Serializable;

import java.util.Date;

/**
 * <p>
 * 生产厂家主数据表 查询结果对象
 * </p>
 *
 * @author visa
 * @date 2020-12-07
 */
@Data
@ApiModel(value="ManufacturerQueryVo对象", description="生产厂家主数据表查询参数")
public class ManufacturerQueryVo implements Serializable{
    private static final long serialVersionUID = 1L;

@ApiModelProperty(value = "主键")
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