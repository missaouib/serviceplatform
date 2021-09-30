package co.yixiang.modules.yaoshitong.web.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import java.io.Serializable;

import java.util.Date;

/**
 * <p>
 * bbs文章列表 查询结果对象
 * </p>
 *
 * @author visa
 * @date 2020-07-27
 */
@Data
@ApiModel(value="BbsArticleQueryVo对象", description="bbs文章列表查询参数")
public class BbsArticleQueryVo implements Serializable{
    private static final long serialVersionUID = 1L;

@ApiModelProperty(value = "主键")
private String id;

@ApiModelProperty(value = "作者id")
private Integer authorId;

@ApiModelProperty(value = "内容")
private String content;

@ApiModelProperty(value = "生成时间")
private Date createAt;

@ApiModelProperty(value = "是否精华贴")
private Boolean good;

@ApiModelProperty(value = "最后回复时间")
private Date lastReplyAt;

@ApiModelProperty(value = "回复数量")
private Integer replyCount;

@ApiModelProperty(value = "标签分类")
private String tab;

@ApiModelProperty(value = "标题")
private String title;

@ApiModelProperty(value = "是否置顶贴")
private Boolean isTop;

@ApiModelProperty(value = "阅读数量")
private Integer visitCount;

@ApiModelProperty(value = "图片")
private String images;

}