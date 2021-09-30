package co.yixiang.modules.shop.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import co.yixiang.modules.shop.entity.YxUserAppointment;
import co.yixiang.modules.shop.web.param.YxUserAppointmentQueryParam;
import co.yixiang.modules.shop.web.vo.YxUserAppointmentQueryVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.io.Serializable;

/**
 * <p>
 * 预约活动 Mapper 接口
 * </p>
 *
 * @author visazhou
 * @since 2020-06-05
 */
@Repository
public interface YxUserAppointmentMapper extends BaseMapper<YxUserAppointment> {

    /**
     * 根据ID获取查询对象
     * @param id
     * @return
     */
    YxUserAppointmentQueryVo getYxUserAppointmentById(Serializable id);

    /**
     * 获取分页对象
     * @param page
     * @param yxUserAppointmentQueryParam
     * @return
     */
    IPage<YxUserAppointmentQueryVo> getYxUserAppointmentPageList(@Param("page") Page page, @Param("param") YxUserAppointmentQueryParam yxUserAppointmentQueryParam);

}
