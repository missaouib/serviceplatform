/**
* Copyright (C) 2018-2020
* All rights reserved, Designed By www.yixiang.co
* 注意：
* 本软件为www.yixiang.co开发研制，未经购买不得使用
* 购买后可获得全部源代码（禁止转卖、分享、上传到码云、github等开源平台）
* 一经发现盗用、分享等行为，将追究法律责任，后果自负
*/
package co.yixiang.mp.service.dto;

import lombok.Data;
import java.sql.Timestamp;
import java.io.Serializable;

/**
* @author visazhou
* @date 2020-12-27
*/
@Data
public class YxWechatUserInfoDto implements Serializable {

    private Integer id;

    /** 用户是否订阅该公众号标识，值为0时，代表此用户没有关注该公众号，拉取不到其余信息 */
    private Integer subscribe;

    /** 用户的昵称 */
    private String nickname;

    /** 用户的性别，值为1时是男性，值为2时是女性，值为0时是未知 */
    private Integer sex;

    /** 用户的语言，简体中文为zh_CN */
    private String language;

    /** 用户所在城市 */
    private String city;

    /** 用户所在省份 */
    private String province;

    /** 用户所在国家 */
    private String country;

    /** 公众号运营者对粉丝的备注，公众号运营者可在微信公众平台用户管理界面对粉丝添加备注 */
    private String remark;

    /** 用户id */
    private Integer uid;

    private Timestamp createTime;

    private Timestamp updateTime;

    /** 用户的标识，对当前公众号唯一 */
    private String openId;

    /** 用户的性别，值为1时是男性，值为2时是女性，值为0时是未知 */
    private String sexDesc;

    /** 用户头像，最后一个数值代表正方形头像大小（有0、46、64、96、132数值可选，0代表640*640正方形头像），用户没有头像时该项为空。若用户更换头像，原有头像URL将失效。 */
    private String headImgUrl;

    /** 用户关注时间，为时间戳。如果用户曾多次关注，则取最后关注时间 */
    private Integer subscribeTime;

    /** 只有在用户将公众号绑定到微信开放平台帐号后，才会出现该字段。 */
    private String unionId;

    /** 公众号名称 */
    private String wechatName;
}
