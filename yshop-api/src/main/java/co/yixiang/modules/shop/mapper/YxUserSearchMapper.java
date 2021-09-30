package co.yixiang.modules.shop.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import co.yixiang.modules.shop.entity.YxUserSearch;
import co.yixiang.modules.shop.web.param.YxUserSearchQueryParam;
import co.yixiang.modules.shop.web.vo.YxUserSearchQueryVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.io.Serializable;

/**
 * <p>
 * 用户搜索词 Mapper 接口
 * </p>
 *
 * @author visazhou
 * @since 2020-06-03
 */
@Repository
public interface YxUserSearchMapper extends BaseMapper<YxUserSearch> {

    /**
     * 根据ID获取查询对象
     * @param id
     * @return
     */
    YxUserSearchQueryVo getYxUserSearchById(Serializable id);

    /**
     * 获取分页对象
     * @param page
     * @param yxUserSearchQueryParam
     * @return
     */
    IPage<YxUserSearchQueryVo> getYxUserSearchPageList(@Param("page") Page page, @Param("param") YxUserSearchQueryParam yxUserSearchQueryParam);

}
