package co.yixiang.modules.yaolian.service;

import co.yixiang.modules.yaolian.entity.YaolianOrderDetail;
import co.yixiang.common.service.BaseService;
import co.yixiang.modules.yaolian.web.param.YaolianOrderDetailQueryParam;
import co.yixiang.modules.yaolian.web.vo.YaolianOrderDetailQueryVo;
import co.yixiang.common.web.vo.Paging;

import java.io.Serializable;

/**
 * <p>
 * 药联订单明细 服务类
 * </p>
 *
 * @author visa
 * @since 2021-03-05
 */
public interface YaolianOrderDetailService extends BaseService<YaolianOrderDetail> {

    /**
     * 根据ID获取查询对象
     * @param id
     * @return
     */
    YaolianOrderDetailQueryVo getYaolianOrderDetailById(Serializable id) throws Exception;

    /**
     * 获取分页对象
     * @param yaolianOrderDetailQueryParam
     * @return
     */
    Paging<YaolianOrderDetailQueryVo> getYaolianOrderDetailPageList(YaolianOrderDetailQueryParam yaolianOrderDetailQueryParam) throws Exception;

}
