package co.yixiang.modules.shop.service.dto;

import co.yixiang.annotation.Query;
import lombok.Data;

/**
* @author hupeng
* @date 2019-12-12
*/
@Data
public class YxExpressQueryCriteria{
    // 精确
    @Query
    private String name;
}