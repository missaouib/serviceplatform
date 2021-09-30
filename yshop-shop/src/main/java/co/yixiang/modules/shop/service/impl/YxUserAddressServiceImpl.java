/**
* Copyright (C) 2018-2020
* All rights reserved, Designed By www.yixiang.co
* 注意：
* 本软件为www.yixiang.co开发研制，未经购买不得使用
* 购买后可获得全部源代码（禁止转卖、分享、上传到码云、github等开源平台）
* 一经发现盗用、分享等行为，将追究法律责任，后果自负
*/
package co.yixiang.modules.shop.service.impl;

import co.yixiang.modules.shop.domain.YxUserAddress;
import co.yixiang.common.service.impl.BaseServiceImpl;
import co.yixiang.modules.shop.service.vo.YxUserAddressQueryVo;
import lombok.AllArgsConstructor;
import co.yixiang.dozer.service.IGenerator;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import co.yixiang.common.utils.QueryHelpPlus;
import co.yixiang.utils.ValidationUtil;
import co.yixiang.utils.FileUtil;
import co.yixiang.modules.shop.service.YxUserAddressService;
import co.yixiang.modules.shop.service.dto.YxUserAddressDto;
import co.yixiang.modules.shop.service.dto.YxUserAddressQueryCriteria;
import co.yixiang.modules.shop.service.mapper.YxUserAddressMapper;
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

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
* @author visa
* @date 2020-10-15
*/
@Service
@AllArgsConstructor
//@CacheConfig(cacheNames = "yxUserAddress")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class YxUserAddressServiceImpl extends BaseServiceImpl<YxUserAddressMapper, YxUserAddress> implements YxUserAddressService {

    private final IGenerator generator;
    @Autowired
    private YxUserAddressMapper userAddressMapper;

    @Override
    //@Cacheable
    public Map<String, Object> queryAll(YxUserAddressQueryCriteria criteria, Pageable pageable) {
        getPage(pageable);
        PageInfo<YxUserAddress> page = new PageInfo<>(queryAll(criteria));
        Map<String, Object> map = new LinkedHashMap<>(2);
        map.put("content", generator.convert(page.getList(), YxUserAddressDto.class));
        map.put("totalElements", page.getTotal());
        return map;
    }


    @Override
    //@Cacheable
    public List<YxUserAddress> queryAll(YxUserAddressQueryCriteria criteria){
        return baseMapper.selectList(QueryHelpPlus.getPredicate(YxUserAddress.class, criteria));
    }


    @Override
    public void download(List<YxUserAddressDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (YxUserAddressDto yxUserAddress : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("用户id", yxUserAddress.getUid());
            map.put("收货人姓名", yxUserAddress.getRealName());
            map.put("收货人电话", yxUserAddress.getPhone());
            map.put("收货人所在省", yxUserAddress.getProvince());
            map.put("收货人所在市", yxUserAddress.getCity());
            map.put("收货人所在区", yxUserAddress.getDistrict());
            map.put("收货人详细地址", yxUserAddress.getDetail());
            map.put("邮编", yxUserAddress.getPostCode());
            map.put("经度", yxUserAddress.getLongitude());
            map.put("纬度", yxUserAddress.getLatitude());
            map.put("是否默认", yxUserAddress.getIsDefault());
            map.put("是否删除", yxUserAddress.getIsDel());
            map.put("添加时间", yxUserAddress.getAddTime());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

    @Override
    public YxUserAddressQueryVo getYxUserAddressById(String addressId) {
        return userAddressMapper.getYxUserAddressById(addressId);
    }

}
