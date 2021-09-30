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
* @date 2021-04-09
*/
@Data
public class ProjectSalesAreaDto implements Serializable {

    /** 主键 */
    private Integer id;

    /** 项目代码 */
    private String projectCode;

    /** 省份名称 */
    private String areaName;

    /** 免邮金额 */
    private Integer freePostage;

    /** 记录生成时间 */
    private Timestamp createTime;

    /** 记录更新时间 */
    private Timestamp updateTime;

    private Integer isFree;
}
