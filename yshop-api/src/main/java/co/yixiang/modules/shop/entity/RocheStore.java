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
 * 
 * </p>
 *
 * @author visa
 * @since 2020-12-28
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value="RocheStore对象", description="")
public class RocheStore extends BaseEntity {

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
    /** 类型（1：发药药房  2：服务药房） */
    private Integer type;

    /** 收款账户单位 */
    private String payeeAccountName;
    /** 收款银行名称 */
    private String payeeBankName;
    /** 收款银行账户 */
    private String payeeBankAccount;

}
