package co.yixiang.modules.shop.web.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.io.Serializable;

import java.util.Date;

/**
 * <p>
 * 药师在线配置表 查询结果对象
 * </p>
 *
 * @author visazhou
 * @date 2020-06-09
 */
@Data
@ApiModel(value="MdPharmacistServiceQueryVo对象", description="药师在线配置表查询参数")
public class MdPharmacistServiceQueryVo implements Serializable{
    private static final long serialVersionUID = 1L;

@ApiModelProperty(value = "标识")
private String id;

@ApiModelProperty(value = "状态(0-停用；1-启用)")
@TableField("STATUS")
private Long status;

@ApiModelProperty(value = "是否在线(0-否；1-是)")
    @TableField("ONLINE")
private Long online;

@ApiModelProperty(value = "是否默认(0-否；1-是)")
    @TableField("IS_DEFALUT")
private Long isDefalut;

@ApiModelProperty(value = "来源(01-药房；02-医院)")
    @TableField("SOURCE")
private String source;

@ApiModelProperty(value = "外键ID(如果是医院，则记录医院ID；如果是药房，则记录药房ID)")
    @TableField("FOREIGN_ID")
private String foreignId;

@ApiModelProperty(value = "姓名")
    @TableField("NAME")
private String name;

@ApiModelProperty(value = "性别(0-女;1-男)")
    @TableField("SEX")
private Long sex;

@ApiModelProperty(value = "药师执业证编号")
    @TableField("CERTIFICATE_NO")
private String certificateNo;

@ApiModelProperty(value = "药师简介")
    @TableField("DESCRIPTION")
private String description;

@ApiModelProperty(value = "客服账号")
    @TableField("CUSTOMER_SERVICE_ACCOUNT")
private String customerServiceAccount;

@ApiModelProperty(value = "客服组")
    @TableField("CUSTOMER_SERVICE_GROUP")
private String customerServiceGroup;

@ApiModelProperty(value = "备注")
    @TableField("REMARK")
private String remark;

@ApiModelProperty(value = "创建人")
    @TableField("CREATE_USER")
private String createUser;

@ApiModelProperty(value = "创建时间")
    @TableField("CREATE_TIME")
private Date createTime;

@ApiModelProperty(value = "更新人")
    @TableField("UPDATE_USER")
private String updateUser;

@ApiModelProperty(value = "更新时间")
    @TableField("UPDATE_TIME")
private Date updateTime;

@ApiModelProperty(value = "药房/医院名称")
    @TableField("FOREIGN_NAME")
private String foreignName;

}