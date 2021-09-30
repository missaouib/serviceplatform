package co.yixiang.modules.taiping.web.param;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import co.yixiang.common.web.param.QueryParam;

/**
 * <p>
 * 太平乐享虚拟卡 查询参数对象
 * </p>
 *
 * @author visa
 * @date 2020-11-19
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value="TaipingCardQueryParam对象", description="太平乐享虚拟卡查询参数")
public class TaipingCardQueryParam extends QueryParam {
    private static final long serialVersionUID = 1L;
}
