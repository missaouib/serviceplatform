/**
 * Copyright (C) 2018-2020
 * All rights reserved, Designed By www.yixiang.co
 * 注意：
 * 本软件为www.yixiang.co开发研制，未经购买不得使用
 * 购买后可获得全部源代码（禁止转卖、分享、上传到码云、github等开源平台）
 * 一经发现盗用、分享等行为，将追究法律责任，后果自负
 */
package co.yixiang.modules.msh.service.impl;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;

import cn.hutool.core.util.NumberUtil;
import cn.hutool.json.JSONUtil;
import co.yixiang.constant.ShopConstants;
import co.yixiang.constant.SystemConfigConstants;
import co.yixiang.exception.BadRequestException;
import co.yixiang.modules.api.common.Result;
import co.yixiang.modules.message.domain.MessageNotice;
import co.yixiang.modules.message.domain.MessageUser;
import co.yixiang.modules.message.service.MessageNoticeService;
import co.yixiang.modules.message.service.MessageUserService;
import co.yixiang.modules.message.service.mapper.MessageUserMapper;
import co.yixiang.modules.msh.service.dto.*;
import co.yixiang.modules.msh.service.enume.MshStatusEnum;
import co.yixiang.modules.msh.util.MshRequestUtil;
import co.yixiang.modules.shop.domain.Project;
import co.yixiang.modules.shop.domain.YxStoreProduct;
import co.yixiang.modules.shop.domain.YxSystemStore;
import co.yixiang.modules.shop.service.YxSystemStoreService;
import co.yixiang.modules.shop.service.YxUserService;
import co.yixiang.modules.shop.service.mapper.UserMapper;
import co.yixiang.modules.taibao.util.MyBeanUtils;
import co.yixiang.modules.websocket.WebSocket;
import co.yixiang.modules.yiyaobao.dto.Prescription;
import co.yixiang.modules.yiyaobao.dto.PrescriptionDetail;
import co.yixiang.mp.yiyaobao.enums.YiyaobaoPayMethodEnum;
import co.yixiang.mp.yiyaobao.enums.YiyaobaoPayTypeEnum;
import co.yixiang.mp.yiyaobao.service.dto.YiyaobaoOrderInfo;
import co.yixiang.mp.yiyaobao.service.mapper.OrdOrderMapper;
import co.yixiang.rabbitmq.send.MqProducer;
import co.yixiang.tools.utils.AppSiganatureUtils;
import co.yixiang.tools.utils.HttpUtils;
import co.yixiang.utils.FileUtil;
import co.yixiang.utils.SecurityUtils;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.PageInfo;

import cn.hutool.core.date.DateUtil;
import co.yixiang.common.service.impl.BaseServiceImpl;
import co.yixiang.common.utils.QueryHelpPlus;
import co.yixiang.dozer.service.IGenerator;
import co.yixiang.modules.msh.domain.MshDemandList;
import co.yixiang.modules.msh.domain.MshDemandListFile;
import co.yixiang.modules.msh.domain.MshDemandListItem;
import co.yixiang.modules.msh.domain.MshOrder;
import co.yixiang.modules.msh.domain.MshPatientInformation;
import co.yixiang.modules.msh.domain.MshPatientListFile;
import co.yixiang.modules.msh.service.MshDemandListFileService;
import co.yixiang.modules.msh.service.MshDemandListItemService;
import co.yixiang.modules.msh.service.MshDemandListService;
import co.yixiang.modules.msh.service.MshPatientInformationService;
import co.yixiang.modules.msh.service.mapper.MshDemandListFileMapper;
import co.yixiang.modules.msh.service.mapper.MshDemandListItemMapper;
import co.yixiang.modules.msh.service.mapper.MshDemandListMapper;
import co.yixiang.modules.msh.service.mapper.MshOrderMapper;
import co.yixiang.modules.msh.service.mapper.MshPatientInformationMapper;
import co.yixiang.modules.msh.service.mapper.MshPatientListFileMapper;
import co.yixiang.modules.shop.domain.MdCountry;
import co.yixiang.modules.shop.service.mapper.MdCountryMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.util.CollectionUtils;

/**
 * @author cq
 * @date 2020-12-25
 */
@Slf4j
@Service
//@AllArgsConstructor
//@CacheConfig(cacheNames = "mshDemandList")
//@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class MshDemandListServiceImpl extends BaseServiceImpl<MshDemandListMapper, MshDemandList> implements MshDemandListService {

    private static DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");

    private static DateFormat dateFormatAdd = new SimpleDateFormat("yyyy-MM-dd");


    @Autowired
    private IGenerator generator;

    @Autowired
    private MshPatientInformationService mshPatientInformationService;

    @Autowired
    private MshDemandListItemService mshDemandListItemService;

    @Autowired
    private MshDemandListFileService mshDemandListFileService;

    @Autowired
    private MshDemandListMapper mshDemandListMapper;

    @Autowired
    private MshOrderMapper mshOrderMapper;

    @Autowired
    private MshDemandListItemMapper mshDemandListItemMapper;

    @Autowired
    private MshDemandListFileMapper mshDemandListFileMapper;

    @Autowired
    private MshPatientListFileMapper mshPatientListFileMapper;

    @Autowired
    private MshPatientInformationMapper mshPatientInformationMapper;

    @Autowired
    private MdCountryMapper mdCountryMapper;

    @Autowired
    private YxSystemStoreService yxSystemStoreService;

    @Value("${file.path}")
    private String filePath;

    @Value("${file.localUrl}")
    private String localUrl;

    @Value("${msh.domainName}")
    private String domainName;

    @Value("${msh.secureKey}")
    private String secureKey;

    @Autowired
    private MqProducer mqProducer;

    @Value("${msh.delayQueueName}")
    private String mshQueueName;


    @Value("${yiyaobao.addSingleMshUrl}")
    private String addSingleMshUrl;

    @Value("${yiyaobao.apiUrlExternal}")
    private String yiyaobao_apiUrl_external;

    @Value("${yiyaobao.appId}")
    private String appId;

    @Value("${yiyaobao.appSecret}")
    private String appSecret;
    @Autowired
    private OrdOrderMapper yiyaobaoOrdOrderMapper;

    @Autowired
    private WebSocket webSocket;

    @Autowired
    private MessageNoticeService messageNoticeService;

    @Autowired
    private MessageUserMapper messageUserMapper;

    @Override
    //@Cacheable
    public Map<String, Object> queryAll(MshDemandListQueryCriteria criteria, Pageable pageable) {
        getMshPage(criteria,pageable);
        PageInfo<MshDemandListDto> page = new PageInfo<>(selectMshDemandListList(criteria));
        Map<String, Object> map = new LinkedHashMap<>(2);
        map.put("content", generator.convert(page.getList(), MshDemandListDto.class));
        map.put("totalElements", page.getTotal());
        return map;
    }

    public void getMshPage(MshDemandListQueryCriteria criteria,Pageable pageable) {
        String order=null;
        if(pageable.getSort()!=null){
            order= pageable.getSort().toString();
            order=order.replace(":","");
            if(StringUtils.isEmpty(criteria.getOrderBy())){
                order="create_time desc";
            }else{
                order=criteria.getOrderBy();
            }
        }
        PageHelper.startPage(pageable.getPageNumber()+1, pageable.getPageSize(),order);
    }

    public List<MshDemandListDto> selectMshDemandListList(MshDemandListQueryCriteria criteria) {
        List<MshDemandListDto> list = mshDemandListMapper.selectMshDemandLists(criteria);
        //更新订单状态文字
        //0:待审核;1:审核通过;2:审核不通过;3:已发货;4:已完成;5:已退货;
//        for(int i = 0; i<list.size(); i++){
//			for (int j = 0; j < list.get(i).getOrderList().size(); j++) {
//				if("0".equals(list.get(i).getOrderList().get(j).getOrderStatus())){
//					list.get(i).getOrderList().get(j).setOrderStatusStr("待审核");
//				}else if("1".equals(list.get(i).getOrderList().get(j).getOrderStatus())){
//					list.get(i).getOrderList().get(j).setOrderStatusStr("审核通过");
//				}else if("2".equals(list.get(i).getOrderList().get(j).getOrderStatus())){
//					list.get(i).getOrderList().get(j).setOrderStatusStr("审核不通过");
//				}else if("3".equals(list.get(i).getOrderList().get(j).getOrderStatus())){
//					list.get(i).getOrderList().get(j).setOrderStatusStr("已发货");
//				}else if("4".equals(list.get(i).getOrderList().get(j).getOrderStatus())){
//					list.get(i).getOrderList().get(j).setOrderStatusStr("已完成");
//				}else if("5".equals(list.get(i).getOrderList().get(j).getOrderStatus())){
//					list.get(i).getOrderList().get(j).setOrderStatusStr("已退货");
//				}else if("6".equals(list.get(i).getOrderList().get(j).getOrderStatus())){
//					list.get(i).getOrderList().get(j).setOrderStatusStr("驳回");
//				}else{
//					list.get(i).getOrderList().get(j).setOrderStatusStr("");
//				}
//			}
//		}
        return list;
    }


    @Override
    //@Cacheable
    public List<MshDemandListDto> queryAll(MshDemandListQueryCriteria criteria) {
        return selectMshDemandListList(criteria);
    }


    @Override
    public void download(List<MshDemandListDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (MshDemandListDto mshDemandList : all) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("MSH Member ID", mshDemandList.getMemberId());
            map.put("患者姓名", mshDemandList.getPatientname());
            map.put("手机号", mshDemandList.getPhone());
            map.put("省", mshDemandList.getProvince());
            map.put("市", mshDemandList.getCity());
            map.put("区", mshDemandList.getDistrict());
            map.put("详细地址", mshDemandList.getDetail());
            map.put("添加时间", mshDemandList.getCreateTime());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }


    @Override
    public ServiceResult<Boolean> createmshDemandList(JSONObject jsonObject) {
        ServiceResult<Boolean> serviceResult = new ServiceResult<>();
        String jsonStr = jsonObject.toString();
        MshDemandListForCreateDto mshDemandListForCreateDto = JSONObject.parseObject(jsonStr, MshDemandListForCreateDto.class);
        //患者表信息
        MshPatientInformation mshPatientInformation = mshDemandListForCreateDto.getMshPatientInformation();
        //需求单附件表其他
        List<MshDemandListFile> mshDemandListFileList = mshDemandListForCreateDto.getMshDemandListFileList();
        //需求单附件表申请表
        List<MshDemandListFile> mshDemandListFileListApplication = mshDemandListForCreateDto.getMshDemandListFileListApplication();
        //需求单附件表处方照片 （医疗文件照片）
        List<MshDemandListFile> mshDemandListFileListPicUrl = mshDemandListForCreateDto.getMshDemandListFileListPicUrl();
        //需求单患者信息附件表
        List<MshPatientListFile> mshPatientListFileList = mshDemandListForCreateDto.getMshPatientListFileList();
        //需求单子表
        List<MshDemandListItem> mshDemandListItemList = mshDemandListForCreateDto.getMshDemandListItemList();

//        //获取省市区的name
//        QueryWrapper<MdCountry> queryWrapper = new QueryWrapper<>();
//        queryWrapper.eq("CODE", mshPatientInformation.getProvinceCode());
//        String province = mdCountryMapper.selectList(queryWrapper).get(0).getName();
//
//        queryWrapper = new QueryWrapper<>();
//        queryWrapper.eq("CODE", mshPatientInformation.getCityCode());
//        String city = mdCountryMapper.selectList(queryWrapper).get(0).getName();
//
//        queryWrapper = new QueryWrapper<>();
//        queryWrapper.eq("CODE", mshPatientInformation.getDistrictCode());
//        String district = mdCountryMapper.selectList(queryWrapper).get(0).getName();

        //获取省市区的name
        String province = null;
        String city = null;
        String district = null;
        QueryWrapper<MdCountry> queryWrapper = null;
        if (mshPatientInformation.getProvinceCode() != null && !("".equals(mshPatientInformation.getProvinceCode()))) {
            queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("CODE", mshPatientInformation.getProvinceCode());
            province = mdCountryMapper.selectList(queryWrapper).get(0).getName();
        }
        if (mshPatientInformation.getCityCode() != null && !("".equals(mshPatientInformation.getCityCode()))) {
            queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("CODE", mshPatientInformation.getCityCode());
            city = mdCountryMapper.selectList(queryWrapper).get(0).getName();
        }
        if (mshPatientInformation.getDistrictCode() != null && !("".equals(mshPatientInformation.getDistrictCode()))) {
            queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("CODE", mshPatientInformation.getDistrictCode());
            district = mdCountryMapper.selectList(queryWrapper).get(0).getName();
        }

        //校验是否存在该手机号，存在即更新，不存在插入
        MshPatientInformation mpi = mshPatientInformationService.getOne(new QueryWrapper<MshPatientInformation>().eq("phone", mshPatientInformation.getPhone()).eq("delete_status", 0));
        if (mpi != null) {
            //更新患者表信息
            mshPatientInformation.setProvince(province);
            mshPatientInformation.setCity(city);
            mshPatientInformation.setDistrict(district);
            mshPatientInformation.setId(mpi.getId());
            mshPatientInformationService.updateById(mshPatientInformation);
        } else {
            //插入患者表信息
            mshPatientInformation.setProvince(province);
            mshPatientInformation.setCity(city);
            mshPatientInformation.setDistrict(district);
            mshPatientInformation.setAddTime(DateUtil.date().toTimestamp());
            boolean patientFlag = mshPatientInformationService.save(mshPatientInformation);
            if (!patientFlag) {
                serviceResult.setOk(false);
                serviceResult.setMsg("插入患者表信息失败！");
                return serviceResult;
            }
        }

        //患者附件信息表全删全插
        if (mshPatientListFileList.get(0).getPatientId() != null) {
            QueryWrapper<MshPatientListFile> deleteQueryWrapper = new QueryWrapper<>();
            deleteQueryWrapper.eq("patient_id", mshPatientListFileList.get(0).getPatientId());
            mshPatientListFileMapper.delete(deleteQueryWrapper);
        }
        //插入患者附件表信息
        for (int i = 0; i < mshPatientListFileList.size(); i++) {
            mshPatientListFileList.get(i).setPatientId(mshPatientInformation.getId());
            mshPatientListFileList.get(i).setType("身份证或护照");
            int num = mshPatientListFileMapper.insert(mshPatientListFileList.get(i));
            if (num == 0) {
                serviceResult.setOk(false);
                serviceResult.setMsg("插入需求单患者附件信息失败！");
                return serviceResult;
            }
        }

        String username = SecurityUtils.getUsername();

        Long maxId = mshDemandListMapper.findMaxId(dateFormat.format(new Date()));
        String demandNo = "";
        if (maxId != null) {
            maxId=maxId+1;
            demandNo = "XXSY" +maxId;
        } else {
            demandNo = "XXSY" + dateFormat.format(new Date()) + String.format("%05d", 1);
        }

        //插入需求单主表信息
        MshDemandList mshDemandList = new MshDemandList();
        mshDemandList.setPatientname(mshPatientInformation.getPatientname());
        mshDemandList.setPhone(mshPatientInformation.getPhone());
        if (mshDemandListFileListPicUrl.size() > 0) {
            mshDemandList.setPicUrl(mshDemandListFileListPicUrl.get(0).getFileUrl());
        }
        mshDemandList.setProvince(province);
        mshDemandList.setCity(city);
        mshDemandList.setDistrict(district);
        mshDemandList.setProvinceCode(mshPatientInformation.getProvinceCode());
        mshDemandList.setCityCode(mshPatientInformation.getCityCode());
        mshDemandList.setDistrictCode(mshPatientInformation.getDistrictCode());
        mshDemandList.setDetail(mshPatientInformation.getDetail());
        mshDemandList.setCreateTime(DateUtil.date().toTimestamp());
        mshDemandList.setPatientId(mshPatientInformation.getId());
        mshDemandList.setSaveStatus(1);

        mshDemandList.setMemberId(mshPatientInformation.getMemberId());
        mshDemandList.setSource("线下");
        mshDemandList.setDemandNo(demandNo);
        mshDemandList.setCreateUser(username);
        mshDemandList.setCompany(mshPatientInformation.getCompany());
        mshDemandList.setVip(mshPatientInformation.getVip());
        mshDemandList.setPerCustoService(mshPatientInformation.getPerCustoService());
        mshDemandList.setPerCustoServiceEmail(mshPatientInformation.getPerCustoServiceEmail());
        mshDemandList.setPatientEmail(mshPatientInformation.getPatientEmail());
        mshDemandList.setDiseaseName(mshPatientInformation.getDiseaseName());
        mshDemandList.setFileHospital(mshPatientInformation.getFileHospital());
        mshDemandList.setFileDate(mshPatientInformation.getFileDate());
        mshDemandList.setReceivingName(mshPatientInformation.getReceivingName());
        mshDemandList.setRelationship(mshPatientInformation.getRelationship());
        mshDemandList.setReceivingPhone(mshPatientInformation.getReceivingPhone());


        int num = mshDemandListMapper.insert(mshDemandList);
        if (num == 0) {
            serviceResult.setOk(false);
            serviceResult.setMsg("插入需求单主表信息失败！");
            return serviceResult;
        }

        //插入需求单附件表信息
        for (int i = 0; i < mshDemandListFileList.size(); i++) {
            mshDemandListFileList.get(i).setDemandListId(mshDemandList.getId());
            mshDemandListFileList.get(i).setType("其他");
            boolean mshDemandListFileFlag = mshDemandListFileService.save(mshDemandListFileList.get(i));
            if (!mshDemandListFileFlag) {
                serviceResult.setOk(false);
                serviceResult.setMsg("插入需求单附件表信息失败！");
                return serviceResult;
            }
        }

        //插入需求单附件表信息
        for (int i = 0; i < mshDemandListFileListApplication.size(); i++) {
            mshDemandListFileListApplication.get(i).setDemandListId(mshDemandList.getId());
            mshDemandListFileListApplication.get(i).setType("申请表");
            boolean mshDemandListFileFlag = mshDemandListFileService.save(mshDemandListFileListApplication.get(i));
            if (!mshDemandListFileFlag) {
                serviceResult.setOk(false);
                serviceResult.setMsg("插入需求单附件表信息失败！");
                return serviceResult;
            }
        }

        //插入需求单附件表信息
        for (int i = 0; i < mshDemandListFileListPicUrl.size(); i++) {
            mshDemandListFileListPicUrl.get(i).setDemandListId(mshDemandList.getId());
            mshDemandListFileListPicUrl.get(i).setType("医疗文件照片");
            boolean mshDemandListFileFlag = mshDemandListFileService.save(mshDemandListFileListPicUrl.get(i));
            if (!mshDemandListFileFlag) {
                serviceResult.setOk(false);
                serviceResult.setMsg("插入需求单附件表信息失败！");
                return serviceResult;
            }
        }

        //插入需求单详细表信息
		for(int i = 0; i<mshDemandListItemList.size(); i++){
			mshDemandListItemList.get(i).setDemandListId(mshDemandList.getId());
			boolean mshDemandListItemFlag = mshDemandListItemService.save(mshDemandListItemList.get(i));
			if(!mshDemandListItemFlag){
				serviceResult.setOk(false);
				serviceResult.setMsg("插入需求单详细表信息失败！");
				return serviceResult;
			}
		}

        serviceResult.setOk(true);
        return serviceResult;
    }

    @Override
    public ServiceResult<Boolean> createmshDemandListForSave(JSONObject jsonObject) {
        ServiceResult<Boolean> serviceResult = new ServiceResult<>();
        String jsonStr = jsonObject.toString();
        MshDemandListForCreateDto mshDemandListForCreateDto = JSONObject.parseObject(jsonStr, MshDemandListForCreateDto.class);
        //患者表信息
        MshPatientInformation mshPatientInformation = mshDemandListForCreateDto.getMshPatientInformation();
        //需求单附件表其他
        List<MshDemandListFile> mshDemandListFileList = mshDemandListForCreateDto.getMshDemandListFileList();
        //需求单附件表申请表
        List<MshDemandListFile> mshDemandListFileListApplication = mshDemandListForCreateDto.getMshDemandListFileListApplication();
        //需求单附件表处方照片 （医疗文件照片）
        List<MshDemandListFile> mshDemandListFileListPicUrl = mshDemandListForCreateDto.getMshDemandListFileListPicUrl();
        //需求单患者信息附件表
        List<MshPatientListFile> mshPatientListFileList = mshDemandListForCreateDto.getMshPatientListFileList();
        //需求单子表
        List<MshDemandListItem> mshDemandListItemList = mshDemandListForCreateDto.getMshDemandListItemList();

        //新增或修改FLAG
        String flag = mshDemandListForCreateDto.getFlag();
        //需求单ID
        Integer id = mshDemandListForCreateDto.getId();
        //保存状态
        Integer saveStatus = mshDemandListForCreateDto.getSaveStatus();

        //获取省市区的name
        String province = null;
        String city = null;
        String district = null;
        QueryWrapper<MdCountry> queryWrapper = null;
        if (mshPatientInformation.getProvinceCode() != null && !("".equals(mshPatientInformation.getProvinceCode()))) {
            queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("CODE", mshPatientInformation.getProvinceCode());
            province = mdCountryMapper.selectList(queryWrapper).get(0).getName();
        }
        if (mshPatientInformation.getCityCode() != null && !("".equals(mshPatientInformation.getCityCode()))) {
            queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("CODE", mshPatientInformation.getCityCode());
            city = mdCountryMapper.selectList(queryWrapper).get(0).getName();
        }
        if (mshPatientInformation.getDistrictCode() != null && !("".equals(mshPatientInformation.getDistrictCode()))) {
            queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("CODE", mshPatientInformation.getDistrictCode());
            district = mdCountryMapper.selectList(queryWrapper).get(0).getName();
        }
        //判断新增还是修改逻辑
        if ("0".equals(flag)) {
            //新增
            //校验是否存在该手机号，存在即更新，不存在插入
            MshPatientInformation mpi = null;
            if (mshPatientInformation.getPhone() == null || "".equals(mshPatientInformation.getPhone())) {
                mpi = null;
            } else {
                mpi = mshPatientInformationService.getOne(new QueryWrapper<MshPatientInformation>().eq("phone", mshPatientInformation.getPhone()).eq("delete_status", 0));
            }

            //判断手机号是否为空,为空时插入一条新的患者数据,不为空需要校验手机是否存在
            if (StringUtils.isEmpty(mshPatientInformation.getPhone())) {
                //插入患者表信息
                mshPatientInformation.setProvince(province);
                mshPatientInformation.setCity(city);
                mshPatientInformation.setDistrict(district);
                mshPatientInformation.setAddTime(DateUtil.date().toTimestamp());
                boolean patientFlag = mshPatientInformationService.save(mshPatientInformation);
                if (!patientFlag) {
                    serviceResult.setOk(false);
                    serviceResult.setMsg("插入患者表信息失败！");
                    return serviceResult;
                }
            } else {
                if (mpi != null) {
                    //更新患者表信息
                    mshPatientInformation.setProvince(province);
                    mshPatientInformation.setCity(city);
                    mshPatientInformation.setDistrict(district);
                    mshPatientInformation.setId(mpi.getId());
                    mshPatientInformationService.updateById(mshPatientInformation);
                } else {
                    //插入患者表信息
                    mshPatientInformation.setProvince(province);
                    mshPatientInformation.setCity(city);
                    mshPatientInformation.setDistrict(district);
                    mshPatientInformation.setAddTime(DateUtil.date().toTimestamp());
                    boolean patientFlag = mshPatientInformationService.save(mshPatientInformation);
                    if (!patientFlag) {
                        serviceResult.setOk(false);
                        serviceResult.setMsg("插入患者表信息失败！");
                        return serviceResult;
                    }
                }
            }

            //患者附件信息表全删全插
            if (mpi != null) {
                QueryWrapper<MshPatientListFile> deleteQueryWrapper = new QueryWrapper<>();
                deleteQueryWrapper.eq("patient_id", mpi.getId());
                mshPatientListFileMapper.delete(deleteQueryWrapper);
            }
            //插入患者附件表信息
            for (int i = 0; i < mshPatientListFileList.size(); i++) {
                mshPatientListFileList.get(i).setPatientId(mshPatientInformation.getId());
                mshPatientListFileList.get(i).setType("身份证或护照");
                int num = mshPatientListFileMapper.insert(mshPatientListFileList.get(i));
                if (num == 0) {
                    serviceResult.setOk(false);
                    serviceResult.setMsg("插入需求单患者附件信息失败！");
                    return serviceResult;
                }
            }
            String username = SecurityUtils.getUsername();

            Long maxId = mshDemandListMapper.findMaxId(dateFormat.format(new Date()));
            String demandNo = "";
            if (maxId != null) {
                maxId=maxId+1;
                demandNo = "XXSY" +maxId;
            } else {
                demandNo = "XXSY" + dateFormat.format(new Date()) + String.format("%05d", 1);
            }

            //插入需求单主表信息
            MshDemandList mshDemandList = new MshDemandList();
            mshDemandList.setPatientname(mshPatientInformation.getPatientname());
            mshDemandList.setPhone(mshPatientInformation.getPhone());
            if (mshDemandListFileListPicUrl.size() > 0) {
                mshDemandList.setPicUrl(mshDemandListFileListPicUrl.get(0).getFileUrl());
            }
            mshDemandList.setProvince(province);
            mshDemandList.setCity(city);
            mshDemandList.setDistrict(district);
            mshDemandList.setProvinceCode(mshPatientInformation.getProvinceCode());
            mshDemandList.setCityCode(mshPatientInformation.getCityCode());
            mshDemandList.setDistrictCode(mshPatientInformation.getDistrictCode());
            mshDemandList.setDetail(mshPatientInformation.getDetail());
            mshDemandList.setCreateTime(DateUtil.date().toTimestamp());
            mshDemandList.setPatientId(mshPatientInformation.getId());
            mshDemandList.setSaveStatus(saveStatus);
            mshDemandList.setMemberId(mshPatientInformation.getMemberId());
            mshDemandList.setSource("线下");
            mshDemandList.setDemandNo(demandNo);
            mshDemandList.setCreateUser(username);
            mshDemandList.setCompany(mshPatientInformation.getCompany());
            mshDemandList.setVip(mshPatientInformation.getVip());
            mshDemandList.setPerCustoService(mshPatientInformation.getPerCustoService());
            mshDemandList.setPerCustoServiceEmail(mshPatientInformation.getPerCustoServiceEmail());
            mshDemandList.setPatientEmail(mshPatientInformation.getPatientEmail());
            mshDemandList.setDiseaseName(mshPatientInformation.getDiseaseName());
            mshDemandList.setFileHospital(mshPatientInformation.getFileHospital());
            mshDemandList.setFileDate(mshPatientInformation.getFileDate());
            mshDemandList.setReceivingName(mshPatientInformation.getReceivingName());
            mshDemandList.setRelationship(mshPatientInformation.getRelationship());
            mshDemandList.setReceivingPhone(mshPatientInformation.getReceivingPhone());

            int num = mshDemandListMapper.insert(mshDemandList);
            if (num == 0) {
                serviceResult.setOk(false);
                serviceResult.setMsg("插入需求单主表信息失败！");
                return serviceResult;
            }

            //插入需求单附件表信息
            for (int i = 0; i < mshDemandListFileList.size(); i++) {
                mshDemandListFileList.get(i).setDemandListId(mshDemandList.getId());
                mshDemandListFileList.get(i).setType("其他");

                boolean mshDemandListFileFlag = mshDemandListFileService.save(mshDemandListFileList.get(i));
                if (!mshDemandListFileFlag) {
                    serviceResult.setOk(false);
                    serviceResult.setMsg("插入需求单附件表信息失败！");
                    return serviceResult;
                }
            }

            //插入需求单附件表信息
            for (int i = 0; i < mshDemandListFileListApplication.size(); i++) {
                mshDemandListFileListApplication.get(i).setDemandListId(mshDemandList.getId());
                mshDemandListFileListApplication.get(i).setType("申请表");
                boolean mshDemandListFileFlag = mshDemandListFileService.save(mshDemandListFileListApplication.get(i));
                if (!mshDemandListFileFlag) {
                    serviceResult.setOk(false);
                    serviceResult.setMsg("插入需求单附件表信息失败！");
                    return serviceResult;
                }
            }

            //插入需求单附件表信息
            for (int i = 0; i < mshDemandListFileListPicUrl.size(); i++) {
                mshDemandListFileListPicUrl.get(i).setDemandListId(mshDemandList.getId());
                mshDemandListFileListPicUrl.get(i).setType("医疗文件照片");
                boolean mshDemandListFileFlag = mshDemandListFileService.save(mshDemandListFileListPicUrl.get(i));
                if (!mshDemandListFileFlag) {
                    serviceResult.setOk(false);
                    serviceResult.setMsg("插入需求单附件表信息失败！");
                    return serviceResult;
                }
            }

            //插入需求单详细表信息
            for(int i = 0; i<mshDemandListItemList.size(); i++){
                mshDemandListItemList.get(i).setDemandListId(mshDemandList.getId());
                boolean mshDemandListItemFlag = mshDemandListItemService.save(mshDemandListItemList.get(i));
                if(!mshDemandListItemFlag){
                    serviceResult.setOk(false);
                    serviceResult.setMsg("插入需求单详细表信息失败！");
                    return serviceResult;
                }
            }
            serviceResult.setOk(true);
            return serviceResult;
        } else {
            //修改
            //查询上次录入的数据手机号是否为空
            MshPatientInformation mpi1 = mshPatientInformationService.getOne(new QueryWrapper<MshPatientInformation>().eq("id", mshPatientInformation.getId()).eq("delete_status", 0));
            //查询当前手机号是否存在
            MshPatientInformation mpi2 = null;
            if (mshPatientInformation.getPhone() == null || "".equals(mshPatientInformation.getPhone())) {
                mpi2 = null;
            } else {
                mpi2 = mshPatientInformationService.getOne(new QueryWrapper<MshPatientInformation>().eq("phone", mshPatientInformation.getPhone()).eq("delete_status", 0));
            }
            //判断当前传入手机号是否为空
            if (mshPatientInformation.getPhone() == null || "".equals(mshPatientInformation.getPhone())) {
                //当前传入手机号为空
                //判断上次手机号是否为空
                if (mpi1 != null && (mpi1.getPhone() == null || "".equals(mpi1.getPhone()))) {
                    //更新患者表信息
                    mshPatientInformation.setProvince(province);
                    mshPatientInformation.setCity(city);
                    mshPatientInformation.setDistrict(district);
                    mshPatientInformation.setId(mpi1.getId());
                    mshPatientInformationService.updateById(mshPatientInformation);
                } else {
                    //插入患者表信息
                    mshPatientInformation.setId(null);
                    mshPatientInformation.setProvince(province);
                    mshPatientInformation.setCity(city);
                    mshPatientInformation.setDistrict(district);
                    mshPatientInformation.setAddTime(DateUtil.date().toTimestamp());
                    boolean patientFlag = mshPatientInformationService.save(mshPatientInformation);
                    if (!patientFlag) {
                        serviceResult.setOk(false);
                        serviceResult.setMsg("插入患者表信息失败！");
                        return serviceResult;
                    }
                }
            } else {
                //当前传入手机号不为空
                //判断上次手机号是否为空
                if (mpi1 != null && (mpi1.getPhone() == null || "".equals(mpi1.getPhone()))) {
                    //上次录入的手机号为空
                    if (mpi2 != null) {
                        //更新患者表信息
                        mshPatientInformation.setProvince(province);
                        mshPatientInformation.setCity(city);
                        mshPatientInformation.setDistrict(district);
                        mshPatientInformation.setId(mpi2.getId());
                        mshPatientInformationService.updateById(mshPatientInformation);
                        //删除之前插入的空的患者数据
                        QueryWrapper<MshPatientInformation> deleteQueryWrapper = new QueryWrapper<>();
                        deleteQueryWrapper.eq("id", mpi1.getId());
                        mshPatientInformationMapper.delete(deleteQueryWrapper);
                    } else {
                        //更新患者表信息
                        mshPatientInformation.setProvince(province);
                        mshPatientInformation.setCity(city);
                        mshPatientInformation.setDistrict(district);
                        mshPatientInformation.setId(mpi1.getId());
                        mshPatientInformationService.updateById(mshPatientInformation);
                    }
                } else {
                    if (mpi2 != null) {
                        //更新患者表信息
                        mshPatientInformation.setProvince(province);
                        mshPatientInformation.setCity(city);
                        mshPatientInformation.setDistrict(district);
                        mshPatientInformation.setId(mpi2.getId());
                        mshPatientInformationService.updateById(mshPatientInformation);
                    } else {
                        //插入患者表信息
                        mshPatientInformation.setId(null);
                        mshPatientInformation.setProvince(province);
                        mshPatientInformation.setCity(city);
                        mshPatientInformation.setDistrict(district);
                        mshPatientInformation.setAddTime(DateUtil.date().toTimestamp());
                        boolean patientFlag = mshPatientInformationService.save(mshPatientInformation);
                        if (!patientFlag) {
                            serviceResult.setOk(false);
                            serviceResult.setMsg("插入患者表信息失败！");
                            return serviceResult;
                        }
                    }
                }
            }

            //更新需求单主表信息
            MshDemandList mshDemandList = new MshDemandList();
            mshDemandList.setId(id);
            mshDemandList.setPatientId(mshPatientInformation.getId());
            mshDemandList.setSaveStatus(saveStatus);

            //线下开单且 保存状态为保存状态
//            if (!(StringUtils.isNotEmpty(mshDemandListForCreateDto.getSource()) &&
//                    (mshDemandListForCreateDto.getSource().equals("APP")
//                            || mshDemandListForCreateDto.getSource().equals("Wechat"))) && (saveStatus == 0 || saveStatus==1)) {
//            }
//
//            if (saveStatus == 0 || saveStatus == 3 || saveStatus==1) {
                mshDemandList.setPatientname(mshPatientInformation.getPatientname());
                mshDemandList.setPhone(mshPatientInformation.getPhone());
                mshDemandList.setMemberId(mshPatientInformation.getMemberId());
                mshDemandList.setCompany(mshPatientInformation.getCompany());
                mshDemandList.setVip(mshPatientInformation.getVip());
                mshDemandList.setPatientEmail(mshPatientInformation.getPatientEmail());
                mshDemandList.setDiseaseName(mshPatientInformation.getDiseaseName());
                mshDemandList.setFileHospital(mshPatientInformation.getFileHospital());
                mshDemandList.setFileDate(mshPatientInformation.getFileDate());

                mshDemandList.setPerCustoService(mshPatientInformation.getPerCustoService());
                mshDemandList.setPerCustoServiceEmail(mshPatientInformation.getPerCustoServiceEmail());
                mshDemandList.setReceivingName(mshPatientInformation.getReceivingName());
                mshDemandList.setRelationship(mshPatientInformation.getRelationship());
                mshDemandList.setReceivingPhone(mshPatientInformation.getReceivingPhone());
                mshDemandList.setDetail(mshPatientInformation.getDetail());
                mshDemandList.setProvince(province);
                mshDemandList.setCity(city);
                mshDemandList.setDistrict(district);
                mshDemandList.setProvinceCode(mshPatientInformation.getProvinceCode());
                mshDemandList.setCityCode(mshPatientInformation.getCityCode());
                mshDemandList.setDistrictCode(mshPatientInformation.getDistrictCode());
//            }
            mshDemandList.setUpdateTime(new Date());
            mshDemandListMapper.updateById(mshDemandList);

            //对需求单附件表，患者附件表，需求单详细表信息进行全删全插
            //删除需求单详细表信息
            QueryWrapper<MshDemandListItem> deleteQueryWrapper = new QueryWrapper<>();
            deleteQueryWrapper.eq("demand_list_id", id);
            mshDemandListItemMapper.delete(deleteQueryWrapper);

            //插入需求单详细表信息
            for(int i = 0; i<mshDemandListItemList.size(); i++){
                mshDemandListItemList.get(i).setDemandListId(id);
                log.info("mshDemandListItemList:"+ JSON.toJSONString(mshDemandListItemList.get(i)));
                boolean mshDemandListItemFlag = mshDemandListItemService.save(mshDemandListItemList.get(i));
                if(!mshDemandListItemFlag){
                    serviceResult.setOk(false);
                    serviceResult.setMsg("插入需求单详细表信息失败！");
                    return serviceResult;
                }
            }
            //删除需求单附件表
            QueryWrapper<MshDemandListFile> deleteQueryWrapper2 = new QueryWrapper<>();
            deleteQueryWrapper2.eq("demand_list_id", id);
            mshDemandListFileMapper.delete(deleteQueryWrapper2);

            //插入需求单附件表信息
            for (int i = 0; i < mshDemandListFileList.size(); i++) {
                mshDemandListFileList.get(i).setDemandListId(id);
                mshDemandListFileList.get(i).setType("其他");
                boolean mshDemandListFileFlag = mshDemandListFileService.save(mshDemandListFileList.get(i));
                if (!mshDemandListFileFlag) {
                    serviceResult.setOk(false);
                    serviceResult.setMsg("插入需求单附件表信息失败！");
                    return serviceResult;
                }
            }

            //插入需求单附件表信息
            for (int i = 0; i < mshDemandListFileListApplication.size(); i++) {
                mshDemandListFileListApplication.get(i).setDemandListId(mshDemandList.getId());
                mshDemandListFileListApplication.get(i).setType("申请表");
                boolean mshDemandListFileFlag = mshDemandListFileService.save(mshDemandListFileListApplication.get(i));
                if (!mshDemandListFileFlag) {
                    serviceResult.setOk(false);
                    serviceResult.setMsg("插入需求单附件表信息失败！");
                    return serviceResult;
                }
            }

            //插入需求单附件表信息
            for (int i = 0; i < mshDemandListFileListPicUrl.size(); i++) {
                mshDemandListFileListPicUrl.get(i).setDemandListId(mshDemandList.getId());
                mshDemandListFileListPicUrl.get(i).setType("医疗文件照片");
                boolean mshDemandListFileFlag = mshDemandListFileService.save(mshDemandListFileListPicUrl.get(i));
                if (!mshDemandListFileFlag) {
                    serviceResult.setOk(false);
                    serviceResult.setMsg("插入需求单附件表信息失败！");
                    return serviceResult;
                }
            }

            //删除患者附件信息
            QueryWrapper<MshPatientListFile> deleteQueryWrapper3 = new QueryWrapper<>();
            deleteQueryWrapper3.eq("patient_id", mpi1.getId());
            mshPatientListFileMapper.delete(deleteQueryWrapper3);

            //插入患者附件表信息
            for (int i = 0; i < mshPatientListFileList.size(); i++) {
                mshPatientListFileList.get(i).setPatientId(mshPatientInformation.getId());
                mshPatientListFileList.get(i).setType("身份证或护照");
                int num = mshPatientListFileMapper.insert(mshPatientListFileList.get(i));
                if (num == 0) {
                    serviceResult.setOk(false);
                    serviceResult.setMsg("插入需求单患者附件信息失败！");
                    return serviceResult;
                }
            }
        }
        serviceResult.setOk(true);
        return serviceResult;
    }


    @Override
    public ServiceResult<Integer> checkDeleteById(Integer[] ids) {
        ServiceResult<Integer> serviceResult = new ServiceResult<>();
        //查询订单表中是否存在该需求单
        for (Integer it : ids) {
            QueryWrapper<MshOrder> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("demand_list_id", it);
            List<MshOrder> mshOrderList = mshOrderMapper.selectList(queryWrapper);
            if (mshOrderList != null && mshOrderList.size() > 0) {
                serviceResult.setOk(false);
                serviceResult.setData(it);
                serviceResult.setMsg("需求单号" + it + "已生成订单");
                return serviceResult;
            }
        }
        serviceResult.setOk(true);
        return serviceResult;
    }


    @Override
    public ServiceResult<Boolean> deleteById(Integer[] ids) {
        ServiceResult<Boolean> serviceResult = new ServiceResult<>();
        //删除需求单主表及子表信息
        for (Integer it : ids) {
            //需求单主表
            mshDemandListMapper.deleteById(it);
            //需求单子表
            QueryWrapper<MshDemandListItem> queryWrapper1 = new QueryWrapper<>();
            queryWrapper1.eq("demand_list_id", it);
            mshDemandListItemMapper.delete(queryWrapper1);
            //需求单附件表
            QueryWrapper<MshDemandListFile> queryWrapper2 = new QueryWrapper<>();
            queryWrapper2.eq("demand_list_id", it);
            mshDemandListFileMapper.delete(queryWrapper2);
        }
        serviceResult.setOk(true);
        return serviceResult;
    }

    @Override
    public Map<String, Object> getMshDemandListdDetails(Integer id) {
        Map<String, Object> map = new LinkedHashMap<>(5);

        MshDemandList mshDemandList = mshDemandListMapper.findById(id);
        map.put("MshDemandListInfo", mshDemandList);
        //需求单附件信息
        QueryWrapper<MshDemandListFile> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("demand_list_id", id);
        queryWrapper.orderByAsc("type");
        map.put("MshDemandListFileList", mshDemandListFileMapper.selectList(queryWrapper));

        //查询患者相关信息
        QueryWrapper<MshPatientInformation> queryWrapper3 = new QueryWrapper<>();
        queryWrapper3.eq("id", mshDemandList.getPatientId());
        List<MshPatientInformation> list3 = mshPatientInformationMapper.selectList(queryWrapper3);
        map.put("MshPatientInformation", list3);

        QueryWrapper<MshPatientListFile> queryWrapper4 = new QueryWrapper<>();
        if (list3 != null && list3.size() > 0) {
            queryWrapper4.eq("patient_id", list3.get(0).getId());
            map.put("MshPatientListFileList", mshPatientListFileMapper.selectList(queryWrapper4));
        } else {
            map.put("MshPatientListFileList", null);
        }

        //查询需求单明细
        QueryWrapper<MshDemandListItem> queryWrapper5 = new QueryWrapper<>();
        queryWrapper5.eq("demand_list_id", id);
        List<MshDemandListItem> list4 = mshDemandListItemMapper.selectList(queryWrapper5);
        map.put("mshDemandListItemList", list4);


        YxSystemStore yxSystemStore = yxSystemStoreService.getOne(new LambdaQueryWrapper<YxSystemStore>().eq(YxSystemStore::getName, ShopConstants.STORENAME_GUANGZHOU_CLOUD), false);
        map.put("storeInfo", yxSystemStore);
        return map;
    }

    @Override
    public MshDemandListAuditDto getMshDemandListAuditInfo(Integer id) {
        MshDemandList mshDemandList = mshDemandListMapper.findById(id);
        MshDemandListAuditDto mshDemandListDto = generator.convert(mshDemandList, MshDemandListAuditDto.class);
        List<MshOrderDto> mshOrderDtos = mshOrderMapper.getMshOrderByDemandListId(id);

        List<MshOrderAuditDto> mshOrderAuditDtos = generator.convert(mshOrderDtos, MshOrderAuditDto.class);
        for (MshOrderAuditDto mshOrderDto : mshOrderAuditDtos) {
            if ("0".equals(mshOrderDto.getOrderStatus())) {
                mshOrderDto.setOrderStatusStr("");
            } else if ("1".equals(mshOrderDto.getOrderStatus())) {
                mshOrderDto.setOrderStatusStr("审核通过");
            } else if ("2".equals(mshOrderDto.getOrderStatus())) {
                mshOrderDto.setOrderStatusStr("审核不通过");
            } else if ("3".equals(mshOrderDto.getOrderStatus())) {
                mshOrderDto.setOrderStatusStr("审核通过");
            } else if ("4".equals(mshOrderDto.getOrderStatus())) {
                mshOrderDto.setOrderStatusStr("审核通过");
            } else if ("5".equals(mshOrderDto.getOrderStatus())) {
                mshOrderDto.setOrderStatusStr("审核通过");
            } else if ("6".equals(mshOrderDto.getOrderStatus())) {
                mshOrderDto.setOrderStatusStr("驳回");
            } else {
                mshOrderDto.setOrderStatusStr("");
            }

        }
        mshDemandListDto.setMshOrderAuditDtos(mshOrderAuditDtos);
        return mshDemandListDto;
    }

    @Override
    public void updateMshDemandList(MshDemandList resources) {
        MshDemandList mshDemandList=  mshDemandListMapper.selectById(resources.getId());
        if(("APP".equals(mshDemandList.getSource()) || "Wechat".equals(mshDemandList.getSource())) && mshDemandList.getAuditStatus().equals(MshStatusEnum.AuditStatus.QX.getCode()) ){
            throw  new BadRequestException("需求单来源为："+mshDemandList.getSource()+",不能取消。");
        }
        if(resources.getAuditStatus()!=null && (resources.getAuditStatus()==1 || resources.getAuditStatus()==2 || resources.getAuditStatus()==6)){
            String username = SecurityUtils.getUsername();
            resources.setAuditName(username);
            resources.setAuditTime(new Timestamp(System.currentTimeMillis()));
        }
        resources.setUpdateTime(new Date());
        mshDemandListMapper.updateById(resources);
        if (resources.getSaveStatus()==2) {
            if(resources.getAuditStatus()==1){
                List<MshOrderDto> list= mshOrderMapper.getMshOrderByDemandListId(resources.getId());
                if(!CollectionUtils.isEmpty(list)){
                    for (MshOrderDto mshOrderDto : list) {
                        if(mshOrderDto.getOrderStatus().equals("6")){
//                        if(ShopConstants.STORENAME_GUANGZHOU_CLOUD.equals(mshOrderDto.getDrugstoreName())){
                            //设置处方字段
                            String prescriptionNo = mshOrderDto.getId() + "_msh";

                            Prescription pres = new Prescription();
                            pres.setOrderId(mshOrderDto.getYiyaobaoId());
                            // 收货信息
                            pres.setAddress(mshDemandList.getDetail());
                            pres.setProvinceName(mshDemandList.getProvince());
                            pres.setCityName(mshDemandList.getCity());
                            pres.setDistrictName(mshDemandList.getDistrict());
                            pres.setReceiver(mshDemandList.getPatientname());
                            pres.setReceiverMobile(mshDemandList.getPhone());
                            pres.setName(mshDemandList.getPatientname());
                            pres.setHospitalName("益药商城");
                            pres.setPrescripNo(prescriptionNo);
                            String url = yiyaobao_apiUrl_external + addSingleMshUrl;

                            String requestBody = JSONUtil.parseObj(pres).toString(); //

                            try {
                                long timestamp = System.currentTimeMillis(); // 生成签名时间戳
                                Map<String, String> headers = new HashMap<String, String>();
                                headers.put("ACCESS_APPID", appId); // 设置APP
                                headers.put("ACCESS_TIMESTAMP", String.valueOf(timestamp)); // 设置签名时间戳
                                String ACCESS_SIGANATURE = AppSiganatureUtils
                                        .createSiganature(requestBody, appId, appSecret,
                                                timestamp);
                                headers.put("ACCESS_SIGANATURE", ACCESS_SIGANATURE); // 生成并设置签名
                                log.info("ACCESS_APPID={}",appId);
                                log.info("ACCESS_TIMESTAMP={}",String.valueOf(timestamp));
                                log.info("ACCESS_SIGANATURE={}",ACCESS_SIGANATURE);
                                log.info("url={}",url);
                                log.info("requestBody={}",requestBody);
                                String result = HttpUtils.postJsonHttps(url, requestBody,
                                        headers); // 发起调用
                                log.info("下发益药宝订单，结果：{}" ,result);
                                cn.hutool.json.JSONObject object = JSONUtil.parseObj(result);


                                if(object.getBool("success")) {
                                    MshOrder order= MyBeanUtils.convert(mshOrderDto, MshOrder.class);
                                    order.setOrderStatus("0");
                                    mshOrderMapper.updateById(order);

                                    YiyaobaoOrderInfo yiyaobaoOrderInfo = new YiyaobaoOrderInfo();
                                    yiyaobaoOrderInfo.setOrderSource("32");
                                    yiyaobaoOrderInfo.setPrsNo(prescriptionNo);
                                    yiyaobaoOrderInfo.setPayMethod(YiyaobaoPayMethodEnum.payMethod_21.getValue());  // 金融支付
                                    yiyaobaoOrderInfo.setPayResult("10"); // 已支付
                                    yiyaobaoOrderInfo.setPayTime(DateUtil.formatDateTime(new Date()));
                                    yiyaobaoOrderInfo.setPayType(YiyaobaoPayTypeEnum.payType_40.getValue());
                                    // 更新益药宝订单的信息
                                    yiyaobaoOrdOrderMapper.updateYiyaobaoOrderInfoByPrescripNo(yiyaobaoOrderInfo);

                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
//                    }
                    }
                }
                udpateMshDemandListAuditStatus(resources.getId().toString());
            }

            //消息队列
            mqProducer.sendDelayQueue(mshQueueName,resources.getId().toString(),2000);
        }
    }




    @Override
    public List<String> getMshDemandAllAuditPerson() {
        return mshDemandListMapper.getMshDemandAllAuditPerson();
    }

    @Override
    public List<String> getMshDemandAllVip() {
        return mshDemandListMapper.getMshDemandAllVip();
    }

    @Override
    public void sendDemandListInfo(String id) {
        MshDemandList demandList = mshDemandListMapper.selectById(id);
        if("线下".equals(demandList.getSource())){

        }else{
            List<MshOrderDto> mshOrderDtos=  mshOrderMapper.getMshOrderByDemandListId(demandList.getId());

            MshDemandListDto mshDemandListDto = generator.convert(demandList, MshDemandListDto.class);
            mshDemandListDto.setOrderList(mshOrderDtos);

            QueryWrapper<MshPatientListFile> queryWrapper4 = new QueryWrapper<>();
            queryWrapper4.eq("patient_id", demandList.getPatientId());
            List<MshPatientListFile> mshPatientListFiles= mshPatientListFileMapper.selectList(queryWrapper4);

            List<String> IdCardImages = CollectionUtils.isEmpty(mshPatientListFiles)?new ArrayList<>():mshPatientListFiles.stream().map(MshPatientListFile::getFileUrl).collect(Collectors.toList());

            QueryWrapper<MshDemandListFile> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("demand_list_id", demandList.getId());
            queryWrapper.orderByAsc("type");
            List<MshDemandListFile>  mshDemandListFiles=  mshDemandListFileMapper.selectList(queryWrapper);

            List<MshDemandListFile>  mshDemandDocumentsResultList = mshDemandListFiles.stream().filter(MshDemandListFile -> Objects.equals(MshDemandListFile.getType(),"医疗文件照片") || Objects.equals(MshDemandListFile.getType(),"病例") || Objects.equals(MshDemandListFile.getType(),"处方照片")  ).collect(Collectors.toList());
            List<String> medicalDocumentsImages = CollectionUtils.isEmpty(mshDemandDocumentsResultList)?new ArrayList<>():mshDemandDocumentsResultList.stream().map(MshDemandListFile::getFileUrl).collect(Collectors.toList());

            List<MshDemandListFile>  mshOtherImagesResultList = mshDemandListFiles.stream().filter(MshDemandListFile -> Objects.equals(MshDemandListFile.getType(),"其他")).collect(Collectors.toList());
            List<String> OtherImagesImages = CollectionUtils.isEmpty(mshOtherImagesResultList)?new ArrayList<>():mshOtherImagesResultList.stream().map(MshDemandListFile::getFileUrl).collect(Collectors.toList());

            mshDemandListDto.setIdCardImages(IdCardImages);
            mshDemandListDto.setMedicalDocumentsImages(medicalDocumentsImages);
            mshDemandListDto.setOtherImages(OtherImagesImages);

            try {
                JSONObject jsonObject=JSONObject.parseObject(MshRequestUtil.syncOrderDetail(domainName,secureKey,mshDemandListDto));
                if(jsonObject==null || !jsonObject.get("code").equals("200")){
//                 throw  new BadRequestException("同步失败");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                throw  new BadRequestException(ex.getMessage());
            }
        }

    }

    @Override
    public MshDemandList udpateMshDemandListAuditStatus(String id){
        MshDemandList demandList = mshDemandListMapper.selectById(Integer.valueOf(id));
        List<MshOrderDto> mshOrderDtos=  mshOrderMapper.getMshOrderByDemandListId(demandList.getId());

        Integer a,b,c,d,e,f,g;
        a=b=c=d=e=f=g=0;
        if(!CollectionUtils.isEmpty(mshOrderDtos)){
            List<String> reason = mshOrderDtos.stream().map(MshOrderDto::getAuditReasons).filter(x -> StringUtils.isNotEmpty(x)).collect(Collectors.toList());

            for (MshOrderDto mshOrderDto : mshOrderDtos) {
                if(Integer.valueOf(mshOrderDto.getOrderStatus()) == MshStatusEnum.OrderStatus.DSH.getCode()){
                    a++;
                }
                if(Integer.valueOf(mshOrderDto.getOrderStatus()) == MshStatusEnum.OrderStatus.SHTG.getCode()){
                    b++;
                }
                if(Integer.valueOf(mshOrderDto.getOrderStatus()) == MshStatusEnum.OrderStatus.SHBTG.getCode()){
                    c++;
                }
                if(Integer.valueOf(mshOrderDto.getOrderStatus()) == MshStatusEnum.OrderStatus.YFH.getCode()){
                    d++;
                }
                if(Integer.valueOf(mshOrderDto.getOrderStatus()) == MshStatusEnum.OrderStatus.YWC.getCode()){
                    e++;
                }
                if(Integer.valueOf(mshOrderDto.getOrderStatus()) == MshStatusEnum.OrderStatus.YTH.getCode()){
                    f++;
                }
                if(Integer.valueOf(mshOrderDto.getOrderStatus()) == MshStatusEnum.OrderStatus.BH.getCode()){
                    g++;
                }
            }
            // 需求单下所有订单都为待审核时，需求单状态=客服审核通过
            if(a>mshOrderDtos.size()){
                demandList.setAuditStatus(MshStatusEnum.AuditStatus.KFSHTG.getCode());
            }

            //无待审核有订单驳回时，需求单状态=药剂师驳回
            if(a==0 && g>0){
                demandList.setAuditStatus(MshStatusEnum.AuditStatus.BH.getCode());
                demandList.setSaveStatus(MshStatusEnum.SaveStatus.BH.getCode());
            }

            //需求单下无待审核订单无订单驳回且有通过时，需求单状态=药剂师审核通过
            if(a==0 && g==0 && (b>0 || d>0 || e>0 || f>0)){
                demandList.setAuditStatus(MshStatusEnum.AuditStatus.YJSSHTG.getCode());
            }
            //无待审核订单无订单通过且无订单驳回时，需求单状态=药剂师审核不通过
            if(c>0 && b==0 && d==0 && e==0 && f==0 && g==0 && a==0 && g==0){
                demandList.setAuditStatus(MshStatusEnum.AuditStatus.YJSSHBTG.getCode());
                if(mshOrderDtos.size()==1){
                    demandList.setCancelReason(MshStatusEnum.CancelReason.getCancelReason(mshOrderDtos.get(0).getAuditReasons())==null?mshOrderDtos.get(0).getAuditReasons():MshStatusEnum.CancelReason.getCancelReason(mshOrderDtos.get(0).getAuditReasons()).getCode());
                }else{
                    Set set = new HashSet();
                    set.addAll(reason);     // 将list所有元素添加到set中    set集合特性会自动去重复
                    reason.clear();
                    reason.addAll(set);    // 将list清空并将set中的所有元素添加到list中
                    if(reason.size()>1){
                        demandList.setCancelReason(MshStatusEnum.CancelReason.reason_1.getCode());
                    }else{
                        demandList.setCancelReason(MshStatusEnum.CancelReason.getCancelReason(mshOrderDtos.get(0).getAuditReasons())==null?mshOrderDtos.get(0).getAuditReasons():MshStatusEnum.CancelReason.getCancelReason(mshOrderDtos.get(0).getAuditReasons()).getCode());
                    }
                }
            }
            demandList.setUpdateTime(new Date());
            mshDemandListMapper.updateById(demandList);
        }
        return  demandList;
    }

    @Override
    public void reportDowload(HttpServletResponse response,MshDemandListQueryCriteria criteria) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        List<Map<String, Object>> mapList = mshDemandListMapper.reportList(criteria);
        for (Map<String, Object> map : mapList) {
            Map<String, Object> linkedHashMap = new LinkedHashMap<>();
            linkedHashMap.put("患者姓名",map.get("患者姓名"));
            linkedHashMap.put("患者地址",map.get("患者地址"));
            linkedHashMap.put("患者电话",map.get("患者电话"));
            linkedHashMap.put("MSH member id",map.get("MSH member id"));
            linkedHashMap.put("需求单号",map.get("需求单号"));
            linkedHashMap.put("需求单生成日期",map.get("需求单生成日期"));
            linkedHashMap.put("SKU",map.get("SKU"));
            linkedHashMap.put("商品名",map.get("商品名"));
            linkedHashMap.put("通用名",map.get("通用名"));
            linkedHashMap.put("药品规格",map.get("药品规格"));
            linkedHashMap.put("数量",map.get("数量"));
            linkedHashMap.put("生产厂家",map.get("生产厂家"));
            linkedHashMap.put("需求单价格",map.get("需求单价格"));
            linkedHashMap.put("销售订单号",map.get("销售订单号"));
            linkedHashMap.put("订单状态",map.get("订单状态"));
            linkedHashMap.put("发货日期",map.get("发货日期"));
            linkedHashMap.put("已发货数量",map.get("已发货数量"));
            linkedHashMap.put("快递单号",map.get("快递单号"));
            linkedHashMap.put("发票号",map.get("发票号"));
            list.add(linkedHashMap);
        }
        FileUtil.downloadExcel(list, response);
    }

    @Override
    public void addMshDemandList(MshDemandDto mshDemandDto) {
        MshDemandList mshDemandList=  mshDemandListMapper.findByDemandNo(mshDemandDto.getDemandNo());
        if(mshDemandList!=null){
            throw  new BadRequestException(HttpStatus.SERVICE_UNAVAILABLE,"报文体中demandNo:"+mshDemandDto.getDemandNo()+",在上药系统已存在。");
        }
        MshPatientInformation mshPatientInformation = new MshPatientInformation();
        mshPatientInformation.setPatientname(mshDemandDto.getPatientName());
        mshPatientInformation.setPhone(mshDemandDto.getPatientPhone());
        mshPatientInformation.setProvince(mshDemandDto.getProvinceName());
        mshPatientInformation.setCity(mshDemandDto.getCityName());
        mshPatientInformation.setDistrict(mshDemandDto.getDistrictName());
        mshPatientInformation.setProvinceCode(mshDemandDto.getProvinceCode());
        mshPatientInformation.setCityCode(mshDemandDto.getCityCode());
        mshPatientInformation.setDistrictCode(mshDemandDto.getDistrictCode());
        mshPatientInformation.setDetail(mshDemandDto.getAddress());
        mshPatientInformation.setAddTime(DateUtil.date().toTimestamp());
        mshPatientInformation.setDeleteStatus(0);
        mshPatientInformation.setMemberId(mshDemandDto.getMermberId());
        mshPatientInformation.setReceivingName(mshDemandDto.getConsigneeName());
        mshPatientInformation.setRelationship(mshDemandDto.getRelation());
        mshPatientInformation.setReceivingPhone(mshDemandDto.getConsigneePhone());
        mshPatientInformation.setCompany(mshDemandDto.getCompanyShortName());
        mshPatientInformation.setVip(mshDemandDto.getVipFlag());
        mshPatientInformation.setPerCustoService(mshDemandDto.getCustomerService());
        mshPatientInformation.setPerCustoServiceEmail(mshDemandDto.getCustomerServiceEmail());
        mshPatientInformation.setPatientEmail(mshDemandDto.getPatientEmail());
        mshPatientInformation.setDiseaseName(mshDemandDto.getDiseaseName());
        mshPatientInformation.setFileHospital(mshDemandDto.getHospitalName());
        try {
            mshPatientInformation.setFileDate(StringUtils.isEmpty(mshDemandDto.getDocumentDate())?null:dateFormatAdd.parse(mshDemandDto.getDocumentDate()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        //新增患者
        MshPatientInformation information = mshPatientInformationService.getOne(new QueryWrapper<MshPatientInformation>().eq("phone", mshDemandDto.getPatientPhone()).eq("delete_status", 0));
        if(information!=null){
            mshPatientInformation.setId(information.getId());
            mshPatientInformationService.updateById(mshPatientInformation);
        }else{
            mshPatientInformationService.save(mshPatientInformation);
        }

        List<String> IdCardImages= mshDemandDto.getIdCardImages();
        List<MshPatientListFile> mshPatientListFiles=new ArrayList<>();
        for (String idCardImage : IdCardImages) {
            MshPatientListFile mshPatientListFile=new MshPatientListFile();
            mshPatientListFile.setType("身份证或护照");
            mshPatientListFile.setPatientId(mshPatientInformation.getId());
            mshPatientListFile.setFileType("image/jpeg");
            mshPatientListFile.setFileUrl(idCardImage);
            mshPatientListFiles.add(mshPatientListFile);
        }
        //患者附件信息表全删全插
        QueryWrapper<MshPatientListFile> deleteQueryWrapper = new QueryWrapper<>();
        deleteQueryWrapper.eq("patient_id", mshPatientInformation.getId());
        mshPatientListFileMapper.delete(deleteQueryWrapper);
        //插入患者附件表信息
        for (int i = 0; i < mshPatientListFiles.size(); i++) {
             mshPatientListFileMapper.insert(mshPatientListFiles.get(i));
        }
        List<String> medicalDocumentsImages=mshDemandDto.getMedicalDocumentsImages();

        mshDemandList = new MshDemandList();
        mshDemandList.setPatientname(mshPatientInformation.getPatientname());
        mshDemandList.setPhone(mshPatientInformation.getPhone());
        if (medicalDocumentsImages.size() > 0) {
            mshDemandList.setPicUrl(medicalDocumentsImages.get(0));
        }
        mshDemandList.setProvince(mshPatientInformation.getProvince());
        mshDemandList.setCity(mshPatientInformation.getCity());
        mshDemandList.setDistrict(mshPatientInformation.getDistrict());
        mshDemandList.setProvinceCode(mshPatientInformation.getProvinceCode());
        mshDemandList.setCityCode(mshPatientInformation.getCityCode());
        mshDemandList.setDistrictCode(mshPatientInformation.getDistrictCode());
        mshDemandList.setDetail(mshPatientInformation.getDetail());
        mshDemandList.setCreateTime(DateUtil.date().toTimestamp());
        mshDemandList.setPatientId(mshPatientInformation.getId());
        mshDemandList.setSaveStatus(0);

        mshDemandList.setMemberId(mshPatientInformation.getMemberId());
        mshDemandList.setSource(mshDemandDto.getSource());
        mshDemandList.setDemandNo(mshDemandDto.getDemandNo());
        mshDemandList.setCreateUser(mshDemandDto.getMaker());
        mshDemandList.setCompany(mshPatientInformation.getCompany());
        mshDemandList.setVip(mshPatientInformation.getVip());
        mshDemandList.setPerCustoService(mshPatientInformation.getPerCustoService());
        mshDemandList.setPerCustoServiceEmail(mshPatientInformation.getPerCustoServiceEmail());
        mshDemandList.setPatientEmail(mshPatientInformation.getPatientEmail());
        mshDemandList.setDiseaseName(mshPatientInformation.getDiseaseName());
        mshDemandList.setFileHospital(mshPatientInformation.getFileHospital());
        mshDemandList.setFileDate(mshPatientInformation.getFileDate());
        mshDemandList.setReceivingName(mshPatientInformation.getReceivingName());
        mshDemandList.setRelationship(mshPatientInformation.getRelationship());
        mshDemandList.setReceivingPhone(mshPatientInformation.getReceivingPhone());

        int num = mshDemandListMapper.insert(mshDemandList);
        if(num>0){
            List<MshDemandListFile> mshDemandListFiles=new ArrayList<>();
            for (String medicalDocumentsImage : medicalDocumentsImages) {
                MshDemandListFile mshDemandListFile=new MshDemandListFile();
                mshDemandListFile.setType("医疗文件照片");
                mshDemandListFile.setFileUrl(medicalDocumentsImage);
                mshDemandListFile.setDemandListId(mshDemandList.getId());
                mshDemandListFile.setFileType("image/jpeg");
                mshDemandListFiles.add(mshDemandListFile);
            }
            List<String> otherImages= mshDemandDto.getOtherImages();
            for (String otherImage : otherImages) {
                MshDemandListFile mshDemandListFile=new MshDemandListFile();
                mshDemandListFile.setType("其他");
                mshDemandListFile.setFileUrl(otherImage);
                mshDemandListFile.setDemandListId(mshDemandList.getId());
                mshDemandListFile.setFileType("image/jpeg");
                mshDemandListFiles.add(mshDemandListFile);
            }
            List<String> applyImages= mshDemandDto.getApplyImages();
            for (String otherImage : applyImages) {
                MshDemandListFile mshDemandListFile=new MshDemandListFile();
                mshDemandListFile.setType("申请表");
                mshDemandListFile.setFileUrl(otherImage);
                mshDemandListFile.setDemandListId(mshDemandList.getId());
                mshDemandListFile.setFileType("image/jpeg");
                mshDemandListFiles.add(mshDemandListFile);
            }
            mshDemandListFileService.saveBatch(mshDemandListFiles);
        }

        QueryWrapper queryWrapper = new QueryWrapper<MessageUser>();
        queryWrapper.apply(" send_dept = {0} ","msh");
        List<MessageUser> messageUsers = messageUserMapper.selectList(queryWrapper);
        if(!CollectionUtils.isEmpty(messageUsers)){
            for (MessageUser messageUser : messageUsers) {
                MessageNotice resources=new MessageNotice();
                resources.setUserId(messageUser.getUserId());
                resources.setCreateTime(new Timestamp(System.currentTimeMillis()));
                resources.setMessage("有新的线上需求单，请及时处理,需求单号为【"+mshDemandList.getDemandNo()+"】");
                resources.setTitle("msh需求单");
                messageNoticeService.save(resources);
                if(!messageUser.getUserId().equals("ALL")){
                    webSocket.pushMessage(messageUser.getUserId(),"您有一笔新的需求单，请及时处理,需求单号为【"+mshDemandList.getDemandNo()+"】");
                }else{
                    webSocket.pushMessage("您有一笔新的需求单，请及时处理,需求单号为【"+mshDemandList.getDemandNo()+"】");
                }
            }
        }

    }


    public static void main(String[] args) {
        System.out.println(MshStatusEnum.OrderStatus.DSH.getCode());
        System.out.println(Integer.parseInt("0")==MshStatusEnum.OrderStatus.DSH.getCode());

    }
}
