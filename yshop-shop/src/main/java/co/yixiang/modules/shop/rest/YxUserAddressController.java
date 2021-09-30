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
import co.yixiang.modules.shop.domain.YxUserAddress;
import co.yixiang.modules.shop.service.YxUserAddressService;
import co.yixiang.modules.shop.service.dto.YxUserAddressQueryCriteria;
import co.yixiang.modules.shop.service.dto.YxUserAddressDto;
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
* @date 2020-10-15
*/
@AllArgsConstructor
@Api(tags = "用户地址管理")
@RestController
@RequestMapping("/api/yxUserAddress")
public class YxUserAddressController {

    private final YxUserAddressService yxUserAddressService;
    private final IGenerator generator;


    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('admin','yxUserAddress:list')")
    public void download(HttpServletResponse response, YxUserAddressQueryCriteria criteria) throws IOException {
        yxUserAddressService.download(generator.convert(yxUserAddressService.queryAll(criteria), YxUserAddressDto.class), response);
    }

    @GetMapping
    @Log("查询用户地址")
    @ApiOperation("查询用户地址")
    @PreAuthorize("@el.check('admin','yxUserAddress:list')")
    public ResponseEntity<Object> getYxUserAddresss(YxUserAddressQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(yxUserAddressService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增用户地址")
    @ApiOperation("新增用户地址")
    @PreAuthorize("@el.check('admin','yxUserAddress:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody YxUserAddress resources){
        return new ResponseEntity<>(yxUserAddressService.save(resources),HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改用户地址")
    @ApiOperation("修改用户地址")
    @PreAuthorize("@el.check('admin','yxUserAddress:edit')")
    public ResponseEntity<Object> update(@Validated @RequestBody YxUserAddress resources){
        yxUserAddressService.updateById(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除用户地址")
    @ApiOperation("删除用户地址")
    @PreAuthorize("@el.check('admin','yxUserAddress:del')")
    @DeleteMapping
    public ResponseEntity<Object> deleteAll(@RequestBody Integer[] ids) {
        Arrays.asList(ids).forEach(id->{
            yxUserAddressService.removeById(id);
        });
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
