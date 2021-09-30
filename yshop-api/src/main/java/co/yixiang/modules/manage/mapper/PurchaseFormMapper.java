package co.yixiang.modules.manage.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import co.yixiang.modules.manage.entity.PurchaseForm;
import co.yixiang.modules.manage.web.param.PurchaseFormQueryParam;
import co.yixiang.modules.manage.web.vo.PurchaseFormQueryVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.io.Serializable;

/**
 * <p>
 * 采购需求单 Mapper 接口
 * </p>
 *
 * @author visazhou
 * @since 2020-05-20
 */
@Repository
public interface PurchaseFormMapper extends BaseMapper<PurchaseForm> {

    /**
     * 根据ID获取查询对象
     * @param id
     * @return
     */
    PurchaseFormQueryVo getPurchaseFormById(Serializable id);

    /**
     * 获取分页对象
     * @param page
     * @param purchaseFormQueryParam
     * @return
     */
    IPage<PurchaseFormQueryVo> getPurchaseFormPageList(@Param("page") Page page, @Param("param") PurchaseFormQueryParam purchaseFormQueryParam);

}
