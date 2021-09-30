package co.yixiang.modules.shop.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import co.yixiang.modules.shop.entity.EnterpriseTopics;
import co.yixiang.modules.shop.web.param.EnterpriseTopicsQueryParam;
import co.yixiang.modules.shop.web.vo.EnterpriseTopicsQueryVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.io.Serializable;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author visazhou
 * @since 2020-06-05
 */
@Repository
public interface EnterpriseTopicsMapper extends BaseMapper<EnterpriseTopics> {

    /**
     * 根据ID获取查询对象
     * @param id
     * @return
     */
    EnterpriseTopicsQueryVo getEnterpriseTopicsById(Serializable id);

    /**
     * 获取分页对象
     * @param page
     * @param enterpriseTopicsQueryParam
     * @return
     */
    IPage<EnterpriseTopicsQueryVo> getEnterpriseTopicsPageList(@Param("page") Page page, @Param("param") EnterpriseTopicsQueryParam enterpriseTopicsQueryParam);

}
