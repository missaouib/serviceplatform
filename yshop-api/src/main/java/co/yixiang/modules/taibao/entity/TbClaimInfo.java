/**
* Copyright (C) 2018-2020
* All rights reserved, Designed By www.yixiang.co
* 注意：
* 本软件为www.yixiang.co开发研制，未经购买不得使用
* 购买后可获得全部源代码（禁止转卖、分享、上传到码云、github等开源平台）
* 一经发现盗用、分享等行为，将追究法律责任，后果自负
*/
package co.yixiang.modules.taibao.entity;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import co.yixiang.common.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

/**
* @author zhoujinlai
* @date 2021-04-30
*/
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value="保单", description="保单")
public class TbClaimInfo extends BaseEntity {

    /** id */
    @ApiModelProperty(value = "id")
    @TableField("id")
    private Long id;

    /** 创建人 */
    @ApiModelProperty(value = "创建人")
    @TableField("create_by")
    private String createBy;

    /** 创建日期 */
    @ApiModelProperty(value = "创建日期")
    @TableField("create_time")
    private Date createTime;


    /** 更新人 */
    @ApiModelProperty(value = "更新人")
    @TableField("update_by")
    private String updateBy;

    /** 更新日期 */
    @ApiModelProperty(value = "更新日期")
    @TableField("update_time")
    private Date updateTime;


    /** 所属部门 */
    @ApiModelProperty(value = "所属部门")
    @TableField("sys_org_code")
    private String sysOrgCode;


    /** 报案号 */
    @ApiModelProperty(value = "报案号")
    @TableField("batchno")
    private String batchno;


    /** 赔案号 */
    @ApiModelProperty(value = "赔案号")
    @TableField("claimno")
    private String claimno;


    /** 收单单位代码 */
    @ApiModelProperty(value = "收单单位代码")
    @TableField("custmco")
    private String custmco;


    /** 快递签收时间 */
    @ApiModelProperty(value = "快递签收时间")
    @TableField("exptime")
    private String exptime;


    /** 医保号 */
    @ApiModelProperty(value = "医保号")
    @TableField("medical_code")
    private String medicalCode;


    /** 是否接受电子邮件 */
    @ApiModelProperty(value = "是否接受电子邮件")
    @TableField("email_accept")
    private String emailAccept;


    /** 收单时间 */
    @ApiModelProperty(value = "收单时间")
    @TableField("visit_date")
    private Date visitDate;


    /** 复核意见 */
    @ApiModelProperty(value = "复核意见")
    @TableField("reauditoption")
    private String reauditoption;


    /** 复核完成时间 */
    @ApiModelProperty(value = "复核完成时间")
    @TableField("reauditdate")
    private Date reauditdate;


    /** 挂起类型(多种类型用逗号拼接) */
    @ApiModelProperty(value = "挂起类型(多种类型用逗号拼接) ")
    @TableField("hangupsign")
    private String hangupsign;


    /** 赔案层结论 */
    @ApiModelProperty(value = "赔案层结论")
    @TableField("claimrescode")
    private String claimrescode;


    /** 审核意见 */
    @ApiModelProperty(value = "审核意见")
    @TableField("auditoption")
    private String auditoption;


    /** 删除标识 */
    @ApiModelProperty(value = "删除标识")
    @TableField("del_flag")
    private Boolean delFlag;


    /** 订单编号 */
    @ApiModelProperty(value = "订单编号")
    @TableField("order_id")
    private Long orderId;

    /** 图片附件 */
    @ApiModelProperty(value = "图片附件")
    @TableField("img_url")
    private String imgUrl;

    /** pdf附件 */
    @ApiModelProperty(value = "pdf附件")
    @TableField("pdf_url")
    private String pdfUrl;

    /** 赔案状态  0未完结 1：已完结 */
    @ApiModelProperty(value = "赔案状态  0未完结 1：已完结")
    @TableField("status")
    private String status;

}
