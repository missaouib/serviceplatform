/**
* Copyright (C) 2018-2020
* All rights reserved, Designed By www.yixiang.co
* 注意：
* 本软件为www.yixiang.co开发研制，未经购买不得使用
* 购买后可获得全部源代码（禁止转卖、分享、上传到码云、github等开源平台）
* 一经发现盗用、分享等行为，将追究法律责任，后果自负
*/
package co.yixiang.modules.taibao.service;

import co.yixiang.common.service.BaseService;
import co.yixiang.modules.taibao.domain.*;
import co.yixiang.modules.taibao.service.dto.TbClaimInfoDto;
import co.yixiang.modules.taibao.service.dto.TbClaimInfoQueryCriteria;
import co.yixiang.modules.taibao.service.vo.ClaimEventPage;
import org.springframework.data.domain.Pageable;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
* @author zhoujinlai
* @date 2021-04-30
*/
public interface TbClaimInfoService  extends BaseService<TbClaimInfo> {

/**
    * 查询数据分页
    * @param criteria 条件
    * @param pageable 分页参数
    * @return Map<String,Object>
    */
    Map<String,Object> queryAll(TbClaimInfoQueryCriteria criteria, Pageable pageable);

    /**
    * 查询所有数据不分页
    * @param criteria 条件参数
    * @return List<TbClaimInfoDto>
    */
    List<TbClaimInfo> queryAll(TbClaimInfoQueryCriteria criteria);

    /**
    * 导出数据
    * @param all 待导出的数据
    * @param response /
    * @throws IOException /
    */
    void download(List<TbClaimInfoDto> all, HttpServletResponse response) throws IOException;


    /**
     * 添加一对多
     *
     */
    public void saveMain(TbClaimInfo claimInfo, TbOrderProjectParam orderProjectParam, List<TbNotificationPerson> notificationPersonList, List<TbInsurancePerson> insurancePersonList, List<TbClaimThirdInsurance> claimThirdInsuranceList, List<TbClaimThirdPay> claimThirdPayList, List<TbClaimMaterial> claimMaterialList, List<TbClaimOther> claimOtherList, List<TbClaimInvest> claimInvestList, List<TbClaimClmestimate> claimClmestimateList, List<TbClaimConsult> claimConsultList, List<TbClaimBenefitPerson> claimBenefitPersonList, List<TbClaimClaimPay> claimClaimPayList, List<TbClaimAddMaterial> claimAddMaterialList, List<TbClaimAuditInfo> claimAuditInfoList, List<TbClaimAuditpolicy> claimAuditpolicyList, List<TbClaimAbove> claimAboveList, List<TbClaimAccInfo> claimAccInfoList, List<ClaimEventPage> claimEventList) ;

    /**
     * 修改一对多
     *
     */
    public void updateMain(TbClaimInfo claimInfo, TbOrderProjectParam orderProjectParam, List<TbNotificationPerson> notificationPersonList, List<TbInsurancePerson> insurancePersonList, List<TbClaimThirdInsurance> claimThirdInsuranceList, List<TbClaimThirdPay> claimThirdPayList, List<TbClaimMaterial> claimMaterialList, List<TbClaimOther> claimOtherList, List<TbClaimInvest> claimInvestList, List<TbClaimClmestimate> claimClmestimateList, List<TbClaimConsult> claimConsultList, List<TbClaimBenefitPerson> claimBenefitPersonList, List<TbClaimClaimPay> claimClaimPayList, List<TbClaimAddMaterial> claimAddMaterialList, List<TbClaimAuditInfo> claimAuditInfoList, List<TbClaimAuditpolicy> claimAuditpolicyList, List<TbClaimAbove> claimAboveList, List<TbClaimAccInfo> claimAccInfoList, List<ClaimEventPage> claimEventList);

    /**
     * 删除一对多
     */
    public void delMain(String id);

    /**
     * 批量删除一对多
     */
    public void delBatchMain(Collection<? extends Serializable> idList);

    TbClaimInfo getByClaimno(String claimno);

    void orderFilish(String id);

    void saveTbOrder(TbOrderProjectParam orderProjectParam);
}
