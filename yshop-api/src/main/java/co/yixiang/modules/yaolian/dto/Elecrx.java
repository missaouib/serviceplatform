package co.yixiang.modules.yaolian.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.List;

@Data
@Slf4j
@ApiModel(value="药联处方对象", description="药联处方对象")
public class Elecrx implements Serializable {
    @ApiModelProperty(value = "处方单流水号")
    private String rx_id;

    @ApiModelProperty(value = "处方单号")
    private String rx_no;

    @ApiModelProperty(value = "处方单号，0是处方单开具中，1是处方单开具成功，2是处方单开具失败")
    private String status;

    @ApiModelProperty(value = "处方单生成时间")
    private String create_time;

    @ApiModelProperty(value = "科室")
    private String department;

    @ApiModelProperty(value = "患者姓名")
    private String name;

    @ApiModelProperty(value = "患者性别")
    private String sex;

    @ApiModelProperty(value = "患者年龄")
    private String age;

    @ApiModelProperty(value = "患者手机号")
    private String phone;

    @ApiModelProperty(value = "患者身份证号")
    private String identity;

    @ApiModelProperty(value = "诊断结果")
    private String diagnose;

    @ApiModelProperty(value = "处方医师")
    private String doctor;

    @ApiModelProperty(value = "处方单图片")
    private String pic;

    @ApiModelProperty(value = "药品名称")
    private List<ElecrxDetail> retailDetail;
}
