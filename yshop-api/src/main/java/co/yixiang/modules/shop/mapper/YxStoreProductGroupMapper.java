package co.yixiang.modules.shop.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import co.yixiang.modules.shop.entity.YxStoreProductGroup;
import co.yixiang.modules.shop.web.param.YxStoreProductGroupQueryParam;
import co.yixiang.modules.shop.web.vo.YxStoreProductGroupQueryVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.io.Serializable;

/**
 * <p>
 * 商品组合 Mapper 接口
 * </p>
 *
 * @author visa
 * @since 2021-08-19
 */
@Repository
public interface YxStoreProductGroupMapper extends BaseMapper<YxStoreProductGroup> {

    /**
     * 根据ID获取查询对象
     * @param id
     * @return
     */
    YxStoreProductGroupQueryVo getYxStoreProductGroupById(Serializable id);

    /**
     * 获取分页对象
     * @param page
     * @param yxStoreProductGroupQueryParam
     * @return
     */
    IPage<YxStoreProductGroupQueryVo> getYxStoreProductGroupPageList(@Param("page") Page page, @Param("param") YxStoreProductGroupQueryParam yxStoreProductGroupQueryParam);

}
