package co.yixiang.modules.yiyaobao.web.param;

import co.yixiang.annotation.Query;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import co.yixiang.common.web.param.QueryParam;

/**
 * <p>
 * 国家地区信息表 查询参数对象
 * </p>
 *
 * @author visazhou
 * @date 2020-05-16
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value="MdCountryQueryParam对象", description="国家地区信息表查询参数")
public class MdCountryQueryParam extends QueryParam {
    private static final long serialVersionUID = 1L;

    @Query(type = Query.Type.EQUAL)
    private String parentId;

// 1 结果集上加了 全国
    private Integer type;

    private String projectCode;
}
