package co.yixiang.modules.yaoshitong.service;

import co.yixiang.modules.yaoshitong.entity.YaoshitongUserLable;
import co.yixiang.common.service.BaseService;
import co.yixiang.modules.yaoshitong.web.param.YaoshitongUserLableQueryParam;
import co.yixiang.modules.yaoshitong.web.vo.YaoshitongUserLableQueryVo;
import co.yixiang.common.web.vo.Paging;

import java.io.Serializable;

/**
 * <p>
 * 药师通用户标签 服务类
 * </p>
 *
 * @author visa
 * @since 2020-08-26
 */
public interface YaoshitongUserLableService extends BaseService<YaoshitongUserLable> {

    /**
     * 根据ID获取查询对象
     * @param id
     * @return
     */
    YaoshitongUserLableQueryVo getYaoshitongUserLableById(Serializable id) throws Exception;

    /**
     * 获取分页对象
     * @param yaoshitongUserLableQueryParam
     * @return
     */
    Paging<YaoshitongUserLable> getYaoshitongUserLablePageList(YaoshitongUserLableQueryParam yaoshitongUserLableQueryParam) throws Exception;

    void saveUserLable(YaoshitongUserLable yaoshitongUserLable);
}
