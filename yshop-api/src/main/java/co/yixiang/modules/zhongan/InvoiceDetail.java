package co.yixiang.modules.zhongan;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value="众安普药订单发票信息传输对象", description="众安普药订单发票信息传输对象")
public class InvoiceDetail {
    @ApiModelProperty(value = "发票类型")
    private String invoiceType;
    @ApiModelProperty(value = "抬头类型")
    private String invoiceTitleType;
    @ApiModelProperty(value = "发票文件类")
    private String invoiceFileType;
    @ApiModelProperty(value = "发票抬头")
    private String invoiceTitle;
    @ApiModelProperty(value = "发票内容")
    private String invoiceContent;

}
