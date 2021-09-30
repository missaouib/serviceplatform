/**
* Copyright (C) 2018-2020
* All rights reserved, Designed By www.yixiang.co
* 注意：
* 本软件为www.yixiang.co开发研制，未经购买不得使用
* 购买后可获得全部源代码（禁止转卖、分享、上传到码云、github等开源平台）
* 一经发现盗用、分享等行为，将追究法律责任，后果自负
*/
package co.yixiang.modules.shop.service.mapper;

import co.yixiang.common.mapper.CoreMapper;
import co.yixiang.modules.shop.domain.YxStoreDisease;
import co.yixiang.modules.shop.service.dto.YxStoreDiseaseExportDto;
import co.yixiang.modules.shop.service.dto.YxStoreDiseaseQueryCriteria;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
* @author visa
* @date 2020-06-02
*/
@Repository
@Mapper
public interface YxStoreDiseaseMapper extends CoreMapper<YxStoreDisease> {

    @Select("<script> " +
            "SELECT GROUP_CONCAT(DISTINCT ysd.pid) AS parentIds FROM yx_store_disease ysd WHERE ysd.id IN  " +
            "<foreach item='item' index='index' collection='ids' open='(' separator=',' close=')'> " +
            "   #{item} " +
            "</foreach>" +
            "</script> "
    )
    String findParentIds( @Param("ids") List<Integer> ids);


    @Select("SELECT ysd.cate_name AS secondCateName,(SELECT ysd1.cate_name FROM yx_store_disease ysd1 WHERE ysd1.id = ysd.pid AND ysd1.project_code = #{criteria.projectCode}) AS firstCateName,ysd.cate_type AS cateType, case when ysd.is_show = 1 then '显示' else '隐藏' end  as showType  FROM yx_store_disease ysd WHERE ysd.pid != 0 AND ysd.project_code = #{criteria.projectCode} ORDER BY firstCateName")
    List<YxStoreDiseaseExportDto> downloadSimple(@Param("criteria") YxStoreDiseaseQueryCriteria criteria);

    @Select("<script>  SELECT count(1) from yx_store_disease where project_code=#{projectCode} and pid!=0 and cate_name =#{cateName} and is_del=0 " +
            " <if test = \"id !=null\"> and id!=#{id} </if> </script> ")
    Integer selectByYxStoreDisease(YxStoreDisease resources);
}
