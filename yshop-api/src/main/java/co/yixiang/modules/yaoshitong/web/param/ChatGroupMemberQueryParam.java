package co.yixiang.modules.yaoshitong.web.param;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import co.yixiang.common.web.param.QueryParam;

/**
 * <p>
 * 聊天群组成员 查询参数对象
 * </p>
 *
 * @author visa
 * @date 2020-09-02
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value="ChatGroupMemberQueryParam对象", description="聊天群组成员查询参数")
public class ChatGroupMemberQueryParam extends QueryParam {
    private static final long serialVersionUID = 1L;
}
