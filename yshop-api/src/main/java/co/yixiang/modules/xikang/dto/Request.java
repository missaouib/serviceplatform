package co.yixiang.modules.xikang.dto;

import lombok.Data;


@Data
public class Request {
    private String systemId;
    private String data;
    private String systemKey;
    private String businessType;
    private String method;
    private Integer pageNum;
    private Integer pageSize;
}
