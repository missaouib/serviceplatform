/**
* Copyright (C) 2018-2020
* All rights reserved, Designed By www.yixiang.co
* 注意：
* 本软件为www.yixiang.co开发研制，未经购买不得使用
* 购买后可获得全部源代码（禁止转卖、分享、上传到码云、github等开源平台）
* 一经发现盗用、分享等行为，将追究法律责任，后果自负
*/
package co.yixiang.modules.shop.service.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.sql.Timestamp;
import java.io.Serializable;

/**
* @author visazhou
* @date 2020-12-28
*/
@Data
public class RocheStoreDto implements Serializable {

    private Integer id;

    /** 门店名称 */
    private String name;

    /** 简介 */
    private String introduction;

    /** 手机号码 */
    private String phone;

    /** 省市区 */
    private String address;

    /** 详细地址 */
    private String detailedAddress;

    /** 门店logo */
    private String image;

    /** 纬度 */
    private String latitude;

    /** 经度 */
    private String longitude;

    /** 核销有效日期 */
    private String validTime;

    /** 每日营业开关时间 */
    private String dayTime;

    /** 添加时间 */
    private Integer addTime;

    /** 是否显示 */
    private Integer isShow;

    /** 是否删除 */
    private Integer isDel;

    private Timestamp dayTimeEnd;

    private Timestamp dayTimeStart;

    private Timestamp validTimeEnd;

    private Timestamp validTimeStart;

    /** 在线客户平台的组号 */
    private String customerServiceGroup;

    private String yiyaobaoId;

    /** 省份code */
    private String provinceCode;

    /** 省份名称 */
    private String provinceName;

    /** 城市code */
    private String cityCode;

    /** 城市name */
    private String cityName;

    /** 轮播图 */
    private String sliderImage;

    private Timestamp createTime;

    private Timestamp updateTime;

    private String districtName;

    private String districtCode;

    /** 类型（1：发药药房  2：服务药房） */
    private String type;

    /** 收款账户单位 */
    private String payeeAccountName;
    /** 收款银行名称 */
    private String payeeBankName;
    /** 收款银行账户 */
    private String payeeBankAccount;
    @ApiModelProperty(value = "是否为益药保门店 0否 1是")
    private Integer isyiyaostoreid;
}
