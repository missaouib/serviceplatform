package co.yixiang.modules.yaoshitong.entity;

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
 * 药品复购提醒
 * </p>
 *
 * @author visa
 * @since 2020-10-21
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value="YaoshitongRepurchaseReminder对象", description="药品复购提醒")
public class YaoshitongRepurchaseReminder extends BaseEntity {

    private static final long serialVersionUID = 1L;

@TableId(value = "id", type = IdType.AUTO)
private Integer id;

@ApiModelProperty(value = "姓名")
private String name;

@ApiModelProperty(value = "电话")
private String phone;

@ApiModelProperty(value = "药房名称")
private String drugstoreName;

@ApiModelProperty(value = "药房id")
private Integer drugstoreId;

@ApiModelProperty(value = "上次购买日期")
private Date lastPurchaseDate;

@ApiModelProperty(value = "下次购买日期")
private Date nextPurchaseDate;

@ApiModelProperty(value = "药品名称")
private String medName;

@ApiModelProperty(value = "药品id")
private Integer medId;

@ApiModelProperty(value = "药品sku编码")
private String medSku;

@ApiModelProperty(value = "药品通用名")
private String medCommonName;

@ApiModelProperty(value = "药品规格")
private String medSpec;

@ApiModelProperty(value = "药品单位")
private String medUnit;

@ApiModelProperty(value = "药品生产厂家")
private String medManufacturer;

@ApiModelProperty(value = "状态")
private String status;

@ApiModelProperty(value = "首次购药日期")
private Date firstPurchaseDate;

@ApiModelProperty(value = "购药次数")
private Integer purchaseTimes;

@ApiModelProperty(value = "总计购药数量")
private Integer purchaseQty;

@ApiModelProperty(value = "上次购药数量")
private Integer lastPurchasseQty;

@ApiModelProperty(value = "用药周期")
private Integer medCycle;

private Date createTime;

private Date updateTime;

private String image;

    @ApiModelProperty(value = "是否已购买")
private String repurchaseFlag;

    @ApiModelProperty(value = "没购买的原因")
private String repurchaseNoReason;

    @ApiModelProperty(value = "没购买的原因备注信息")
private String repurchaseNoReasonRemark;

    @ApiModelProperty(value = "购买方式")
private String repurchaseYesMethod;

    private String provinceName;

    private String cityName;

    private String districtName;

    private String address;

    private String receiver;

    private String receiverMobile;

}
