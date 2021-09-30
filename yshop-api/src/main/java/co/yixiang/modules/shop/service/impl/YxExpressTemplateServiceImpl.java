package co.yixiang.modules.shop.service.impl;

import co.yixiang.common.utils.QueryHelpPlus;
import co.yixiang.modules.shop.entity.YxExpressTemplate;
import co.yixiang.modules.shop.entity.YxExpressTemplateDetail;
import co.yixiang.modules.shop.mapper.YxExpressTemplateMapper;
import co.yixiang.modules.shop.service.YxExpressTemplateDetailService;
import co.yixiang.modules.shop.service.YxExpressTemplateService;
import co.yixiang.modules.shop.web.param.YxExpressTemplateQueryParam;
import co.yixiang.modules.shop.web.vo.YxExpressTemplateQueryVo;
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
 * 物流运费模板 服务实现类
 * </p>
 *
 * @author visa
 * @since 2020-11-28
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class YxExpressTemplateServiceImpl extends BaseServiceImpl<YxExpressTemplateMapper, YxExpressTemplate> implements YxExpressTemplateService {

    @Autowired
    private YxExpressTemplateMapper yxExpressTemplateMapper;

    @Autowired
    private YxExpressTemplateDetailService yxExpressTemplateDetailService;

    @Override
    public YxExpressTemplateQueryVo getYxExpressTemplateById(Serializable id) throws Exception{

        YxExpressTemplateQueryVo templateQueryVo = yxExpressTemplateMapper.getYxExpressTemplateById(id);

        templateQueryVo.setDetails( yxExpressTemplateDetailService.list(new QueryWrapper<YxExpressTemplateDetail>().eq("template_id",templateQueryVo.getId())));

        return templateQueryVo;
    }

    @Override
    public Paging<YxExpressTemplateQueryVo> getYxExpressTemplatePageList(YxExpressTemplateQueryParam yxExpressTemplateQueryParam) throws Exception{
        Page page = setPageParam(yxExpressTemplateQueryParam,OrderItem.desc("create_time"));
        QueryWrapper queryWrapper = QueryHelpPlus.getPredicate(YxExpressTemplateQueryParam.class, yxExpressTemplateQueryParam);
        IPage<YxExpressTemplate> iPage = yxExpressTemplateMapper.selectPage(page,queryWrapper);

        for(YxExpressTemplate templateQueryVo:iPage.getRecords()) {
            templateQueryVo.setDetails( yxExpressTemplateDetailService.list(new QueryWrapper<YxExpressTemplateDetail>().eq("template_id",templateQueryVo.getId())));
        }

        return new Paging(iPage);
    }

    @Override
    public Boolean saveTemplate(YxExpressTemplate yxExpressTemplat) {

        saveOrUpdate(yxExpressTemplat);

        yxExpressTemplateDetailService.remove(new QueryWrapper<YxExpressTemplateDetail>().eq("template_id",yxExpressTemplat.getId()));

        for(  YxExpressTemplateDetail detail : yxExpressTemplat.getDetails()) {
            detail.setTemplateId(yxExpressTemplat.getId());
        }
        yxExpressTemplateDetailService.saveBatch(yxExpressTemplat.getDetails());

        return true;
    }
}
