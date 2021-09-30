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
@ApiModel(value="君岭药品主数据对象", description="君岭药品主数据对象")
public class BaiJiMed implements Serializable {

	private static final long serialVersionUID = 1L;

	private String image;//商品主图片
	private String name;//商品名称
	private String licenseNumber;//批准文号
	private String commonName;//通用名
	private String drugForm;//剂型(瓶，盒)
	private String spec;//规格(如500ml)
	private String manufacturer;//生产厂家
	private String storageCondition;//存储条件
	private String taxRate;//交易税率
	private String unit;//单位
	private String indication;//适应症
	private String directions;//用法用量
	private String untowardEffect;//不良反应
	private String contraindication;//禁忌
	private String drugInteraction;//药物相互作用
	private String functionIndication;//功能主治
	private String qualityPeriod;//保质期
	private String category;//类别，列子：处方药 OTC甲类 OTC乙类 保健品
	private String applyCrowdDesc;//适用人群
	private String code;//药品sku编码
	private String izDel;//是否删除（0 /否，1/是）
	private String sliderImage;//商品轮播图
	private String diseaseName;//疾病分类，多个用逗号分隔。例子：急性胃炎,维生素缺乏症

}