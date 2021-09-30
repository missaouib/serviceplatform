package co.yixiang.modules.yaoshitong.entity;

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
 * 聊天群组成员
 * </p>
 *
 * @author visa
 * @since 2020-09-02
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value="ChatGroupMember对象", description="聊天群组成员")
public class ChatGroupMember extends BaseEntity {

    private static final long serialVersionUID = 1L;

@ApiModelProperty(value = "主键")
@TableId(value = "id", type = IdType.AUTO)
private Integer id;

@ApiModelProperty(value = "群组id")
private Integer groupId;

@ApiModelProperty(value = "成员uid")
private Integer uid;

    @ApiModelProperty(value = "生成时间")
    @TableField(fill= FieldFill.INSERT)
    private Date createTime;

    @ApiModelProperty(value = "更新时间")
    @TableField(fill= FieldFill.INSERT_UPDATE)
    private Date updateTime;

    @TableField(exist = false)
    private String name;
    @TableField(exist = false)
    private String phone;
    @TableField(exist = false)
    private String avatar;

    private Integer isManager;



}
