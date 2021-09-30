package co.yixiang.modules.shop.service.impl;

import co.yixiang.common.utils.QueryHelpPlus;
import co.yixiang.modules.shop.entity.Project;
import co.yixiang.modules.shop.mapper.ProjectMapper;
import co.yixiang.modules.shop.service.ProjectService;
import co.yixiang.modules.shop.web.param.ProjectQueryParam;
import co.yixiang.modules.shop.web.vo.ProjectQueryVo;
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
 * 项目 服务实现类
 * </p>
 *
 * @author visa
 * @since 2021-03-05
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class ProjectServiceImpl extends BaseServiceImpl<ProjectMapper, Project> implements ProjectService {

    @Autowired
    private ProjectMapper projectMapper;

    @Override
    public ProjectQueryVo getProjectById(Serializable id) throws Exception{
        return projectMapper.getProjectById(id);
    }

    @Override
    public Paging<ProjectQueryVo> getProjectPageList(ProjectQueryParam projectQueryParam) throws Exception{
        Page page = setPageParam(projectQueryParam,OrderItem.desc("create_time"));
        QueryWrapper queryWrapper = QueryHelpPlus.getPredicate(ProjectQueryParam.class, projectQueryParam);
        IPage<ProjectQueryVo> iPage = projectMapper.selectPage(page,queryWrapper);
        return new Paging(iPage);
    }

}
