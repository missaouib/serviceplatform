package co.yixiang.modules.shop.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import co.yixiang.modules.shop.entity.YxStoreCouponCard;
import co.yixiang.modules.shop.web.param.YxStoreCouponCardQueryParam;
import co.yixiang.modules.shop.web.vo.YxStoreCouponCardQueryVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.io.Serializable;

/**
 * <p>
 * 优惠券发放记录表 Mapper 接口
 * </p>
 *
 * @author visa
 * @since 2020-12-10
 */
@Repository
public interface YxStoreCouponCardMapper extends BaseMapper<YxStoreCouponCard> {

    /**
     * 根据ID获取查询对象
     * @param id
     * @return
     */
    YxStoreCouponCardQueryVo getYxStoreCouponCardById(Serializable id);

    /**
     * 获取分页对象
     * @param page
     * @param yxStoreCouponCardQueryParam
     * @return
     */
    IPage<YxStoreCouponCardQueryVo> getYxStoreCouponCardPageList(@Param("page") Page page, @Param("param") YxStoreCouponCardQueryParam yxStoreCouponCardQueryParam);

}
