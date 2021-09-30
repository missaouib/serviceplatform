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
import cn.hutool.core.util.StrUtil;
import co.yixiang.modules.shop.domain.YxStoreCategory;
import co.yixiang.modules.shop.domain.YxStoreDisease;
import co.yixiang.common.service.impl.BaseServiceImpl;
import co.yixiang.modules.shop.service.dto.YxStoreCategoryDto;
import co.yixiang.modules.shop.service.dto.YxStoreDiseaseExportDto;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.AllArgsConstructor;
import co.yixiang.dozer.service.IGenerator;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import co.yixiang.common.utils.QueryHelpPlus;
import co.yixiang.utils.ValidationUtil;
import co.yixiang.utils.FileUtil;
import co.yixiang.modules.shop.service.YxStoreDiseaseService;
import co.yixiang.modules.shop.service.dto.YxStoreDiseaseDto;
import co.yixiang.modules.shop.service.dto.YxStoreDiseaseQueryCriteria;
import co.yixiang.modules.shop.service.mapper.YxStoreDiseaseMapper;
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
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.io.IOException;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletResponse;

/**
* @author visa
* @date 2020-06-02
*/
@Service
@AllArgsConstructor
//@CacheConfig(cacheNames = "yxStoreDisease")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class YxStoreDiseaseServiceImpl extends BaseServiceImpl<YxStoreDiseaseMapper, YxStoreDisease> implements YxStoreDiseaseService {

    private final IGenerator generator;

    @Override
    //@Cacheable
    public Map<String, Object> queryAll(YxStoreDiseaseQueryCriteria criteria, Pageable pageable) {
        getPage(pageable);
        PageInfo<YxStoreDiseaseDto> page = new PageInfo<>(queryAll(criteria));
        Map<String, Object> map = new LinkedHashMap<>(2);
        map.put("content", page.getList());
        map.put("totalElements", page.getTotal());
        return map;
    }


    @Override
    //@Cacheable
    public List<YxStoreDiseaseDto> queryAll(YxStoreDiseaseQueryCriteria criteria){
        QueryWrapper queryWrapper = QueryHelpPlus.getPredicate(YxStoreDisease.class, criteria);
        if( "".equals(criteria.getProjectCode())) {
            queryWrapper.eq("project_code",criteria.getProjectCode());
        }
        return generator.convert(baseMapper.selectList(queryWrapper),YxStoreDiseaseDto.class);


    }

    @Override
    public List<YxStoreDiseaseExportDto> queryAllSimple(YxStoreDiseaseQueryCriteria criteria) {
        return baseMapper.downloadSimple(criteria);
    }

    @Override
    public void download(List<YxStoreDiseaseDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (YxStoreDiseaseDto yxStoreDisease : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("父id", yxStoreDisease.getPid());
            map.put("病种名称", yxStoreDisease.getCateName());
            map.put("排序", yxStoreDisease.getSort());
            map.put("图标", yxStoreDisease.getPic());
            map.put("是否推荐", yxStoreDisease.getIsShow());
            map.put("添加时间", yxStoreDisease.getAddTime());
            map.put("删除状态", yxStoreDisease.getIsDel());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }


    @Override
    public void downloadSimple(List<YxStoreDiseaseExportDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (YxStoreDiseaseExportDto yxStoreDisease : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("一级类目", yxStoreDisease.getFirstCateName());
            map.put("二级类目", yxStoreDisease.getSecondCateName());

            if(StrUtil.isNotBlank(yxStoreDisease.getCateType())){
               List<String> cateList = new ArrayList<>();
               List<String> stringList = Arrays.asList(yxStoreDisease.getCateType().split(","));
               for(String cate:stringList) {
                   if("1".equals(cate)) {
                       cateList.add("我要找药");
                   }else if("2".equals(cate)) {
                       cateList.add("健康馆");
                   }
               }
                map.put("类型", CollUtil.join(cateList,","));
            } else {
                map.put("类型", yxStoreDisease.getCateType());
            }

            map.put("状态",yxStoreDisease.getShowType());

            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

    @Override
    public Integer selectByYxStoreDisease(YxStoreDisease resources) {
        return baseMapper.selectByYxStoreDisease(resources);
    }

    @Override
    public Object buildTree(List<YxStoreDiseaseDto> diseaseDTOS) {
        Set<YxStoreDiseaseDto> trees = new LinkedHashSet<>();
        Set<YxStoreDiseaseDto> cates= new LinkedHashSet<>();
        List<String> deptNames = diseaseDTOS.stream().map(YxStoreDiseaseDto::getCateName)
                .collect(Collectors.toList());

        YxStoreDiseaseDto diseaseDto = new YxStoreDiseaseDto();
        Boolean isChild;
        List<YxStoreDisease> categories = this.list();
        for (YxStoreDiseaseDto deptDTO : diseaseDTOS) {
            isChild = false;
            if ("0".equals(deptDTO.getPid().toString())) {
                trees.add(deptDTO);
            }
            for (YxStoreDiseaseDto it : diseaseDTOS) {
                if (it.getPid().equals(deptDTO.getId())) {
                    isChild = true;
                    if (deptDTO.getChildren() == null) {
                        deptDTO.setChildren(new ArrayList<YxStoreDiseaseDto>());
                    }
                    deptDTO.getChildren().add(it);
                }
            }
            if(isChild)
                cates.add(deptDTO);
            for (YxStoreDisease category : categories) {
                if(category.getId().equals(deptDTO.getPid())&&!deptNames.contains(category.getCateName())){
                    cates.add(deptDTO);
                }
            }
        }



        if (CollectionUtils.isEmpty(trees)) {
            trees = cates;
        }



        Integer totalElements = diseaseDTOS!=null?diseaseDTOS.size():0;

        Map map = new HashMap();
        map.put("totalElements",totalElements);
        map.put("content",CollectionUtils.isEmpty(trees)?diseaseDTOS:trees);
        return map;
    }

    @Override
    public String findParentids(List<Integer> ids) {
        return baseMapper.findParentIds(ids);
    }
}
