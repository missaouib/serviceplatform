package co.yixiang.modules.shop.web.dto;

import co.yixiang.modules.shop.entity.YxSystemStore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@ApiModel(value = "Data4ProjectDTO对象", description = "特定项目数据对象")
public class Data4ProjectDTO  implements Serializable {

    // 药房列表
    @ApiModelProperty(value = "药房列表")
    private List<Store4ProjectDTO> storeList = new ArrayList<>();
    // 电话联系
    @ApiModelProperty(value = "项目联系电话")
    private String phone;
    // 在线客服联系
    @ApiModelProperty(value = "在线客服组Id")
    private String serviceGroupId;

    @ApiModelProperty(value = "项目介绍")
    private String desc;

    @ApiModelProperty(value = "项目备注")
    private String remark;

    @ApiModelProperty(value = "项目名称")
    private String projectName;
    @ApiModelProperty(value = "支付方式")
    private String payType;
}
