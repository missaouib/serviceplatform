/**
* Copyright (C) 2018-2020
* All rights reserved, Designed By www.yixiang.co
* 注意：
* 本软件为www.yixiang.co开发研制，未经购买不得使用
* 购买后可获得全部源代码（禁止转卖、分享、上传到码云、github等开源平台）
* 一经发现盗用、分享等行为，将追究法律责任，后果自负
*/
package co.yixiang.modules.shop.service;
import co.yixiang.common.service.BaseService;
import co.yixiang.modules.api.common.Result;
import co.yixiang.modules.shop.domain.MdCountry;
import co.yixiang.modules.shop.service.dto.CascadeDto;
import co.yixiang.modules.shop.service.dto.MdCountryDto;
import co.yixiang.modules.shop.service.dto.MdCountryQueryCriteria;
import org.springframework.data.domain.Pageable;
import java.util.Map;
import java.util.List;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;

/**
* @author visa
* @date 2020-10-16
*/
public interface MdCountryService  extends BaseService<MdCountry>{

/**
    * 查询数据分页
    * @param criteria 条件
    * @param pageable 分页参数
    * @return Map<String,Object>
    */
    Map<String,Object> queryAll(MdCountryQueryCriteria criteria, Pageable pageable);

    /**
    * 查询所有数据不分页
    * @param criteria 条件参数
    * @return List<MdCountryDto>
    */
    List<MdCountry> queryAll(MdCountryQueryCriteria criteria);

    /**
    * 导出数据
    * @param all 待导出的数据
    * @param response /
    * @throws IOException /
    */
    void download(List<MdCountryDto> all, HttpServletResponse response) throws IOException;

    List<CascadeDto> queryAllCascade(MdCountryQueryCriteria criteria, Pageable pageable);

    List<CascadeDto> queryAllTree(MdCountryQueryCriteria criteria, Pageable pageable);

    Result<?> queryMdCountry(String parentCode);
}
