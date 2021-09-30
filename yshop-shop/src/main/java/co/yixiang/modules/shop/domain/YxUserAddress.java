/**
* Copyright (C) 2018-2020
* All rights reserved, Designed By www.yixiang.co
* 注意：
* 本软件为www.yixiang.co开发研制，未经购买不得使用
* 购买后可获得全部源代码（禁止转卖、分享、上传到码云、github等开源平台）
* 一经发现盗用、分享等行为，将追究法律责任，后果自负
*/
package co.yixiang.modules.shop.domain;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import com.baomidou.mybatisplus.annotation.TableName;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import javax.validation.constraints.*;
import java.io.Serializable;

/**
* @author visa
* @date 2020-10-15
*/
@Data
@TableName("yx_user_address")
public class YxUserAddress implements Serializable {

    /** 用户地址id */
    @TableId
    private Integer id;


    /** 用户id */
    @NotNull
    private Integer uid;


    /** 收货人姓名 */
    @NotBlank
    private String realName;


    /** 收货人电话 */
    @NotBlank
    private String phone;


    /** 收货人所在省 */
    @NotBlank
    private String province;


    /** 收货人所在市 */
    @NotBlank
    private String city;


    /** 收货人所在区 */
    @NotBlank
    private String district;


    /** 收货人详细地址 */
    @NotBlank
    private String detail;


    /** 邮编 */
    @NotBlank
    private String postCode;


    /** 经度 */
    @NotBlank
    private String longitude;


    /** 纬度 */
    @NotBlank
    private String latitude;


    /** 是否默认 */
    @NotNull
    private Integer isDefault;


    /** 是否删除 */
    @NotNull
    private Integer isDel;


    /** 添加时间 */
    @NotNull
    private Integer addTime;

    @ApiModelProperty(value = "省份 code")
    private String provinceCode;

    @ApiModelProperty(value = "城市 code")
    private String cityCode;

    @ApiModelProperty(value = "区县 code")
    private String districtCode;

    public void copy(YxUserAddress source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}
