/**
* Copyright (C) 2018-2020
* All rights reserved, Designed By www.yixiang.co
* 注意：
* 本软件为www.yixiang.co开发研制，未经购买不得使用
* 购买后可获得全部源代码（禁止转卖、分享、上传到码云、github等开源平台）
* 一经发现盗用、分享等行为，将追究法律责任，后果自负
*/
package co.yixiang.modules.yaolian.service;
import co.yixiang.common.service.BaseService;
import co.yixiang.modules.yaolian.domain.YaolianOrder;
import co.yixiang.modules.yaolian.service.dto.YaolianOrderDto;
import co.yixiang.modules.yaolian.service.dto.YaolianOrderQueryCriteria;
import org.springframework.data.domain.Pageable;
import java.util.Map;
import java.util.List;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;

/**
* @author visa
* @date 2021-03-02
*/
public interface YaolianOrderService  extends BaseService<YaolianOrder>{

/**
    * 查询数据分页
    * @param criteria 条件
    * @param pageable 分页参数
    * @return Map<String,Object>
    */
    Map<String,Object> queryAll(YaolianOrderQueryCriteria criteria, Pageable pageable);

    /**
    * 查询所有数据不分页
    * @param criteria 条件参数
    * @return List<YaolianOrderDto>
    */
    List<YaolianOrder> queryAll(YaolianOrderQueryCriteria criteria);

    /**
    * 导出数据
    * @param all 待导出的数据
    * @param response /
    * @throws IOException /
    */
    void download(List<YaolianOrderDto> all, HttpServletResponse response) throws IOException;
}
