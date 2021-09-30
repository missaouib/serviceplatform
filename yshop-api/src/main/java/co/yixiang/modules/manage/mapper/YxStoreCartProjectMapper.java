package co.yixiang.modules.manage.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import co.yixiang.modules.manage.entity.YxStoreCartProject;
import co.yixiang.modules.manage.web.param.YxStoreCartProjectQueryParam;
import co.yixiang.modules.manage.web.vo.YxStoreCartProjectQueryVo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.io.Serializable;

/**
 * <p>
 * 购物车表-项目 Mapper 接口
 * </p>
 *
 * @author visa
 * @since 2020-08-24
 */
@Repository
public interface YxStoreCartProjectMapper extends BaseMapper<YxStoreCartProject> {

    /**
     * 根据ID获取查询对象
     * @param id
     * @return
     */
    YxStoreCartProjectQueryVo getYxStoreCartProjectById(Serializable id);

    /**
     * 获取分页对象
     * @param page
     * @param yxStoreCartProjectQueryParam
     * @return
     */
    IPage<YxStoreCartProjectQueryVo> getYxStoreCartProjectPageList(@Param("page") Page page, @Param("param") YxStoreCartProjectQueryParam yxStoreCartProjectQueryParam);

    @Select("select IFNULL(sum(cart_num),0) from yx_store_cart_project " +
            "where is_pay=0 and is_del=0 and is_new=0 and uid=#{uid} and type=#{type} and project_code = #{projectCode}")
    int cartSum(@Param("uid") int uid,@Param("type") String type,@Param("projectCode") String projectCode);
}
