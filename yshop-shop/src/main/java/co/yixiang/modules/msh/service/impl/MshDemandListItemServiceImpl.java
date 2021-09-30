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
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import cn.hutool.core.collection.CollUtil;
import co.yixiang.constant.ShopConstants;
import co.yixiang.modules.shop.domain.YxSystemStore;
import co.yixiang.modules.shop.service.YxSystemStoreService;
import co.yixiang.utils.ImageUtil;
import co.yixiang.utils.OpenOfficeUtil;
import co.yixiang.utils.OrderUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.PageInfo;

import co.yixiang.common.service.impl.BaseServiceImpl;
import co.yixiang.common.utils.QueryHelpPlus;
import co.yixiang.dozer.service.IGenerator;
import co.yixiang.modules.msh.domain.MshDemandList;
import co.yixiang.modules.msh.domain.MshDemandListFile;
import co.yixiang.modules.msh.domain.MshDemandListItem;
import co.yixiang.modules.msh.domain.MshPatientInformation;
import co.yixiang.modules.msh.domain.MshPatientListFile;
import co.yixiang.modules.msh.service.MshDemandListItemService;
import co.yixiang.modules.msh.service.dto.MshDemandListDto;
import co.yixiang.modules.msh.service.dto.MshDemandListItemDto;
import co.yixiang.modules.msh.service.dto.MshDemandListItemQueryCriteria;
import co.yixiang.modules.msh.service.mapper.MshDemandListFileMapper;
import co.yixiang.modules.msh.service.mapper.MshDemandListItemMapper;
import co.yixiang.modules.msh.service.mapper.MshDemandListMapper;
import co.yixiang.modules.msh.service.mapper.MshPatientInformationMapper;
import co.yixiang.modules.msh.service.mapper.MshPatientListFileMapper;
import co.yixiang.utils.FileUtil;
import lombok.AllArgsConstructor;

/**
* @author cq
* @date 2020-12-25
*/
@Service

//@CacheConfig(cacheNames = "mshDemandListItem")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class MshDemandListItemServiceImpl extends BaseServiceImpl<MshDemandListItemMapper, MshDemandListItem> implements MshDemandListItemService {

	@Autowired
    private  IGenerator generator;

    @Autowired
    private MshDemandListItemMapper mshDemandListItemMapper;

    @Autowired
    private MshDemandListMapper mshDemandListMapper;

    @Autowired
    private MshDemandListFileMapper mshDemandListFileMapper;

    @Autowired
    private MshPatientListFileMapper mshPatientListFileMapper;

    @Autowired
    private MshPatientInformationMapper mshPatientInformationMapper;

    @Value("${file.path}")
    private String filePath;

    @Value("${file.localUrl}")
    private String localUrl;

    @Autowired
    private YxSystemStoreService yxSystemStoreService;


    @Override
    //@Cacheable
    public Map<String, Object> queryAll(MshDemandListItemQueryCriteria criteria, Pageable pageable) {
        getPage(pageable);
        PageInfo<MshDemandListItem> page = new PageInfo<>(queryAll(criteria));
        Map<String, Object> map = new LinkedHashMap<>(2);
        map.put("content", generator.convert(page.getList(), MshDemandListItemDto.class));
        map.put("totalElements", page.getTotal());
        return map;
    }


    @Override
    //@Cacheable
    public List<MshDemandListItem> queryAll(MshDemandListItemQueryCriteria criteria){
        return baseMapper.selectList(QueryHelpPlus.getPredicate(MshDemandListItem.class, criteria));
    }


    @Override
    public void download(List<MshDemandListItemDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (MshDemandListItemDto mshDemandListItem : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("需求单编号", mshDemandListItem.getDemandListId());
            if(mshDemandListItem.getOrderId()!=null&&!"".equals(mshDemandListItem.getOrderId())){
            	map.put("处方单号", mshDemandListItem.getOrderId()+"_msh");
            }else{
            	map.put("处方单号", "");
            }
            map.put("订单编号", mshDemandListItem.getExternalOrderId());
            //0:待审核;1:审核通过;2:审核不通过;3:已发货;4:已完成;5:已退货;
            if("0".equals(mshDemandListItem.getOrderStatus())){
            	map.put("订单状态", "待审核");
			}else if("1".equals(mshDemandListItem.getOrderStatus())){
				map.put("订单状态", "审核通过");
			}else if("2".equals(mshDemandListItem.getOrderStatus())){
				map.put("订单状态", "审核不通过");
			}else if("3".equals(mshDemandListItem.getOrderStatus())){
				map.put("订单状态", "已发货");
			}else if("4".equals(mshDemandListItem.getOrderStatus())){
				map.put("订单状态", "已完成");
			}else if("5".equals(mshDemandListItem.getOrderStatus())){
				map.put("订单状态", "已退货");
			}else{
				map.put("订单状态", "");
			}

            map.put("需求单药品名称", mshDemandListItem.getMedName());
            map.put("需求单药品规格", mshDemandListItem.getMedSpec());
            map.put("需求单药品单价", mshDemandListItem.getUnitPrice());
            map.put("需求单药品数量", mshDemandListItem.getPurchaseQty());

            map.put("订单药品名称", mshDemandListItem.getMedNameForOrder());
            map.put("订单药品规格", mshDemandListItem.getMedSpecForOrder());
            map.put("订单药品单价", mshDemandListItem.getUnitPriceForOrder());
            map.put("订单药品数量", mshDemandListItem.getPurchaseQtyForOrder());

            map.put("药房名称", mshDemandListItem.getDrugstoreName());
            map.put("物流单号", mshDemandListItem.getLogisticsNum());
            map.put("患者姓名", mshDemandListItem.getPatientname());
            map.put("患者电话", mshDemandListItem.getPhone());
            map.put("MemberId", mshDemandListItem.getMemberId());
            map.put("创建时间", mshDemandListItem.getCreateTime());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }


	@Override
	public Map<String, Object> selectMshDemandListList(MshDemandListItemQueryCriteria criteria, Pageable pageable) {
		getPage(pageable);
        PageInfo<MshDemandListDto> page = new PageInfo<>(selectMshDemandListList(criteria));
        Map<String, Object> map = new LinkedHashMap<>(2);
        map.put("content", generator.convert(page.getList(), MshDemandListDto.class));
        map.put("totalElements", page.getTotal());
        return map;
	}

    public List<MshDemandListDto> selectMshDemandListList(MshDemandListItemQueryCriteria criteria){
    	List<MshDemandListDto> list = mshDemandListItemMapper.selectMshDemandListList(criteria);
    	//更新订单状态文字
    	//0:待审核;1:审核通过;2:审核不通过;3:已发货;4:已完成;5:已退货;
    	for(int i = 0; i<list.size(); i++){
    		for (int j = 0; j < list.get(i).getOrderList().size(); j++) {
    			if("0".equals(list.get(i).getOrderList().get(j).getOrderStatus())){
    				list.get(i).getOrderList().get(j).setOrderStatusStr("待审核");
    			}else if("1".equals(list.get(i).getOrderList().get(j).getOrderStatus())){
    				list.get(i).getOrderList().get(j).setOrderStatusStr("审核通过");
    			}else if("2".equals(list.get(i).getOrderList().get(j).getOrderStatus())){
    				list.get(i).getOrderList().get(j).setOrderStatusStr("审核不通过");
    			}else if("3".equals(list.get(i).getOrderList().get(j).getOrderStatus())){
    				list.get(i).getOrderList().get(j).setOrderStatusStr("已发货");
    			}else if("4".equals(list.get(i).getOrderList().get(j).getOrderStatus())){
    				list.get(i).getOrderList().get(j).setOrderStatusStr("已完成");
    			}else if("5".equals(list.get(i).getOrderList().get(j).getOrderStatus())){
    				list.get(i).getOrderList().get(j).setOrderStatusStr("已退货");
    			}else{
    				list.get(i).getOrderList().get(j).setOrderStatusStr("");
    			}
			}
		}
        return list;
    }


	@Override
	public Map<String, Object> selectMshDemandListItemListForMakeOrder(Integer id) {
		Map<String, Object> map = new LinkedHashMap<>(5);
		//全部数据
		List<MshDemandListItemDto> list = mshDemandListItemMapper.selectListByDemandListID(id,0);
        map.put("MshDemandListItemListAll", list);
        //未生成订单数据
        map.put("MshDemandListItemList", mshDemandListItemMapper.selectListByDemandListID(id,1));
        //需求单附件信息
        QueryWrapper<MshDemandListFile> queryWrapper = new QueryWrapper<>();
		queryWrapper.eq("demand_list_id", id);
		queryWrapper.orderByAsc("type");
        map.put("MshDemandListFileList", mshDemandListFileMapper.selectList(queryWrapper));
        //查询获取需求单中的患者ID
        QueryWrapper<MshDemandList> queryWrapper2 = new QueryWrapper<>();
        queryWrapper2.eq("id", id);
        List<MshDemandList> list2 = mshDemandListMapper.selectList(queryWrapper2);
        //查询患者相关信息
        QueryWrapper<MshPatientInformation> queryWrapper3 = new QueryWrapper<>();
        queryWrapper3.eq("id", list2.get(0).getPatientId());
        List<MshPatientInformation> list3 = mshPatientInformationMapper.selectList(queryWrapper3);
		map.put("MshPatientInformation", list3);

        QueryWrapper<MshPatientListFile> queryWrapper4 = new QueryWrapper<>();
        if(list3!=null&&list3.size()>0){
        	queryWrapper4.eq("patient_id", list3.get(0).getId());
    		map.put("MshPatientListFileList", mshPatientListFileMapper.selectList(queryWrapper4));
        }else{
    		map.put("MshPatientListFileList", null);
        }


        YxSystemStore yxSystemStore = yxSystemStoreService.getOne(new LambdaQueryWrapper<YxSystemStore>().eq(YxSystemStore::getName, ShopConstants.STORENAME_GUANGZHOU_CLOUD),false);
        map.put("storeInfo",yxSystemStore);
		return map;
	}


    @Override
    //@Cacheable
    public List<MshDemandListItemDto> selectMshDemandListItemList(MshDemandListItemQueryCriteria criteria){
        return mshDemandListItemMapper.selectMshDemandListItemList(criteria);
    }

    @Override
    public List<String> convertImage(Integer id) {
        List<String> result = new ArrayList<>();
        //查询获取需求单中的患者ID
        QueryWrapper queryWrapper2 = new QueryWrapper<>();
        queryWrapper2.eq("id", id);
        MshDemandList mshDemandList = mshDemandListMapper.selectById(id);
        //查询患者相关附件信息
        QueryWrapper<MshPatientListFile> queryWrapper4 = new QueryWrapper<>();

        queryWrapper4.eq("patient_id", mshDemandList.getPatientId());

        List<MshPatientListFile> fileList_1 = mshPatientListFileMapper.selectList(queryWrapper4);

        //需求单附件信息
        QueryWrapper<MshDemandListFile> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("demand_list_id", id);
        queryWrapper.orderByAsc("type");
        List<MshDemandListFile> fileList_2 = mshDemandListFileMapper.selectList(queryWrapper);

        for(MshPatientListFile file : fileList_1 ) {
            String fileType = FileUtil.extName(file.getFileName()).toLowerCase();

            // 将pdf 转化为图片
            if("pdf".equals(fileType)) {
                // 2.pdf 转成图片
                String pdfPath = file.getFileUrl();
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
                result.add(fileUrlConvert);
            }else if("jpg".equals(fileType) || "png".equals(fileType) || "jpeg".equals(fileType)  ) {
                result.add(file.getFileUrl());
            }else if("doc".equals(fileType) || "docx".equals(fileType) || "ppt".equals(fileType) || "pptx".equals(fileType) || "xls".equals(fileType) || "xlsx".equals(fileType) ) {
                String officePath = file.getFileUrl();
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
                result.add(fileUrlConvert);

            }
        }


        for(MshDemandListFile file : fileList_2 ) {
            String fileType = FileUtil.extName(file.getFileName()).toLowerCase();

            // 将pdf 转化为图片
            if("pdf".equals(fileType)) {
                // 2.pdf 转成图片
                String pdfPath = file.getFileUrl();
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
                result.add(fileUrlConvert);
            }else if("jpg".equals(fileType) || "png".equals(fileType) || "jpeg".equals(fileType)  ) {
                result.add(file.getFileUrl());
            }else if("doc".equals(fileType) || "docx".equals(fileType) || "ppt".equals(fileType) || "pptx".equals(fileType) || "xls".equals(fileType) || "xlsx".equals(fileType) ) {
                String officePath = file.getFileUrl();
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
                result.add(fileUrlConvert);

            }
        }
        return result;
    }
}
