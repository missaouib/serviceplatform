/**
* Copyright (C) 2018-2020
* All rights reserved, Designed By www.yixiang.co
* 注意：
* 本软件为www.yixiang.co开发研制，未经购买不得使用
* 购买后可获得全部源代码（禁止转卖、分享、上传到码云、github等开源平台）
* 一经发现盗用、分享等行为，将追究法律责任，后果自负
*/
package co.yixiang.modules.shop.service.dto;

import co.yixiang.modules.shop.domain.YxSystemStore;
import lombok.Data;
import java.sql.Timestamp;
import java.io.Serializable;
import java.util.List;

/**
* @author visa
* @date 2021-06-17
*/
@Data
public class HospitalDto implements Serializable {

    /** 主键 */
    private Integer id;

    /** 医院名称 */
    private String name;

    /** 地址 */
    private String address;

    /** logo图片 */
    private String image;

    /** 记录生成时间 */
    private Timestamp createTime;

    /** 记录更新时间 */
    private Timestamp updateTime;

    /** 站点信息 */
    private String siteInfo;

    /** 编码 */
    private String code;

    private String storeIds;

    private List<YxSystemStore> storeList;
}
