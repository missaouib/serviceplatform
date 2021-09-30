/**
* Copyright (C) 2018-2020
* All rights reserved, Designed By www.yixiang.co
* 注意：
* 本软件为www.yixiang.co开发研制，未经购买不得使用
* 购买后可获得全部源代码（禁止转卖、分享、上传到码云、github等开源平台）
* 一经发现盗用、分享等行为，将追究法律责任，后果自负
*/
package co.yixiang.modules.shop.service.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import java.io.Serializable;
import java.util.List;

/**
* @author visa
* @date 2020-06-02
*/
@Data
public class YxStoreDiseaseDto implements Serializable {

    /** 商品分类表ID */
    private Integer id;

    /** 父id */
    private Integer pid;

    /** 病种名称 */
    private String cateName;

    /** 排序 */
    private Integer sort;

    /** 图标 */
    private String pic;

    /** 是否推荐 */
    private Integer isShow;

    /** 添加时间 */
    private Integer addTime;

    /** 删除状态 */
    private Integer isDel;

    private String projectCode="";
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<YxStoreDiseaseDto> children;


    /** '分类类型，1/我要找药 2/健康馆'*/
    private String cateType;

    private List<String> cateTypeList;

    public String getLabel() {
        return cateName;
    }
}
