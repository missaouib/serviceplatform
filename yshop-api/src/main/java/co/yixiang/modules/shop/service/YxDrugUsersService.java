package co.yixiang.modules.shop.service;

import co.yixiang.modules.shop.entity.YxDrugUsers;
import co.yixiang.common.service.BaseService;
import co.yixiang.modules.shop.web.param.YxDrugUsersQueryParam;
import co.yixiang.modules.shop.web.vo.YxDrugUsersQueryVo;
import co.yixiang.common.web.vo.Paging;

import java.io.Serializable;

/**
 * <p>
 * 用药人列表 服务类
 * </p>
 *
 * @author visa
 * @since 2020-12-20
 */
public interface YxDrugUsersService extends BaseService<YxDrugUsers> {

    /**
     * 根据ID获取查询对象
     * @param id
     * @return
     */
    YxDrugUsersQueryVo getYxDrugUsersById(Serializable id) throws Exception;

    /**
     * 获取分页对象
     * @param yxDrugUsersQueryParam
     * @return
     */
    Paging<YxDrugUsers> getYxDrugUsersPageList(YxDrugUsersQueryParam yxDrugUsersQueryParam) throws Exception;

    YxDrugUsers saveDrugUsers(YxDrugUsers yxDrugUsers);

    YxDrugUsers getUserDefaultDrugUser(int uid);

    YxDrugUsers getDrugUserByInfo(int uid,String drugUserName,String drugUserPhone);
}
