package co.yixiang.common.api;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 *   接口返回数据格式
 * @author scott
 * @email jeecgos@163.com
 * @date  2019年1月19日
 */
@Data
@ApiModel(value="第三方接口请求对象", description="第三方接口请求对象")
public class ApiRequest<T> implements Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "请求的接口类型代码")
	private String requestType;
	@ApiModelProperty(value = "请求唯一id")
	private String requestId;
	@ApiModelProperty(value = "请求返回时间")
	private String requestTime;
	@ApiModelProperty(value = "合作伙伴的客户id")
	private String companyId;
	@ApiModelProperty(value = "传输的业务数据内容")
	private T requestData;


}