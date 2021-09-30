/**
* Copyright (C) 2018-2020
* All rights reserved, Designed By www.yixiang.co
* 注意：
* 本软件为www.yixiang.co开发研制，未经购买不得使用
* 购买后可获得全部源代码（禁止转卖、分享、上传到码云、github等开源平台）
* 一经发现盗用、分享等行为，将追究法律责任，后果自负
*/
package co.yixiang.modules.bi.domain;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import com.baomidou.mybatisplus.annotation.TableName;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.io.Serializable;

/**
* @author visa
* @date 2020-10-12
*/
@Data
@TableName("bi_mtd_data_medicines")
public class BiMtdDataMedicines implements Serializable {

    /** 主键 */
    @TableId
    private Integer id;


    /** 药品名称 */
    private String medName;


    /** 通用名 */
    private String commonName;


    /** 规格 */
    private String spec;


    /** 单位 */
    private String unit;


    /** 厂家 */
    private String manufacturer;


    /** 销售额 */
    private BigDecimal amount;


    /** 销量 */
    private Integer qty;


    /** 日期 2019-01 */
    private String infodate;


    public void copy(BiMtdDataMedicines source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}
