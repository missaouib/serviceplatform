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

import co.yixiang.modules.msh.service.dto.*;
import org.springframework.data.domain.Pageable;

import com.alibaba.fastjson.JSONObject;

import co.yixiang.common.service.BaseService;
import co.yixiang.modules.msh.domain.MshDemandList;

/**
* @author cq
* @date 2020-12-25
*/
public interface MshDemandListService  extends BaseService<MshDemandList>{

/**
    * 查询数据分页
    * @param criteria 条件
    * @param pageable 分页参数
    * @return Map<String,Object>
    */
    Map<String,Object> queryAll(MshDemandListQueryCriteria criteria, Pageable pageable);

    /**
    * 查询所有数据不分页
    * @param criteria 条件参数
    * @return List<MshDemandListDto>
    */
    List<MshDemandListDto> queryAll(MshDemandListQueryCriteria criteria);

    /**
    * 导出数据
    * @param all 待导出的数据
    * @param response /
    * @throws IOException /
    */
    void download(List<MshDemandListDto> all, HttpServletResponse response) throws IOException;

    /**
     * 新增需求单相关（提交）
     * @param all 新增需求单相关（提交）
     * @param response /
     * @throws IOException /
     */
    ServiceResult<Boolean> createmshDemandList(JSONObject jsonObject);

    /**
     * 新增需求单相关（保存）
     * @param all 新增需求单相关（保存）
     * @param response /
     * @throws IOException /
     */
    ServiceResult<Boolean> createmshDemandListForSave(JSONObject jsonObject);

    /**
     * 校验需求单
     * @param all 校验需求单
     * @param response /
     * @throws IOException /
     */
    ServiceResult<Integer> checkDeleteById(Integer[] ids);


    /**
     * 新增需求单相关
     * @param all 新增需求单相关
     * @param response /
     * @throws IOException /
     */
    ServiceResult<Boolean> deleteById(Integer[] ids);

    /**
     * 查询需求单详细生成订单用
     * @param id
     * @return
     */
    Map<String,Object>  getMshDemandListdDetails(Integer id);

    MshDemandListAuditDto getMshDemandListAuditInfo(Integer id);

    void updateMshDemandList(MshDemandList resources);

    List<String> getMshDemandAllAuditPerson();

    List<String> getMshDemandAllVip();

    void sendDemandListInfo(String body);

    void addMshDemandList(MshDemandDto mshDemandDto);

    MshDemandList udpateMshDemandListAuditStatus(String id);

    void reportDowload(HttpServletResponse response,MshDemandListQueryCriteria criteria) throws IOException;
}
