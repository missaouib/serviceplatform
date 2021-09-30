package co.yixiang.modules.taibao.service.vo;

import co.yixiang.modules.taibao.domain.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

/**
 * @Description: claim_info
 * @Author: jeecg-boot
 * @Date:   2021-04-23
 * @Version: V1.0
 */
@Data
@ApiModel(value="claim_infoPage对象", description="claim_info")
public class ClaimInfoPage {

	/**id*/
	@ApiModelProperty(value = "id")
    private Long id;
	/**创建人*/
	@ApiModelProperty(value = "创建人")
    private String createBy;
	/**创建日期*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
	@ApiModelProperty(value = "创建日期")
    private Date createTime;
	/**更新人*/
	@ApiModelProperty(value = "更新人")
    private String updateBy;
	/**更新日期*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
	@ApiModelProperty(value = "更新日期")
    private Date updateTime;
	/**所属部门*/
	@ApiModelProperty(value = "所属部门")
    private String sysOrgCode;
	/**订单号*/
	@ApiModelProperty(value = "订单id")
	private Long orderId;
	/**报案号*/
	@ApiModelProperty(value = "报案号")
	private String reportno;
	/**批次号*/
	@ApiModelProperty(value = "批次号")
    private String batchno;
	/**赔案号*/
	@ApiModelProperty(value = "赔案号")
    private String claimno;
	/**收单单位代码*/
	@ApiModelProperty(value = "收单单位代码")
    private String custmco;
	/**快递签收时间*/
	@ApiModelProperty(value = "快递签收时间")
    private String exptime;
	/**医保号*/
	@ApiModelProperty(value = "医保号")
    private String medicalCode;
	/**是否接受电子邮件*/
	@ApiModelProperty(value = "是否接受电子邮件  Y-接受 N-不接收，默认为：Y")
    private String emailAccept="Y";
	/**收单时间*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
	@ApiModelProperty(value = "收单时间")
    private Date visitDate;
	/**资料收齐时间*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	@ApiModelProperty(value = "资料收齐时间")
	private Date advanceClosingTime;
	/**垫付结案时间*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	@ApiModelProperty(value = "垫付结案时间")
	private Date dataCollectionDay;
	/**复核意见*/
	@ApiModelProperty(value = "复核意见")
    private String reauditoption;
	/**复核完成时间*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
	@ApiModelProperty(value = "复核完成时间")
    private Date reauditdate;
	/**挂起类型(多种类型用逗号拼接)*/
	@ApiModelProperty(value = "挂起类型(多种类型用逗号拼接)")
    private String hangupsign;
	/**赔案层结论*/
	@ApiModelProperty(value = "赔案层结论")
    private String claimrescode;
	/**审核意见*/
	@ApiModelProperty(value = "审核意见")
    private String auditoption;
	/**删除标识*/
	@ApiModelProperty(value = "删除标识")
    private Double delFlag;

	/** 图片附件 */
	@ApiModelProperty(value = "图片附件")
	private String imgUrl;

	/** pdf附件 */
	@ApiModelProperty(value = "pdf附件")
	private String pdfUrl;

	/** 赔案状态  0未完结 1：已完结 */
	@ApiModelProperty(value = "赔案状态  0未完结 1：已完结")
	private String status;

	/** 身份状态  0：正常， 1：疑似恐怖分子 */
	@ApiModelProperty(value = "身份状态：0: 正常 ,  1：疑似恐怖分子")
	private String userStatus;

	@ApiModelProperty(value = "报案人信息")
	private List<TbNotificationPerson> notificationPersonList;
	@ApiModelProperty(value = "被保人信息")
	private List<TbInsurancePerson> insurancePersonList;
	@ApiModelProperty(value = "第三方投保信息")
	private List<TbClaimThirdInsurance> claimThirdInsuranceList;
	@ApiModelProperty(value = "第三方赔付情况")
	private List<TbClaimThirdPay> claimThirdPayList;
	@ApiModelProperty(value = "资料情况")
	private List<TbClaimMaterial> claimMaterialList;
	@ApiModelProperty(value = "其他")
	private List<TbClaimOther> claimOtherList;
	@ApiModelProperty(value = "调查")
	private List<TbClaimInvest> claimInvestList;
	@ApiModelProperty(value = "理赔预估信息")
	private List<TbClaimClmestimate> claimClmestimateList;
	@ApiModelProperty(value = "协谈")
	private List<TbClaimConsult> claimConsultList;
	@ApiModelProperty(value = "领款人")
	private List<TbClaimBenefitPerson> claimBenefitPersonList;
	@ApiModelProperty(value = "责任赔付金额信息")
	private List<TbClaimClaimPay> claimClaimPayList;
	@ApiModelProperty(value = "补充资料")
	private List<TbClaimAddMaterial> claimAddMaterialList;
	@ApiModelProperty(value = "赔付责任理赔结论")
	private List<TbClaimAuditInfo> claimAuditInfoList;
	@ApiModelProperty(value = "赔付保单后续处理")
	private List<TbClaimAuditpolicy> claimAuditpolicyList;
	@ApiModelProperty(value = "超额件")
	private List<TbClaimAbove> claimAboveList;
	@ApiModelProperty(value = "出险信息")
	private List<TbClaimAccInfo> claimAccInfoList;
	@ApiModelProperty(value = "事件")
	private List<ClaimEventPage> claimEventList;
	@ApiModelProperty(value = "订单信息")
	private	TbOrderProjectParam orderProjectParam;

}
