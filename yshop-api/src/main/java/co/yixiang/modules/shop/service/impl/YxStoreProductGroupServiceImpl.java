package co.yixiang.modules.shop.service.impl;

import co.yixiang.common.utils.QueryHelpPlus;
import co.yixiang.modules.shop.entity.YxStoreProductGroup;
import co.yixiang.modules.shop.mapper.YxStoreProductGroupMapper;
import co.yixiang.modules.shop.service.YxStoreProductGroupService;
import co.yixiang.modules.shop.web.param.YxStoreProductGroupQueryParam;
import co.yixiang.modules.shop.web.vo.YxStoreProductGroupQueryVo;
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
 * 商品组合 服务实现类
 * </p>
 *
 * @author visa
 * @since 2021-08-19
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class YxStoreProductGroupServiceImpl extends BaseServiceImpl<YxStoreProductGroupMapper, YxStoreProductGroup> implements YxStoreProductGroupService {

    @Autowired
    private YxStoreProductGroupMapper yxStoreProductGroupMapper;

    @Override
    public YxStoreProductGroupQueryVo getYxStoreProductGroupById(Serializable id) throws Exception{
        return yxStoreProductGroupMapper.getYxStoreProductGroupById(id);
    }

    @Override
    public Paging<YxStoreProductGroupQueryVo> getYxStoreProductGroupPageList(YxStoreProductGroupQueryParam yxStoreProductGroupQueryParam) throws Exception{
        Page page = setPageParam(yxStoreProductGroupQueryParam,OrderItem.desc("create_time"));
        QueryWrapper queryWrapper = QueryHelpPlus.getPredicate(YxStoreProductGroupQueryParam.class, yxStoreProductGroupQueryParam);
        IPage<YxStoreProductGroupQueryVo> iPage = yxStoreProductGroupMapper.selectPage(page,queryWrapper);
        return new Paging(iPage);
    }

}
