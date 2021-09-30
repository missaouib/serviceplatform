package co.yixiang.modules.yaoshitong.web.param;

import co.yixiang.annotation.Query;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import co.yixiang.common.web.param.QueryParam;

/**
 * <p>
 * 药师通用户标签 查询参数对象
 * </p>
 *
 * @author visa
 * @date 2020-08-26
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value="YaoshitongUserLableQueryParam对象", description="药师通用户标签查询参数")
public class YaoshitongUserLableQueryParam extends QueryParam {
    private static final long serialVersionUID = 1L;
    @Query(type = Query.Type.EQUAL)
    private Integer uid;
}
