package co.yixiang.modules.shop.web.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import java.io.Serializable;

import java.util.Date;

/**
 * <p>
 *  查询结果对象
 * </p>
 *
 * @author visa
 * @date 2020-12-28
 */
@Data
@ApiModel(value="RocheStoreQueryVo对象", description="查询参数")
public class RocheStoreQueryVo implements Serializable{
    private static final long serialVersionUID = 1L;

private Integer id;

@ApiModelProperty(value = "门店名称")
private String name;

@ApiModelProperty(value = "简介")
private String introduction;

@ApiModelProperty(value = "手机号码")
private String phone;

@ApiModelProperty(value = "省市区")
private String address;

@ApiModelProperty(value = "详细地址")
private String detailedAddress;

@ApiModelProperty(value = "门店logo")
private String image;

@ApiModelProperty(value = "纬度")
private String latitude;

@ApiModelProperty(value = "经度")
private String longitude;

@ApiModelProperty(value = "核销有效日期")
private String validTime;

@ApiModelProperty(value = "每日营业开关时间")
private String dayTime;

@ApiModelProperty(value = "添加时间")
private Integer addTime;

@ApiModelProperty(value = "是否显示")
private Boolean isShow;

@ApiModelProperty(value = "是否删除")
private Boolean isDel;

private Date dayTimeEnd;

private Date dayTimeStart;

private Date validTimeEnd;

private Date validTimeStart;

@ApiModelProperty(value = "在线客户平台的组号")
private String customerServiceGroup;

private String yiyaobaoId;

@ApiModelProperty(value = "省份code")
private String provinceCode;

@ApiModelProperty(value = "省份名称")
private String provinceName;

@ApiModelProperty(value = "城市code")
private String cityCode;

@ApiModelProperty(value = "城市name")
private String cityName;

@ApiModelProperty(value = "轮播图")
private String sliderImage;

}