package co.yixiang.modules.yaolian.service.impl;

import co.yixiang.common.utils.QueryHelpPlus;
import co.yixiang.modules.yaolian.entity.YaolianOrder;
import co.yixiang.modules.yaolian.mapper.YaolianOrderMapper;
import co.yixiang.modules.yaolian.service.YaolianOrderService;
import co.yixiang.modules.yaolian.web.param.YaolianOrderQueryParam;
import co.yixiang.modules.yaolian.web.vo.YaolianOrderQueryVo;
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
 * 药联订单表 服务实现类
 * </p>
 *
 * @author visa
 * @since 2021-03-05
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class YaolianOrderServiceImpl extends BaseServiceImpl<YaolianOrderMapper, YaolianOrder> implements YaolianOrderService {

    @Autowired
    private YaolianOrderMapper yaolianOrderMapper;

    @Override
    public YaolianOrderQueryVo getYaolianOrderById(Serializable id) throws Exception{
        return yaolianOrderMapper.getYaolianOrderById(id);
    }

    @Override
    public Paging<YaolianOrderQueryVo> getYaolianOrderPageList(YaolianOrderQueryParam yaolianOrderQueryParam) throws Exception{
        Page page = setPageParam(yaolianOrderQueryParam,OrderItem.desc("create_time"));
        QueryWrapper queryWrapper = QueryHelpPlus.getPredicate(YaolianOrderQueryParam.class, yaolianOrderQueryParam);
        IPage<YaolianOrderQueryVo> iPage = yaolianOrderMapper.selectPage(page,queryWrapper);
        return new Paging(iPage);
    }

}
