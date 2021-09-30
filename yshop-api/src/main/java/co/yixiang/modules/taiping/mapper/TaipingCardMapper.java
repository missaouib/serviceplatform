package co.yixiang.modules.taiping.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import co.yixiang.modules.taiping.entity.TaipingCard;
import co.yixiang.modules.taiping.web.param.TaipingCardQueryParam;
import co.yixiang.modules.taiping.web.vo.TaipingCardQueryVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.io.Serializable;

/**
 * <p>
 * 太平乐享虚拟卡 Mapper 接口
 * </p>
 *
 * @author visa
 * @since 2020-11-19
 */
@Repository
public interface TaipingCardMapper extends BaseMapper<TaipingCard> {

    /**
     * 根据ID获取查询对象
     * @param id
     * @return
     */
    TaipingCardQueryVo getTaipingCardById(Serializable id);

    /**
     * 获取分页对象
     * @param page
     * @param taipingCardQueryParam
     * @return
     */
    IPage<TaipingCardQueryVo> getTaipingCardPageList(@Param("page") Page page, @Param("param") TaipingCardQueryParam taipingCardQueryParam);

}
