package co.yixiang.modules.shop.web.param;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import co.yixiang.common.web.param.QueryParam;

import java.util.List;

/**
 * <p>
 * 商品表 查询参数对象
 * </p>
 *
 * @author hupeng
 * @date 2019-10-19
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value="YxStoreProductQueryParam对象", description="商品表查询参数")
public class YxStoreProductQueryParam extends QueryParam {
    private static final long serialVersionUID = 1L;

    private String type;
    @ApiModelProperty(value = "分类/科室Id")
    private String sid;
    private String news;
    @ApiModelProperty(value = "价格排序,desc/asc",required = true)
    private String priceOrder;
    @ApiModelProperty(value = "销量排序,desc/asc",required = true)
    private String salesOrder;
    private String keyword;

    @ApiModelProperty(value = "合作伙伴Code",required = false)
    private String partnerCode;
    @ApiModelProperty(value = "经度",required = false)
    private String longitude;
    @ApiModelProperty(value = "纬度",required = false)
    private String latitude;

    @ApiModelProperty(value = "微信openid",required = false)
    private String openid;

    @ApiModelProperty(value = "医生Id",required = false)
    private String doctorId;

    @ApiModelProperty(value = "科室Id",required = false)
    private String departmentId;

    @ApiModelProperty(value = "病种id",required = false)
    private String diseaseId;

    @ApiModelProperty(value = "项目代码",required = false)
    private String projectCode="";


    @ApiModelProperty(value = "病种大类id",required = false)
    private String diseaseParentId;

    @ApiModelProperty(value = "药厂名称/品牌名称",required = false)
    private String manufacturer;

    @ApiModelProperty(value = "药厂Id/品牌Id",required = false)
    private Integer manufacturerId;


    // 药房分类  85折药房/5折药房
    @ApiModelProperty(value = "药房分类",required = false)
    private String drugStoreType = "";

    @ApiModelProperty(value = "太平卡类型",required = false)
    private String cardType = "";


    @ApiModelProperty(value = "药店id,多个用英文逗号分隔",required = false)
    private String storeIds;

    @ApiModelProperty(value = "药店id",required = false)
    private List<Integer> storeList;

    private String pinYin;
}
