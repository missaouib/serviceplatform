/**
* Copyright (C) 2018-2020
* All rights reserved, Designed By www.yixiang.co
* 注意：
* 本软件为www.yixiang.co开发研制，未经购买不得使用
* 购买后可获得全部源代码（禁止转卖、分享、上传到码云、github等开源平台）
* 一经发现盗用、分享等行为，将追究法律责任，后果自负
*/
package co.yixiang.modules.yaoshitong.domain;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import com.baomidou.mybatisplus.annotation.TableName;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import javax.validation.constraints.*;
import java.sql.Timestamp;
import java.io.Serializable;

/**
* @author visa
* @date 2020-10-21
*/
@Data
@TableName("yaoshitong_repurchase_med")
public class YaoshitongRepurchaseMed implements Serializable {

    @TableId
    private Integer id;


    /** 药品名称 */
    private String medName;


    /** 药品用药周期 */
    private Integer medCycle;


    /** sku编码 */
    private String medSku;


    /** 药品通用名 */
    private String medCommonName;


    @TableField(fill= FieldFill.INSERT)
    private Timestamp createTime;


    @TableField(fill= FieldFill.INSERT_UPDATE)
    private Timestamp updateTime;
    @TableField(fill = FieldFill.UPDATE)
    private Integer reminderDays;

    public void copy(YaoshitongRepurchaseMed source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}
