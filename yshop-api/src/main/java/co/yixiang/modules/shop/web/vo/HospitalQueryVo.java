package co.yixiang.modules.shop.web.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import java.io.Serializable;

import java.util.Date;

/**
 * <p>
 * 医院 查询结果对象
 * </p>
 *
 * @author visa
 * @date 2021-06-11
 */
@Data
@ApiModel(value="HospitalQueryVo对象", description="医院查询参数")
public class HospitalQueryVo implements Serializable{
    private static final long serialVersionUID = 1L;

@ApiModelProperty(value = "主键")
private Integer id;

@ApiModelProperty(value = "医院名称")
private String name;

@ApiModelProperty(value = "地址")
private String address;

@ApiModelProperty(value = "logo图片")
private String image;

@ApiModelProperty(value = "记录生成时间")
private Date createTime;

@ApiModelProperty(value = "记录更新时间")
private Date updateTime;

@ApiModelProperty(value = "站点信息")
private String siteInfo;

}