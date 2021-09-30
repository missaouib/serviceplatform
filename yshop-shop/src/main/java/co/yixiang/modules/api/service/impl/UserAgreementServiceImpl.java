/**
* Copyright (C) 2018-2020
* All rights reserved, Designed By www.yixiang.co
* 注意：
* 本软件为www.yixiang.co开发研制，未经购买不得使用
* 购买后可获得全部源代码（禁止转卖、分享、上传到码云、github等开源平台）
* 一经发现盗用、分享等行为，将追究法律责任，后果自负
*/
package co.yixiang.modules.api.service.impl;

import co.yixiang.modules.api.domain.UserAgreement;
import co.yixiang.common.service.impl.BaseServiceImpl;
import lombok.AllArgsConstructor;
import co.yixiang.dozer.service.IGenerator;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import co.yixiang.common.utils.QueryHelpPlus;
import co.yixiang.utils.ValidationUtil;
import co.yixiang.utils.FileUtil;
import co.yixiang.modules.api.service.UserAgreementService;
import co.yixiang.modules.api.service.dto.UserAgreementDto;
import co.yixiang.modules.api.service.dto.UserAgreementQueryCriteria;
import co.yixiang.modules.api.service.mapper.UserAgreementMapper;
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
* @date 2020-11-30
*/
@Service
@AllArgsConstructor
//@CacheConfig(cacheNames = "userAgreement")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class UserAgreementServiceImpl extends BaseServiceImpl<UserAgreementMapper, UserAgreement> implements UserAgreementService {

    private final IGenerator generator;

    @Override
    //@Cacheable
    public Map<String, Object> queryAll(UserAgreementQueryCriteria criteria, Pageable pageable) {
        getPage(pageable);
        PageInfo<UserAgreement> page = new PageInfo<>(queryAll(criteria));
        Map<String, Object> map = new LinkedHashMap<>(2);
        map.put("content", generator.convert(page.getList(), UserAgreementDto.class));
        map.put("totalElements", page.getTotal());
        return map;
    }


    @Override
    //@Cacheable
    public List<UserAgreement> queryAll(UserAgreementQueryCriteria criteria){
        return baseMapper.selectList(QueryHelpPlus.getPredicate(UserAgreement.class, criteria));
    }


    @Override
    public void download(List<UserAgreementDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (UserAgreementDto userAgreement : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("用户id", userAgreement.getUid());
            map.put("患者姓名", userAgreement.getUserName());
            map.put("患者手机号", userAgreement.getUserPhone());
            map.put("签名请求ID", userAgreement.getRequestId());
            map.put("签名ID", userAgreement.getSignFlowId());
            map.put("签名的pdf地址", userAgreement.getSignFilePath());
            map.put("是否已经签名 0否 1是", userAgreement.getStatus());
            map.put("记录生成时间", userAgreement.getCreateTime());
            map.put("记录更新时间", userAgreement.getUpdateTime());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }
}
