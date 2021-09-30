package co.yixiang.modules.shop.entity;

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
 * 项目
 * </p>
 *
 * @author visa
 * @since 2021-03-05
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value="Project对象", description="项目")
public class Project extends BaseEntity {

    private static final long serialVersionUID = 1L;

@TableId(value = "id", type = IdType.AUTO)
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


@ApiModelProperty(value = "1-同步广州店的药品数据")
private String flag;

    /** 满多少金额包邮，单位元 */
    @ApiModelProperty(value = "满多少金额包邮，单位元")
    private Integer freePostage;
    /** 物流运费模板id */
    @ApiModelProperty(value = "物流运费模板id,多个用英文逗号分隔")
    private String expressTemplateId;
    @ApiModelProperty(value = "口令牌")
    private String token;
    @ApiModelProperty(value = "商户配置名称")
    private String mchName;
    @ApiModelProperty(value = "药店id")
    private String storeIds;

    @ApiModelProperty(value = "站点信息")
    private String siteInfo;


    @ApiModelProperty(value = " 是否广州店的项目 0 否 1 是")
    private String guangzhouFlag;
    @ApiModelProperty(value = " 是否开启互联网医院获取处方 Y/N")
    private String needInternetHospital;

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
