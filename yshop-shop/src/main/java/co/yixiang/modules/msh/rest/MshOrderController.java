/**
* Copyright (C) 2018-2020
* All rights reserved, Designed By www.yixiang.co
* 注意：
* 本软件为www.yixiang.co开发研制，未经购买不得使用
* 购买后可获得全部源代码（禁止转卖、分享、上传到码云、github等开源平台）
* 一经发现盗用、分享等行为，将追究法律责任，后果自负
*/
package co.yixiang.modules.msh.rest;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import co.yixiang.modules.msh.domain.MshDemandList;
import co.yixiang.modules.msh.service.MshDemandListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;

import co.yixiang.dozer.service.IGenerator;
import co.yixiang.exception.BadRequestException;
import co.yixiang.logging.aop.log.Log;
import co.yixiang.modules.msh.domain.MshOrder;
import co.yixiang.modules.msh.service.MshOrderService;
import co.yixiang.modules.msh.service.dto.MshOrderDto;
import co.yixiang.modules.msh.service.dto.MshOrderQueryCriteria;
import co.yixiang.modules.msh.service.dto.ServiceResult;
import co.yixiang.modules.shop.service.param.ExpressParam;
import co.yixiang.tools.express.dao.ExpressInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;

/**
* @author cq
* @date 2020-12-25
*/
@AllArgsConstructor
@Api(tags = "订单表管理")
@RestController
@RequestMapping("/api/mshOrder")
public class MshOrderController {

    private final MshOrderService mshOrderService;
    private final IGenerator generator;

    @Autowired
    private MshDemandListService mshDemandListService;


    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    public void download(HttpServletResponse response, MshOrderQueryCriteria criteria) throws IOException {
        mshOrderService.download(generator.convert(mshOrderService.queryAll(criteria), MshOrderDto.class), response);
    }

    @GetMapping
    @Log("查询订单表")
    @ApiOperation("查询订单表")
    public ResponseEntity<Object> getMshOrders(MshOrderQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(mshOrderService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增订单表")
    @ApiOperation("新增订单表")
    public ResponseEntity<Object> create(@Validated @RequestBody MshOrder resources){
        return new ResponseEntity<>(mshOrderService.save(resources),HttpStatus.CREATED);
    }

    @Log("修改订单表")
    @ApiOperation("修改订单表")
    @PostMapping(value = "/update")
    public ResponseEntity<Object> update(@Validated @RequestBody MshOrder resources){
        MshOrder order=  mshOrderService.getById(resources.getId());
        MshDemandList mshDemandList= mshDemandListService.getById(order.getDemandListId());
        if(mshDemandList.getLssueStatus()==1){
            Map<String, Object> map = new LinkedHashMap<>(2);
            map.put("status", HttpStatus.BAD_REQUEST);
            map.put("message", "所属需求单已下发，不能修改该订单。");
            return new ResponseEntity(map,HttpStatus.BAD_REQUEST);
        }

        mshOrderService.updateById(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除订单表")
    @ApiOperation("删除订单表")
    @PostMapping(value = "/deleteAll")
    public ResponseEntity<Object> deleteAll(@RequestBody Integer[] ids) {
        if(ids.length==0){
            Map<String, Object> map = new LinkedHashMap<>(2);
            map.put("status", HttpStatus.BAD_REQUEST);
            map.put("message", "请勾选订单。");
            return new ResponseEntity(map,HttpStatus.BAD_REQUEST);
        }
        MshOrder order=  mshOrderService.getById(ids[0]);
        MshDemandList mshDemandList= mshDemandListService.getById(order.getDemandListId());
        if(mshDemandList.getLssueStatus()==1){
            Map<String, Object> map = new LinkedHashMap<>(2);
            map.put("status", HttpStatus.BAD_REQUEST);
            map.put("message", "所属需求单已下发，不能删除所选订单。");
            return new ResponseEntity(map,HttpStatus.BAD_REQUEST);
        }
        Arrays.asList(ids).forEach(id->{
            mshOrderService.removeById(id);
        });
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Log("新增订单相关")
    @ApiOperation("新增订单相关")
    @PostMapping(value = "/makeOrder")
    public ResponseEntity<Object> makeOrder(@Validated @RequestBody JSONObject jsonObject){
        try {
        	ServiceResult<Boolean> flag = mshOrderService.makeOrder(jsonObject);
        	if(!flag.isOk()){
        		Map<String, Object> map = new LinkedHashMap<>(2);
                map.put("status", HttpStatus.BAD_REQUEST);
                map.put("message", flag.getMsg());
        		return new ResponseEntity(map,HttpStatus.BAD_REQUEST);
        	}
        }catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> map = new LinkedHashMap<>(2);
            map.put("status", HttpStatus.BAD_REQUEST);
            map.put("message", "插入数据有误！");
            return new ResponseEntity(map,HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(HttpStatus.CREATED);
    }


    /**@Valid
     * 获取物流信息,根据传的订单编号 ShipperCode快递公司编号 和物流单号,
     */
    @PostMapping("/express")
    @Log("获取物流信息")
    @ApiOperation(value = "获取物流信息",notes = "获取物流信息",response = ExpressParam.class)
    public ResponseEntity express( @RequestBody ExpressParam expressInfoDo){
        ExpressInfo expressInfo = mshOrderService.queryOrderLogisticsProcess(expressInfoDo);

        if(!expressInfo.isSuccess()) throw new BadRequestException(expressInfo.getReason());
        return new ResponseEntity(expressInfo, HttpStatus.OK);
    }

    /**@Valid
     * 获取物流信息,根据传的订单编号 ShipperCode快递公司编号 和物流单号,
     */
    @PostMapping("/newExpress")
    @Log("获取物流信息")
    @ApiOperation(value = "获取物流信息",notes = "获取物流信息",response = ExpressParam.class)
    public ResponseEntity newExpress( @RequestBody ExpressParam expressInfoDo){
        return mshOrderService.queryNewOrderLogisticsProcess(expressInfoDo);
    }



    @Log("查询需求单详细")
    @ApiOperation("查询需求单详细")
    @PostMapping(value = "/getMshOrderByDemandListId")
    public ResponseEntity<Object> getMshOrderByDemandListId(@RequestBody Integer demandListId){
        return new ResponseEntity<>(mshOrderService.getMshOrderByDemandListId(demandListId),HttpStatus.OK);
    }

    @Log("下发订单")
    @ApiOperation("下发订单")
    @PostMapping(value = "/lssueOrderByDemandListId")
    public ResponseEntity<Object> lssueOrderByDemandListId(@RequestBody Integer demandListId){
        mshOrderService.lssueOrderByDemandListId(demandListId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
