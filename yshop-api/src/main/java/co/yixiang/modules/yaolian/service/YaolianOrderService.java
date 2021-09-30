package co.yixiang.modules.yaolian.service;

import co.yixiang.modules.yaolian.entity.YaolianOrder;
import co.yixiang.common.service.BaseService;
import co.yixiang.modules.yaolian.web.param.YaolianOrderQueryParam;
import co.yixiang.modules.yaolian.web.vo.YaolianOrderQueryVo;
import co.yixiang.common.web.vo.Paging;

import java.io.Serializable;

/**
 * <p>
 * 药联订单表 服务类
 * </p>
 *
 * @author visa
 * @since 2021-03-05
 */
public interface YaolianOrderService extends BaseService<YaolianOrder> {

    /**
     * 根据ID获取查询对象
     * @param id
     * @return
     */
    YaolianOrderQueryVo getYaolianOrderById(Serializable id) throws Exception;

    /**
     * 获取分页对象
     * @param yaolianOrderQueryParam
     * @return
     */
    Paging<YaolianOrderQueryVo> getYaolianOrderPageList(YaolianOrderQueryParam yaolianOrderQueryParam) throws Exception;

}
