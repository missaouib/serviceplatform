package co.yixiang.modules.manage.entity;

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
 * @since 2020-05-20
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value="Partner对象", description="")
public class Partner extends BaseEntity {

    private static final long serialVersionUID = 1L;

@TableId(value = "id", type = IdType.AUTO)
private Integer id;

private String name;

private String appId;

private String appSecret;

private String projectNo;

private String sellerId;

private Integer addTime;

}
