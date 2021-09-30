package co.yixiang.modules.manage.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import co.yixiang.common.entity.BaseEntity;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * <p>
 * 采购需求单
 * </p>
 *
 * @author visazhou
 * @since 2020-05-20
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value="PurchaseForm对象", description="采购需求单")
public class PurchaseForm extends BaseEntity {

    private static final long serialVersionUID = 1L;

@ApiModelProperty(value = "主键")
@TableId(value = "id", type = IdType.AUTO)
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
