/**
* Copyright (C) 2018-2020
* All rights reserved, Designed By www.yixiang.co
* 注意：
* 本软件为www.yixiang.co开发研制，未经购买不得使用
* 购买后可获得全部源代码（禁止转卖、分享、上传到码云、github等开源平台）
* 一经发现盗用、分享等行为，将追究法律责任，后果自负
*/
package co.yixiang.modules.taibao.domain;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

/**
* @author zhoujinlai
* @date 2021-04-30
*/
@Data
@TableName("tb_claim_info")
public class TbClaimInfo implements Serializable {

    /** id */
    @TableId
    private Long id;


    /** 创建人 */
    private String createBy;


    /** 创建日期 */
    @TableField(fill= FieldFill.INSERT)
    private Timestamp createTime;


    /** 更新人 */
    private String updateBy;


    /** 更新日期 */
    @TableField(fill= FieldFill.INSERT_UPDATE)
    private Timestamp updateTime;


    /** 所属部门 */
    private String sysOrgCode;


    /** 报案号 */
    private String reportno;

    /** 批次号 */
    private String batchno;


    /** 赔案号 */
    private String claimno;


    /** 收单单位代码 */
    private String custmco;


    /** 快递签收时间 */
    private String exptime;


    /** 医保号 */
    private String medicalCode;


    /** 是否接受电子邮件 */
    private String emailAccept;


    /** 收单时间 */
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    private Date visitDate;

    /**资料收齐时间*/
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date advanceClosingTime;

    /**垫付结案时间*/
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date dataCollectionDay;

    /** 复核意见 */
    private String reauditoption;


    /** 复核完成时间 */
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    private Date reauditdate;


    /** 挂起类型(多种类型用逗号拼接) */
    private String hangupsign;


    /** 赔案层结论 */
    private String claimrescode;


    /** 审核意见 */
    private String auditoption;


    /** 删除标识 */
    @TableLogic
    @TableField(fill=FieldFill.INSERT_UPDATE)
    private Boolean delFlag;


    /** 订单编号 */
    private Long orderId;

    /** 图片附件 */
    private String imgUrl;

    /** pdf附件 */
    private String pdfUrl;

    /** 赔案状态  0未完结 1：已完结 */
    private String status;

    public void copy(TbClaimInfo source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}
