package co.yixiang.modules.yaoshitong.service;

import co.yixiang.modules.yaoshitong.entity.YaoshitongRepurchaseReminder;
import co.yixiang.common.service.BaseService;
import co.yixiang.modules.yaoshitong.web.param.YaoshitongRepurchaseReminderQueryParam;
import co.yixiang.modules.yaoshitong.web.vo.YaoshitongRepurchaseReminderQueryVo;
import co.yixiang.common.web.vo.Paging;

import java.io.Serializable;

/**
 * <p>
 * 药品复购提醒 服务类
 * </p>
 *
 * @author visa
 * @since 2020-10-21
 */
public interface YaoshitongRepurchaseReminderService extends BaseService<YaoshitongRepurchaseReminder> {

    /**
     * 根据ID获取查询对象
     * @param id
     * @return
     */
    YaoshitongRepurchaseReminderQueryVo getYaoshitongRepurchaseReminderById(Serializable id) throws Exception;

    /**
     * 获取分页对象
     * @param yaoshitongRepurchaseReminderQueryParam
     * @return
     */
    Paging<YaoshitongRepurchaseReminderQueryVo> getYaoshitongRepurchaseReminderPageList(YaoshitongRepurchaseReminderQueryParam yaoshitongRepurchaseReminderQueryParam) throws Exception;

}
