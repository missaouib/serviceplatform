/**
* Copyright (C) 2018-2020
* All rights reserved, Designed By www.yixiang.co
* 注意：
* 本软件为www.yixiang.co开发研制，未经购买不得使用
* 购买后可获得全部源代码（禁止转卖、分享、上传到码云、github等开源平台）
* 一经发现盗用、分享等行为，将追究法律责任，后果自负
*/
package co.yixiang.modules.yaoshitong.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import co.yixiang.common.service.impl.BaseServiceImpl;
import co.yixiang.common.utils.QueryHelpPlus;
import co.yixiang.dozer.service.IGenerator;
import co.yixiang.modules.shop.entity.MdPharmacistService;
import co.yixiang.modules.shop.service.MdPharmacistServiceService;
import co.yixiang.modules.user.entity.YxUser;
import co.yixiang.modules.user.entity.YxWechatUser;
import co.yixiang.modules.user.service.YxUserService;
import co.yixiang.modules.user.service.YxWechatUserService;
import co.yixiang.modules.user.web.vo.YxWechatUserQueryVo;
import co.yixiang.modules.yaoshitong.entity.YaoshitongUserLable;
import co.yixiang.modules.yaoshitong.entity.YaoshitongUserLableRelation;
import co.yixiang.modules.yaoshitong.service.YaoshitongPatientService;
import co.yixiang.modules.yaoshitong.service.YaoshitongUserLableRelationService;
import co.yixiang.modules.yaoshitong.service.YaoshitongUserLableService;
import co.yixiang.modules.yaoshitong.web.vo.YaoshitongPatientDto;
import co.yixiang.modules.yaoshitong.web.param.YaoshitongPatientQueryCriteria;
import co.yixiang.modules.yaoshitong.mapper.YaoshitongPatientMapper;
import co.yixiang.modules.yaoshitong.mapper.YaoshitongPatientRelationMapper;
import co.yixiang.modules.yaoshitong.entity.YaoshitongPatient;
import co.yixiang.modules.yaoshitong.entity.YaoshitongPatientRelation;
import co.yixiang.utils.FileUtil;
import co.yixiang.utils.RedisUtils;
import co.yixiang.utils.SecurityUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

// 默认不使用缓存
//import org.springframework.cache.annotation.CacheConfig;
//import org.springframework.cache.annotation.CacheEvict;
//import org.springframework.cache.annotation.Cacheable;

/**
* @author visa
* @date 2020-07-13
*/
@Service
//@AllArgsConstructor
//@CacheConfig(cacheNames = "yaoshitongPatient")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class YaoshitongPatientServiceImpl extends BaseServiceImpl<YaoshitongPatientMapper, YaoshitongPatient> implements YaoshitongPatientService {

    private final IGenerator generator;

    @Autowired
    private YaoshitongPatientRelationMapper relationMapper;

    @Autowired
    private MdPharmacistServiceService pharmacistService;

    @Autowired
    private YxUserService yxUserService;

    @Autowired
    private YaoshitongUserLableRelationService userLableRelationService;

    public YaoshitongPatientServiceImpl(IGenerator generator){
        this.generator = generator;
    }

    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private YxWechatUserService wechatUserService;

    @Override
    //@Cacheable
    public Map<String, Object> queryAll(YaoshitongPatientQueryCriteria criteria, Pageable pageable) {
        QueryWrapper<YaoshitongPatient> queryWrapper = new QueryWrapper<>();
        if(StrUtil.isNotBlank(criteria.getKeyword())){
            queryWrapper.and(Wrapper -> Wrapper.like("name",criteria.getKeyword()).or().like("phone",criteria.getKeyword()));
        }

        /*if(criteria.getPharmacistId() != null) {
            queryWrapper.apply(" exists (select 1 from yaoshitong_patient_relation a where a.patient_id = yaoshitong_patient.id and a.pharmacist_id = {0})",criteria.getPharmacistId());
        }*/

        if(CollUtil.isNotEmpty(criteria.getLableIds())) {
            queryWrapper.exists(" SELECT 1 FROM yaoshitong_user_lable_relation yulr WHERE yulr.patient_id = a.id AND yulr.lable_id IN ( " + CollUtil.join(criteria.getLableIds(),",")  +")");
        }
        queryWrapper.apply(" b.patient_id = a.id and b.pharmacist_id = {0}",criteria.getPharmacistId());
        queryWrapper.orderByDesc("b.update_time");
        Page<YaoshitongPatient> pageModel = new Page<>(criteria.getPage(),
                criteria.getLimit());
        //IPage<YaoshitongPatient> pageList = baseMapper.selectPage(pageModel,queryWrapper);
        IPage<YaoshitongPatient> pageList = baseMapper.getPatientPageList(pageModel,queryWrapper,criteria.getPharmacistId());
        List<YaoshitongPatientDto> yaoshitongPatientDtoList = generator.convert(pageList.getRecords(), YaoshitongPatientDto.class);
        for(YaoshitongPatientDto patient:yaoshitongPatientDtoList) {
            if(patient!=null && StrUtil.isNotBlank(patient.getBirth())) {
                patient.setAge(DateUtil.ageOfNow(patient.getBirth() + "01"));
            }

            // 根据手机号查找患者的uid
            if(StrUtil.isNotBlank(patient.getPhone())){
                YxUser yxUser = yxUserService.getOne(new QueryWrapper<YxUser>().eq("phone",patient.getPhone()).last("limit 1"),false);
                if(yxUser != null) {
                    patient.setUid(yxUser.getUid());
                    Integer senduserid = yxUser.getUid();
                    Integer reviceuserid = SecurityUtils.getUserId().intValue();

                    String key = "msgUnread-"+senduserid+"-"+reviceuserid;
                    if(redisUtils.get(key) != null) {
                        Integer unReadCount = Integer.valueOf(String.valueOf(redisUtils.get(key)));
                        patient.setUnRead(unReadCount);
                    }

                }
            }


            // 查找患者标签
           List<YaoshitongUserLable> lableList = userLableRelationService.getUserLableRelationByUid(criteria.getPharmacistId(),patient.getId());

            patient.setLableList(lableList);
        }
       //  getPage(pageable);
       // PageHelper.startPage(pageable.getPageNumber()+1, pageable.getPageSize(),null);
       // PageInfo<YaoshitongPatient> page = new PageInfo<>(queryAll(criteria));
        Map<String, Object> map = new LinkedHashMap<>(2);
        map.put("content", yaoshitongPatientDtoList);
        map.put("totalElements", pageList.getTotal());
        return map;
    }


    @Override
    //@Cacheable
    public List<YaoshitongPatient> queryAll(YaoshitongPatientQueryCriteria criteria){
        String pharmacistId = criteria.getPharmacistId();
        criteria.setPharmacistId(null);
        QueryWrapper queryWrapper = QueryHelpPlus.getPredicate(YaoshitongPatient.class, criteria);

        if(pharmacistId != null) {
            queryWrapper.apply(" exists (select 1 from yaoshitong_patient_relation a where a.patient_id = yaoshitong_patient.id and a.pharmacist_id = {0})",pharmacistId);
        }
        queryWrapper.orderByDesc("update_time");
        return baseMapper.selectList(queryWrapper);
    }


    @Override
    public void download(List<YaoshitongPatientDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (YaoshitongPatientDto yaoshitongPatient : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("名称", yaoshitongPatient.getName());
            map.put("手机号", yaoshitongPatient.getPhone());
            map.put("性别", yaoshitongPatient.getSex());
            map.put("年龄", yaoshitongPatient.getAge());
            map.put("身份证号", yaoshitongPatient.getIdCard());
            map.put("社保卡号", yaoshitongPatient.getSocialCard());
            /*map.put("病史", yaoshitongPatient.getMedicalHistory());
            map.put("诊断史", yaoshitongPatient.getDiagnosisHistory());
            map.put("用药史", yaoshitongPatient.getMedicationHistory());
            map.put("药物过敏", yaoshitongPatient.getDrugAllergy());
            map.put("用药禁忌", yaoshitongPatient.getDrugContraindications());
            map.put("生成时间", yaoshitongPatient.getCreateTime());
            map.put("更新时间", yaoshitongPatient.getUpdateTime());
            map.put("更新人", yaoshitongPatient.getUpdateUser());*/
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

    @Override
    public YaoshitongPatient savePatient(YaoshitongPatient resources,Integer uid) {
        MdPharmacistService pharmacist = pharmacistService.getMdPharmacistByUid(uid);
        if(StrUtil.isBlank(resources.getPharmacistId() )) {
            if(pharmacist != null) {
                resources.setPharmacistId(pharmacist.getId());
            }
        }

        // 更新年龄
        if(StrUtil.isNotBlank(resources.getBirth())) {
            resources.setAge(DateUtil.ageOfNow(resources.getBirth() + "01"));
        }

        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("phone",resources.getPhone());
        queryWrapper.select("id");
        YaoshitongPatient yaoshitongPatient = this.getOne(queryWrapper,true);
        if(ObjectUtil.isNotNull(yaoshitongPatient)) {
            resources.setId(yaoshitongPatient.getId());
         //   resources.setUpdateTime(DateUtil.date().toTimestamp());
        }

        // 最后修改人

        if(pharmacist != null){
            resources.setUpdateUser(pharmacist.getName());
        }

        // 查找患者电话，时候已经绑定用户

        YxUser yxUser = yxUserService.getOne(new QueryWrapper<YxUser>().eq("phone",resources.getPhone()).eq("user_type","routine").last("limit 1").select("uid"),false);

        if(yxUser != null) {
            resources.setUid(yxUser.getUid());

           /* yxUser.setRealName(resources.getName());
            yxUserService.updateById(yxUser);
*/
        }

        this.saveOrUpdate(resources);

        //新增患者-药师的关系

        QueryWrapper queryWrapper1 = new QueryWrapper();
        queryWrapper1.eq("patient_id",resources.getId());
        queryWrapper1.eq("pharmacist_id",resources.getPharmacistId());
        Integer count = relationMapper.selectCount(queryWrapper1);
        if(count == 0) {
            YaoshitongPatientRelation relation = new YaoshitongPatientRelation();
            relation.setPatientId(resources.getId());
            relation.setPharmacistId(resources.getPharmacistId());
            relation.setCreateTime(DateUtil.date().toTimestamp());
            relation.setUpdateTime(DateUtil.date().toTimestamp());
            relation.setIsDel(0);
            relationMapper.insert(relation);
        }

        // 删除患者标签
        QueryWrapper queryWrapper2 = new QueryWrapper();
        queryWrapper2.eq("pharmacist_id",resources.getPharmacistId());
        queryWrapper2.eq("patient_id",resources.getId());
        userLableRelationService.remove(queryWrapper2);

        if(resources.getLableIds() != null) {
            // 新增患者标签
            for(Integer lableId : resources.getLableIds()) {
                YaoshitongUserLableRelation userLableRelation = new YaoshitongUserLableRelation();
                userLableRelation.setLableId(lableId);
                userLableRelation.setPatientId(resources.getId());
                userLableRelation.setUid(uid);
                userLableRelation.setPharmacistId(resources.getPharmacistId());
                userLableRelationService.save(userLableRelation);
            }
        }


        return resources;
    }


    @Override
    public YaoshitongPatient savePatientByPatient(YaoshitongPatient resources) {

        // 更新年龄
        if(StrUtil.isNotBlank(resources.getBirth())) {
            resources.setAge(DateUtil.ageOfNow(resources.getBirth() + "01"));
        }

        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("phone",resources.getPhone());
        queryWrapper.select("id");
        YaoshitongPatient yaoshitongPatient = this.getOne(queryWrapper,true);
        if(ObjectUtil.isNotNull(yaoshitongPatient)) {
            resources.setId(yaoshitongPatient.getId());
            //   resources.setUpdateTime(DateUtil.date().toTimestamp());
        }

        this.saveOrUpdate(resources);

        //新增患者-药师的关系

        QueryWrapper queryWrapper1 = new QueryWrapper();
        queryWrapper1.eq("patient_id",resources.getId());
        queryWrapper1.eq("pharmacist_id",resources.getPharmacistId());
        Integer count = relationMapper.selectCount(queryWrapper1);
        if(count == 0) {
            YaoshitongPatientRelation relation = new YaoshitongPatientRelation();
            relation.setPatientId(resources.getId());
            relation.setPharmacistId(resources.getPharmacistId());
            relation.setCreateTime(DateUtil.date().toTimestamp());
            relation.setUpdateTime(DateUtil.date().toTimestamp());
            relation.setIsDel(0);
            relationMapper.insert(relation);
        }

        // 删除患者标签
        QueryWrapper queryWrapper2 = new QueryWrapper();
        queryWrapper2.eq("pharmacist_id",resources.getPharmacistId());
        queryWrapper2.eq("patient_id",resources.getId());
        userLableRelationService.remove(queryWrapper2);

        if(resources.getLableIds() != null) {
            // 新增患者标签
            for(Integer lableId : resources.getLableIds()) {
                YaoshitongUserLableRelation userLableRelation = new YaoshitongUserLableRelation();
                userLableRelation.setLableId(lableId);
                userLableRelation.setPatientId(resources.getId());
                userLableRelation.setUid(resources.getUid());
                userLableRelation.setPharmacistId(resources.getPharmacistId());
                userLableRelationService.save(userLableRelation);
            }
        }


        return resources;

    }

    @Override
    public YaoshitongPatient findPatientByUid(Integer uid) {
        return baseMapper.findPatientByUid(uid);
    }

    @Override
    public void savePatientByChat(YaoshitongPatient resources) {

        YxUser yxUser = yxUserService.getById(resources.getUid());
        if(yxUser == null) {
            return;
        }
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("uid",resources.getUid());
        queryWrapper.last(" limit 1");
        queryWrapper.select("id");
        YaoshitongPatient yaoshitongPatient = this.getOne(queryWrapper,true);
        if(ObjectUtil.isNotNull(yaoshitongPatient)) { // 已经存在
            resources.setId(yaoshitongPatient.getId());
            //   resources.setUpdateTime(DateUtil.date().toTimestamp());
        }else{
            resources.setName(yxUser.getNickname());
            resources.setPhone(yxUser.getPhone());

            YxWechatUserQueryVo yxWechatUser = wechatUserService.getYxWechatUserById(resources.getUid());
            if(yxWechatUser != null) {
                Integer sex = yxWechatUser.getSex();
                if(sex == 1) {
                    resources.setSex("男");
                }else {
                    resources.setSex("女");
                }
            }
            this.save(resources);
        }

     //   this.saveOrUpdate(resources);

        //新增患者-药师的关系

        QueryWrapper queryWrapper1 = new QueryWrapper();
        queryWrapper1.eq("patient_id",resources.getId());
        queryWrapper1.eq("pharmacist_id",resources.getPharmacistId());
        Integer count = relationMapper.selectCount(queryWrapper1);
        if(count == 0) {
            YaoshitongPatientRelation relation = new YaoshitongPatientRelation();
            relation.setPatientId(resources.getId());
            relation.setPharmacistId(resources.getPharmacistId());
            relation.setCreateTime(DateUtil.date().toTimestamp());
            relation.setUpdateTime(DateUtil.date().toTimestamp());
            relation.setIsDel(0);
            relationMapper.insert(relation);
        }
    }
}
