package co.yixiang.modules.gjp.Lib.vo;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@Data
public class SelfbuiltmallproductVo {
    private String productname;
    private String numid;
    private String outerid;
    private String picurl;
    private float price;
    private Integer stockstatus;
    private List<SelfbuiltmallproductskuVo>  skus;
}
