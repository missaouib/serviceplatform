package co.yixiang.modules.taibao.service.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;

/**
 * @Description: claim_material
 * @Author: jeecg-boot
 * @Date: 2021-04-23
 * @Version: V1.0
 */
@XmlType(propOrder = {"code", "isflag"})
@XmlRootElement(name = "material")
@ApiModel(value = "material", description = "material")
public class ClaimMaterialVo implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "资料代码")
    private String code;

    @ApiModelProperty(value = "是否原件")
    private String isflag;

    public String getCode() {
        return code;
    }

    @XmlElement(name = "code")
    public void setCode(String code) {
        this.code = code;
    }

    public String getIsflag() {
        return isflag;
    }

    @XmlElement(name = "isflag")
    public void setIsflag(String isflag) {
        this.isflag = isflag;
    }

    public ClaimMaterialVo() {
        this.code = "";
        this.isflag = "";
    }
}
