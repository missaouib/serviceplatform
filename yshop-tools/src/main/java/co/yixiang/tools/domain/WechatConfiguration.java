/**
* Copyright (C) 2018-2020
* All rights reserved, Designed By www.yixiang.co
* 注意：
* 本软件为www.yixiang.co开发研制，未经购买不得使用
* 购买后可获得全部源代码（禁止转卖、分享、上传到码云、github等开源平台）
* 一经发现盗用、分享等行为，将追究法律责任，后果自负
*/
package co.yixiang.tools.domain;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.sql.Timestamp;

/**
* @author zhoujinlai
* @date 2021-09-24
*/
@Data
@TableName("wechat_configuration")
public class WechatConfiguration implements Serializable {

    @TableId
    private Integer id;


    /** 1：小程序 2：H5 、 公众号  3：app  4：众安 */
    private String type;


    /** 1:默认 */
    private Integer isDefault;


    /** 应用名称 */
    private String name;


    /** 商户Id */
    private String mchId;


    /** 商户key */
    private String mchKey;


    /** 微信appid */
    private String appId;


    /** 支付证书地址 */
    private String keyPath;


    /**  支付回调接口 */
    private String notifyUrl;

    /**  退款回调接口 */
    private String returnNotifyUrl;

    /** 创建时间 */
    @TableField(fill= FieldFill.INSERT)
    private Timestamp createTime;


    /** 创建人 */
    private String createUser;


    /** 更新时间 */
    @TableField(fill= FieldFill.INSERT_UPDATE)
    private Timestamp updateTime;


    /** 更新人 */
    private String updateUser;


    /** 删除表示  1：已删除  0 未删除 */
    private Integer deleteFlag;


    public void copy(WechatConfiguration source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}
