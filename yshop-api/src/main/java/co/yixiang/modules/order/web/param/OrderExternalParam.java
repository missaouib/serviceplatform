package co.yixiang.modules.order.web.param;

import co.yixiang.modules.user.web.vo.YxUserAddressQueryVo;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @ClassName OrderParam
 * @Author hupeng <610796224@qq.com>
 * @Date 2019/10/28
 **/
@Data
public class OrderExternalParam implements Serializable {
    private YxUserAddressQueryVo address;

    private String payType;

    private String imagePath;
    private String mark;

    /*订单类型 慈善赠药*/
    private String type;

    /*订单编号*/
    private String orderNo;


}
