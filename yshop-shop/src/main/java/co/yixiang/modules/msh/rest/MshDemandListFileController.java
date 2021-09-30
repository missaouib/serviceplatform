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
import co.yixiang.modules.msh.domain.MshDemandListFile;
import co.yixiang.modules.msh.service.MshDemandListFileService;
import co.yixiang.modules.msh.service.dto.MshDemandListFileQueryCriteria;
import co.yixiang.modules.msh.service.dto.MshDemandListFileDto;
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
* @date 2020-12-25
*/
@AllArgsConstructor
@Api(tags = "需求单附件管理")
@RestController
@RequestMapping("/api/mshDemandListFile")
public class MshDemandListFileController {

    private final MshDemandListFileService mshDemandListFileService;
    private final IGenerator generator;


    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    public void download(HttpServletResponse response, MshDemandListFileQueryCriteria criteria) throws IOException {
        mshDemandListFileService.download(generator.convert(mshDemandListFileService.queryAll(criteria), MshDemandListFileDto.class), response);
    }

    @GetMapping
    @Log("查询需求单附件")
    @ApiOperation("查询需求单附件")
    public ResponseEntity<Object> getMshDemandListFiles(MshDemandListFileQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(mshDemandListFileService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增需求单附件")
    @ApiOperation("新增需求单附件")
    public ResponseEntity<Object> create(@Validated @RequestBody MshDemandListFile resources){
        return new ResponseEntity<>(mshDemandListFileService.save(resources),HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改需求单附件")
    @ApiOperation("修改需求单附件")
    public ResponseEntity<Object> update(@Validated @RequestBody MshDemandListFile resources){
        mshDemandListFileService.updateById(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除需求单附件")
    @ApiOperation("删除需求单附件")
    @DeleteMapping
    public ResponseEntity<Object> deleteAll(@RequestBody Integer[] ids) {
        Arrays.asList(ids).forEach(id->{
            mshDemandListFileService.removeById(id);
        });
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
