package co.yixiang.modules.yaoshitong.service;

import co.yixiang.modules.yaoshitong.entity.YaoshitongPrescription;
import co.yixiang.common.service.BaseService;
import co.yixiang.modules.yaoshitong.web.param.YaoshitongPrescriptionQueryParam;
import co.yixiang.modules.yaoshitong.web.vo.YaoshitongPrescriptionListQueryVo;
import co.yixiang.modules.yaoshitong.web.vo.YaoshitongPrescriptionQueryVo;
import co.yixiang.common.web.vo.Paging;

import java.io.Serializable;

/**
 * <p>
 * 药师通-处方信息表 服务类
 * </p>
 *
 * @author visa
 * @since 2020-07-17
 */
public interface YaoshitongPrescriptionService extends BaseService<YaoshitongPrescription> {

    /**
     * 根据ID获取查询对象
     * @param id
     * @return
     */
    YaoshitongPrescriptionQueryVo getYaoshitongPrescriptionById(Serializable id) throws Exception;

    /**
     * 获取分页对象
     * @param yaoshitongPrescriptionQueryParam
     * @return
     */
    Paging<YaoshitongPrescriptionListQueryVo> getYaoshitongPrescriptionPageList(YaoshitongPrescriptionQueryParam yaoshitongPrescriptionQueryParam) throws Exception;

    int saveYaoshitongPrescription(YaoshitongPrescription resource);

}
