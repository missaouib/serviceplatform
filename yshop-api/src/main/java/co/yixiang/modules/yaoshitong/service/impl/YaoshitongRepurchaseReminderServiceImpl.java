package co.yixiang.modules.yaoshitong.service.impl;

import cn.hutool.core.util.StrUtil;
import co.yixiang.common.utils.QueryHelpPlus;
import co.yixiang.modules.yaoshitong.entity.YaoshitongRepurchaseReminder;
import co.yixiang.modules.yaoshitong.mapper.YaoshitongRepurchaseReminderMapper;
import co.yixiang.modules.yaoshitong.service.YaoshitongRepurchaseReminderService;
import co.yixiang.modules.yaoshitong.web.param.YaoshitongRepurchaseReminderQueryParam;
import co.yixiang.modules.yaoshitong.web.vo.YaoshitongRepurchaseReminderQueryVo;
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
 * 药品复购提醒 服务实现类
 * </p>
 *
 * @author visa
 * @since 2020-10-21
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class YaoshitongRepurchaseReminderServiceImpl extends BaseServiceImpl<YaoshitongRepurchaseReminderMapper, YaoshitongRepurchaseReminder> implements YaoshitongRepurchaseReminderService {

    @Autowired
    private YaoshitongRepurchaseReminderMapper yaoshitongRepurchaseReminderMapper;

    @Override
    public YaoshitongRepurchaseReminderQueryVo getYaoshitongRepurchaseReminderById(Serializable id) throws Exception{
        return yaoshitongRepurchaseReminderMapper.getYaoshitongRepurchaseReminderById(id);
    }

    @Override
    public Paging<YaoshitongRepurchaseReminderQueryVo> getYaoshitongRepurchaseReminderPageList(YaoshitongRepurchaseReminderQueryParam yaoshitongRepurchaseReminderQueryParam) throws Exception{
        Page page = setPageParam(yaoshitongRepurchaseReminderQueryParam,OrderItem.desc("unit_price"));

        if("全部".equals(yaoshitongRepurchaseReminderQueryParam.getStatus())) {
            yaoshitongRepurchaseReminderQueryParam.setStatus("");
        }

        QueryWrapper queryWrapper = QueryHelpPlus.getPredicate(YaoshitongRepurchaseReminderQueryParam.class, yaoshitongRepurchaseReminderQueryParam);
        if(StrUtil.isNotBlank(yaoshitongRepurchaseReminderQueryParam.getKeyword())) {
            queryWrapper.like("med_name",yaoshitongRepurchaseReminderQueryParam.getKeyword());
            queryWrapper.or();
            queryWrapper.like("med_common_name",yaoshitongRepurchaseReminderQueryParam.getKeyword());
        }

        IPage<YaoshitongRepurchaseReminder> iPage = yaoshitongRepurchaseReminderMapper.selectPage(page,queryWrapper);
        return new Paging(iPage);
    }

}
