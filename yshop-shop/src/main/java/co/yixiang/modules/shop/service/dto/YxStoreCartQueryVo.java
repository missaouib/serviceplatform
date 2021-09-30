package co.yixiang.modules.shop.service.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * <p>
 * 购物车表 查询结果对象
 * </p>
 *
 * @author hupeng
 * @date 2019-10-25
 */
@Data
@ApiModel(value = "YxStoreCartQueryVo对象", description = "购物车表查询参数")
public class YxStoreCartQueryVo implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "购物车表ID")
    private Long id;

    @ApiModelProperty(value = "用户ID")
    private Integer uid;

    @ApiModelProperty(value = "类型")
    private String type;

    @ApiModelProperty(value = "商品ID")
    private Integer productId;

    @ApiModelProperty(value = "商品属性")
    private String productAttrUnique;

    @ApiModelProperty(value = "商品数量")
    private Integer cartNum;

    @ApiModelProperty(value = "添加时间")
    private Integer addTime;

    @ApiModelProperty(value = "拼团id")
    private Integer combinationId;

    @ApiModelProperty(value = "秒杀产品ID")
    private Integer seckillId;

    @ApiModelProperty(value = "砍价id")
    private Integer bargainId;

    private YxStoreProductQueryVo productInfo;


    private Double costPrice;

    private Double truePrice;

    private Integer trueStock;

    private Double vipTruePrice;

    private String unique;

    private Integer isReply;

    private Integer storeId;

    @ApiModelProperty(value = "内购价")
    private Double innerPrice;

    @ApiModelProperty(value = "是否内购 0/否 ，1/是")
    private Integer isInner;
    @ApiModelProperty(value = "益药宝sku编号")
    private String yiyaobaoSku;

    private String partnerId;

    @ApiModelProperty(value = "项目编码")
    private String projectCode;

    @ApiModelProperty(value = "推荐人编码")
    private String refereeCode;

    @ApiModelProperty(value = "合作伙伴编码")
    private String partnerCode;

    @ApiModelProperty(value = "科室编码")
    private String departCode;

    private Boolean isReplyFlag;

    private String label1;
    private String label2;
    private String label3;
    private BigDecimal discount;

    // 折扣金额
    private BigDecimal discountAmount;
}