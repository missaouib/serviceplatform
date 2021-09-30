package co.yixiang.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
public class MessageModel {
    /**
     * 事件类型
     */
    private String eventType ;
    /**
     * 请求参数？ MemberRegisterInfo
     */
    private Object requestParam;
    /**
     * 时间戳
     */
    private Date timeStamp;
}
