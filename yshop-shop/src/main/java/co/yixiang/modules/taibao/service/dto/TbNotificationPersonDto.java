/**
* Copyright (C) 2018-2020
* All rights reserved, Designed By www.yixiang.co
* 注意：
* 本软件为www.yixiang.co开发研制，未经购买不得使用
* 购买后可获得全部源代码（禁止转卖、分享、上传到码云、github等开源平台）
* 一经发现盗用、分享等行为，将追究法律责任，后果自负
*/
package co.yixiang.modules.taibao.service.dto;

import lombok.Data;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

/**
* @author zhoujinlai
* @date 2021-04-30
*/
@Data
public class TbNotificationPersonDto implements Serializable {

    /** id */
    private Long id;

    /** 与被保人关系 */
    private String relationship;

    /** 报案日期 */
    private Date noticeDate;

    /** 证件类别 */
    private String idtype;

    /** 证件号码 */
    private String idno;

    /** 证件有效起期 */
    private Date idBegdate;

    /** 证件有效止期 */
    private Date idEnddate;

    /** 姓名 */
    private String name;

    /** 性别 */
    private String sex;

    /** 移动电话 */
    private String mobilephone;

    /** 固定电话 */
    private String telephone;

    /** 邮箱地址 */
    private String email;

    /** 联系地址 */
    private String addr;

    /** 邮政编码 */
    private String zip;

    /** 创建人 */
    private String createBy;

    /** 创建时间 */
    private Timestamp createTime;

    /** 修改人 */
    private String updateBy;

    /** 修改时间 */
    private Timestamp updateTime;

    /** 0表示未删除,1表示删除 */
    private Boolean delFlag;

    /** 赔案信息Id */
    private String claimInfoId;
}
