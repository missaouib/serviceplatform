package co.yixiang.modules.shop.web.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@ApiModel(value = "特定项目中商品组合传输对象", description = "特定项目中商品组合传输对象")
public class GroupProduct4ProjectDTO implements Serializable {
    @ApiModelProperty(value = "组合名称")
    private String groupName;

    @ApiModelProperty(value = "药品列表")
    private List<Product4ProjectDTO> productList = new ArrayList<>();
}
