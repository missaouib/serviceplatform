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
import java.sql.Timestamp;
import java.io.Serializable;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

/**
* @author visa
* @date 2020-10-16
*/
@Data
public class MdCountryDto implements Serializable {

    /** 标识 */
    /** 防止精度丢失 */
    @JsonSerialize(using= ToStringSerializer.class)
    private Long id;

    /** 代码 */
    private String code;

    /** 名称 */
    private String name;

    /** 名称拼音 */
    private String pinyin;

    /** 父节点ID */
    private String parentId;

    /** 树节点ID */
    private String treeId;

    /** 是否叶子节点 */
    private Integer isLeaf;

    /** 是否售药城市(0-否;1-是) */
    private Integer isSale;

    /** 是否直辖市(0-否;1-是) */
    private Integer isDirect;

    /** 城市编码，如021 */
    private String areaCode;

    /** 描述 */
    private String description;

    /** 创建人 */
    private String createUser;

    /** 创建时间 */
    private Timestamp createTime;

    /** 更新人 */
    private String updateUser;

    /** 更新时间 */
    private Timestamp updateTime;

    /** 业务区域 */
    private String areaName;
}
