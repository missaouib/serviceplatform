package co.yixiang.modules.yaolian.service.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class StockReq implements Serializable{
    private String store_id;
    private String drug_id;
    private String quantity;
    private String create_time;
    private String update_time;
}
