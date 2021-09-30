package co.yixiang.modules.yaolian.service.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class OrderMain implements Serializable{
    private ReqHead requestHead;
    private String channel;
    private List<OrderInfoReq> orders;
}
