package co.yixiang.modules.shop.web.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import java.io.Serializable;

import java.util.Date;

/**
 * <p>
 * 项目 查询结果对象
 * </p>
 *
 * @author visa
 * @date 2021-03-05
 */
@Data
@ApiModel(value="ProjectQueryVo对象", description="项目查询参数")
public class ProjectQueryVo implements Serializable{
    private static final long serialVersionUID = 1L;

private Integer id;

@ApiModelProperty(value = "项目编码")
private String projectCode;

@ApiModelProperty(value = "项目名称")
private String projectName;

@ApiModelProperty(value = "项目简介")
private String projectDesc;

@ApiModelProperty(value = "项目备注")
private String remark;

@ApiModelProperty(value = "项目联系电话")
private String phone;

@ApiModelProperty(value = "在线咨询客服组id")
private String serviceGroupId;

@ApiModelProperty(value = "益药宝项目代码")
private String yiyaobaoProjectCode;

@ApiModelProperty(value = "记录生成时间")
private Date createTime;

@ApiModelProperty(value = "记录更新时间")
private Date updateTime;

@ApiModelProperty(value = "支付方式")
private String payType;

    @ApiModelProperty(value = "商户配置名称")
    private String mchName;

    @ApiModelProperty(value = "支付宝h5AppID")
    private String alipayHfiveAppid;

    @ApiModelProperty(value = "支付宝小程序AppID")
    private String alipayAppletAppid;

    @ApiModelProperty(value = "微信h5Mchid")
    private String wechatHfiveMchid;

    @ApiModelProperty(value = "微信小程序Mchid")
    private String wechatAppletMchid;

    @ApiModelProperty(value = "微信app Mchid")
    private String wechatAppMchid;

    @ApiModelProperty(value = "微信众安 Mchid")
    private String wechatZhonganMchid;

}