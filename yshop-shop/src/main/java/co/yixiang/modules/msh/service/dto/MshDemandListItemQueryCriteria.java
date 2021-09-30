/**
* Copyright (C) 2018-2020
* All rights reserved, Designed By www.yixiang.co
* 注意：
* 本软件为www.yixiang.co开发研制，未经购买不得使用
* 购买后可获得全部源代码（禁止转卖、分享、上传到码云、github等开源平台）
* 一经发现盗用、分享等行为，将追究法律责任，后果自负
*/
package co.yixiang.modules.msh.service.dto;

import lombok.Data;
import java.util.List;
import co.yixiang.annotation.Query;

/**
* @author cq
* @date 2020-12-25
*/
@Data
public class MshDemandListItemQueryCriteria{
	/** 模糊 */
    @Query(type = Query.Type.INNER_LIKE)
    private String patientname;

    /** 模糊 */
    @Query(type = Query.Type.INNER_LIKE)
    private String phone;

    /** 模糊 */
    @Query(type = Query.Type.INNER_LIKE)
    private String medName;

    /** 模糊 */
    @Query(type = Query.Type.INNER_LIKE)
    private String orderStatus;

    /** 模糊 */
    @Query(type = Query.Type.INNER_LIKE)
    private Integer auditStatus;

    /** 模糊 */
    @Query(type = Query.Type.INNER_LIKE)
    private Integer saveStatus;

    /** 模糊 */
    @Query(type = Query.Type.INNER_LIKE)
    private String memberId;

}
