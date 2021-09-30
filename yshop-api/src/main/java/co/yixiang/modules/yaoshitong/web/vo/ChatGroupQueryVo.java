package co.yixiang.modules.yaoshitong.web.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import java.io.Serializable;

import java.util.Date;

/**
 * <p>
 * 聊天群组 查询结果对象
 * </p>
 *
 * @author visa
 * @date 2020-09-02
 */
@Data
@ApiModel(value="ChatGroupQueryVo对象", description="聊天群组查询参数")
public class ChatGroupQueryVo implements Serializable{
    private static final long serialVersionUID = 1L;

@ApiModelProperty(value = "主键")
private Integer id;

@ApiModelProperty(value = "群组名称")
private String name;

@ApiModelProperty(value = "创建人uid")
private Integer makerId;

@ApiModelProperty(value = "管理员uid")
private Integer managerId;

@ApiModelProperty(value = "生成时间")
@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
private Date createTime;

@ApiModelProperty(value = "更新时间")
@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
private Date updateTime;

}