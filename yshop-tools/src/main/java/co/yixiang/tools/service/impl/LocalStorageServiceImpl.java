/**
 * Copyright (C) 2018-2020
 * All rights reserved, Designed By www.yixiang.co

 */
package co.yixiang.tools.service.impl;

import cn.hutool.core.util.ObjectUtil;
import co.yixiang.common.service.impl.BaseServiceImpl;
import co.yixiang.common.utils.QueryHelpPlus;
import co.yixiang.dozer.service.IGenerator;
import co.yixiang.exception.BadRequestException;
import co.yixiang.tools.domain.LocalStorage;
import co.yixiang.tools.service.LocalStorageService;
import co.yixiang.tools.service.dto.LocalStorageDto;
import co.yixiang.tools.service.dto.LocalStorageQueryCriteria;
import co.yixiang.tools.service.mapper.LocalStorageMapper;
import co.yixiang.utils.FileUtil;
import co.yixiang.utils.SecurityUtils;
import co.yixiang.utils.StringUtils;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
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
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

// 默认不使用缓存
//import org.springframework.cache.annotation.CacheConfig;
//import org.springframework.cache.annotation.CacheEvict;
//import org.springframework.cache.annotation.Cacheable;

/**
* @author hupeng
* @date 2020-05-13
*/
@Service
@Slf4j
//@CacheConfig(cacheNames = "localStorage")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class LocalStorageServiceImpl extends BaseServiceImpl<LocalStorageMapper, LocalStorage> implements LocalStorageService {

    private final IGenerator generator;
    @Value("${file.path}")
    private String path;

    @Value("${file.maxSize}")
    private long maxSize;

    public LocalStorageServiceImpl(IGenerator generator) {
        this.generator = generator;
    }

    @Override
    //@Cacheable
    public Map<String, Object> queryAll(LocalStorageQueryCriteria criteria, Pageable pageable) {
        getPage(pageable);
        PageInfo<LocalStorage> page = new PageInfo<>(baseMapper.selectList(QueryHelpPlus.getPredicate(LocalStorage.class, criteria)));
        Map<String, Object> map = new LinkedHashMap<>(2);
        map.put("content", generator.convert(page.getList(), LocalStorageDto.class));
        map.put("totalElements", page.getTotal());
        return map;
    }


    @Override
    //@Cacheable
    public List<LocalStorageDto> queryAll(LocalStorageQueryCriteria criteria){
        return generator.convert(baseMapper.selectList(QueryHelpPlus.getPredicate(LocalStorage.class, criteria)),LocalStorageDto.class);
    }

    @Override
    public LocalStorageDto findById(Long id) {
        LocalStorage localStorage = this.getById(id);
        return generator.convert(localStorage,LocalStorageDto.class);
    }

    @Override
    public LocalStorageDto create(String name, MultipartFile multipartFile) {
        FileUtil.checkSize(maxSize, multipartFile.getSize());
        String suffix = FileUtil.getExtensionName(multipartFile.getOriginalFilename());
        log.info("上传文件 name： "+name);
        log.info("上传文件 suffix： "+suffix);

        String type = FileUtil.getFileType(suffix);
        String fileName=null;
        String filePath=null;
        if("image".equals(suffix)){
            try {
                StringBuffer nowStr = FileUtil.fileRename();
                fileName = nowStr + "." + suffix;

                String url = path + type +  File.separator + fileName;
                byte[] bytes = multipartFile.getBytes();
                InputStream fis = new ByteArrayInputStream(bytes);
                FileOutputStream fs = new FileOutputStream(url);

                    //执行fileOutputStream的输出操作
                int len = 1;
                byte[] b = new byte[1024];
                while ((len = fis.read(b)) != -1) {
                    fs.write(b, 0, len);
                }
                fs.close();
                fis.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            File file = FileUtil.upload(multipartFile, path + type +  File.separator);
            if(ObjectUtil.isNull(file)){
                throw new BadRequestException("上传失败");
            }
            fileName=file.getName();
            filePath=file.getPath();
        }

        String userName = "system";
        try {
            userName = SecurityUtils.getUsername();
        }catch (Exception e) {

        }
        try {
            name = StringUtils.isBlank(name) ? FileUtil.getFileNameNoEx(multipartFile.getOriginalFilename()) : name;
            LocalStorage localStorage = new LocalStorage(
                    fileName,
                    name,
                    suffix,
                    filePath,
                    type,
                    FileUtil.getSize(multipartFile.getSize()),
                    userName
            );
            this.save(localStorage);
            return generator.convert(localStorage,LocalStorageDto.class);
        }catch (Exception e){
            throw e;
        }
    }


    @Override
    public LocalStorageDto create2signPdf(String name, MultipartFile multipartFile) {
        FileUtil.checkSize(maxSize, multipartFile.getSize());
        // String suffix = FileUtil.getExtensionName(multipartFile.getOriginalFilename());
        String suffix = "pdf";
        String type = "signPdf";
        File file = FileUtil.upload(multipartFile, path + type +  File.separator,name);
        if(ObjectUtil.isNull(file)){
            throw new BadRequestException("上传失败");
        }
        String userName = "system";
        try {
            userName = SecurityUtils.getUsername();
        }catch (Exception e) {

        }
        try {
            name = StringUtils.isBlank(name) ? FileUtil.getFileNameNoEx(multipartFile.getOriginalFilename()) : name;
            LocalStorage localStorage = new LocalStorage(
                    file.getName(),
                    name,
                    suffix,
                    file.getPath(),
                    type,
                    FileUtil.getSize(multipartFile.getSize()),
                    userName
            );
            this.save(localStorage);
            return generator.convert(localStorage,LocalStorageDto.class);
        }catch (Exception e){
            FileUtil.del(file);
            throw e;
        }
    }

    @Override
    public LocalStorageDto createByUrl(String url_parm, String name) {

        HttpURLConnection conn = null;
        try {
            URL url = new URL(url_parm);
            conn = (HttpURLConnection) url.openConnection();
//设置请求方式为"GET"
            conn.setRequestMethod("GET");
//超时响应时间为5秒
            conn.setConnectTimeout(5 * 1000);
//通过输入流获取图片数据
            InputStream inStream = conn.getInputStream();

            MultipartFile multipartFile = new MockMultipartFile(name,name,"", inStream);

            return create(name,multipartFile);
        }catch (Exception e) {
            e.printStackTrace();
        }


        return null;
    }

    @Override
    public void deleteAll(Long[] ids) {
        for (Long id : ids) {
            LocalStorage storage = this.getById(id);
            FileUtil.del(storage.getPath());
            this.removeById(id);
        }
    }


    @Override
    public void download(List<LocalStorageDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (LocalStorageDto localStorage : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("文件真实的名称", localStorage.getRealName());
            map.put("文件名", localStorage.getName());
            map.put("后缀", localStorage.getSuffix());
//            map.put("路径", localStorage.getPath());
            map.put("类型", localStorage.getType());
            map.put("大小", localStorage.getSize());
            map.put("操作人", localStorage.getOperate());
            map.put("创建日期", localStorage.getCreateTime());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

    @Override
    public void updateLocalStorage(LocalStorageDto resources) {
        LocalStorage localStorage = this.getById(resources.getId());
        BeanUtils.copyProperties(resources,localStorage);
        this.saveOrUpdate(localStorage);
    }
}
