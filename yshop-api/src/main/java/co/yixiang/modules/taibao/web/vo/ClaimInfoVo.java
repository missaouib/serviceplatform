package co.yixiang.modules.taibao.web.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.xml.bind.annotation.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description: claim_info
 * @Author: jeecg-boot
 * @Date: 2021-04-23
 * @Version: V1.0
 */
@ApiModel(value = "claim_info对象", description = "claim_info")
@Data
public class ClaimInfoVo implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "赔案号")
    private String claimno;


    /** 订单编号 */
    @ApiModelProperty(value = "订单编号")
    private Long orderId;

    /** 图片附件 */
    @ApiModelProperty(value = "图片附件")
    private String imgUrl;

    @ApiModelProperty(value = "赔案状态 0未完结 1：已完结")
    private Integer status;

}
