/**
* Copyright (C) 2018-2020
* All rights reserved, Designed By www.yixiang.co
* 注意：
* 本软件为www.yixiang.co开发研制，未经购买不得使用
* 购买后可获得全部源代码（禁止转卖、分享、上传到码云、github等开源平台）
* 一经发现盗用、分享等行为，将追究法律责任，后果自负
*/
package co.yixiang.modules.api.domain;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import com.baomidou.mybatisplus.annotation.TableName;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import javax.validation.constraints.*;
import java.sql.Timestamp;
import java.io.Serializable;

/**
* @author visa
* @date 2020-11-30
*/
@Data
@TableName("user_agreement")
public class UserAgreement implements Serializable {

    /** 主键 */
    @TableId
    private Integer id;


    /** 用户id */
    private Integer uid;


    /** 患者姓名 */
    private String userName;


    /** 患者手机号 */
    private String userPhone;


    /** 签名请求ID */
    private String requestId;


    /** 签名ID */
    private String signFlowId;


    /** 签名的pdf地址 */
    private String signFilePath;


    /** 是否已经签名 0否 1是 */
    private Integer status;


    /** 记录生成时间 */
    @TableField(fill= FieldFill.INSERT)
    private Timestamp createTime;


    /** 记录更新时间 */
    @TableField(fill= FieldFill.INSERT_UPDATE)
    private Timestamp updateTime;


    public void copy(UserAgreement source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }

    @ApiModelProperty(value = "订单缓存key")
    private String orderKey;

    @ApiModelProperty(value = "订单编号")
    private String orderNo;
}
