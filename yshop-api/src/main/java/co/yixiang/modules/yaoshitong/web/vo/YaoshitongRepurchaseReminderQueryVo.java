package co.yixiang.modules.yaoshitong.web.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import java.io.Serializable;

import java.util.Date;

/**
 * <p>
 * 药品复购提醒 查询结果对象
 * </p>
 *
 * @author visa
 * @date 2020-10-21
 */
@Data
@ApiModel(value="YaoshitongRepurchaseReminderQueryVo对象", description="药品复购提醒查询参数")
public class YaoshitongRepurchaseReminderQueryVo implements Serializable{
    private static final long serialVersionUID = 1L;

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
}