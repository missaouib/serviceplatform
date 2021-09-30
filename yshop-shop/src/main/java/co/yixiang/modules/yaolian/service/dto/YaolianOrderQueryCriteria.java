/**
* Copyright (C) 2018-2020
* All rights reserved, Designed By www.yixiang.co
* 注意：
* 本软件为www.yixiang.co开发研制，未经购买不得使用
* 购买后可获得全部源代码（禁止转卖、分享、上传到码云、github等开源平台）
* 一经发现盗用、分享等行为，将追究法律责任，后果自负
*/
package co.yixiang.modules.yaolian.service.dto;

import lombok.Data;
import java.sql.Timestamp;
import java.util.List;
import co.yixiang.annotation.Query;

/**
* @author visa
* @date 2021-03-02
*/
@Data
public class YaolianOrderQueryCriteria{
    /** BETWEEN */
    @Query(type = Query.Type.BETWEEN)
    private List<Timestamp> createTime;

    @Query(type =  Query.Type.INNER_LIKE)
    private String id;
}