package co.yixiang.modules.user.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import co.yixiang.modules.user.entity.InnerEmployee;
import co.yixiang.modules.user.web.param.InnerEmployeeQueryParam;
import co.yixiang.modules.user.web.vo.InnerEmployeeQueryVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.io.Serializable;

/**
 * <p>
 * 内部员工表 Mapper 接口
 * </p>
 *
 * @author visazhou
 * @since 2020-05-20
 */
@Repository
public interface InnerEmployeeMapper extends BaseMapper<InnerEmployee> {

    /**
     * 根据ID获取查询对象
     * @param id
     * @return
     */
    InnerEmployeeQueryVo getInnerEmployeeById(Serializable id);

    /**
     * 获取分页对象
     * @param page
     * @param innerEmployeeQueryParam
     * @return
     */
    IPage<InnerEmployeeQueryVo> getInnerEmployeePageList(@Param("page") Page page, @Param("param") InnerEmployeeQueryParam innerEmployeeQueryParam);

}
