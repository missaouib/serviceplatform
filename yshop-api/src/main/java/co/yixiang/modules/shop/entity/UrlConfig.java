package co.yixiang.modules.shop.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import co.yixiang.common.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * <p>
 * 
 * </p>
 *
 * @author visazhou
 * @since 2020-06-10
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value="UrlConfig对象", description="")
public class UrlConfig extends BaseEntity {

    private static final long serialVersionUID = 1L;

@TableId(value = "id", type = IdType.AUTO)
private Integer id;

@ApiModelProperty(value = "url")
private String url;

@ApiModelProperty(value = "图片地址")
private String image;

}
