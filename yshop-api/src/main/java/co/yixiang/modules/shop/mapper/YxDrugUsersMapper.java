package co.yixiang.modules.shop.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import co.yixiang.modules.shop.entity.YxDrugUsers;
import co.yixiang.modules.shop.web.param.YxDrugUsersQueryParam;
import co.yixiang.modules.shop.web.vo.YxDrugUsersQueryVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.io.Serializable;

/**
 * <p>
 * 用药人列表 Mapper 接口
 * </p>
 *
 * @author visa
 * @since 2020-12-20
 */
@Repository
public interface YxDrugUsersMapper extends BaseMapper<YxDrugUsers> {

    /**
     * 根据ID获取查询对象
     * @param id
     * @return
     */
    YxDrugUsersQueryVo getYxDrugUsersById(Serializable id);

    /**
     * 获取分页对象
     * @param page
     * @param yxDrugUsersQueryParam
     * @return
     */
    IPage<YxDrugUsersQueryVo> getYxDrugUsersPageList(@Param("page") Page page, @Param("param") YxDrugUsersQueryParam yxDrugUsersQueryParam);

}
