package co.yixiang.modules.user.web.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @ClassName UserEditParam
 * @Author hupeng <610796224@qq.com>
 * @Date 2020/02/07
 **/
@Data
public class UserEditParam implements Serializable {
    @NotBlank(message = "请上传头像")
    private String avatar;
    @NotBlank(message = "请填写昵称")
    private String nickname;
    private String innerEmployeeCode;
    private String repurchaseReminderFlag;
    private String realName;

    @ApiModelProperty(value = "身份证号码")
    private String cardId;

    @ApiModelProperty(value = "手机号")
    private String phone;

    @ApiModelProperty(value = "是否vip 0否 1是")
    private Integer vipFlag;

    @ApiModelProperty(value = "性别 1/男 2/女 0/未知")
    private Integer sex;


}
