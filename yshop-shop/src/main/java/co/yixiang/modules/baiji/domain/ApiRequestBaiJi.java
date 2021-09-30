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
@ApiModel(value="君岭接口请求对象", description="君岭接口请求对象")
public class ApiRequestBaiJi<T> implements Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "由君岭提供给合作方的应用 ID")
	private String companyId;
	@ApiModelProperty(value = "接口方法名（固定值）")
	private String requestType;
	@ApiModelProperty(value = "随机数")
	private String requestId;
	@ApiModelProperty(value = "时间戳")
	private String requestTime;
	@ApiModelProperty(value = "请求传输的业务数据内容")
	private T requestData;


}