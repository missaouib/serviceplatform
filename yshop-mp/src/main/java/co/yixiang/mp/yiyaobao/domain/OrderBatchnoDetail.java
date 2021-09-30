/**
* Copyright (C) 2018-2020
* All rights reserved, Designed By www.yixiang.co
* 注意：
* 本软件为www.yixiang.co开发研制，未经购买不得使用
* 购买后可获得全部源代码（禁止转卖、分享、上传到码云、github等开源平台）
* 一经发现盗用、分享等行为，将追究法律责任，后果自负
*/
package co.yixiang.mp.yiyaobao.domain;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import com.baomidou.mybatisplus.annotation.TableName;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import javax.validation.constraints.*;
import java.io.Serializable;

/**
* @author visa
* @date 2020-07-02
*/
@Data
@TableName("order_batchno_detail")
public class OrderBatchnoDetail implements Serializable {

    /** 主键 */
    @TableId
    private Integer id;


    /** 订单号 */
    private String orderId;


    /** 产品id */
    private String productId;


    /** 数量 */
    private Integer num;


    /** 批号 */
    private String batchno;


    /** 药品名称 */
    private String productName;


    /** 药监码列表 */
    private String codeList;


    public void copy(OrderBatchnoDetail source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}
