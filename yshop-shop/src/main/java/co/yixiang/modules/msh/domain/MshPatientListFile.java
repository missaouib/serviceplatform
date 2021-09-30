/**
* Copyright (C) 2018-2020
* All rights reserved, Designed By www.yixiang.co
* 注意：
* 本软件为www.yixiang.co开发研制，未经购买不得使用
* 购买后可获得全部源代码（禁止转卖、分享、上传到码云、github等开源平台）
* 一经发现盗用、分享等行为，将追究法律责任，后果自负
*/
package co.yixiang.modules.msh.domain;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import com.baomidou.mybatisplus.annotation.TableName;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import javax.validation.constraints.*;
import java.io.Serializable;

/**
* @author cq
* @date 2020-12-25
*/
@Data
@TableName("msh_patient_list_file")
public class MshPatientListFile implements Serializable {

    @TableId
    private Integer id;


    /** 患者ID */
    @NotNull
    private Integer patientId;


    /** 文件地址 */
    private String fileUrl;

    /** 文件名称 */
    private String fileName;

    /** 文件大小 */
    private String fileSize;

    /** 文件类型 */
    private String type;

    /** 文件类型 */
    private String fileType;

    private String fileUrlConvert;

    public void copy(MshPatientListFile source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}
