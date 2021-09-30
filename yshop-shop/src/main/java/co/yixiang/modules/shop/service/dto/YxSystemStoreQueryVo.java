package co.yixiang.modules.shop.service.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * <p>
 * 门店自提 查询结果对象
 * </p>
 *
 * @author hupeng
 * @date 2020-03-04
 */
@Data
@ApiModel(value = "YxSystemStoreQueryVo对象", description = "门店自提查询参数")
public class YxSystemStoreQueryVo implements Serializable {
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

    private String latlng;

    public String getLatlng(){
        return latitude+","+longitude;
    }

    @ApiModelProperty(value = "每日营业开关时间")
    private String dayTime;

    @ApiModelProperty(value = "添加时间")
    private Integer addTime;

    private Date dayTimeEnd;

    private Date dayTimeStart;

    private Date validTimeEnd;

    private Date validTimeStart;

    private String distance;

    private BigDecimal price;

    private Integer stock;

    private String unique;

    private BigDecimal innerPrice;

    private Integer isInner;

    private String customerServiceGroup;

    private String sliderImage;

    private String mchName;

    private String linkPhone;

    /** 支付宝h5AppID */
    private String alipayHfiveAppid;

    /** 支付宝小程序AppID */
    private String alipayAppletAppid;

    @ApiModelProperty(value = "微信h5 Mchid")
    private String wechatHfiveMchid;

    @ApiModelProperty(value = "微信小程序Mchid")
    private String wechatAppletMchid;

    @ApiModelProperty(value = "微信app Mchid")
    private String wechatAppMchid;

    @ApiModelProperty(value = "微信众安 Mchid")
    private String wechatZhonganMchid;

}