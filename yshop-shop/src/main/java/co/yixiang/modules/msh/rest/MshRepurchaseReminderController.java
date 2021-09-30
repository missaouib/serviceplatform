/**
* Copyright (C) 2018-2020
* All rights reserved, Designed By www.yixiang.co
* 注意：
* 本软件为www.yixiang.co开发研制，未经购买不得使用
* 购买后可获得全部源代码（禁止转卖、分享、上传到码云、github等开源平台）
* 一经发现盗用、分享等行为，将追究法律责任，后果自负
*/
package co.yixiang.modules.msh.rest;
import java.util.Arrays;
import co.yixiang.dozer.service.IGenerator;
import lombok.AllArgsConstructor;
import co.yixiang.logging.aop.log.Log;
import co.yixiang.modules.msh.domain.MshRepurchaseReminder;
import co.yixiang.modules.msh.service.MshRepurchaseReminderService;
import co.yixiang.modules.msh.service.dto.MshRepurchaseReminderQueryCriteria;
import co.yixiang.modules.msh.service.dto.MshRepurchaseReminderQueryCriteria2;
import co.yixiang.modules.msh.service.dto.MshRepurchaseReminderDto;
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
* @author cq
* @date 2020-12-24
*/
@AllArgsConstructor
@Api(tags = "复购信息管理")
@RestController
@RequestMapping("/api/mshRepurchaseReminder")
public class MshRepurchaseReminderController {

    private final MshRepurchaseReminderService mshRepurchaseReminderService;
    private final IGenerator generator;


    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    public void download(HttpServletResponse response, MshRepurchaseReminderQueryCriteria2 criteria) throws IOException {
        mshRepurchaseReminderService.download(generator.convert(mshRepurchaseReminderService.queryList(criteria), MshRepurchaseReminderDto.class), response,criteria);
    }

    @PostMapping
    @Log("新增复购信息")
    @ApiOperation("新增复购信息")
    public ResponseEntity<Object> create(@Validated @RequestBody MshRepurchaseReminder resources){
        return new ResponseEntity<>(mshRepurchaseReminderService.save(resources),HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改复购信息")
    @ApiOperation("修改复购信息")
    public ResponseEntity<Object> update(@Validated @RequestBody MshRepurchaseReminder resources){
        mshRepurchaseReminderService.updateById(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除复购信息")
    @ApiOperation("删除复购信息")
    @DeleteMapping
    public ResponseEntity<Object> deleteAll(@RequestBody Integer[] ids) {
        Arrays.asList(ids).forEach(id->{
            mshRepurchaseReminderService.removeById(id);
        });
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping
    @Log("查询复购信息")
    @ApiOperation("查询复购信息")
    public ResponseEntity<Object> getMshRepurchaseReminders(MshRepurchaseReminderQueryCriteria2 criteria, Pageable pageable){
        return new ResponseEntity<>(mshRepurchaseReminderService.queryList(criteria,pageable),HttpStatus.OK);
    }
}
