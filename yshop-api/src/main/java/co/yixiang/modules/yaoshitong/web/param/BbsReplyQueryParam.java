package co.yixiang.modules.yaoshitong.web.param;

import co.yixiang.annotation.Query;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import co.yixiang.common.web.param.QueryParam;

/**
 * <p>
 * 帖子回复表 查询参数对象
 * </p>
 *
 * @author visa
 * @date 2020-07-27
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value="BbsReplyQueryParam对象", description="帖子回复表查询参数")
public class BbsReplyQueryParam extends QueryParam {
    private static final long serialVersionUID = 1L;
    @Query(type = Query.Type.EQUAL)
    private String articleId;
}
