/**
* Copyright (C) 2018-2020
* All rights reserved, Designed By www.yixiang.co
* 注意：
* 本软件为www.yixiang.co开发研制，未经购买不得使用
* 购买后可获得全部源代码（禁止转卖、分享、上传到码云、github等开源平台）
* 一经发现盗用、分享等行为，将追究法律责任，后果自负
*/
package co.yixiang.modules.taiping.rest;
import java.util.Arrays;
import co.yixiang.dozer.service.IGenerator;
import lombok.AllArgsConstructor;
import co.yixiang.logging.aop.log.Log;
import co.yixiang.modules.taiping.domain.YxStoreCouponCard;
import co.yixiang.modules.taiping.service.YxStoreCouponCardService;
import co.yixiang.modules.taiping.service.dto.YxStoreCouponCardQueryCriteria;
import co.yixiang.modules.taiping.service.dto.YxStoreCouponCardDto;
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
* @date 2020-12-10
*/
@AllArgsConstructor
@Api(tags = "卡号对应的优惠券记录表管理")
@RestController
@RequestMapping("/api/yxStoreCouponCard")
public class YxStoreCouponCardController {

    private final YxStoreCouponCardService yxStoreCouponCardService;
    private final IGenerator generator;


    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('admin','yxStoreCouponCard:list')")
    public void download(HttpServletResponse response, YxStoreCouponCardQueryCriteria criteria) throws IOException {
        yxStoreCouponCardService.download(generator.convert(yxStoreCouponCardService.queryAll(criteria), YxStoreCouponCardDto.class), response);
    }

    @GetMapping
    @Log("查询卡号对应的优惠券记录表")
    @ApiOperation("查询卡号对应的优惠券记录表")
    @PreAuthorize("@el.check('admin','yxStoreCouponCard:list')")
    public ResponseEntity<Object> getYxStoreCouponCards(YxStoreCouponCardQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(yxStoreCouponCardService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增卡号对应的优惠券记录表")
    @ApiOperation("新增卡号对应的优惠券记录表")
    @PreAuthorize("@el.check('admin','yxStoreCouponCard:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody YxStoreCouponCard resources){
        return new ResponseEntity<>(yxStoreCouponCardService.save(resources),HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改卡号对应的优惠券记录表")
    @ApiOperation("修改卡号对应的优惠券记录表")
    @PreAuthorize("@el.check('admin','yxStoreCouponCard:edit')")
    public ResponseEntity<Object> update(@Validated @RequestBody YxStoreCouponCard resources){
        yxStoreCouponCardService.updateById(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除卡号对应的优惠券记录表")
    @ApiOperation("删除卡号对应的优惠券记录表")
    @PreAuthorize("@el.check('admin','yxStoreCouponCard:del')")
    @DeleteMapping
    public ResponseEntity<Object> deleteAll(@RequestBody Integer[] ids) {
        Arrays.asList(ids).forEach(id->{
            yxStoreCouponCardService.removeById(id);
        });
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
