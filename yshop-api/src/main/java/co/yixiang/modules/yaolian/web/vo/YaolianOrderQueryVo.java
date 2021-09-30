package co.yixiang.modules.yaolian.web.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import java.io.Serializable;

import java.util.Date;

/**
 * <p>
 * 药联订单表 查询结果对象
 * </p>
 *
 * @author visa
 * @date 2021-03-05
 */
@Data
@ApiModel(value="YaolianOrderQueryVo对象", description="药联订单表查询参数")
public class YaolianOrderQueryVo implements Serializable{
    private static final long serialVersionUID = 1L;

@ApiModelProperty(value = "交易流水号(即药联订单号)")
private String id;

@ApiModelProperty(value = "药联下单时间")
private Date createTime;

@ApiModelProperty(value = "门店内码")
private String storeId;

@ApiModelProperty(value = "会员ID（需要配合会员数据录入接口实现）")
private String memberId;

@ApiModelProperty(value = "店员手机号")
private String assistantMobile;

@ApiModelProperty(value = "店员工号")
private String assistantNumber;

@ApiModelProperty(value = "订单总价")
private String totalPrice;

@ApiModelProperty(value = "药联直付金额")
private String freePrice;

@ApiModelProperty(value = "顾客自付金额")
private String salePrice;

@ApiModelProperty(value = "超级会员日订单标示")
    @TableField("isSuper")
private String isSuper;

@ApiModelProperty(value = "订单是否有处方单标示，1是存在处方单，0是没有")
private String isPrescription;

@ApiModelProperty(value = "处方单流水号")
private String rxId;

@ApiModelProperty(value = "益药宝订单id")
private String yiyaobaoOrderId;

@ApiModelProperty(value = "益药宝订单号")
private String yiyaobaoOrderNo;

@ApiModelProperty(value = "是否已经下发至益药宝平台 0/否， 1/是")
private Integer uploadYiyaobaoFlag;

@ApiModelProperty(value = "下发至益药宝平台的时间")
private Date uploadYiyaobaoTime;

@ApiModelProperty(value = "省份")
private String provinceName;

@ApiModelProperty(value = "城市")
private String cityName;

@ApiModelProperty(value = "区县")
private String districtName;

@ApiModelProperty(value = "地址")
private String address;

@ApiModelProperty(value = "收货人")
private String receiver;

@ApiModelProperty(value = "收货人电话")
private String receiverPhone;

@ApiModelProperty(value = "处方图片")
private String image;

@ApiModelProperty(value = "患者名称")
private String patientName;

@ApiModelProperty(value = "患者电话")
private String patientPhone;

}