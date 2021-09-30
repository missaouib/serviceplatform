package co.yixiang.modules.yaolian.service.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
public class Drug implements Serializable{
    private Integer id;
    private Timestamp createTime;
    private Timestamp updateTime;
    private String name;
    private String number;
    private BigDecimal price;
    private String pack;
    private String manufacturer;
    private String form;
    private Integer groupId;
    private Integer quantity;
    private String type;
    private Integer status;
    private String barCode;
    private String commonName;
    private Integer ignoreStock;
}
