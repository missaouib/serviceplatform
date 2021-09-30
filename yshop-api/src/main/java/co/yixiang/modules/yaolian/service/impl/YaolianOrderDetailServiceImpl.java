package co.yixiang.modules.yaolian.service.impl;

import co.yixiang.common.utils.QueryHelpPlus;
import co.yixiang.modules.yaolian.entity.YaolianOrderDetail;
import co.yixiang.modules.yaolian.mapper.YaolianOrderDetailMapper;
import co.yixiang.modules.yaolian.service.YaolianOrderDetailService;
import co.yixiang.modules.yaolian.web.param.YaolianOrderDetailQueryParam;
import co.yixiang.modules.yaolian.web.vo.YaolianOrderDetailQueryVo;
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
 * 药联订单明细 服务实现类
 * </p>
 *
 * @author visa
 * @since 2021-03-05
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class YaolianOrderDetailServiceImpl extends BaseServiceImpl<YaolianOrderDetailMapper, YaolianOrderDetail> implements YaolianOrderDetailService {

    @Autowired
    private YaolianOrderDetailMapper yaolianOrderDetailMapper;

    @Override
    public YaolianOrderDetailQueryVo getYaolianOrderDetailById(Serializable id) throws Exception{
        return yaolianOrderDetailMapper.getYaolianOrderDetailById(id);
    }

    @Override
    public Paging<YaolianOrderDetailQueryVo> getYaolianOrderDetailPageList(YaolianOrderDetailQueryParam yaolianOrderDetailQueryParam) throws Exception{
        Page page = setPageParam(yaolianOrderDetailQueryParam,OrderItem.desc("create_time"));
        QueryWrapper queryWrapper = QueryHelpPlus.getPredicate(YaolianOrderDetailQueryParam.class, yaolianOrderDetailQueryParam);
        IPage<YaolianOrderDetailQueryVo> iPage = yaolianOrderDetailMapper.selectPage(page,queryWrapper);
        return new Paging(iPage);
    }

}
