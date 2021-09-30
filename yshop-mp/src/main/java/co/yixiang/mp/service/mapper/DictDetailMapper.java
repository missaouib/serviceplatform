package co.yixiang.mp.service.mapper;



import co.yixiang.mp.domain.DictDetail;
import co.yixiang.mp.rest.param.DictDetailQueryParam;
import co.yixiang.mp.rest.vo.DictDetailQueryVo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.util.List;

/**
 * <p>
 * 数据字典详情 Mapper 接口
 * </p>
 *
 * @author visazhou
 * @since 2020-07-13
 */
@Repository
@Mapper
public interface DictDetailMapper extends BaseMapper<DictDetail> {

    /**
     * 根据ID获取查询对象
     * @param id
     * @return
     */
    DictDetailQueryVo getDictDetailById(Serializable id);

    /**
     * 获取分页对象
     * @param page
     * @param dictDetailQueryParam
     * @return
     */
    IPage<DictDetailQueryVo> getDictDetailPageList(@Param("page") Page page, @Param("param") DictDetailQueryParam dictDetailQueryParam);

    @Select("SELECT dd.id, dd.sort, dd.label,dd.value FROM dict d, dict_detail dd WHERE d.id = dd.dict_id AND d.name = #{param.name} order by sort")
    List<DictDetail> getDictDetailList(@Param("param") DictDetailQueryParam param);


    @Select("<script>SELECT d.* from dict_detail d LEFT JOIN dict t on d.dict_id = t.id where 1=1 <if test = \"label !=null\" > and d.label LIKE concat('%', #{label}, '%') </if> <if test = \"dictName != ''||dictName !=null\" > AND t.name = #{dictName} order by d.sort asc</if></script>")
    List<DictDetail> selectDictDetailList(@Param("label") String label, @Param("dictName") String dictName);

    @Select("<script> " +
            "SELECT dd.* FROM dict d, dict_detail dd WHERE d.id = dd.dict_id AND d.name = #{dictName} AND dd.value IN  " +
            "<foreach item='item' index='index' collection='values' open='(' separator=',' close=')'> " +
            "   #{item} " +
            "</foreach>" +
            "</script> "
    )
    List<DictDetail> findDetails(@Param("values") List<String> values, @Param("dictName") String dictName);
}
