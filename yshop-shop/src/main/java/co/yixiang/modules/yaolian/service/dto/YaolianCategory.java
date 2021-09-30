package co.yixiang.modules.yaolian.service.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel(value="药联药品类目对象", description="药联药品类目对象")
public class YaolianCategory implements Serializable {
    @ApiModelProperty(value = "类目ID")
    private String category_id;
    @ApiModelProperty(value = "上级ID")
    private String parent_id;
    @ApiModelProperty(value = "分类名称")
    private String category_name;
    @ApiModelProperty(value = "分类别名")
    private String category_alias;
    @ApiModelProperty(value = "类目层级")
    private String levels;
    @ApiModelProperty(value = "是否启用 0不启用 1启用")
    private String status;
    @ApiModelProperty(value = "分类顺序")
    private String sort;

}
