/**
* Copyright (C) 2018-2020
* All rights reserved, Designed By www.yixiang.co
* 注意：
* 本软件为www.yixiang.co开发研制，未经购买不得使用
* 购买后可获得全部源代码（禁止转卖、分享、上传到码云、github等开源平台）
* 一经发现盗用、分享等行为，将追究法律责任，后果自负
*/
package co.yixiang.modules.yaoshitong.rest;
import java.util.Arrays;

import co.yixiang.annotation.AnonymousAccess;
import co.yixiang.dozer.service.IGenerator;
import lombok.AllArgsConstructor;
import co.yixiang.logging.aop.log.Log;
import co.yixiang.modules.yaoshitong.domain.YaoshitongRepurchaseReminder;
import co.yixiang.modules.yaoshitong.service.YaoshitongRepurchaseReminderService;
import co.yixiang.modules.yaoshitong.service.dto.YaoshitongRepurchaseReminderQueryCriteria;
import co.yixiang.modules.yaoshitong.service.dto.YaoshitongRepurchaseReminderDto;
import org.springframework.beans.factory.annotation.Autowired;
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
* @date 2020-10-21
*/
@AllArgsConstructor
@Api(tags = "药品复购提醒管理")
@RestController
@RequestMapping("/api/yaoshitongRepurchaseReminder")
public class YaoshitongRepurchaseReminderController {

    private final YaoshitongRepurchaseReminderService yaoshitongRepurchaseReminderService;
    private final IGenerator generator;


    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('admin','yaoshitongRepurchaseReminder:list')")
    public void download(HttpServletResponse response, YaoshitongRepurchaseReminderQueryCriteria criteria) throws IOException {
        yaoshitongRepurchaseReminderService.download(generator.convert(yaoshitongRepurchaseReminderService.queryAll(criteria), YaoshitongRepurchaseReminderDto.class), response);
    }

    @GetMapping
    @Log("查询药品复购提醒")
    @ApiOperation("查询药品复购提醒")
    @PreAuthorize("@el.check('admin','yaoshitongRepurchaseReminder:list')")
    public ResponseEntity<Object> getYaoshitongRepurchaseReminders(YaoshitongRepurchaseReminderQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(yaoshitongRepurchaseReminderService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增药品复购提醒")
    @ApiOperation("新增药品复购提醒")
    @PreAuthorize("@el.check('admin','yaoshitongRepurchaseReminder:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody YaoshitongRepurchaseReminder resources){
        return new ResponseEntity<>(yaoshitongRepurchaseReminderService.save(resources),HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改药品复购提醒")
    @ApiOperation("修改药品复购提醒")
    @PreAuthorize("@el.check('admin','yaoshitongRepurchaseReminder:edit')")
    public ResponseEntity<Object> update(@Validated @RequestBody YaoshitongRepurchaseReminder resources){
        yaoshitongRepurchaseReminderService.updateById(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除药品复购提醒")
    @ApiOperation("删除药品复购提醒")
    @PreAuthorize("@el.check('admin','yaoshitongRepurchaseReminder:del')")
    @DeleteMapping
    public ResponseEntity<Object> deleteAll(@RequestBody Integer[] ids) {
        Arrays.asList(ids).forEach(id->{
            yaoshitongRepurchaseReminderService.removeById(id);
        });
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping(value = "/auto")
    @ApiOperation("自动生成药品复购提醒")
    @AnonymousAccess
    public ResponseEntity<Object> createAuto(){
        return new ResponseEntity<>(yaoshitongRepurchaseReminderService.generateReminder(),HttpStatus.OK);
    }


    @PostMapping(value = "/medCycleNotice")
    @ApiOperation("用药周期提醒")
    @AnonymousAccess
    public ResponseEntity<Object> medCycleNotice(){
        return new ResponseEntity<>(yaoshitongRepurchaseReminderService.medCycleNotice(),HttpStatus.OK);
    }
}
