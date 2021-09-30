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
* @date 2021-02-08
*/
@Data
@TableName("yx_drug_users")
public class YxDrugUsers implements Serializable {

    /** 主键 */
    @TableId
    private Integer id;


    /** 用户id */
    private Integer uid;


    /** 姓名 */
    private String name;


    /** 关系 1/本人 2/亲属 3/朋友 4/其他 */
    private Integer relation;


    /** 手机号 */
    private String phone;


    /** 性别 */
    private String sex;


    /** 身份证号 */
    private String idcard;


    /** 生成时间 */
    @TableField(fill= FieldFill.INSERT)
    private Timestamp createTime;


    /** 最后更新时间 */
    @TableField(fill= FieldFill.INSERT_UPDATE)
    private Timestamp updateTime;


    /** 是否默认 */
    private Integer isDefault;


    /** 是否删除 */
    private Integer isDel;


    /** 年龄 */
    private Integer age;


    /** 疾病史 */
    private String diseaseHistory;


    /** 出生年月 */
    private String birth;


    /** 用药人类型 1/成人 2/儿童 */
    private Integer userType;


    /** 体重 */
    private String weight;


    public void copy(YxDrugUsers source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}
