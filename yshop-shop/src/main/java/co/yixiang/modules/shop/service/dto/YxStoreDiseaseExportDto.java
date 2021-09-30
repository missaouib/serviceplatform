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
public class YxStoreDiseaseExportDto implements Serializable {

    /** 一级类目 */
    private String firstCateName;

    /** 二级类目 */
    private String secondCateName;

    /** '分类类型，1/我要找药 2/健康馆'*/
    private String cateType;

    /** 是否显示 0 否，1 是 */
    private String showType;

    }
