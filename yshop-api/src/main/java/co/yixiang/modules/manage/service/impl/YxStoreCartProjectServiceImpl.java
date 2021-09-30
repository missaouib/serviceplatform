package co.yixiang.modules.manage.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import co.yixiang.common.utils.QueryHelpPlus;
import co.yixiang.exception.ErrorRequestException;
import co.yixiang.modules.activity.mapper.YxStoreBargainMapper;
import co.yixiang.modules.activity.mapper.YxStoreCombinationMapper;
import co.yixiang.modules.activity.mapper.YxStoreSeckillMapper;
import co.yixiang.modules.activity.service.YxStoreBargainService;
import co.yixiang.modules.activity.service.YxStoreCombinationService;
import co.yixiang.modules.activity.service.YxStoreSeckillService;
import co.yixiang.modules.manage.entity.YxStoreCartProject;
import co.yixiang.modules.manage.mapper.YxStoreCartProjectMapper;
import co.yixiang.modules.manage.mapping.CartProjectMap;
import co.yixiang.modules.manage.service.YxStoreCartProjectService;
import co.yixiang.modules.manage.web.param.YxStoreCartProjectQueryParam;
import co.yixiang.modules.manage.web.vo.YxStoreCartProjectQueryVo;
import co.yixiang.common.service.impl.BaseServiceImpl;
import co.yixiang.common.web.vo.Paging;
import co.yixiang.modules.shop.entity.Product4project;
import co.yixiang.modules.shop.entity.YxStoreCart;
import co.yixiang.modules.shop.entity.YxStoreProductAttrValue;
import co.yixiang.modules.shop.mapping.CartMap;
import co.yixiang.modules.shop.service.Product4projectService;
import co.yixiang.modules.shop.service.YxStoreProductAttrService;
import co.yixiang.modules.shop.service.YxStoreProductService;
import co.yixiang.modules.shop.web.vo.YxStoreCartQueryVo;
import co.yixiang.modules.shop.web.vo.YxStoreProductQueryVo;
import co.yixiang.modules.user.service.YxUserService;
import co.yixiang.modules.yiyaobao.web.vo.StoreCartVo;
import co.yixiang.utils.OrderUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import java.io.Serializable;
import java.util.*;


/**
 * <p>
 * 购物车表-项目 服务实现类
 * </p>
 *
 * @author visa
 * @since 2020-08-24
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class YxStoreCartProjectServiceImpl extends BaseServiceImpl<YxStoreCartProjectMapper, YxStoreCartProject> implements YxStoreCartProjectService {

    @Autowired
    private YxStoreCartProjectMapper yxStoreCartProjectMapper;

    @Autowired
    private Product4projectService product4projectService;

    @Autowired
    private YxStoreProductService productService;


    @Autowired
    private YxStoreSeckillMapper storeSeckillMapper;
    @Autowired
    private YxStoreBargainMapper yxStoreBargainMapper;
    @Autowired
    private YxStoreCombinationMapper storeCombinationMapper;

    @Autowired
    private CartProjectMap cartMap;


    @Autowired
    private YxUserService userService;

    @Autowired
    private YxStoreProductAttrService productAttrService;

    @Override
    public YxStoreCartProjectQueryVo getYxStoreCartProjectById(Serializable id) throws Exception{
        return yxStoreCartProjectMapper.getYxStoreCartProjectById(id);
    }

    @Override
    public Paging<YxStoreCartProjectQueryVo> getYxStoreCartProjectPageList(YxStoreCartProjectQueryParam yxStoreCartProjectQueryParam) throws Exception{
        Page page = setPageParam(yxStoreCartProjectQueryParam,OrderItem.desc("create_time"));
        QueryWrapper queryWrapper = QueryHelpPlus.getPredicate(YxStoreCartProjectQueryParam.class, yxStoreCartProjectQueryParam);
        IPage<YxStoreCartProject> iPage = yxStoreCartProjectMapper.selectPage(page,queryWrapper);
        return new Paging(iPage);
    }


    @Override
    public void add4Project(String projectNo,int uid) {


        QueryWrapper queryWrapper1 = new QueryWrapper();
        queryWrapper1.eq("uid",uid);
        queryWrapper1.eq("project_code",projectNo);
        yxStoreCartProjectMapper.delete(queryWrapper1);


        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("project_no",projectNo);
        List<Product4project> product4projectList = product4projectService.list(queryWrapper);

        for(Product4project product4project : product4projectList) {
            Integer cartNum = product4project.getNum();
            Integer productId = product4project.getProductId();
            String uniqueId = product4project.getProductUniqueId();
            int isNew = 0;
            Integer storeId = product4project.getStoreId();
            //拼团
            int combinationId = 0;
            //秒杀
            int seckillId = 0;
            // 砍价
            int bargainId = 0;

            String departmentId = "";
            String partnerId = projectNo;
            String doctorId = "";

            this.addCart4Project(uid,productId,cartNum,uniqueId
                    ,"product",isNew,combinationId,seckillId,bargainId,storeId,departmentId,partnerId,doctorId,projectNo);
        }
    }



    public int addCart4Project(int uid, int productId, int cartNum, String productAttrUnique,
                               String type, int isNew, int combinationId, int seckillId, int bargainId,Integer storeId,String departmentCode,String partnerCode,String refereeCode,String projectNo) {


        int stock = productService.getProductStock(productId,productAttrUnique);
        if(stock < cartNum){
            throw new ErrorRequestException("该产品库存不足"+cartNum);
        }


        YxStoreCartProject storeCart = new YxStoreCartProject();

        storeCart.setBargainId(bargainId);
        storeCart.setCartNum(cartNum);
        storeCart.setCombinationId(combinationId);
        storeCart.setProductAttrUnique(productAttrUnique);
        storeCart.setProductId(productId);
        storeCart.setSeckillId(seckillId);
        storeCart.setType(type);
        storeCart.setUid(uid);
        storeCart.setStoreId(storeId);
        storeCart.setDepartCode(departmentCode);
        storeCart.setProjectCode(projectNo);
        storeCart.setPartnerCode(partnerCode);
        storeCart.setRefereeCode(refereeCode);
        storeCart.setAddTime(OrderUtil.getSecondTimestampTwo());

        storeCart.setCartNum(cartNum );

        yxStoreCartProjectMapper.insert(storeCart);

        return storeCart.getId().intValue();
    }


    /**
     * 删除购物车
     * @param uid
     * @param ids
     */
    @Override
    public void removeUserCart(int uid, List<String> ids) {
        QueryWrapper<YxStoreCartProject> wrapper = new QueryWrapper<>();
        wrapper.eq("uid",uid).in("id",ids);

        YxStoreCartProject storeCart = new YxStoreCartProject();
        storeCart.setIsDel(1);

        yxStoreCartProjectMapper.update(storeCart,wrapper);
    }

    /**
     * 改购物车数量
     * @param cartId
     * @param cartNum
     * @param uid
     */
    @Override
    public void changeUserCartNum(int cartId, int cartNum, int uid) {
        QueryWrapper<YxStoreCartProject> wrapper = new QueryWrapper<>();
        wrapper.eq("uid",uid).eq("id",cartId);

        YxStoreCartProject cart = getOne(wrapper);
        if(ObjectUtil.isNull(cart)){
            throw new ErrorRequestException("购物车不存在");
        }

        if(cartNum <= 0){
            throw new ErrorRequestException("库存错误");
        }

        //todo 普通商品库存
        int stock = productService.getProductStock(cart.getProductId()
                ,cart.getProductAttrUnique());
        if(stock < cartNum){
            throw new ErrorRequestException("该产品库存不足"+cartNum);
        }

        if(cartNum == cart.getCartNum()) return;

        YxStoreCartProject storeCart = new YxStoreCartProject();
        storeCart.setCartNum(cartNum);
        storeCart.setId(Long.valueOf(cartId));

        yxStoreCartProjectMapper.updateById(storeCart);


    }

    /**
     * 购物车列表
     * @param uid 用户id
     * @param cartIds 购物车id，多个逗号隔开
     * @param status 0-购购物车列表
     * @return
     */
    @Override
    public List<StoreCartVo> getUserProductCartList(int uid, String cartIds, int status,String projectCode) {
        QueryWrapper<YxStoreCartProject> wrapper = new QueryWrapper<>();
        wrapper.eq("uid",uid).eq("type","product").eq("is_pay",0)
                .eq("is_del",0).eq("project_code",projectCode)
                .orderByDesc("add_time");
        if(status == 0) wrapper.eq("is_new",0);
        if(StrUtil.isNotEmpty(cartIds)) wrapper.in("id", Arrays.asList(cartIds.split(",")));
        List<YxStoreCartProject> carts = yxStoreCartProjectMapper.selectList(wrapper);

        List<YxStoreCartProjectQueryVo> valid = new ArrayList<>();
        List<YxStoreCartProjectQueryVo> invalid = new ArrayList<>();

        for (YxStoreCartProject storeCart : carts) {
            YxStoreProductQueryVo storeProduct = null;
            if(storeCart.getCombinationId() > 0){
                storeProduct = ObjectUtil.clone(storeCombinationMapper.combinatiionInfo(storeCart.getCombinationId()));
            }else if(storeCart.getSeckillId() > 0){
                storeProduct = ObjectUtil.clone(storeSeckillMapper.seckillInfo(storeCart.getSeckillId()));
            }else if(storeCart.getBargainId() > 0){
                storeProduct = ObjectUtil.clone(yxStoreBargainMapper.bargainInfo(storeCart.getBargainId()));
            }else{
                //必须得重新克隆创建一个新对象
                storeProduct = ObjectUtil.clone(productService
                        .getNewStoreProductById(storeCart.getProductId()));
            }

            YxStoreCartProjectQueryVo storeCartQueryVo = cartMap.toDto(storeCart);

            if(ObjectUtil.isNull(storeProduct)){
                YxStoreCartProject yxStoreCart = new YxStoreCartProject();
                yxStoreCart.setIsDel(1);
                yxStoreCartProjectMapper.update(yxStoreCart,
                        new QueryWrapper<YxStoreCartProject>()
                                .lambda().eq(YxStoreCartProject::getId,storeCart.getId()));
            }else if( storeProduct.getIsDel() == 1 || storeProduct.getStock() == 0){
                storeCartQueryVo.setProductInfo(storeProduct);
                invalid.add(storeCartQueryVo);
            }else{
                if(StrUtil.isNotEmpty(storeCart.getProductAttrUnique())){
                    YxStoreProductAttrValue productAttrValue = productAttrService
                            .uniqueByAttrInfo(storeCart.getProductAttrUnique());
                    if(ObjectUtil.isNull(productAttrValue) || productAttrValue.getStock() == 0){
                        storeCartQueryVo.setProductInfo(storeProduct);
                        invalid.add(storeCartQueryVo);
                    }else{
                        storeProduct.setAttrInfo(productAttrValue);
                        storeCartQueryVo.setProductInfo(storeProduct);

                        //设置真实价格
                        //设置VIP价格
                        double vipPrice = 0d;
                        if(storeCart.getCombinationId() > 0 || storeCart.getSeckillId() > 0
                                || storeCart.getBargainId() > 0){
                            vipPrice = productAttrValue.getPrice().doubleValue();
                        }else{
                            vipPrice = userService.setLevelPrice(
                                    productAttrValue.getPrice().doubleValue(),uid);
                        }
                        storeCartQueryVo.setTruePrice(vipPrice);
                        //设置会员价
                        storeCartQueryVo.setVipTruePrice(productAttrValue.getPrice()
                                .doubleValue());
                        storeCartQueryVo.setCostPrice(productAttrValue.getCost()
                                .doubleValue());
                        storeCartQueryVo.setTrueStock(productAttrValue.getStock());

                        valid.add(storeCartQueryVo);

                    }
                }else{
                    //设置VIP价格
                    //设置VIP价格
                    double vipPrice = 0d;
                    if(storeCart.getCombinationId() > 0 || storeCart.getSeckillId() > 0
                            || storeCart.getBargainId() > 0){
                        vipPrice = storeProduct.getPrice().doubleValue();
                    }else{
                        vipPrice = userService.setLevelPrice(
                                storeProduct.getPrice().doubleValue(),uid);
                    }

                    storeCartQueryVo.setTruePrice(vipPrice);
                    //todo 设置会员价
                    storeCartQueryVo.setVipTruePrice(0d);
                    storeCartQueryVo.setCostPrice(storeProduct.getCost()
                            .doubleValue());
                    storeCartQueryVo.setTrueStock(storeProduct.getStock());
                    storeCartQueryVo.setProductInfo(storeProduct);

                    valid.add(storeCartQueryVo);
                }
            }

        }

        Map<String,Object> map = new LinkedHashMap<>();
        map.put("valid",valid);
        map.put("invalid",invalid);


        List<StoreCartVo> result = new ArrayList<>();

        StoreCartVo storeCartVo = new StoreCartVo();
        storeCartVo.setInfo(map);
        storeCartVo.setStoreId(0);
        storeCartVo.setStoreName("");
        result.add( storeCartVo);

        return result;
    }


    @Override
    public int getUserCartNum(int uid, String type, int numType,String projectCode) {
        int num = 0;
        QueryWrapper<YxStoreCartProject> wrapper = new QueryWrapper<>();
        wrapper.eq("uid",uid).eq("type",type).eq("is_pay",0).eq("is_del",0).eq("is_new",0);
        if(numType > 0){
            num = yxStoreCartProjectMapper.selectCount(wrapper);
        }else{
            num = yxStoreCartProjectMapper.cartSum(uid,type,projectCode);
        }
        return num;
    }
}
