/**
* Copyright (C) 2018-2020
* All rights reserved, Designed By www.yixiang.co
* 注意：
* 本软件为www.yixiang.co开发研制，未经购买不得使用
* 购买后可获得全部源代码（禁止转卖、分享、上传到码云、github等开源平台）
* 一经发现盗用、分享等行为，将追究法律责任，后果自负
*/
package co.yixiang.modules.msh.service;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.data.domain.Pageable;

import co.yixiang.common.service.BaseService;
import co.yixiang.modules.msh.domain.MshPatientInformation;
import co.yixiang.modules.msh.service.dto.MshPatientInformationDto;
import co.yixiang.modules.msh.service.dto.MshPatientInformationQueryCriteria;

/**
* @author cq
* @date 2020-12-18
*/
public interface MshPatientInformationService  extends BaseService<MshPatientInformation>{

/**
    * 查询数据分页
    * @param criteria 条件
    * @param pageable 分页参数
    * @return Map<String,Object>
    */
    Map<String,Object> queryAll(MshPatientInformationQueryCriteria criteria, Pageable pageable);

    /**
    * 查询所有数据不分页
    * @param criteria 条件参数
    * @return List<MshPatientInformationDto>
    */
    List<MshPatientInformation> queryAll(MshPatientInformationQueryCriteria criteria);

    /**
    * 导出数据
    * @param all 待导出的数据
    * @param response /
    * @throws IOException /
    */
    void download(List<MshPatientInformationDto> all, HttpServletResponse response) throws IOException;

    /**
     * 根据手机号查询患者信息
     * @param criteria 条件参数
     * @return List<MshPatientInformationDto>
     */
    List<MshPatientInformationDto> selectMshPatientInformationListByPhone(String phone);

    /**
     * 查询数据分页
     * @param criteria 条件
     * @param pageable 分页参数
     * @return Map<String,Object>
     */
    Map<String,Object> selectMshPatientListList(MshPatientInformationQueryCriteria criteria, Pageable pageable);
}
