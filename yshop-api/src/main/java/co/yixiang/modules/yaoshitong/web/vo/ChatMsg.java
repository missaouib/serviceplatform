package co.yixiang.modules.yaoshitong.web.vo;

import co.yixiang.common.constant.CommonConstant;
import co.yixiang.common.entity.BaseEntity;
import co.yixiang.common.web.param.QueryParam;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = true)
public class ChatMsg  extends BaseEntity {
    private String senduserid;

    private String reciveuserid;

    private Date sendtime;

    private String msgtype;

    private String sendtext;

/*    private String sendusername;
    private String senduserimage;

    private String reciveusername;
    private String reciveuserimage;*/

    @ApiModelProperty(value = "页码,默认为1")
    @TableField(exist = false)
    private Integer page = CommonConstant.DEFAULT_PAGE_INDEX;

    @ApiModelProperty(value = "页大小,默认为10")
    @TableField(exist = false)
    private Integer limit = CommonConstant.DEFAULT_PAGE_SIZE;
}