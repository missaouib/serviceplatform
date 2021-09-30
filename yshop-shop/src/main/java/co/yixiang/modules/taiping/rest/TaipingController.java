/**
* Copyright (C) 2018-2020
* All rights reserved, Designed By www.yixiang.co
* 注意：
* 本软件为www.yixiang.co开发研制，未经购买不得使用
* 购买后可获得全部源代码（禁止转卖、分享、上传到码云、github等开源平台）
* 一经发现盗用、分享等行为，将追究法律责任，后果自负
*/
package co.yixiang.modules.taiping.rest;

import co.yixiang.annotation.AnonymousAccess;
import co.yixiang.logging.aop.log.Log;
import co.yixiang.modules.taiping.service.TaipingCardService;
import co.yixiang.modules.taiping.service.TaipingPayableService;
import co.yixiang.modules.taiping.service.dto.TaipingDataDto;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
* @author visa
* @date 2020-11-02
*/

@Api(tags = "太平乐享虚拟卡管理")
@RestController
@RequestMapping("/api/taiping")
public class TaipingController {
    @Autowired
    private TaipingCardService taipingCardService;

    @Autowired
    private TaipingPayableService taipingPayableService;


    @PostMapping("/card")
    @Log("新增太平乐享虚拟卡")
    @ApiOperation("新增太平乐享虚拟卡")
    @AnonymousAccess
    public ResponseEntity<Object> addCard(@Validated @RequestBody TaipingDataDto resources){
        return new ResponseEntity<>(taipingCardService.saveCard(resources),HttpStatus.CREATED);

    }


    @PostMapping("/payable")
    @Log("新增太平应付款记录")
    @ApiOperation("新增太平应付款记录")
    @AnonymousAccess
    public ResponseEntity<Object> addPayable(@Validated @RequestBody TaipingDataDto resources){
        return new ResponseEntity<>(taipingPayableService.savePayable(resources),HttpStatus.CREATED);
    }

}
