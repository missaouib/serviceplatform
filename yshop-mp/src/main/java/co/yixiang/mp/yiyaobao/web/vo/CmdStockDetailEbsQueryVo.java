package co.yixiang.mp.yiyaobao.web.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.io.Serializable;

import java.math.BigDecimal;
import java.util.Date;

/**
 * <p>
 * 商品库存明细表 查询结果对象
 * </p>
 *
 * @author visazhou
 * @date 2020-06-16
 */
@Data
@ApiModel(value="CmdStockDetailEbsQueryVo对象", description="商品库存明细表查询参数")
public class CmdStockDetailEbsQueryVo implements Serializable{
    private static final long serialVersionUID = 1L;

@ApiModelProperty(value = "ID")
private String id;

@ApiModelProperty(value = "药品ID")
    @TableField("MED_ID")
private String medId;

@ApiModelProperty(value = "平台商品编码SKU")
    @TableField("SKU")
private String sku;

@ApiModelProperty(value = "销售商ID")
    @TableField("SELLER_ID")
private String sellerId;

@ApiModelProperty(value = "销售商编码")
    @TableField("SELLER_CODE")
private String sellerCode;

@ApiModelProperty(value = "仓库ID")
    @TableField("WAREHOUSE_ID")
private String warehouseId;

@ApiModelProperty(value = "仓库编码")
    @TableField("WAREHOUSE_CODE")
private String warehouseCode;

@ApiModelProperty(value = "货架ID")
    @TableField("STORAGE_RACK_ID")
private String storageRackId;

@ApiModelProperty(value = "货架编码")
    @TableField("STORAGE_RACK_CODE")
private String storageRackCode;

@ApiModelProperty(value = "批号")
    @TableField("LOT_NO")
private String lotNo;

@ApiModelProperty(value = "单位")
    @TableField("UNIT")
private String unit;

@ApiModelProperty(value = "在途库存")
    @TableField("PASSAGE_AMOUNT")
private BigDecimal passageAmount;

@ApiModelProperty(value = "锁定库存")
    @TableField("LOCK_AMOUNT")
private BigDecimal lockAmount;

@ApiModelProperty(value = "可用库存")
    @TableField("USABLE_AMOUNT")
private BigDecimal usableAmount;

@ApiModelProperty(value = "库存总量")
    @TableField("TOTAL_AMOUNT")
private BigDecimal totalAmount;

@ApiModelProperty(value = "远效期")
    @TableField("FAR_PERIOD")
private Date farPeriod;

@ApiModelProperty(value = "近效期")
    @TableField("NEAR_PERIOD")
private Date nearPeriod;

@ApiModelProperty(value = "生产日期")
    @TableField("MAKE_DATE")
private Date makeDate;

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

}