package co.yixiang.modules.shop.service.impl;

import co.yixiang.common.utils.QueryHelpPlus;
import co.yixiang.modules.shop.entity.YxExpressTemplateDetail;
import co.yixiang.modules.shop.mapper.YxExpressTemplateDetailMapper;
import co.yixiang.modules.shop.service.YxExpressTemplateDetailService;
import co.yixiang.modules.shop.web.param.YxExpressTemplateDetailQueryParam;
import co.yixiang.modules.shop.web.vo.YxExpressTemplateDetailQueryVo;
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
 * 物流运费模板明细 服务实现类
 * </p>
 *
 * @author visa
 * @since 2020-11-28
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class YxExpressTemplateDetailServiceImpl extends BaseServiceImpl<YxExpressTemplateDetailMapper, YxExpressTemplateDetail> implements YxExpressTemplateDetailService {

    @Autowired
    private YxExpressTemplateDetailMapper yxExpressTemplateDetailMapper;

    @Override
    public YxExpressTemplateDetailQueryVo getYxExpressTemplateDetailById(Serializable id) throws Exception{
        return yxExpressTemplateDetailMapper.getYxExpressTemplateDetailById(id);
    }

    @Override
    public Paging<YxExpressTemplateDetailQueryVo> getYxExpressTemplateDetailPageList(YxExpressTemplateDetailQueryParam yxExpressTemplateDetailQueryParam) throws Exception{
        Page page = setPageParam(yxExpressTemplateDetailQueryParam,OrderItem.desc("create_time"));
        QueryWrapper queryWrapper = QueryHelpPlus.getPredicate(YxExpressTemplateDetailQueryParam.class, yxExpressTemplateDetailQueryParam);
        IPage<YxExpressTemplateDetail> iPage = yxExpressTemplateDetailMapper.selectPage(page,queryWrapper);
        return new Paging(iPage);
    }

}
