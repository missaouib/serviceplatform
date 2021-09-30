package co.yixiang.modules.gjp.Lib.vo;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Data
@Slf4j
public class GjpNotifyDTO {
    private String storeCode;
    private String tradeid;
    private String freightName;
    private String freightCode;
    private String freightNo;
    private List<Eshopsaleorderdetail> eshopSaleOrderDetail;
}
