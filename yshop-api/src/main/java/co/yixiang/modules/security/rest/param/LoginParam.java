package co.yixiang.modules.security.rest.param;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @ClassName LoginParam
 * @Author hupeng <610796224@qq.com>
 * @Date 2020/01/15
 **/
@Data
public class LoginParam {
    @NotBlank(message = "code参数缺失")
    private String code;

    private String spread;

    private String encryptedData;

    private String iv;

    // 项目代码
    private String projectCode;

    // 太平卡类型
    private String cardType;

    //太平卡号
    private String cardNumber;


}
