package co.yixiang.mp.yiyaobao.domain;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.util.Date;

@Data
@Slf4j
public class Medicine {

    @ApiModelProperty(value = "益药宝平台商品名称")
    private String storeName;

    @ApiModelProperty(value = "益药宝平台商品SKU")
    private String yiyaobaoSku;

    @ApiModelProperty(value = "批准文号")
    private String licenseNumber;

    @ApiModelProperty(value = "通用名")
    private String commonName;

    @ApiModelProperty(value = "英文名")
    private String englishName;

    @ApiModelProperty(value = "汉语拼音名称")
    private String pinyinName;

    @ApiModelProperty(value = "汉语拼音简称")
    private String pinyinShortName;

    @ApiModelProperty(value = "剂型代码")
    private String drugFormCode;

    @ApiModelProperty(value = "剂型(瓶，盒)")
    private String drugForm;

    @ApiModelProperty(value = "规格(如500ml)")
    private String spec;

    @ApiModelProperty(value = "包装")
    private String packages;

    @ApiModelProperty(value = "生产厂家")
    private String manufacturer;

    @ApiModelProperty(value = "药品描述")
    private String description;

    @ApiModelProperty(value = "存储条件(常温/冷藏/阴凉)")
    private String storageCondition;

    @ApiModelProperty(value = "是否基药(0-否；1-是)")
    private Integer isBasic;

    @ApiModelProperty(value = "是否计生(0-否；1-是)")
    private Integer isBirthControl;

    @ApiModelProperty(value = "是否兴奋剂(0-否；1-是)")
    private Integer isStimulant;

    @ApiModelProperty(value = "是否第二类精神药品(0-否；1-是)")
    private Integer isPsychotropic;

    @ApiModelProperty(value = "交易税率(%)")
    private BigDecimal taxRate;

    @ApiModelProperty(value = "单位代码")
    private String unitCode;

    @ApiModelProperty(value = "单位(如：盒)")
    private String unit;

    @ApiModelProperty(value = "包装单位")
    private String packageUnit;

    @ApiModelProperty(value = "单位换算值")
    private Integer unitExchange;

    @ApiModelProperty(value = "是否拆零(0-否；1-是)")
    private Integer isOpenStock;

    @ApiModelProperty(value = "药品长(MM)")
    private Integer medLength;

    @ApiModelProperty(value = "药品宽(MM)")
    private Integer medWidth;

    @ApiModelProperty(value = "药品高(MM)")
    private Integer medHeight;

    @ApiModelProperty(value = "药品毛重(kg)")
    private BigDecimal medGrossWeight;

    @ApiModelProperty(value = "药品体积(单位m3)")
    private BigDecimal medCapacity;

    @ApiModelProperty(value = "中包装数量")
    private Integer mediumAmount;

    @ApiModelProperty(value = "中包装单位代码")
    private String mediumUnitCode;

    @ApiModelProperty(value = "中包装单位名称")
    private String mediumUnitName;

    @ApiModelProperty(value = "中包装长(mm)")
    private Integer mediumLength;

    @ApiModelProperty(value = "中包装宽(mm)")
    private Integer mediumWidth;

    @ApiModelProperty(value = "中包装高(mm)")
    private Integer mediumHeight;

    @ApiModelProperty(value = "中包装重量(kg)")
    private BigDecimal mediumWeight;

    @ApiModelProperty(value = "中包装体积(单位m3)")
    private BigDecimal mediumCapacity;

    @ApiModelProperty(value = "件装数量")
    private Integer largeAmount;

    @ApiModelProperty(value = "件装单位代码")
    private String largeUnitCode;

    @ApiModelProperty(value = "件装单位名称")
    private String largeUnitName;

    @ApiModelProperty(value = "件装长(mm)")
    private Integer largeLength;

    @ApiModelProperty(value = "件装宽(mm)")
    private Integer largeWidth;

    @ApiModelProperty(value = "件装高(mm)")
    private Integer largeHeight;

    @ApiModelProperty(value = "件装重量(kg)")
    private BigDecimal largeWeight;

    @ApiModelProperty(value = "件装体积(单位m3)")
    private BigDecimal largeCapacity;

    @ApiModelProperty(value = "注意事项")
    private String attention;

    @ApiModelProperty(value = "主要成分")
    private String basis;

    @ApiModelProperty(value = "性状")
    private String characters;

    @ApiModelProperty(value = "作用类别")
    private String functionCategory;

    @ApiModelProperty(value = "适应症")
    private String indication;

    @ApiModelProperty(value = "用法用量")
    private String directions;

    @ApiModelProperty(value = "不良反应")
    private String untowardEffect;

    @ApiModelProperty(value = "禁忌")
    private String contraindication;

    @ApiModelProperty(value = "药物相互作用")
    private String drugInteraction;

    @ApiModelProperty(value = "药理作用")
    private String pharmacologicalEffect;

    @ApiModelProperty(value = "贮藏")
    private String storage;

    @ApiModelProperty(value = "执行标准")
    private String standard;

    @ApiModelProperty(value = "生产地址")
    private String productionAddress;

    @ApiModelProperty(value = "电话")
    private String tel;

    @ApiModelProperty(value = "商品产地，比如白云山何济公")
    private String productArea;

    @ApiModelProperty(value = "功能主治")
    private String functionIndication;

    @ApiModelProperty(value = "保质期")
    private String qualityPeriod;

    @ApiModelProperty(value = "进口/国产(0-国产；1-进口)")
    private Integer isImport;

    @ApiModelProperty(value = "经营目录编号")
    private String businessDirectoryCode;

    @ApiModelProperty(value = "类别(蓝帽子健字号、普通营养剂、OTC、处方药)")
    private String category;

    @ApiModelProperty(value = "是否礼盒包装(0-否；1-是)")
    private Integer isGiftBox;

    @ApiModelProperty(value = "药品证照效期")
    private Date licenseDeadline;

    @ApiModelProperty(value = "是否需要厂商授权(0-否；1-是)")
    private Integer isAuthorization;

    @ApiModelProperty(value = "是否含特殊药品复方制剂(0-否；1-是)")
    private Integer isCompoundPreparation;

    @ApiModelProperty(value = "是否冷链(0-否；1-是)")
    private Integer isColdChain;

    @ApiModelProperty(value = "其他名称（SEO）")
    private String seo;

    @ApiModelProperty(value = "孕妇及哺乳妇女用药")
    private String pregnancyLactationDirections;

    @ApiModelProperty(value = "儿童用药")
    private String childrenDirections;

    @ApiModelProperty(value = "老年用药")
    private String elderlyPatientDirections;

    @ApiModelProperty(value = "适用人群")
    private String applyCrowdDesc;

    @ApiModelProperty(value = "适用人群代码")
    private String applyCrowdCode;

    @ApiModelProperty(value = "药代动力学")
    private String phamacokinetics;

    @ApiModelProperty(value = "药物过量")
    private String overdosage;

    @ApiModelProperty(value = "临床试验")
    private String clinicalTest;

    @ApiModelProperty(value = "使用单位,如片/粒/次")
    private String useUnit;

    @ApiModelProperty(value = "药理毒理")
    private String pharmacologyToxicology;

    @ApiModelProperty(value = "是否异型产品")
    private Integer isHeterotype;

    @ApiModelProperty(value = "资质证书图片ID")
    private String certImagId;

    @ApiModelProperty(value = "用药周期（天）")
    private String medicationCycle;

    @ApiModelProperty(value = "病种id，多个时用英文逗号分隔")
    private String diseaseId;

    @ApiModelProperty(value = "药品分类，0/普通商品 1/OTC，2/RX处方药，3/RX特药")
    private Integer type;

    @ApiModelProperty(value = "图片")
    private String image;


    /** 轮播图 */
    @ApiModelProperty(value = "轮播图")
    private String sliderImage;
}
