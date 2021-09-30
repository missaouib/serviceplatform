package co.yixiang.modules.yaoshitong.web.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import java.io.Serializable;

import java.util.Date;

/**
 * <p>
 * 聊天组群聊天记录 查询结果对象
 * </p>
 *
 * @author visa
 * @date 2020-09-02
 */
@Data
@ApiModel(value="ChatGroupMsgQueryVo对象", description="聊天组群聊天记录查询参数")
public class ChatGroupMsgQueryVo implements Serializable{
    private static final long serialVersionUID = 1L;

@ApiModelProperty(value = "主键")
private Integer id;

@ApiModelProperty(value = "消息发送人")
private Integer sendUid;

@ApiModelProperty(value = "消息")
private String sendText;

@ApiModelProperty(value = "发送时间")
private Date sendTime;

@ApiModelProperty(value = "消息类型")
private String msgType;

private Integer groupId;

}