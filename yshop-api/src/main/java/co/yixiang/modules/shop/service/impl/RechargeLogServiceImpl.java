package co.yixiang.modules.shop.service.impl;

import co.yixiang.common.utils.QueryHelpPlus;
import co.yixiang.modules.shop.entity.RechargeLog;
import co.yixiang.modules.shop.mapper.RechargeLogMapper;
import co.yixiang.modules.shop.service.RechargeLogService;
import co.yixiang.modules.shop.web.param.RechargeLogQueryParam;
import co.yixiang.modules.shop.web.vo.RechargeLogQueryVo;
import co.yixiang.common.service.impl.BaseServiceImpl;
import co.yixiang.common.web.vo.Paging;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import java.io.Serializable;


/**
 * <p>
 * 储值记录表 服务实现类
 * </p>
 *
 * @author visa
 * @since 2021-07-05
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class RechargeLogServiceImpl extends BaseServiceImpl<RechargeLogMapper, RechargeLog> implements RechargeLogService {

    @Autowired
    private RechargeLogMapper rechargeLogMapper;

    @Override
    public RechargeLogQueryVo getRechargeLogById(Serializable id) throws Exception{
        return rechargeLogMapper.getRechargeLogById(id);
    }

    @Override
    public Paging<RechargeLogQueryVo> getRechargeLogPageList(RechargeLogQueryParam rechargeLogQueryParam) throws Exception{
        Page page = setPageParam(rechargeLogQueryParam,OrderItem.desc("create_time"));
        QueryWrapper queryWrapper = QueryHelpPlus.getPredicate(RechargeLogQueryParam.class, rechargeLogQueryParam);
        IPage<RechargeLogQueryVo> iPage = rechargeLogMapper.selectPage(page,queryWrapper);
        return new Paging(iPage);
    }

}
