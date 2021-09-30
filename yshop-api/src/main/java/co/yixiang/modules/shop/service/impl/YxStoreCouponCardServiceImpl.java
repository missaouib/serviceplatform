package co.yixiang.modules.shop.service.impl;

import co.yixiang.common.utils.QueryHelpPlus;
import co.yixiang.modules.shop.entity.YxStoreCouponCard;
import co.yixiang.modules.shop.mapper.YxStoreCouponCardMapper;
import co.yixiang.modules.shop.service.YxStoreCouponCardService;
import co.yixiang.modules.shop.web.param.YxStoreCouponCardQueryParam;
import co.yixiang.modules.shop.web.vo.YxStoreCouponCardQueryVo;
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
 * 优惠券发放记录表 服务实现类
 * </p>
 *
 * @author visa
 * @since 2020-12-10
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class YxStoreCouponCardServiceImpl extends BaseServiceImpl<YxStoreCouponCardMapper, YxStoreCouponCard> implements YxStoreCouponCardService {

    @Autowired
    private YxStoreCouponCardMapper yxStoreCouponCardMapper;

    @Override
    public YxStoreCouponCardQueryVo getYxStoreCouponCardById(Serializable id) throws Exception{
        return yxStoreCouponCardMapper.getYxStoreCouponCardById(id);
    }

    @Override
    public Paging<YxStoreCouponCardQueryVo> getYxStoreCouponCardPageList(YxStoreCouponCardQueryParam yxStoreCouponCardQueryParam) throws Exception{
        Page page = setPageParam(yxStoreCouponCardQueryParam,OrderItem.desc("create_time"));
        QueryWrapper queryWrapper = QueryHelpPlus.getPredicate(YxStoreCouponCardQueryParam.class, yxStoreCouponCardQueryParam);
        IPage<YxStoreCouponCard> iPage = yxStoreCouponCardMapper.selectPage(page,queryWrapper);
        return new Paging(iPage);
    }

}
