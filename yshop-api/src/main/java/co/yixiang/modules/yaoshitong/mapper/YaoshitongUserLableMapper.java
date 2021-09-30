package co.yixiang.modules.yaoshitong.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import co.yixiang.modules.yaoshitong.entity.YaoshitongUserLable;
import co.yixiang.modules.yaoshitong.web.param.YaoshitongUserLableQueryParam;
import co.yixiang.modules.yaoshitong.web.vo.YaoshitongUserLableQueryVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.io.Serializable;

/**
 * <p>
 * 药师通用户标签 Mapper 接口
 * </p>
 *
 * @author visa
 * @since 2020-08-26
 */
@Repository
public interface YaoshitongUserLableMapper extends BaseMapper<YaoshitongUserLable> {

    /**
     * 根据ID获取查询对象
     * @param id
     * @return
     */
    YaoshitongUserLableQueryVo getYaoshitongUserLableById(Serializable id);

    /**
     * 获取分页对象
     * @param page
     * @param yaoshitongUserLableQueryParam
     * @return
     */
    IPage<YaoshitongUserLableQueryVo> getYaoshitongUserLablePageList(@Param("page") Page page, @Param("param") YaoshitongUserLableQueryParam yaoshitongUserLableQueryParam);

}
