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
* @author visa
* @date 2020-06-02
*/
@Data
public class MdPharmacistServiceDto implements Serializable {

    /** 标识 */
    private String id;

    /** 状态(0-停用；1-启用) */
    private Long status;

    /** 是否在线(0-否；1-是) */
    private Long online;

    /** 是否默认(0-否；1-是) */
    private Long isDefalut;

    /** 来源(01-药房；02-医院) */
    private String source;

    /** 外键ID(如果是医院，则记录医院ID；如果是药房，则记录药房ID) */
    private String foreignId;

    /** 姓名 */
    private String name;

    /** 性别(0-女;1-男) */
    private Long sex;

    /** 药师执业证编号 */
    private String certificateNo;

    /** 药师简介 */
    private String description;

    /** 客服账号 */
    private String customerServiceAccount;

    /** 客服组 */
    private String customerServiceGroup;

    /** 备注 */
    private String remark;

    /** 创建人 */
    private String createUser;

    /** 创建时间 */
    private Timestamp createTime;

    /** 更新人 */
    private String updateUser;

    /** 更新时间 */
    private Timestamp updateTime;

    private String foreignName;

    @ApiModelProperty(value = "药师照片")
    private String imagePath;
    @ApiModelProperty(value = "药师手机号")
    private String phone;
    @ApiModelProperty(value = "出生年月")
    private String birth;

    private Integer uid;
}
