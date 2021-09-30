package co.yixiang.modules.user.service;

import co.yixiang.modules.user.entity.InnerEmployee;
import co.yixiang.common.service.BaseService;
import co.yixiang.modules.user.web.param.InnerEmployeeQueryParam;
import co.yixiang.modules.user.web.vo.InnerEmployeeQueryVo;
import co.yixiang.common.web.vo.Paging;

import java.io.Serializable;

/**
 * <p>
 * 内部员工表 服务类
 * </p>
 *
 * @author visazhou
 * @since 2020-05-20
 */
public interface InnerEmployeeService extends BaseService<InnerEmployee> {

    /**
     * 根据ID获取查询对象
     * @param id
     * @return
     */
    InnerEmployeeQueryVo getInnerEmployeeById(Serializable id) throws Exception;

    /**
     * 获取分页对象
     * @param innerEmployeeQueryParam
     * @return
     */
    Paging<InnerEmployeeQueryVo> getInnerEmployeePageList(InnerEmployeeQueryParam innerEmployeeQueryParam) throws Exception;

}
