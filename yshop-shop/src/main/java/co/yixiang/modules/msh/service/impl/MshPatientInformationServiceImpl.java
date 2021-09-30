/**
* Copyright (C) 2018-2020
* All rights reserved, Designed By www.yixiang.co
* 注意：
* 本软件为www.yixiang.co开发研制，未经购买不得使用
* 购买后可获得全部源代码（禁止转卖、分享、上传到码云、github等开源平台）
* 一经发现盗用、分享等行为，将追究法律责任，后果自负
*/
package co.yixiang.modules.msh.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.PageInfo;

import co.yixiang.common.service.impl.BaseServiceImpl;
import co.yixiang.common.utils.QueryHelpPlus;
import co.yixiang.dozer.service.IGenerator;
import co.yixiang.modules.msh.domain.MshPatientInformation;
import co.yixiang.modules.msh.domain.MshPatientListFile;
import co.yixiang.modules.msh.service.MshPatientInformationService;
import co.yixiang.modules.msh.service.dto.MshPatientInformationDto;
import co.yixiang.modules.msh.service.dto.MshPatientInformationQueryCriteria;
import co.yixiang.modules.msh.service.mapper.MshDemandListItemMapper;
import co.yixiang.modules.msh.service.mapper.MshPatientInformationMapper;
import co.yixiang.modules.msh.service.mapper.MshPatientListFileMapper;
import co.yixiang.utils.FileUtil;
import lombok.AllArgsConstructor;

/**
* @author cq
* @date 2020-12-18
*/
@Service
@AllArgsConstructor
//@CacheConfig(cacheNames = "mshPatientInformation")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class MshPatientInformationServiceImpl extends BaseServiceImpl<MshPatientInformationMapper, MshPatientInformation> implements MshPatientInformationService {

	@Autowired
    private final IGenerator generator;

    @Autowired
    private MshPatientInformationMapper mshPatientInformationMapper;

    @Autowired
    private MshPatientListFileMapper mshPatientListFileMapper;

    @Autowired
    private MshDemandListItemMapper mshDemandListItemMapper;

    @Override
    //@Cacheable
    public Map<String, Object> queryAll(MshPatientInformationQueryCriteria criteria, Pageable pageable) {
        getPage(pageable);
        PageInfo<MshPatientInformation> page = new PageInfo<>(queryAll(criteria));
        Map<String, Object> map = new LinkedHashMap<>(2);
        map.put("content", generator.convert(page.getList(), MshPatientInformationDto.class));
        map.put("totalElements", page.getTotal());
        return map;
    }


    @Override
    //@Cacheable
    public List<MshPatientInformation> queryAll(MshPatientInformationQueryCriteria criteria){
        return baseMapper.selectList(QueryHelpPlus.getPredicate(MshPatientInformation.class, criteria));
    }


    @Override
    public void download(List<MshPatientInformationDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (MshPatientInformationDto mshPatientInformation : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("MemberId", mshPatientInformation.getMemberId());
            map.put("患者姓名", mshPatientInformation.getPatientname());
            map.put("手机号", mshPatientInformation.getPhone());
            map.put("省", mshPatientInformation.getProvince());
            map.put("市", mshPatientInformation.getCity());
            map.put("区", mshPatientInformation.getDistrict());
            map.put("详细地址", mshPatientInformation.getDetail());
            map.put("添加时间", mshPatientInformation.getAddTime());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

	@Override
	public Map<String, Object> selectMshPatientListList(MshPatientInformationQueryCriteria criteria, Pageable pageable) {
		getPage(pageable);
        PageInfo<MshPatientInformationDto> page = new PageInfo<>(selectMshPatientListList(criteria));
        Map<String, Object> map = new LinkedHashMap<>(2);
        map.put("content", generator.convert(page.getList(), MshPatientInformationDto.class));
        map.put("totalElements", page.getTotal());
        return map;
	}

	public List<MshPatientInformationDto> selectMshPatientListList(MshPatientInformationQueryCriteria criteria){
    	List<MshPatientInformationDto> list = mshDemandListItemMapper.selectMshPatientListList(criteria);
        return list;
    }


	@Override
	public List<MshPatientInformationDto> selectMshPatientInformationListByPhone(String phone) {
		List<MshPatientInformationDto> list = new ArrayList<>();
		//根据手机号查询患者信息
		QueryWrapper<MshPatientInformation> queryWrapper = new QueryWrapper<>();
		queryWrapper.eq("phone", phone);
		queryWrapper.eq("delete_status",0);
		List<MshPatientInformation> mshOrderList = mshPatientInformationMapper.selectList(queryWrapper);
		MshPatientInformationDto mshPatientInformationDto = null;
		for (int i = 0; i < mshOrderList.size(); i++) {
			mshPatientInformationDto = new MshPatientInformationDto();
			mshPatientInformationDto.setId(mshOrderList.get(i).getId());
			mshPatientInformationDto.setPatientname(mshOrderList.get(i).getPatientname());
			mshPatientInformationDto.setPhone(mshOrderList.get(i).getPhone());
			mshPatientInformationDto.setAddTime(mshOrderList.get(i).getAddTime());
			mshPatientInformationDto.setProvinceCode(mshOrderList.get(i).getProvinceCode());
			mshPatientInformationDto.setCityCode(mshOrderList.get(i).getCityCode());
			mshPatientInformationDto.setDistrictCode(mshOrderList.get(i).getDistrictCode());
			mshPatientInformationDto.setProvince(mshOrderList.get(i).getProvince());
			mshPatientInformationDto.setCity(mshOrderList.get(i).getCity());
			mshPatientInformationDto.setDistrict(mshOrderList.get(i).getDistrict());
			mshPatientInformationDto.setDetail(mshOrderList.get(i).getDetail());
            mshPatientInformationDto.setMemberId(mshOrderList.get(i).getMemberId());
            mshPatientInformationDto.setReceivingName(mshOrderList.get(i).getReceivingName());
            mshPatientInformationDto.setRelationship(mshOrderList.get(i).getRelationship());
            mshPatientInformationDto.setReceivingPhone(mshOrderList.get(i).getReceivingPhone());
            mshPatientInformationDto.setCompany(mshOrderList.get(i).getCompany());
            mshPatientInformationDto.setVip(mshOrderList.get(i).getVip());
            mshPatientInformationDto.setPerCustoService(mshOrderList.get(i).getPerCustoService());
            mshPatientInformationDto.setPerCustoServiceEmail(mshOrderList.get(i).getPerCustoServiceEmail());
            mshPatientInformationDto.setPatientEmail(mshOrderList.get(i).getPatientEmail());
            mshPatientInformationDto.setDiseaseName(mshOrderList.get(i).getDiseaseName());
            mshPatientInformationDto.setFileHospital(mshOrderList.get(i).getFileHospital());
            mshPatientInformationDto.setFileDate(mshOrderList.get(i).getFileDate());

			QueryWrapper<MshPatientListFile> queryWrapper2 = new QueryWrapper<>();
			queryWrapper2.eq("patient_id", mshOrderList.get(i).getId());
			mshPatientInformationDto.setMshPatientListFileList(mshPatientListFileMapper.selectList(queryWrapper2));
			list.add(mshPatientInformationDto);
		}
		return list;
	}
}
