package co.yixiang.modules.yaoshitong.entity;

import co.yixiang.modules.yaoshitong.web.vo.BbsAuthorVo;
import com.baomidou.mybatisplus.annotation.IdType;
import co.yixiang.common.entity.BaseEntity;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.models.auth.In;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * 帖子回复表
 * </p>
 *
 * @author visa
 * @since 2020-07-27
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value="BbsReply对象", description="帖子回复表")
public class BbsReply extends BaseEntity {

    private static final long serialVersionUID = 1L;

@ApiModelProperty(value = "主键")
@TableId(value = "id", type = IdType.UUID)
    private String id;

@ApiModelProperty(value = "文章id")
private String articleId;

@ApiModelProperty(value = "回复人id")
private Integer authorId;

@ApiModelProperty(value = "回复内容")
private String content;

@ApiModelProperty(value = "回复时间")
private Date createAt;

private Integer isUped;

private String replyId;

@ApiModelProperty(value = "点赞人")
private String upsStr;

@TableField(exist = false)
private BbsAuthorVo author;
    @TableField(exist = false)
private Integer ups;

private String images;
@TableLogic
private Boolean isDel;

@ApiModelProperty(value = "是否本人回复")
@TableField(exist = false)
private Boolean isSelf;

@ApiModelProperty(value = "是否本人点赞")
@TableField(exist = false)
private Boolean isSelfUp;

}
