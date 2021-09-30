package co.yixiang.modules.xikang.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class UploadPatient {
    private String supplierCode;
    private String supplierName;
    private String patientName;
    private String patientGender;
    private String patientIDCard;
    private String patientTel;
    private String patientType;
    private String requestType;
    private String openId;
    private List<Drugs> drugs = new ArrayList<>();
    private String attrs;
    private String appletsId;
    private String orderId;

}
