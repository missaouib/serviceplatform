package co.yixiang.modules.shop.web.param;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import co.yixiang.common.web.param.QueryParam;

/**
 * <p>
 * 商品分类表 查询参数对象
 * </p>
 *
 * @author hupeng
 * @date 2019-10-22
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value="YxStoreCategoryQueryParam对象", description="商品分类表查询参数")
public class YxStoreCategoryQueryParam extends QueryParam {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "合作伙伴ID",required = false)
    private String partnerId;
    @ApiModelProperty(value = "微信openid",required = false)
    private String openid;

    @ApiModelProperty(value = "科室Id",required = false)
    private String departmentId;
    @ApiModelProperty(value = "医生Id",required = false)
    private String doctorId;


}
