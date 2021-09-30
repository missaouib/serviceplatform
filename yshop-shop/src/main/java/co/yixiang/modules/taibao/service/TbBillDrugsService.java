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
import co.yixiang.modules.taibao.domain.TbBillDrugs;
import co.yixiang.modules.taibao.service.dto.TbBillDrugsDto;
import co.yixiang.modules.taibao.service.dto.TbBillDrugsQueryCriteria;
import org.springframework.data.domain.Pageable;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
* @author zhoujinlai
* @date 2021-04-30
*/
public interface TbBillDrugsService  extends BaseService<TbBillDrugs> {

/**
    * 查询数据分页
    * @param criteria 条件
    * @param pageable 分页参数
    * @return Map<String,Object>
    */
    Map<String,Object> queryAll(TbBillDrugsQueryCriteria criteria, Pageable pageable);

    /**
    * 查询所有数据不分页
    * @param criteria 条件参数
    * @return List<TbBillDrugsDto>
    */
    List<TbBillDrugs> queryAll(TbBillDrugsQueryCriteria criteria);

    /**
    * 导出数据
    * @param all 待导出的数据
    * @param response /
    * @throws IOException /
    */
    void download(List<TbBillDrugsDto> all, HttpServletResponse response) throws IOException;

    /**
     * 根据收据信息ID查询所有药品
     * @param mainId
     * @return
     */
    public List<TbBillDrugs> selectByMainId(String mainId);

}
