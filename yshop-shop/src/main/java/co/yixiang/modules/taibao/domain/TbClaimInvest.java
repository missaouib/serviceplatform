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
@TableName("tb_claim_invest")
public class TbClaimInvest implements Serializable {

    @TableId
    private Long id;


    /** 赔案信息Id */
    private Long claimInfoId;


    /** 任务性质（即时调查|常规调查|复杂疑难调查|反欺诈调查） */
    private String kind;


    /** 任务子类型（疾病死亡|意外死亡|重大疾病|疾病医疗|意外医疗|残疾失能） */
    private String investtype;


    /** 调查方式（现场勘查|走访调查|询问调查|住院及费用核实|住院排查|住院补贴监控|追踪调查|综合调查） */
    private String subway;


    /** 调查要求 */
    private String demand;


    /** 调查结果 */
    private String result;


    /** 申请日期 */
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    private Date applydate;


    /** 反馈日期 */
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    private Date backdate;


    /** 调查员 */
    private String emp;


    /** 创建人 */
    private String createBy;


    /** 创建时间 */
    @TableField(fill= FieldFill.INSERT)
    private Timestamp createTime;


    /** 修改人 */
    private String updateBy;


    /** 修改时间 */
    @TableField(fill= FieldFill.INSERT_UPDATE)
    private Timestamp updateTime;


    /** 0表示未删除,1表示删除 */
    @TableLogic
    @TableField(fill=FieldFill.INSERT_UPDATE)
    private Boolean delFlag;


    public void copy(TbClaimInvest source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}
