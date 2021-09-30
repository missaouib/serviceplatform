/**
* Copyright (C) 2018-2020
* All rights reserved, Designed By www.yixiang.co
* 注意：
* 本软件为www.yixiang.co开发研制，未经购买不得使用
* 购买后可获得全部源代码（禁止转卖、分享、上传到码云、github等开源平台）
* 一经发现盗用、分享等行为，将追究法律责任，后果自负
*/
package co.yixiang.modules.shop.service.impl;

import cn.hutool.core.collection.CollUtil;
import co.yixiang.modules.shop.domain.ProjectSalesArea;
import co.yixiang.common.service.impl.BaseServiceImpl;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.AllArgsConstructor;
import co.yixiang.dozer.service.IGenerator;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import co.yixiang.common.utils.QueryHelpPlus;
import co.yixiang.utils.ValidationUtil;
import co.yixiang.utils.FileUtil;
import co.yixiang.modules.shop.service.ProjectSalesAreaService;
import co.yixiang.modules.shop.service.dto.ProjectSalesAreaDto;
import co.yixiang.modules.shop.service.dto.ProjectSalesAreaQueryCriteria;
import co.yixiang.modules.shop.service.mapper.ProjectSalesAreaMapper;
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
* @date 2021-04-09
*/
@Service
@AllArgsConstructor
//@CacheConfig(cacheNames = "projectSalesArea")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class ProjectSalesAreaServiceImpl extends BaseServiceImpl<ProjectSalesAreaMapper, ProjectSalesArea> implements ProjectSalesAreaService {

    private final IGenerator generator;

    @Override
    //@Cacheable
    public Map<String, Object> queryAll(ProjectSalesAreaQueryCriteria criteria, Pageable pageable) {
        getPage(pageable);
        PageInfo<ProjectSalesArea> page = new PageInfo<>(queryAll(criteria));
        Map<String, Object> map = new LinkedHashMap<>(2);
        map.put("content", generator.convert(page.getList(), ProjectSalesAreaDto.class));
        map.put("totalElements", page.getTotal());
        return map;
    }


    @Override
    //@Cacheable
    public List<ProjectSalesArea> queryAll(ProjectSalesAreaQueryCriteria criteria){
        return baseMapper.selectList(QueryHelpPlus.getPredicate(ProjectSalesArea.class, criteria));
    }


    @Override
    public void download(List<ProjectSalesAreaDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (ProjectSalesAreaDto projectSalesArea : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("项目代码", projectSalesArea.getProjectCode());
            map.put("省份名称", projectSalesArea.getAreaName());
            map.put("免邮金额", projectSalesArea.getFreePostage());
            map.put("记录生成时间", projectSalesArea.getCreateTime());
            map.put("记录更新时间", projectSalesArea.getUpdateTime());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

    @Override
    public Boolean saveList(List<ProjectSalesArea> resouce,String projectCode) {
        LambdaQueryWrapper<ProjectSalesArea> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(ProjectSalesArea::getProjectCode,projectCode);
        this.remove(queryWrapper);
        if(CollUtil.isNotEmpty(resouce)) {
            for(ProjectSalesArea projectSalesArea:resouce) {
                projectSalesArea.setId(null);
                this.save(projectSalesArea);
            }

        }

        return true;
    }
}
