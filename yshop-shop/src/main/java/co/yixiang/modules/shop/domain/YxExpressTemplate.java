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
import java.util.List;

/**
* @author visa
* @date 2020-11-28
*/
@Data
@TableName("yx_express_template")
public class YxExpressTemplate implements Serializable {

    @TableId
    private Integer id;


    /** 模板名称 */
    private String templateName;


    /** 物流商名称 */
    private String expressName;


    /** 是否默认 */
    private Integer isDefault;


    /** 项目代码 */
    private String projectCode;


    /** 记录生成时间 */
    @TableField(fill= FieldFill.INSERT)
    private Timestamp createTime;


    /** 记录更新时间 */
    @TableField(fill= FieldFill.INSERT_UPDATE)
    private Timestamp updateTime;


    /** 记录创建人 */
    private String creater;


    /** 记录更新人 */
    private String maker;

    @TableField(exist = false)
    private List<YxExpressTemplateDetail> details;

    public void copy(YxExpressTemplate source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}
