/**
* Copyright (C) 2018-2020
* All rights reserved, Designed By www.yixiang.co
* 注意：
* 本软件为www.yixiang.co开发研制，未经购买不得使用
* 购买后可获得全部源代码（禁止转卖、分享、上传到码云、github等开源平台）
* 一经发现盗用、分享等行为，将追究法律责任，后果自负
*/
package co.yixiang.modules.shop.domain;
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
* @date 2020-08-20
*/
@Data
@TableName("charities")
public class Charities implements Serializable {

    /** 主键 */
    @TableId
    private Integer id;


    /** 编号 */
    private Integer code;


    /** 药房名称 */
    private String drugstoreName;


    /** 项目名称 */
    private String projectName;


    /** 基金会名称 */
    private String foundationsName;


    /** 电话，多个用逗号分隔 */
    private String phone;


    /** 药品名称 */
    private String productName;


    /** 药品通用名 */
    private String commonName;


    /** 剂型 */
    private String drugForm;


    /** 规格 */
    private String spec;


    /** 生产厂商 */
    private String manufacturer;


    /** 药品发放时段 */
    private String timeInterval;


    /** 项目展示网址 */
    private String projectWeburl;


    /** 热线电话 */
    private String hotlinePhone;


    /** 电子邮件 */
    private String email;


    /** 资料邮寄地址 */
    private String mailAddress;


    @TableField(fill= FieldFill.INSERT)
    private Timestamp createTime;


    @TableField(fill= FieldFill.INSERT_UPDATE)
    private Timestamp updateTime;

    private String image;

    public void copy(Charities source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}
