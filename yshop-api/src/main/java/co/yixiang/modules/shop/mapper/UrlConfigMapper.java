package co.yixiang.modules.shop.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import co.yixiang.modules.shop.entity.UrlConfig;
import co.yixiang.modules.shop.web.param.UrlConfigQueryParam;
import co.yixiang.modules.shop.web.vo.UrlConfigQueryVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.io.Serializable;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author visazhou
 * @since 2020-06-10
 */
@Repository
public interface UrlConfigMapper extends BaseMapper<UrlConfig> {

    /**
     * 根据ID获取查询对象
     * @param id
     * @return
     */
    UrlConfigQueryVo getUrlConfigById(Serializable id);

    /**
     * 获取分页对象
     * @param page
     * @param urlConfigQueryParam
     * @return
     */
    IPage<UrlConfigQueryVo> getUrlConfigPageList(@Param("page") Page page, @Param("param") UrlConfigQueryParam urlConfigQueryParam);

}
