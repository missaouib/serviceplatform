package co.yixiang.modules.yaolian.service.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@ApiModel(value="药联药品类目传输对象", description="药联药品类目传输对象")
public class YaolianCategoryDto implements Serializable {
    @ApiModelProperty(value = "请求头")
    private ReqHead requestHead;
    @ApiModelProperty(value = "药品类目")
    private List<YaolianCategory> categorys;
}
