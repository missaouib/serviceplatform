package co.yixiang.modules.shop.service;

import co.yixiang.modules.shop.entity.RechargeLog;
import co.yixiang.common.service.BaseService;
import co.yixiang.modules.shop.web.param.RechargeLogQueryParam;
import co.yixiang.modules.shop.web.vo.RechargeLogQueryVo;
import co.yixiang.common.web.vo.Paging;

import java.io.Serializable;

/**
 * <p>
 * 储值记录表 服务类
 * </p>
 *
 * @author visa
 * @since 2021-07-05
 */
public interface RechargeLogService extends BaseService<RechargeLog> {

    /**
     * 根据ID获取查询对象
     * @param id
     * @return
     */
    RechargeLogQueryVo getRechargeLogById(Serializable id) throws Exception;

    /**
     * 获取分页对象
     * @param rechargeLogQueryParam
     * @return
     */
    Paging<RechargeLogQueryVo> getRechargeLogPageList(RechargeLogQueryParam rechargeLogQueryParam) throws Exception;

}
