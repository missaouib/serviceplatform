/**
* Copyright (C) 2018-2020
* All rights reserved, Designed By www.yixiang.co
* 注意：
* 本软件为www.yixiang.co开发研制，未经购买不得使用
* 购买后可获得全部源代码（禁止转卖、分享、上传到码云、github等开源平台）
* 一经发现盗用、分享等行为，将追究法律责任，后果自负
*/
package co.yixiang.modules.message.rest;
import java.util.Arrays;
import co.yixiang.dozer.service.IGenerator;
import lombok.AllArgsConstructor;
import co.yixiang.logging.aop.log.Log;
import co.yixiang.modules.message.domain.MessageUser;
import co.yixiang.modules.message.service.MessageUserService;
import co.yixiang.modules.message.service.dto.MessageUserQueryCriteria;
import co.yixiang.modules.message.service.dto.MessageUserDto;
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
* @date 2021-07-28
*/
@AllArgsConstructor
@Api(tags = "消息用户管理")
@RestController
@RequestMapping("/api/messageUser")
public class MessageUserController {

    private final MessageUserService messageUserService;
    private final IGenerator generator;


    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('admin','messageUser:list')")
    public void download(HttpServletResponse response, MessageUserQueryCriteria criteria) throws IOException {
        messageUserService.download(generator.convert(messageUserService.queryAll(criteria), MessageUserDto.class), response);
    }

    @GetMapping
    @Log("查询消息用户")
    @ApiOperation("查询消息用户")
    @PreAuthorize("@el.check('admin','messageUser:list')")
    public ResponseEntity<Object> getMessageUsers(MessageUserQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(messageUserService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增消息用户")
    @ApiOperation("新增消息用户")
    @PreAuthorize("@el.check('admin','messageUser:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody MessageUser resources){
        return new ResponseEntity<>(messageUserService.save(resources),HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改消息用户")
    @ApiOperation("修改消息用户")
    @PreAuthorize("@el.check('admin','messageUser:edit')")
    public ResponseEntity<Object> update(@Validated @RequestBody MessageUser resources){
        messageUserService.updateById(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除消息用户")
    @ApiOperation("删除消息用户")
    @PreAuthorize("@el.check('admin','messageUser:del')")
    @DeleteMapping
    public ResponseEntity<Object> deleteAll(@RequestBody Integer[] ids) {
        Arrays.asList(ids).forEach(id->{
            messageUserService.removeById(id);
        });
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
