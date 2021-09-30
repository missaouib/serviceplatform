package co.yixiang.mp.yiyaobao.service;

import co.yixiang.mp.yiyaobao.entity.CmdStockDetailEbs;
import co.yixiang.common.service.BaseService;

import co.yixiang.common.web.vo.Paging;

import java.io.Serializable;
import java.util.List;

/**
 * <p>
 * 商品库存明细表 服务类
 * </p>
 *
 * @author visazhou
 * @since 2020-06-16
 */
public interface CmdStockDetailEbsService extends BaseService<CmdStockDetailEbs> {


    void syncStock(List<CmdStockDetailEbs> list);
}
