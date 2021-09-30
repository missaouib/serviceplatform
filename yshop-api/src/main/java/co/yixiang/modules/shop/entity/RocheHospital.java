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
 * 罗氏罕见病sma医院列表
 * </p>
 *
 * @author visa
 * @since 2021-02-05
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value="RocheHospital对象", description="罗氏罕见病sma医院列表")
public class RocheHospital extends BaseEntity {

    private static final long serialVersionUID = 1L;

@ApiModelProperty(value = "主键")
@TableId(value = "id", type = IdType.AUTO)
private Integer id;

@ApiModelProperty(value = "医院名称")
private String name;

@ApiModelProperty(value = "省份名称")
private String provinceName;

@ApiModelProperty(value = "城市名称")
private String cityName;

@ApiModelProperty(value = "记录生成时间")
private Date createTime;

@ApiModelProperty(value = "记录更新时间")
private Date updateTime;

@ApiModelProperty(value = "状态 有效/无效")
private String status;

}
