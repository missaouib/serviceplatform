/**
* Copyright (C) 2018-2020
* All rights reserved, Designed By www.yixiang.co
* 注意：
* 本软件为www.yixiang.co开发研制，未经购买不得使用
* 购买后可获得全部源代码（禁止转卖、分享、上传到码云、github等开源平台）
* 一经发现盗用、分享等行为，将追究法律责任，后果自负
*/
package co.yixiang.modules.shop.service.impl;

import co.yixiang.modules.shop.domain.YxUserAppointment;
import co.yixiang.common.service.impl.BaseServiceImpl;
import lombok.AllArgsConstructor;
import co.yixiang.dozer.service.IGenerator;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import co.yixiang.common.utils.QueryHelpPlus;
import co.yixiang.utils.ValidationUtil;
import co.yixiang.utils.FileUtil;
import co.yixiang.modules.shop.service.YxUserAppointmentService;
import co.yixiang.modules.shop.service.dto.YxUserAppointmentDto;
import co.yixiang.modules.shop.service.dto.YxUserAppointmentQueryCriteria;
import co.yixiang.modules.shop.service.mapper.YxUserAppointmentMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
// 默认不使用缓存
//import org.springframework.cache.annotation.CacheConfig;
//import org.springframework.cache.annotation.CacheEvict;
//import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Map;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
* @author visa
* @date 2020-06-05
*/
@Service
@AllArgsConstructor
//@CacheConfig(cacheNames = "yxUserAppointment")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class YxUserAppointmentServiceImpl extends BaseServiceImpl<YxUserAppointmentMapper, YxUserAppointment> implements YxUserAppointmentService {

    private final IGenerator generator;

    @Override
    //@Cacheable
    public Map<String, Object> queryAll(YxUserAppointmentQueryCriteria criteria, Pageable pageable) {
        getPage(pageable);
        PageInfo<YxUserAppointment> page = new PageInfo<>(queryAll(criteria));
        Map<String, Object> map = new LinkedHashMap<>(2);
        map.put("content", generator.convert(page.getList(), YxUserAppointmentDto.class));
        map.put("totalElements", page.getTotal());
        return map;
    }


    @Override
    //@Cacheable
    public List<YxUserAppointment> queryAll(YxUserAppointmentQueryCriteria criteria){
        return baseMapper.selectList(QueryHelpPlus.getPredicate(YxUserAppointment.class, criteria));
    }


    @Override
    public void download(List<YxUserAppointmentDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (YxUserAppointmentDto yxUserAppointment : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("活动id", yxUserAppointment.getEventId());
            map.put("用户id", yxUserAppointment.getUid());
            map.put("手机号", yxUserAppointment.getMobile());
            map.put("活动名称", yxUserAppointment.getEventName());
            map.put(" addTime",  yxUserAppointment.getAddTime());
            map.put("状态，0/已预约 1/已取消", yxUserAppointment.getStatus());
            map.put("用户名称", yxUserAppointment.getName());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }
}
