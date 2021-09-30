package co.yixiang.modules.yaoshitong.mapper;

import co.yixiang.modules.yaoshitong.entity.YaoshitongUserLable;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import co.yixiang.modules.yaoshitong.entity.YaoshitongUserLableRelation;
import co.yixiang.modules.yaoshitong.web.param.YaoshitongUserLableRelationQueryParam;
import co.yixiang.modules.yaoshitong.web.vo.YaoshitongUserLableRelationQueryVo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.util.List;

/**
 * <p>
 * 患者对应的标签库 Mapper 接口
 * </p>
 *
 * @author visa
 * @since 2020-08-26
 */
@Repository
public interface YaoshitongUserLableRelationMapper extends BaseMapper<YaoshitongUserLableRelation> {

    /**
     * 根据ID获取查询对象
     * @param id
     * @return
     */
    YaoshitongUserLableRelationQueryVo getYaoshitongUserLableRelationById(Serializable id);

    /**
     * 获取分页对象
     * @param page
     * @param yaoshitongUserLableRelationQueryParam
     * @return
     */
    IPage<YaoshitongUserLableRelationQueryVo> getYaoshitongUserLableRelationPageList(@Param("page") Page page, @Param("param") YaoshitongUserLableRelationQueryParam yaoshitongUserLableRelationQueryParam);

    @Select("SELECT yul.id,\n" +
            "       yul.uid,\n" +
            "       yul.lable_name,\n" +
            "       yul.create_time,\n" +
            "       yul.update_time,\n" +
            "       yul.is_default\n" +
            "    FROM yaoshitong_user_lable yul,yaoshitong_user_lable_relation yulr\n" +
            "  WHERE yulr.lable_id = yul.id\n" +
            "  AND yulr.pharmacist_id = #{pharmacistId} \n" +
            "  AND yulr.patient_id = #{patientId} ")
    List<YaoshitongUserLable> getUserLableRelationByUid(@Param("pharmacistId")String pharmacistId, @Param("patientId") Integer patientId);

}
