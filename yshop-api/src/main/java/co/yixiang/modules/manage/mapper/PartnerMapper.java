package co.yixiang.modules.manage.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import co.yixiang.modules.manage.entity.Partner;
import co.yixiang.modules.manage.web.param.PartnerQueryParam;
import co.yixiang.modules.manage.web.vo.PartnerQueryVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.io.Serializable;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author visazhou
 * @since 2020-05-20
 */
@Repository
public interface PartnerMapper extends BaseMapper<Partner> {

    /**
     * 根据ID获取查询对象
     * @param id
     * @return
     */
    PartnerQueryVo getPartnerById(Serializable id);

    /**
     * 获取分页对象
     * @param page
     * @param partnerQueryParam
     * @return
     */
    IPage<PartnerQueryVo> getPartnerPageList(@Param("page") Page page, @Param("param") PartnerQueryParam partnerQueryParam);

}
