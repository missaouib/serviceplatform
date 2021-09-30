/**
* Copyright (C) 2018-2020
* All rights reserved, Designed By www.yixiang.co
* 注意：
* 本软件为www.yixiang.co开发研制，未经购买不得使用
* 购买后可获得全部源代码（禁止转卖、分享、上传到码云、github等开源平台）
* 一经发现盗用、分享等行为，将追究法律责任，后果自负
*/
package co.yixiang.modules.shop.service.impl;

import co.yixiang.modules.shop.domain.YxDrugUsers;
import co.yixiang.common.service.impl.BaseServiceImpl;
import lombok.AllArgsConstructor;
import co.yixiang.dozer.service.IGenerator;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import co.yixiang.common.utils.QueryHelpPlus;
import co.yixiang.utils.ValidationUtil;
import co.yixiang.utils.FileUtil;
import co.yixiang.modules.shop.service.YxDrugUsersService;
import co.yixiang.modules.shop.service.dto.YxDrugUsersDto;
import co.yixiang.modules.shop.service.dto.YxDrugUsersQueryCriteria;
import co.yixiang.modules.shop.service.mapper.YxDrugUsersMapper;
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
* @date 2021-02-08
*/
@Service
@AllArgsConstructor
//@CacheConfig(cacheNames = "yxDrugUsers")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class YxDrugUsersServiceImpl extends BaseServiceImpl<YxDrugUsersMapper, YxDrugUsers> implements YxDrugUsersService {

    private final IGenerator generator;

    @Override
    //@Cacheable
    public Map<String, Object> queryAll(YxDrugUsersQueryCriteria criteria, Pageable pageable) {
        getPage(pageable);
        PageInfo<YxDrugUsers> page = new PageInfo<>(queryAll(criteria));
        Map<String, Object> map = new LinkedHashMap<>(2);
        map.put("content", generator.convert(page.getList(), YxDrugUsersDto.class));
        map.put("totalElements", page.getTotal());
        return map;
    }


    @Override
    //@Cacheable
    public List<YxDrugUsers> queryAll(YxDrugUsersQueryCriteria criteria){
        return baseMapper.selectList(QueryHelpPlus.getPredicate(YxDrugUsers.class, criteria));
    }


    @Override
    public void download(List<YxDrugUsersDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (YxDrugUsersDto yxDrugUsers : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("用户id", yxDrugUsers.getUid());
            map.put("姓名", yxDrugUsers.getName());
            map.put("关系 1/本人 2/亲属 3/朋友 4/其他", yxDrugUsers.getRelation());
            map.put("手机号", yxDrugUsers.getPhone());
            map.put("性别", yxDrugUsers.getSex());
            map.put("身份证号", yxDrugUsers.getIdcard());
            map.put("生成时间", yxDrugUsers.getCreateTime());
            map.put("最后更新时间", yxDrugUsers.getUpdateTime());
            map.put("是否默认", yxDrugUsers.getIsDefault());
            map.put("是否删除", yxDrugUsers.getIsDel());
            map.put("年龄", yxDrugUsers.getAge());
            map.put("疾病史", yxDrugUsers.getDiseaseHistory());
            map.put("出生年月", yxDrugUsers.getBirth());
            map.put("用药人类型 1/成人 2/儿童", yxDrugUsers.getUserType());
            map.put("体重", yxDrugUsers.getWeight());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }
}
