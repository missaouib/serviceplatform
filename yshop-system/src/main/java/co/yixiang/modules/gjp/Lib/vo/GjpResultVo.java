package co.yixiang.modules.gjp.Lib.vo;

import lombok.Data;

@Data
public class GjpResultVo {
    private String requestid;
    private Boolean iserror;
    private GjpResponseVo response;
}
