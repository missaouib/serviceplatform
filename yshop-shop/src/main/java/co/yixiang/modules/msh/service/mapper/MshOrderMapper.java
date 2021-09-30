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
import co.yixiang.modules.msh.domain.MshOrder;
import co.yixiang.modules.msh.service.dto.MshDemandListItemDto;
import co.yixiang.modules.msh.service.dto.MshOrderDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
* @author cq
* @date 2020-12-25
*/
@Repository
@Mapper
public interface MshOrderMapper extends CoreMapper<MshOrder> {

    List<MshOrderDto> getMshOrderByDemandListId(@Param("demandListId") Integer demandListId);

    List<MshDemandListItemDto> selectOrderItemList(@Param("orderId") Integer orderId);

    MshOrderDto getByExternalOrderId(@Param("phaOrderNo") String phaOrderNo);

    Integer getCountByMshDemandListId(@Param("demandListId")Integer demandListId);

    List<MshOrder> getAllTenDayNotAnswer();
}
