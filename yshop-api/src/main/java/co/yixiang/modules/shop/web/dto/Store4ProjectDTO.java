package co.yixiang.modules.shop.web.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@ApiModel(value = "Store4ProjectDTO对象", description = "特定项目中的药房数据对象")
public class Store4ProjectDTO  implements Serializable {

    @ApiModelProperty(value = "药房id")
    private Integer id;

    @ApiModelProperty(value = "药房名称")
    private String name;

    @ApiModelProperty(value = "药房名称")
    private String phone;

    @ApiModelProperty(value = "详细地址")
    private String address;

    @ApiModelProperty(value = "门店logo")
    private String image;

    // 药品列表
    @ApiModelProperty(value = "药房在售药品列表")
    private List<Product4ProjectDTO> productList = new ArrayList<>();

    @ApiModelProperty(value = "药房在售组合药品列表")
    private List<GroupProduct4ProjectDTO> groupList = new ArrayList<>();
}
