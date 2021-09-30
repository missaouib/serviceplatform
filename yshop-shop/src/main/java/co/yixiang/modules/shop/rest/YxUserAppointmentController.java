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
import co.yixiang.modules.shop.domain.YxUserAppointment;
import co.yixiang.modules.shop.service.YxUserAppointmentService;
import co.yixiang.modules.shop.service.dto.YxUserAppointmentQueryCriteria;
import co.yixiang.modules.shop.service.dto.YxUserAppointmentDto;
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
* @date 2020-06-05
*/
@AllArgsConstructor
@Api(tags = "预约活动管理")
@RestController
@RequestMapping("/api/yxUserAppointment")
public class YxUserAppointmentController {

    private final YxUserAppointmentService yxUserAppointmentService;
    private final IGenerator generator;


    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('admin','yxUserAppointment:list')")
    public void download(HttpServletResponse response, YxUserAppointmentQueryCriteria criteria) throws IOException {
        yxUserAppointmentService.download(generator.convert(yxUserAppointmentService.queryAll(criteria), YxUserAppointmentDto.class), response);
    }

    @GetMapping
    @Log("查询预约活动")
    @ApiOperation("查询预约活动")
    @PreAuthorize("@el.check('admin','yxUserAppointment:list')")
    public ResponseEntity<Object> getYxUserAppointments(YxUserAppointmentQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(yxUserAppointmentService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增预约活动")
    @ApiOperation("新增预约活动")
    @PreAuthorize("@el.check('admin','yxUserAppointment:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody YxUserAppointment resources){
        return new ResponseEntity<>(yxUserAppointmentService.save(resources),HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改预约活动")
    @ApiOperation("修改预约活动")
    @PreAuthorize("@el.check('admin','yxUserAppointment:edit')")
    public ResponseEntity<Object> update(@Validated @RequestBody YxUserAppointment resources){
        yxUserAppointmentService.updateById(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除预约活动")
    @ApiOperation("删除预约活动")
    @PreAuthorize("@el.check('admin','yxUserAppointment:del')")
    @DeleteMapping
    public ResponseEntity<Object> deleteAll(@RequestBody Integer[] ids) {
        Arrays.asList(ids).forEach(id->{
            yxUserAppointmentService.removeById(id);
        });
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
