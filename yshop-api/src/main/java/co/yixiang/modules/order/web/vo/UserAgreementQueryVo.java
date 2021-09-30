package co.yixiang.modules.order.web.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import java.io.Serializable;

import java.util.Date;

/**
 * <p>
 * 用户同意书 查询结果对象
 * </p>
 *
 * @author visa
 * @date 2020-11-30
 */
@Data
@ApiModel(value="UserAgreementQueryVo对象", description="用户同意书查询参数")
public class UserAgreementQueryVo implements Serializable{
    private static final long serialVersionUID = 1L;

@ApiModelProperty(value = "主键")
private Integer id;

@ApiModelProperty(value = "用户id")
private Integer uid;

@ApiModelProperty(value = "患者姓名")
private String userName;

@ApiModelProperty(value = "患者手机号")
private String userPhone;

@ApiModelProperty(value = "签名请求ID")
private String requestId;

@ApiModelProperty(value = "签名ID")
private String signFlowId;

@ApiModelProperty(value = "签名的pdf地址")
private String signFilePath;

@ApiModelProperty(value = "是否已经签名 0否 1是")
private Integer status;

@ApiModelProperty(value = "记录生成时间")
private Date createTime;

@ApiModelProperty(value = "记录更新时间")
private Date updateTime;

    @ApiModelProperty(value = "订单缓存key")
    private String orderKey;

    @ApiModelProperty(value = "订单编号")
    private String orderNo;

}