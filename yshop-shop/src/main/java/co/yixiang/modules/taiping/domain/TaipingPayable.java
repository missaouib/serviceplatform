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
* @date 2020-11-03
*/
@Data
@TableName("taiping_payable")
public class TaipingPayable implements Serializable {

    /** 主键 */
    @TableId
    private Integer id;


    /** 卡号 */
    private String cardNumber;


    /** 卡类型，根据卡类型获取单价作为结算依据 */
    private String cardType;


    /** 应付记录号 */
    @TableField(value = "fee_id")
    private String feeID;


    /** 应付记录的状态 1 新增记录  -1 负记录 */
    private Integer negativeRecord;


    /** 卡渠道 */
    private String sellChannel;


    /** 代理 */
    private String agentCate;


    /** 组织ID */
    @TableField(value = "organ_id")
    private String organID;


    /** 乐享同步记录时间 */
    private String insertTime;


    /** 记录生成时间 */
    @TableField(fill= FieldFill.INSERT)
    private Timestamp createTime;


    /** 记录更新时间 */
    @TableField(fill= FieldFill.INSERT_UPDATE)
    private Timestamp updateTime;


    public void copy(TaipingPayable source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}
