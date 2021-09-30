package co.yixiang.modules.yaoshitong.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import co.yixiang.modules.yaoshitong.entity.BbsArticle;
import co.yixiang.modules.yaoshitong.web.param.BbsArticleQueryParam;
import co.yixiang.modules.yaoshitong.web.vo.BbsArticleQueryVo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import java.io.Serializable;

/**
 * <p>
 * bbs文章列表 Mapper 接口
 * </p>
 *
 * @author visa
 * @since 2020-07-27
 */
@Repository
public interface BbsArticleMapper extends BaseMapper<BbsArticle> {

    /**
     * 根据ID获取查询对象
     * @param id
     * @return
     */
    BbsArticleQueryVo getBbsArticleById(Serializable id);

    /**
     * 获取分页对象
     * @param page
     * @param bbsArticleQueryParam
     * @return
     */
    IPage<BbsArticleQueryVo> getBbsArticlePageList(@Param("page") Page page, @Param("param") BbsArticleQueryParam bbsArticleQueryParam);

    @Update("UPDATE bbs_article SET visit_count = visit_count + 1 WHERE id = #{articleId}")
    void updateVisitCount( @Param("articleId") String articleId );
}
