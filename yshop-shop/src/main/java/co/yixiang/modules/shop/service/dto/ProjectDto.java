/**
* Copyright (C) 2018-2020
* All rights reserved, Designed By www.yixiang.co
* 注意：
* 本软件为www.yixiang.co开发研制，未经购买不得使用
* 购买后可获得全部源代码（禁止转卖、分享、上传到码云、github等开源平台）
* 一经发现盗用、分享等行为，将追究法律责任，后果自负
*/
package co.yixiang.modules.shop.service.dto;

import co.yixiang.modules.shop.domain.YxSystemStore;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.sql.Timestamp;
import java.io.Serializable;
import java.util.List;

/**
* @author visa
* @date 2021-02-25
*/
@Data
public class ProjectDto implements Serializable {

    private Integer id;

    /** 项目编码 */
    private String projectCode;

    /** 项目名称 */
    private String projectName;

    /** 项目简介 */
    private String projectDesc;

    /** 项目备注 */
    private String remark;

    /** 项目联系电话 */
    private String phone;

    /** 在线咨询客服组id */
    private String serviceGroupId;

    /** 益药宝项目代码 */
    private String yiyaobaoProjectCode;

    /** 记录生成时间 */
    private Timestamp createTime;

    /** 记录更新时间 */
    private Timestamp updateTime;
    /** 支付方式 */
    private String payType;

    /** 满多少金额包邮，单位元 */
    private Integer freePostage;
    /** 物流运费模板id */
    private String expressTemplateId;

    private List<String> expressTemplateIdList;

    private String expressTemplateName;

    private String mchName;

    private String flag;

    private String companyId;

    private String storeIds;

    private String siteInfo;

    private String guangzhouFlag;

    private List<YxSystemStore> storeList;

    @ApiModelProperty(value = " 是否开启互联网医院获取处方 Y/N")
    private String needInternetHospital;

    /** 支付宝h5AppID */
    private String alipayHfiveAppid;

    /** 支付宝小程序AppID */
    private String alipayAppletAppid;

    @ApiModelProperty(value = "微信h5Mchid")
    private String wechatHfiveMchid;

    @ApiModelProperty(value = "微信小程序Mchid")
    private String wechatAppletMchid;

    @ApiModelProperty(value = "微信app Mchid")
    private String wechatAppMchid;

    @ApiModelProperty(value = "微信众安 Mchid")
    private String wechatZhonganMchid;

}
