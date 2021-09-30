package co.yixiang.modules.shop.web.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import java.io.Serializable;

import java.util.Date;

/**
 * <p>
 * 罗氏罕见病sma医院列表 查询结果对象
 * </p>
 *
 * @author visa
 * @date 2021-02-05
 */
@Data
@ApiModel(value="RocheHospitalQueryVo对象", description="罗氏罕见病sma医院列表查询参数")
public class RocheHospitalQueryVo implements Serializable{
    private static final long serialVersionUID = 1L;

@ApiModelProperty(value = "主键")
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