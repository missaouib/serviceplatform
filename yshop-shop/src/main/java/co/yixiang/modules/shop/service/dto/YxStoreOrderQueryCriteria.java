package co.yixiang.modules.shop.service.dto;

import co.yixiang.annotation.Query;
import lombok.Data;

import java.util.List;

/**
* @author hupeng
* @date 2019-10-14
*/
@Data
public class YxStoreOrderQueryCriteria{

    // 模糊
    @Query(type = Query.Type.UNIX_TIMESTAMP)
    private List<String> addTime;


    // 模糊
    @Query(type = Query.Type.INNER_LIKE)
    private String orderId;

    // 模糊
    @Query(type = Query.Type.INNER_LIKE)
    private String realName;

    // 模糊
    @Query(type = Query.Type.INNER_LIKE)
    private String userPhone;

    @Query
    private Integer paid;


    @Query(type = Query.Type.IN)
    private List<Integer> status;

    @Query
    private Integer refundStatus;

    @Query
    private Integer isDel;

    @Query
    private Integer combinationId;

    @Query
    private Integer seckillId;

    @Query
    private Integer bargainId;

    @Query(propName="combinationId",type = Query.Type.NOT_EQUAL)
    private Integer newCombinationId;

    @Query(propName="seckillId",type = Query.Type.NOT_EQUAL)
    private Integer newSeckillId;

    @Query(propName="bargainId",type = Query.Type.NOT_EQUAL)
    private Integer newBargainId;

    @Query
    private Integer shippingType;

    @Query(type = Query.Type.IN)
    private List<Integer> storeId;

    @Query
    private Integer uploadGjpFlag;

    @Query
    private String partnerCode;

    @Query
    private String projectCode;

    private String storeName;

    @Query(type = Query.Type.EQUAL)
    private String payType;


    @Query(type = Query.Type.BETWEEN)
    private List<String> refundFactTime;
    @Query(type = Query.Type.INNER_LIKE)
    private String drugUserName;
    @Query(type = Query.Type.INNER_LIKE)
    private String drugUserPhone;
}