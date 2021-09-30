package co.yixiang.modules.yaoshitong.service.dto;

import lombok.Data;

@Data
public class SalesInfoDto {

    private Integer qty;
    private String provinceName;
    private String cityName;
    private String districtName;
    private String address;
    private String receiver;
    private String mobile;
}
