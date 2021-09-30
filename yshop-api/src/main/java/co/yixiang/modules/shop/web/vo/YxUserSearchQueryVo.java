package co.yixiang.modules.shop.web.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.io.Serializable;

import java.util.Date;

/**
 * <p>
 * 用户搜索词 查询结果对象
 * </p>
 *
 * @author visazhou
 * @date 2020-06-03
 */
@Data
@ApiModel(value="YxUserSearchQueryVo对象", description="用户搜索词查询参数")
public class YxUserSearchQueryVo implements Serializable{
    private static final long serialVersionUID = 1L;

@ApiModelProperty(value = "用户id")
private Integer uid;

@ApiModelProperty(value = "搜索词")
private String keyword;

@ApiModelProperty(value = "生成时间")
private Integer addTime;

@ApiModelProperty(value = "主键")
private Integer id;

@ApiModelProperty(value = "是否删除")
private Integer isDel;

}