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
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.AllArgsConstructor;
import co.yixiang.logging.aop.log.Log;
import co.yixiang.modules.shop.domain.YxExpressTemplateDetail;
import co.yixiang.modules.shop.service.YxExpressTemplateDetailService;
import co.yixiang.modules.shop.service.dto.YxExpressTemplateDetailQueryCriteria;
import co.yixiang.modules.shop.service.dto.YxExpressTemplateDetailDto;
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
* @date 2020-11-28
*/
@AllArgsConstructor
@Api(tags = "物流模板明细管理")
@RestController
@RequestMapping("/api/yxExpressTemplateDetail")
public class YxExpressTemplateDetailController {

    private final YxExpressTemplateDetailService yxExpressTemplateDetailService;
    private final IGenerator generator;


    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    public void download(HttpServletResponse response, YxExpressTemplateDetailQueryCriteria criteria) throws IOException {
        yxExpressTemplateDetailService.download(generator.convert(yxExpressTemplateDetailService.queryAll(criteria), YxExpressTemplateDetailDto.class), response);
    }

    @GetMapping
    @Log("查询物流模板明细")
    @ApiOperation("查询物流模板明细")
    public ResponseEntity<Object> getYxExpressTemplateDetails(YxExpressTemplateDetailQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(yxExpressTemplateDetailService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增物流模板明细")
    @ApiOperation("新增物流模板明细")
    public ResponseEntity<Object> create(@Validated @RequestBody YxExpressTemplateDetail resources){
        return new ResponseEntity<>(yxExpressTemplateDetailService.save(resources),HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改物流模板明细")
    @ApiOperation("修改物流模板明细")
    public ResponseEntity<Object> update(@Validated @RequestBody YxExpressTemplateDetail resources){
        yxExpressTemplateDetailService.updateById(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除物流模板明细")
    @ApiOperation("删除物流模板明细")
    @DeleteMapping
    public ResponseEntity<Object> deleteAll(@RequestBody Integer[] ids) {
        Arrays.asList(ids).forEach(id->{
            YxExpressTemplateDetail yxExpressTemplateDetail = yxExpressTemplateDetailService.getById(id);
            String parentAreaName = yxExpressTemplateDetail.getAreaName();
            Integer templateId = yxExpressTemplateDetail.getTemplateId();
            LambdaQueryWrapper<YxExpressTemplateDetail> lambdaQueryWrapper = new LambdaQueryWrapper();
            lambdaQueryWrapper.eq(YxExpressTemplateDetail::getTemplateId,templateId);
            lambdaQueryWrapper.eq(YxExpressTemplateDetail::getParentAreaName,parentAreaName);
            lambdaQueryWrapper.eq(YxExpressTemplateDetail::getLevel,2);
            yxExpressTemplateDetailService.remove(lambdaQueryWrapper);
            yxExpressTemplateDetailService.removeById(id);
        });
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
