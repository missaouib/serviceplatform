package co.yixiang.modules.manage.web.vo;

import co.yixiang.modules.shop.web.vo.YxStoreProductQueryVo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import java.io.Serializable;

import java.util.Date;

/**
 * <p>
 * 购物车表-项目 查询结果对象
 * </p>
 *
 * @author visa
 * @date 2020-08-24
 */
@Data
@ApiModel(value="YxStoreCartProjectQueryVo对象", description="购物车表-项目查询参数")
public class YxStoreCartProjectQueryVo implements Serializable{
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

@ApiModelProperty(value = "0 = 未购买 1 = 已购买")
private Integer isPay;

@ApiModelProperty(value = "是否删除")
private Integer isDel;

@ApiModelProperty(value = "是否为立即购买")
private Integer isNew;

@ApiModelProperty(value = "拼团id")
private Integer combinationId;

@ApiModelProperty(value = "秒杀产品ID")
private Integer seckillId;

@ApiModelProperty(value = "砍价id")
private Integer bargainId;

@ApiModelProperty(value = "药店id")
private Integer storeId;

@ApiModelProperty(value = "益药宝药品sku")
private String yiyaobaoSku;

@ApiModelProperty(value = "益药宝药店id")
private String yiyaobaoSellerId;

@ApiModelProperty(value = "项目名称")
private String projectName;

@ApiModelProperty(value = "项目编码")
private String projectCode;

@ApiModelProperty(value = "推荐人编码")
private String refereeCode;

@ApiModelProperty(value = "合作伙伴编码")
private String partnerCode;

@ApiModelProperty(value = "医院编码")
private String departCode;

    private YxStoreProductQueryVo productInfo;
    private Double costPrice;

    private Double truePrice;

    private Integer trueStock;

    private Double vipTruePrice;
}