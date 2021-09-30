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
 * @date 2020-06-05
 */
@Data
@ApiModel(value="EnterpriseTopicsQueryVo对象", description="查询参数")
public class EnterpriseTopicsQueryVo implements Serializable{
    private static final long serialVersionUID = 1L;

@ApiModelProperty(value = "主键")
private Integer id;

@ApiModelProperty(value = "企业名称")
private String name;

@ApiModelProperty(value = "logo图片")
private String logo;

@ApiModelProperty(value = "企业介绍图片")
private String image;

@ApiModelProperty(value = "简介")
private String synopsis;

@ApiModelProperty(value = "长图文内容，信息活动")
private String content;

@ApiModelProperty(value = "是否删除")
private Boolean isDel;

@ApiModelProperty(value = "添加时间")
private Integer addTime;

@ApiModelProperty(value = "是否显示 0/是 1/否")
private Boolean isShow;

}