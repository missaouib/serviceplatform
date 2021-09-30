/**
* Copyright (C) 2018-2020
* All rights reserved, Designed By www.yixiang.co
* 注意：
* 本软件为www.yixiang.co开发研制，未经购买不得使用
* 购买后可获得全部源代码（禁止转卖、分享、上传到码云、github等开源平台）
* 一经发现盗用、分享等行为，将追究法律责任，后果自负
*/
package co.yixiang.modules.shop.domain;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import com.baomidou.mybatisplus.annotation.TableName;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.List;

/**
* @author visa
* @date 2020-06-02
*/
@Data
@TableName("yx_store_disease")
public class YxStoreDisease implements Serializable {

    /** 商品分类表ID */
    @TableId
    private Integer id;


    /** 父id */
    @NotNull
    private Integer pid;


    /** 病种名称 */
    @NotBlank
    private String cateName;


    /** 排序 */
    private Integer sort;


    /** 图标 */
    private String pic;


    /** 是否推荐 */
    private Integer isShow;


    /** 添加时间 */
    private Integer addTime;


    /** 删除状态 */
    private Integer isDel;

    private String projectCode="";

    /** '分类类型，1/我要找药 2/健康馆'*/
    private String cateType;

    @TableField(exist = false)
    private List<String> cateTypeList;

    public void copy(YxStoreDisease source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}
