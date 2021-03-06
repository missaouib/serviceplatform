/**
 * Copyright (C) 2018-2019
 * All rights reserved, Designed By www.yixiang.co
 * 注意：
 * 本软件为www.yixiang.co开发研制，未经购买不得使用
 * 购买后可获得全部源代码（禁止转卖、分享、上传到码云、github等开源平台）
 * 一经发现盗用、分享等行为，将追究法律责任，后果自负
 */
package co.yixiang.modules.shop.service.impl;

import cn.hutool.core.util.StrUtil;
import co.yixiang.common.service.impl.BaseServiceImpl;
import co.yixiang.modules.shop.entity.YxStoreCategory;
import co.yixiang.modules.shop.mapper.YxStoreCategoryMapper;
import co.yixiang.modules.shop.mapping.CategoryMap;
import co.yixiang.modules.shop.service.YxStoreCategoryService;
import co.yixiang.modules.shop.web.param.YxStoreCategoryQueryParam;
import co.yixiang.modules.shop.web.vo.YxStoreCategoryQueryVo;
import co.yixiang.utils.CateDTO;
import co.yixiang.utils.TreeUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.List;


/**
 * <p>
 * 商品分类表 服务实现类
 * </p>
 *
 * @author hupeng
 * @since 2019-10-22
 */
@Slf4j
@Service
@AllArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class YxStoreCategoryServiceImpl extends BaseServiceImpl<YxStoreCategoryMapper, YxStoreCategory> implements YxStoreCategoryService {

    private final YxStoreCategoryMapper yxStoreCategoryMapper;

    private final CategoryMap categoryMap;

    @Override
    public YxStoreCategoryQueryVo getYxStoreCategoryById(Serializable id) throws Exception{
        return yxStoreCategoryMapper.getYxStoreCategoryById(id);
    }

    @Override
    public List<CateDTO> getList() {
      //  Integer partnerId = 1;
        QueryWrapper<YxStoreCategory> wrapper = new QueryWrapper<>();
        wrapper.eq("is_show",1).eq("is_del",0).orderByAsc("sort");
       // wrapper.apply(" id IN (SELECT ysp.cate_id FROM yx_store_product ysp ,product_partner_mapping ppm WHERE ysp.id = ppm.product_id AND ppm.partner_id = {0})",partnerId);
        List<CateDTO> list = categoryMap.toDto(baseMapper.selectList(wrapper));
        return TreeUtil.list2TreeConverter(list,0);
    }

    @Override
    public List<CateDTO> getDepartmentList(YxStoreCategoryQueryParam yxStoreCategoryQueryParam) {
        String partnerId = yxStoreCategoryQueryParam.getPartnerId();
        QueryWrapper<YxStoreCategory> wrapper = new QueryWrapper<>();
        wrapper.eq("is_show",1).eq("is_del",0).orderByAsc("sort");
        if(StrUtil.isNotBlank(partnerId )) {
            wrapper.apply(" id IN (SELECT ysp.cate_id FROM yx_store_product ysp ,product_partner_mapping ppm WHERE ysp.is_del = 0 and ysp.id = ppm.product_id AND ppm.partner_id = {0})", Integer.valueOf( partnerId));
        }

        List<CateDTO> list = categoryMap.toDto(baseMapper.selectList(wrapper));

        return list;
    }

    @Override
    public List<String> getAllChilds(int catid) {
        QueryWrapper<YxStoreCategory> wrapper = new QueryWrapper<>();
        wrapper.eq("is_show",1).eq("id",catid);

        List<CateDTO> list = categoryMap.toDto(baseMapper.selectList(wrapper));

        //System.out.println(TreeUtil.getChildList(list,new CateDTO()));
        return null;
    }
}
