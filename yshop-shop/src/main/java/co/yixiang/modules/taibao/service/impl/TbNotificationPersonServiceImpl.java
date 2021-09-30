/**
* Copyright (C) 2018-2020
* All rights reserved, Designed By www.yixiang.co
* 注意：
* 本软件为www.yixiang.co开发研制，未经购买不得使用
* 购买后可获得全部源代码（禁止转卖、分享、上传到码云、github等开源平台）
* 一经发现盗用、分享等行为，将追究法律责任，后果自负
*/
package co.yixiang.modules.taibao.service.impl;

import co.yixiang.common.service.impl.BaseServiceImpl;
import co.yixiang.common.utils.QueryHelpPlus;
import co.yixiang.dozer.service.IGenerator;
import co.yixiang.modules.taibao.domain.TbNotificationPerson;
import co.yixiang.modules.taibao.service.TbNotificationPersonService;
import co.yixiang.modules.taibao.service.dto.TbNotificationPersonDto;
import co.yixiang.modules.taibao.service.dto.TbNotificationPersonQueryCriteria;
import co.yixiang.modules.taibao.service.mapper.TbNotificationPersonMapper;
import co.yixiang.utils.FileUtil;
import com.github.pagehelper.PageInfo;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

// 默认不使用缓存
//import org.springframework.cache.annotation.CacheConfig;
//import org.springframework.cache.annotation.CacheEvict;
//import org.springframework.cache.annotation.Cacheable;

/**
* @author zhoujinlai
* @date 2021-04-30
*/
@Service
@AllArgsConstructor
//@CacheConfig(cacheNames = "tbNotificationPerson")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class TbNotificationPersonServiceImpl extends BaseServiceImpl<TbNotificationPersonMapper, TbNotificationPerson> implements TbNotificationPersonService {

    private final IGenerator generator;
    @Autowired
    private TbNotificationPersonMapper notificationPersonMapper;
    @Override
    //@Cacheable
    public Map<String, Object> queryAll(TbNotificationPersonQueryCriteria criteria, Pageable pageable) {
        getPage(pageable);
        PageInfo<TbNotificationPerson> page = new PageInfo<>(queryAll(criteria));
        Map<String, Object> map = new LinkedHashMap<>(2);
        map.put("content", generator.convert(page.getList(), TbNotificationPersonDto.class));
        map.put("totalElements", page.getTotal());
        return map;
    }


    @Override
    //@Cacheable
    public List<TbNotificationPerson> queryAll(TbNotificationPersonQueryCriteria criteria){
        return baseMapper.selectList(QueryHelpPlus.getPredicate(TbNotificationPerson.class, criteria));
    }


    @Override
    public void download(List<TbNotificationPersonDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (TbNotificationPersonDto tbNotificationPerson : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("与被保人关系", tbNotificationPerson.getRelationship());
            map.put("报案日期", tbNotificationPerson.getNoticeDate());
            map.put("证件类别", tbNotificationPerson.getIdtype());
            map.put("证件号码", tbNotificationPerson.getIdno());
            map.put("证件有效起期", tbNotificationPerson.getIdBegdate());
            map.put("证件有效止期", tbNotificationPerson.getIdEnddate());
            map.put("姓名", tbNotificationPerson.getName());
            map.put("性别", tbNotificationPerson.getSex());
            map.put("移动电话", tbNotificationPerson.getMobilephone());
            map.put("固定电话", tbNotificationPerson.getTelephone());
            map.put("邮箱地址", tbNotificationPerson.getEmail());
            map.put("联系地址", tbNotificationPerson.getAddr());
            map.put("邮政编码", tbNotificationPerson.getZip());
            map.put("创建人", tbNotificationPerson.getCreateBy());
            map.put("创建时间", tbNotificationPerson.getCreateTime());
            map.put("修改人", tbNotificationPerson.getUpdateBy());
            map.put("修改时间", tbNotificationPerson.getUpdateTime());
            map.put("0表示未删除,1表示删除", tbNotificationPerson.getDelFlag());
            map.put("赔案信息Id", tbNotificationPerson.getClaimInfoId());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

    @Override
    public List<TbNotificationPerson> selectByMainId(String mainId) {
        return notificationPersonMapper.selectByMainId(Long.valueOf(mainId));
    }
}
