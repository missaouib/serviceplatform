package co.yixiang.modules.taiping.service;

import co.yixiang.modules.taiping.entity.OrderStatusDto;
import co.yixiang.modules.taiping.entity.TaipingCard;
import co.yixiang.common.service.BaseService;
import co.yixiang.modules.taiping.entity.TaipingParamDto;
import co.yixiang.modules.taiping.web.param.TaipingCardQueryParam;
import co.yixiang.modules.taiping.web.vo.TaipingCardQueryVo;
import co.yixiang.common.web.vo.Paging;

import java.io.Serializable;

/**
 * <p>
 * 太平乐享虚拟卡 服务类
 * </p>
 *
 * @author visa
 * @since 2020-11-19
 */
public interface TaipingCardService extends BaseService<TaipingCard> {

    /**
     * 根据ID获取查询对象
     * @param id
     * @return
     */
    TaipingCardQueryVo getTaipingCardById(Serializable id) throws Exception;

    /**
     * 获取分页对象
     * @param taipingCardQueryParam
     * @return
     */
    Paging<TaipingCardQueryVo> getTaipingCardPageList(TaipingCardQueryParam taipingCardQueryParam) throws Exception;

    TaipingCard getTaipingCardByNumber(String cardNumber);

    TaipingParamDto analysisParam(TaipingParamDto taipingParamDto);

    Boolean sendOrderStatus(OrderStatusDto orderStatus);

    Boolean sendOrderStatus(String orderId,Integer status);
}
