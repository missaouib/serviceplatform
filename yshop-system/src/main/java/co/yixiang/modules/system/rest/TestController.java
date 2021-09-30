/**
 * Copyright (C) 2018-2020
 * All rights reserved, Designed By www.yixiang.co

 */
package co.yixiang.modules.system.rest;

import cn.hutool.core.collection.CollectionUtil;
import co.yixiang.config.DataScope;
import co.yixiang.dozer.service.IGenerator;
import co.yixiang.exception.BadRequestException;
import co.yixiang.logging.aop.log.Log;
import co.yixiang.modules.system.domain.Dept;
import co.yixiang.modules.system.service.DeptService;
import co.yixiang.modules.system.service.dto.DeptDto;
import co.yixiang.modules.system.service.dto.DeptQueryCriteria;
import co.yixiang.utils.ValidationUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
* @author hupeng
* @date 2019-03-25
*/
@RestController
@Api(tags = "测试类")
@RequestMapping("/api/test")
public class TestController {

    @GetMapping(value = "/one")
    public ResponseEntity<Object> fanoutSender() throws Exception {
         return new ResponseEntity<>("ok",HttpStatus.OK);
    }
}
