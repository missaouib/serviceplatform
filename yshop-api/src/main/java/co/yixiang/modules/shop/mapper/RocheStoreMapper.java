package co.yixiang.modules.shop.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import co.yixiang.modules.shop.entity.RocheStore;
import co.yixiang.modules.shop.web.param.RocheStoreQueryParam;
import co.yixiang.modules.shop.web.vo.RocheStoreQueryVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.io.Serializable;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author visa
 * @since 2020-12-28
 */
@Repository
public interface RocheStoreMapper extends BaseMapper<RocheStore> {

    /**
     * 根据ID获取查询对象
     * @param id
     * @return
     */
    RocheStoreQueryVo getRocheStoreById(Serializable id);

    /**
     * 获取分页对象
     * @param page
     * @param rocheStoreQueryParam
     * @return
     */
    IPage<RocheStoreQueryVo> getRocheStorePageList(@Param("page") Page page, @Param("param") RocheStoreQueryParam rocheStoreQueryParam);

}
