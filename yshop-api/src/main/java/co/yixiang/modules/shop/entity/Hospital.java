package co.yixiang.modules.shop.entity;

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
 * 医院
 * </p>
 *
 * @author visa
 * @since 2021-06-11
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value="Hospital对象", description="医院")
public class Hospital extends BaseEntity {

    private static final long serialVersionUID = 1L;

@ApiModelProperty(value = "主键")
@TableId(value = "id", type = IdType.AUTO)
private Integer id;

@ApiModelProperty(value = "医院名称")
private String name;

@ApiModelProperty(value = "地址")
private String address;

@ApiModelProperty(value = "logo图片")
private String image;

@ApiModelProperty(value = "记录生成时间")
private Date createTime;

@ApiModelProperty(value = "记录更新时间")
private Date updateTime;

@ApiModelProperty(value = "站点信息")
private String siteInfo;

    @ApiModelProperty(value = "医院编码")
private String code;
    @ApiModelProperty(value = "药房id，多个用逗号分隔")
    private String storeIds;

}
