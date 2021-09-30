package co.yixiang.modules.shop.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import co.yixiang.common.entity.BaseEntity;

import java.math.BigDecimal;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * <p>
 * 用药人列表
 * </p>
 *
 * @author visa
 * @since 2020-12-20
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value="YxDrugUsers对象", description="用药人列表")
public class YxDrugUsers extends BaseEntity {

    private static final long serialVersionUID = 1L;

@ApiModelProperty(value = "主键")
@TableId(value = "id", type = IdType.AUTO)
private Integer id;

@ApiModelProperty(value = "用户id")
private Integer uid;

@ApiModelProperty(value = "姓名")
private String name;

@ApiModelProperty(value = "关系 1/本人 2/亲属 3/朋友 4/其他")
private Integer relation;

@ApiModelProperty(value = "手机号")
private String phone;

@ApiModelProperty(value = "性别")
private String sex;

@ApiModelProperty(value = "身份证号")
private String idcard;

@ApiModelProperty(value = "生成时间")
private Date createTime;

@ApiModelProperty(value = "最后更新时间")
private Date updateTime;

@ApiModelProperty(value = "是否默认")
private Integer isDefault;

@ApiModelProperty(value = "是否删除")
private Integer isDel;

@ApiModelProperty(value = "年龄")
private Integer age;

@ApiModelProperty(value = "疾病史")
private String diseaseHistory;

@ApiModelProperty(value = "出生年月")
private String birth;

@ApiModelProperty(value = "用药人类型 1/成人 2/儿童")
private Integer userType;

@ApiModelProperty(value = "体重")
private String weight;

}
