/**
 * Copyright (C) 2018-2020
 * All rights reserved, Designed By www.yixiang.co

 */
package co.yixiang.tools.service.impl;

import cn.hutool.core.util.StrUtil;
import co.yixiang.dozer.service.IGenerator;
import co.yixiang.exception.BadRequestException;
import co.yixiang.tools.domain.QiniuConfig;
import co.yixiang.tools.domain.QiniuContent;
import co.yixiang.tools.service.QiNiuService;
import co.yixiang.tools.service.QiniuConfigService;
import co.yixiang.tools.service.QiniuContentService;
import co.yixiang.tools.service.dto.QiniuQueryCriteria;
import co.yixiang.tools.utils.QiNiuUtil;
import co.yixiang.utils.FileUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.storage.model.FileInfo;
import com.qiniu.util.Auth;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

/**
 * @author hupeng
 * @date 2018-12-31
 */
@Service
@Slf4j
//@CacheConfig(cacheNames = "qiNiu")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class QiNiuServiceImpl implements QiNiuService {

    private final QiniuConfigService qiniuConfigService;

    private final QiniuContentService qiniuContentService;

    private final IGenerator generator;

    @Value("${qiniu.max-size}")
    private Long maxSize;

    public QiNiuServiceImpl(QiniuConfigService qiniuConfigService, QiniuContentService qiniuContentService, IGenerator generator) {
        this.qiniuConfigService = qiniuConfigService;
        this.qiniuContentService = qiniuContentService;
        this.generator = generator;
    }

    @Override
//    @Cacheable
    public Object queryAll(QiniuQueryCriteria criteria, Pageable pageable){
        return qiniuContentService.queryAll(criteria,pageable);
    }

    @Override
    public List<QiniuContent> queryAll(QiniuQueryCriteria criteria) {
        return qiniuContentService.queryAll(criteria);
    }

    @Override
//    @Cacheable(key = "'1'")
    public QiniuConfig find() {
        QiniuConfig qiniuConfig = qiniuConfigService.getById(1L);
        return qiniuConfig;
    }

    @Override
//    @CachePut(cacheNames = "qiNiuConfig", key = "'1'")
    @Transactional(rollbackFor = Exception.class)
    public QiniuConfig update(QiniuConfig qiniuConfig) {
        String http = "http://", https = "https://";
        if (!(qiniuConfig.getHost().toLowerCase().startsWith(http)||qiniuConfig.getHost().toLowerCase().startsWith(https))) {
            throw new BadRequestException("?????????????????????http://??????https://??????");
        }
        qiniuConfig.setId(1L);
        qiniuConfigService.saveOrUpdate(qiniuConfig);
        return qiniuConfig;
    }

    @Override
//    @CacheEvict(allEntries = true)
    @Transactional(rollbackFor = Exception.class)
    public QiniuContent upload(MultipartFile file, QiniuConfig qiniuConfig,String filename) {
        FileUtil.checkSize(maxSize, file.getSize());
        if(qiniuConfig.getId() == null){
            throw new BadRequestException("????????????????????????????????????");
        }
        // ?????????????????????Zone??????????????????
        Configuration cfg = new Configuration(QiNiuUtil.getRegion(qiniuConfig.getZone()));
        UploadManager uploadManager = new UploadManager(cfg);
        Auth auth = Auth.create(qiniuConfig.getAccessKey(), qiniuConfig.getSecretKey());
        String upToken = auth.uploadToken(qiniuConfig.getBucket());
        try {
            String key = "";
            if(StrUtil.isBlank(filename)) {
                key = file.getOriginalFilename();
                String suffix =  FileUtil.getExtensionName(key);

                key =  UUID.randomUUID().toString().replace("-","") + "." + suffix;
            } else {
                key = filename;
            }

            if(qiniuContentService.getOne(new QueryWrapper<QiniuContent>().eq("name",key)) != null) {
                key = QiNiuUtil.getKey(key);
            }
            Response response = uploadManager.put(file.getBytes(), key, upToken);
            //???????????????????????????

            DefaultPutRet putRet = JSON.parseObject(response.bodyString(), DefaultPutRet.class);

            QiniuContent content = qiniuContentService.getOne(new QueryWrapper<QiniuContent>().eq("name",FileUtil.getFileNameNoEx(putRet.key)));
            if (content == null) {
                //???????????????
                QiniuContent qiniuContent = new QiniuContent();
                qiniuContent.setSuffix(FileUtil.getExtensionName(putRet.key));
                qiniuContent.setBucket(qiniuConfig.getBucket());
                qiniuContent.setType(qiniuConfig.getType());
                qiniuContent.setName(FileUtil.getFileNameNoEx(putRet.key));
                qiniuContent.setUrl(qiniuConfig.getHost()+"/"+putRet.key);
                qiniuContent.setSize(FileUtil.getSize(Integer.parseInt(file.getSize()+"")));
                qiniuContentService.save(qiniuContent);
                return qiniuContent;
            }
            return content;
        } catch (Exception e) {
           throw new BadRequestException(e.getMessage());
        }
    }

    @Override
//    @Cacheable
    public QiniuContent findByContentId(Long id) {
        QiniuContent qiniuContent = qiniuContentService.getById(id);
        return qiniuContent;
    }

    @Override
//    @Cacheable
    public String download(QiniuContent content,QiniuConfig config){
        String finalUrl;
        String type = "??????";
        if(type.equals(content.getType())){
            finalUrl  = content.getUrl();
        } else {
            Auth auth = Auth.create(config.getAccessKey(), config.getSecretKey());
            // 1??????????????????????????????????????????
            long expireInSeconds = 3600;
            finalUrl = auth.privateDownloadUrl(content.getUrl(), expireInSeconds);
        }
        return finalUrl;
    }

    @Override
//    @CacheEvict(allEntries = true)
    @Transactional(rollbackFor = Exception.class)
    public void delete(QiniuContent content, QiniuConfig config) {
        //?????????????????????Zone??????????????????
        Configuration cfg = new Configuration(QiNiuUtil.getRegion(config.getZone()));
        Auth auth = Auth.create(config.getAccessKey(), config.getSecretKey());
        BucketManager bucketManager = new BucketManager(auth, cfg);
        try {
            bucketManager.delete(content.getBucket(), content.getName() + "." + content.getSuffix());
            qiniuContentService.removeById(content.getId());
        } catch (QiniuException ex) {
            qiniuConfigService.removeById(content.getId());
        }
    }

    @Override
//    @CacheEvict(allEntries = true)
    @Transactional(rollbackFor = Exception.class)
    public void synchronize(QiniuConfig config) {
        if(config.getId() == null){
            throw new BadRequestException("????????????????????????????????????");
        }
        //?????????????????????Zone??????????????????
        Configuration cfg = new Configuration(QiNiuUtil.getRegion(config.getZone()));
        Auth auth = Auth.create(config.getAccessKey(), config.getSecretKey());
        BucketManager bucketManager = new BucketManager(auth, cfg);
        //???????????????
        String prefix = "";
        //????????????????????????????????????1000???????????? 1000
        int limit = 1000;
        //?????????????????????????????????????????????????????????????????????????????????????????????????????????
        String delimiter = "";
        //????????????????????????
        BucketManager.FileListIterator fileListIterator = bucketManager.createFileListIterator(config.getBucket(), prefix, limit, delimiter);
        while (fileListIterator.hasNext()) {
            //???????????????file list??????
            QiniuContent qiniuContent;
            FileInfo[] items = fileListIterator.next();
            for (FileInfo item : items) {
                if(qiniuContentService.getOne(new QueryWrapper<QiniuContent>().eq("name",FileUtil.getFileNameNoEx(item.key)))
                        == null){
                    qiniuContent = new QiniuContent();
                    qiniuContent.setSize(FileUtil.getSize(Integer.parseInt(item.fsize+"")));
                    qiniuContent.setSuffix(FileUtil.getExtensionName(item.key));
                    qiniuContent.setName(FileUtil.getFileNameNoEx(item.key));
                    qiniuContent.setType(config.getType());
                    qiniuContent.setBucket(config.getBucket());
                    qiniuContent.setUrl(config.getHost()+"/"+item.key);
                    qiniuContentService.save(qiniuContent);
                }
            }
        }
    }

    @Override
//    @CacheEvict(allEntries = true)
    public void deleteAll(Long[] ids, QiniuConfig config) {
        for (Long id : ids) {
            delete(findByContentId(id), config);
        }
    }

    @Override
//    @CacheEvict(allEntries = true)
    @Transactional(rollbackFor = Exception.class)
    public void update(String type) {
        qiniuConfigService.update(type);
    }

    @Override
    public void downloadList(List<QiniuContent> queryAll, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (QiniuContent content : queryAll) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("?????????", content.getName());
            map.put("????????????", content.getSuffix());
            map.put("????????????", content.getBucket());
            map.put("????????????", content.getSize());
            map.put("????????????", content.getType());
            map.put("????????????", content.getUpdateTime());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

    @Override
    public QiniuContent uploadByUrl(String url_parm, QiniuConfig qiniuConfig) {
        log.info("url_parm={}",url_parm);
        HttpURLConnection conn = null;
        try {
            URL url = new URL(url_parm);
            conn = (HttpURLConnection) url.openConnection();
//?????????????????????"GET"
            conn.setRequestMethod("GET");
//?????????????????????5???
            conn.setConnectTimeout(5 * 1000);
//?????????????????????????????????
            InputStream inStream = conn.getInputStream();
            String suffix =  FileUtil.getExtensionName(url_parm);
            String name =  UUID.randomUUID().toString().replace("-","") + "." + suffix;
          //  String name = UUID.randomUUID().toString().replace("-","")+".jpg";
            MultipartFile multipartFile = new MockMultipartFile(name,name,"", inStream);
           // log.info("multipartFile={}",multipartFile);
            log.info("multipartFile.getSize={}",multipartFile.getSize());
            return upload(multipartFile,qiniuConfig,"");
        }catch (Exception e) {
            e.printStackTrace();
        }


        return null;
    }

    //??????url????????????
    public void downloadPicture(String urlList,String path) {
        URL url = null;
        try {
            url = new URL(urlList);
            DataInputStream dataInputStream = new DataInputStream(url.openStream());

            FileOutputStream fileOutputStream = new FileOutputStream(new File(path));
            ByteArrayOutputStream output = new ByteArrayOutputStream();

            byte[] buffer = new byte[1024];
            int length;

            while ((length = dataInputStream.read(buffer)) > 0) {
                output.write(buffer, 0, length);
            }
            fileOutputStream.write(output.toByteArray());
            dataInputStream.close();
            fileOutputStream.close();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
