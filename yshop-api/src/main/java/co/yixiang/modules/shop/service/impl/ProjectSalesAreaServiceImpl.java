package co.yixiang.modules.shop.service.impl;

import co.yixiang.common.utils.QueryHelpPlus;
import co.yixiang.modules.shop.entity.ProjectSalesArea;
import co.yixiang.modules.shop.mapper.ProjectSalesAreaMapper;
import co.yixiang.modules.shop.service.ProjectSalesAreaService;
import co.yixiang.modules.shop.web.param.ProjectSalesAreaQueryParam;
import co.yixiang.modules.shop.web.vo.ProjectSalesAreaQueryVo;
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
 * 项目配置销售省份 服务实现类
 * </p>
 *
 * @author visa
 * @since 2021-04-12
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class ProjectSalesAreaServiceImpl extends BaseServiceImpl<ProjectSalesAreaMapper, ProjectSalesArea> implements ProjectSalesAreaService {

    @Autowired
    private ProjectSalesAreaMapper projectSalesAreaMapper;

    @Override
    public ProjectSalesAreaQueryVo getProjectSalesAreaById(Serializable id) throws Exception{
        return projectSalesAreaMapper.getProjectSalesAreaById(id);
    }

    @Override
    public Paging<ProjectSalesAreaQueryVo> getProjectSalesAreaPageList(ProjectSalesAreaQueryParam projectSalesAreaQueryParam) throws Exception{
        Page page = setPageParam(projectSalesAreaQueryParam,OrderItem.desc("create_time"));
        QueryWrapper queryWrapper = QueryHelpPlus.getPredicate(ProjectSalesAreaQueryParam.class, projectSalesAreaQueryParam);
        IPage<ProjectSalesAreaQueryVo> iPage = projectSalesAreaMapper.selectPage(page,queryWrapper);
        return new Paging(iPage);
    }

}
