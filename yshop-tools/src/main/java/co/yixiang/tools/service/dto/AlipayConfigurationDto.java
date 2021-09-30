/**
* Copyright (C) 2018-2020
* All rights reserved, Designed By www.yixiang.co
* 注意：
* 本软件为www.yixiang.co开发研制，未经购买不得使用
* 购买后可获得全部源代码（禁止转卖、分享、上传到码云、github等开源平台）
* 一经发现盗用、分享等行为，将追究法律责任，后果自负
*/
package co.yixiang.tools.service.dto;

import lombok.Data;

import java.io.Serializable;
import java.sql.Timestamp;

/**
* @author zhoujinlai
* @date 2021-09-01
*/
@Data
public class AlipayConfigurationDto implements Serializable {

    private Integer id;

    /** 1：小程序 2：H5 */
    private String type;
    /** 1:默认*/
    private Integer isDefault;
    /** 应用名称 */
    private String name;

    /** 支付宝appid */
    private String appId;

    private String format;

    private String charset;

    private String signType;

    /** 支付宝网关 */
    private String serverUrl;

    /** 应用网关 ，回调接口 */
    private String notifyUrl;

    /** 授权回调地址 */
    private String returnUrl;

    /** 私钥 */
    private String privateKey;

    /** 支付宝应用公钥 */
    private String publicKey;

    /** 创建时间 */
    private Timestamp createTime;

    /** 创建人 */
    private String createUser;

    /** 更新时间 */
    private Timestamp updateTime;

    /** 更新人 */
    private String updateUser;

    /** 删除表示  1：已删除  0 未删除 */
    private Integer deleteFlag;
}
