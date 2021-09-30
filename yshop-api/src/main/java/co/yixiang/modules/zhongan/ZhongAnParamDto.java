package co.yixiang.modules.zhongan;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class ZhongAnParamDto {
    // 项目编码
    @NotBlank(message="项目编码projectCode不能为空")
    private String projectCode="";

    // 手机号
    @NotBlank(message="手机号cardType不能为空")
    private String cardType="";

    // 用户标识
    @NotBlank(message="用户标识cardNumber不能为空")
    private String cardNumber="";

    // 时间戳
    @NotBlank(message="时间戳orderNumber不能为空")
    private String orderNumber="";

    private String pageNum="";

    private String token="";
    private String expiresTime="";
    private String couponNo="";
}
