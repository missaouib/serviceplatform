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
import cn.hutool.core.util.StrUtil;
import co.yixiang.exception.BadRequestException;
import co.yixiang.modules.shop.domain.Staff;
import co.yixiang.common.service.impl.BaseServiceImpl;
import co.yixiang.modules.xikang.domain.XikangMedMapping;
import co.yixiang.mp.domain.DictDetail;
import co.yixiang.mp.rest.param.DictDetailQueryParam;
import co.yixiang.mp.service.DictDetailService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.AllArgsConstructor;
import co.yixiang.dozer.service.IGenerator;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import co.yixiang.common.utils.QueryHelpPlus;
import co.yixiang.utils.ValidationUtil;
import co.yixiang.utils.FileUtil;
import co.yixiang.modules.shop.service.StaffService;
import co.yixiang.modules.shop.service.dto.StaffDto;
import co.yixiang.modules.shop.service.dto.StaffQueryCriteria;
import co.yixiang.modules.shop.service.mapper.StaffMapper;
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
* @author visa
* @date 2021-03-04
*/
@Service

//@CacheConfig(cacheNames = "staff")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class StaffServiceImpl extends BaseServiceImpl<StaffMapper, Staff> implements StaffService {
    @Autowired
    private  IGenerator generator;

    @Autowired
    private DictDetailService detailService;

    @Override
    //@Cacheable
    public Map<String, Object> queryAll(StaffQueryCriteria criteria, Pageable pageable) {
        getPage(pageable);
        PageInfo<Staff> page = new PageInfo<>(queryAll(criteria));
        Map<String, Object> map = new LinkedHashMap<>(2);
        map.put("content", generator.convert(page.getList(), StaffDto.class));
        map.put("totalElements", page.getTotal());
        return map;
    }


    @Override
    //@Cacheable
    public List<Staff> queryAll(StaffQueryCriteria criteria){
        QueryWrapper queryWrapper = QueryHelpPlus.getPredicate(Staff.class, criteria);
        if(StrUtil.isNotBlank(criteria.getStaffType())) {
            queryWrapper.like("type",criteria.getStaffType());
        }
        return baseMapper.selectList(queryWrapper);
    }


    @Override
    public void download(List<StaffDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (StaffDto staff : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("员工代码", staff.getCode());
            map.put("姓名", staff.getName());

            String type = "";
            List<String> valueList = new ArrayList<>();
            valueList.add(staff.getType());



            List<DictDetail> dictDetailList = detailService.findDetails(valueList,"staff_type");
            if(CollUtil.isNotEmpty(dictDetailList)) {
                type = dictDetailList.get(0).getLabel();
            }

            map.put("员工分类", type);
            map.put("机构", staff.getOrganization());
            map.put("科室", staff.getDepart());
            map.put("项目代码", staff.getProjectCode());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }


    @Override
    public int upload(List<Map<String, Object>> list) {
        List<Staff> staffArrayList = new ArrayList<>();

        for(Map<String,Object> data : list) {
            String name = "";
            String type = "";
            String organization = "";
            String depart = "";
            String projectCode="";
            String code = "";

            Object code_Object = data.get("员工代码");
            if(ObjectUtil.isNotEmpty(code_Object)) {
                code = String.valueOf(code_Object);
            } else {
                throw  new BadRequestException("员工代码不能为空");
            }


            Object name_Object = data.get("姓名");
            if(ObjectUtil.isNotEmpty(name_Object)) {
                name = String.valueOf(name_Object);
            } else {
                throw  new BadRequestException("姓名不能为空");
            }

            Object organization_Object = data.get("机构");
            if(ObjectUtil.isNotEmpty(organization_Object)) {
                organization = String.valueOf(organization_Object);
            }

            Object depart_Object = data.get("科室");
            if(ObjectUtil.isNotEmpty(depart_Object)) {
                depart = String.valueOf(depart_Object);
            }

            Object type_Object = data.get("员工分类");
            if(ObjectUtil.isNotEmpty(type_Object)) {
                String type_str = String.valueOf(type_Object);
                DictDetailQueryParam dictDetailQueryParam = new DictDetailQueryParam();
                dictDetailQueryParam.setName("staff_type");
                dictDetailQueryParam.setLabel(type_str);
                List<DictDetail> dictDetailList = detailService.queryAll(dictDetailQueryParam);
                if(CollUtil.isNotEmpty(dictDetailList)) {
                    type = dictDetailList.get(0).getValue();
                }

            }

            Object project_Object = data.get("项目代码");
            if(ObjectUtil.isNotEmpty(project_Object)) {
                projectCode = String.valueOf(project_Object);
            }else {
                throw  new BadRequestException("项目代码不能为空");
            }

            LambdaQueryWrapper<Staff> queryWrapper = new LambdaQueryWrapper<Staff>();

            queryWrapper.eq(Staff::getName,name);

            Staff staff = this.getOne(queryWrapper);

            if(staff == null) {
                staff = new Staff();
            }

            staff.setName(name);
            staff.setOrganization(organization);
            staff.setProjectCode(projectCode);
            staff.setType(type);
            staff.setDepart(depart);
            staff.setCode(code);
            staffArrayList.add(staff);
        }
        this.saveOrUpdateBatch(staffArrayList) ;
        return staffArrayList.size();


    }
}
