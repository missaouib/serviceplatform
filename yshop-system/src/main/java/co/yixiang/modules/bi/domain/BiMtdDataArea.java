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
* @date 2020-09-28
*/
@Data
@TableName("bi_mtd_data_area")
public class BiMtdDataArea implements Serializable {

    @TableId
    private Integer id;


    /** 业务大区名称 */
    private String areaname;


    /** 销售额 */
    private BigDecimal amount;


    /** callcenter呼入量 */
    private Integer callin;


    /** 日期 */
    private String infoDate;


    public void copy(BiMtdDataArea source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}
