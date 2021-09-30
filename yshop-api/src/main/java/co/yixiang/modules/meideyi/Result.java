package co.yixiang.modules.meideyi;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
/**
 * @author zhouhang
 * @version 1.0
 * @date 2021/5/6 14:01
 */
@Data
@ApiModel(value="美德医返回结果对象", description="美德医返回结果对象")
public class Result {
    @ApiModelProperty(value = "返回码 200成功； 500失败")
     private Integer status;


    @ApiModelProperty(value = "返回描述")
     private String msg;

    @ApiModelProperty(value = "结果")
     private SubResult result;

}
