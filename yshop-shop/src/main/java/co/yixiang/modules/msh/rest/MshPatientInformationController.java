/**
* Copyright (C) 2018-2020
* All rights reserved, Designed By www.yixiang.co
* 注意：
* 本软件为www.yixiang.co开发研制，未经购买不得使用
* 购买后可获得全部源代码（禁止转卖、分享、上传到码云、github等开源平台）
* 一经发现盗用、分享等行为，将追究法律责任，后果自负
*/
package co.yixiang.modules.msh.rest;
import java.io.File;
import java.util.*;

import cn.hutool.core.collection.CollUtil;
import co.yixiang.dozer.service.IGenerator;
import co.yixiang.exception.BadRequestException;
import co.yixiang.utils.FileUtil;
import co.yixiang.utils.ImageUtil;
import co.yixiang.utils.OpenOfficeUtil;
import co.yixiang.utils.OrderUtil;
import lombok.AllArgsConstructor;
import co.yixiang.logging.aop.log.Log;
import co.yixiang.modules.msh.domain.MshPatientInformation;
import co.yixiang.modules.msh.domain.MshPatientListFile;
import co.yixiang.modules.msh.service.MshPatientInformationService;
import co.yixiang.modules.msh.service.dto.MshPatientInformationQueryCriteria;
import co.yixiang.modules.msh.service.mapper.MshPatientListFileMapper;
import co.yixiang.modules.shop.domain.MdCountry;
import co.yixiang.modules.shop.service.mapper.MdCountryMapper;
import co.yixiang.modules.msh.service.dto.MshDemandListItemQueryCriteria;
import co.yixiang.modules.msh.service.dto.MshPatientInformationDto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import cn.hutool.core.date.DateUtil;
import io.swagger.annotations.*;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;

/**
* @author cq
* @date 2020-12-18
*/

@Api(tags = "患者管理管理")
@RestController
@RequestMapping("/api/mshPatientInformation")
public class MshPatientInformationController {

	@Autowired
    private  MshPatientInformationService mshPatientInformationService;

    @Autowired
    private MdCountryMapper mdCountryMapper;

    @Autowired
    private MshPatientListFileMapper mshPatientListFileMapper;
	@Autowired
    private  IGenerator generator;

	@Value("${file.path}")
	private String filePath;

	@Value("${file.localUrl}")
	private String localUrl;

    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    public void download(HttpServletResponse response, MshPatientInformationQueryCriteria criteria) throws IOException {
        mshPatientInformationService.download(generator.convert(mshPatientInformationService.queryAll(criteria), MshPatientInformationDto.class), response);
    }

    @GetMapping
    @Log("查询患者管理")
    @ApiOperation("查询患者管理")
    public ResponseEntity<Object> getMshPatientInformations(MshPatientInformationQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(mshPatientInformationService.selectMshPatientListList(criteria,pageable),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增患者管理")
    @ApiOperation("新增患者管理")
    public ResponseEntity<Object> create(@Validated @RequestBody MshPatientInformationDto resources){
    	MshPatientInformation mpi = mshPatientInformationService.getOne(new QueryWrapper<MshPatientInformation>().eq("phone",resources.getPhone()).eq("delete_status", 0));
    	if(mpi!=null){
    		Map<String, Object> map = new LinkedHashMap<>(2);
            map.put("status", HttpStatus.BAD_REQUEST);
            map.put("message", "已存在该手机号！");

            return new ResponseEntity(map,HttpStatus.BAD_REQUEST);
    	}else{
    		//获取省市区的name
    		QueryWrapper<MdCountry> queryWrapper = new QueryWrapper<>();
    		queryWrapper.eq("CODE", resources.getProvinceCode());
    		String province = mdCountryMapper.selectList(queryWrapper).get(0).getName();
    		queryWrapper = new QueryWrapper<>();
    		queryWrapper.eq("CODE", resources.getCityCode());
    		String city = mdCountryMapper.selectList(queryWrapper).get(0).getName();
    		queryWrapper = new QueryWrapper<>();
    		queryWrapper.eq("CODE", resources.getDistrictCode());
    		String district = mdCountryMapper.selectList(queryWrapper).get(0).getName();
    		MshPatientInformation mshPatientInformation = new MshPatientInformation();
    		mshPatientInformation.setPatientname(resources.getPatientname());
    		mshPatientInformation.setPhone(resources.getPhone());
    		mshPatientInformation.setProvinceCode(resources.getProvinceCode());
    		mshPatientInformation.setCityCode(resources.getCityCode());
    		mshPatientInformation.setDistrictCode(resources.getDistrictCode());
    		mshPatientInformation.setProvince(province);
    		mshPatientInformation.setCity(city);
    		mshPatientInformation.setDistrict(district);
    		mshPatientInformation.setAddTime(DateUtil.date().toTimestamp());
			mshPatientInformation.setDetail(resources.getDetail());
			mshPatientInformation.setMemberId(resources.getMemberId());
			mshPatientInformation.setReceivingName(resources.getReceivingName());
			mshPatientInformation.setRelationship(resources.getRelationship());
			mshPatientInformation.setReceivingPhone(resources.getReceivingPhone());
			mshPatientInformation.setCompany(resources.getCompany());
			mshPatientInformation.setVip(resources.getVip());
			mshPatientInformation.setPerCustoService(resources.getPerCustoService());
			mshPatientInformation.setPerCustoServiceEmail(resources.getPerCustoServiceEmail());
			mshPatientInformation.setPatientEmail(resources.getPatientEmail());
			mshPatientInformation.setDiseaseName(resources.getDiseaseName());
			mshPatientInformation.setFileHospital(resources.getFileHospital());
			mshPatientInformation.setFileDate(resources.getFileDate());


			mshPatientInformationService.save(mshPatientInformation);
/*
			for(MshPatientListFile mshPatientListFile : resources.getMshPatientListFileList()) {
				String fileType = FileUtil.extName(mshPatientListFile.getFileName()).toLowerCase();
				// 将pdf 转化为图片
				if("pdf".equals(fileType)) {
					// 2.pdf 转成图片
					String pdfPath = mshPatientListFile.getFileUrl();
					if(pdfPath.contains(localUrl)) {
						pdfPath = pdfPath.replace(localUrl+"/file",filePath);
					}
					String fileNo = OrderUtil.orderSn();
					String picPath = filePath + "msh" + File.separator + fileNo ;

					int pageCount = ImageUtil.pdf2Pic(pdfPath,picPath);
					List<String> picWebUrlList = new ArrayList<>();
					for(int j = 0 ;j<pageCount;j++) {
						String picWebUrl = localUrl + File.separator + "file" + File.separator + "msh" + File.separator + fileNo + j + ".png";
						picWebUrlList.add(picWebUrl);
					}
					String fileUrlConvert = CollUtil.join(picWebUrlList,",");
					mshPatientListFile.setFileUrlConvert(fileUrlConvert);
				}else if("jpg".equals(fileType) || "png".equals(fileType) || "jpeg".equals(fileType) ) {
					mshPatientListFile.setFileUrlConvert(mshPatientListFile.getFileUrl());
				}else if("doc".equals(fileType) || "docx".equals(fileType) || "ppt".equals(fileType) || "pptx".equals(fileType) || "xls".equals(fileType) || "xlsx".equals(fileType) ) {
					String officePath = mshPatientListFile.getFileUrl();
					if(officePath.contains(localUrl)) {
						officePath = officePath.replace(localUrl+"/file",filePath);
					}
					String fileNo = OrderUtil.orderSn();
					String pdfPath = filePath + "msh" + File.separator + fileNo+".pdf" ;
					OpenOfficeUtil.officeToPDF(officePath,pdfPath);

					String picFileNo = OrderUtil.orderSn();
					String picPath = filePath + "msh" + File.separator + picFileNo ;
					int pageCount = ImageUtil.pdf2Pic(pdfPath,picPath);
					List<String> picWebUrlList = new ArrayList<>();
					for(int j = 0 ;j<pageCount;j++) {
						String picWebUrl = localUrl + File.separator + "file" + File.separator + "msh" + File.separator + picFileNo + j + ".png";
						picWebUrlList.add(picWebUrl);
					}
					String fileUrlConvert = CollUtil.join(picWebUrlList,",");
					mshPatientListFile.setFileUrlConvert(fileUrlConvert);

				}
			}*/

        	//插入需求单附件表信息
    		for(int i = 0; i<resources.getMshPatientListFileList().size(); i++){
    			resources.getMshPatientListFileList().get(i).setPatientId(mshPatientInformation.getId());
    			resources.getMshPatientListFileList().get(i).setType("身份证或护照");
    			int num = mshPatientListFileMapper.insert(resources.getMshPatientListFileList().get(i));
    			if(num==0){
    				Map<String, Object> map = new LinkedHashMap<>(2);
    	            map.put("status", HttpStatus.BAD_REQUEST);
    	            map.put("message", "插入需求单患者附件信息失败！");
    	            return new ResponseEntity(map,HttpStatus.BAD_REQUEST);
    			}
    		}
            return new ResponseEntity<>(HttpStatus.CREATED);
    	}
    }

    @PutMapping
    @Log("修改患者管理")
    @ApiOperation("修改患者管理")
    public ResponseEntity<Object> update(@Validated @RequestBody MshPatientInformationDto mshPatientInformationDto){
		//获取省市区的name
		QueryWrapper<MdCountry> queryWrapper = new QueryWrapper<>();
		queryWrapper.eq("CODE", mshPatientInformationDto.getProvinceCode());
		String province = mdCountryMapper.selectList(queryWrapper).get(0).getName();
		queryWrapper = new QueryWrapper<>();
		queryWrapper.eq("CODE", mshPatientInformationDto.getCityCode());
		String city = mdCountryMapper.selectList(queryWrapper).get(0).getName();
		queryWrapper = new QueryWrapper<>();
		queryWrapper.eq("CODE", mshPatientInformationDto.getDistrictCode());
		String district = mdCountryMapper.selectList(queryWrapper).get(0).getName();

		MshPatientInformation mshPatientInformation = new MshPatientInformation();
		mshPatientInformation.setId(mshPatientInformationDto.getId());
		mshPatientInformation.setPatientname(mshPatientInformationDto.getPatientname());
		//手机号不允许修改
		mshPatientInformation.setAddTime(mshPatientInformationDto.getAddTime());
		mshPatientInformation.setProvinceCode(mshPatientInformationDto.getProvinceCode());
		mshPatientInformation.setCityCode(mshPatientInformationDto.getCityCode());
		mshPatientInformation.setDistrictCode(mshPatientInformationDto.getDistrictCode());
		mshPatientInformation.setProvince(province);
		mshPatientInformation.setCity(city);
		mshPatientInformation.setDistrict(district);
		mshPatientInformation.setDetail(mshPatientInformationDto.getDetail());
		mshPatientInformation.setMemberId(mshPatientInformationDto.getMemberId());
		mshPatientInformation.setReceivingName(mshPatientInformationDto.getReceivingName());
		mshPatientInformation.setRelationship(mshPatientInformationDto.getRelationship());
		mshPatientInformation.setReceivingPhone(mshPatientInformationDto.getReceivingPhone());
		mshPatientInformation.setCompany(mshPatientInformationDto.getCompany());
		mshPatientInformation.setVip(mshPatientInformationDto.getVip());
		mshPatientInformation.setPerCustoService(mshPatientInformationDto.getPerCustoService());
		mshPatientInformation.setPerCustoServiceEmail(mshPatientInformationDto.getPerCustoServiceEmail());
		mshPatientInformation.setPatientEmail(mshPatientInformationDto.getPatientEmail());
		mshPatientInformation.setDiseaseName(mshPatientInformationDto.getDiseaseName());
		mshPatientInformation.setFileHospital(mshPatientInformationDto.getFileHospital());
		mshPatientInformation.setFileDate(mshPatientInformationDto.getFileDate());

        mshPatientInformationService.updateById(mshPatientInformation);
        //患者附件信息表全删全插
    	QueryWrapper<MshPatientListFile> deleteQueryWrapper = new QueryWrapper<>();
    	deleteQueryWrapper.eq("patient_id", mshPatientInformationDto.getId());
    	mshPatientListFileMapper.delete(deleteQueryWrapper);

		for(MshPatientListFile mshPatientListFile : mshPatientInformationDto.getMshPatientListFileList()) {
			String fileType = FileUtil.extName(mshPatientListFile.getFileName()).toLowerCase();
			// 将pdf 转化为图片
			if("pdf".equals(fileType)) {
				// 2.pdf 转成图片
				String pdfPath = mshPatientListFile.getFileUrl();
				if(pdfPath.contains(localUrl)) {
					pdfPath = pdfPath.replace(localUrl+"/file",filePath);
				}
				String fileNo = OrderUtil.orderSn();
				String picPath = filePath + "msh" + File.separator + fileNo ;

				int pageCount = ImageUtil.pdf2Pic(pdfPath,picPath);
				List<String> picWebUrlList = new ArrayList<>();
				for(int j = 0 ;j<pageCount;j++) {
					String picWebUrl = localUrl + File.separator + "file" + File.separator + "msh" + File.separator + fileNo + j + ".png";
					picWebUrlList.add(picWebUrl);
				}
				String fileUrlConvert = CollUtil.join(picWebUrlList,",");
				mshPatientListFile.setFileUrlConvert(fileUrlConvert);
			}else if("jpg".equals(fileType) || "png".equals(fileType) || "jpeg".equals(fileType) ) {
				mshPatientListFile.setFileUrlConvert(mshPatientListFile.getFileUrl());
			}else if("doc".equals(fileType) || "docx".equals(fileType) || "ppt".equals(fileType) || "pptx".equals(fileType) || "xls".equals(fileType) || "xlsx".equals(fileType) ) {
				String officePath = mshPatientListFile.getFileUrl();
				if(officePath.contains(localUrl)) {
					officePath = officePath.replace(localUrl+"/file",filePath);
				}
				String fileNo = OrderUtil.orderSn();
				String pdfPath = filePath + "msh" + File.separator + fileNo+".pdf" ;
				OpenOfficeUtil.officeToPDF(officePath,pdfPath);

				String picFileNo = OrderUtil.orderSn();
				String picPath = filePath + "msh" + File.separator + picFileNo ;
				int pageCount = ImageUtil.pdf2Pic(pdfPath,picPath);
				List<String> picWebUrlList = new ArrayList<>();
				for(int j = 0 ;j<pageCount;j++) {
					String picWebUrl = localUrl + File.separator + "file" + File.separator + "msh" + File.separator + picFileNo + j + ".png";
					picWebUrlList.add(picWebUrl);
				}
				String fileUrlConvert = CollUtil.join(picWebUrlList,",");
				mshPatientListFile.setFileUrlConvert(fileUrlConvert);

			}
		}

    	//插入需求单附件表信息
		for(int i = 0; i<mshPatientInformationDto.getMshPatientListFileList().size(); i++){
			mshPatientInformationDto.getMshPatientListFileList().get(i).setType("身份证或护照");
			int num = mshPatientListFileMapper.insert(mshPatientInformationDto.getMshPatientListFileList().get(i));
			if(num==0){
				Map<String, Object> map = new LinkedHashMap<>(2);
	            map.put("status", HttpStatus.BAD_REQUEST);
	            map.put("message", "插入需求单患者附件信息失败！");
	            return new ResponseEntity(map,HttpStatus.BAD_REQUEST);
			}
		}
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除患者管理")
    @ApiOperation("删除患者管理")
    @DeleteMapping
    public ResponseEntity<Object> deleteAll(@RequestBody Integer[] ids) {
        Arrays.asList(ids).forEach(id->{
        	MshPatientInformation mshPatientInformation = new MshPatientInformation();
        	mshPatientInformation.setId(id);
        	mshPatientInformation.setDeleteStatus(1);
        	//假删除
        	mshPatientInformationService.updateById(mshPatientInformation);
        });

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Log("查询患者信息")
    @ApiOperation("查询患者信息")
    @PostMapping(value = "/list")
    public ResponseEntity<Object> getMshPatientInformationList(@RequestBody String phone){
        return new ResponseEntity<>(mshPatientInformationService.selectMshPatientInformationListByPhone(phone),HttpStatus.OK);
    }
}
