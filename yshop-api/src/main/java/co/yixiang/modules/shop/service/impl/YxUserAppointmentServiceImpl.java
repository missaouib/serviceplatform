package co.yixiang.modules.shop.service.impl;

import co.yixiang.modules.shop.entity.YxUserAppointment;
import co.yixiang.modules.shop.mapper.YxUserAppointmentMapper;
import co.yixiang.modules.shop.service.YxUserAppointmentService;
import co.yixiang.modules.shop.web.param.YxUserAppointmentQueryParam;
import co.yixiang.modules.shop.web.vo.YxUserAppointmentQueryVo;
import co.yixiang.common.service.impl.BaseServiceImpl;
import co.yixiang.common.web.vo.Paging;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import java.io.Serializable;


/**
 * <p>
 * 预约活动 服务实现类
 * </p>
 *
 * @author visazhou
 * @since 2020-06-05
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class YxUserAppointmentServiceImpl extends BaseServiceImpl<YxUserAppointmentMapper, YxUserAppointment> implements YxUserAppointmentService {

    @Autowired
    private YxUserAppointmentMapper yxUserAppointmentMapper;

    @Override
    public YxUserAppointmentQueryVo getYxUserAppointmentById(Serializable id) throws Exception{
        return yxUserAppointmentMapper.getYxUserAppointmentById(id);
    }

    @Override
    public Paging<YxUserAppointmentQueryVo> getYxUserAppointmentPageList(YxUserAppointmentQueryParam yxUserAppointmentQueryParam) throws Exception{
        Page page = setPageParam(yxUserAppointmentQueryParam,OrderItem.desc("create_time"));
        IPage<YxUserAppointmentQueryVo> iPage = yxUserAppointmentMapper.getYxUserAppointmentPageList(page,yxUserAppointmentQueryParam);
        return new Paging(iPage);
    }

}
