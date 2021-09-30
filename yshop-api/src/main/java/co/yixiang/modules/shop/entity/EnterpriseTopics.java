package co.yixiang.modules.shop.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import co.yixiang.common.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * <p>
 * 
 * </p>
 *
 * @author visazhou
 * @since 2020-06-05
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value="EnterpriseTopics对象", description="")
public class EnterpriseTopics extends BaseEntity {

    private static final long serialVersionUID = 1L;

@ApiModelProperty(value = "主键")
@TableId(value = "id", type = IdType.AUTO)
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
