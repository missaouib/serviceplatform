package co.yixiang.modules.user.service.impl;

import co.yixiang.modules.user.entity.InnerEmployee;
import co.yixiang.modules.user.mapper.InnerEmployeeMapper;
import co.yixiang.modules.user.service.InnerEmployeeService;
import co.yixiang.modules.user.web.param.InnerEmployeeQueryParam;
import co.yixiang.modules.user.web.vo.InnerEmployeeQueryVo;
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
 * 内部员工表 服务实现类
 * </p>
 *
 * @author visazhou
 * @since 2020-05-20
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class InnerEmployeeServiceImpl extends BaseServiceImpl<InnerEmployeeMapper, InnerEmployee> implements InnerEmployeeService {

    @Autowired
    private InnerEmployeeMapper innerEmployeeMapper;

    @Override
    public InnerEmployeeQueryVo getInnerEmployeeById(Serializable id) throws Exception{
        return innerEmployeeMapper.getInnerEmployeeById(id);
    }

    @Override
    public Paging<InnerEmployeeQueryVo> getInnerEmployeePageList(InnerEmployeeQueryParam innerEmployeeQueryParam) throws Exception{
        Page page = setPageParam(innerEmployeeQueryParam,OrderItem.desc("create_time"));
        IPage<InnerEmployeeQueryVo> iPage = innerEmployeeMapper.getInnerEmployeePageList(page,innerEmployeeQueryParam);
        return new Paging(iPage);
    }

}
