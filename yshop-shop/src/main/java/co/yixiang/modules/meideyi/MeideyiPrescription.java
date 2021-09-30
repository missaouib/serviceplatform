package co.yixiang.modules.meideyi;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author zhouhang
 * @version 1.0
 * @date 2021/5/10 8:41
 */
@Data
@ApiModel(value="美德医处方传输对象", description="美德医处方传输对象")
public class MeideyiPrescription {
    @ApiModelProperty(value = "处方签号")
    private String prescriptionCode;
    @ApiModelProperty(value = "处方日期")
    private String prescriptionDate;
    @ApiModelProperty(value = "处方日期")
    private List<MeideyiImage> images;

}
