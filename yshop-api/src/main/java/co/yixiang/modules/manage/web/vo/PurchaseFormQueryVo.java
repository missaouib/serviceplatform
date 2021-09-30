package co.yixiang.modules.manage.web.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.io.Serializable;

import java.util.Date;

/**
 * <p>
 * 采购需求单 查询结果对象
 * </p>
 *
 * @author visazhou
 * @date 2020-05-20
 */
@Data
@ApiModel(value="PurchaseFormQueryVo对象", description="采购需求单查询参数")
public class PurchaseFormQueryVo implements Serializable{
    private static final long serialVersionUID = 1L;

@ApiModelProperty(value = "主键")
private Integer id;

@ApiModelProperty(value = "联系人名称")
private String username;

@ApiModelProperty(value = "手机")
private String mobile;

@ApiModelProperty(value = "采购需求")
private String request;

@ApiModelProperty(value = "地址")
private String address;

@ApiModelProperty(value = "添加时间")
private Date createTime;

@ApiModelProperty(value = "产品名称")
private String productName;

@ApiModelProperty(value = "用户id")
private Integer uid;

}