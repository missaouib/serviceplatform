/**
* Copyright (C) 2018-2020
* All rights reserved, Designed By www.yixiang.co
* 注意：
* 本软件为www.yixiang.co开发研制，未经购买不得使用
* 购买后可获得全部源代码（禁止转卖、分享、上传到码云、github等开源平台）
* 一经发现盗用、分享等行为，将追究法律责任，后果自负
*/
package co.yixiang.modules.msh.service.dto;

import lombok.Data;
import java.io.Serializable;

/**
* @author cq
* @date 2020-12-25
*/
@Data
public class MshDemandListFileDto implements Serializable {

    private Integer id;

    /** 需求单主表ID */
    private Integer demandListId;

    /** 文件地址 */
    private String fileUrl;

    /** 文件名称 */
    private String fileName;

    /** 文件大小 */
    private String fileSize;
}
