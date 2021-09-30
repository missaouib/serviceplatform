package co.yixiang.modules.manage.service.impl;

import co.yixiang.modules.manage.entity.PurchaseForm;
import co.yixiang.modules.manage.mapper.PurchaseFormMapper;
import co.yixiang.modules.manage.service.PurchaseFormService;
import co.yixiang.modules.manage.web.param.PurchaseFormQueryParam;
import co.yixiang.modules.manage.web.vo.PurchaseFormQueryVo;
import co.yixiang.common.service.impl.BaseServiceImpl;
import co.yixiang.common.web.vo.Paging;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import java.io.Serializable;


/**
 * <p>
 * 采购需求单 服务实现类
 * </p>
 *
 * @author visazhou
 * @since 2020-05-20
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class PurchaseFormServiceImpl extends BaseServiceImpl<PurchaseFormMapper, PurchaseForm> implements PurchaseFormService {

    @Autowired
    private PurchaseFormMapper purchaseFormMapper;

    @Override
    public PurchaseFormQueryVo getPurchaseFormById(Serializable id) throws Exception{
        return purchaseFormMapper.getPurchaseFormById(id);
    }

    @Override
    public Paging<PurchaseFormQueryVo> getPurchaseFormPageList(PurchaseFormQueryParam purchaseFormQueryParam) throws Exception{
        Page page = setPageParam(purchaseFormQueryParam,OrderItem.desc("create_time"));
        IPage<PurchaseFormQueryVo> iPage = purchaseFormMapper.getPurchaseFormPageList(page,purchaseFormQueryParam);
        return new Paging(iPage);
    }

}
