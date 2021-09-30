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
import java.math.BigDecimal;
import java.io.Serializable;

/**
* @author visa
* @date 2020-11-28
*/
@Data
@TableName("yx_express_template_detail")
public class YxExpressTemplateDetail implements Serializable {

    @TableId
    private Integer id;


    /** 模板id */
    private Integer templateId;


    /** 区域名称 */
    private String areaName;


    /** 价格 */
    private BigDecimal price;


    /** 记录生成时间 */
    @TableField(fill= FieldFill.INSERT)
    private Timestamp createTime;


    /** 更新时间 */
    @TableField(fill= FieldFill.INSERT_UPDATE)
    private Timestamp updateTime;


    /** 记录生成人 */
    private String creater;


    /** 记录更新人 */
    private String maker;
    /** 区域级别，省份1，城市2 */
    private Integer level;

    /** 上级区域名称 */
    private String parentAreaName;

    public void copy(YxExpressTemplateDetail source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}
