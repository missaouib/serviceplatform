package co.yixiang.modules.shop.web.param;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import co.yixiang.common.web.param.QueryParam;

import java.util.List;


/**
 * <p>
 * 门店自提 查询参数对象
 * </p>
 *
 * @author hupeng
 * @date 2020-03-04
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value="门店自提", description="门店自提查询参数")
public class YxSystemStoreQueryParam extends QueryParam {
    private static final long serialVersionUID = 1L;
    @ApiModelProperty(value = "纬度",required = false)
    private String latitude;
    @ApiModelProperty(value = "经度",required = false)
    private String longitude;
    @ApiModelProperty(value = "药品Id",required = false)
    private Integer productId;

    private List<String> selectCountrys;

    private String provinceName;

    private String keyword;
    private Integer id;

}
