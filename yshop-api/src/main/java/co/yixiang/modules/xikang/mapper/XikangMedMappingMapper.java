package co.yixiang.modules.xikang.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import co.yixiang.modules.xikang.entity.XikangMedMapping;
import co.yixiang.modules.xikang.web.param.XikangMedMappingQueryParam;
import co.yixiang.modules.xikang.web.vo.XikangMedMappingQueryVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.io.Serializable;

/**
 * <p>
 * 熙康医院与商城药品的映射 Mapper 接口
 * </p>
 *
 * @author visa
 * @since 2020-12-30
 */
@Repository
public interface XikangMedMappingMapper extends BaseMapper<XikangMedMapping> {

    /**
     * 根据ID获取查询对象
     * @param id
     * @return
     */
    XikangMedMappingQueryVo getXikangMedMappingById(Serializable id);

    /**
     * 获取分页对象
     * @param page
     * @param xikangMedMappingQueryParam
     * @return
     */
    IPage<XikangMedMappingQueryVo> getXikangMedMappingPageList(@Param("page") Page page, @Param("param") XikangMedMappingQueryParam xikangMedMappingQueryParam);

}
