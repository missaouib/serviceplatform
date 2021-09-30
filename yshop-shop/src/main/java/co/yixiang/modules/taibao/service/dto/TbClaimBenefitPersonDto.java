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

/**
* @author zhoujinlai
* @date 2021-04-30
*/
@Data
public class TbClaimBenefitPersonDto implements Serializable {

    private Long id;

    /** 赔案信息Id */
    private String claimInfoId;

    /** 领款人类型（1个人、2单位） */
    private String bftype;

    /** 与被保人关系 */
    private String relationship;

    /** 证件类型 */
    private String idtype;

    /** 证件号码 */
    private String idno;

    /** 证件有效期起期 */
    private String idBegdate;

    /** 证件有效期止期 */
    private String idEnddate;

    /** 领款人姓名 */
    private String name;

    /** 性别 */
    private String sex;

    /** 出生日期 */
    private String birthdate;

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

    /** 支付方式 （1现金2支票3转账） */
    private String settype;

    /** 银行名称（银行类别）提供枚举值 */
    private String banktype;

    /** 开户行 */
    private String banksubtype;

    /** 分行 */
    private String bankbranch;

    /** 支行 */
    private String banksubbranch;

    /** 银行所在省 */
    private String provinceofbank;

    /** 银行所在市 */
    private String cityofbank;

    /** 银行账号 */
    private String acctno;

    /** 所属部门 */
    private String sysOrgCode;

    /** 更新日期 */
    private Timestamp updateTime;

    /** 更新人 */
    private String updateBy;

    /** 创建日期 */
    private Timestamp createTime;

    /** 创建人 */
    private String createBy;

    private Boolean delFlag;

}
