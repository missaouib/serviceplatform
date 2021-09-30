/**
* Copyright (C) 2018-2020
* All rights reserved, Designed By www.yixiang.co
* 注意：
* 本软件为www.yixiang.co开发研制，未经购买不得使用
* 购买后可获得全部源代码（禁止转卖、分享、上传到码云、github等开源平台）
* 一经发现盗用、分享等行为，将追究法律责任，后果自负
*/
package co.yixiang.modules.taiping.service.impl;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import co.yixiang.modules.taiping.domain.TaipingCard;
import co.yixiang.modules.taiping.domain.TaipingPayable;
import co.yixiang.common.service.impl.BaseServiceImpl;
import co.yixiang.modules.taiping.service.dto.TaipingDataDto;
import co.yixiang.modules.taiping.util.EncryptionToolUtilAes;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.AllArgsConstructor;
import co.yixiang.dozer.service.IGenerator;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import co.yixiang.common.utils.QueryHelpPlus;
import co.yixiang.utils.ValidationUtil;
import co.yixiang.utils.FileUtil;
import co.yixiang.modules.taiping.service.TaipingPayableService;
import co.yixiang.modules.taiping.service.dto.TaipingPayableDto;
import co.yixiang.modules.taiping.service.dto.TaipingPayableQueryCriteria;
import co.yixiang.modules.taiping.service.mapper.TaipingPayableMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
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
* @date 2020-11-03
*/
@Slf4j
@Service
//@AllArgsConstructor
//@CacheConfig(cacheNames = "taipingPayable")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class TaipingPayableServiceImpl extends BaseServiceImpl<TaipingPayableMapper, TaipingPayable> implements TaipingPayableService {

    @Autowired
    private  IGenerator generator;

    @Value("${taiping.CipherKey}")
    private String CipherKey;

    @Override
    //@Cacheable
    public Map<String, Object> queryAll(TaipingPayableQueryCriteria criteria, Pageable pageable) {
        getPage(pageable);
        PageInfo<TaipingPayable> page = new PageInfo<>(queryAll(criteria));
        Map<String, Object> map = new LinkedHashMap<>(2);
        map.put("content", generator.convert(page.getList(), TaipingPayableDto.class));
        map.put("totalElements", page.getTotal());
        return map;
    }


    @Override
    //@Cacheable
    public List<TaipingPayable> queryAll(TaipingPayableQueryCriteria criteria){
        return baseMapper.selectList(QueryHelpPlus.getPredicate(TaipingPayable.class, criteria));
    }


    @Override
    public void download(List<TaipingPayableDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (TaipingPayableDto taipingPayable : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("卡号", taipingPayable.getCardNumber());
            map.put("卡类型，根据卡类型获取单价作为结算依据", taipingPayable.getCardType());
            map.put("应付记录号", taipingPayable.getFeeID());
            map.put("应付记录的状态 1 新增记录  -1 负记录", taipingPayable.getNegativeRecord());
            map.put("卡渠道", taipingPayable.getSellChannel());
            map.put("代理", taipingPayable.getAgentCate());
            map.put("组织ID", taipingPayable.getOrganID());
            map.put("乐享同步记录时间", taipingPayable.getInsertTime());
            map.put("记录生成时间", taipingPayable.getCreateTime());
            map.put("记录更新时间", taipingPayable.getUpdateTime());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

    @Override
    public String savePayable(TaipingDataDto resource) {
        String dataEncrypt = resource.getData();

        String dataDEC = EncryptionToolUtilAes.decrypt(dataEncrypt, CipherKey);
        JSONObject jsonObject = JSONUtil.createObj();
        TaipingPayable payable = JSONUtil.toBean(dataDEC,TaipingPayable.class);
        log.info("{}",payable);
        try {
            QueryWrapper queryWrapper = new QueryWrapper();
            queryWrapper.eq("fee_id",payable.getFeeID());
            int existsFalg = this.count(queryWrapper);
            if(existsFalg == 0) {
                this.save(payable);
            }

            jsonObject.put("status",1);
        }catch (DuplicateKeyException e) {
            // e.printStackTrace();
            jsonObject.put("status",1);
        } catch (Exception e) {
            e.printStackTrace();
            jsonObject.put("status",0);
        }



        return JSONUtil.toJsonStr(jsonObject);
    }
}
