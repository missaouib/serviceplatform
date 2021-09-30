package co.yixiang.modules.shop.domain;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.Date;

@Data
public class OrderUserInfo {
    // 购药人id
    private Integer uid;

    // 购药人真实姓名
    private String realName;

    // 购药人真实电话
    private String phone;

    /** 购药人身份证号码 */
    private String cardId;


    private Integer addressId;
    /** 收货人姓名 */
    private String addressRealName;


    /** 收货人电话 */

    private String addressPhone;


    /** 收货人所在省 */

    private String provinceName;


    /** 收货人所在市 */

    private String cityName;


    /** 收货人所在区 */

    private String districtName;


    /** 收货人详细地址 */

    private String address;

    /** 药店id */
    private Integer storeId;

    @ApiModelProperty(value = "服务药店Id")
    private Integer serviceDrugstoreId;

    /*订单编号*/
    private String orderId;

    private String imagePath;

    @ApiModelProperty(value = "用药人id")
    private Integer drugUserId;

    @ApiModelProperty(value = "用药人姓名")
    private String drugUserName;

    @ApiModelProperty(value = "用药人手机号")
    private String drugUserPhone;

    @ApiModelProperty(value = "用药人身份证号")
    private String drugUserIdcard;

    @ApiModelProperty(value = "用药人出身年月")
    private String drugUserBirth;

    @ApiModelProperty(value = "用药人体重")
    private String drugUserWeight;

    @ApiModelProperty(value = "用药人性别")
    private String drugUserSex;

    @ApiModelProperty(value = "用药人类型 1/成人 2/儿童")
    private Integer drugUserType;

    @ApiModelProperty(value = "云配液收件地址")
    private String cloudProduceAddress;

    @ApiModelProperty(value = "预计收货日期")
    private Date expectedReceivingDate;

    @ApiModelProperty(value = "付款方名称")
    private String payerAccountName;

    @ApiModelProperty(value = "收货地址类型 1 医院 2 药房 3 其他")
    private String addressType;

}
