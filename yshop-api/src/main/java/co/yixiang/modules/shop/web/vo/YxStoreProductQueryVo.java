package co.yixiang.modules.shop.web.vo;

import cn.hutool.core.util.StrUtil;
import co.yixiang.modules.shop.entity.MdPharmacistService;
import co.yixiang.modules.shop.entity.YxStoreProductAttrValue;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Date;
/**
 * <p>
 * 商品表 查询结果对象
 * </p>
 *
 * @author hupeng
 * @date 2019-10-19
 */
@Data
@ApiModel(value = "YxStoreProductQueryVo对象", description = "商品表查询参数")
public class YxStoreProductQueryVo implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "商品id")
    private Integer id;

    @ApiModelProperty(value = "商户Id(0为总后台管理员创建,不为0的时候是商户后台创建)")
    private Integer merId;

    @ApiModelProperty(value = "商品图片")
    private String image;

    private String image_base;

    private String codeBase;

    public String getImage_base() {
        return image;
    }

    private Boolean userCollect = false;

    private Boolean userLike = false;

    @ApiModelProperty(value = "轮播图")
    private String sliderImage;

    private List<String> sliderImageArr;

    public List<String> getSliderImageArr() {
        //Arrays.asList(sliderImage.split(","));
        if(StrUtil.isNotEmpty(sliderImage)){
            return Arrays.asList(sliderImage.split(","));
        }

        return new ArrayList<>();

    }

    private YxStoreProductAttrValue attrInfo;

    @ApiModelProperty(value = "商品名称")
    private String storeName;

    @ApiModelProperty(value = "商品简介")
    private String storeInfo;

    @ApiModelProperty(value = "关键字")
    private String keyword;

    @ApiModelProperty(value = "产品条码（一维码）")
    private String barCode;

    @ApiModelProperty(value = "分类id")
    private String cateId;

    @ApiModelProperty(value = "商品价格")
    private BigDecimal price;

    @ApiModelProperty(value = "会员价格")
    private BigDecimal vipPrice;

    @ApiModelProperty(value = "市场价")
    private BigDecimal otPrice;

    @ApiModelProperty(value = "邮费")
    private BigDecimal postage;

    @ApiModelProperty(value = "单位名")
    private String unitName;

    @ApiModelProperty(value = "排序")
    private Integer sort;

    @ApiModelProperty(value = "销量")
    private Integer sales;

    @ApiModelProperty(value = "库存")
    private Integer stock;

    @ApiModelProperty(value = "状态（0：未上架，1：上架）")
    private Integer isShow;

    @ApiModelProperty(value = "是否热卖")
    private Integer isHot;

    @ApiModelProperty(value = "是否优惠")
    private Integer isBenefit;

    @ApiModelProperty(value = "是否精品")
    private Integer isBest;

    @ApiModelProperty(value = "是否新品")
    private Integer isNew;

    @ApiModelProperty(value = "产品描述")
    private String description;

    @ApiModelProperty(value = "添加时间")
    private Integer addTime;

    @ApiModelProperty(value = "是否包邮")
    private Integer isPostage;

    @ApiModelProperty(value = "是否删除")
    private Integer isDel;

    @ApiModelProperty(value = "商户是否代理 0不可代理1可代理")
    private Integer merUse;

    @ApiModelProperty(value = "获得积分")
    private BigDecimal giveIntegral;

    @ApiModelProperty(value = "成本价")
    private BigDecimal cost;

    @ApiModelProperty(value = "秒杀状态 0 未开启 1已开启")
    private Integer isSeckill;

    @ApiModelProperty(value = "砍价状态 0未开启 1开启")
    private Integer isBargain;

    @ApiModelProperty(value = "是否优品推荐")
    private Integer isGood;

    @ApiModelProperty(value = "虚拟销量")
    private Integer ficti;

    @ApiModelProperty(value = "浏览量")
    private Integer browse;

    @ApiModelProperty(value = "产品二维码地址(用户小程序海报)")
    private String codePath;

    @ApiModelProperty(value = "淘宝京东1688类型")
    private String soureLink;


    @ApiModelProperty(value = "益药宝平台商品ID")
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

    @ApiModelProperty(value = "药店名称")
    private String storeNameReal;

    @ApiModelProperty(value = "药店Id")
    private Integer storeIdReal;

    @ApiModelProperty(value = "最近药店的距离")
    private String distance;

    private List<YxSystemStoreQueryVo> storeList;

    @ApiModelProperty(value = "内购价")
    private BigDecimal innerPrice;

    @ApiModelProperty(value = "是否内购人员 0/否 ，1/是")
    private Integer isInner;

    private String unique;

    @ApiModelProperty(value = "药品分类，1/OTC，2/RX处方药，3/RX特药")
    private String type;

    private YxStoreProductReplyQueryVo reply;

    private String replyChance;

    private Integer replyCount = 0;

    @ApiModelProperty(value = "价格区间,最低价")

    private BigDecimal priceMin;

    @ApiModelProperty(value = "价格区间,最高价")

    private BigDecimal priceMax;

    @ApiModelProperty(value = "药品标签1")
    private String label1;

    @ApiModelProperty(value = "药品标签2")
    private String label2;

    @ApiModelProperty(value = "药品标签3")
    private String label3;

    @ApiModelProperty(value = "药品会员等级")
    private String userLevel;

    @ApiModelProperty(value = "是否需要云配液")
    private Integer isNeedCloudProduce;

    @ApiModelProperty(value = "药师列表")
    private List<MdPharmacistService> pharmacists;

    @ApiModelProperty(value = "福利说明")
    private List<String> benefitsDesc;

    @ApiModelProperty(value = "折扣率")
    private BigDecimal discount;

    @ApiModelProperty(value = "购药流程简图")
    private String flowImagePath;

    @ApiModelProperty(value = "客服组Id")
    private String serviceGroupId;

    private Integer isGroup;

    @ApiModelProperty(value = "组合中的子商品")
    private List<YxStoreProductGroupQueryVo> groupDetailList;


}