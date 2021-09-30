package co.yixiang.modules.yaoshitong.entity;

import co.yixiang.common.constant.CommonConstant;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import co.yixiang.common.entity.BaseEntity;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * <p>
 * 聊天组群聊天记录
 * </p>
 *
 * @author visa
 * @since 2020-09-02
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value="ChatGroupMsg对象", description="聊天组群聊天记录")
public class ChatGroupMsg extends BaseEntity {

    private static final long serialVersionUID = 1L;

@ApiModelProperty(value = "主键")
@TableId(value = "id", type = IdType.AUTO)
private Integer id;

@ApiModelProperty(value = "消息发送人")
private Integer sendUid;

@ApiModelProperty(value = "消息")
private String sendText;

@ApiModelProperty(value = "发送时间")
@TableField(fill= FieldFill.INSERT)
private Date sendTime;

@ApiModelProperty(value = "消息类型")
private String msgType;

private Integer groupId;

    @ApiModelProperty(value = "页码,默认为1")
    @TableField(exist = false)
    private Integer page = CommonConstant.DEFAULT_PAGE_INDEX;

    @ApiModelProperty(value = "页大小,默认为10")
    @TableField(exist = false)
    private Integer limit = CommonConstant.DEFAULT_PAGE_SIZE;

    @TableField(exist = false)
    private String sendAvatar;

    @TableField(exist = false)
    private String sendName;

    @TableField(exist = false)
    private Boolean isSelfRecord;
}
