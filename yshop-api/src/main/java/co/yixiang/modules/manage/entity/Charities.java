package co.yixiang.modules.manage.entity;

import co.yixiang.modules.shop.entity.YxSystemStore;
import com.baomidou.mybatisplus.annotation.IdType;
import co.yixiang.common.entity.BaseEntity;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * 慈善活动表
 * </p>
 *
 * @author visa
 * @since 2020-08-20
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value="Charities对象", description="慈善活动表")
public class Charities extends BaseEntity {

    private static final long serialVersionUID = 1L;

@ApiModelProperty(value = "主键")
@TableId(value = "id", type = IdType.AUTO)
private Integer id;

@ApiModelProperty(value = "编号")
private Integer code;

@ApiModelProperty(value = "药房名称")
private String drugstoreName;

@ApiModelProperty(value = "项目名称")
private String projectName;

@ApiModelProperty(value = "基金会名称")
private String foundationsName;

@ApiModelProperty(value = "电话，多个用逗号分隔")
private String phone;

@ApiModelProperty(value = "药品名称")
private String productName;

@ApiModelProperty(value = "药品通用名")
private String commonName;

@ApiModelProperty(value = "剂型")
private String drugForm;

@ApiModelProperty(value = "规格")
private String spec;

@ApiModelProperty(value = "生产厂商")
private String manufacturer;

@ApiModelProperty(value = "药品发放时段")
private String timeInterval;

@ApiModelProperty(value = "项目展示网址")
private String projectWeburl;

@ApiModelProperty(value = "热线电话")
private String hotlinePhone;

@ApiModelProperty(value = "电子邮件")
private String email;

@ApiModelProperty(value = "资料邮寄地址")
private String mailAddress;

private Date createTime;

private Date updateTime;

@TableField(exist = false)
    private List<YxSystemStore> drugstoreList;
    private String image;
}
