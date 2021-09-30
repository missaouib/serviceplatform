/**
* Copyright (C) 2018-2020
* All rights reserved, Designed By www.yixiang.co
* 注意：
* 本软件为www.yixiang.co开发研制，未经购买不得使用
* 购买后可获得全部源代码（禁止转卖、分享、上传到码云、github等开源平台）
* 一经发现盗用、分享等行为，将追究法律责任，后果自负
*/
package co.yixiang.modules.taibao.rest;
import java.util.Arrays;
import co.yixiang.dozer.service.IGenerator;
import lombok.AllArgsConstructor;
import co.yixiang.logging.aop.log.Log;
import co.yixiang.modules.taibao.domain.TbBlackUser;
import co.yixiang.modules.taibao.service.TbBlackUserService;
import co.yixiang.modules.taibao.service.dto.TbBlackUserQueryCriteria;
import co.yixiang.modules.taibao.service.dto.TbBlackUserDto;
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
* @author zhoujinlai
* @date 2021-05-27
*/
@AllArgsConstructor
@Api(tags = "太保安联-黑名单管理")
@RestController
@RequestMapping("/api/tbBlackUser")
public class TbBlackUserController {

    private final TbBlackUserService tbBlackUserService;
    private final IGenerator generator;


    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('admin','tbBlackUser:list')")
    public void download(HttpServletResponse response, TbBlackUserQueryCriteria criteria) throws IOException {
        tbBlackUserService.download(generator.convert(tbBlackUserService.queryAll(criteria), TbBlackUserDto.class), response);
    }

    @GetMapping
    @Log("查询太保安联-黑名单")
    @ApiOperation("查询太保安联-黑名单")
    @PreAuthorize("@el.check('admin','tbBlackUser:list')")
    public ResponseEntity<Object> getTbBlackUsers(TbBlackUserQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(tbBlackUserService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增太保安联-黑名单")
    @ApiOperation("新增太保安联-黑名单")
    @PreAuthorize("@el.check('admin','tbBlackUser:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody TbBlackUser resources){
        return new ResponseEntity<>(tbBlackUserService.save(resources),HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改太保安联-黑名单")
    @ApiOperation("修改太保安联-黑名单")
    @PreAuthorize("@el.check('admin','tbBlackUser:edit')")
    public ResponseEntity<Object> update(@Validated @RequestBody TbBlackUser resources){
        tbBlackUserService.updateById(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除太保安联-黑名单")
    @ApiOperation("删除太保安联-黑名单")
    @PreAuthorize("@el.check('admin','tbBlackUser:del')")
    @DeleteMapping
    public ResponseEntity<Object> deleteAll(@RequestBody Integer[] ids) {
        Arrays.asList(ids).forEach(id->{
            tbBlackUserService.removeById(id);
        });
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
