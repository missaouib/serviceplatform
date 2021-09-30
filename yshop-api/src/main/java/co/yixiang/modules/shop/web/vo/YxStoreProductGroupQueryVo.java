package co.yixiang.modules.shop.web.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import java.io.Serializable;

import java.math.BigDecimal;
import java.util.Date;

/**
 * <p>
 * 商品组合 查询结果对象
 * </p>
 *
 * @author visa
 * @date 2021-08-19
 */
@Data
@ApiModel(value="YxStoreProductGroupQueryVo对象", description="商品组合查询参数")
public class YxStoreProductGroupQueryVo implements Serializable{
    private static final long serialVersionUID = 1L;

@ApiModelProperty(value = "主键")
private Integer id;

@ApiModelProperty(value = "父商品sku")
private String parentProductYiyaobaoSku;

@ApiModelProperty(value = "父商品id")
private Integer parentProductId;

@ApiModelProperty(value = "商品sku")
private String productYiyaobaoSku;

@ApiModelProperty(value = "商品id")
private Integer productId;

@ApiModelProperty(value = "默认销售数量")
private Integer num;

@ApiModelProperty(value = "销售单价")
private BigDecimal unitPrice;

private Date createTime;

private Date updateTime;

@ApiModelProperty(value = "是否删除")
private Boolean isDel;

@ApiModelProperty(value = "药品门店唯一码")
private String productUnique;

    @ApiModelProperty(value = "商品名称")
    private String storeName;

    @ApiModelProperty(value = "通用名")
    private String commonName;

    @ApiModelProperty(value = "规格(如500ml)")
    private String spec;

    @ApiModelProperty(value = "单位(如：盒)")
    private String unit;

    @ApiModelProperty(value = "剂型(瓶，盒)")
    private String drugForm;

    @ApiModelProperty(value = "生产厂家")
    private String manufacturer;

    @ApiModelProperty(value = "商品图片")
    private String image;

    @ApiModelProperty(value = "轮播图")
    private String sliderImage;

    @ApiModelProperty(value = "批准文号")
    private String licenseNumber;

    @ApiModelProperty(value = "存储条件(常温/冷藏/阴凉)")
    private String storageCondition;

    @ApiModelProperty(value = "适应症")
    private String indication;

    @ApiModelProperty(value = "保质期")
    private String qualityPeriod;

    @ApiModelProperty(value = "禁忌")
    private String contraindication;

    @ApiModelProperty(value = "益药宝平台商品ID")
    private String yiyaobaoSku;

    @ApiModelProperty(value = "孕妇及哺乳妇女用药")
    private String pregnancyLactationDirections;

    @ApiModelProperty(value = "儿童用药")
    private String childrenDirections;

    @ApiModelProperty(value = "老年用药")
    private String elderlyPatientDirections;

    @ApiModelProperty(value = "药品分类，1/OTC，2/RX处方药，3/RX特药")
    private String type;

}