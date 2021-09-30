package co.yixiang.modules.xikang.web.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import java.io.Serializable;

import java.util.Date;

/**
 * <p>
 * 熙康医院与商城药品的映射 查询结果对象
 * </p>
 *
 * @author visa
 * @date 2020-12-30
 */
@Data
@ApiModel(value="XikangMedMappingQueryVo对象", description="熙康医院与商城药品的映射查询参数")
public class XikangMedMappingQueryVo implements Serializable{
    private static final long serialVersionUID = 1L;

private Integer id;

@ApiModelProperty(value = "熙康代码")
private String xikangCode;

@ApiModelProperty(value = "益药宝代码")
private String yiyaobaoSku;

private Date createTime;

private Date updateTime;

}