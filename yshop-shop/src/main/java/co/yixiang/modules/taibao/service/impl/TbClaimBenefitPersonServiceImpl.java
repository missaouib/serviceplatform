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
import co.yixiang.modules.taibao.domain.TbClaimBenefitPerson;
import co.yixiang.modules.taibao.service.TbClaimBenefitPersonService;
import co.yixiang.modules.taibao.service.dto.TbClaimBenefitPersonDto;
import co.yixiang.modules.taibao.service.dto.TbClaimBenefitPersonQueryCriteria;
import co.yixiang.modules.taibao.service.mapper.TbClaimBenefitPersonMapper;
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
//@CacheConfig(cacheNames = "tbClaimBenefitPerson")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class TbClaimBenefitPersonServiceImpl extends BaseServiceImpl<TbClaimBenefitPersonMapper, TbClaimBenefitPerson> implements TbClaimBenefitPersonService {

    private final IGenerator generator;
    @Autowired
    private TbClaimBenefitPersonMapper claimBenefitPersonMapper;
    @Override
    //@Cacheable
    public Map<String, Object> queryAll(TbClaimBenefitPersonQueryCriteria criteria, Pageable pageable) {
        getPage(pageable);
        PageInfo<TbClaimBenefitPerson> page = new PageInfo<>(queryAll(criteria));
        Map<String, Object> map = new LinkedHashMap<>(2);
        map.put("content", generator.convert(page.getList(), TbClaimBenefitPersonDto.class));
        map.put("totalElements", page.getTotal());
        return map;
    }


    @Override
    //@Cacheable
    public List<TbClaimBenefitPerson> queryAll(TbClaimBenefitPersonQueryCriteria criteria){
        return baseMapper.selectList(QueryHelpPlus.getPredicate(TbClaimBenefitPerson.class, criteria));
    }


    @Override
    public void download(List<TbClaimBenefitPersonDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (TbClaimBenefitPersonDto tbClaimBenefitPerson : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("赔案信息Id", tbClaimBenefitPerson.getClaimInfoId());
            map.put("领款人类型（1个人、2单位）", tbClaimBenefitPerson.getBftype());
            map.put("与被保人关系", tbClaimBenefitPerson.getRelationship());
            map.put("证件类型", tbClaimBenefitPerson.getIdtype());
            map.put("证件号码", tbClaimBenefitPerson.getIdno());
            map.put("证件有效期起期", tbClaimBenefitPerson.getIdBegdate());
            map.put("证件有效期止期", tbClaimBenefitPerson.getIdEnddate());
            map.put("领款人姓名", tbClaimBenefitPerson.getName());
            map.put("性别", tbClaimBenefitPerson.getSex());
            map.put("出生日期", tbClaimBenefitPerson.getBirthdate());
            map.put("移动电话", tbClaimBenefitPerson.getMobilephone());
            map.put("固定电话", tbClaimBenefitPerson.getTelephone());
            map.put("邮箱地址", tbClaimBenefitPerson.getEmail());
            map.put("联系地址", tbClaimBenefitPerson.getAddr());
            map.put("邮政编码", tbClaimBenefitPerson.getZip());
            map.put("支付方式 （1现金2支票3转账）", tbClaimBenefitPerson.getSettype());
            map.put("银行名称（银行类别）提供枚举值", tbClaimBenefitPerson.getBanktype());
            map.put("开户行", tbClaimBenefitPerson.getBanksubtype());
            map.put("分行", tbClaimBenefitPerson.getBankbranch());
            map.put("支行", tbClaimBenefitPerson.getBanksubbranch());
            map.put("银行所在省", tbClaimBenefitPerson.getProvinceofbank());
            map.put("银行所在市", tbClaimBenefitPerson.getCityofbank());
            map.put("银行账号", tbClaimBenefitPerson.getAcctno());
            map.put("所属部门", tbClaimBenefitPerson.getSysOrgCode());
            map.put("更新日期", tbClaimBenefitPerson.getUpdateTime());
            map.put("更新人", tbClaimBenefitPerson.getUpdateBy());
            map.put("创建日期", tbClaimBenefitPerson.getCreateTime());
            map.put("创建人", tbClaimBenefitPerson.getCreateBy());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

    @Override
    public List<TbClaimBenefitPerson> selectByMainId(String mainId) {
        return claimBenefitPersonMapper.selectByMainId(Long.valueOf(mainId));
    }
}
