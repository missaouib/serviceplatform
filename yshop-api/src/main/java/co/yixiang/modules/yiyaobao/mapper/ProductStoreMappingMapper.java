package co.yixiang.modules.yiyaobao.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import co.yixiang.modules.yiyaobao.entity.ProductStoreMapping;
import co.yixiang.modules.yiyaobao.web.param.ProductStoreMappingQueryParam;
import co.yixiang.modules.yiyaobao.web.vo.ProductStoreMappingQueryVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.io.Serializable;

/**
 * <p>
 * 商品-药店-价格配置 Mapper 接口
 * </p>
 *
 * @author visazhou
 * @since 2020-05-18
 */
@Repository
public interface ProductStoreMappingMapper extends BaseMapper<ProductStoreMapping> {

    /**
     * 根据ID获取查询对象
     * @param id
     * @return
     */
    ProductStoreMappingQueryVo getProductStoreMappingById(Serializable id);

    /**
     * 获取分页对象
     * @param page
     * @param productStoreMappingQueryParam
     * @return
     */
    IPage<ProductStoreMappingQueryVo> getProductStoreMappingPageList(@Param("page") Page page, @Param("param") ProductStoreMappingQueryParam productStoreMappingQueryParam);

}
