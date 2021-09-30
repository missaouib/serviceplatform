package co.yixiang.modules.shop.web.dto;

import co.yixiang.modules.shop.entity.MdPharmacistService;
import co.yixiang.modules.shop.entity.YxArticle;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class SpecialProjectDTO {
    List<SpecialProductDTO> productList;
    List<YxArticle> articleList;
    String projectCode;
    String projectName;
    List<MdPharmacistService> pharmacists;
    String phone;

    @ApiModelProperty(value = "七鱼客服组id")
    private String serviceGroupId;

    @ApiModelProperty(value = "项目备注")
    private String remark;


    @ApiModelProperty(value = "药师提示")
    private String pharmacistTips;
}
