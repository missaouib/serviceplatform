package co.yixiang.modules.yiyaobao.web.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value="地图搜索", description="地图搜索")
public class MapQueryVo {
    @ApiModelProperty(value = "关键字")
    private String keyword;
    @ApiModelProperty(value = "区域")
    private String region;
    @ApiModelProperty(value = "地址")
    private String address;

    @ApiModelProperty(value = "纬度")
    private String latitude;
    @ApiModelProperty(value = "经度")
    private String longitude;

}
