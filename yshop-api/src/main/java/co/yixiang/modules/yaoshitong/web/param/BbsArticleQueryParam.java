package co.yixiang.modules.yaoshitong.web.param;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import co.yixiang.common.web.param.QueryParam;

/**
 * <p>
 * bbs文章列表 查询参数对象
 * </p>
 *
 * @author visa
 * @date 2020-07-27
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value="BbsArticleQueryParam对象", description="bbs文章列表查询参数")
public class BbsArticleQueryParam extends QueryParam {
    private static final long serialVersionUID = 1L;

    private String option;
}
