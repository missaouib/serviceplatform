/**
* Copyright (C) 2018-2020
* All rights reserved, Designed By www.yixiang.co
* 注意：
* 本软件为www.yixiang.co开发研制，未经购买不得使用
* 购买后可获得全部源代码（禁止转卖、分享、上传到码云、github等开源平台）
* 一经发现盗用、分享等行为，将追究法律责任，后果自负
*/
package co.yixiang.mp.rest;
import java.util.Arrays;
import co.yixiang.dozer.service.IGenerator;
import lombok.AllArgsConstructor;
import co.yixiang.mp.domain.YxWechatUserInfo;
import co.yixiang.mp.service.YxWechatUserInfoService;
import co.yixiang.mp.service.dto.YxWechatUserInfoQueryCriteria;
import co.yixiang.mp.service.dto.YxWechatUserInfoDto;
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
* @author visazhou
* @date 2020-12-27
*/
@AllArgsConstructor
@Api(tags = "微信公众号用户列表管理")
@RestController
@RequestMapping("/api/yxWechatUserInfo")
public class YxWechatUserInfoController {

    private final YxWechatUserInfoService yxWechatUserInfoService;
    private final IGenerator generator;



    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('admin','yxWechatUserInfo:list')")
    public void download(HttpServletResponse response, YxWechatUserInfoQueryCriteria criteria) throws IOException {
        yxWechatUserInfoService.download(generator.convert(yxWechatUserInfoService.queryAll(criteria), YxWechatUserInfoDto.class), response);
    }

    @GetMapping

    @ApiOperation("查询微信公众号用户列表")
    @PreAuthorize("@el.check('admin','yxWechatUserInfo:list')")
    public ResponseEntity<Object> getYxWechatUserInfos(YxWechatUserInfoQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(yxWechatUserInfoService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @PostMapping

    @ApiOperation("新增微信公众号用户列表")
    @PreAuthorize("@el.check('admin','yxWechatUserInfo:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody YxWechatUserInfo resources){
        return new ResponseEntity<>(yxWechatUserInfoService.save(resources),HttpStatus.CREATED);
    }

    @PutMapping

    @ApiOperation("修改微信公众号用户列表")
    @PreAuthorize("@el.check('admin','yxWechatUserInfo:edit')")
    public ResponseEntity<Object> update(@Validated @RequestBody YxWechatUserInfo resources){
        yxWechatUserInfoService.updateById(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }


    @ApiOperation("删除微信公众号用户列表")
    @PreAuthorize("@el.check('admin','yxWechatUserInfo:del')")
    @DeleteMapping
    public ResponseEntity<Object> deleteAll(@RequestBody Integer[] ids) {
        Arrays.asList(ids).forEach(id->{
            yxWechatUserInfoService.removeById(id);
        });
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
