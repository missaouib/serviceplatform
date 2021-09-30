package co.yixiang.mp.service;

import co.yixiang.common.service.BaseService;
import co.yixiang.common.web.vo.Paging;
import co.yixiang.mp.domain.DictDetail;
import co.yixiang.mp.rest.param.DictDetailQueryParam;
import co.yixiang.mp.rest.vo.DictDetailQueryVo;
import org.springframework.data.domain.Pageable;


import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 数据字典详情 服务类
 * </p>
 *
 * @author visazhou
 * @since 2020-07-13
 */
public interface DictDetailService extends BaseService<DictDetail> {

    /**
     * 根据ID获取查询对象
     * @param id
     * @return
     */
    DictDetailQueryVo getDictDetailById(Serializable id) throws Exception;

    /**
     * 获取分页对象
     * @param dictDetailQueryParam
     * @return
     */
    Paging<DictDetailQueryVo> getDictDetailPageList(DictDetailQueryParam dictDetailQueryParam) throws Exception;

    List<DictDetail> getDictDetailList(DictDetailQueryParam dictDetailQueryParam);
    List<DictDetail> queryAll(DictDetailQueryParam dictDetailQueryParam);

    List<DictDetail> findDetails(List<String> values, String dicName);

}
