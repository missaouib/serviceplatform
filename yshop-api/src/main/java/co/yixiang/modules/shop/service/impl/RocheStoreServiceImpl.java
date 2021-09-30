package co.yixiang.modules.shop.service.impl;

import co.yixiang.common.utils.QueryHelpPlus;
import co.yixiang.modules.shop.entity.RocheStore;
import co.yixiang.modules.shop.mapper.RocheStoreMapper;
import co.yixiang.modules.shop.service.RocheStoreService;
import co.yixiang.modules.shop.web.param.RocheStoreQueryParam;
import co.yixiang.modules.shop.web.vo.RocheStoreQueryVo;
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
 *  服务实现类
 * </p>
 *
 * @author visa
 * @since 2020-12-28
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class RocheStoreServiceImpl extends BaseServiceImpl<RocheStoreMapper, RocheStore> implements RocheStoreService {

    @Autowired
    private RocheStoreMapper rocheStoreMapper;

    @Override
    public RocheStoreQueryVo getRocheStoreById(Serializable id) throws Exception{
        return rocheStoreMapper.getRocheStoreById(id);
    }

    @Override
    public Paging<RocheStoreQueryVo> getRocheStorePageList(RocheStoreQueryParam rocheStoreQueryParam) throws Exception{
        Page page = setPageParam(rocheStoreQueryParam,OrderItem.desc("create_time"));
        QueryWrapper queryWrapper = QueryHelpPlus.getPredicate(RocheStoreQueryParam.class, rocheStoreQueryParam);
        IPage<RocheStoreQueryVo> iPage = rocheStoreMapper.selectPage(page,queryWrapper);
        return new Paging(iPage);
    }

}
