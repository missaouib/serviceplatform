package co.yixiang.modules.baiji.domain;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 *   接口返回数据格式
 * @author zhoujinlai
 * @date  2021-09-06
 */
@Data
@ApiModel(value="君岭药品库存对象", description="君岭药品库存对象")
public class BaiJiStock implements Serializable {

	private static final long serialVersionUID = 1L;

	private String  commonName;//药品通用名
	private String  pharmacyCode;//药房编码
	private String  goodsCode;//药品编码
	private String	spec;//规格
	private String  manufacturer;//生产厂家
	private String	unit;//单位
	private String	pharmacyName;//药房名称
	private String	name;//药品名称
	private Integer	stock;//库存
	private Integer	unitPrice;//单价，单位分

}