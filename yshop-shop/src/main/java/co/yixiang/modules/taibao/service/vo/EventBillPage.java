package co.yixiang.modules.taibao.service.vo;

import co.yixiang.modules.taibao.domain.TbBillDrugs;
import co.yixiang.modules.taibao.domain.TbBillItem;
import co.yixiang.modules.taibao.domain.TbBillOtherItem;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

/**
 * @Description: event_bill
 * @Author: jeecg-boot
 * @Date:   2021-04-23
 * @Version: V1.0
 */
@Data
@ApiModel(value="收据信息", description="收据信息")
public class EventBillPage {

	/**id*/
	@ApiModelProperty(value = "id")
    private Long id;
	/**事件信息Id*/
	@ApiModelProperty(value = "事件信息Id")
    private Long eventId;
	/**收据号*/
	@ApiModelProperty(value = "收据号")
    private String billSno;
	/**收据类型（枚举值） （1 住院2 门急诊 3 药店）*/
	@ApiModelProperty(value = "收据类型（枚举值） （1 住院2 门急诊 3 药店）")
    private String billType;
	/**币种(枚举值)*/
	@ApiModelProperty(value = "币种(枚举值)")
    private String currency;
	/**汇率*/
	@ApiModelProperty(value = "汇率")
    private String currRate;
	/**收据总金额*/
	@ApiModelProperty(value = "收据总金额")
    private String billAmt;
	/**发票日期*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
	@ApiModelProperty(value = "发票日期")
    private Date billDate;
	/**统筹支付*/
	@ApiModelProperty(value = "统筹支付")
    private String overallpay;
	/**附加支付*/
	@ApiModelProperty(value = "附加支付")
    private String attachpay;
	/**自费金额*/
	@ApiModelProperty(value = "自费金额")
    private String ownamt;
	/**分类自负*/
	@ApiModelProperty(value = "分类自负")
    private String divamt;
	/**第三方支付*/
	@ApiModelProperty(value = "第三方支付")
    private String thirdpay;
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
    private boolean delFlag;

	@ApiModelProperty(value = "收据信息汇总项目")
	private List<TbBillItem> billItemList;
	@ApiModelProperty(value = "药品清单")
	private List<TbBillOtherItem> billOtherItemList;
	@ApiModelProperty(value = "其他费用清单")
	private List<TbBillDrugs> billDrugsList;

}
