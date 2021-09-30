package co.yixiang.modules.yiyaobao.web.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Map;

@Data
@ApiModel(value="StoreCartVo对象", description="多门店购物车查询参数")
public class StoreCartVo  implements Serializable {
    @ApiModelProperty(value = "药店Id")
    private Integer storeId;
    @ApiModelProperty(value = "药店名称")
    private String storeName;
    @ApiModelProperty(value = "药品信息")
    private  Map<String, Object> info;
}
