package co.yixiang.modules.yaolian.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import co.yixiang.modules.yaolian.entity.YaolianOrderDetail;
import co.yixiang.modules.yaolian.web.param.YaolianOrderDetailQueryParam;
import co.yixiang.modules.yaolian.web.vo.YaolianOrderDetailQueryVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.io.Serializable;

/**
 * <p>
 * 药联订单明细 Mapper 接口
 * </p>
 *
 * @author visa
 * @since 2021-03-05
 */
@Repository
public interface YaolianOrderDetailMapper extends BaseMapper<YaolianOrderDetail> {

    /**
     * 根据ID获取查询对象
     * @param id
     * @return
     */
    YaolianOrderDetailQueryVo getYaolianOrderDetailById(Serializable id);

    /**
     * 获取分页对象
     * @param page
     * @param yaolianOrderDetailQueryParam
     * @return
     */
    IPage<YaolianOrderDetailQueryVo> getYaolianOrderDetailPageList(@Param("page") Page page, @Param("param") YaolianOrderDetailQueryParam yaolianOrderDetailQueryParam);

}
