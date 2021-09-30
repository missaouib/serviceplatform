package co.yixiang.modules.yaolian.service.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class ReqHead implements Serializable{
    private String cooperation;
    private String nonce;
    private String sign;
    private String timestamp;
    private String tradeDate;
}
