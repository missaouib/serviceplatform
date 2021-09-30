/**
* Copyright (C) 2018-2020
* All rights reserved, Designed By www.yixiang.co
* 注意：
* 本软件为www.yixiang.co开发研制，未经购买不得使用
* 购买后可获得全部源代码（禁止转卖、分享、上传到码云、github等开源平台）
* 一经发现盗用、分享等行为，将追究法律责任，后果自负
*/
package co.yixiang.modules.shop.service.dto;

import lombok.Data;
import java.io.Serializable;

/**
* @author visa
* @date 2020-06-05
*/
@Data
public class EnterpriseTopicsDto implements Serializable {

    /** 主键 */
    private Integer id;

    /** 企业名称 */
    private String name;

    /** logo图片 */
    private String logo;

    /** 企业介绍图片 */
    private String image;

    /** 简介 */
    private String synopsis;

    /** 长图文内容，信息活动 */
    private String content;

    /** 添加时间 */
    private Integer addTime;

    /** 是否删除 */
    private Integer isDel;

    /** 是否显示 0/是 1/否 */
    private Integer isShow;
}
