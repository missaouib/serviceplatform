package co.yixiang.modules.gjp.Lib.vo;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Data
@Slf4j
public class Eshopsaleorderdetail {
    private String oid;
    private Integer qty;
    private List<String> serials;
}
