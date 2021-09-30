package co.yixiang.modules.yaoshitong.web.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import java.io.Serializable;

import java.util.Date;

/**
 * <p>
 * 药师通用户标签 查询结果对象
 * </p>
 *
 * @author visa
 * @date 2020-08-26
 */
@Data
@ApiModel(value="YaoshitongUserLableQueryVo对象", description="药师通用户标签查询参数")
public class YaoshitongUserLableQueryVo implements Serializable{
    private static final long serialVersionUID = 1L;

@ApiModelProperty(value = "主键")
private Integer id;

@ApiModelProperty(value = "用户id")
private Integer uid;

@ApiModelProperty(value = "标签名称")
private String lableName;

@ApiModelProperty(value = "记录生成时间")
private Date createTime;

@ApiModelProperty(value = "记录更新时间")
private Date updateTime;

@ApiModelProperty(value = "是否默认")
private Integer isDefault;

}