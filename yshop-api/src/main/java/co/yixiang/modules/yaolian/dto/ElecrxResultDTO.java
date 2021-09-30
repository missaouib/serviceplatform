package co.yixiang.modules.yaolian.dto;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;

@Data
@Slf4j
@ApiModel(value="药联处方查询返回对象", description="药联处方查询返回对象")
public class ElecrxResultDTO implements Serializable {
    private String errno;
    private String error;
    private ElecrxDTO data;

}
