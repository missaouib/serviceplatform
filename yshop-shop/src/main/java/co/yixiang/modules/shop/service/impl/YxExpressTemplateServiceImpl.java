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
import cn.hutool.core.util.ObjectUtil;
import co.yixiang.modules.shop.domain.YxExpressTemplate;
import co.yixiang.common.service.impl.BaseServiceImpl;
import co.yixiang.modules.shop.domain.YxExpressTemplateDetail;
import co.yixiang.modules.shop.service.YxExpressTemplateDetailService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import lombok.AllArgsConstructor;
import co.yixiang.dozer.service.IGenerator;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import co.yixiang.common.utils.QueryHelpPlus;
import co.yixiang.utils.ValidationUtil;
import co.yixiang.utils.FileUtil;
import co.yixiang.modules.shop.service.YxExpressTemplateService;
import co.yixiang.modules.shop.service.dto.YxExpressTemplateDto;
import co.yixiang.modules.shop.service.dto.YxExpressTemplateQueryCriteria;
import co.yixiang.modules.shop.service.mapper.YxExpressTemplateMapper;
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
import javax.swing.text.StyledEditorKit;
import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
* @author visa
* @date 2020-11-28
*/
@Service
@AllArgsConstructor
//@CacheConfig(cacheNames = "yxExpressTemplate")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class YxExpressTemplateServiceImpl extends BaseServiceImpl<YxExpressTemplateMapper, YxExpressTemplate> implements YxExpressTemplateService {

    private final IGenerator generator;

    @Autowired
    private YxExpressTemplateDetailService yxExpressTemplateDetailService;
    @Override
    //@Cacheable
    public Map<String, Object> queryAll(YxExpressTemplateQueryCriteria criteria, Pageable pageable) {
        getPage(pageable);
        PageInfo<YxExpressTemplate> page = new PageInfo<>(queryAll(criteria));
        Map<String, Object> map = new LinkedHashMap<>(2);
        List<YxExpressTemplateDto> dtoList = generator.convert(page.getList(), YxExpressTemplateDto.class);
        for(YxExpressTemplateDto dto:dtoList) {
              QueryWrapper queryWrapper = new QueryWrapper();
              queryWrapper.eq("template_id",dto.getId());
              queryWrapper.eq("level","1");
              dto.setDetails(yxExpressTemplateDetailService.list(queryWrapper));
        }
        map.put("content", dtoList);
        map.put("totalElements", page.getTotal());
        return map;
    }


    @Override
    //@Cacheable
    public List<YxExpressTemplate> queryAll(YxExpressTemplateQueryCriteria criteria){
        return baseMapper.selectList(QueryHelpPlus.getPredicate(YxExpressTemplate.class, criteria));
    }


    @Override
    public void download(List<YxExpressTemplateDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (YxExpressTemplateDto yxExpressTemplate : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("模板名称", yxExpressTemplate.getTemplateName());
            map.put("物流商名称", yxExpressTemplate.getExpressName());
            map.put("是否默认", yxExpressTemplate.getIsDefault());
            map.put("项目代码", yxExpressTemplate.getProjectCode());
            map.put("记录生成时间", yxExpressTemplate.getCreateTime());
            map.put("记录更新时间", yxExpressTemplate.getUpdateTime());
            map.put("记录创建人", yxExpressTemplate.getCreater());
            map.put("记录更新人", yxExpressTemplate.getMaker());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }


    @Override
    public Boolean saveExpressTemplate(YxExpressTemplate yxExpressTemplate) {
        if(ObjectUtil.isNotEmpty(yxExpressTemplate.getId())) {
            QueryWrapper wrapper = new QueryWrapper();
            wrapper.eq("template_id",yxExpressTemplate.getId());
            yxExpressTemplateDetailService.remove(wrapper);
        }

        saveOrUpdate(yxExpressTemplate);

        if(CollUtil.isNotEmpty(yxExpressTemplate.getDetails())) {
            for(YxExpressTemplateDetail detail:yxExpressTemplate.getDetails()) {
                detail.setId(null);
                detail.setTemplateId(yxExpressTemplate.getId());
            }
            yxExpressTemplateDetailService.saveBatch(yxExpressTemplate.getDetails());
        }

        // 是否默认配置
        if(yxExpressTemplate.getIsDefault() == 1) {
            LambdaUpdateWrapper<YxExpressTemplate> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
            lambdaUpdateWrapper.set(YxExpressTemplate::getIsDefault,0);
            lambdaUpdateWrapper.ne(YxExpressTemplate::getId,yxExpressTemplate.getId());
            this.update(lambdaUpdateWrapper);
        }

        return true;
    }
}
