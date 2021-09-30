/**
 * Copyright (C) 2018-2020
 * All rights reserved, Designed By www.yixiang.co

 */
package co.yixiang.modules.shop.domain;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.Date;

/**
* @author hupeng
* @date 2020-05-12
*/

@Data
@TableName("yx_system_store")
public class YxSystemStore implements Serializable {

    @TableId
    private Integer id;


    /** 门店名称 */
    @NotBlank(message = "请填写门店名称")
    private String name;


    /** 简介 */

    private String introduction;


    /** 手机号码 */

    private String phone;


    /** 省市区 */
    @NotBlank(message = "请填地址")
    private String address;


    /** 详细地址 */
    private String detailedAddress;


    /** 门店logo */
    @NotBlank(message = "请上传门店logo")
    private String image;


    /** 纬度 */
    @NotBlank(message = "请输入纬度")
    private String latitude;


    /** 经度 */
    @NotBlank(message = "请输入经度")
    private String longitude;


    /** 核销有效日期 */

    private String validTime;


    /** 每日营业开关时间 */

    private String dayTime;


    /** 添加时间 */
    @TableField(fill= FieldFill.INSERT)
    private Integer addTime;


    /** 是否显示 */
    private Integer isShow;


    /** 是否删除 */
    private Integer isDel;

    private Date validTimeEnd;

    private Date validTimeStart;

    private Date dayTimeStart;

    private Date dayTimeEnd;

    private String customerServiceGroup;

    private String yiyaobaoId;

    public void copy(YxSystemStore source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }

    private String sliderImage;
    private String provinceName;
    private String cityName;

    private String shortName;

    private String mchName;

    private String linkPhone;

    /** 支付宝h5AppID */
    private String alipayHfiveAppid;

    /** 支付宝小程序AppID */
    private String alipayAppletAppid;

    @ApiModelProperty(value = "微信h5Mchid")
    private String wechatHfiveMchid;

    @ApiModelProperty(value = "微信小程序Mchid")
    private String wechatAppletMchid;

    @ApiModelProperty(value = "微信app Mchid")
    private String wechatAppMchid;

    @ApiModelProperty(value = "微信众安 Mchid")
    private String wechatZhonganMchid;


}
