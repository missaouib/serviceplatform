package co.yixiang.modules.yaoshitong.web.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import java.io.Serializable;

import java.util.Date;

/**
 * <p>
 * 聊天群组成员 查询结果对象
 * </p>
 *
 * @author visa
 * @date 2020-09-02
 */
@Data
@ApiModel(value="ChatGroupMemberQueryVo对象", description="聊天群组成员查询参数")
public class ChatGroupMemberQueryVo implements Serializable{
    private static final long serialVersionUID = 1L;

@ApiModelProperty(value = "主键")
private Integer id;

@ApiModelProperty(value = "群组id")
private Integer groupId;

@ApiModelProperty(value = "成员uid")
private Integer uid;

@ApiModelProperty(value = "记录生成时间")
private Date createTime;

@ApiModelProperty(value = "更新时间")
private Date updateTime;

}