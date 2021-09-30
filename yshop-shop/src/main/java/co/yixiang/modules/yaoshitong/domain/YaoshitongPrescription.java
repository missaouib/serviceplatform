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
import java.util.Date;

/**
* @author visa
* @date 2020-07-21
*/
@Data
@TableName("yaoshitong_prescription")
public class YaoshitongPrescription implements Serializable {

    /** 主键 */
    @TableId
    private Integer id;


    /** 处方编号 */
    private String prescriptionNo;


    /** 患者id */
    private Integer patientId;


    /** 药师id */
    private String pharmacistId;


    /** 是否处方药 */
    private String isPrescription;


    /** 处方日期 */
    private Date prescriptionDate;


    /** 处方医院 */
    private String hospitalName;


    /** 处方医生 */
    private String doctorName;


    /** 科室 */
    private String departName;


    /** 诊断 */
    private String diagnosis;


    /** 药品明细 */
    private String medDetail;


    /** 生成时间 */
    @TableField(fill= FieldFill.INSERT)
    private Timestamp createTime;


    /** 更新时间 */
    @TableField(fill= FieldFill.INSERT_UPDATE)
    private Timestamp updateTime;


    /** 处方图片 */
    private String imagePath;


    public void copy(YaoshitongPrescription source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}
