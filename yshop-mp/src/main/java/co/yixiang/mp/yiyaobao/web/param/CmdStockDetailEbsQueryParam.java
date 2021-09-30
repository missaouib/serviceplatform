package co.yixiang.mp.yiyaobao.web.param;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import co.yixiang.common.web.param.QueryParam;

/**
 * <p>
 * 商品库存明细表 查询参数对象
 * </p>
 *
 * @author visazhou
 * @date 2020-06-16
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value="CmdStockDetailEbsQueryParam对象", description="商品库存明细表查询参数")
public class CmdStockDetailEbsQueryParam extends QueryParam {
    private static final long serialVersionUID = 1L;
}
