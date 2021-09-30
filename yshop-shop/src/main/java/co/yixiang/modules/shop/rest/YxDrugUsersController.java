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
import co.yixiang.dozer.service.IGenerator;
import lombok.AllArgsConstructor;
import co.yixiang.logging.aop.log.Log;
import co.yixiang.modules.shop.domain.YxDrugUsers;
import co.yixiang.modules.shop.service.YxDrugUsersService;
import co.yixiang.modules.shop.service.dto.YxDrugUsersQueryCriteria;
import co.yixiang.modules.shop.service.dto.YxDrugUsersDto;
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
* @date 2021-02-08
*/
@AllArgsConstructor
@Api(tags = "用药人管理")
@RestController
@RequestMapping("/api/yxDrugUsers")
public class YxDrugUsersController {

    private final YxDrugUsersService yxDrugUsersService;
    private final IGenerator generator;


    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('admin','yxDrugUsers:list')")
    public void download(HttpServletResponse response, YxDrugUsersQueryCriteria criteria) throws IOException {
        yxDrugUsersService.download(generator.convert(yxDrugUsersService.queryAll(criteria), YxDrugUsersDto.class), response);
    }

    @GetMapping
    @Log("查询用药人")
    @ApiOperation("查询用药人")
    @PreAuthorize("@el.check('admin','yxDrugUsers:list')")
    public ResponseEntity<Object> getYxDrugUserss(YxDrugUsersQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(yxDrugUsersService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增用药人")
    @ApiOperation("新增用药人")
    @PreAuthorize("@el.check('admin','yxDrugUsers:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody YxDrugUsers resources){
        return new ResponseEntity<>(yxDrugUsersService.save(resources),HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改用药人")
    @ApiOperation("修改用药人")
    @PreAuthorize("@el.check('admin','yxDrugUsers:edit')")
    public ResponseEntity<Object> update(@Validated @RequestBody YxDrugUsers resources){
        yxDrugUsersService.updateById(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除用药人")
    @ApiOperation("删除用药人")
    @PreAuthorize("@el.check('admin','yxDrugUsers:del')")
    @DeleteMapping
    public ResponseEntity<Object> deleteAll(@RequestBody Integer[] ids) {
        Arrays.asList(ids).forEach(id->{
            yxDrugUsersService.removeById(id);
        });
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
