package co.yixiang.modules.yaoshitong.service;

import co.yixiang.modules.yaoshitong.entity.YaoshitongUserLable;
import co.yixiang.modules.yaoshitong.entity.YaoshitongUserLableRelation;
import co.yixiang.common.service.BaseService;
import co.yixiang.modules.yaoshitong.web.param.YaoshitongUserLableRelationQueryParam;
import co.yixiang.modules.yaoshitong.web.vo.YaoshitongUserLableRelationQueryVo;
import co.yixiang.common.web.vo.Paging;

import java.io.Serializable;
import java.util.List;

/**
 * <p>
 * 患者对应的标签库 服务类
 * </p>
 *
 * @author visa
 * @since 2020-08-26
 */
public interface YaoshitongUserLableRelationService extends BaseService<YaoshitongUserLableRelation> {

    /**
     * 根据ID获取查询对象
     * @param id
     * @return
     */
    YaoshitongUserLableRelationQueryVo getYaoshitongUserLableRelationById(Serializable id) throws Exception;

    /**
     * 获取分页对象
     * @param yaoshitongUserLableRelationQueryParam
     * @return
     */
    Paging<YaoshitongUserLableRelationQueryVo> getYaoshitongUserLableRelationPageList(YaoshitongUserLableRelationQueryParam yaoshitongUserLableRelationQueryParam) throws Exception;

    List<YaoshitongUserLable> getUserLableRelationByUid(String pharmacistId, Integer patientId);
}
