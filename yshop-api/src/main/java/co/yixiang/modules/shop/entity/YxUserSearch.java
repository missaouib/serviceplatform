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
 * 用户搜索词
 * </p>
 *
 * @author visazhou
 * @since 2020-06-03
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value="YxUserSearch对象", description="用户搜索词")
public class YxUserSearch extends BaseEntity {

    private static final long serialVersionUID = 1L;

@ApiModelProperty(value = "用户id")
private Integer uid;

@ApiModelProperty(value = "搜索词")
private String keyword;

@ApiModelProperty(value = "生成时间")
private Integer addTime;

@ApiModelProperty(value = "主键")
@TableId(value = "id", type = IdType.AUTO)
private Integer id;

@ApiModelProperty(value = "是否删除")
private Integer isDel;

}
