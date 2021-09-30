package co.yixiang.modules.xikang.entity;

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
 * 熙康医院与商城药品的映射
 * </p>
 *
 * @author visa
 * @since 2020-12-30
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value="XikangMedMapping对象", description="熙康医院与商城药品的映射")
public class XikangMedMapping extends BaseEntity {

    private static final long serialVersionUID = 1L;

@TableId(value = "id", type = IdType.AUTO)
private Integer id;

@ApiModelProperty(value = "熙康代码")
private String xikangCode;

@ApiModelProperty(value = "益药宝代码")
private String yiyaobaoSku;

private Date createTime;

private Date updateTime;

}
