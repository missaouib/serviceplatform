package co.yixiang.modules.shop.mapper;

import co.yixiang.modules.shop.web.dto.Store4ProjectDTO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import co.yixiang.modules.shop.entity.Product4project;
import co.yixiang.modules.shop.web.param.Product4projectQueryParam;
import co.yixiang.modules.shop.web.vo.Product4projectQueryVo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.util.List;

/**
 * <p>
 * 项目对应的药品 Mapper 接口
 * </p>
 *
 * @author visazhou
 * @since 2020-06-11
 */
@Repository
public interface Product4projectMapper extends BaseMapper<Product4project> {

    /**
     * 根据ID获取查询对象
     * @param id
     * @return
     */
    Product4projectQueryVo getProduct4projectById(Serializable id);

    /**
     * 获取分页对象
     * @param page
     * @param product4projectQueryParam
     * @return
     */
    IPage<Product4projectQueryVo> getProduct4projectPageList(@Param("page") Page page, @Param("param") Product4projectQueryParam product4projectQueryParam);

    @Select("SELECT DISTINCT yss.id,yss.name,yss.phone,yss.detailed_address AS address,yss.image  \n" +
            "  FROM product4project p,yx_system_store yss\n" +
            "  WHERE p.store_id = yss.id\n" +
            "  AND p.project_no = #{projectNo} and p.is_del = 0")
    List<Store4ProjectDTO> queryStore(@Param("projectNo") String projectNo);
}
