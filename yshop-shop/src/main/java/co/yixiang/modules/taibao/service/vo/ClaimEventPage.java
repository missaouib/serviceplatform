package co.yixiang.modules.taibao.service.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

/**
 * @Description: claim_event
 * @Author: jeecg-boot
 * @Date:   2021-04-23
 * @Version: V1.0
 */
@Data
@ApiModel(value="claim_eventPage对象", description="claim_event")
public class ClaimEventPage {

	/**id*/
	@ApiModelProperty(value = "id")
    private Long id;
	/**赔案信息Id*/
	@ApiModelProperty(value = "赔案信息Id")
    private Long claimInfoId;
	/**索赔事故性质（枚举值）*/
	@ApiModelProperty(value = "索赔事故性质（枚举值）")
    private String claimacc;
	/**疾病诊断*/
	@ApiModelProperty(value = "疾病诊断")
    private String illcode;
	/**就诊日期*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
	@ApiModelProperty(value = "就诊日期")
    private Date caredate;
	/**入院日期*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
	@ApiModelProperty(value = "入院日期")
    private Date indate;
	/**出院日期*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
	@ApiModelProperty(value = "出院日期")
    private Date outdate;
	/**住院天数*/
	@ApiModelProperty(value = "住院天数")
    private Integer indays;
	/**身故日期*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
	@ApiModelProperty(value = "身故日期")
    private Date deadDate;
	/**伤残鉴定日期*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
	@ApiModelProperty(value = "伤残鉴定日期")
    private Date disableDate;
	/**就诊医院代码*/
	@ApiModelProperty(value = "就诊医院代码")
    private String hospitalInfo;
	/**就诊医院名称*/
	@ApiModelProperty(value = "就诊医院名称")
    private String clinical;
	/**主治医生姓名*/
	@ApiModelProperty(value = "主治医生姓名")
    private String doctor;
	/**手术代码*/
	@ApiModelProperty(value = "手术代码")
    private String surgery;
	/**重疾代码*/
	@ApiModelProperty(value = "重疾代码")
    private String critical;
	/**医保类型*/
	@ApiModelProperty(value = "医保类型")
    private String medicalType;
	/**是否转诊*/
	@ApiModelProperty(value = "是否转诊")
    private String referral;
	/**转来医院名称*/
	@ApiModelProperty(value = "转来医院名称")
    private String referralHosp;
	/**科室名称*/
	@ApiModelProperty(value = "科室名称")
    private String referralClinical;
	/**医生姓名*/
	@ApiModelProperty(value = "医生姓名")
    private String referralDoctor;
	@ApiModelProperty(value = "预产期")
    private String edc;
	/**预期是否单胎*/
	@ApiModelProperty(value = "预期是否单胎")
    private String issingle;
	/**是否使用妊娠辅助医疗或人工授精*/
	@ApiModelProperty(value = "是否使用妊娠辅助医疗或人工授精")
    private String isuseOther;
	/**具体情况*/
	@ApiModelProperty(value = "具体情况")
    private String conditionInfo;
	/**收据总数*/
	@ApiModelProperty(value = "收据总数")
    private Integer billCnt;
	/**事件审核结论*/
	@ApiModelProperty(value = "事件审核结论")
    private String auditconclusion;
	/**事件审核意见*/
	@ApiModelProperty(value = "事件审核意见")
    private String auditoption;
	/**创建人*/
	@ApiModelProperty(value = "创建人")
    private String createBy;
	/**创建时间*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
	@ApiModelProperty(value = "创建时间")
    private Date createTime;
	/**修改人*/
	@ApiModelProperty(value = "修改人")
    private String updateBy;
	/**修改时间*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
	@ApiModelProperty(value = "修改时间")
    private Date updateTime;
	/**0表示未删除,1表示删除*/
	@ApiModelProperty(value = "0表示未删除,1表示删除")
    private String delFlag;

	@ApiModelProperty(value = "收据信息")
	private List<EventBillPage> eventBillList;

}
