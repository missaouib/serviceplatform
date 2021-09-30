/**
* Copyright (C) 2018-2020
* All rights reserved, Designed By www.yixiang.co
* 注意：
* 本软件为www.yixiang.co开发研制，未经购买不得使用
* 购买后可获得全部源代码（禁止转卖、分享、上传到码云、github等开源平台）
* 一经发现盗用、分享等行为，将追究法律责任，后果自负
*/
package co.yixiang.modules.taibao.rest;

import co.yixiang.dozer.service.IGenerator;
import co.yixiang.logging.aop.log.Log;
import co.yixiang.modules.taibao.domain.TbPolicyInfo;
import co.yixiang.modules.taibao.service.TbPolicyInfoService;
import co.yixiang.modules.taibao.service.dto.PolicyInfoDTO;
import co.yixiang.modules.taibao.service.dto.TbPolicyInfoDto;
import co.yixiang.modules.taibao.service.dto.TbPolicyInfoQueryCriteria;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;

/**
* @author zhoujinlai
* @date 2021-04-30
*/
@AllArgsConstructor
@Api(tags = "太保安联-保单信息表管理")
@RestController
@RequestMapping("/api/tbPolicyInfo")
public class TbPolicyInfoController {
    private final TbPolicyInfoService tbPolicyInfoService;

    private final IGenerator generator;


    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('admin','tbPolicyInfo:list','TBPOLICYINFO_ALL')")
    public void download(HttpServletResponse response, TbPolicyInfoQueryCriteria criteria) throws IOException {
        tbPolicyInfoService.download(generator.convert(tbPolicyInfoService.queryAll(criteria), TbPolicyInfoDto.class), response);
    }

    @GetMapping
    @Log("查询太保安联-保单信息表")
    @ApiOperation("查询太保安联-保单信息表")
    @PreAuthorize("@el.check('admin','tbPolicyInfo:list','TBPOLICYINFO_ALL')")
    public ResponseEntity<Object> getTbPolicyInfos(TbPolicyInfoQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(tbPolicyInfoService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增太保安联-保单信息表")
    @ApiOperation("新增太保安联-保单信息表")
    @PreAuthorize("@el.check('admin','tbPolicyInfo:add','TBPOLICYINFO_ALL')")
    public ResponseEntity<Object> create(@Validated @RequestBody TbPolicyInfo resources){
        return new ResponseEntity<>(tbPolicyInfoService.save(resources),HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改太保安联-保单信息表")
    @ApiOperation("修改太保安联-保单信息表")
    @PreAuthorize("@el.check('admin','tbPolicyInfo:edit','TBPOLICYINFO_ALL')")
    public ResponseEntity<Object> update(@Validated @RequestBody TbPolicyInfo resources){
        tbPolicyInfoService.updateById(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除太保安联-保单信息表")
    @ApiOperation("删除太保安联-保单信息表")
    @PreAuthorize("@el.check('admin','tbPolicyInfo:del','TBPOLICYINFO_ALL')")
    @DeleteMapping
    public ResponseEntity<Object> deleteAll(@RequestBody String[] ids) {
        Arrays.asList(ids).forEach(id->{
            tbPolicyInfoService.removeById(id);
        });
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    @Log(value = "保单信息表-通过id查询")
    @ApiOperation(value="保单信息表-通过id查询", notes="保单信息表-通过id查询")
    @GetMapping(value = "/queryById")
    public ResponseEntity<Object> queryById(@RequestParam(name="id",required=true) String id) {
        TbPolicyInfo policyInfo = tbPolicyInfoService.getById(id);
        if(policyInfo==null) {
            return new ResponseEntity<>(new TbPolicyInfo(),HttpStatus.OK);
        }
        return  new ResponseEntity<>(policyInfo,HttpStatus.OK);
    }

    /**
     * 垫付结果通知
     * @param policyInfoDTO
     * @return
     */
    @Log("垫付结果通知")
    @ApiOperation("垫付结果通知")
    @PostMapping(value = "/advancePaymentResult")
    public ResponseEntity<Object>  advancePaymentResult(@RequestBody PolicyInfoDTO policyInfoDTO) {
        tbPolicyInfoService.advancePaymentResult(policyInfoDTO);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
