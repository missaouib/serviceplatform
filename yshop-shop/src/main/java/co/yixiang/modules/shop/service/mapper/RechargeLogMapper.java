package co.yixiang.modules.shop.service.mapper;

import co.yixiang.modules.shop.domain.RechargeLog;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
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
@Mapper
public interface RechargeLogMapper extends BaseMapper<RechargeLog> {

}
