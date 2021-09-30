package co.yixiang.modules.yaolian.service.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class OrderInfoDetaiReq implements Serializable{
    private String drug_id;
    private String common_name;
    private String number;
    private String form;
    private String amount;
    private String pack;
    private String price;
    private String unit_price;
    private String code;
    private String batch_number;
    private String expirydate;
}
