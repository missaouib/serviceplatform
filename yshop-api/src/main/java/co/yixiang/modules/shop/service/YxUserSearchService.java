package co.yixiang.modules.shop.service;

import co.yixiang.modules.shop.entity.YxUserSearch;
import co.yixiang.common.service.BaseService;
import co.yixiang.modules.shop.web.param.YxUserSearchQueryParam;
import co.yixiang.modules.shop.web.vo.YxUserSearchQueryVo;
import co.yixiang.common.web.vo.Paging;

import java.io.Serializable;

/**
 * <p>
 * 用户搜索词 服务类
 * </p>
 *
 * @author visazhou
 * @since 2020-06-03
 */
public interface YxUserSearchService extends BaseService<YxUserSearch> {

    /**
     * 根据ID获取查询对象
     * @param id
     * @return
     */
    YxUserSearchQueryVo getYxUserSearchById(Serializable id) throws Exception;

    /**
     * 获取分页对象
     * @param yxUserSearchQueryParam
     * @return
     */
    Paging<YxUserSearchQueryVo> getYxUserSearchPageList(YxUserSearchQueryParam yxUserSearchQueryParam) throws Exception;

    Boolean deleteYxUserSearchAll(Integer uid);
}
