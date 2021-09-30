/**
* Copyright (C) 2018-2020
* All rights reserved, Designed By www.yixiang.co
* 注意：
* 本软件为www.yixiang.co开发研制，未经购买不得使用
* 购买后可获得全部源代码（禁止转卖、分享、上传到码云、github等开源平台）
* 一经发现盗用、分享等行为，将追究法律责任，后果自负
*/
package co.yixiang.modules.yaolian.service.impl;

import co.yixiang.modules.yaolian.domain.YaolianOrderDetail;
import co.yixiang.common.service.impl.BaseServiceImpl;
import lombok.AllArgsConstructor;
import co.yixiang.dozer.service.IGenerator;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import co.yixiang.common.utils.QueryHelpPlus;
import co.yixiang.utils.ValidationUtil;
import co.yixiang.utils.FileUtil;
import co.yixiang.modules.yaolian.service.YaolianOrderDetailService;
import co.yixiang.modules.yaolian.service.dto.YaolianOrderDetailDto;
import co.yixiang.modules.yaolian.service.dto.YaolianOrderDetailQueryCriteria;
import co.yixiang.modules.yaolian.service.mapper.YaolianOrderDetailMapper;
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
* @date 2021-03-02
*/
@Service
@AllArgsConstructor
//@CacheConfig(cacheNames = "yaolianOrderDetail")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class YaolianOrderDetailServiceImpl extends BaseServiceImpl<YaolianOrderDetailMapper, YaolianOrderDetail> implements YaolianOrderDetailService {

    private final IGenerator generator;

    @Override
    //@Cacheable
    public Map<String, Object> queryAll(YaolianOrderDetailQueryCriteria criteria, Pageable pageable) {
        getPage(pageable);
        PageInfo<YaolianOrderDetail> page = new PageInfo<>(queryAll(criteria));
        Map<String, Object> map = new LinkedHashMap<>(2);
        map.put("content", generator.convert(page.getList(), YaolianOrderDetailDto.class));
        map.put("totalElements", page.getTotal());
        return map;
    }


    @Override
    //@Cacheable
    public List<YaolianOrderDetail> queryAll(YaolianOrderDetailQueryCriteria criteria){
        return baseMapper.selectList(QueryHelpPlus.getPredicate(YaolianOrderDetail.class, criteria));
    }


    @Override
    public void download(List<YaolianOrderDetailDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (YaolianOrderDetailDto yaolianOrderDetail : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("药联订单号", yaolianOrderDetail.getOrderId());
            map.put("商品id，和连锁商品保持一致", yaolianOrderDetail.getDrugId());
            map.put("商品通用名", yaolianOrderDetail.getCommonName());
            map.put("数量", yaolianOrderDetail.getAmount());
            map.put("商品原价", yaolianOrderDetail.getPrice());
            map.put("结算扣率", yaolianOrderDetail.getSettleDiscountRate());
            map.put("商品条形码", yaolianOrderDetail.getCode());
            map.put("1:使用优惠价购买0:未使用到优惠价", yaolianOrderDetail.getActivityType());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }
}
