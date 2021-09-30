package co.yixiang.mp.service.impl;

import co.yixiang.common.service.impl.BaseServiceImpl;
import co.yixiang.common.web.vo.Paging;
import co.yixiang.mp.domain.DictDetail;
import co.yixiang.mp.service.DictDetailService;
import co.yixiang.mp.service.mapper.DictDetailMapper;
import co.yixiang.mp.rest.param.DictDetailQueryParam;
import co.yixiang.mp.rest.vo.DictDetailQueryVo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.List;


/**
 * <p>
 * 数据字典详情 服务实现类
 * </p>
 *
 * @author visazhou
 * @since 2020-07-13
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class DictDetailServiceImpl extends BaseServiceImpl<DictDetailMapper, DictDetail> implements DictDetailService {

    @Autowired
    private DictDetailMapper dictDetailMapper;

    @Override
    public DictDetailQueryVo getDictDetailById(Serializable id) throws Exception{
        return dictDetailMapper.getDictDetailById(id);
    }

    @Override
    public Paging<DictDetailQueryVo> getDictDetailPageList(DictDetailQueryParam dictDetailQueryParam) throws Exception{
        Page page = setPageParam(dictDetailQueryParam,OrderItem.desc("create_time"));
        IPage<DictDetailQueryVo> iPage = dictDetailMapper.getDictDetailPageList(page,dictDetailQueryParam);
        return new Paging(iPage);
    }

    @Override
    public List<DictDetail> getDictDetailList(DictDetailQueryParam dictDetailQueryParam) {
        return dictDetailMapper.getDictDetailList(dictDetailQueryParam);
    }

    @Override
    //@Cacheable
    public List<DictDetail> queryAll(DictDetailQueryParam dictDetailQueryParam){
        List<DictDetail> list =  baseMapper.selectDictDetailList(dictDetailQueryParam.getLabel(),dictDetailQueryParam.getName());
        return list;
    }

    @Override
    public List<DictDetail> findDetails(List<String> values, String dicName) {
        return baseMapper.findDetails(values,dicName);
    }
}
