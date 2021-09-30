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
import java.sql.Timestamp;
import java.io.Serializable;

/**
* @author visa
* @date 2021-03-04
*/
@Data
public class StaffDto implements Serializable {

    /** 主键 */
    private Integer id;

    /** 代码 */
    private String code;

    /** 名称 */
    private String name;

    /** 类型 */
    private String type;

    /** 机构 */
    private String organization;

    /** 记录生成时间 */
    private Timestamp createTime;

    /** 记录更新时间 */
    private Timestamp updateTime;

    /** 项目代码 */
    private String projectCode;

    private String depart;
}
