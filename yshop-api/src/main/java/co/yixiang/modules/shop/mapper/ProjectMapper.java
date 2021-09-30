package co.yixiang.modules.shop.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import co.yixiang.modules.shop.entity.Project;
import co.yixiang.modules.shop.web.param.ProjectQueryParam;
import co.yixiang.modules.shop.web.vo.ProjectQueryVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.io.Serializable;

/**
 * <p>
 * 项目 Mapper 接口
 * </p>
 *
 * @author visa
 * @since 2021-03-05
 */
@Repository
public interface ProjectMapper extends BaseMapper<Project> {

    /**
     * 根据ID获取查询对象
     * @param id
     * @return
     */
    ProjectQueryVo getProjectById(Serializable id);

    /**
     * 获取分页对象
     * @param page
     * @param projectQueryParam
     * @return
     */
    IPage<ProjectQueryVo> getProjectPageList(@Param("page") Page page, @Param("param") ProjectQueryParam projectQueryParam);

}
