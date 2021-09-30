package co.yixiang.modules.yaolian.service.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class StoreReqStores implements Serializable{
    private String id;
    private String create_time;
    private String update_time;
    private String number;
    private String name;
    private String address;
    private String province;
    private String city;
    private String area;
    private String longitude;
    private String latitude;
    private String phone;
    private String group_id;
    private String is_dtp;
    private String business_time;
    private String status;
    private String channel;
}
