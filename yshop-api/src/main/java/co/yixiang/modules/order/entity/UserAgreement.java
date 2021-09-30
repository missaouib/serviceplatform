package co.yixiang.modules.order.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import co.yixiang.common.entity.BaseEntity;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * <p>
 * 用户同意书
 * </p>
 *
 * @author visa
 * @since 2020-11-30
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value="UserAgreement对象", description="用户同意书")
public class UserAgreement extends BaseEntity {

    private static final long serialVersionUID = 1L;

@ApiModelProperty(value = "主键")
@TableId(value = "id", type = IdType.AUTO)
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
