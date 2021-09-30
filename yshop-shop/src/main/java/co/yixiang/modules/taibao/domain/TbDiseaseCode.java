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
import lombok.Data;

import java.io.Serializable;
import java.sql.Timestamp;

/**
* @author zhoujinlai
* @date 2021-05-08
*/
@Data
@TableName("tb_disease_code")
public class TbDiseaseCode implements Serializable {

    @TableId
    private Integer id;


    /** ICD10疾病代码 */
    private String code;


    /** 疾病诊断名称 */
    private String name;


    /** 代码分类01意外02疾病 */
    private String codeClass;


    @TableField(fill= FieldFill.INSERT)
    private Timestamp createTime;


    @TableField(fill= FieldFill.INSERT_UPDATE)
    private Timestamp updateTime;


    @TableLogic
    @TableField(fill= FieldFill.INSERT_UPDATE)
    private Boolean delFlag;


    public void copy(TbDiseaseCode source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}
