/**
* Copyright (C) 2018-2020
* All rights reserved, Designed By www.yixiang.co
* 注意：
* 本软件为www.yixiang.co开发研制，未经购买不得使用
* 购买后可获得全部源代码（禁止转卖、分享、上传到码云、github等开源平台）
* 一经发现盗用、分享等行为，将追究法律责任，后果自负
*/
package co.yixiang.modules.taibao.web.controller;

import co.yixiang.common.api.ApiResult;
import co.yixiang.modules.taibao.entity.TbClaimInfo;
import co.yixiang.modules.taibao.service.TbClaimInfoService;
import co.yixiang.modules.taibao.web.param.ClaimInfoParam;
import co.yixiang.modules.taibao.web.vo.ClaimInfoVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;

/**
* @author zhoujinlai
* @date 2021-04-30
*/
@Slf4j
@RestController
@RequestMapping("/tbClaimInfo")
@Api("太保安联订单—赔案信息 API")
public class TbClaimInfoController {

    @Autowired
    private TbClaimInfoService tbClaimInfoService;

    @PostMapping("/getClaimInfo")
    @ApiOperation(value = "获取校验是否为太保订单",notes = "获取校验是否为太保订单",response = ApiResult.class)
    public ApiResult<ClaimInfoVo> getClaimInfo(@Valid @RequestBody ClaimInfoParam claimInfoParam){
        if (claimInfoParam.getOrderId()==null){
            return ApiResult.fail("参数有误！");
        }
        ClaimInfoVo claimInfoVo = tbClaimInfoService.getByOrderId(claimInfoParam.getOrderId());
        if(claimInfoVo==null){
            return ApiResult.fail("不是太保安联订单，无需添加附件。");
        }
        return ApiResult.ok(claimInfoVo);
    }


    @PostMapping("/uploadClaimImg")
    @ApiOperation(value = "上传太保安联图片附件",notes = "上传太保安联图片附件",response = ApiResult.class)
    public ApiResult uploadClaimImg(@RequestBody ClaimInfoParam claimInfoParam){
        if (claimInfoParam.getOrderId()==null || StringUtils.isEmpty(claimInfoParam.getImgPah())){
            return ApiResult.fail("参数有误！");
        }
        ClaimInfoVo claimInfoVo = tbClaimInfoService.getByOrderId(claimInfoParam.getOrderId());
        if(claimInfoVo==null){
            return ApiResult.fail("不是太保安联订单，无需添加附件。");
        }
        TbClaimInfo tbClaimInfo=new TbClaimInfo();
        tbClaimInfo.setId(claimInfoVo.getId());
        tbClaimInfo.setImgUrl(claimInfoParam.getImgPah());
        tbClaimInfoService.updateImgUrlById(tbClaimInfo);
        return ApiResult.ok();
    }


}
