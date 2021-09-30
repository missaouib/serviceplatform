/**
* Copyright (C) 2018-2020
* All rights reserved, Designed By www.yixiang.co
* 注意：
* 本软件为www.yixiang.co开发研制，未经购买不得使用
* 购买后可获得全部源代码（禁止转卖、分享、上传到码云、github等开源平台）
* 一经发现盗用、分享等行为，将追究法律责任，后果自负
*/
package co.yixiang.modules.taiping.domain;
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
* @date 2020-11-02
*/
@Data
@TableName("taiping_card")
public class TaipingCard implements Serializable {

    /** 主键 */
    @TableId
    private Integer id;


    /** 卡号 */
    private String cardNumber;


    /** 卡的具体种类 */
    private String cardType;


    /** 卡渠道 */
    private String sellChannel;


    /** 代理 */
    private String agentCate;


    /** 组织ID */
    @TableField(value = "organ_id")
    private String organID;


    /** 乐享同步记录时间 */
    private String insertTime;


    @TableField(fill= FieldFill.INSERT)
    private Timestamp createTime;


    @TableField(fill= FieldFill.INSERT_UPDATE)
    private Timestamp updateTime;


    public void copy(TaipingCard source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}
