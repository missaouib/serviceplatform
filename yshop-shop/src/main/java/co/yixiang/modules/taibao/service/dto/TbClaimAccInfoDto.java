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
public class TbClaimAccInfoDto implements Serializable {

    /** 主键 */
    private Long id;

    /** 出险日期 */
    private Date accDate;

    /** 初次就诊日期 */
    private Date firstDate;

    /** 出险地区  （1. 大陆地区 2. 港澳台 3. 境外不含港澳台 ） */
    private String accAddrType;

    /** 出险类型 （1意外，2疾病，3其他） */
    private String accSubtype;

    /** 出险经过 */
    private String accInfo;

    /** 索赔事故性质  （01 身故  02 伤残  03 重大疾病 04 门急诊医疗 05 住院医疗 06 住院补贴 07 女性生育），多个用逗号拼接 */
    private String claimacc;

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
