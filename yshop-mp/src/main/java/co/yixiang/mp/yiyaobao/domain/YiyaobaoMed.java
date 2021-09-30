package co.yixiang.mp.yiyaobao.domain;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.mapstruct.ap.internal.conversion.BigDecimalToBigIntegerConversion;

import java.math.BigDecimal;

@Data
public class YiyaobaoMed {
    @ApiModelProperty(value = "规格(如500ml)")
    private String spec = "";
    private String commonName = "";
    private BigDecimal taxRate = new BigDecimal(0);
    /* 1 停用 0 启用*/
    private Integer status = 0;
    private String filePath = "";
    private String indication = "";
    private String applyCrowdDesc = "";
    private String sku = "";
    private String drugForm = "";
    private String directions = "";
    private String contraindication = "";
    private String unit = "";
    private String category = "";
    private String licenseNumber = "";
    private String medName = "";
    @ApiModelProperty(value = "存储条件(常温/冷藏/阴凉)")
    private String storageCondition = "";
    private String attention = "";
    private String medicationCycle = "";
    private String qualityPeriod = "";
    private String storage = "";
    @ApiModelProperty(value = "生产厂家")
    private String manufacturer = "";
    private BigDecimal price= new BigDecimal(0);
    private String mainFilePath = "";


    @ApiModelProperty(value = "不良反应")
    private String untowardEffect;
    @ApiModelProperty(value = "药物相互作用")
    private String drugInteraction;
    @ApiModelProperty(value = "功能主治")
    private String functionIndication;
    @ApiModelProperty(value = "主要成分")
    private String basis;
    @ApiModelProperty(value = "性状")
    private String characters;

    @ApiModelProperty(value = "孕妇及哺乳妇女用药")
    private String pregnancyLactationDirections;

    @ApiModelProperty(value = "儿童用药")
    private String childrenDirections;

    @ApiModelProperty(value = "老年用药")
    private String elderlyPatientDirections;
    @ApiModelProperty(value = "是否删除 1是 0否")
    private Integer isDelete;
    private String medPartnerMedicineId;
}
