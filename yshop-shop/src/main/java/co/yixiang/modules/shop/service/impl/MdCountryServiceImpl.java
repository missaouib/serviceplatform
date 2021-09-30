/**
* Copyright (C) 2018-2020
* All rights reserved, Designed By www.yixiang.co
* 注意：
* 本软件为www.yixiang.co开发研制，未经购买不得使用
* 购买后可获得全部源代码（禁止转卖、分享、上传到码云、github等开源平台）
* 一经发现盗用、分享等行为，将追究法律责任，后果自负
*/
package co.yixiang.modules.shop.service.impl;

import cn.hutool.core.util.StrUtil;
import co.yixiang.modules.api.common.Result;
import co.yixiang.modules.shop.domain.MdCountry;
import co.yixiang.common.service.impl.BaseServiceImpl;
import co.yixiang.modules.shop.service.dto.CascadeDto;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.AllArgsConstructor;
import co.yixiang.dozer.service.IGenerator;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import co.yixiang.common.utils.QueryHelpPlus;
import co.yixiang.utils.ValidationUtil;
import co.yixiang.utils.FileUtil;
import co.yixiang.modules.shop.service.MdCountryService;
import co.yixiang.modules.shop.service.dto.MdCountryDto;
import co.yixiang.modules.shop.service.dto.MdCountryQueryCriteria;
import co.yixiang.modules.shop.service.mapper.MdCountryMapper;
import net.sf.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
// 默认不使用缓存
//import org.springframework.cache.annotation.CacheConfig;
//import org.springframework.cache.annotation.CacheEvict;
//import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.*;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;

/**
* @author visa
* @date 2020-10-16
*/
@Service
@AllArgsConstructor
//@CacheConfig(cacheNames = "mdCountry")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class MdCountryServiceImpl extends BaseServiceImpl<MdCountryMapper, MdCountry> implements MdCountryService {

    private final IGenerator generator;

    @Override
    //@Cacheable
    public Map<String, Object> queryAll(MdCountryQueryCriteria criteria, Pageable pageable) {
        getPage(pageable);
        PageInfo<MdCountry> page = new PageInfo<>(queryAll(criteria));
        Map<String, Object> map = new LinkedHashMap<>(2);
        map.put("content", generator.convert(page.getList(), MdCountryDto.class));
        map.put("totalElements", page.getTotal());
        return map;
    }


    @Override
    //@Cacheable
    public List<MdCountry> queryAll(MdCountryQueryCriteria criteria){
        QueryWrapper queryWrapper = QueryHelpPlus.getPredicate(MdCountry.class, criteria);
        return baseMapper.selectList(queryWrapper);
    }


    @Override
    public void download(List<MdCountryDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (MdCountryDto mdCountry : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("代码", mdCountry.getCode());
            map.put("名称", mdCountry.getName());
            map.put("名称拼音", mdCountry.getPinyin());
            map.put("父节点ID", mdCountry.getParentId());
            map.put("树节点ID", mdCountry.getTreeId());
            map.put("是否叶子节点", mdCountry.getIsLeaf());
            map.put("是否售药城市(0-否;1-是)", mdCountry.getIsSale());
            map.put("是否直辖市(0-否;1-是)", mdCountry.getIsDirect());
            map.put("城市编码，如021", mdCountry.getAreaCode());
            map.put("描述", mdCountry.getDescription());
            map.put("创建人", mdCountry.getCreateUser());
            map.put("创建时间", mdCountry.getCreateTime());
            map.put("更新人", mdCountry.getUpdateUser());
            map.put("更新时间", mdCountry.getUpdateTime());
            map.put("业务区域", mdCountry.getAreaName());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

    @Override
    public List<CascadeDto> queryAllCascade(MdCountryQueryCriteria criteria, Pageable pageable) {

        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("PARENT_ID","0");

        List<MdCountry> provinceList = list(queryWrapper);
        List<CascadeDto> provinceOption = new ArrayList<>();
        for(MdCountry province:provinceList) {
            QueryWrapper cityWrapper = new QueryWrapper();
            cityWrapper.eq("PARENT_ID",province.getId());
            List<MdCountry> cityList = list(cityWrapper);
            List<CascadeDto> childrenCity = new ArrayList<>();
            for(MdCountry city:cityList) {
                QueryWrapper dictWrapper = new QueryWrapper();
                dictWrapper.eq("PARENT_ID",city.getId());
                List<MdCountry> dictList = list(dictWrapper);
                List<CascadeDto> childrenDict = new ArrayList<>();
                for(MdCountry dict:dictList) {
                    String lable = dict.getName();
                    String value = dict.getCode();
                    String code = dict.getCode();
                    CascadeDto cascadeDto_dict = new CascadeDto();
                    cascadeDto_dict.setLabel(lable);
                    cascadeDto_dict.setValue(value);
                    cascadeDto_dict.setCode(code);
                    childrenDict.add(cascadeDto_dict);
                }

                CascadeDto cascadeDto_city = new CascadeDto();
                cascadeDto_city.setValue(city.getCode());
                cascadeDto_city.setLabel(city.getName());
                cascadeDto_city.setCode(city.getCode());
                cascadeDto_city.setChildren(childrenDict);
                childrenCity.add(cascadeDto_city);
            }

            CascadeDto cascadeDto_province = new CascadeDto();
            cascadeDto_province.setLabel(province.getName());
            cascadeDto_province.setValue(province.getCode());
            cascadeDto_province.setCode(province.getCode());
            cascadeDto_province.setChildren(childrenCity);

            provinceOption.add(cascadeDto_province);

        }


        return provinceOption;
    }

    @Override
    public List<CascadeDto> queryAllTree(MdCountryQueryCriteria criteria, Pageable pageable) {
        String parentid = criteria.getParentId();
        if(StrUtil.isBlank(parentid)) {
            parentid = "0";
        }
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("PARENT_ID",parentid);
        queryWrapper.select("code","name","id");
        List<MdCountry> provinceList = list(queryWrapper);
        List<CascadeDto> provinceOption = new ArrayList<>();
        for(MdCountry province:provinceList) {
            QueryWrapper cityWrapper = new QueryWrapper();
            cityWrapper.eq("PARENT_ID",province.getId());
            cityWrapper.select("code","name","id");
            List<MdCountry> cityList = list(cityWrapper);
            List<CascadeDto> childrenCity = new ArrayList<>();
            if("0".equals(parentid)) {
                for(MdCountry city:cityList) {
                    QueryWrapper dictWrapper = new QueryWrapper();
                    dictWrapper.eq("PARENT_ID",city.getId());
                    List<MdCountry> dictList = list(dictWrapper);
                    List<CascadeDto> childrenDict = new ArrayList<>();

                    CascadeDto cascadeDto_city = new CascadeDto();
                    cascadeDto_city.setValue(city.getCode());
                    cascadeDto_city.setLabel(city.getName());
                    cascadeDto_city.setCode(city.getCode());
                    cascadeDto_city.setChildren(childrenDict);
                    childrenCity.add(cascadeDto_city);
                }
            }


            CascadeDto cascadeDto_province = new CascadeDto();
            cascadeDto_province.setLabel(province.getName());
            cascadeDto_province.setValue(province.getCode());
            cascadeDto_province.setCode(province.getCode());
            cascadeDto_province.setChildren(childrenCity);

            provinceOption.add(cascadeDto_province);

        }


        return provinceOption;
    }

    @Override
    public Result<?> queryMdCountry(String parentCode) {
        List<Map<String,Object>> countryMap=new ArrayList<>();
        List<MdCountry> mdCountryList =  list(new QueryWrapper<MdCountry>().eq("PARENT_ID",parentCode));
        for (MdCountry mdCountry : mdCountryList) {
            Map<String,Object> map=new HashMap<>();
            map.put("code",mdCountry.getCode());
            map.put("name",mdCountry.getName());
            map.put("parentCode",mdCountry.getParentId());
            countryMap.add(map);
        }
        return Result.OK(countryMap);
    }
}
