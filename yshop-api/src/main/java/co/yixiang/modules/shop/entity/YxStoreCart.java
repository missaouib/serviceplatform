package co.yixiang.modules.shop.entity;

import co.yixiang.common.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 购物车表
 * </p>
 *
 * @author hupeng
 * @since 2019-10-25
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "YxStoreCart对象", description = "购物车表")
public class YxStoreCart extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "购物车表ID")
    @TableId(value = "id", type = IdType.AUTO)
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

    @ApiModelProperty(value = "0 = 未购买 1 = 已购买")
    private Integer isPay;

    @ApiModelProperty(value = "是否删除")
    private Integer isDel;

    @ApiModelProperty(value = "是否为立即购买 0 否，1 是")
    private Integer isNew;

    @ApiModelProperty(value = "拼团id")
    private Integer combinationId;

    @ApiModelProperty(value = "秒杀产品ID")
    private Integer seckillId;

    @ApiModelProperty(value = "砍价id")
    private Integer bargainId;

    @ApiModelProperty(value = "药店id")
    private Integer storeId;

    @ApiModelProperty(value = "科室id")
    private Integer departmentId;

    @ApiModelProperty(value = "合作伙伴id")
    private String partnerId;

    @ApiModelProperty(value = "医生id")
    private Integer doctorId;
    @ApiModelProperty(value = "是否内购 0/否 1/是")
    private Integer isInner;


    @ApiModelProperty(value = "项目编码")
    private String projectCode;

    @ApiModelProperty(value = "推荐人编码")
    private String refereeCode;

    @ApiModelProperty(value = "合作伙伴编码")
    private String partnerCode;

    @ApiModelProperty(value = "科室编码")
    private String departCode;
}
