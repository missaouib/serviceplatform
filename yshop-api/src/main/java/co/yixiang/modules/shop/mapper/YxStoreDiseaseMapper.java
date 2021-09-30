package co.yixiang.modules.shop.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import co.yixiang.modules.shop.entity.YxStoreDisease;
import co.yixiang.modules.shop.web.param.YxStoreDiseaseQueryParam;
import co.yixiang.modules.shop.web.vo.YxStoreDiseaseQueryVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.io.Serializable;

/**
 * <p>
 * 病种 Mapper 接口
 * </p>
 *
 * @author visazhou
 * @since 2020-06-03
 */
@Repository
public interface YxStoreDiseaseMapper extends BaseMapper<YxStoreDisease> {

    /**
     * 根据ID获取查询对象
     * @param id
     * @return
     */
    YxStoreDiseaseQueryVo getYxStoreDiseaseById(Serializable id);

    /**
     * 获取分页对象
     * @param page
     * @param yxStoreDiseaseQueryParam
     * @return
     */
    IPage<YxStoreDiseaseQueryVo> getYxStoreDiseasePageList(@Param("page") Page page, @Param("param") YxStoreDiseaseQueryParam yxStoreDiseaseQueryParam);


}
