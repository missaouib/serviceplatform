package co.yixiang.modules.yiyaobao.dto;

import lombok.Data;

@Data
public class OrderResultDTO {
    private String status;
    private String msg;
    private String token;
    private String data;
}
