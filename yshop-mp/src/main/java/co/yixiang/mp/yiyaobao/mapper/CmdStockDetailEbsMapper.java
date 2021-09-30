package co.yixiang.mp.yiyaobao.mapper;


import co.yixiang.mp.yiyaobao.domain.Medicine;
import co.yixiang.mp.yiyaobao.domain.Seller;
import co.yixiang.mp.yiyaobao.domain.SkuSellerPriceStock;
import co.yixiang.mp.yiyaobao.domain.YiyaobaoMed;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import co.yixiang.mp.yiyaobao.entity.CmdStockDetailEbs;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * 商品库存明细表 Mapper 接口
 * </p>
 *
 * @author visazhou
 * @since 2020-06-16
 */
@Repository
@Mapper
public interface CmdStockDetailEbsMapper extends BaseMapper<CmdStockDetailEbs> {

     void syncStock();

     @DS("multi-datasource1")
     @Select("SELECT ID, MED_ID, SKU, SELLER_ID, SELLER_CODE, WAREHOUSE_ID, WAREHOUSE_CODE, STORAGE_RACK_ID, STORAGE_RACK_CODE, LOT_NO, UNIT, PASSAGE_AMOUNT, LOCK_AMOUNT, USABLE_AMOUNT, TOTAL_AMOUNT, FAR_PERIOD, NEAR_PERIOD, MAKE_DATE, REMARK, CREATE_USER, CREATE_TIME, UPDATE_USER, UPDATE_TIME" +
             " from cmd_stock_detail_ebs")
     List<CmdStockDetailEbs> getYiyaobaoStock();
     @DS("multi-datasource1")
     @Select("SELECT cp.SKU AS sku,cp.UNIT_PRICE AS price,ms.ID AS sellerId,ms.SELLER_NAME AS sellerName,csde.USABLE_AMOUNT AS stock\n" +
             "FROM yiyao_b2c.cmd_price cp ,yiyao_meta.md_seller ms, yiyao_b2c.cmd_stock_detail_ebs csde\n" +
             "WHERE LENGTH(TRIM( IFNULL(cp.SELLER_ID,''))) = 0 AND cp.PROVINCE_CODE = ms.PROVINCE_CODE AND cp.CITY_CODE = ms.CITY_CODE\n" +
             "AND ms.status = 1 AND ms.IS_SELF_SUPPORT =1\n" +
             "AND cp.SKU = csde.SKU\n" +
             "AND csde.SELLER_ID = ms.ID\n" +
             "and ms.SELLER_NAME != '广州上药益药药房有限公司'  ")
     List<SkuSellerPriceStock> getSellerPriceStockByCity( );
     @DS("multi-datasource1")
     @Select("SELECT cp.SKU AS sku,cp.UNIT_PRICE AS price,ms.ID AS sellerId,ms.SELLER_NAME AS sellerName,csde.USABLE_AMOUNT AS stock\n" +
             "FROM yiyao_b2c.cmd_price cp ,yiyao_meta.md_seller ms, yiyao_b2c.cmd_stock_detail_ebs csde\n" +
             "WHERE cp.SELLER_ID = ms.ID\n" +
             "AND ms.status = 1 AND ms.IS_SELF_SUPPORT =1\n" +
             "AND cp.SKU = csde.SKU\n" +
             "AND csde.SELLER_ID = ms.ID\n" +
             "and ms.SELLER_NAME != '广州上药益药药房有限公司'" +
             "")
     List<SkuSellerPriceStock> getSellerPriceStockBySeller();
     @DS("multi-datasource1")
     @Select("\n" +
             "SELECT\n" +
             "  SKU AS yiyaobaoSku,\n" +
             "  MED_NAME AS storeName,\n" +
             "  LICENSE_NUMBER AS licenseNumber,\n" +
             "  COMMON_NAME AS commonName,\n" +
             "  ENGLISH_NAME AS englishName,\n" +
             "  PINYIN_NAME AS pinyinName,\n" +
             "  PINYIN_SHORT_NAME AS pinyinShortName,\n" +
             "  DRUG_FORM_CODE AS drugFormCode,\n" +
             "  DRUG_FORM AS drugForm,\n" +
             "  SPEC AS spec,\n" +
             "  PACKAGES AS packages,\n" +
             "  MANUFACTURER AS manufacturer,\n" +
             "  DESCRIPTION AS description,\n" +
             "  STORAGE_CONDITION AS storageCondition,\n" +
             "  IS_BASIC AS isBasic,\n" +
             "  IS_BIRTH_CONTROL AS isBirthControl,\n" +
             "  IS_STIMULANT AS isStimulant,\n" +
             "  IS_PSYCHOTROPIC AS isPsychotropic,\n" +
             "  TAX_RATE AS taxRate,\n" +
             "  UNIT_CODE AS unitCode,\n" +
             "  UNIT AS unit,\n" +
             "  PACKAGE_UNIT AS packageUnit,\n" +
             "  UNIT_EXCHANGE AS unitExchange,\n" +
             "  IS_OPEN_STOCK\t as \tisOpenStock,\n" +
             "  MED_LENGTH\t as medLength,\n" +
             "  MED_WIDTH\t as medWidth,\n" +
             "  MED_HEIGHT\t as medHeight,\n" +
             "  MED_GROSS_WEIGHT\t as medGrossWeight,\n" +
             "  MED_CAPACITY\t as medCapacity,\n" +
             "  MEDIUM_AMOUNT\t as mediumAmount,\n" +
             "  MEDIUM_UNIT_CODE\t as mediumUnitCode,\n" +
             "  MEDIUM_UNIT_NAME\t as mediumUnitName,\n" +
             "  MEDIUM_LENGTH\t as mediumLength,\n" +
             "  MEDIUM_WIDTH\t as mediumWidth,\n" +
             "  MEDIUM_HEIGHT\t as mediumHeight,\n" +
             "  MEDIUM_WEIGHT\t as mediumWeight,\n" +
             "  MEDIUM_CAPACITY\t as mediumCapacity,\n" +
             "  LARGE_AMOUNT\t as largeAmount,\n" +
             "  LARGE_UNIT_CODE\t as largeUnitCode,\n" +
             "  LARGE_UNIT_NAME\t as largeUnitName,\n" +
             "  LARGE_LENGTH\t as largeLength,\n" +
             "  LARGE_WIDTH\t as largeWidth,\n" +
             "  LARGE_HEIGHT\t as largeHeight,\n" +
             "  LARGE_WEIGHT\t as largeWeight,\n" +
             "  LARGE_CAPACITY\t as largeCapacity,\n" +
             "  ATTENTION\t as attention,\n" +
             "  BASIS\t as basis,\n" +
             "  CHARACTERS\t as characters,\n" +
             "  FUNCTION_CATEGORY\t as functionCategory,\n" +
             "  INDICATION\t as indication,\n" +
             "  DIRECTIONS\t as directions,\n" +
             "  UNTOWARD_EFFECT\t as untowardEffect,\n" +
             "  CONTRAINDICATION\t as contraindication,\n" +
             "  DRUG_INTERACTION\t as drugInteraction,\n" +
             "  PHARMACOLOGICAL_EFFECT\t as pharmacologicalEffect,\n" +
             "  STORAGE\t as storage,\n" +
             "  STANDARD\t as standard,\n" +
             "  PRODUCTION_ADDRESS\t as productionAddress,\n" +
             "  TEL\t as tel,\n" +
             "  PRODUCT_AREA\t as productArea,\n" +
             "  FUNCTION_INDICATION\t as functionIndication,\n" +
             "  QUALITY_PERIOD\t as qualityPeriod,\n" +
             "  IS_IMPORT\t as isImport,\n" +
             "  BUSINESS_DIRECTORY_CODE\t as businessDirectoryCode,\n" +
             "  CATEGORY\t as category,\n" +
             "  IS_GIFT_BOX\t as isGiftBox,\n" +
             "  LICENSE_DEADLINE\t as licenseDeadline,\n" +
             "  IS_AUTHORIZATION\t as isAuthorization,\n" +
             "  STATUS\t as status,\n" +
             "  IS_COMPOUND_PREPARATION\t as isCompoundPreparation,\n" +
             "  IS_COLD_CHAIN\t as isColdChain,\n" +
             "  SEO\t as seo,\n" +
             "  SEARCH_KEYWORDS\t as searchKeywords,\n" +
             "  PREGNANCY_LACTATION_DIRECTIONS\t as pregnancyLactationDirections,\n" +
             "  CHILDREN_DIRECTIONS\t as childrenDirections,\n" +
             "  ELDERLY_PATIENT_DIRECTIONS\t as elderlyPatientDirections,\n" +
             "  APPLY_CROWD_DESC\t as applyCrowdDesc,\n" +
             "  APPLY_CROWD_CODE\t as applyCrowdCode,\n" +
             "  PHAMACOKINETICS\t as phamacokinetics,\n" +
             "  OVERDOSAGE\t as overdosage,\n" +
             "  CLINICAL_TEST\t as clinicalTest,\n" +
             "  USE_UNIT\t as useUnit,\n" +
             "  PHARMACOLOGY_TOXICOLOGY\t as pharmacologyToxicology,\n" +
             "  IS_HETEROTYPE\t as isHeterotype,\n" +
             "  CERT_IMAG_ID\t as certImagId,\n" +
             "  MEDICATION_CYCLE\t as medicationCycle," +
             "  CASE when CATEGORY = '01' THEN 2 WHEN CATEGORY IN ('02','03') THEN 1 ELSE 0 end AS type" +
             " FROM yiyao_meta.med_medicine WHERE sku = #{sku} limit 1")
     Medicine getMedicineBySku(@Param("sku") String sku);

     @DS("multi-datasource1")
     @Select("SELECT ms.id AS yiyaobaoId,seller_name AS name,ms.OFFICE_TEL AS phone,ms.FULL_ADDRESS AS detailedAddress, ms.FULL_ADDRESS as address, ms.IMAGE_ID AS image,ms.DESCRIPTION AS introduction ,\n" +
             "  CASE when status = 1 THEN 0 ELSE 1 END AS isDel ,\n" +
             "  CASE WHEN status = 1 THEN 1 ELSE 0 END AS isShow \n" +
             "FROM yiyao_meta.md_seller ms WHERE  ms.IS_SELF_SUPPORT = 1 and (ms.CREATE_TIME >= #{startDate} OR ms.UPDATE_TIME >= #{startDate})")
     List<Seller> getSellerByDate(@Param("startDate") String startDate);

     @DS("multi-datasource1")
     @Select("SELECT FILE_PATH AS filePath FROM yiyao_meta.md_image_detail WHERE image_id = #{imageId} ORDER BY create_time DESC limit 1")
     String getSellerImageById(@Param("imageId") String imageId);

     @DS("multi-datasource1")
     @Select("SELECT  cid.FILE_PATH AS filePath  \n" +
             "FROM yiyao_b2c.cmd_commodity cc JOIN yiyao_b2c.cmd_image_detail cid \n" +
             "ON cc.IMAGE_ID = cid.IMAGE_ID WHERE sku = #{sku} and cc.IMAGE_ID !='' AND cid.FILE_PATH != '' \n" +
             "ORDER BY cid.IS_MAIN DESC, cid.CREATE_TIME DESC LIMIT 6 ")
     List<String> getMedicineImageBySku(@Param("sku") String sku);


     @DS("multi-datasource1")
     @Select("SELECT\n" +
             "SPEC AS spec,\n" +
             "COMMON_NAME AS commonName,\n" +
             "INDICATION as indication,\n" +
             "APPLY_CROWD_DESC as applyCrowdDesc,\n" +
             "SKU AS sku,\n" +
             "DRUG_FORM AS drugForm,\n" +
             "DIRECTIONS as directions,\n" +
             "CONTRAINDICATION as contraindication,\n" +
             "UNIT AS unit,\n" +
             "CATEGORY as category,\n" +
             "LICENSE_NUMBER AS licenseNumber,\n" +
             "MED_NAME AS medName,\n" +
             "STORAGE_CONDITION AS storageCondition,\n" +
             "ATTENTION as attention,\n" +
             "MEDICATION_CYCLE as medicationCycle,\n" +
             "QUALITY_PERIOD as qualityPeriod,\n" +
             "STORAGE as storage,\n" +
             "MANUFACTURER AS manufacturer\n" +
             "FROM yiyao_meta.med_medicine WHERE sku = #{sku} limit 1"
           )
     YiyaobaoMed getMedicineBySkuSample(@Param("sku") String sku);


     @DS("multi-datasource1")
     @Select("SELECT ms.id AS yiyaobaoId,seller_name AS name,ms.OFFICE_TEL AS phone,ms.FULL_ADDRESS AS detailedAddress, ms.FULL_ADDRESS as address, ms.IMAGE_ID AS image,ms.DESCRIPTION AS introduction ,\n" +
             "  CASE when status = 1 THEN 0 ELSE 1 END AS isDel ,\n" +
             "  CASE WHEN status = 1 THEN 1 ELSE 0 END AS isShow \n" +
             "FROM yiyao_meta.md_seller ms WHERE  ms.IS_SELF_SUPPORT = 1 and ms.ID =  #{sellerId}")
     Seller getSellerById(@Param("sellerId") String sellerId);

     @DS("multi-datasource1")
     @Select("SELECT ms.id AS yiyaobaoId,seller_name AS name,ms.OFFICE_TEL AS phone,ms.FULL_ADDRESS AS detailedAddress, ms.FULL_ADDRESS as address, ms.IMAGE_ID AS image,ms.DESCRIPTION AS introduction ,\n" +
             "  CASE when status = 1 THEN 0 ELSE 1 END AS isDel ,\n" +
             "  CASE WHEN status = 1 THEN 1 ELSE 0 END AS isShow \n" +
             "FROM yiyao_meta.md_seller ms WHERE  ms.IS_SELF_SUPPORT = 1 ")
     List<Seller> getSeller();
}

