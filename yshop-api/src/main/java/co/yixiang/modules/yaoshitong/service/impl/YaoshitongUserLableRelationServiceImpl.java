package co.yixiang.modules.yaoshitong.service.impl;

import co.yixiang.common.utils.QueryHelpPlus;
import co.yixiang.modules.yaoshitong.entity.YaoshitongUserLable;
import co.yixiang.modules.yaoshitong.entity.YaoshitongUserLableRelation;
import co.yixiang.modules.yaoshitong.mapper.YaoshitongUserLableRelationMapper;
import co.yixiang.modules.yaoshitong.service.YaoshitongUserLableRelationService;
import co.yixiang.modules.yaoshitong.web.param.YaoshitongUserLableRelationQueryParam;
import co.yixiang.modules.yaoshitong.web.vo.YaoshitongUserLableRelationQueryVo;
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
import java.util.List;


/**
 * <p>
 * 患者对应的标签库 服务实现类
 * </p>
 *
 * @author visa
 * @since 2020-08-26
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class YaoshitongUserLableRelationServiceImpl extends BaseServiceImpl<YaoshitongUserLableRelationMapper, YaoshitongUserLableRelation> implements YaoshitongUserLableRelationService {

    @Autowired
    private YaoshitongUserLableRelationMapper yaoshitongUserLableRelationMapper;

    @Override
    public YaoshitongUserLableRelationQueryVo getYaoshitongUserLableRelationById(Serializable id) throws Exception{
        return yaoshitongUserLableRelationMapper.getYaoshitongUserLableRelationById(id);
    }

    @Override
    public Paging<YaoshitongUserLableRelationQueryVo> getYaoshitongUserLableRelationPageList(YaoshitongUserLableRelationQueryParam yaoshitongUserLableRelationQueryParam) throws Exception{
        Page page = setPageParam(yaoshitongUserLableRelationQueryParam,OrderItem.desc("create_time"));
        QueryWrapper queryWrapper = QueryHelpPlus.getPredicate(YaoshitongUserLableRelationQueryParam.class, yaoshitongUserLableRelationQueryParam);
        IPage<YaoshitongUserLableRelation> iPage = yaoshitongUserLableRelationMapper.selectPage(page,queryWrapper);
        return new Paging(iPage);
    }

    @Override
    public List<YaoshitongUserLable> getUserLableRelationByUid(String pharmacistId, Integer patientId) {
        return yaoshitongUserLableRelationMapper.getUserLableRelationByUid(pharmacistId,patientId);
    }
}
