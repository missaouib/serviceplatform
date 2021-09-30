package co.yixiang.modules.taibao.service.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

/**
 * @Description: claim_add_material
 * @Author: jeecg-boot
 * @Date:   2021-04-23
 * @Version: V1.0
 */
@XmlRootElement(name = "material")
@ApiModel(value="material", description="material")
public class ClaimAddMaterialVo implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "资料代码")
    private String code;
    @ApiModelProperty(value = "资料名称")
    private String name;
    @ApiModelProperty(value = "申请日期")
    private String applydate;
    @ApiModelProperty(value = "反馈日期")
    private String backdate;
    @ApiModelProperty(value = "审核员")
    private String emp;

    public ClaimAddMaterialVo() {
        this.code = "";
        this.name ="";
        this.applydate = "";
        this.backdate = "";
        this.emp = "";
    }

    public String getCode() {
        return code;
    }
    @XmlElement(name = "code")
    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }
    @XmlElement(name = "name")
    public void setName(String name) {
        this.name = name;
    }

    public String getApplydate() {
        return applydate;
    }
    @XmlElement(name = "applydate")
    public void setApplydate(String applydate) {
        this.applydate = applydate;
    }

    public String getBackdate() {
        return backdate;
    }
    @XmlElement(name = "backdate")
    public void setBackdate(String backdate) {
        this.backdate = backdate;
    }

    public String getEmp() {
        return emp;
    }
    @XmlElement(name = "emp")
    public void setEmp(String emp) {
        this.emp = emp;
    }
}
