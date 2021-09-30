/**
   * Generate time : 2015-06-11 14:23:51
   * Version : 1.0.1.V20070717
   */
package co.yixiang.modules.yiyaobao.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotBlank;


/**
 * PrsPrescription
 *用于对外处方接口使用。根据需要存入TEMP表或正式表
 *入库时需要手动设置字段  1. source   2.id  3.isDelete 4.partnerId
 */
public class Prescription implements Serializable{

	/**
	 *
	 */
	private static final long serialVersionUID = -5634691466579347614L;
	@NotBlank(message="validate.notnull")
	private String hospitalName = " ";  // 医院名称
	@NotBlank(message="validate.notnull")
	private String prescripNo = " ";  //处方编号

	private Long registerType = new Long(1);   //门诊类型 1普通 2急诊 3住院
	@NotBlank(message="validate.notnull")
	private String name = " ";  //患者姓名

	private BigDecimal totalAmount = new BigDecimal(0); //收费金额
	private String diagnoseResult = " "; //诊断结果
	private String medicalRecordNo = " ";  //病历号
	private String registerNo = " ";  //门诊号
	private String department = " "; //科别
	private String deptCode =" ";//科室代码
	private BigDecimal temperature = new BigDecimal(0); //体温
	private String roomNo = " "; //病室
	private String bedNo = " ";//床位号
	private String feeType = " "; //费别(自费、医保)
	private Long registerDate;  //挂号日期
	private Long prescribeDate;  //开方日期
	private Long age = new Long(0); //年龄
	private Long sex = new Long(0);  //性别(0-女;1-男)
	private String idCard = " "; //身份证号
	private String medicalCareCard = " "; //医保卡号
	private String healthCard = " ";//健康卡号
	private String socialSecurityCard = " "; //社保卡号
	private String militaryCard = " "; // 军官证号
	private String studentCard = " "; //学生证号
	private String residenceCard = " "; //居住证号
	private String allergyTest = " "; //过敏试验
	private String doctorName = " "; //医生姓名
	private String address = " "; //送货地址
	private String mobile = " ";  //手机号
    private BigDecimal discount = new BigDecimal("0");		// 折扣(如0.92)
    private BigDecimal discountAmount = new BigDecimal("0");		// 折扣金额
	private String communityCode = " "; //社区配送点代码
	private String communityName = " ";  //社区配送点名称
	private String deliverType = " "; //送货方式
	private String remark = " ";
	private String provinceName="";
	private String cityName="";
	private String districtName = "";

    private String receiver="";
    private String receiverMobile="";
    private BigDecimal paidAmount=new BigDecimal(0);
    private String payMethod;

    private List<ImageModel> images;


    // 原处方号(退费处方时使用)
    private String originalPrescripNo="";

    private String orderId;
    //来源
    private String orderSource;

    private String payType;

    public String getOriginalPrescripNo() {
        return originalPrescripNo;
    }

    public void setOriginalPrescripNo(String originalPrescripNo) {
        this.originalPrescripNo = originalPrescripNo;
    }

    public List<ImageModel> getImages() {
        return images;
    }

    public void setImages(List<ImageModel> images) {
        this.images = images;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getReceiverMobile() {
        return receiverMobile;
    }

    public void setReceiverMobile(String receiverMobile) {
        this.receiverMobile = receiverMobile;
    }

    public BigDecimal getPaidAmount() {
        return paidAmount;
    }

    public void setPaidAmount(BigDecimal paidAmount) {
        this.paidAmount = paidAmount;
    }

    public String getPayMethod() {
        return payMethod;
    }

    public void setPayMethod(String payMethod) {
        this.payMethod = payMethod;
    }

    public String getProvinceName() {
        return provinceName;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getDistrictName() {
        return districtName;
    }

    public void setDistrictName(String districtName) {
        this.districtName = districtName;
    }

    private List<PrescriptionDetail> details; /*订单详情列表。正常情况下，一个处方包含一条或多条明细。由于历史原因，
	                                                                                                              部分系统在调用上传处方接口时，一次调用只能上传一条处方明细。此时应使用detail字段。
	                                                                                                            否则，则应该使用details    */

    private PrescriptionDetail  detail;  /*订单详情。正常情况下，一个处方包含一条或多条明细。由于历史原因，
                                                                                                             部分系统在调用上传处方接口时，一次调用只能上传一条处方明细。此时应使用detail字段。  否则，则应该使用details*/


	/**
	 * the constructor
	 */
	public Prescription() {

	}




   public String getDeptCode() {
		return deptCode;
	}




	public void setDeptCode(String deptCode) {
		this.deptCode = deptCode;
	}




/**
    * 设置订单详情
    * @return
    */
    public PrescriptionDetail getDetail() {
		return detail;
	}




	public void setDetail(PrescriptionDetail detail) {
		this.detail = detail;
	}




	/**
    * get the hospitalName - 医院名称
    * @return the hospitalName
    */
    public String getHospitalName() {
    return this.hospitalName;
    }

    /**
    * set the hospitalName - 医院名称
    */
    public void setHospitalName(String hospitalName) {
    this.hospitalName = hospitalName;
    }
    /**
    * get the prescripNo - 处方编号
    * @return the prescripNo
    */
    public String getPrescripNo() {
    return this.prescripNo;
    }

    /**
    * set the prescripNo - 处方编号
    */
    public void setPrescripNo(String prescripNo) {
    this.prescripNo = prescripNo;
    }

    /**
    * get the registerType - 挂号类别（1-普通;2-急诊）
    * @return the registerType
    */
    public Long getRegisterType() {
    return this.registerType;
    }

    /**
    * set the registerType - 挂号类别（1-普通;2-急诊）
    */
    public void setRegisterType(Long registerType) {
    this.registerType = registerType;
    }
    /**
    * get the medicalRecordNo - 病历号
    * @return the medicalRecordNo
    */
    public String getMedicalRecordNo() {
    return this.medicalRecordNo;
    }

    /**
    * set the medicalRecordNo - 病历号
    */
    public void setMedicalRecordNo(String medicalRecordNo) {
    this.medicalRecordNo = medicalRecordNo;
    }
    /**
    * get the registerNo - 门诊号
    * @return the registerNo
    */
    public String getRegisterNo() {
    return this.registerNo;
    }

    /**
    * set the registerNo - 门诊号
    */
    public void setRegisterNo(String registerNo) {
    this.registerNo = registerNo;
    }
    /**
    * get the department - 科别(儿科、眼科等)
    * @return the department
    */
    public String getDepartment() {
    return this.department;
    }

    /**
    * set the department - 科别(儿科、眼科等)
    */
    public void setDepartment(String department) {
    this.department = department;
    }
    /**
    * get the temperature - 体温
    * @return the temperature
    */
    public BigDecimal getTemperature() {
    return this.temperature;
    }

    /**
    * set the temperature - 体温
    */
    public void setTemperature(BigDecimal temperature) {
    this.temperature = temperature;
    }
    /**
    * get the roomNo - 病室
    * @return the roomNo
    */
    public String getRoomNo() {
    return this.roomNo;
    }

    /**
    * set the roomNo - 病室
    */
    public void setRoomNo(String roomNo) {
    this.roomNo = roomNo;
    }
    /**
    * get the bedNo - 床位号
    * @return the bedNo
    */
    public String getBedNo() {
    return this.bedNo;
    }

    /**
    * set the bedNo - 床位号
    */
    public void setBedNo(String bedNo) {
    this.bedNo = bedNo;
    }
    /**
    * get the feeType - 费别(自费、医保)
    * @return the feeType
    */
    public String getFeeType() {
    return this.feeType;
    }

    /**
    * set the feeType - 费别(自费、医保)
    */
    public void setFeeType(String feeType) {
    this.feeType = feeType;
    }
    /**
    * get the registerDate - 挂号日期
    * @return the registerDate
    */
    public Long getRegisterDate() {
    return this.registerDate;
    }

    /**
    * set the registerDate - 挂号日期
    */
    public void setRegisterDate(Long registerDate) {
    this.registerDate = registerDate;
    }
    /**
    * get the prescribeDate - 开方日期
    * @return the prescribeDate
    */
    public Long getPrescribeDate() {
    return this.prescribeDate;
    }

    /**
    * set the prescribeDate - 开方日期
    */
    public void setPrescribeDate(Long prescribeDate) {
    this.prescribeDate = prescribeDate;
    }
    /**
    * get the name - 姓名
    * @return the name
    */
    public String getName() {
    return this.name;
    }

    /**
    * set the name - 姓名
    */
    public void setName(String name) {
    this.name = name;
    }
    /**
    * get the age - 年龄
    * @return the age
    */
    public Long getAge() {
    return this.age;
    }

    /**
    * set the age - 年龄
    */
    public void setAge(Long age) {
    this.age = age;
    }
    /**
    * get the sex - 性别(0-女;1-男)
    * @return the sex
    */
    public Long getSex() {
    return this.sex;
    }

    /**
    * set the sex - 性别(0-女;1-男)
    */
    public void setSex(Long sex) {
    this.sex = sex;
    }
    /**
    * get the diagnoseResult - 诊断结果
    * @return the diagnoseResult
    */
    public String getDiagnoseResult() {
    return this.diagnoseResult;
    }

    /**
    * set the diagnoseResult - 诊断结果
    */
    public void setDiagnoseResult(String diagnoseResult) {
    this.diagnoseResult = diagnoseResult;
    }
    /**
    * get the idCard - 身份证号
    * @return the idCard
    */
    public String getIdCard() {
    return this.idCard;
    }

    /**
    * set the idCard - 身份证号
    */
    public void setIdCard(String idCard) {
    this.idCard = idCard;
    }
    /**
    * get the medicalCareCard - 医保卡号
    * @return the medicalCareCard
    */
    public String getMedicalCareCard() {
    return this.medicalCareCard;
    }

    /**
    * set the medicalCareCard - 医保卡号
    */
    public void setMedicalCareCard(String medicalCareCard) {
    this.medicalCareCard = medicalCareCard;
    }
    /**
    * get the healthCard - 健康卡号
    * @return the healthCard
    */
    public String getHealthCard() {
    return this.healthCard;
    }

    /**
    * set the healthCard - 健康卡号
    */
    public void setHealthCard(String healthCard) {
    this.healthCard = healthCard;
    }
    /**
    * get the socialSecurityCard - 社保卡号
    * @return the socialSecurityCard
    */
    public String getSocialSecurityCard() {
    return this.socialSecurityCard;
    }

    /**
    * set the socialSecurityCard - 社保卡号
    */
    public void setSocialSecurityCard(String socialSecurityCard) {
    this.socialSecurityCard = socialSecurityCard;
    }
    /**
    * get the militaryCard - 军官证号
    * @return the militaryCard
    */
    public String getMilitaryCard() {
    return this.militaryCard;
    }

    /**
    * set the militaryCard - 军官证号
    */
    public void setMilitaryCard(String militaryCard) {
    this.militaryCard = militaryCard;
    }
    /**
    * get the studentCard - 学生证号
    * @return the studentCard
    */
    public String getStudentCard() {
    return this.studentCard;
    }

    /**
    * set the studentCard - 学生证号
    */
    public void setStudentCard(String studentCard) {
    this.studentCard = studentCard;
    }
    /**
    * get the residenceCard - 居住证号
    * @return the residenceCard
    */
    public String getResidenceCard() {
    return this.residenceCard;
    }

    /**
    * set the residenceCard - 居住证号
    */
    public void setResidenceCard(String residenceCard) {
    this.residenceCard = residenceCard;
    }
    /**
    * get the allergyTest - 过敏试验
    * @return the allergyTest
    */
    public String getAllergyTest() {
    return this.allergyTest;
    }

    /**
    * set the allergyTest - 过敏试验
    */
    public void setAllergyTest(String allergyTest) {
    this.allergyTest = allergyTest;
    }


    /**
    * get the doctorName - 医生姓名
    * @return the doctorName
    */
    public String getDoctorName() {
    return this.doctorName;
    }

    /**
    * set the doctorName - 医生姓名
    */
    public void setDoctorName(String doctorName) {
    this.doctorName = doctorName;
    }


    /**
    * get the address - 送货地址
    * @return the address
    */
    public String getAddress() {
    return this.address;
    }

    /**
    * set the address - 送货地址
    */
    public void setAddress(String address) {
    this.address = address;
    }
    /**
    * get the mobile - 手机号
    * @return the mobile
    */
    public String getMobile() {
    return this.mobile;
    }

    /**
    * set the mobile - 手机号
    */
    public void setMobile(String mobile) {
    this.mobile = mobile;
    }
    /**
    * get the communityCode - 社区配送点代码
    * @return the communityCode
    */
    public String getCommunityCode() {
    return this.communityCode;
    }

    /**
    * set the communityCode - 社区配送点代码
    */
    public void setCommunityCode(String communityCode) {
    this.communityCode = communityCode;
    }
    /**
    * get the communityName - 社区配送点名称
    * @return the communityName
    */
    public String getCommunityName() {
    return this.communityName;
    }

    /**
    * set the communityName - 社区配送点名称
    */
    public void setCommunityName(String communityName) {
    this.communityName = communityName;
    }
    /**
    * get the deliverType - 送货方式
    * @return the deliverType
    */
    public String getDeliverType() {
    return this.deliverType;
    }

    /**
    * set the deliverType - 送货方式
    */
    public void setDeliverType(String deliverType) {
    this.deliverType = deliverType;
    }

    /**
    * get the remark - 备注
    * @return the remark
    */
    public String getRemark() {
    return this.remark;
    }

    /**
    * set the remark - 备注
    */
    public void setRemark(String remark) {
    this.remark = remark;
    }

    /**
    * get the totalAmount - 收费金额
    * @return the totalAmount
    */
    public BigDecimal getTotalAmount() {
    return this.totalAmount;
    }

    /**
    * set the totalAmount - 收费金额
    */
    public void setTotalAmount(BigDecimal totalAmount) {
    this.totalAmount = totalAmount;
    }
    /**
    * get the discount - 折扣(如0.92)
    * @return the discount
    */
    public BigDecimal getDiscount() {
    return this.discount;
    }

    /**
    * set the discount - 折扣(如0.92)
    */
    public void setDiscount(BigDecimal discount) {
    this.discount = discount;
    }
    /**
    * get the discountAmount - 折扣金额
    * @return the discountAmount
    */
    public BigDecimal getDiscountAmount() {
    return this.discountAmount;
    }

    /**
    * set the discountAmount - 折扣金额
    */
    public void setDiscountAmount(BigDecimal discountAmount) {
    this.discountAmount = discountAmount;
    }


   /**
    * 处方明细
    * @return
    */
	public List<PrescriptionDetail> getDetails() {
		return details;
	}


	  /**
	    * 处方明细
	    * @return
	    */
	public void setDetails(List<PrescriptionDetail> details) {
		this.details = details;
	}

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getOrderSource() {
        return orderSource;
    }

    public void setOrderSource(String orderSource) {
        this.orderSource = orderSource;
    }

    public String getPayType() {
        return payType;
    }

    public void setPayType(String payType) {
        this.payType = payType;
    }
}