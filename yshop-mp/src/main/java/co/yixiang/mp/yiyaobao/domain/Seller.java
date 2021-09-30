package co.yixiang.mp.yiyaobao.domain;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import javax.validation.constraints.NotBlank;

@Data
@Slf4j
public class Seller {
    /** 门店名称 */
    @ApiModelProperty(value = "门店名称")
    private String name;

    /** 简介 */
    @ApiModelProperty(value = "简介")
    private String introduction;

    /** 手机号码 */
    @ApiModelProperty(value = "手机号码")
    private String phone;

    /** 省市区 */
    @ApiModelProperty(value = "地址")
    private String address;

    /** 详细地址 */
    @ApiModelProperty(value = "详细地址")
    private String detailedAddress;

    /** 门店logo */
    @ApiModelProperty(value = "门店logo")
    private String image;

    /** 纬度 */
    @ApiModelProperty(value = "纬度")
    private String latitude;

    /** 经度 */
    @ApiModelProperty(value = "经度")
    private String longitude;

    /** 是否显示 */
    private Integer isShow;

    /** 是否删除 */
    private Integer isDel;

    private String yiyaobaoId;
}
