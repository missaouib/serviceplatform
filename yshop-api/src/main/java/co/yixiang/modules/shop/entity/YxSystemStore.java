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
 * 门店自提
 * </p>
 *
 * @author hupeng
 * @since 2020-03-04
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "YxSystemStore对象", description = "门店自提")
public class YxSystemStore extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
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
    private Integer isShow;

    @ApiModelProperty(value = "是否删除")
    private Integer isDel;

    private Date dayTimeEnd;

    private Date dayTimeStart;

    private Date validTimeEnd;

    private Date validTimeStart;

    private String customerServiceGroup;

    private String yiyaobaoId;

    private String sliderImage;

    private String cityName;
    private String provinceName;

    /*药店是否支持在线支付 1 表示支持*/
    private String supportOnlinePayFlag;

    private String mchName;

    /*药店是否支持在线支付 支付方式*/
    private String payType;

    private String linkPhone;
    /** 支付宝h5AppID */
    private String alipayHfiveAppid;

    /** 支付宝小程序AppID */
    private String alipayAppletAppid;

    @ApiModelProperty(value = "微信h5 Mchid")
    private String wechatHfiveMchid;

    @ApiModelProperty(value = "微信小程序 Mchid")
    private String wechatAppletMchid;

    @ApiModelProperty(value = "微信app Mchid")
    private String wechatAppMchid;

    @ApiModelProperty(value = "微信众安 Mchid")
    private String wechatZhonganMchid;

}
