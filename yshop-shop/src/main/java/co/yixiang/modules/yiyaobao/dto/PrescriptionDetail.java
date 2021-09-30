  /**
   * Generate time : 2015-06-11 14:23:10
   * Version : 1.0.1.V20070717
   */
package co.yixiang.modules.yiyaobao.dto;



import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;



import java.util.HashMap;
import java.util.Map;

import org.hibernate.validator.constraints.NotBlank;


  /**
   * PrsPrescriptionDetailTemp
   * 用于对外处方接口使用。根据需要存入TEMP表或正式表
   *入库时需要手动设置字段 1.prsId
   */
  public class PrescriptionDetail implements Serializable {

      /**
       *
       */
      private static final long serialVersionUID = -1689374082510910106L;
      @NotBlank(message="validate.notnull")
      private String medCode = " ";	 //药品编码
      @NotBlank(message="validate.notnull")
      private String medName = " ";	 //药品名称

      private BigDecimal amount = new BigDecimal(0); //药品数量 不能为空

      private BigDecimal unitPrice = new BigDecimal(0);		//单价
      private String spec = " ";//规格
      private String drugForm = " ";//剂型(瓶，盒)
      private String directions = " "; // 用法用量
      private BigDecimal discount = new BigDecimal(0);		/* 折扣(如0.92)*/
      private BigDecimal discountAmount = new BigDecimal(0);		/* 折扣金额*/




      /**
       * the constructor
       */
      public PrescriptionDetail() {

      }

      /**
       * get the medCode 药品编码
       * @return the medCode
       */
      public String getMedCode() {
          return this.medCode;
      }

      /**
       * set the medCode 药品编码
       */
      public void setMedCode(String medCode) {
          this.medCode = medCode;
      }

      /**
       * get the medName 药品名称
       * @return the medName
       */
      public String getMedName() {
          return this.medName;
      }

      /**
       * set the medName 药品名称
       */
      public void setMedName(String medName) {
          this.medName = medName;
      }

      /**
       * get the spec 规格
       * @return the spec
       */
      public String getSpec() {
          return this.spec;
      }

      /**
       * set the spec 规格
       */
      public void setSpec(String spec) {
          this.spec = spec;
      }


      /**
       * get the drugForm 剂型(瓶，盒)
       * @return the drugForm
       */
      public String getDrugForm() {
          return this.drugForm;
      }

      /**
       * set the drugForm 剂型(瓶，盒)
       */
      public void setDrugForm(String drugForm) {
          this.drugForm = drugForm;
      }



      /**
       * get the directions  用法用量
       * @return the directions
       */
      public String getDirections() {
          return this.directions;
      }

      /** 用法用量
       * set the directions
       */
      public void setDirections(String directions) {
          this.directions = directions;
      }

      /**
       * 药品数量
       * @return
       */
      public BigDecimal getAmount() {
          return amount;
      }

      /**
       * 药品数量
       * @param amount
       */
      public void setAmount(BigDecimal amount) {
          this.amount = amount;
      }

      /**
       * 药品单价
       * @return
       */
      public BigDecimal getUnitPrice() {
          return unitPrice;
      }

      /**
       * 药品单价
       * @param unitPrice
       */
      public void setUnitPrice(BigDecimal unitPrice) {
          this.unitPrice = unitPrice;
      }

      /**
       * 折扣比例 如0.92
       * @return
       */
      public BigDecimal getDiscount() {
          return discount;
      }

      /**
       * 折扣比例 如0.92
       * @param discount
       */
      public void setDiscount(BigDecimal discount) {
          this.discount = discount;
      }

      /**
       * 折扣金额
       * @return
       */
      public BigDecimal getDiscountAmount() {
          return discountAmount;
      }

      /**
       * 折扣金额
       * @param discountAmount
       */
      public void setDiscountAmount(BigDecimal discountAmount) {
          this.discountAmount = discountAmount;
      }



  }