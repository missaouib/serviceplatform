package co.yixiang.modules.shop.service.impl;

import co.yixiang.common.utils.QueryHelpPlus;
import co.yixiang.modules.shop.entity.Manufacturer;
import co.yixiang.modules.shop.mapper.ManufacturerMapper;
import co.yixiang.modules.shop.service.ManufacturerService;
import co.yixiang.modules.shop.web.param.ManufacturerQueryParam;
import co.yixiang.modules.shop.web.vo.ManufacturerQueryVo;
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
 * 生产厂家主数据表 服务实现类
 * </p>
 *
 * @author visa
 * @since 2020-12-07
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class ManufacturerServiceImpl extends BaseServiceImpl<ManufacturerMapper, Manufacturer> implements ManufacturerService {

    @Autowired
    private ManufacturerMapper manufacturerMapper;

    @Override
    public ManufacturerQueryVo getManufacturerById(Serializable id) throws Exception{
        return manufacturerMapper.getManufacturerById(id);
    }

    @Override
    public Paging<ManufacturerQueryVo> getManufacturerPageList(ManufacturerQueryParam manufacturerQueryParam) throws Exception{
        Page page = setPageParam(manufacturerQueryParam,OrderItem.desc("create_time"));
        QueryWrapper queryWrapper = QueryHelpPlus.getPredicate(ManufacturerQueryParam.class, manufacturerQueryParam);
        IPage<Manufacturer> iPage = manufacturerMapper.selectPage(page,queryWrapper);
        return new Paging(iPage);
    }

}
