package co.yixiang.modules.yiyaobao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import co.yixiang.common.entity.BaseEntity;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * 国家地区信息表
 * </p>
 *
 * @author visazhou
 * @since 2020-05-16
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value="MdCountry对象", description="国家地区信息表")
public class MdCountry extends BaseEntity {

    private static final long serialVersionUID = 1L;

@ApiModelProperty(value = "标识")
@TableId(value = "ID", type = IdType.ID_WORKER)
    private Long id;

@ApiModelProperty(value = "代码")
    @TableField("CODE")
private String code;

@ApiModelProperty(value = "名称")
    @TableField("NAME")
private String name;

@ApiModelProperty(value = "名称拼音")
    @TableField("PINYIN")
private String pinyin;

@ApiModelProperty(value = "父节点ID")
    @TableField("PARENT_ID")
private String parentId;

@ApiModelProperty(value = "树节点ID")
    @TableField("TREE_ID")
private String treeId;

@ApiModelProperty(value = "是否叶子节点")
    @TableField("IS_LEAF")
private Integer isLeaf;

@ApiModelProperty(value = "是否售药城市(0-否;1-是)")
    @TableField("IS_SALE")
private Integer isSale;

@ApiModelProperty(value = "是否直辖市(0-否;1-是)")
    @TableField("IS_DIRECT")
private Integer isDirect;

@ApiModelProperty(value = "城市编码，如021")
    @TableField("AREA_CODE")
private String areaCode;

@ApiModelProperty(value = "描述")
    @TableField("DESCRIPTION")
private String description;

@ApiModelProperty(value = "创建人")
    @TableField("CREATE_USER")
private String createUser;

@ApiModelProperty(value = "创建时间")
    @TableField("CREATE_TIME")
private Date createTime;

@ApiModelProperty(value = "更新人")
    @TableField("UPDATE_USER")
private String updateUser;

@ApiModelProperty(value = "更新时间")
    @TableField("UPDATE_TIME")
private Date updateTime;

}
