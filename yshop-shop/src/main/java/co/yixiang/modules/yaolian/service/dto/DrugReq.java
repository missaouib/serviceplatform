package co.yixiang.modules.yaolian.service.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class DrugReq implements Serializable{
    private String id;
    private String create_time;
    private String update_time;
    private String name;
    private String code;
    private String number;
    private String price;
    private String member_price;
    private String pack;
    private String manufacturer;
    private String form;
    private String group_id;
    private String is_dtp;
    private String status;
    private String channel;
    private List<String> category_id;
    private List<String> tags;
}
