package co.yixiang.modules.yaolian.service.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class OrderInfoReq implements Serializable{
    private String id;
    private String create_time;
    private String update_time;
    private String member_id;
    private String member_name;
    private String store_id;
    private String card_number;
    private String total_price;
    private String ud_card;
    private String order_no;
    private List<OrderInfoDetaiReq> retailDetail;
}
