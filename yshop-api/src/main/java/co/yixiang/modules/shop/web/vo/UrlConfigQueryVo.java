package co.yixiang.modules.shop.web.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.io.Serializable;

import java.util.Date;

/**
 * <p>
 *  查询结果对象
 * </p>
 *
 * @author visazhou
 * @date 2020-06-10
 */
@Data
@ApiModel(value="UrlConfigQueryVo对象", description="查询参数")
public class UrlConfigQueryVo implements Serializable{
    private static final long serialVersionUID = 1L;

private Integer id;

@ApiModelProperty(value = "url")
private String url;

@ApiModelProperty(value = "图片地址")
private String image;

}