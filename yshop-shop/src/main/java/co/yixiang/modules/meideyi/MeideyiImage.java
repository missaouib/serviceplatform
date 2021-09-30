package co.yixiang.modules.meideyi;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author zhouhang
 * @version 1.0
 * @date 2021/5/10 8:43
 */
@Data
@ApiModel(value="美德医图片传输对象", description="美德医图片传输对象")
public class MeideyiImage {
    @ApiModelProperty(value = "处方图片")
    private String imageUrl;
}
