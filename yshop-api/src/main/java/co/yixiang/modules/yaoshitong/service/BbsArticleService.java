package co.yixiang.modules.yaoshitong.service;

import co.yixiang.modules.yaoshitong.entity.BbsArticle;
import co.yixiang.common.service.BaseService;
import co.yixiang.modules.yaoshitong.web.param.BbsArticleQueryParam;
import co.yixiang.modules.yaoshitong.web.vo.BbsArticleQueryVo;
import co.yixiang.common.web.vo.Paging;

import java.io.Serializable;

/**
 * <p>
 * bbs文章列表 服务类
 * </p>
 *
 * @author visa
 * @since 2020-07-27
 */
public interface BbsArticleService extends BaseService<BbsArticle> {

    /**
     * 根据ID获取查询对象
     * @param id
     * @return
     */
    BbsArticle getBbsArticleById(Serializable id) throws Exception;

    /**
     * 获取分页对象
     * @param bbsArticleQueryParam
     * @return
     */
    Paging<BbsArticle> getBbsArticlePageList(BbsArticleQueryParam bbsArticleQueryParam) throws Exception;

    void updateVisitCount(String articleId);


    Integer upArticle(String id,Integer uid);

    Integer collectArticle(String id,Integer uid);
}
