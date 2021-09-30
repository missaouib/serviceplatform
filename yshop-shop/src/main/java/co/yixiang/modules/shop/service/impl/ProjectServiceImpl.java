/**
* Copyright (C) 2018-2020
* All rights reserved, Designed By www.yixiang.co
* 注意：
* 本软件为www.yixiang.co开发研制，未经购买不得使用
* 购买后可获得全部源代码（禁止转卖、分享、上传到码云、github等开源平台）
* 一经发现盗用、分享等行为，将追究法律责任，后果自负
*/
package co.yixiang.modules.shop.service.impl;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.api.impl.WxMaServiceImpl;
import cn.binarywang.wx.miniapp.bean.WxMaCodeLineColor;
import cn.binarywang.wx.miniapp.config.impl.WxMaDefaultConfigImpl;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.img.ImgUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.qrcode.QrCodeUtil;
import co.yixiang.enums.RedisKeyEnum;
import co.yixiang.exception.ErrorRequestException;
import co.yixiang.modules.shop.domain.*;
import co.yixiang.common.service.impl.BaseServiceImpl;
import co.yixiang.modules.shop.service.*;
import co.yixiang.modules.yiyaobao.service.OrderServiceImpl;
import co.yixiang.utils.*;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.AllArgsConstructor;
import co.yixiang.dozer.service.IGenerator;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import co.yixiang.common.utils.QueryHelpPlus;
import co.yixiang.modules.shop.service.dto.ProjectDto;
import co.yixiang.modules.shop.service.dto.ProjectQueryCriteria;
import co.yixiang.modules.shop.service.mapper.ProjectMapper;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;

import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
// 默认不使用缓存
//import org.springframework.cache.annotation.CacheConfig;
//import org.springframework.cache.annotation.CacheEvict;
//import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.awt.*;
import java.io.File;
import java.sql.Timestamp;
import java.util.*;
import java.io.IOException;
import java.util.List;
import javax.servlet.http.HttpServletResponse;

/**
* @author visa
* @date 2021-02-25
*/
@Service
@Slf4j
//@CacheConfig(cacheNames = "project")
//@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class ProjectServiceImpl extends BaseServiceImpl<ProjectMapper, Project> implements ProjectService {
    @Autowired
    private IGenerator generator;

    @Value("${file.path}")
    private String path;

    @Value("${yiyao.url}")
    private String yiyaoUrl;

    @Autowired
    private OrderServiceImpl yiyaobaoOrderService;
    @Autowired
    private YxExpressTemplateService yxExpressTemplateService;

    @Autowired
    private YxSystemStoreService yxSystemStoreService;

    @Autowired
    private MdCountryService mdCountryService;

    @Autowired
    private ProjectSalesAreaService projectSalesAreaService;

    @Override
    //@Cacheable
    public Map<String, Object> queryAll(ProjectQueryCriteria criteria, Pageable pageable) {
        getPage(pageable);
        PageInfo<Project> page = new PageInfo<>(queryAll(criteria));
        Map<String, Object> map = new LinkedHashMap<>(2);
        List<ProjectDto> projectList = generator.convert(page.getList(), ProjectDto.class);
        for(ProjectDto project:projectList) {
           if( StrUtil.isNotBlank(project.getExpressTemplateId())) {
               /*YxExpressTemplate yxExpressTemplate = yxExpressTemplateService.getById(project.getExpressTemplateId());
               if(yxExpressTemplate != null) {
                   project.setExpressTemplateName(yxExpressTemplate.getTemplateName());
               }*/
               project.setExpressTemplateIdList(Arrays.asList(project.getExpressTemplateId().split(",")));
           }

            if(StrUtil.isNotBlank(project.getStoreIds())) {
                LambdaQueryWrapper<YxSystemStore> lambdaQueryWrapper = new LambdaQueryWrapper();
                lambdaQueryWrapper.in(YxSystemStore::getId, Arrays.asList(project.getStoreIds().split(",")));
                List<YxSystemStore> storeList = yxSystemStoreService.list(lambdaQueryWrapper);
                project.setStoreList(storeList);
            }
        }

        map.put("content", projectList);
        map.put("totalElements", page.getTotal());
        return map;
    }


    @Override
    //@Cacheable
    public List<Project> queryAll(ProjectQueryCriteria criteria){
        QueryWrapper queryWrapper = QueryHelpPlus.getPredicate(Project.class, criteria);
        queryWrapper.ne("project_code","");

        if(CollUtil.isNotEmpty(criteria.getProjectCodeList())) {
            queryWrapper.in("project_code",criteria.getProjectCodeList());
        }

        return baseMapper.selectList(queryWrapper);
    }


    @Override
    public void download(List<ProjectDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (ProjectDto project : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("项目编码", project.getProjectCode());
            map.put("项目名称", project.getProjectName());
            map.put("项目简介", project.getProjectDesc());
            map.put("项目备注", project.getRemark());
            map.put("项目联系电话", project.getPhone());
            map.put("在线咨询客服组id", project.getServiceGroupId());
            map.put("益药宝项目代码", project.getYiyaobaoProjectCode());
            map.put("记录生成时间", project.getCreateTime());
            map.put("记录更新时间", project.getUpdateTime());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

    @Override
    public String generateQRCode(String projectNo,String staffCode) {

        //读取redis配置
        String appId = RedisUtil.get(RedisKeyEnum.WXAPP_APPID.getValue());
        String secret = RedisUtil.get(RedisKeyEnum.WXAPP_SECRET.getValue());
        if (StrUtil.isBlank(appId) || StrUtil.isBlank(secret)) {
            throw new ErrorRequestException("请先配置小程序");
        }

        //调用工具包的服务
        WxMaService wxMaService = new WxMaServiceImpl();
        WxMaDefaultConfigImpl wxMaDefaultConfigImpl = new WxMaDefaultConfigImpl();
        wxMaDefaultConfigImpl.setAppid(appId);		//小程序appId
        wxMaDefaultConfigImpl.setSecret(secret);	//小程序secret
        wxMaService.setWxMaConfig(wxMaDefaultConfigImpl);

        // 设置小程序二维码线条颜色为黑色
        WxMaCodeLineColor lineColor = new WxMaCodeLineColor("0", "0", "0");
        byte[] qrCodeBytes = null;
        try {
            //其中codeType以及parameterValue为前端页面所需要接收的参数。
            qrCodeBytes = wxMaService.getQrcodeService().createWxaCodeBytes("pages/ShoppingCart/uploadOrder?projectCode=" + projectNo + "&refereeCode=" + staffCode, 30, false, lineColor, false);
      //      File file = wxMaService.getQrcodeService().createWxaCode("pages/ShoppingCart/uploadOrder?projectCode=" + projectNo + "&refereeCode=" + staffCode, 30, false, lineColor, false);
           if(StrUtil.isNotBlank(staffCode)) {
               String fileName = path +  staffCode +".jpg";
               String destFileName = path +  staffCode +"_txt.jpg";
               File file = cn.hutool.core.io.FileUtil.writeBytes(qrCodeBytes,fileName);
               ImgUtil.pressText(//
                       file, //
                       FileUtil.file(destFileName), //
                       staffCode, Color.BLACK, //文字
                       new Font("黑体", Font.BOLD, 15), //字体
                       100, //x坐标修正值。 默认在中间，偏移量相对于中间偏移
                       130, //y坐标修正值。 默认在中间，偏移量相对于中间偏移
                       1.0f//透明度：alpha 必须是范围 [0.0, 1.0] 之内（包含边界值）的一个浮点数字
               );
               qrCodeBytes = cn.hutool.core.io.FileUtil.readBytes(destFileName);
           }

        } catch (WxErrorException e) {
            e.printStackTrace();
        }

        String qrCodeStr= Base64.encodeBase64String(qrCodeBytes);
        qrCodeStr = "data:image/jpeg;base64," + qrCodeStr;





        return qrCodeStr;
    }


    @Override
    public String generateQRCodeH5(String projectNo,String staffCode) {

        String url = yiyaoUrl + "#/pages/ShoppingCart/uploadOrder?projectCode=" + projectNo + "&refereeCode=" + staffCode;
        log.info("generateQRCodeH5 url={}",url);
        byte[] qrCodeBytes = QrCodeUtil.generatePng(url, 300, 300);

        String qrCodeStr= Base64.encodeBase64String(qrCodeBytes);
        qrCodeStr = "data:image/jpeg;base64," + qrCodeStr;

        return qrCodeStr;
    }


    @Override
    public boolean saveProject(Project resources) {
        Boolean insertFlag = false;
        if(resources.getId() == null) {
            insertFlag  = true;
        }

        if( CollUtil.isEmpty(resources.getExpressTemplateIdList())) {
            resources.setExpressTemplateId("");
        } else {
            resources.setExpressTemplateId(CollUtil.join(resources.getExpressTemplateIdList(),","));
        }

        saveOrUpdate(resources);

        // 新增项目 项目默认销售区域-全国
        if(insertFlag) {
            LambdaQueryWrapper<MdCountry> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(MdCountry::getParentId,'0');
            lambdaQueryWrapper.select(MdCountry::getName);
            List<MdCountry> mdCountryList = mdCountryService.list(lambdaQueryWrapper);
            for(MdCountry mdCountry:mdCountryList) {
                ProjectSalesArea projectSalesArea = new ProjectSalesArea();
                projectSalesArea.setProjectCode(resources.getProjectCode());
                projectSalesArea.setAreaName(mdCountry.getName());
                projectSalesArea.setFreePostage(0);
                projectSalesArea.setIsFree(0);
                projectSalesArea.setCreateTime(new Timestamp(System.currentTimeMillis()));
                projectSalesArea.setUpdateTime(new Timestamp(System.currentTimeMillis()));
                projectSalesAreaService.save(projectSalesArea);
            }
        }


        if(StrUtil.isNotBlank(resources.getProjectCode())) {
            yiyaobaoOrderService.saveProject(resources);
        }

        return true;
    }

    @Override
    public List<String> queryProjectCode(String userName) {
        return baseMapper.queryProjectCode(userName);
    }
}
