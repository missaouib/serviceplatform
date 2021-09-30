package co.yixiang.modules.hospitaldemand.web.vo;

import co.yixiang.enums.OrderSourceEnum;
import lombok.Data;

@Data
public class InternetHospitalCart {
    // 购物车id，多个时英文逗号分隔
    String cartIds;

    // 处方照片
    String imagePath;

    // 订单来源
    String orderSource = OrderSourceEnum.internetHospital.getValue();

    String projectCode;
    String cardType;
    String cardNumber;
    String orderNumber;

    Integer demandId;

    String orderId = "";
    Integer isUse = 0;

}
