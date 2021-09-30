package co.yixiang.modules.yiyaobao.service.impl;

import co.yixiang.modules.yiyaobao.entity.MdCountry;
import co.yixiang.modules.yiyaobao.mapper.MdCountryMapper;
import co.yixiang.modules.yiyaobao.service.MdCountryService;
import co.yixiang.modules.yiyaobao.web.param.MdCountryQueryParam;
import co.yixiang.modules.yiyaobao.web.vo.MdCountryQueryVo;
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
 * 国家地区信息表 服务实现类
 * </p>
 *
 * @author visazhou
 * @since 2020-05-16
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class MdCountryServiceImpl extends BaseServiceImpl<MdCountryMapper, MdCountry> implements MdCountryService {

    @Autowired
    private MdCountryMapper mdCountryMapper;

    @Override
    public MdCountryQueryVo getMdCountryById(Serializable id) throws Exception{
        return mdCountryMapper.getMdCountryById(id);
    }

    @Override
    public Paging<MdCountryQueryVo> getMdCountryPageList(MdCountryQueryParam mdCountryQueryParam) throws Exception{
        Page page = setPageParam(mdCountryQueryParam,OrderItem.desc("create_time"));
        IPage<MdCountryQueryVo> iPage = mdCountryMapper.getMdCountryPageList(page,mdCountryQueryParam);
        return new Paging(iPage);
    }

}
