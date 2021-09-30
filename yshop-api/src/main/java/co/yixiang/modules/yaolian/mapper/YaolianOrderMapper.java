package co.yixiang.modules.yaolian.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import co.yixiang.modules.yaolian.entity.YaolianOrder;
import co.yixiang.modules.yaolian.web.param.YaolianOrderQueryParam;
import co.yixiang.modules.yaolian.web.vo.YaolianOrderQueryVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.io.Serializable;

/**
 * <p>
 * 药联订单表 Mapper 接口
 * </p>
 *
 * @author visa
 * @since 2021-03-05
 */
@Repository
public interface YaolianOrderMapper extends BaseMapper<YaolianOrder> {

    /**
     * 根据ID获取查询对象
     * @param id
     * @return
     */
    YaolianOrderQueryVo getYaolianOrderById(Serializable id);

    /**
     * 获取分页对象
     * @param page
     * @param yaolianOrderQueryParam
     * @return
     */
    IPage<YaolianOrderQueryVo> getYaolianOrderPageList(@Param("page") Page page, @Param("param") YaolianOrderQueryParam yaolianOrderQueryParam);

}
