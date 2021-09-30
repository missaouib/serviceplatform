package co.yixiang.modules.shop.service.dto;

import co.yixiang.annotation.Query;
import lombok.Data;

/**
* @author hupeng
* @date 2019-10-04
*/
@Data
public class YxStoreProductQueryCriteria{

    // 模糊
    @Query(type = Query.Type.INNER_LIKE)
    private String storeName;

    @Query(type = Query.Type.INNER_LIKE)
    private String commonName;

    private Integer storeId;

    /**
     * 是否为广州药店  0 否  1是
     */
    private Integer isGZStoreId;
    // 精确
    @Query
    private Integer isDel;

    @Query
    private Integer isShow;

    @Query
    private Integer uploadGjpFlag;

    private String keyword;

    private String projectCode="";
    @Query(type = Query.Type.INNER_LIKE)
    private String yiyaobaoSku;
}