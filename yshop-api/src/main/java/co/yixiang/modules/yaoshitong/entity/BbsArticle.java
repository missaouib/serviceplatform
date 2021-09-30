package co.yixiang.modules.yaoshitong.entity;

import co.yixiang.modules.yaoshitong.web.vo.BbsAuthorVo;
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
 * bbs文章列表
 * </p>
 *
 * @author visa
 * @since 2020-07-27
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value="BbsArticle对象", description="bbs文章列表")
public class BbsArticle extends BaseEntity {

    private static final long serialVersionUID = 1L;

@ApiModelProperty(value = "主键")
@TableId(value = "id", type = IdType.UUID)
    private String id;

@ApiModelProperty(value = "作者id")
private Integer authorId;

@ApiModelProperty(value = "内容")
private String content;

@JsonFormat(pattern="yyyy/MM/dd HH:mm:ss", timezone="GMT+8")
@ApiModelProperty(value = "生成时间")
@TableField(fill= FieldFill.INSERT)
private Date createAt;

@ApiModelProperty(value = "是否精华贴")
private Boolean good;

@JsonFormat(pattern="yyyy/MM/dd HH:mm:ss", timezone="GMT+8")
@ApiModelProperty(value = "最后回复时间")
private Date lastReplyAt;

@ApiModelProperty(value = "回复数量")
private Integer replyCount;

@ApiModelProperty(value = "标签分类")
private String tab;

@ApiModelProperty(value = "标题")
private String title;

@ApiModelProperty(value = "是否置顶贴")
//@TableField("is_top")
private Boolean isTop;

@ApiModelProperty(value = "阅读数量")
private Integer visitCount;

@TableField(exist = false)
private BbsAuthorVo author;

@ApiModelProperty(value = "图片")
private String images;
   // @TableField(exist = false)
//private List<BbsReply> replies;
   @ApiModelProperty(value = "点赞人")
private String upsStr;

   @ApiModelProperty(value = "收藏人")
private String collectStr;

    @TableField(exist = false)
   private Integer ups;

    @TableField(exist = false)
   private Integer collects;

    @ApiModelProperty(value = "是否本人点赞")
    @TableField(exist = false)
    private Boolean isSelfUp;

    @ApiModelProperty(value = "是否本人收藏")
    @TableField(exist = false)
    private Boolean isSelfCollect;
}
