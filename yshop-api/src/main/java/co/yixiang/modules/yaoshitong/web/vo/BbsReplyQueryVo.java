package co.yixiang.modules.yaoshitong.web.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import java.io.Serializable;

import java.util.Date;

/**
 * <p>
 * 帖子回复表 查询结果对象
 * </p>
 *
 * @author visa
 * @date 2020-07-27
 */
@Data
@ApiModel(value="BbsReplyQueryVo对象", description="帖子回复表查询参数")
public class BbsReplyQueryVo implements Serializable{
    private static final long serialVersionUID = 1L;

@ApiModelProperty(value = "主键")
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

}