package co.yixiang.mp.yiyaobao.service.impl;

import co.yixiang.mp.yiyaobao.entity.CmdStockDetailEbs;
import co.yixiang.mp.yiyaobao.mapper.CmdStockDetailEbsMapper;
import co.yixiang.mp.yiyaobao.service.CmdStockDetailEbsService;
import co.yixiang.common.service.impl.BaseServiceImpl;
import co.yixiang.common.web.vo.Paging;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import java.io.Serializable;
import java.util.List;


/**
 * <p>
 * 商品库存明细表 服务实现类
 * </p>
 *
 * @author visazhou
 * @since 2020-06-16
 */
@Slf4j
@Service
public class CmdStockDetailEbsServiceImpl extends BaseServiceImpl<CmdStockDetailEbsMapper, CmdStockDetailEbs> implements CmdStockDetailEbsService {

    @Autowired
    private CmdStockDetailEbsMapper cmdStockDetailEbsMapper;



    @Override
    @DS("master")
    public void syncStock(List<CmdStockDetailEbs> list) {
        QueryWrapper queryWrapper = new QueryWrapper();
         // 删除历史数据
         remove(queryWrapper);
         // 保存最新数据
         saveBatch(list);

         for(CmdStockDetailEbs cmdStockDetailEbs:list) {

         }

    }
}
