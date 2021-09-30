package co.yixiang.modules.shop.service.impl;

import co.yixiang.modules.shop.entity.YxUserSearch;
import co.yixiang.modules.shop.mapper.YxUserSearchMapper;
import co.yixiang.modules.shop.service.YxUserSearchService;
import co.yixiang.modules.shop.web.param.YxUserSearchQueryParam;
import co.yixiang.modules.shop.web.vo.YxUserSearchQueryVo;
import co.yixiang.common.service.impl.BaseServiceImpl;
import co.yixiang.common.web.vo.Paging;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
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
 * 用户搜索词 服务实现类
 * </p>
 *
 * @author visazhou
 * @since 2020-06-03
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class YxUserSearchServiceImpl extends BaseServiceImpl<YxUserSearchMapper, YxUserSearch> implements YxUserSearchService {

    @Autowired
    private YxUserSearchMapper yxUserSearchMapper;

    @Override
    public YxUserSearchQueryVo getYxUserSearchById(Serializable id) throws Exception{
        return yxUserSearchMapper.getYxUserSearchById(id);
    }

    @Override
    public Paging<YxUserSearchQueryVo> getYxUserSearchPageList(YxUserSearchQueryParam yxUserSearchQueryParam) throws Exception{
        Page page = setPageParam(yxUserSearchQueryParam,OrderItem.desc("add_time"));
        IPage<YxUserSearchQueryVo> iPage = yxUserSearchMapper.getYxUserSearchPageList(page,yxUserSearchQueryParam);
        return new Paging(iPage);
    }

    @Override
    public Boolean deleteYxUserSearchAll(Integer uid) {
        UpdateWrapper<YxUserSearch> updateWrapper = new UpdateWrapper();
        updateWrapper.set("is_del",1);
        updateWrapper.eq("uid",uid);
        return this.update(updateWrapper);

    }
}
