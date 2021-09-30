/**
* Copyright (C) 2018-2020
* All rights reserved, Designed By www.yixiang.co
* 注意：
* 本软件为www.yixiang.co开发研制，未经购买不得使用
* 购买后可获得全部源代码（禁止转卖、分享、上传到码云、github等开源平台）
* 一经发现盗用、分享等行为，将追究法律责任，后果自负
*/
package co.yixiang.modules.api.service.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.sql.Timestamp;
import java.io.Serializable;

/**
* @author visa
* @date 2020-11-30
*/
@Data
public class UserAgreementDto implements Serializable {

    /** 主键 */
    private Integer id;

    /** 用户id */
    private Integer uid;

    /** 患者姓名 */
    private String userName;

    /** 患者手机号 */
    private String userPhone;

    /** 签名请求ID */
    private String requestId;

    /** 签名ID */
    private String signFlowId;

    /** 签名的pdf地址 */
    private String signFilePath;

    /** 是否已经签名 0否 1是 */
    private Integer status;

    /** 记录生成时间 */
    private Timestamp createTime;

    /** 记录更新时间 */
    private Timestamp updateTime;

    @ApiModelProperty(value = "订单缓存key")
    private String orderKey;

    @ApiModelProperty(value = "订单编号")
    private String orderNo;
}
