package co.yixiang.modules.yaoshitong.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import co.yixiang.modules.yaoshitong.entity.YaoshitongPrescription;
import co.yixiang.modules.yaoshitong.web.param.YaoshitongPrescriptionQueryParam;
import co.yixiang.modules.yaoshitong.web.vo.YaoshitongPrescriptionQueryVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.io.Serializable;

/**
 * <p>
 * 药师通-处方信息表 Mapper 接口
 * </p>
 *
 * @author visa
 * @since 2020-07-17
 */
@Repository
public interface YaoshitongPrescriptionMapper extends BaseMapper<YaoshitongPrescription> {

    /**
     * 根据ID获取查询对象
     * @param id
     * @return
     */
    YaoshitongPrescriptionQueryVo getYaoshitongPrescriptionById(Serializable id);

    /**
     * 获取分页对象
     * @param page
     * @param yaoshitongPrescriptionQueryParam
     * @return
     */
    IPage<YaoshitongPrescriptionQueryVo> getYaoshitongPrescriptionPageList(@Param("page") Page page, @Param("param") YaoshitongPrescriptionQueryParam yaoshitongPrescriptionQueryParam);

}
