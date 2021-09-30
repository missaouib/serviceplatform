package co.yixiang.modules.order.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import co.yixiang.modules.order.entity.UserAgreement;
import co.yixiang.modules.order.web.param.UserAgreementQueryParam;
import co.yixiang.modules.order.web.vo.UserAgreementQueryVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.io.Serializable;

/**
 * <p>
 * 用户同意书 Mapper 接口
 * </p>
 *
 * @author visa
 * @since 2020-11-30
 */
@Repository
public interface UserAgreementMapper extends BaseMapper<UserAgreement> {

    /**
     * 根据ID获取查询对象
     * @param id
     * @return
     */
    UserAgreementQueryVo getUserAgreementById(Serializable id);

    /**
     * 获取分页对象
     * @param page
     * @param userAgreementQueryParam
     * @return
     */
    IPage<UserAgreementQueryVo> getUserAgreementPageList(@Param("page") Page page, @Param("param") UserAgreementQueryParam userAgreementQueryParam);

}
