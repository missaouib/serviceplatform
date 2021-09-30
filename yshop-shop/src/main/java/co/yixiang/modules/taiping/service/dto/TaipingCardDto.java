/**
* Copyright (C) 2018-2020
* All rights reserved, Designed By www.yixiang.co
* 注意：
* 本软件为www.yixiang.co开发研制，未经购买不得使用
* 购买后可获得全部源代码（禁止转卖、分享、上传到码云、github等开源平台）
* 一经发现盗用、分享等行为，将追究法律责任，后果自负
*/
package co.yixiang.modules.taiping.service.dto;

import lombok.Data;
import java.sql.Timestamp;
import java.io.Serializable;

/**
* @author visa
* @date 2020-11-02
*/
@Data
public class TaipingCardDto implements Serializable {

    /** 主键 */
    private Integer id;

    /** 卡号 */
    private String cardNumber;

    /** 卡的具体种类 */
    private String cardType;

    /** 卡渠道 */
    private String sellChannel;

    /** 代理 */
    private String agentCate;

    /** 组织ID */
    private String organID;

    /** 乐享同步记录时间 */
    private String insertTime;

    private Timestamp createTime;

    private Timestamp updateTime;
}
