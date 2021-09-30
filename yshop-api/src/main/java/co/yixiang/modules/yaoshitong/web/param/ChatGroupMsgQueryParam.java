package co.yixiang.modules.yaoshitong.web.param;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import co.yixiang.common.web.param.QueryParam;

/**
 * <p>
 * 聊天组群聊天记录 查询参数对象
 * </p>
 *
 * @author visa
 * @date 2020-09-02
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value="ChatGroupMsgQueryParam对象", description="聊天组群聊天记录查询参数")
public class ChatGroupMsgQueryParam extends QueryParam {
    private static final long serialVersionUID = 1L;
}
