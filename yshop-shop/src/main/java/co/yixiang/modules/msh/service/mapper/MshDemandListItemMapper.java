/**
* Copyright (C) 2018-2020
* All rights reserved, Designed By www.yixiang.co
* 注意：
* 本软件为www.yixiang.co开发研制，未经购买不得使用
* 购买后可获得全部源代码（禁止转卖、分享、上传到码云、github等开源平台）
* 一经发现盗用、分享等行为，将追究法律责任，后果自负
*/
package co.yixiang.modules.msh.service.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import co.yixiang.common.mapper.CoreMapper;
import co.yixiang.modules.msh.domain.MshDemandListItem;
import co.yixiang.modules.msh.domain.MshOrderItem;
import co.yixiang.modules.msh.domain.MshPatientListFile;
import co.yixiang.modules.msh.domain.MshRepurchaseReminder;
import co.yixiang.modules.msh.service.dto.MshDemandListDto;
import co.yixiang.modules.msh.service.dto.MshDemandListItemDto;
import co.yixiang.modules.msh.service.dto.MshDemandListItemQueryCriteria;
import co.yixiang.modules.msh.service.dto.MshOrderDto;
import co.yixiang.modules.msh.service.dto.MshPatientInformationDto;
import co.yixiang.modules.msh.service.dto.MshPatientInformationQueryCriteria;
import co.yixiang.modules.msh.service.dto.MshRepurchaseReminderQueryCriteria2;

/**
* @author cq
* @date 2020-12-25
*/
@Repository
@Mapper
public interface MshDemandListItemMapper extends CoreMapper<MshDemandListItem> {

	List<MshDemandListItemDto> selectMshDemandListItemList(@Param("data") MshDemandListItemQueryCriteria criteria);

	List<MshDemandListItemDto> selectListByDemandListID(@Param("id") Integer id,@Param("type") Integer type);

	List<MshRepurchaseReminder> selectListByDate(@Param("data") MshRepurchaseReminderQueryCriteria2 criteria);

	List<MshOrderItem> selectListByPhoneAndMedId(@Param("phone") String phone,@Param("medId") Integer medId);

	List<MshDemandListDto> selectMshDemandListList(@Param("data") MshDemandListItemQueryCriteria criteria);

	List<MshOrderDto> selectOrderList(@Param("orderId") Integer orderId);

	List<MshDemandListItemDto> selectOrderItemList(@Param("orderId") Integer orderId);

	List<MshPatientInformationDto> selectMshPatientListList(@Param("data") MshPatientInformationQueryCriteria criteria);

	List<MshPatientListFile> selectMshPatientFileList(@Param("patientId") Integer patientId);

	Integer getCountByMshDemandListId(@Param("demandListId") Integer demandListId);
}
