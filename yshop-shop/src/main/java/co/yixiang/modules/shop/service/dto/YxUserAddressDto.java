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
import java.io.Serializable;

/**
* @author visa
* @date 2020-10-15
*/
@Data
public class YxUserAddressDto implements Serializable {

    /** 用户地址id */
    private Integer id;

    /** 用户id */
    private Integer uid;

    /** 收货人姓名 */
    private String realName;

    /** 收货人电话 */
    private String phone;

    /** 收货人所在省 */
    private String province;

    /** 收货人所在市 */
    private String city;

    /** 收货人所在区 */
    private String district;

    /** 收货人详细地址 */
    private String detail;

    /** 邮编 */
    private String postCode;

    /** 经度 */
    private String longitude;

    /** 纬度 */
    private String latitude;

    /** 是否默认 */
    private Integer isDefault;

    /** 是否删除 */
    private Integer isDel;

    /** 添加时间 */
    private Integer addTime;


    /** 省份 code */
    private String provinceCode;

    /** 城市 code*/
    private String cityCode;

    /** 区县 code*/
    private String districtCode;

}
