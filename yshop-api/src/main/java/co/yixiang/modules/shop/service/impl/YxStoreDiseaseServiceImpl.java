package co.yixiang.modules.shop.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import co.yixiang.constant.ShopConstants;
import co.yixiang.enums.ProjectNameEnum;
import co.yixiang.enums.TaipingCardTypeEnum;
import co.yixiang.enums.TaipingDrugStoreTypeEnum;
import co.yixiang.modules.shop.entity.*;
import co.yixiang.modules.shop.mapper.YxStoreDiseaseMapper;
import co.yixiang.modules.shop.mapper.YxStoreProductMapper;
import co.yixiang.modules.shop.mapping.CategoryMap;
import co.yixiang.modules.shop.mapping.DiseaseMap;
import co.yixiang.modules.shop.service.*;
import co.yixiang.modules.shop.web.param.YxStoreDiseaseQueryParam;
import co.yixiang.modules.shop.web.vo.YxStoreDiseaseQueryVo;
import co.yixiang.common.service.impl.BaseServiceImpl;
import co.yixiang.common.web.vo.Paging;
import co.yixiang.utils.CateDTO;
import co.yixiang.utils.DiseaseDTO;
import co.yixiang.utils.RedisUtils;
import co.yixiang.utils.TreeUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import java.io.Serializable;
import java.util.*;


/**
 * <p>
 * 病种 服务实现类
 * </p>
 *
 * @author visazhou
 * @since 2020-06-03
 */
@Slf4j
@Service
@AllArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class YxStoreDiseaseServiceImpl extends BaseServiceImpl<YxStoreDiseaseMapper, YxStoreDisease> implements YxStoreDiseaseService {

    @Autowired
    private YxStoreDiseaseMapper yxStoreDiseaseMapper;

    @Autowired
    private HospitalService hospitalService;

    private final DiseaseMap diseaseMap;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private YxStoreProductService yxStoreProductService;

    @Override
    public YxStoreDiseaseQueryVo getYxStoreDiseaseById(Serializable id) throws Exception{
        return yxStoreDiseaseMapper.getYxStoreDiseaseById(id);
    }

    @Override
    public Paging<YxStoreDiseaseQueryVo> getYxStoreDiseasePageList(YxStoreDiseaseQueryParam yxStoreDiseaseQueryParam) throws Exception{
        Page page = setPageParam(yxStoreDiseaseQueryParam,OrderItem.desc("create_time"));
        IPage<YxStoreDiseaseQueryVo> iPage = yxStoreDiseaseMapper.getYxStoreDiseasePageList(page,yxStoreDiseaseQueryParam);
        return new Paging(iPage);
    }

    @Override
    public List<DiseaseDTO> getList(YxStoreDiseaseQueryParam yxStoreDiseaseQueryParam) {
        //  Integer partnerId = 1;
        QueryWrapper<YxStoreDisease> wrapper = new QueryWrapper<>();
        wrapper.eq("is_show",1);
        wrapper.eq("is_del",0);

        wrapper.orderByAsc("sort");

        if(StrUtil.isNotBlank(yxStoreDiseaseQueryParam.getCateType())) {
            wrapper.like("cate_type",yxStoreDiseaseQueryParam.getCateType());
        }

        if(StrUtil.isNotBlank(yxStoreDiseaseQueryParam.getProjectCode())) {
            if(ProjectNameEnum.TAIPING_LEXIANG.getValue().equals(yxStoreDiseaseQueryParam.getProjectCode())) {
                wrapper.eq("project_code",ProjectNameEnum.TAIPING_LEXIANG.getValue());
                if(TaipingCardTypeEnum.card_base.getValue().equals(yxStoreDiseaseQueryParam.getCardType())) {
                    String ids = (String)redisUtils.get(  "disease-" +  yxStoreDiseaseQueryParam.getProjectCode() + "-label1");
                    wrapper.apply(" id in ("+ids+")");
                }else if(TaipingCardTypeEnum.card_chronic.getValue().equals(yxStoreDiseaseQueryParam.getCardType())) {
                    String ids = (String)redisUtils.get(  "disease-" +  yxStoreDiseaseQueryParam.getProjectCode() + "-label2");
                    wrapper.apply(" id in ("+ids+")");
                }else if(TaipingCardTypeEnum.card_advanced.getValue().equals(yxStoreDiseaseQueryParam.getCardType())) {
                    if(TaipingDrugStoreTypeEnum.store_85.getValue().equals(yxStoreDiseaseQueryParam.getDrugStoreType())) {
                        String ids = (String)redisUtils.get(  "disease-" +  yxStoreDiseaseQueryParam.getProjectCode() + "-label1");
                        wrapper.apply(" id in ("+ids+")");
                    } else if(TaipingDrugStoreTypeEnum.store_50.getValue().equals(yxStoreDiseaseQueryParam.getDrugStoreType())) {
                        String ids = (String)redisUtils.get(  "disease-" +  yxStoreDiseaseQueryParam.getProjectCode() + "-label3");
                        wrapper.apply(" id in ("+ids+")");
                    }
                }
            } else if (ProjectNameEnum.LINGYUANZHI.getValue().equals(yxStoreDiseaseQueryParam.getProjectCode())) {
                wrapper.eq("project_code",ProjectNameEnum.LINGYUANZHI.getValue());
                String ids = (String)redisUtils.get(  "disease-" +  yxStoreDiseaseQueryParam.getProjectCode());
                wrapper.apply(" id in ("+ids+")");
            } else {
                Project project = projectService.getOne(new LambdaQueryWrapper<Project>().eq(Project::getProjectCode,yxStoreDiseaseQueryParam.getProjectCode()),false);
                if(project!= null && "1".equals(project.getGuangzhouFlag())) {  // 广州店的项目
                    wrapper.eq("project_code",ProjectNameEnum.TAIPING_LEXIANG.getValue());
                    String ids = (String)redisUtils.get(  "disease-" +  yxStoreDiseaseQueryParam.getProjectCode());
                    wrapper.apply(" id in ("+ids+")");

                } else {   // 非广州店的项目
                    wrapper.eq("project_code","");
                    String ids = (String)redisUtils.get(  "disease-" +  yxStoreDiseaseQueryParam.getProjectCode());
                    wrapper.apply(" id in ("+ids+")");
                }
            }
        }else if(StrUtil.isBlank(yxStoreDiseaseQueryParam.getProjectCode())) {
            wrapper.eq("project_code","");
        }

       /* if(ProjectNameEnum.TAIPING_LEXIANG.getValue().equals(yxStoreDiseaseQueryParam.getProjectCode())) {
            wrapper.eq("project_code",ProjectNameEnum.TAIPING_LEXIANG.getValue());
            if(TaipingCardTypeEnum.card_base.getValue().equals(yxStoreDiseaseQueryParam.getCardType())) {
                 wrapper.apply(" EXISTS (SELECT 1 FROM yx_store_product ysp WHERE FIND_IN_SET(yx_store_disease.id,ysp.disease_id_cloud) AND ysp.label1 = 'Y')");
            }else if(TaipingCardTypeEnum.card_chronic.getValue().equals(yxStoreDiseaseQueryParam.getCardType())) {
                wrapper.apply(" EXISTS (SELECT 1 FROM yx_store_product ysp WHERE FIND_IN_SET(yx_store_disease.id,ysp.disease_id_cloud) AND ysp.label2 = 'Y')");
            }else if(TaipingCardTypeEnum.card_advanced.getValue().equals(yxStoreDiseaseQueryParam.getCardType())) {
                if(TaipingDrugStoreTypeEnum.store_85.getValue().equals(yxStoreDiseaseQueryParam.getDrugStoreType())) {
                    wrapper.apply(" EXISTS (SELECT 1 FROM yx_store_product ysp WHERE FIND_IN_SET(yx_store_disease.id,ysp.disease_id_cloud) AND ysp.label1 = 'Y')");
                } else if(TaipingDrugStoreTypeEnum.store_50.getValue().equals(yxStoreDiseaseQueryParam.getDrugStoreType())) {
                    wrapper.apply(" EXISTS (SELECT 1 FROM yx_store_product ysp WHERE FIND_IN_SET(yx_store_disease.id,ysp.disease_id_cloud) AND ysp.label3 = 'Y')");
                }
            }*//*else {
                wrapper.apply(" EXISTS (SELECT 1 FROM yx_store_product ysp, product4project p WHERE FIND_IN_SET(yx_store_disease.id,ysp.disease_id_cloud) AND ysp.id = p.product_id and p.is_show = 1 and p.is_del = 0 and p.project_no = {0})",yxStoreDiseaseQueryParam.getProjectCode());
            }*//*
        } else if(ProjectNameEnum.HEALTHSTORE.getValue().equals(yxStoreDiseaseQueryParam.getProjectCode())) {  // 健康馆
            wrapper.eq("project_code",ProjectNameEnum.TAIPING_LEXIANG.getValue());
          //  wrapper.apply(" EXISTS (SELECT 1 FROM yx_store_product ysp, product4project p WHERE FIND_IN_SET(yx_store_disease.id,ysp.disease_id) AND ysp.id = p.product_id and p.is_show = 1 and p.is_del = 0 and p.project_no = {0})",yxStoreDiseaseQueryParam.getProjectCode());
        }else if(StrUtil.isNotBlank(yxStoreDiseaseQueryParam.getProjectCode())) {
               // 如果是广州店的项目，则限制project为taipinglexiang
            Project project = projectService.getOne(new LambdaQueryWrapper<Project>().eq(Project::getProjectCode,yxStoreDiseaseQueryParam.getProjectCode()),false);
            if(project!= null && "1".equals(project.getGuangzhouFlag())) {  // 广州店的项目
                wrapper.eq("project_code",ProjectNameEnum.TAIPING_LEXIANG.getValue());
                *//*if( StrUtil.isNotBlank(project.getStoreIds())) {  // 配置了门店信息，用门店的药品做限制
                    wrapper.exists("  (SELECT 1 FROM yx_store_product ysp,yx_store_product_attr_value yspav WHERE FIND_IN_SET(yx_store_disease.id,ysp.disease_id) AND ysp.id = yspav.product_id AND yspav.is_del = 0 and yspav.stock >0 AND ysp.is_show = 1 AND ysp.is_del = 0 AND yspav.store_id IN ( " + project.getStoreIds() + ") )");
                } else {   // 用项目药品做限制
                    wrapper.apply(" EXISTS (SELECT 1 FROM yx_store_product ysp, product4project p WHERE FIND_IN_SET(yx_store_disease.id,ysp.disease_id) AND ysp.id = p.product_id and p.is_show = 1 and p.is_del = 0 and p.project_no = {0})",yxStoreDiseaseQueryParam.getProjectCode());
                }*//*
                String ids = (String)redisUtils.get(  "disease-" +  yxStoreDiseaseQueryParam.getProjectCode());

                wrapper.apply(" id in ("+ids+")");

            } else {   // 非广州店的项目
                wrapper.eq("project_code","");
                if(StrUtil.isNotBlank(project.getStoreIds())) { // 配置了门店信息，用门店的药品做限制
                    wrapper.exists("  SELECT 1 FROM yx_store_product ysp,yx_store_product_attr_value yspav WHERE FIND_IN_SET(yx_store_disease.id,ysp.disease_id) AND ysp.id = yspav.product_id AND yspav.is_del = 0  and yspav.stock >0 AND ysp.is_show = 1 AND ysp.is_del = 0 AND yspav.store_id IN ( " + project.getStoreIds() + ") ");
                } else { // 用项目药品做限制
                    wrapper.apply(" EXISTS (SELECT 1 FROM yx_store_product ysp, product4project p WHERE FIND_IN_SET(yx_store_disease.id,ysp.disease_id) AND ysp.id = p.product_id and p.is_show = 1 and p.is_del = 0 and p.project_no = {0})",yxStoreDiseaseQueryParam.getProjectCode());
                }
            }
        }else if(StrUtil.isBlank(yxStoreDiseaseQueryParam.getProjectCode())) {
            wrapper.eq("project_code","");
        }
*/

        /*if(StrUtil.isNotBlank(yxStoreDiseaseQueryParam.getPartnerCode())) {
            Hospital hospital = hospitalService.getOne(new LambdaQueryWrapper<Hospital>().eq(Hospital::getCode,yxStoreDiseaseQueryParam.getPartnerCode()),false);
            if(hospital != null && StrUtil.isNotBlank(hospital.getStoreIds())) {
                wrapper.exists(" SELECT 1 FROM yx_store_product ysp,yx_store_product_attr_value yspav WHERE ysp.id = yspav.product_id AND yspav.is_del = 0 AND yspav.store_id in ("+ hospital.getStoreIds() +") AND FIND_IN_SET(yx_store_disease.id,ysp.disease_id_common)");
            }

        }*/

        // 限制处方药

        // wrapper.apply(" id IN (SELECT ysp.cate_id FROM yx_store_product ysp ,product_partner_mapping ppm WHERE ysp.id = ppm.product_id AND ppm.partner_id = {0})",partnerId);
        List<DiseaseDTO> list = diseaseMap.toDto(baseMapper.selectList(wrapper));
        // 查询分类所属的组合商品id
        if(ProjectNameEnum.LINGYUANZHI.getValue().equals(yxStoreDiseaseQueryParam.getProjectCode())) {
            for(DiseaseDTO diseaseDTO:list) {
                LambdaQueryWrapper<YxStoreProduct> lambdaQueryWrapper = new LambdaQueryWrapper<>();
               // lambdaQueryWrapper.apply(" FIND_IN_SET({0},disease_id)",diseaseDTO.getId());
                lambdaQueryWrapper.eq(YxStoreProduct::getDiseaseId,String.valueOf(diseaseDTO.getId()));
                lambdaQueryWrapper.eq(YxStoreProduct::getIsDel,0);
                lambdaQueryWrapper.eq(YxStoreProduct::getIsGroup,1);
                lambdaQueryWrapper.last(" limit 1");
                lambdaQueryWrapper.select(YxStoreProduct::getId);
                //  lambdaQueryWrapper.apply(" exists ( select 1 from product4project b where b.product_id = yx_store_product.id and b.is_group = 1 and b.is_show = 1 and b.project_no = {0})",yxStoreDiseaseQueryParam.getProjectCode());
                YxStoreProduct yxStoreProduct = yxStoreProductService.getOne(lambdaQueryWrapper,false);
                if(yxStoreProduct != null) {
                    diseaseDTO.setProductId(yxStoreProduct.getId());
                }
            }
        }


        List<DiseaseDTO> list2 = new ArrayList<>();

        if( StrUtil.isNotBlank(yxStoreDiseaseQueryParam.getProjectCode()) ) {
            List<Long> idList = new ArrayList<>();
            for(DiseaseDTO diseaseDTO:list) {
                if(! idList.contains(diseaseDTO.getPid())) {
                    idList.add(diseaseDTO.getPid());
                }
            }
            if(CollUtil.isNotEmpty(idList)) {
                QueryWrapper queryWrapper = new QueryWrapper();
                queryWrapper.in("id",idList);
                list2 = diseaseMap.toDto(baseMapper.selectList(queryWrapper));
            }

        }

        list.addAll(list2);

        return TreeUtil.list2TreeConverter4Disease(list,Long.valueOf(0));
    }

    @Override
    public List<YxStoreDisease> getList4patient() {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.apply(" EXISTS (SELECT 1 FROM yx_article  WHERE yx_article.type = 2 AND FIND_IN_SET(yx_store_disease.id,yx_article.cid)) ");
        return baseMapper.selectList(queryWrapper);
    }

    @Override
    public List<DiseaseDTO> getListFirstLevel(YxStoreDiseaseQueryParam yxStoreDiseaseQueryParam) {
        QueryWrapper<YxStoreDisease> wrapper = new QueryWrapper<>();
        wrapper.eq("is_show",1).eq("is_del",0).eq("project_code",yxStoreDiseaseQueryParam.getProjectCode()).eq("pid",0).notIn("cate_name","其他用药","风湿骨科用药") .orderByAsc("sort");
        List<DiseaseDTO> list = diseaseMap.toDto(baseMapper.selectList(wrapper));
        return list;
    }
}
