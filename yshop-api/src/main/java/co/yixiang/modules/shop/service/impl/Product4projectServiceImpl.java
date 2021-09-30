package co.yixiang.modules.shop.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import co.yixiang.constant.ShopConstants;
import co.yixiang.enums.ProjectNameEnum;
import co.yixiang.modules.shop.entity.*;
import co.yixiang.modules.shop.mapper.Product4projectMapper;
import co.yixiang.modules.shop.service.*;
import co.yixiang.modules.shop.web.dto.*;
import co.yixiang.modules.shop.web.param.Product4projectQueryParam;
import co.yixiang.modules.shop.web.vo.Product4projectQueryVo;
import co.yixiang.common.service.impl.BaseServiceImpl;
import co.yixiang.common.web.vo.Paging;
import co.yixiang.mp.domain.DictDetail;
import co.yixiang.mp.rest.param.DictDetailQueryParam;
import co.yixiang.mp.service.DictDetailService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>
 * 项目对应的药品 服务实现类
 * </p>
 *
 * @author visazhou
 * @since 2020-06-11
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class Product4projectServiceImpl extends BaseServiceImpl<Product4projectMapper, Product4project> implements Product4projectService {

    @Autowired
    private Product4projectMapper product4projectMapper;

    @Autowired
    private YxStoreProductService yxStoreProductService;

    @Autowired
    private ArticleService articleService;

    @Autowired
    private YxStoreProductAttrValueService yxStoreProductAttrValueService;

    @Autowired
    private DictDetailService dictDetailService;

    @Autowired
    private ProjectService projectService;
    @Override
    public Product4projectQueryVo getProduct4projectById(Serializable id) throws Exception{
        return product4projectMapper.getProduct4projectById(id);
    }

    @Override
    public Paging<Product4projectQueryVo> getProduct4projectPageList(Product4projectQueryParam product4projectQueryParam) throws Exception{
        Page page = setPageParam(product4projectQueryParam,OrderItem.desc("create_time"));
        IPage<Product4projectQueryVo> iPage = product4projectMapper.getProduct4projectPageList(page,product4projectQueryParam);
        return new Paging(iPage);
    }

    @Override
    public List<SpecialProjectDTO> querySpecialProject() {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("project_no", ProjectNameEnum.ROCHE_SMA.getValue());
        queryWrapper.eq("is_del",0);
        queryWrapper.eq("is_show",1);
        SpecialProjectDTO specialProjectDTO = new SpecialProjectDTO();
        List<SpecialProjectDTO> result = new ArrayList<>();

        List<SpecialProductDTO> productList = new ArrayList<>();
        List<Product4project> projectList =  this.list(queryWrapper);
        Project project = projectService.getOne(new LambdaQueryWrapper<Project>().eq(Project::getProjectCode,ProjectNameEnum.ROCHE_SMA.getValue()),false);
        // 获取药品
        if(CollUtil.isNotEmpty(projectList)) {
            for(Product4project product4project : projectList) {
                QueryWrapper queryWrapper1 = new QueryWrapper();
                queryWrapper1.eq("id",product4project.getProductId());
                queryWrapper1.select("id","image","store_name");
                YxStoreProduct yxStoreProduct = yxStoreProductService.getOne(queryWrapper1);
                if(ObjectUtil.isNotNull(yxStoreProduct)) {
                    SpecialProductDTO specialProductDTO = new SpecialProductDTO();
                    specialProductDTO.setImagePath(yxStoreProduct.getImage());
                    specialProductDTO.setProductId(yxStoreProduct.getId());
                    specialProductDTO.setProductName(yxStoreProduct.getStoreName());
                    specialProductDTO.setProductUniqueId(product4project.getProductUniqueId());
                    productList.add(specialProductDTO);
                }
            }
        }

        // 获取文章
        specialProjectDTO.setArticleList( articleService.list(new QueryWrapper<YxArticle>().eq("project_code",project.getProjectCode()).eq("type",1)));
        specialProjectDTO.setProductList(productList);
        specialProjectDTO.setProjectCode(project.getProjectCode());
        specialProjectDTO.setProjectName(project.getProjectDesc());
        specialProjectDTO.setRemark(project.getRemark());

       /* QueryWrapper queryWrapper2 = new QueryWrapper();
        queryWrapper2.eq("name", ShopConstants.STORENAME_SHANGHAI_CLOUD);

        YxSystemStore yxSystemStore = yxSystemStoreService.getOne(queryWrapper2,false);
        if(yxSystemStore != null) {
            QueryWrapper queryWrapper1 = new QueryWrapper();
            queryWrapper1.eq("FOREIGN_ID",yxSystemStore.getId());
            List<MdPharmacistService> pharmacists = mdPharmacistServiceService.list(queryWrapper1);
            specialProjectDTO.setPharmacists(pharmacists);
        }*/


       /* DictDetailQueryParam dictDetailQueryParam = new DictDetailQueryParam();
        dictDetailQueryParam.setName("serviceGroupId");
        String label = ProjectNameEnum.ROCHE_SMA.getValue();

        dictDetailQueryParam.setLabel(label);
        List<DictDetail> dictDetailList = dictDetailService.queryAll(dictDetailQueryParam);
        if(CollUtil.isNotEmpty(dictDetailList)) {
            specialProjectDTO.setServiceGroupId(dictDetailList.get(0).getValue());
        }*/



        specialProjectDTO.setPhone(project.getPhone());
        specialProjectDTO.setServiceGroupId(project.getServiceGroupId());
        specialProjectDTO.setPharmacistTips("扫码添加“艾满欣服务药师”\n开展线上咨询");
        result.add(specialProjectDTO);

        return result;
    }

    @Override
    public Data4ProjectDTO queryData(Product4projectQueryParam product4projectQueryParam) {

        // 获取药房列表
        List<Store4ProjectDTO> store4ProjectDTOList =  baseMapper.queryStore(product4projectQueryParam.getProjectCode());

        for(Store4ProjectDTO store4ProjectDTO : store4ProjectDTOList) {
            // 获取药品列表
            LambdaQueryWrapper<Product4project> lambdaQueryWrapper = new LambdaQueryWrapper();
            lambdaQueryWrapper.eq(Product4project::getProjectNo,product4projectQueryParam.getProjectCode());
            lambdaQueryWrapper.eq(Product4project::getStoreId,store4ProjectDTO.getId());
            lambdaQueryWrapper.eq(Product4project::getIsDel,0);
            lambdaQueryWrapper.eq(Product4project::getIsShow,1);
            lambdaQueryWrapper.eq(Product4project::getGroupName,"");
            List<Product4project> Product4projectList = list(lambdaQueryWrapper);
            List<Product4ProjectDTO> product4ProjectDTOList = new ArrayList<>();
            for(Product4project product4project : Product4projectList) {
                // 获取药品字段
                LambdaQueryWrapper<YxStoreProduct> lambdaQueryWrapperProduct = new  LambdaQueryWrapper();
                lambdaQueryWrapperProduct.eq(YxStoreProduct::getId,product4project.getProductId());
                lambdaQueryWrapperProduct.select(YxStoreProduct::getStoreName,YxStoreProduct::getImage, YxStoreProduct::getCommonName, YxStoreProduct::getSpec,YxStoreProduct::getManufacturer, YxStoreProduct::getUnit,YxStoreProduct::getId);
                YxStoreProduct yxStoreProduct = yxStoreProductService.getOne(lambdaQueryWrapperProduct);

                Product4ProjectDTO product4ProjectDTO = new Product4ProjectDTO();
                product4ProjectDTO.setCommonName(yxStoreProduct.getCommonName());
                product4ProjectDTO.setProductId(yxStoreProduct.getId());
                product4ProjectDTO.setSpec(yxStoreProduct.getSpec());
                product4ProjectDTO.setImagePath(yxStoreProduct.getImage());
                product4ProjectDTO.setManufacturer(yxStoreProduct.getManufacturer());
                product4ProjectDTO.setUnit(yxStoreProduct.getUnit());
                product4ProjectDTO.setMedName(yxStoreProduct.getStoreName());
                // 获取价格
                // YxStoreProductAttrValue yxStoreProductAttrValue = yxStoreProductAttrValueService.getOne(new LambdaQueryWrapper<YxStoreProductAttrValue>().eq(YxStoreProductAttrValue::getUnique,product4project.getProductUniqueId()));
                if(product4project.getUnitPrice() == null) {
                    YxStoreProductAttrValue yxStoreProductAttrValue = yxStoreProductAttrValueService.getOne(new LambdaQueryWrapper<YxStoreProductAttrValue>().eq(YxStoreProductAttrValue::getUnique,product4project.getProductUniqueId()));
                    product4ProjectDTO.setPrice(yxStoreProductAttrValue.getPrice());
                } else {
                    product4ProjectDTO.setPrice(product4project.getUnitPrice());
                }

                product4ProjectDTO.setProductUniqueId(product4project.getProductUniqueId());
                product4ProjectDTO.setRemarks(product4project.getRemarks());
                product4ProjectDTO.setIsFixNum(product4project.getIsFixNum());
                product4ProjectDTO.setNum(product4project.getNum());
                product4ProjectDTOList.add(product4ProjectDTO);
            }

            store4ProjectDTO.setProductList(product4ProjectDTOList);


            // 获取组合商品
            List<GroupProduct4ProjectDTO> groupList = new ArrayList<>();
            // 1.获取组合名称
            QueryWrapper<Product4project> QueryWrapper_group = new QueryWrapper();
            QueryWrapper_group.eq("project_no",product4projectQueryParam.getProjectCode());
            QueryWrapper_group.eq("store_id",store4ProjectDTO.getId());
            QueryWrapper_group.eq("is_del",0);
            QueryWrapper_group.ne("group_name","");
            QueryWrapper_group.select("distinct group_name");
            List<Product4project> groupNametList = list(QueryWrapper_group);
            for(Product4project product4project:groupNametList) {
                String groupNanme = product4project.getGroupName();

                GroupProduct4ProjectDTO groupProduct4ProjectDTO = new GroupProduct4ProjectDTO();
                groupProduct4ProjectDTO.setGroupName(groupNanme);
                // 根据组合名称查询药品列表

                LambdaQueryWrapper<Product4project> lambdaQueryWrapper1 = new LambdaQueryWrapper();
                lambdaQueryWrapper1.eq(Product4project::getProjectNo,product4projectQueryParam.getProjectCode());
                lambdaQueryWrapper1.eq(Product4project::getStoreId,store4ProjectDTO.getId());
                lambdaQueryWrapper1.eq(Product4project::getIsDel,0);
                lambdaQueryWrapper1.eq(Product4project::getGroupName,groupNanme);
                List<Product4project> Product4projectList1 = list(lambdaQueryWrapper1);
                List<Product4ProjectDTO> product4ProjectDTOList1 = new ArrayList<>();
                for(Product4project product4project1 : Product4projectList1) {
                    // 获取药品字段
                    LambdaQueryWrapper<YxStoreProduct> lambdaQueryWrapperProduct = new  LambdaQueryWrapper();
                    lambdaQueryWrapperProduct.eq(YxStoreProduct::getId,product4project1.getProductId());
                    lambdaQueryWrapperProduct.select(YxStoreProduct::getStoreName,YxStoreProduct::getImage, YxStoreProduct::getCommonName, YxStoreProduct::getSpec,YxStoreProduct::getManufacturer, YxStoreProduct::getUnit,YxStoreProduct::getId);
                    YxStoreProduct yxStoreProduct = yxStoreProductService.getOne(lambdaQueryWrapperProduct);

                    Product4ProjectDTO product4ProjectDTO = new Product4ProjectDTO();
                    product4ProjectDTO.setCommonName(yxStoreProduct.getCommonName());
                    product4ProjectDTO.setProductId(yxStoreProduct.getId());
                    product4ProjectDTO.setSpec(yxStoreProduct.getSpec());
                    product4ProjectDTO.setImagePath(yxStoreProduct.getImage());
                    product4ProjectDTO.setManufacturer(yxStoreProduct.getManufacturer());
                    product4ProjectDTO.setUnit(yxStoreProduct.getUnit());
                    product4ProjectDTO.setMedName(yxStoreProduct.getStoreName());

                    // 获取价格
                    //
                    // YxStoreProductAttrValue yxStoreProductAttrValue = yxStoreProductAttrValueService.getOne(new LambdaQueryWrapper<YxStoreProductAttrValue>().eq(YxStoreProductAttrValue::getUnique,product4project.getProductUniqueId()));
                   // product4ProjectDTO.setPrice(product4project1.getUnitPrice());

                    // 获取价格
                    // YxStoreProductAttrValue yxStoreProductAttrValue = yxStoreProductAttrValueService.getOne(new LambdaQueryWrapper<YxStoreProductAttrValue>().eq(YxStoreProductAttrValue::getUnique,product4project.getProductUniqueId()));
                    if(product4project1.getUnitPrice() == null) {
                        YxStoreProductAttrValue yxStoreProductAttrValue = yxStoreProductAttrValueService.getOne(new LambdaQueryWrapper<YxStoreProductAttrValue>().eq(YxStoreProductAttrValue::getUnique,product4project1.getProductUniqueId()));
                        product4ProjectDTO.setPrice(yxStoreProductAttrValue.getPrice());
                    } else {
                        product4ProjectDTO.setPrice(product4project1.getUnitPrice());
                    }

                    product4ProjectDTO.setProductUniqueId(product4project1.getProductUniqueId());
                    product4ProjectDTO.setRemarks(product4project1.getRemarks());
                    product4ProjectDTO.setIsFixNum(product4project1.getIsFixNum());
                    product4ProjectDTO.setNum(product4project1.getNum());
                    product4ProjectDTOList1.add(product4ProjectDTO);
                }

                groupProduct4ProjectDTO.setProductList(product4ProjectDTOList1);

                groupList.add(groupProduct4ProjectDTO);
            }

            store4ProjectDTO.setGroupList(groupList);
        }

        Project project = projectService.getOne(new LambdaQueryWrapper<Project>().eq(Project::getProjectCode,product4projectQueryParam.getProjectCode()),false);
        Data4ProjectDTO data4ProjectDTO = new Data4ProjectDTO();
        data4ProjectDTO.setStoreList(store4ProjectDTOList);
        data4ProjectDTO.setPhone(project.getPhone());
        data4ProjectDTO.setServiceGroupId(project.getServiceGroupId());
        data4ProjectDTO.setRemark(project.getRemark());
        data4ProjectDTO.setDesc(project.getProjectDesc());
        data4ProjectDTO.setProjectName(project.getProjectName());
        data4ProjectDTO.setPayType(project.getPayType());
        return data4ProjectDTO;
    }
}
