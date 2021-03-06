/**
 * Copyright (C) 2018-2020
 * All rights reserved, Designed By www.yixiang.co

 */
package co.yixiang.modules.quartz.service.dto;

import co.yixiang.annotation.Query;
import lombok.Data;

import java.sql.Timestamp;
import java.util.List;

/**
* @author hupeng
* @date 2020-05-13
*/
@Data
public class QuartzLogQueryCriteria{

    @Query(type = Query.Type.INNER_LIKE)
    private String jobName;
    @Query(type = Query.Type.BETWEEN)
    private List<Timestamp> createTime;
    @Query(type = Query.Type.EQUAL)
    private Boolean isSuccess;
}
