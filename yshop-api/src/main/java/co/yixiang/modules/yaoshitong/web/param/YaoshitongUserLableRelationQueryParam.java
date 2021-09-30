package co.yixiang.modules.yaoshitong.web.param;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import co.yixiang.common.web.param.QueryParam;

/**
 * <p>
 * 患者对应的标签库 查询参数对象
 * </p>
 *
 * @author visa
 * @date 2020-08-26
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value="YaoshitongUserLableRelationQueryParam对象", description="患者对应的标签库查询参数")
public class YaoshitongUserLableRelationQueryParam extends QueryParam {
    private static final long serialVersionUID = 1L;
}
