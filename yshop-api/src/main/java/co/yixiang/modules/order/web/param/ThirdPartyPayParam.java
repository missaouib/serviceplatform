package co.yixiang.modules.order.web.param;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@Data
public class ThirdPartyPayParam implements Serializable {
    // 众安小程序的openid
    @NotBlank(message="openId不能为空")
    private String openId;

    // 订单编号
    @NotBlank(message="订单编号不能为空")
    private String orderId;

    @NotBlank(message="加密值")
    private String encrypt;
}
