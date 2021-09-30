/**
* Copyright (C) 2018-2020
* All rights reserved, Designed By www.yixiang.co
* 注意：
* 本软件为www.yixiang.co开发研制，未经购买不得使用
* 购买后可获得全部源代码（禁止转卖、分享、上传到码云、github等开源平台）
* 一经发现盗用、分享等行为，将追究法律责任，后果自负
*/
package co.yixiang.modules.shop.rest;
import java.util.Arrays;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Validator;
import cn.hutool.core.util.StrUtil;
import co.yixiang.annotation.AnonymousAccess;
import co.yixiang.dozer.service.IGenerator;
import co.yixiang.exception.ErrorRequestException;
import co.yixiang.modules.shop.domain.ProjectSalesArea;
import co.yixiang.modules.shop.service.ProjectSalesAreaService;
import co.yixiang.utils.SecurityUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.AllArgsConstructor;
import co.yixiang.logging.aop.log.Log;
import co.yixiang.modules.shop.domain.Project;
import co.yixiang.modules.shop.service.ProjectService;
import co.yixiang.modules.shop.service.dto.ProjectQueryCriteria;
import co.yixiang.modules.shop.service.dto.ProjectDto;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import io.swagger.annotations.*;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletResponse;

/**
* @author visa
* @date 2021-02-25
*/
@AllArgsConstructor
@Api(tags = "项目管理")
@RestController
@RequestMapping("/api/project")
public class ProjectController {

    private final ProjectService projectService;
    private final IGenerator generator;
    private final ProjectSalesAreaService projectSalesAreaService;


    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('admin','project:list')")
    public void download(HttpServletResponse response, ProjectQueryCriteria criteria) throws IOException {
        projectService.download(generator.convert(projectService.queryAll(criteria), ProjectDto.class), response);
    }

    @GetMapping
    //@Log("查询项目")
    @ApiOperation("查询项目")
    @PreAuthorize("@el.check('admin','project:list')")
    public ResponseEntity<Object> getProjects(ProjectQueryCriteria criteria, Pageable pageable){
        String username = SecurityUtils.getUsername();

        // 获取当前用户的所有权限
        List<String> elPermissions = SecurityUtils.getUserDetails().getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList());
        Boolean adminFlag = elPermissions.contains("admin");

        if(!adminFlag) {  // 非admin权限，需要限制用户所能查询项目数据
            List<String> projectCodeList = projectService.queryProjectCode(username);
            if(CollUtil.isEmpty(projectCodeList)) {
                criteria.setProjectCode("无");
            }
            criteria.setProjectCodeList(projectCodeList);
        }



        return new ResponseEntity<>(projectService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增项目")
    @ApiOperation("新增项目")
    @PreAuthorize("@el.check('admin','project:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody Project resources){
        Boolean hasChinese =   Validator.hasChinese(resources.getProjectCode());
        if(hasChinese) {
            throw new ErrorRequestException("项目代码不能含有中文");
        }
        JSONObject jsonObject = JSON.parseObject(resources.getSiteInfo());
        resources.setSiteInfo(jsonObject==null?"":jsonObject.toJSONString());
        return new ResponseEntity<>(projectService.saveProject(resources),HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改项目")
    @ApiOperation("修改项目")
    @PreAuthorize("@el.check('admin','project:edit')")
    public ResponseEntity<Object> update(@Validated @RequestBody Project resources){
        Boolean hasChinese =   Validator.hasChinese(resources.getProjectCode());
        if(hasChinese) {
            throw new ErrorRequestException("项目代码不能含有中文");
        }
        JSONObject jsonObject = JSON.parseObject(resources.getSiteInfo());
        resources.setSiteInfo(jsonObject==null?"":jsonObject.toJSONString());
        projectService.saveProject(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除项目")
    @ApiOperation("删除项目")
    @PreAuthorize("@el.check('admin','project:del')")
    @DeleteMapping
    public ResponseEntity<Object> deleteAll(@RequestBody Integer[] ids) {
        Arrays.asList(ids).forEach(id->{
            Project project = projectService.getById(id);

            LambdaQueryWrapper<ProjectSalesArea> queryWrapper = new LambdaQueryWrapper();
            queryWrapper.eq(ProjectSalesArea::getProjectCode,project.getProjectCode());
            projectSalesAreaService.remove(queryWrapper);


            projectService.removeById(id);
        });
        return new ResponseEntity<>(HttpStatus.OK);
    }



    @ApiOperation("生成项目的小程序码")
    @AnonymousAccess
    @GetMapping("/generateQRCode/{projectNo}")
    public ResponseEntity<Object> generateQRCode(@PathVariable String projectNo,@RequestParam(defaultValue = "") String staffCode,@RequestParam(defaultValue = "1") String qrcodeType) {
        String result = "";
        if("1".equals(qrcodeType)) {  // 小程序码
            result = projectService.generateQRCode(projectNo,staffCode);
        }else if("2".equals(qrcodeType)) {   //普通二维码
            result = projectService.generateQRCodeH5(projectNo,staffCode);
        }

        return new ResponseEntity<>(result,HttpStatus.OK);
    }
}
