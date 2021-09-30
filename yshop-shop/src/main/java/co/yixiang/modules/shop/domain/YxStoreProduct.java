/**
 * Copyright (C) 2018-2020
 * All rights reserved, Designed By www.yixiang.co

 */
package co.yixiang.modules.shop.domain;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import co.yixiang.modules.shop.service.dto.YxStoreProductGroupDto;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

/**
* @author hupeng
* @date 2020-05-12
*/


@TableName("yx_store_product")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class YxStoreProduct implements Serializable {

    /** 商品id */
    @TableId
    private Integer id;


    /** 商户Id(0为总后台管理员创建,不为0的时候是商户后台创建) */
    private Integer merId;


    /** 商品图片 */

    private String image;


    /** 轮播图 */

    private String sliderImage;


    /** 商品名称 */

    private String storeName;


    /** 商品简介 */
    private String storeInfo;


    /** 关键字 */
    private String keyword;


    /** 产品条码（一维码） */
    private String barCode;


    /** 分类id */
    @ApiModelProperty(value = "科室id，多个时用英文逗号分隔")
    private String cateId;


    /** 商品价格 */

    @DecimalMin(value="0.00", message = "商品价格不在合法范围内" )
    @DecimalMax(value="99999999.99", message = "商品价格不在合法范围内")
    private BigDecimal price = new BigDecimal(0);


    /** 会员价格 */
    private BigDecimal vipPrice;


    /** 市场价 */

    @DecimalMin(value="0.00", message = "市场价不在合法范围内" )
    @DecimalMax(value="99999999.99", message = "市场价不在合法范围内")
    private BigDecimal otPrice;


    /** 邮费 */
    private BigDecimal postage;


    /** 单位名 */

    private String unitName;


    /** 排序 */
    private Integer sort;


    /** 销量 */
    private Integer sales;


    /** 库存 */

    private Integer stock;


    /** 状态（0：未上架，1：上架） */
    private Integer isShow;


    /** 是否热卖 */
    private Integer isHot;


    /** 是否优惠 */
    private Integer isBenefit;


    /** 是否精品 */
    private Integer isBest;


    /** 是否新品 */
    private Integer isNew;


    /** 产品描述 */
   // @NotBlank(message = "请填写商品详情")
    private String description;


    /** 添加时间 */
    private Integer addTime;


    /** 是否包邮 */
    private Integer isPostage;


    /** 是否删除 0 否 1是*/
    private Integer isDel;


    /** 商户是否代理 0不可代理1可代理 */
    private Integer merUse;


    /** 获得积分 */
    @DecimalMin(value="0.00", message = "获得积分不在合法范围内" )
    @DecimalMax(value="99999999.99", message = "获得积分不在合法范围内")
    private BigDecimal giveIntegral = new BigDecimal(0);


    /** 成本价 */

    @DecimalMin(value="0.00", message = "成本价不在合法范围内" )
    @DecimalMax(value="99999999.99", message = "成本价不在合法范围内")
    private BigDecimal cost = new BigDecimal(0);


    /** 秒杀状态 0 未开启 1已开启 */
    private Integer isSeckill;


    /** 砍价状态 0未开启 1开启 */
    private Integer isBargain;


    /** 是否优品推荐 */
    private Integer isGood;


    /** 虚拟销量 */
    private Integer ficti;


    /** 浏览量 */
    private Integer browse;


    /** 产品二维码地址(用户小程序海报) */
    private String codePath;


    /** 淘宝京东1688类型 */
    private String soureLink;

    @TableField(exist = false)
    private YxStoreCategory storeCategory;

    // 是否已经上传至管家婆 0/否 1/是
    private Integer uploadGjpFlag;
    public void copy(YxStoreProduct source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }

    @ApiModelProperty(value = "益药宝平台商品ID")
    private String yiyaobaoSku;

    @ApiModelProperty(value = "批准文号")
    private String licenseNumber = "";

    @ApiModelProperty(value = "通用名")
    private String commonName = "";

    @ApiModelProperty(value = "英文名")
    private String englishName;

    @ApiModelProperty(value = "汉语拼音名称")
    private String pinyinName;

    @ApiModelProperty(value = "汉语拼音简称")
    private String pinyinShortName;

    @ApiModelProperty(value = "剂型代码")
    private String drugFormCode;

    @ApiModelProperty(value = "剂型(瓶，盒)")
    private String drugForm = "";

    @ApiModelProperty(value = "规格(如500ml)")
    private String spec = "";

    @ApiModelProperty(value = "包装")
    private String packages;

    @ApiModelProperty(value = "生产厂家")
    private String manufacturer = "";

    @ApiModelProperty(value = "存储条件(常温/冷藏/阴凉)")
    private String storageCondition = "";

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
    private String unit = "";

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
    private String indication = "";

    @ApiModelProperty(value = "用法用量")
    private String directions;

    @ApiModelProperty(value = "不良反应")
    private String untowardEffect;

    @ApiModelProperty(value = "禁忌")
    private String contraindication = "";

    @ApiModelProperty(value = "药物相互作用")
    private String drugInteraction;

    @ApiModelProperty(value = "药理作用")
    private String pharmacologicalEffect;

    @ApiModelProperty(value = "贮藏")
    private String storage = "";

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
    private String qualityPeriod = "";

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
    private String applyCrowdCode = "";

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
    private String diseaseId = "";

    @ApiModelProperty(value = "药品分类，1/OTC，2/RX处方药，3/RX特药  00/RX肿瘤 07/肿瘤 01/处方药 02/OTC甲类 03/OTC乙类  04/营养保健 05/食品  06/其他")
    private String type = "";

    @ApiModelProperty(value = "病种大分类id，多个时用英文逗号分隔")
    private String diseaseParentId = "";

    @ApiModelProperty(value = "在云药房的病种id，多个时用英文逗号分隔")
    private String diseaseIdCloud;
    @ApiModelProperty(value = "普通门店的病种id，多个时用英文逗号分隔")
    private String diseaseIdCommon = "";

    @ApiModelProperty(value = "太平项目85折药品标记")
    private String label1 = "";

    @ApiModelProperty(value = "太平项目88折标记")
    private String label2 = "";

    @ApiModelProperty(value = "太平项目5折标记")
    private String label3 = "";

    @ApiModelProperty(value = "记录生成时间")
    @TableField(fill= FieldFill.INSERT)
    private Timestamp createTime;

    @ApiModelProperty(value = "记录更新时间")
    @TableField(fill= FieldFill.INSERT_UPDATE)
    private Timestamp updateTime;
    @ApiModelProperty(value = "是否参与销售 0/是 1/否")
    private Integer isSales = 0;

    @ApiModelProperty(value = "项目名称")
    @TableField(exist = false)
    private String projectCode;

    @ApiModelProperty(value = "唯一值")
    @TableField(exist = false)
    private String unique;

    @ApiModelProperty(value = "是否组合 0 否 1 是")
    private Integer isGroup;

    @TableField(exist = false)
    @ApiModelProperty(value = "组合中的子商品")
    private List<YxStoreProductGroupDto> groupDetailList;

    @TableField(exist = false)
    @ApiModelProperty(value = "项目结算价")
    private BigDecimal settlementPrice;
}
