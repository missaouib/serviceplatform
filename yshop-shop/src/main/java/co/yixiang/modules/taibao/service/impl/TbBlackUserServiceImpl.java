/**
* Copyright (C) 2018-2020
* All rights reserved, Designed By www.yixiang.co
* 注意：
* 本软件为www.yixiang.co开发研制，未经购买不得使用
* 购买后可获得全部源代码（禁止转卖、分享、上传到码云、github等开源平台）
* 一经发现盗用、分享等行为，将追究法律责任，后果自负
*/
package co.yixiang.modules.taibao.service.impl;

import co.yixiang.modules.taibao.domain.TbBlackUser;
import co.yixiang.common.service.impl.BaseServiceImpl;
import lombok.AllArgsConstructor;
import co.yixiang.dozer.service.IGenerator;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import co.yixiang.common.utils.QueryHelpPlus;
import co.yixiang.utils.ValidationUtil;
import co.yixiang.utils.FileUtil;
import co.yixiang.modules.taibao.service.TbBlackUserService;
import co.yixiang.modules.taibao.service.dto.TbBlackUserDto;
import co.yixiang.modules.taibao.service.dto.TbBlackUserQueryCriteria;
import co.yixiang.modules.taibao.service.mapper.TbBlackUserMapper;
import org.springframework.beans.factory.annotation.Autowired;
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
* @author zhoujinlai
* @date 2021-05-27
*/
@Service
//@AllArgsConstructor
//@CacheConfig(cacheNames = "tbBlackUser")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class TbBlackUserServiceImpl extends BaseServiceImpl<TbBlackUserMapper, TbBlackUser> implements TbBlackUserService {
    @Autowired
    private IGenerator generator;
    @Autowired
    private TbBlackUserMapper tbBlackUserMapper;



    @Override
    //@Cacheable
    public Map<String, Object> queryAll(TbBlackUserQueryCriteria criteria, Pageable pageable) {
        getPage(pageable);
        PageInfo<TbBlackUser> page = new PageInfo<>(queryAll(criteria));
        Map<String, Object> map = new LinkedHashMap<>(2);
        map.put("content", generator.convert(page.getList(), TbBlackUserDto.class));
        map.put("totalElements", page.getTotal());
        return map;
    }


    @Override
    //@Cacheable
    public List<TbBlackUser> queryAll(TbBlackUserQueryCriteria criteria){
        return baseMapper.selectList(QueryHelpPlus.getPredicate(TbBlackUser.class, criteria));
    }


    @Override
    public void download(List<TbBlackUserDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (TbBlackUserDto tbBlackUser : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("姓名", tbBlackUser.getName());
            map.put("性别", tbBlackUser.getSex());
            map.put("证件类型", tbBlackUser.getIdType());
            map.put("证件号", tbBlackUser.getIdNo());
            map.put("国家", tbBlackUser.getCountry());
            map.put("创建人", tbBlackUser.getCreateBy());
            map.put("创建时间", tbBlackUser.getCreateTime());
            map.put("修改人", tbBlackUser.getUpdateBy());
            map.put("修改时间", tbBlackUser.getUpdateTime());
            map.put("0表示未删除,1表示删除", tbBlackUser.getDelFlag());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

    @Override
    public int findByNameOrIdCard(String name, String contactsName, String idNo) {
        return tbBlackUserMapper.findByNameOrIdCard(name,contactsName,idNo);
    }
}
