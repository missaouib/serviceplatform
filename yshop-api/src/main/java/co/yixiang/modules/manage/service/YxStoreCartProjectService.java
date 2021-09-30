package co.yixiang.modules.manage.service;

import co.yixiang.modules.manage.entity.YxStoreCartProject;
import co.yixiang.common.service.BaseService;
import co.yixiang.modules.manage.web.param.YxStoreCartProjectQueryParam;
import co.yixiang.modules.manage.web.vo.YxStoreCartProjectQueryVo;
import co.yixiang.common.web.vo.Paging;
import co.yixiang.modules.yiyaobao.web.vo.StoreCartVo;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 购物车表-项目 服务类
 * </p>
 *
 * @author visa
 * @since 2020-08-24
 */
public interface YxStoreCartProjectService extends BaseService<YxStoreCartProject> {

    /**
     * 根据ID获取查询对象
     * @param id
     * @return
     */
    YxStoreCartProjectQueryVo getYxStoreCartProjectById(Serializable id) throws Exception;

    /**
     * 获取分页对象
     * @param yxStoreCartProjectQueryParam
     * @return
     */
    Paging<YxStoreCartProjectQueryVo> getYxStoreCartProjectPageList(YxStoreCartProjectQueryParam yxStoreCartProjectQueryParam) throws Exception;

    void add4Project(String projectNo,int uid);

    void removeUserCart(int uid, List<String> ids);

    void changeUserCartNum(int cartId,int cartNum,int uid);

    List<StoreCartVo> getUserProductCartList(int uid, String cartIds, int status, String projectCode);

    int getUserCartNum(int uid,String type,int numType,String projectCode);
}
