/**
 * Copyright (C) 2018-2019
 * All rights reserved, Designed By www.yixiang.co
 * 注意：
 * 本软件为www.yixiang.co开发研制，未经购买不得使用
 * 购买后可获得全部源代码（禁止转卖、分享、上传到码云、github等开源平台）
 * 一经发现盗用、分享等行为，将追究法律责任，后果自负
 */
package co.yixiang.modules.shop.service;

import co.yixiang.common.service.BaseService;
import co.yixiang.modules.shop.entity.YxStoreCart;
import co.yixiang.modules.shop.entity.YxSystemStore;
import co.yixiang.modules.shop.web.vo.YxStoreCartQueryVo;
import co.yixiang.modules.yiyaobao.web.vo.StoreCartVo;
import org.apache.ibatis.annotations.Param;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 购物车表 服务类
 * </p>
 *
 * @author hupeng
 * @since 2019-10-25
 */
public interface YxStoreCartService extends BaseService<YxStoreCart> {

    void removeUserCart(int uid, List<String> ids);

    void changeUserCartNum(int cartId,int cartNum,int uid);

    void changeUserCartNum4Store(int cartId,int cartNum,int uid);

    Map<String,Object> getUserProductCartList(int uid,String cartIds,int status,String projectCode);

    List<StoreCartVo> getUserProductCartList4Store(int uid, String cartIds, int status,String projectCode,String cardNmuber,String cardType,Integer demandId );

    int getUserCartNum(int uid,String type,int numType,List<String> projectCodes);

    int addCart(int uid,int productId,int cartNum, String productAttrUnique,
                String type,int isNew,int combinationId,int seckillId,int bargainId,String departmentCode,String partnerCode,String refereeCode,String projectNo);

    /**
     * 根据ID获取查询对象
     * @param id
     * @return
     */
    YxStoreCartQueryVo getYxStoreCartById(Serializable id);

    List<YxSystemStore> getStoreInfo( int uid, String type,Integer is_new,  List<String> cartIds,List<String> projectCodes);


    void add4Project(String projectNo,int uid);

    Boolean deleteCartByUidProductid(Integer uid,Integer productid,String productUnique);
}
