package co.yixiang.common.web.param;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;


@Data
@ApiModel("url参数")
public class UrlParam implements Serializable {
    private static final long serialVersionUID = -5353973980674510450L;

    @NotBlank(message="url不能为空")
    private String url;
}
