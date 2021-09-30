package co.yixiang.modules.manage.web.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.io.Serializable;

import java.util.Date;

/**
 * <p>
 *  查询结果对象
 * </p>
 *
 * @author visazhou
 * @date 2020-05-20
 */
@Data
@ApiModel(value="PartnerQueryVo对象", description="查询参数")
public class PartnerQueryVo implements Serializable{
    private static final long serialVersionUID = 1L;

private Integer id;

private String name;

private String appId;

private String appSecret;

private String projectNo;

private String sellerId;

private Integer addTime;

}