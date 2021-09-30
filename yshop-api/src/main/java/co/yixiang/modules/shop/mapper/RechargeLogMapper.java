package co.yixiang.modules.shop.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import co.yixiang.modules.shop.entity.RechargeLog;
import co.yixiang.modules.shop.web.param.RechargeLogQueryParam;
import co.yixiang.modules.shop.web.vo.RechargeLogQueryVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.io.Serializable;

/**
 * <p>
 * 储值记录表 Mapper 接口
 * </p>
 *
 * @author visa
 * @since 2021-07-05
 */
@Repository
public interface RechargeLogMapper extends BaseMapper<RechargeLog> {

    /**
     * 根据ID获取查询对象
     * @param id
     * @return
     */
    RechargeLogQueryVo getRechargeLogById(Serializable id);

    /**
     * 获取分页对象
     * @param page
     * @param rechargeLogQueryParam
     * @return
     */
    IPage<RechargeLogQueryVo> getRechargeLogPageList(@Param("page") Page page, @Param("param") RechargeLogQueryParam rechargeLogQueryParam);

}
