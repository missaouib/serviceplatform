/**
* Copyright (C) 2018-2020
* All rights reserved, Designed By www.yixiang.co
* 注意：
* 本软件为www.yixiang.co开发研制，未经购买不得使用
* 购买后可获得全部源代码（禁止转卖、分享、上传到码云、github等开源平台）
* 一经发现盗用、分享等行为，将追究法律责任，后果自负
*/
package co.yixiang.modules.shop.domain;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import com.baomidou.mybatisplus.annotation.TableName;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import javax.validation.constraints.*;
import java.io.Serializable;

/**
* @author visa
* @date 2020-06-10
*/
@Data
@TableName("url_config")
public class UrlConfig implements Serializable {

    @TableId
    private Integer id;


    /** url */
    private String url;


    /** 图片地址 */
    private String image;


    public void copy(UrlConfig source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}
