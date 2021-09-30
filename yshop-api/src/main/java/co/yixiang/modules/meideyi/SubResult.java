package co.yixiang.modules.meideyi;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;
/**
 * @author zhouhang
 * @version 1.0
 * @date 2021/5/6 14:01
 */
@Data
@ApiModel(value="美德医商品传输对象", description="美德医商品传输对象")
public class SubResult {
    @ApiModelProperty(value = "总条数")
    private Integer total;

    @ApiModelProperty(value = "商品列表")
    private List<Goods> root;


}
