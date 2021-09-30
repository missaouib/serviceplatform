package co.yixiang.modules.manage.service.impl;

import cn.hutool.core.util.StrUtil;
import co.yixiang.common.utils.QueryHelpPlus;
import co.yixiang.modules.manage.entity.Charities;
import co.yixiang.modules.manage.mapper.CharitiesMapper;
import co.yixiang.modules.manage.service.CharitiesService;
import co.yixiang.modules.manage.web.param.CharitiesQueryParam;
import co.yixiang.modules.manage.web.vo.CharitiesQueryVo;
import co.yixiang.common.service.impl.BaseServiceImpl;
import co.yixiang.common.web.vo.Paging;
import co.yixiang.modules.shop.entity.YxSystemStore;
import co.yixiang.modules.shop.service.YxSystemStoreService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * <p>
 * 慈善活动表 服务实现类
 * </p>
 *
 * @author visa
 * @since 2020-08-20
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class CharitiesServiceImpl extends BaseServiceImpl<CharitiesMapper, Charities> implements CharitiesService {

    @Autowired
    private CharitiesMapper charitiesMapper;

    @Autowired
    private YxSystemStoreService yxSystemStoreService;

    @Override
    public CharitiesQueryVo getCharitiesById(Serializable id) throws Exception{
        return charitiesMapper.getCharitiesById(id);
    }

    @Override
    public Paging<Charities> getCharitiesPageList(CharitiesQueryParam charitiesQueryParam) throws Exception{
        Page page = setPageParam(charitiesQueryParam,OrderItem.desc("create_time"));
        QueryWrapper queryWrapper = QueryHelpPlus.getPredicate(CharitiesQueryParam.class, charitiesQueryParam);

        if(StrUtil.isNotBlank(charitiesQueryParam.getKeyword())){
            queryWrapper.apply("  ( drugstore_name like concat('%',{0},'%') or product_name like concat('%',{1},'%') or common_name like concat('%',{2},'%') )",charitiesQueryParam.getKeyword(),charitiesQueryParam.getKeyword(),charitiesQueryParam.getKeyword());
        }

        IPage<Charities> iPage = charitiesMapper.selectPage(page,queryWrapper);

        for( Charities charities: iPage.getRecords()) {
            List<String> drugstoreNames = Arrays.asList(charities.getDrugstoreName().split("；"));

            QueryWrapper queryWrapper1 = new QueryWrapper();
            queryWrapper1.in("name",drugstoreNames);
            List<YxSystemStore> drugstoreList =  yxSystemStoreService.list(queryWrapper1);
            charities.setDrugstoreList(drugstoreList);
        }
        return new Paging(iPage);
    }

}
