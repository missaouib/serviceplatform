package co.yixiang.modules.meideyi;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author zhouhang
 * @version 1.0
 * @date 2021/5/6 14:01
 */
@ApiModel(value="美德医商品传输对象", description="美德医商品传输对象")
@Data
public class RequestParam {

    @ApiModelProperty(value = "页码 从1开始")
    private Integer page;

    @ApiModelProperty(value = "排序 0：默认； 20：价格升序；21：价格降序；")
    private Integer sortType;

    @ApiModelProperty(value = "每页数据条数")
    private Integer limit;

    @ApiModelProperty(value = "关键词 可能是药品或疾病")
    private String keyword;
}
