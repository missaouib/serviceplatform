package co.yixiang.modules.yaoshitong.entity;

import co.yixiang.modules.user.entity.YxUser;
import co.yixiang.modules.yaoshitong.web.vo.ChatGroupMemberQueryVo;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import co.yixiang.common.entity.BaseEntity;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * 聊天群组
 * </p>
 *
 * @author visa
 * @since 2020-09-02
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value="ChatGroup对象", description="聊天群组")
public class ChatGroup extends BaseEntity {

    private static final long serialVersionUID = 1L;

@ApiModelProperty(value = "主键")
@TableId(value = "id", type = IdType.AUTO)
private Integer id;

@ApiModelProperty(value = "群组名称")
private String name;

@ApiModelProperty(value = "创建人uid")
private Integer makerId;

@ApiModelProperty(value = "管理员uid")
private Integer managerId;

@ApiModelProperty(value = "生成时间")
@TableField(fill= FieldFill.INSERT)
@JsonFormat(pattern = "yyyy-MM-dd")
private Date createTime;

@ApiModelProperty(value = "更新时间")
@TableField(fill= FieldFill.INSERT_UPDATE)
@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
private Date updateTime;

@ApiModelProperty(value = "封面图片")
private String image;

@TableField(exist = false)
List<ChatGroupMember> memberList;

@TableField(exist = false)
List<Integer> memberIds;

    @TableField(exist = false)
    private Integer unRead;

    @TableField(exist = false)
    private YxUser currentUser;
}
