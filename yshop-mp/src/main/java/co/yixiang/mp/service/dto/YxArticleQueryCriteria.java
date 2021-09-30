/**
 * Copyright (C) 2018-2020
 * All rights reserved, Designed By www.yixiang.co

 */
package co.yixiang.mp.service.dto;

import co.yixiang.annotation.Query;
import lombok.Data;

/**
* @author hupeng
* @date 2020-05-12
*/
@Data
public class YxArticleQueryCriteria{
    @Query(type = Query.Type.EQUAL)
    private Integer type;
    @Query(type = Query.Type.EQUAL)
    private String projectCode="";
}
