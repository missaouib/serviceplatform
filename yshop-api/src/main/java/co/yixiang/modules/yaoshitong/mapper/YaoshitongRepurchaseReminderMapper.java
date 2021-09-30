package co.yixiang.modules.yaoshitong.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import co.yixiang.modules.yaoshitong.entity.YaoshitongRepurchaseReminder;
import co.yixiang.modules.yaoshitong.web.param.YaoshitongRepurchaseReminderQueryParam;
import co.yixiang.modules.yaoshitong.web.vo.YaoshitongRepurchaseReminderQueryVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.io.Serializable;

/**
 * <p>
 * 药品复购提醒 Mapper 接口
 * </p>
 *
 * @author visa
 * @since 2020-10-21
 */
@Repository
public interface YaoshitongRepurchaseReminderMapper extends BaseMapper<YaoshitongRepurchaseReminder> {

    /**
     * 根据ID获取查询对象
     * @param id
     * @return
     */
    YaoshitongRepurchaseReminderQueryVo getYaoshitongRepurchaseReminderById(Serializable id);

    /**
     * 获取分页对象
     * @param page
     * @param yaoshitongRepurchaseReminderQueryParam
     * @return
     */
    IPage<YaoshitongRepurchaseReminderQueryVo> getYaoshitongRepurchaseReminderPageList(@Param("page") Page page, @Param("param") YaoshitongRepurchaseReminderQueryParam yaoshitongRepurchaseReminderQueryParam);

}
