/**
* Copyright (C) 2018-2020
* All rights reserved, Designed By www.yixiang.co
* 注意：
* 本软件为www.yixiang.co开发研制，未经购买不得使用
* 购买后可获得全部源代码（禁止转卖、分享、上传到码云、github等开源平台）
* 一经发现盗用、分享等行为，将追究法律责任，后果自负
*/
package co.yixiang.modules.msh.service.mapper;

import co.yixiang.common.mapper.CoreMapper;
import co.yixiang.modules.msh.domain.MshDemandList;
import co.yixiang.modules.msh.service.dto.MshDemandListDto;
import co.yixiang.modules.msh.service.dto.MshDemandListQueryCriteria;
import com.baomidou.mybatisplus.annotation.SqlParser;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
* @author cq
* @date 2020-12-25
*/
@Repository
@Mapper
public interface MshDemandListMapper extends CoreMapper<MshDemandList> {

    @Select("select max(substring(demand_no,5)) FROM msh_demand_list where substring(demand_no,5,8)= #{date} ")
    Long findMaxId(@Param("date") String date);

    List<MshDemandListDto> selectMshDemandLists(MshDemandListQueryCriteria criteria);

    List<String> getMshDemandAllAuditPerson();

    List<String> getMshDemandAllVip();

    MshDemandList findByDemandNo(@Param("demandNo")String demandNo);

    @SqlParser(filter = true)
    MshDemandList findById(@Param("id")Integer id);

    List<Map<String,Object>> reportList(MshDemandListQueryCriteria criteria);
}
