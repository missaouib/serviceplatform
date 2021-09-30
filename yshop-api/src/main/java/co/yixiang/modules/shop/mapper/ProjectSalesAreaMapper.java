package co.yixiang.modules.shop.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import co.yixiang.modules.shop.entity.ProjectSalesArea;
import co.yixiang.modules.shop.web.param.ProjectSalesAreaQueryParam;
import co.yixiang.modules.shop.web.vo.ProjectSalesAreaQueryVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.io.Serializable;

/**
 * <p>
 * 项目配置销售省份 Mapper 接口
 * </p>
 *
 * @author visa
 * @since 2021-04-12
 */
@Repository
public interface ProjectSalesAreaMapper extends BaseMapper<ProjectSalesArea> {

    /**
     * 根据ID获取查询对象
     * @param id
     * @return
     */
    ProjectSalesAreaQueryVo getProjectSalesAreaById(Serializable id);

    /**
     * 获取分页对象
     * @param page
     * @param projectSalesAreaQueryParam
     * @return
     */
    IPage<ProjectSalesAreaQueryVo> getProjectSalesAreaPageList(@Param("page") Page page, @Param("param") ProjectSalesAreaQueryParam projectSalesAreaQueryParam);

}
