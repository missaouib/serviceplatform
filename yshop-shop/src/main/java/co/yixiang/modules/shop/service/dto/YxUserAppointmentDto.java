/**
* Copyright (C) 2018-2020
* All rights reserved, Designed By www.yixiang.co
* 注意：
* 本软件为www.yixiang.co开发研制，未经购买不得使用
* 购买后可获得全部源代码（禁止转卖、分享、上传到码云、github等开源平台）
* 一经发现盗用、分享等行为，将追究法律责任，后果自负
*/
package co.yixiang.modules.shop.service.dto;

import lombok.Data;
import java.io.Serializable;

/**
* @author visa
* @date 2020-06-05
*/
@Data
public class YxUserAppointmentDto implements Serializable {

    /** 主键id */
    private Integer id;

    /** 活动id */
    private Integer eventId;

    /** 用户id */
    private Integer uid;

    /** 手机号 */
    private String mobile;

    /** 活动名称 */
    private String eventName;

    private Integer addTime;

    /** 状态，0/已预约 1/已取消 */
    private Integer status;

    /** 用户名称 */
    private String name;
}
