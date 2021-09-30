package co.yixiang.modules.order.service.impl;

import co.yixiang.common.utils.QueryHelpPlus;
import co.yixiang.modules.order.entity.UserAgreement;
import co.yixiang.modules.order.mapper.UserAgreementMapper;
import co.yixiang.modules.order.service.UserAgreementService;
import co.yixiang.modules.order.web.param.UserAgreementQueryParam;
import co.yixiang.modules.order.web.vo.UserAgreementQueryVo;
import co.yixiang.common.service.impl.BaseServiceImpl;
import co.yixiang.common.web.vo.Paging;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import java.io.Serializable;


/**
 * <p>
 * 用户同意书 服务实现类
 * </p>
 *
 * @author visa
 * @since 2020-11-30
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class UserAgreementServiceImpl extends BaseServiceImpl<UserAgreementMapper, UserAgreement> implements UserAgreementService {

    @Autowired
    private UserAgreementMapper userAgreementMapper;

    @Override
    public UserAgreementQueryVo getUserAgreementById(Serializable id) throws Exception{
        return userAgreementMapper.getUserAgreementById(id);
    }

    @Override
    public Paging<UserAgreementQueryVo> getUserAgreementPageList(UserAgreementQueryParam userAgreementQueryParam) throws Exception{
        Page page = setPageParam(userAgreementQueryParam,OrderItem.desc("create_time"));
        QueryWrapper queryWrapper = QueryHelpPlus.getPredicate(UserAgreementQueryParam.class, userAgreementQueryParam);
        IPage<UserAgreement> iPage = userAgreementMapper.selectPage(page,queryWrapper);
        return new Paging(iPage);
    }

}
