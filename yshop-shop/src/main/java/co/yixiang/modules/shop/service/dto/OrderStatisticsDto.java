package co.yixiang.modules.shop.service.dto;


import lombok.Data;

@Data
public class OrderStatisticsDto {
    private String statusName;
    private Integer countOrder;
    private Integer countOrder4cs;
}
