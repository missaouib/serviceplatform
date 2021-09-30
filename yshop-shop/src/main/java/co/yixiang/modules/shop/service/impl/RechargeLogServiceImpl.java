package co.yixiang.modules.shop.service.impl;

import co.yixiang.common.service.impl.BaseServiceImpl;
import co.yixiang.modules.shop.domain.RechargeLog;
import co.yixiang.modules.shop.service.RechargeLogService;
import co.yixiang.modules.shop.service.mapper.RechargeLogMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 * 储值记录表 服务实现类
 * </p>
 *
 * @author visa
 * @since 2021-07-05
 */
@Service
@AllArgsConstructor
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class RechargeLogServiceImpl extends BaseServiceImpl<RechargeLogMapper, RechargeLog> implements RechargeLogService {

}
