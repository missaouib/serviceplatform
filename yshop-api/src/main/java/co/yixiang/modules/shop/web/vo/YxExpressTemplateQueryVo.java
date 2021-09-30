package co.yixiang.modules.shop.web.vo;

import co.yixiang.modules.shop.entity.YxExpressTemplateDetail;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import java.io.Serializable;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * 物流运费模板 查询结果对象
 * </p>
 *
 * @author visa
 * @date 2020-11-28
 */
@Data
@ApiModel(value="YxExpressTemplateQueryVo对象", description="物流运费模板查询参数")
public class YxExpressTemplateQueryVo implements Serializable{
    private static final long serialVersionUID = 1L;

private Integer id;

@ApiModelProperty(value = "模板名称")
private String templateName;

@ApiModelProperty(value = "物流商名称")
private String expressName;

@ApiModelProperty(value = "是否默认")
private Integer isDefault;

@ApiModelProperty(value = "项目代码")
private String projectCode;

@ApiModelProperty(value = "记录生成时间")
private Date createTime;

@ApiModelProperty(value = "记录更新时间")
private Date updateTime;

@ApiModelProperty(value = "记录创建人")
private String creater;

@ApiModelProperty(value = "记录更新人")
private String maker;

private List<YxExpressTemplateDetail> details;

}