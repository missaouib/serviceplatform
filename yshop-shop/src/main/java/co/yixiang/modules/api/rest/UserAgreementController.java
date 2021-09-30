/**
* Copyright (C) 2018-2020
* All rights reserved, Designed By www.yixiang.co
* 注意：
* 本软件为www.yixiang.co开发研制，未经购买不得使用
* 购买后可获得全部源代码（禁止转卖、分享、上传到码云、github等开源平台）
* 一经发现盗用、分享等行为，将追究法律责任，后果自负
*/
package co.yixiang.modules.api.rest;
import java.util.Arrays;
import co.yixiang.dozer.service.IGenerator;
import lombok.AllArgsConstructor;
import co.yixiang.logging.aop.log.Log;
import co.yixiang.modules.api.domain.UserAgreement;
import co.yixiang.modules.api.service.UserAgreementService;
import co.yixiang.modules.api.service.dto.UserAgreementQueryCriteria;
import co.yixiang.modules.api.service.dto.UserAgreementDto;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import io.swagger.annotations.*;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;

/**
* @author visa
* @date 2020-11-30
*/
@AllArgsConstructor
@Api(tags = "用户同意书管理")
@RestController
@RequestMapping("/api/userAgreement")
public class UserAgreementController {

    private final UserAgreementService userAgreementService;
    private final IGenerator generator;


    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('admin','userAgreement:list')")
    public void download(HttpServletResponse response, UserAgreementQueryCriteria criteria) throws IOException {
        userAgreementService.download(generator.convert(userAgreementService.queryAll(criteria), UserAgreementDto.class), response);
    }

    @GetMapping
    @Log("查询用户同意书")
    @ApiOperation("查询用户同意书")
    @PreAuthorize("@el.check('admin','userAgreement:list')")
    public ResponseEntity<Object> getUserAgreements(UserAgreementQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(userAgreementService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增用户同意书")
    @ApiOperation("新增用户同意书")
    @PreAuthorize("@el.check('admin','userAgreement:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody UserAgreement resources){
        return new ResponseEntity<>(userAgreementService.save(resources),HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改用户同意书")
    @ApiOperation("修改用户同意书")
    @PreAuthorize("@el.check('admin','userAgreement:edit')")
    public ResponseEntity<Object> update(@Validated @RequestBody UserAgreement resources){
        userAgreementService.updateById(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除用户同意书")
    @ApiOperation("删除用户同意书")
    @PreAuthorize("@el.check('admin','userAgreement:del')")
    @DeleteMapping
    public ResponseEntity<Object> deleteAll(@RequestBody Integer[] ids) {
        Arrays.asList(ids).forEach(id->{
            userAgreementService.removeById(id);
        });
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
