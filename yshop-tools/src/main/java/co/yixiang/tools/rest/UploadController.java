/**
 * Copyright (C) 2018-2020
 * All rights reserved, Designed By www.yixiang.co

 */
package co.yixiang.tools.rest;

import cn.hutool.core.util.StrUtil;
import co.yixiang.annotation.AnonymousAccess;
import co.yixiang.tools.domain.QiniuContent;
import co.yixiang.tools.service.LocalStorageService;
import co.yixiang.tools.service.QiNiuService;
import co.yixiang.tools.service.dto.LocalStorageDto;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

/**
 * @author hupeng
 * @date 2020-01-09
 */
@Api(tags = "上传统一管理")
@RestController
@Slf4j
@RequestMapping("/api/upload")
@SuppressWarnings("unchecked")
public class UploadController {

    @Value("${file.localUrl}")
    private String localUrl;

    private final LocalStorageService localStorageService;

    private final QiNiuService qiNiuService;

    public UploadController(LocalStorageService localStorageService, QiNiuService qiNiuService) {
        this.localStorageService = localStorageService;
        this.qiNiuService = qiNiuService;
    }


    @ApiOperation("上传文件")
    @PostMapping
    @AnonymousAccess
    public ResponseEntity<Object> create(@RequestParam(defaultValue = "") String name, @RequestParam("file") MultipartFile[] files) {
        StringBuilder url = new StringBuilder();
        log.info("上传文件： "+localUrl);
        if (StrUtil.isNotEmpty(localUrl)) { //存在走本地
            log.info("上传文件 MultipartFile length： "+(files==null?null:files.length));
            for (MultipartFile file : files) {
                LocalStorageDto localStorageDTO = localStorageService.create(name, file);
                if ("".equals(url.toString())) {
                    url = url.append(localUrl + "/file/" + localStorageDTO.getType() + "/" + localStorageDTO.getRealName());
                } else {
                    url = url.append(","+localUrl + "/file/" + localStorageDTO.getType() + "/" + localStorageDTO.getRealName());
                }
            }
        } else {//走七牛云
            for (MultipartFile file : files) {
                QiniuContent qiniuContent = qiNiuService.upload(file, qiNiuService.find(),"");
                if ("".equals(url.toString())) {
                    url = url.append(qiniuContent.getUrl());
                }else{
                    url = url.append(","+qiniuContent.getUrl());
                }
            }
        }
        log.info("上传文件 url： "+url);
        Map<String, Object> map = new HashMap<>(2);
        map.put("errno", 0);
        map.put("link", url);
        return new ResponseEntity(map, HttpStatus.CREATED);
    }


}
