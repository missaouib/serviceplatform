package co.yixiang.modules.xikang.dto;

import lombok.Data;

@Data
public class XkExpress {
    private String supplierCode;
    private String supplierName;
    private String prescriptionCode;
    private String allocateeName;
    private String allocateeDatetime;
    //快递公司编码
    private String expressCompanyCode;
    //快递公司名称
    private String expressCompanyName;
    //快递编号
    private String expressCode;

}
