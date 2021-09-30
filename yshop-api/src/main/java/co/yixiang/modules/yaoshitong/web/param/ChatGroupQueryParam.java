package co.yixiang.modules.yaoshitong.web.param;

import co.yixiang.annotation.Query;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import co.yixiang.common.web.param.QueryParam;

/**
 * <p>
 * 聊天群组 查询参数对象
 * </p>
 *
 * @author visa
 * @date 2020-09-02
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value="ChatGroupQueryParam对象", description="聊天群组查询参数")
public class ChatGroupQueryParam extends QueryParam {
    private static final long serialVersionUID = 1L;

    @Query(type = Query.Type.EQUAL)
    private Integer managerId;
}
