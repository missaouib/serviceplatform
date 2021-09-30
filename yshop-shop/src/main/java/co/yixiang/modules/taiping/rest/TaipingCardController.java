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
import co.yixiang.modules.taiping.domain.TaipingCard;
import co.yixiang.modules.taiping.service.TaipingCardService;
import co.yixiang.modules.taiping.service.dto.TaipingCardQueryCriteria;
import co.yixiang.modules.taiping.service.dto.TaipingCardDto;
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
* @date 2020-11-02
*/
@AllArgsConstructor
@Api(tags = "太平乐享虚拟卡管理")
@RestController
@RequestMapping("/api/taipingCard")
public class TaipingCardController {

    private final TaipingCardService taipingCardService;
    private final IGenerator generator;


    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('admin','taipingCard:list')")
    public void download(HttpServletResponse response, TaipingCardQueryCriteria criteria) throws IOException {
        taipingCardService.download(generator.convert(taipingCardService.queryAll(criteria), TaipingCardDto.class), response);
    }

    @GetMapping
    @Log("查询太平乐享虚拟卡")
    @ApiOperation("查询太平乐享虚拟卡")
    @PreAuthorize("@el.check('admin','taipingCard:list')")
    public ResponseEntity<Object> getTaipingCards(TaipingCardQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(taipingCardService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增太平乐享虚拟卡")
    @ApiOperation("新增太平乐享虚拟卡")
    @PreAuthorize("@el.check('admin','taipingCard:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody TaipingCard resources){
        return new ResponseEntity<>(taipingCardService.save(resources),HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改太平乐享虚拟卡")
    @ApiOperation("修改太平乐享虚拟卡")
    @PreAuthorize("@el.check('admin','taipingCard:edit')")
    public ResponseEntity<Object> update(@Validated @RequestBody TaipingCard resources){
        taipingCardService.updateById(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除太平乐享虚拟卡")
    @ApiOperation("删除太平乐享虚拟卡")
    @PreAuthorize("@el.check('admin','taipingCard:del')")
    @DeleteMapping
    public ResponseEntity<Object> deleteAll(@RequestBody Integer[] ids) {
        Arrays.asList(ids).forEach(id->{
            taipingCardService.removeById(id);
        });
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
